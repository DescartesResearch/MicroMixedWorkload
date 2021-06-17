package apm;

import com.sun.jna.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class APM {

    private static final String FILE_NAME = "apm.csv";

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

    private static ThreadMXBean threadmxBean = ManagementFactory.getThreadMXBean();
    private static final ThreadLocal<Path> THREAD_LOCAL_PATHHANDLE = new ThreadLocal<>();
    private static final ThreadLocal<Long> THREAD_LOCAL_PROC_FS_READ_OVERHEAD = new ThreadLocal<Long>() {
        @Override
        public Long initialValue() {
            return 0L;
        }
    };

    private static final String READ_BYTES = "rchar";
    private static final String WRITE_BYTES = "write_bytes";

    private static long getCurrentThreadCpuTime() {
        return threadmxBean.getCurrentThreadCpuTime();
    }

    private static long getCurrentThreadAllocatedBytes() {
        long bytes = -1;
        if (threadmxBean instanceof com.sun.management.ThreadMXBean) {
            com.sun.management.ThreadMXBean sunThreadMXBean = (com.sun.management.ThreadMXBean) threadmxBean;
            // Returns an approximation of the total amount of memory, in bytes, allocated in heap memory for the thread of the specified ID.
            bytes = sunThreadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId());
        }
        return bytes;
    }

    private static long[] getDiskBytesReadAndWritten() {

        if (Platform.isWindows()) {
            return new long[] {0, 0};
        }

        /*
         * rchar: 476726516 wchar: 450053132 syscr: 1145703 syscw: 461006
         * read_bytes: 933888 write_bytes: 26984448 cancelled_write_bytes: 0
         */

        long[] result = null;
        try {

            byte[] filearray = readAllBytes(getPath());
            String text = new String(filearray, "UTF-8");

            int startIndex = text.indexOf(READ_BYTES);
            if (startIndex == -1) {
                return new long[] {};
            }
            startIndex += READ_BYTES.length() + 2;
            int endIndex = text.indexOf('\n', startIndex);
            long readBytes = Long.parseLong(text.substring(startIndex, endIndex)) - THREAD_LOCAL_PROC_FS_READ_OVERHEAD.get();

            startIndex = text.indexOf(WRITE_BYTES);
            if (startIndex == -1) {
                return new long[] {};
            }
            startIndex += WRITE_BYTES.length() + 2;
            endIndex = text.indexOf('\n', startIndex);
            long writeBytes = Long.parseLong(text.substring(startIndex, endIndex));

            result = new long[2];
            result[0] = readBytes;
            result[1] = writeBytes;

            THREAD_LOCAL_PROC_FS_READ_OVERHEAD.set(THREAD_LOCAL_PROC_FS_READ_OVERHEAD.get() + text.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Path getPath() {
        if (THREAD_LOCAL_PATHHANDLE.get() == null) {
            Path path = FileSystems.getDefault().getPath("/proc", Integer.toString(getProcessId()), "task",
                Integer.toString(getThreadId()), "io");
            THREAD_LOCAL_PATHHANDLE.set(path);
        }
        return THREAD_LOCAL_PATHHANDLE.get();
    }

    private static int getProcessId() {
        if (Platform.isWindows()) {
            return Kernel32Library.INSTANCE.GetCurrentProcessId();
        } else if (Platform.isLinux()) {
            return CLibrary.INSTANCE.getpid();
        }
        return 0;
    }

    public static int getThreadId() {
        if (Platform.isWindows()) {
            return Kernel32Library.INSTANCE.GetCurrentThreadId();
        } else if (Platform.isLinux() && Platform.isIntel()) {
            if (Platform.is64Bit()) {
                return CLibrary.INSTANCE.syscall(CLibrary.GETTID_X86_64);
            } else {
                return CLibrary.INSTANCE.syscall(CLibrary.GETTID_X86_32);
            }
        }

        return 0;
    }

    private static byte[] readAllBytes(Path path) throws IOException {
        SeekableByteChannel sbc = null;
        InputStream in = null;
        byte[] result = null;
        try {
            sbc = Files.newByteChannel(path);
            in = Channels.newInputStream(sbc);
            result = read(in, 1024);
        } finally {
            if (in != null) {
                in.close();
            }
            if (sbc != null) {
                sbc.close();
            }
        }
        return result;
    }

    private static byte[] read(InputStream source, int initialSize) throws IOException {
        int capacity = initialSize;
        byte[] buf = new byte[capacity];
        int nread = 0;
        int n;
        for (; ; ) {
            // read to EOF which may read more or less than initialSize (eg:
            // file
            // is truncated while we are reading)
            while ((n = source.read(buf, nread, capacity - nread)) > 0) {
                nread += n;
            }

            // if last call to source.read() returned -1, we are done
            // otherwise, try to read one more byte; if that failed we're done
            // too
            if (n < 0 || (n = source.read()) < 0) {
                break;
            }

            // one more byte was read; need to allocate a larger buffer
            if (capacity <= 4096 - capacity) {
                capacity = Math.max(capacity << 1, 4096);
            } else {
                if (capacity == 4096) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                capacity = 4096;
            }
            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte) n;
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    public static void start() {
        cpuStart = getCurrentThreadCpuTime();
        heapStart = getCurrentThreadAllocatedBytes();
        long[] bytes = getDiskBytesReadAndWritten();
        diskReadStart = bytes[0];
        diskWriteStart = bytes[1];
    }

    public static void stop() {
        long cpu = getCurrentThreadCpuTime() - cpuStart;
        long heap = getCurrentThreadAllocatedBytes() - heapStart;
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
