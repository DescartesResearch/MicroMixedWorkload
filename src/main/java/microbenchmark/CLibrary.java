package microbenchmark;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;

public interface CLibrary extends Library {
	CLibrary INSTANCE = (CLibrary) Native.load("c", CLibrary.class);
	int getpid();

}
