package microbenchmark;

import com.sun.jna.platform.win32.Kernel32;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PID {

	/**
	 * The Java system property string to retrieve the operating system Java is running on.
	 */
	public static final String OS_NAME_PROPERTY = "os.name";

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


}
