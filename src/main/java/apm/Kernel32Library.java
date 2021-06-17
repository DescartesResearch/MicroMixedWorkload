package apm;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.LongByReference;

public interface Kernel32Library extends Library {
    Kernel32Library INSTANCE = Native.loadLibrary("kernel32", Kernel32Library.class);

    //CHECKSTYLE:OFF
    int GetCurrentProcessId();

    int GetCurrentThreadId();

    Handle GetCurrentThread();

    boolean QueryThreadCycleTime(Handle threadHandle, LongByReference cycles);
    //CHECKSTYLE:ON

    public static class Handle extends PointerType {
        public Handle(Pointer address) {
            super(address);
        }

        public Handle() {
            super();
        }
    }
}
