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

/// Represents a pointer to a {@code PassTimestampWrites} structure in native memory.
///
/// ## Structure
///
/// {@snippet lang=c :
/// typedef struct PassTimestampWrites {
///     ChainedStruct* nextInChain; // @link substring="ChainedStruct" target="ChainedStruct" @link substring="nextInChain" target="#nextInChain"
///     QuerySet querySet; // @link substring="QuerySet" target="QuerySet" @link substring="querySet" target="#querySet"
///     uint32_t beginningOfPassWriteIndex; // @link substring="beginningOfPassWriteIndex" target="#beginningOfPassWriteIndex"
///     uint32_t endOfPassWriteIndex; // @link substring="endOfPassWriteIndex" target="#endOfPassWriteIndex"
/// } PassTimestampWrites;
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
public record PassTimestampWrites(@NotNull MemorySegment segment) implements IPassTimestampWrites {
    /// Represents a pointer to / an array of null structure(s) in native memory.
    ///
    /// Technically speaking, this type has no difference with {@link PassTimestampWrites}. This type
    /// is introduced mainly for user to distinguish between a pointer to a single structure
    /// and a pointer to (potentially) an array of structure(s). APIs should use interface
    /// IPassTimestampWrites to handle both types uniformly. See package level documentation for more
    /// details.
    ///
    /// ## Contracts
    ///
    /// The property {@link #segment()} should always be not-null
    /// ({@code segment != NULL && !segment.equals(MemorySegment.NULL)}), and properly aligned to
    /// {@code PassTimestampWrites.LAYOUT.byteAlignment()} bytes. To represent null pointer, you may use a Java
    /// {@code null} instead. See the documentation of {@link IPointer#segment()} for more details.
    ///
    /// The constructor of this class is marked as {@link UnsafeConstructor}, because it does not
    /// perform any runtime check. The constructor can be useful for automatic code generators.
    @ValueBasedCandidate
    @UnsafeConstructor
    public record Ptr(@NotNull MemorySegment segment) implements IPassTimestampWrites, Iterable<PassTimestampWrites> {
        public long size() {
            return segment.byteSize() / PassTimestampWrites.BYTES;
        }

        /// Returns (a pointer to) the structure at the given index.
        ///
        /// Note that unlike {@code read} series functions ({@link IntPtr#read()} for
        /// example), modification on returned structure will be reflected on the original
        /// structure array. So this function is called {@code at} to explicitly
        /// indicate that the returned structure is a view of the original structure.
        public @NotNull PassTimestampWrites at(long index) {
            return new PassTimestampWrites(segment.asSlice(index * PassTimestampWrites.BYTES, PassTimestampWrites.BYTES));
        }

        public void write(long index, @NotNull PassTimestampWrites value) {
            MemorySegment s = segment.asSlice(index * PassTimestampWrites.BYTES, PassTimestampWrites.BYTES);
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
            return new Ptr(segment.reinterpret(newSize * PassTimestampWrites.BYTES));
        }

        public @NotNull Ptr offset(long offset) {
            return new Ptr(segment.asSlice(offset * PassTimestampWrites.BYTES));
        }

        /// Note that this function uses the {@link List#subList(int, int)} semantics (left inclusive,
        /// right exclusive interval), not {@link MemorySegment#asSlice(long, long)} semantics
        /// (offset + newSize). Be careful with the difference
        public @NotNull Ptr slice(long start, long end) {
            return new Ptr(segment.asSlice(
                start * PassTimestampWrites.BYTES,
                (end - start) * PassTimestampWrites.BYTES
            ));
        }

        public Ptr slice(long end) {
            return new Ptr(segment.asSlice(0, end * PassTimestampWrites.BYTES));
        }

        public PassTimestampWrites[] toArray() {
            PassTimestampWrites[] ret = new PassTimestampWrites[(int) size()];
            for (long i = 0; i < size(); i++) {
                ret[(int) i] = at(i);
            }
            return ret;
        }

        @Override
        public @NotNull Iterator<PassTimestampWrites> iterator() {
            return new Iter(this.segment());
        }

        /// An iterator over the structures.
        private static final class Iter implements Iterator<PassTimestampWrites> {
            Iter(@NotNull MemorySegment segment) {
                this.segment = segment;
            }

            @Override
            public boolean hasNext() {
                return segment.byteSize() >= PassTimestampWrites.BYTES;
            }

            @Override
            public PassTimestampWrites next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                PassTimestampWrites ret = new PassTimestampWrites(segment.asSlice(0, PassTimestampWrites.BYTES));
                segment = segment.asSlice(PassTimestampWrites.BYTES);
                return ret;
            }

            private @NotNull MemorySegment segment;
        }
    }

    public static PassTimestampWrites allocate(Arena arena) {
        return new PassTimestampWrites(arena.allocate(LAYOUT));
    }

    public static PassTimestampWrites.Ptr allocate(Arena arena, long count) {
        MemorySegment segment = arena.allocate(LAYOUT, count);
        return new PassTimestampWrites.Ptr(segment);
    }

    public static PassTimestampWrites clone(Arena arena, PassTimestampWrites src) {
        PassTimestampWrites ret = allocate(arena);
        ret.segment.copyFrom(src.segment);
        return ret;
    }

    public PassTimestampWrites nextInChain(@Nullable IChainedStruct value) {
        MemorySegment s = value == null ? MemorySegment.NULL : value.segment();
        nextInChainRaw(s);
        return this;
    }

    @Unsafe public @Nullable ChainedStruct.Ptr nextInChain(int assumedCount) {
        MemorySegment s = nextInChainRaw();
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }

        s = s.reinterpret(assumedCount * ChainedStruct.BYTES);
        return new ChainedStruct.Ptr(s);
    }

    public @Nullable ChainedStruct nextInChain() {
        MemorySegment s = nextInChainRaw();
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }
        return new ChainedStruct(s);
    }

    public @Pointer(target=ChainedStruct.class) MemorySegment nextInChainRaw() {
        return segment.get(LAYOUT$nextInChain, OFFSET$nextInChain);
    }

    public void nextInChainRaw(@Pointer(target=ChainedStruct.class) MemorySegment value) {
        segment.set(LAYOUT$nextInChain, OFFSET$nextInChain, value);
    }

    public @Nullable QuerySet querySet() {
        MemorySegment s = segment.asSlice(OFFSET$querySet, SIZE$querySet);
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }
        return new QuerySet(s);
    }

    public PassTimestampWrites querySet(@Nullable QuerySet value) {
        segment.set(LAYOUT$querySet, OFFSET$querySet, value != null ? value.segment() : MemorySegment.NULL);
        return this;
    }

    public @Unsigned int beginningOfPassWriteIndex() {
        return segment.get(LAYOUT$beginningOfPassWriteIndex, OFFSET$beginningOfPassWriteIndex);
    }

    public PassTimestampWrites beginningOfPassWriteIndex(@Unsigned int value) {
        segment.set(LAYOUT$beginningOfPassWriteIndex, OFFSET$beginningOfPassWriteIndex, value);
        return this;
    }

    public @Unsigned int endOfPassWriteIndex() {
        return segment.get(LAYOUT$endOfPassWriteIndex, OFFSET$endOfPassWriteIndex);
    }

    public PassTimestampWrites endOfPassWriteIndex(@Unsigned int value) {
        segment.set(LAYOUT$endOfPassWriteIndex, OFFSET$endOfPassWriteIndex, value);
        return this;
    }

    public static final StructLayout LAYOUT = NativeLayout.structLayout(
        ValueLayout.ADDRESS.withTargetLayout(ChainedStruct.LAYOUT).withName("nextInChain"),
        ValueLayout.ADDRESS.withName("querySet"),
        ValueLayout.JAVA_INT.withName("beginningOfPassWriteIndex"),
        ValueLayout.JAVA_INT.withName("endOfPassWriteIndex")
    );
    public static final long BYTES = LAYOUT.byteSize();

    public static final PathElement PATH$nextInChain = PathElement.groupElement("nextInChain");
    public static final PathElement PATH$querySet = PathElement.groupElement("querySet");
    public static final PathElement PATH$beginningOfPassWriteIndex = PathElement.groupElement("beginningOfPassWriteIndex");
    public static final PathElement PATH$endOfPassWriteIndex = PathElement.groupElement("endOfPassWriteIndex");

    public static final AddressLayout LAYOUT$nextInChain = (AddressLayout) LAYOUT.select(PATH$nextInChain);
    public static final AddressLayout LAYOUT$querySet = (AddressLayout) LAYOUT.select(PATH$querySet);
    public static final OfInt LAYOUT$beginningOfPassWriteIndex = (OfInt) LAYOUT.select(PATH$beginningOfPassWriteIndex);
    public static final OfInt LAYOUT$endOfPassWriteIndex = (OfInt) LAYOUT.select(PATH$endOfPassWriteIndex);

    public static final long SIZE$nextInChain = LAYOUT$nextInChain.byteSize();
    public static final long SIZE$querySet = LAYOUT$querySet.byteSize();
    public static final long SIZE$beginningOfPassWriteIndex = LAYOUT$beginningOfPassWriteIndex.byteSize();
    public static final long SIZE$endOfPassWriteIndex = LAYOUT$endOfPassWriteIndex.byteSize();

    public static final long OFFSET$nextInChain = LAYOUT.byteOffset(PATH$nextInChain);
    public static final long OFFSET$querySet = LAYOUT.byteOffset(PATH$querySet);
    public static final long OFFSET$beginningOfPassWriteIndex = LAYOUT.byteOffset(PATH$beginningOfPassWriteIndex);
    public static final long OFFSET$endOfPassWriteIndex = LAYOUT.byteOffset(PATH$endOfPassWriteIndex);
}
