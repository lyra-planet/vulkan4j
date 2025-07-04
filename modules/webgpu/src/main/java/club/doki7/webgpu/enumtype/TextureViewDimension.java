package club.doki7.webgpu.enumtype;

import club.doki7.ffm.annotation.*;

public final class TextureViewDimension {
    public static final int UNDEFINED = 0x0;
    public static final int 1D = 0x1;
    public static final int 2D = 0x2;
    public static final int 2D_ARRAY = 0x3;
    public static final int CUBE = 0x4;
    public static final int CUBE_ARRAY = 0x5;
    public static final int 3D = 0x6;
    public static final int FORCE32 = 0x7fffffff;

    public static String explain(@EnumType(TextureViewDimension.class) int value) {
        return switch (value) {
            case TextureViewDimension.1D -> "1D";
            case TextureViewDimension.2D -> "2D";
            case TextureViewDimension.2D_ARRAY -> "2D_ARRAY";
            case TextureViewDimension.3D -> "3D";
            case TextureViewDimension.CUBE -> "CUBE";
            case TextureViewDimension.CUBE_ARRAY -> "CUBE_ARRAY";
            case TextureViewDimension.FORCE32 -> "FORCE32";
            case TextureViewDimension.UNDEFINED -> "UNDEFINED";
            default -> "UNKNOWN(" + value + ")";
        };
    }

    /// Constructing this class is nonsense so the constructor is made private.
    private TextureViewDimension() {}
}
