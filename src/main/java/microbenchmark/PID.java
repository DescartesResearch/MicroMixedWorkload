package microbenchmark;

import com.sun.jna.platform.win32.Kernel32;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class PID {

	/**
	 * The Java system property string to retrieve the operating system Java is running on.
	 */
	public static final String OS_NAME_PROPERTY = "os.name";

	/**
	 * Standard filepath for the PID file.
	 */
	public static final Path STD_PID_FILEPATH = FileSystems.getDefault().getPath("pid.txt");

	/**
	 * Returns the PID of this process. Returns -1 if the OS is unknown.
	 * @return PID as integer.
	 */
	public static int getPID() {
		String OSName = System.getProperty(OS_NAME_PROPERTY);
		int pid = -1;
		if (OSName.startsWith("Windows")) {
			pid = Kernel32.INSTANCE.GetCurrentProcessId();
		} else if (OSName.startsWith("Linux")) {
			pid = CLibrary.INSTANCE.getpid();
		}
		return pid;
	}

	/**
	 * Function to write PID to a textfile at the given filename and directory.
	 * @param filePath Path to the file containing the PID number.
	 * @param pid PID as integer that will be written to the textfile.
	 */
	public static void writePID(Path filePath, int pid) {
		if (filePath == null) {
			throw new IllegalArgumentException("Path to PID file is null.");
		}
		File f = filePath.toFile();
		try (FileWriter writer = new FileWriter(f)) {
			writer.write(Integer.toString(pid));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Convenience function if we want to write to the standard directory and the standard filename.
	 * @param pid PID as integer that will be written to the textfile.
	 */
	public static void writePID(int pid) {
		writePID(STD_PID_FILEPATH, pid);
	}
}
