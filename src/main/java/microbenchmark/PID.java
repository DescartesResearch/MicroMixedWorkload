package microbenchmark;

import com.sun.jna.platform.win32.Kernel32;

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

}
