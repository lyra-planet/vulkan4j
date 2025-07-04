package club.doki7.sdl3.enumtype;

import club.doki7.ffm.annotation.*;

/// The normalized factor used to multiply pixel components.
///
/// The blend factors are multiplied with the pixels from a drawing operation
/// (src) and the pixels from the render target (dst) before the blend
/// operation. The comma-separated factors listed above are always applied in
/// the component order red, green, blue, and alpha.
///
/// Since: This enum is available since SDL 3.2.0.
///
public final class SDL_BlendFactor {
    public static final int ZERO = 0x1;
    public static final int ONE = 0x2;
    public static final int SRC_COLOR = 0x3;
    public static final int ONE_MINUS_SRC_COLOR = 0x4;
    public static final int SRC_ALPHA = 0x5;
    public static final int ONE_MINUS_SRC_ALPHA = 0x6;
    public static final int DST_COLOR = 0x7;
    public static final int ONE_MINUS_DST_COLOR = 0x8;
    public static final int DST_ALPHA = 0x9;
    public static final int ONE_MINUS_DST_ALPHA = 0xa;

    public static String explain(@EnumType(SDL_BlendFactor.class) int value) {
        return switch (value) {
            case SDL_BlendFactor.DST_ALPHA -> "SDL_BLENDFACTOR_DST_ALPHA";
            case SDL_BlendFactor.DST_COLOR -> "SDL_BLENDFACTOR_DST_COLOR";
            case SDL_BlendFactor.ONE -> "SDL_BLENDFACTOR_ONE";
            case SDL_BlendFactor.ONE_MINUS_DST_ALPHA -> "SDL_BLENDFACTOR_ONE_MINUS_DST_ALPHA";
            case SDL_BlendFactor.ONE_MINUS_DST_COLOR -> "SDL_BLENDFACTOR_ONE_MINUS_DST_COLOR";
            case SDL_BlendFactor.ONE_MINUS_SRC_ALPHA -> "SDL_BLENDFACTOR_ONE_MINUS_SRC_ALPHA";
            case SDL_BlendFactor.ONE_MINUS_SRC_COLOR -> "SDL_BLENDFACTOR_ONE_MINUS_SRC_COLOR";
            case SDL_BlendFactor.SRC_ALPHA -> "SDL_BLENDFACTOR_SRC_ALPHA";
            case SDL_BlendFactor.SRC_COLOR -> "SDL_BLENDFACTOR_SRC_COLOR";
            case SDL_BlendFactor.ZERO -> "SDL_BLENDFACTOR_ZERO";
            default -> "UNKNOWN(" + value + ")";
        };
    }

    /// Constructing this class is nonsense so the constructor is made private.
    private SDL_BlendFactor() {}
}
