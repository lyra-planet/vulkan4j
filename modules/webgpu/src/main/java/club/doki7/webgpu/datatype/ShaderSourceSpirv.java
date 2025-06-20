package club.doki7.webgpu.datatype;

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
import club.doki7.webgpu.bitmask.*;
import club.doki7.webgpu.handle.*;
import club.doki7.webgpu.enumtype.*;
import static club.doki7.webgpu.WebGPUConstants.*;

/// Represents a pointer to a {@code ShaderSourceSpirv} structure in native memory.
///
/// ## Structure
///
/// {@snippet lang=c :
/// typedef struct ShaderSourceSpirv {
///     uint32_t codeSize; // @link substring="codeSize" target="#codeSize"
///     uint32_t code; // @link substring="code" target="#code"
/// } ShaderSourceSpirv;
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
public record ShaderSourceSpirv(@NotNull MemorySegment segment) implements IShaderSourceSpirv {
    /// Represents a pointer to / an array of null structure(s) in native memory.
    ///
    /// Technically speaking, this type has no difference with {@link ShaderSourceSpirv}. This type
    /// is introduced mainly for user to distinguish between a pointer to a single structure
    /// and a pointer to (potentially) an array of structure(s). APIs should use interface
    /// IShaderSourceSpirv to handle both types uniformly. See package level documentation for more
    /// details.
    ///
    /// ## Contracts
    ///
    /// The property {@link #segment()} should always be not-null
    /// ({@code segment != NULL && !segment.equals(MemorySegment.NULL)}), and properly aligned to
    /// {@code ShaderSourceSpirv.LAYOUT.byteAlignment()} bytes. To represent null pointer, you may use a Java
    /// {@code null} instead. See the documentation of {@link IPointer#segment()} for more details.
    ///
    /// The constructor of this class is marked as {@link UnsafeConstructor}, because it does not
    /// perform any runtime check. The constructor can be useful for automatic code generators.
    @ValueBasedCandidate
    @UnsafeConstructor
    public record Ptr(@NotNull MemorySegment segment) implements IShaderSourceSpirv, Iterable<ShaderSourceSpirv> {
        public long size() {
            return segment.byteSize() / ShaderSourceSpirv.BYTES;
        }

        /// Returns (a pointer to) the structure at the given index.
        ///
        /// Note that unlike {@code read} series functions ({@link IntPtr#read()} for
        /// example), modification on returned structure will be reflected on the original
        /// structure array. So this function is called {@code at} to explicitly
        /// indicate that the returned structure is a view of the original structure.
        public @NotNull ShaderSourceSpirv at(long index) {
            return new ShaderSourceSpirv(segment.asSlice(index * ShaderSourceSpirv.BYTES, ShaderSourceSpirv.BYTES));
        }

        public void write(long index, @NotNull ShaderSourceSpirv value) {
            MemorySegment s = segment.asSlice(index * ShaderSourceSpirv.BYTES, ShaderSourceSpirv.BYTES);
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
            return new Ptr(segment.reinterpret(newSize * ShaderSourceSpirv.BYTES));
        }

        public @NotNull Ptr offset(long offset) {
            return new Ptr(segment.asSlice(offset * ShaderSourceSpirv.BYTES));
        }

        /// Note that this function uses the {@link List#subList(int, int)} semantics (left inclusive,
        /// right exclusive interval), not {@link MemorySegment#asSlice(long, long)} semantics
        /// (offset + newSize). Be careful with the difference
        public @NotNull Ptr slice(long start, long end) {
            return new Ptr(segment.asSlice(
                start * ShaderSourceSpirv.BYTES,
                (end - start) * ShaderSourceSpirv.BYTES
            ));
        }

        public Ptr slice(long end) {
            return new Ptr(segment.asSlice(0, end * ShaderSourceSpirv.BYTES));
        }

        public ShaderSourceSpirv[] toArray() {
            ShaderSourceSpirv[] ret = new ShaderSourceSpirv[(int) size()];
            for (long i = 0; i < size(); i++) {
                ret[(int) i] = at(i);
            }
            return ret;
        }

        @Override
        public @NotNull Iterator<ShaderSourceSpirv> iterator() {
            return new Iter(this.segment());
        }

        /// An iterator over the structures.
        private static final class Iter implements Iterator<ShaderSourceSpirv> {
            Iter(@NotNull MemorySegment segment) {
                this.segment = segment;
            }

            @Override
            public boolean hasNext() {
                return segment.byteSize() >= ShaderSourceSpirv.BYTES;
            }

            @Override
            public ShaderSourceSpirv next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                ShaderSourceSpirv ret = new ShaderSourceSpirv(segment.asSlice(0, ShaderSourceSpirv.BYTES));
                segment = segment.asSlice(ShaderSourceSpirv.BYTES);
                return ret;
            }

            private @NotNull MemorySegment segment;
        }
    }

    public static ShaderSourceSpirv allocate(Arena arena) {
        return new ShaderSourceSpirv(arena.allocate(LAYOUT));
    }

    public static ShaderSourceSpirv.Ptr allocate(Arena arena, long count) {
        MemorySegment segment = arena.allocate(LAYOUT, count);
        return new ShaderSourceSpirv.Ptr(segment);
    }

    public static ShaderSourceSpirv clone(Arena arena, ShaderSourceSpirv src) {
        ShaderSourceSpirv ret = allocate(arena);
        ret.segment.copyFrom(src.segment);
        return ret;
    }

    public @Unsigned int codeSize() {
        return segment.get(LAYOUT$codeSize, OFFSET$codeSize);
    }

    public ShaderSourceSpirv codeSize(@Unsigned int value) {
        segment.set(LAYOUT$codeSize, OFFSET$codeSize, value);
        return this;
    }

    public @Unsigned int code() {
        return segment.get(LAYOUT$code, OFFSET$code);
    }

    public ShaderSourceSpirv code(@Unsigned int value) {
        segment.set(LAYOUT$code, OFFSET$code, value);
        return this;
    }

    public static final StructLayout LAYOUT = NativeLayout.structLayout(
        ValueLayout.JAVA_INT.withName("codeSize"),
        ValueLayout.JAVA_INT.withName("code")
    );
    public static final long BYTES = LAYOUT.byteSize();

    public static final PathElement PATH$codeSize = PathElement.groupElement("codeSize");
    public static final PathElement PATH$code = PathElement.groupElement("code");

    public static final OfInt LAYOUT$codeSize = (OfInt) LAYOUT.select(PATH$codeSize);
    public static final OfInt LAYOUT$code = (OfInt) LAYOUT.select(PATH$code);

    public static final long SIZE$codeSize = LAYOUT$codeSize.byteSize();
    public static final long SIZE$code = LAYOUT$code.byteSize();

    public static final long OFFSET$codeSize = LAYOUT.byteOffset(PATH$codeSize);
    public static final long OFFSET$code = LAYOUT.byteOffset(PATH$code);
}
