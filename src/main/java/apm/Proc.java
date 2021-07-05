package apm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Proc {

	public static final String FILE_NAME = "proc.csv";

	public static final int _SC_CLK_TCK = 2;

	static {
		try {
			Path path = Paths.get(FILE_NAME);
			Files.deleteIfExists(path);
			Files.createFile(path);
			Files.write(
				Paths.get(FILE_NAME),
				"cpu (ns),heap (bytes),diskRead (bytes),diskWrite (bytes)\n".getBytes(),
				StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long cpuStart;
	private static long heapStart;
	private static long diskReadStart;
	private static long diskWriteStart;
	private static int pid;
	private static int tid;

	public static long getCurrentThreadCpuTime() {
		String cpuData = null;
		try (Stream<String> lines = Files.lines(Paths.get("/proc/" + pid + "/task/" + tid + "/stat"))) {
			cpuData = lines.collect(Collectors.joining());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		if (cpuData == null || cpuData.isEmpty()) {
			return 0;
		}

		// Get USER_HZ configuration from kernel
		long userHz = CLibrary.INSTANCE.sysconf(_SC_CLK_TCK);

		// CPU statistics
		String[] statistics = cpuData.split(" ");
		// Process / Thread
		long utime = Long.parseLong(statistics[13]);
		long stime = Long.parseLong(statistics[14]);
		// Child Thread / Processes
		long cutime = Long.parseLong(statistics[15]);
		long cstime = Long.parseLong(statistics[16]);

		long totalClockTicks = utime + stime + cutime + cstime;

		return Math.round(((double)totalClockTicks * 1e9) / (double)userHz);
		//return totalClockTicks;
	}

	public static long getCurrentThreadAllocatedBytes() {
		long heap = 0;
		try (Stream<String> memData = Files.lines(Paths.get("/proc/" + pid + "/task/" + tid + "/status"))) {
			heap = memData.filter(line -> line.startsWith("VmSize"))
				.map(line -> line.split(":"))
				.map(entry -> entry[1].split(" "))
				.mapToLong(entry -> Long.parseLong(entry[0].trim()) * 1024L)
				.sum();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return heap;
	}

	/*
	public static long getCurrentThreadAllocatedBytes() {
		long heap = 0;
		try (Stream<String> memData = Files.lines(Paths.get("/proc/" + pid + "/task/" + tid + "/maps"))) {
			heap = memData
				.map(line -> line.split(" "))
				.filter(entry -> entry.length > 5)
				.filter(entry -> entry[entry.length - 1].trim().equals("[heap]"))
				.map(entry -> entry[0].split("-"))
				.mapToLong(entry -> Long.valueOf(entry[1].trim(), 16) - Long.valueOf(entry[0].trim(), 16))
				.sum();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return heap;
	}

	public static long getCurrentThreadAllocatedBytes() {
		long heap = 0;
		try (Stream<String> memData = Files.lines(Paths.get("/proc/" + pid + "/task/" + tid + "/smaps"))) {
			List<String> lines = memData.collect(Collectors.toList());
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).contains("[heap]")) {
					String[] sizeStr = lines.get(i + 1).split(" ");
					heap += Long.valueOf(sizeStr[sizeStr.length - 2].trim()) * 1024;
					i += 20;
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return heap;
	}
	*/

	public static long[] getDiskBytesReadAndWritten() {
		// Uses proc anyways
		return APM.getDiskBytesReadAndWritten();
	}

	public static void start() {
		pid = APM.getProcessId();
		tid = APM.getThreadId();
		cpuStart = getCurrentThreadCpuTime();
		heapStart = getCurrentThreadAllocatedBytes();
		long[] bytes = getDiskBytesReadAndWritten();
		diskReadStart = bytes[0];
		diskWriteStart = bytes[1];
	}

	public static void stop() {
		long cpu = getCurrentThreadCpuTime() - cpuStart;
		long heap = getCurrentThreadAllocatedBytes() - heapStart;
		//long heap = getCurrentThreadAllocatedBytes();
		//if (heapStart < heap) {
		//	heap -= heapStart;
		//}
		long[] bytes = getDiskBytesReadAndWritten();
		long diskRead = bytes[0] - diskReadStart;
		long diskWrite = bytes[1] - diskWriteStart;

		String content = cpu + "," + heap + "," + diskRead + "," + diskWrite + "\n";

		try {
			Files.write(
				Paths.get(FILE_NAME),
				content.getBytes(),
				StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print() {
		pid = APM.getProcessId();
		tid = APM.getThreadId();
		long cpu = getCurrentThreadCpuTime();
		long heap = getCurrentThreadAllocatedBytes();
		long[] bytes = getDiskBytesReadAndWritten();
		long diskRead = bytes[0] - diskReadStart;
		long diskWrite = bytes[1] - diskWriteStart;

		String content = cpu + "," + heap + "," + diskRead + "," + diskWrite + "\n";

		try {
			Files.write(
				Paths.get(FILE_NAME),
				content.getBytes(),
				StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
