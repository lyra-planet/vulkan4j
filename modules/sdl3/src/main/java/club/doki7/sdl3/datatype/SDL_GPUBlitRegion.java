package club.doki7.sdl3.datatype;

import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import club.doki7.ffm.IPointer;
import club.doki7.ffm.NativeLayout;
import club.doki7.ffm.annotation.*;
import club.doki7.ffm.ptr.*;
import club.doki7.sdl3.bitmask.*;
import club.doki7.sdl3.handle.*;
import club.doki7.sdl3.enumtype.*;
import static club.doki7.sdl3.SDL3Constants.*;

/// A structure specifying a region of a texture used in the blit operation.
///
/// Since: This struct is available since SDL 3.2.0.
///
/// See also:
/// - `SDL_BlitGPUTexture`
///
/// ## Structure
///
/// {@snippet lang=c :
/// typedef struct SDL_GPUBlitRegion {
///     SDL_GPUTexture* texture; // @link substring="SDL_GPUTexture" target="SDL_GPUTexture" @link substring="texture" target="#texture"
///     Uint32 mip_level; // @link substring="mip_level" target="#mip_level"
///     Uint32 layer_or_depth_plane; // @link substring="layer_or_depth_plane" target="#layer_or_depth_plane"
///     Uint32 x; // @link substring="x" target="#x"
///     Uint32 y; // @link substring="y" target="#y"
///     Uint32 w; // @link substring="w" target="#w"
///     Uint32 h; // @link substring="h" target="#h"
/// } SDL_GPUBlitRegion;
/// }
///
/// ## Contracts
///
/// The property {@link #segment()} should always be not-null
/// ({@code segment != NULL && !segment.equals(MemorySegment.NULL)}), and properly aligned to
/// {@code LAYOUT.byteAlignment()} bytes. To represent null pointer, you may use a Java
/// {@code null} instead. See the documentation of {@link IPointer#segment()} for more details.
///
/// The constructor of this class is marked as {@link UnsafeConstructor}, because it does not
/// perform any runtime check. The constructor can be useful for automatic code generators.
@ValueBasedCandidate
@UnsafeConstructor
public record SDL_GPUBlitRegion(@NotNull MemorySegment segment) implements ISDL_GPUBlitRegion {
    /// Represents a pointer to / an array of null structure(s) in native memory.
    ///
    /// Technically speaking, this type has no difference with {@link SDL_GPUBlitRegion}. This type
    /// is introduced mainly for user to distinguish between a pointer to a single structure
    /// and a pointer to (potentially) an array of structure(s). APIs should use interface
    /// ISDL_GPUBlitRegion to handle both types uniformly. See package level documentation for more
    /// details.
    ///
    /// ## Contracts
    ///
    /// The property {@link #segment()} should always be not-null
    /// ({@code segment != NULL && !segment.equals(MemorySegment.NULL)}), and properly aligned to
    /// {@code SDL_GPUBlitRegion.LAYOUT.byteAlignment()} bytes. To represent null pointer, you may use a Java
    /// {@code null} instead. See the documentation of {@link IPointer#segment()} for more details.
    ///
    /// The constructor of this class is marked as {@link UnsafeConstructor}, because it does not
    /// perform any runtime check. The constructor can be useful for automatic code generators.
    @ValueBasedCandidate
    @UnsafeConstructor
    public record Ptr(@NotNull MemorySegment segment) implements ISDL_GPUBlitRegion, Iterable<SDL_GPUBlitRegion> {
        public long size() {
            return segment.byteSize() / SDL_GPUBlitRegion.BYTES;
        }

        /// Returns (a pointer to) the structure at the given index.
        ///
        /// Note that unlike {@code read} series functions ({@link IntPtr#read()} for
        /// example), modification on returned structure will be reflected on the original
        /// structure array. So this function is called {@code at} to explicitly
        /// indicate that the returned structure is a view of the original structure.
        public @NotNull SDL_GPUBlitRegion at(long index) {
            return new SDL_GPUBlitRegion(segment.asSlice(index * SDL_GPUBlitRegion.BYTES, SDL_GPUBlitRegion.BYTES));
        }

        public void write(long index, @NotNull SDL_GPUBlitRegion value) {
            MemorySegment s = segment.asSlice(index * SDL_GPUBlitRegion.BYTES, SDL_GPUBlitRegion.BYTES);
            s.copyFrom(value.segment);
        }

        /// Assume the {@link Ptr} is capable of holding at least {@code newSize} structures,
        /// create a new view {@link Ptr} that uses the same backing storage as this
        /// {@link Ptr}, but with the new size. Since there is actually no way to really check
        /// whether the new size is valid, while buffer overflow is undefined behavior, this method is
        /// marked as {@link Unsafe}.
        ///
        /// This method could be useful when handling data returned from some C API, where the size of
        /// the data is not known in advance.
        ///
        /// If the size of the underlying segment is actually known in advance and correctly set, and
        /// you want to create a shrunk view, you may use {@link #slice(long)} (with validation)
        /// instead.
        @Unsafe
        public @NotNull Ptr reinterpret(long newSize) {
            return new Ptr(segment.reinterpret(newSize * SDL_GPUBlitRegion.BYTES));
        }

        public @NotNull Ptr offset(long offset) {
            return new Ptr(segment.asSlice(offset * SDL_GPUBlitRegion.BYTES));
        }

        /// Note that this function uses the {@link List#subList(int, int)} semantics (left inclusive,
        /// right exclusive interval), not {@link MemorySegment#asSlice(long, long)} semantics
        /// (offset + newSize). Be careful with the difference
        public @NotNull Ptr slice(long start, long end) {
            return new Ptr(segment.asSlice(
                start * SDL_GPUBlitRegion.BYTES,
                (end - start) * SDL_GPUBlitRegion.BYTES
            ));
        }

        public Ptr slice(long end) {
            return new Ptr(segment.asSlice(0, end * SDL_GPUBlitRegion.BYTES));
        }

        public SDL_GPUBlitRegion[] toArray() {
            SDL_GPUBlitRegion[] ret = new SDL_GPUBlitRegion[(int) size()];
            for (long i = 0; i < size(); i++) {
                ret[(int) i] = at(i);
            }
            return ret;
        }

        @Override
        public @NotNull Iterator<SDL_GPUBlitRegion> iterator() {
            return new Iter(this.segment());
        }

        /// An iterator over the structures.
        private static final class Iter implements Iterator<SDL_GPUBlitRegion> {
            Iter(@NotNull MemorySegment segment) {
                this.segment = segment;
            }

            @Override
            public boolean hasNext() {
                return segment.byteSize() >= SDL_GPUBlitRegion.BYTES;
            }

            @Override
            public SDL_GPUBlitRegion next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                SDL_GPUBlitRegion ret = new SDL_GPUBlitRegion(segment.asSlice(0, SDL_GPUBlitRegion.BYTES));
                segment = segment.asSlice(SDL_GPUBlitRegion.BYTES);
                return ret;
            }

            private @NotNull MemorySegment segment;
        }
    }

    public static SDL_GPUBlitRegion allocate(Arena arena) {
        return new SDL_GPUBlitRegion(arena.allocate(LAYOUT));
    }

    public static SDL_GPUBlitRegion.Ptr allocate(Arena arena, long count) {
        MemorySegment segment = arena.allocate(LAYOUT, count);
        return new SDL_GPUBlitRegion.Ptr(segment);
    }

    public static SDL_GPUBlitRegion clone(Arena arena, SDL_GPUBlitRegion src) {
        SDL_GPUBlitRegion ret = allocate(arena);
        ret.segment.copyFrom(src.segment);
        return ret;
    }

    public @Nullable SDL_GPUTexture texture() {
        MemorySegment s = segment.asSlice(OFFSET$texture, SIZE$texture);
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }
        return new SDL_GPUTexture(s);
    }

    public SDL_GPUBlitRegion texture(@Nullable SDL_GPUTexture value) {
        segment.set(LAYOUT$texture, OFFSET$texture, value != null ? value.segment() : MemorySegment.NULL);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int mip_level() {
        return segment.get(LAYOUT$mip_level, OFFSET$mip_level);
    }

    public SDL_GPUBlitRegion mip_level(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$mip_level, OFFSET$mip_level, value);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int layer_or_depth_plane() {
        return segment.get(LAYOUT$layer_or_depth_plane, OFFSET$layer_or_depth_plane);
    }

    public SDL_GPUBlitRegion layer_or_depth_plane(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$layer_or_depth_plane, OFFSET$layer_or_depth_plane, value);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int x() {
        return segment.get(LAYOUT$x, OFFSET$x);
    }

    public SDL_GPUBlitRegion x(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$x, OFFSET$x, value);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int y() {
        return segment.get(LAYOUT$y, OFFSET$y);
    }

    public SDL_GPUBlitRegion y(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$y, OFFSET$y, value);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int w() {
        return segment.get(LAYOUT$w, OFFSET$w);
    }

    public SDL_GPUBlitRegion w(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$w, OFFSET$w, value);
        return this;
    }

    public @NativeType("Uint32") @Unsigned int h() {
        return segment.get(LAYOUT$h, OFFSET$h);
    }

    public SDL_GPUBlitRegion h(@NativeType("Uint32") @Unsigned int value) {
        segment.set(LAYOUT$h, OFFSET$h, value);
        return this;
    }

    public static final StructLayout LAYOUT = NativeLayout.structLayout(
        ValueLayout.ADDRESS.withName("texture"),
        ValueLayout.JAVA_INT.withName("mip_level"),
        ValueLayout.JAVA_INT.withName("layer_or_depth_plane"),
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y"),
        ValueLayout.JAVA_INT.withName("w"),
        ValueLayout.JAVA_INT.withName("h")
    );
    public static final long BYTES = LAYOUT.byteSize();

    public static final PathElement PATH$texture = PathElement.groupElement("texture");
    public static final PathElement PATH$mip_level = PathElement.groupElement("mip_level");
    public static final PathElement PATH$layer_or_depth_plane = PathElement.groupElement("layer_or_depth_plane");
    public static final PathElement PATH$x = PathElement.groupElement("x");
    public static final PathElement PATH$y = PathElement.groupElement("y");
    public static final PathElement PATH$w = PathElement.groupElement("w");
    public static final PathElement PATH$h = PathElement.groupElement("h");

    public static final AddressLayout LAYOUT$texture = (AddressLayout) LAYOUT.select(PATH$texture);
    public static final OfInt LAYOUT$mip_level = (OfInt) LAYOUT.select(PATH$mip_level);
    public static final OfInt LAYOUT$layer_or_depth_plane = (OfInt) LAYOUT.select(PATH$layer_or_depth_plane);
    public static final OfInt LAYOUT$x = (OfInt) LAYOUT.select(PATH$x);
    public static final OfInt LAYOUT$y = (OfInt) LAYOUT.select(PATH$y);
    public static final OfInt LAYOUT$w = (OfInt) LAYOUT.select(PATH$w);
    public static final OfInt LAYOUT$h = (OfInt) LAYOUT.select(PATH$h);

    public static final long SIZE$texture = LAYOUT$texture.byteSize();
    public static final long SIZE$mip_level = LAYOUT$mip_level.byteSize();
    public static final long SIZE$layer_or_depth_plane = LAYOUT$layer_or_depth_plane.byteSize();
    public static final long SIZE$x = LAYOUT$x.byteSize();
    public static final long SIZE$y = LAYOUT$y.byteSize();
    public static final long SIZE$w = LAYOUT$w.byteSize();
    public static final long SIZE$h = LAYOUT$h.byteSize();

    public static final long OFFSET$texture = LAYOUT.byteOffset(PATH$texture);
    public static final long OFFSET$mip_level = LAYOUT.byteOffset(PATH$mip_level);
    public static final long OFFSET$layer_or_depth_plane = LAYOUT.byteOffset(PATH$layer_or_depth_plane);
    public static final long OFFSET$x = LAYOUT.byteOffset(PATH$x);
    public static final long OFFSET$y = LAYOUT.byteOffset(PATH$y);
    public static final long OFFSET$w = LAYOUT.byteOffset(PATH$w);
    public static final long OFFSET$h = LAYOUT.byteOffset(PATH$h);
}
