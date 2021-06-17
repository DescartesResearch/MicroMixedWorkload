package apm;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {

    CLibrary INSTANCE = Native.loadLibrary("c", CLibrary.class);

    // Derived from Linux/arch/x86/include/asm/unistd_32.h
    public static final int GETTID_X86_32 = 224;
    // Derived from Linux/arch/x86/include/asm/unistd_64.h
    public static final int GETTID_X86_64 = 186;

    int getpid();

    int syscall(int syscall);
}