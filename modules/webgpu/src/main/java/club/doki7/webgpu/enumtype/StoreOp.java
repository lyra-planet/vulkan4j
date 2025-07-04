package club.doki7.webgpu.enumtype;

import club.doki7.ffm.annotation.*;

public final class StoreOp {
    public static final int UNDEFINED = 0x0;
    public static final int STORE = 0x1;
    public static final int DISCARD = 0x2;
    public static final int FORCE32 = 0x7fffffff;

    public static String explain(@EnumType(StoreOp.class) int value) {
        return switch (value) {
            case StoreOp.DISCARD -> "DISCARD";
            case StoreOp.FORCE32 -> "FORCE32";
            case StoreOp.STORE -> "STORE";
            case StoreOp.UNDEFINED -> "UNDEFINED";
            default -> "UNKNOWN(" + value + ")";
        };
    }

    /// Constructing this class is nonsense so the constructor is made private.
    private StoreOp() {}
}
