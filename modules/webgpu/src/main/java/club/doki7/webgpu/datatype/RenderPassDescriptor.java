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

/// Represents a pointer to a {@code RenderPassDescriptor} structure in native memory.
///
/// ## Structure
///
/// {@snippet lang=c :
/// typedef struct RenderPassDescriptor {
///     ChainedStruct* nextInChain; // @link substring="ChainedStruct" target="ChainedStruct" @link substring="nextInChain" target="#nextInChain"
///     StringView label; // @link substring="StringView" target="StringView" @link substring="label" target="#label"
///     size_t colorAttachmentCount; // @link substring="colorAttachmentCount" target="#colorAttachmentCount"
///     RenderPassColorAttachment const* colorAttachments; // @link substring="RenderPassColorAttachment" target="RenderPassColorAttachment" @link substring="colorAttachments" target="#colorAttachments"
///     RenderPassDepthStencilAttachment depthStencilAttachment; // optional // @link substring="RenderPassDepthStencilAttachment" target="RenderPassDepthStencilAttachment" @link substring="depthStencilAttachment" target="#depthStencilAttachment"
///     QuerySet occlusionQuerySet; // optional // @link substring="QuerySet" target="QuerySet" @link substring="occlusionQuerySet" target="#occlusionQuerySet"
///     PassTimestampWrites timestampWrites; // optional // @link substring="PassTimestampWrites" target="PassTimestampWrites" @link substring="timestampWrites" target="#timestampWrites"
/// } RenderPassDescriptor;
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
public record RenderPassDescriptor(@NotNull MemorySegment segment) implements IRenderPassDescriptor {
    /// Represents a pointer to / an array of null structure(s) in native memory.
    ///
    /// Technically speaking, this type has no difference with {@link RenderPassDescriptor}. This type
    /// is introduced mainly for user to distinguish between a pointer to a single structure
    /// and a pointer to (potentially) an array of structure(s). APIs should use interface
    /// IRenderPassDescriptor to handle both types uniformly. See package level documentation for more
    /// details.
    ///
    /// ## Contracts
    ///
    /// The property {@link #segment()} should always be not-null
    /// ({@code segment != NULL && !segment.equals(MemorySegment.NULL)}), and properly aligned to
    /// {@code RenderPassDescriptor.LAYOUT.byteAlignment()} bytes. To represent null pointer, you may use a Java
    /// {@code null} instead. See the documentation of {@link IPointer#segment()} for more details.
    ///
    /// The constructor of this class is marked as {@link UnsafeConstructor}, because it does not
    /// perform any runtime check. The constructor can be useful for automatic code generators.
    @ValueBasedCandidate
    @UnsafeConstructor
    public record Ptr(@NotNull MemorySegment segment) implements IRenderPassDescriptor, Iterable<RenderPassDescriptor> {
        public long size() {
            return segment.byteSize() / RenderPassDescriptor.BYTES;
        }

        /// Returns (a pointer to) the structure at the given index.
        ///
        /// Note that unlike {@code read} series functions ({@link IntPtr#read()} for
        /// example), modification on returned structure will be reflected on the original
        /// structure array. So this function is called {@code at} to explicitly
        /// indicate that the returned structure is a view of the original structure.
        public @NotNull RenderPassDescriptor at(long index) {
            return new RenderPassDescriptor(segment.asSlice(index * RenderPassDescriptor.BYTES, RenderPassDescriptor.BYTES));
        }

        public void write(long index, @NotNull RenderPassDescriptor value) {
            MemorySegment s = segment.asSlice(index * RenderPassDescriptor.BYTES, RenderPassDescriptor.BYTES);
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
            return new Ptr(segment.reinterpret(newSize * RenderPassDescriptor.BYTES));
        }

        public @NotNull Ptr offset(long offset) {
            return new Ptr(segment.asSlice(offset * RenderPassDescriptor.BYTES));
        }

        /// Note that this function uses the {@link List#subList(int, int)} semantics (left inclusive,
        /// right exclusive interval), not {@link MemorySegment#asSlice(long, long)} semantics
        /// (offset + newSize). Be careful with the difference
        public @NotNull Ptr slice(long start, long end) {
            return new Ptr(segment.asSlice(
                start * RenderPassDescriptor.BYTES,
                (end - start) * RenderPassDescriptor.BYTES
            ));
        }

        public Ptr slice(long end) {
            return new Ptr(segment.asSlice(0, end * RenderPassDescriptor.BYTES));
        }

        public RenderPassDescriptor[] toArray() {
            RenderPassDescriptor[] ret = new RenderPassDescriptor[(int) size()];
            for (long i = 0; i < size(); i++) {
                ret[(int) i] = at(i);
            }
            return ret;
        }

        @Override
        public @NotNull Iterator<RenderPassDescriptor> iterator() {
            return new Iter(this.segment());
        }

        /// An iterator over the structures.
        private static final class Iter implements Iterator<RenderPassDescriptor> {
            Iter(@NotNull MemorySegment segment) {
                this.segment = segment;
            }

            @Override
            public boolean hasNext() {
                return segment.byteSize() >= RenderPassDescriptor.BYTES;
            }

            @Override
            public RenderPassDescriptor next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                RenderPassDescriptor ret = new RenderPassDescriptor(segment.asSlice(0, RenderPassDescriptor.BYTES));
                segment = segment.asSlice(RenderPassDescriptor.BYTES);
                return ret;
            }

            private @NotNull MemorySegment segment;
        }
    }

    public static RenderPassDescriptor allocate(Arena arena) {
        return new RenderPassDescriptor(arena.allocate(LAYOUT));
    }

    public static RenderPassDescriptor.Ptr allocate(Arena arena, long count) {
        MemorySegment segment = arena.allocate(LAYOUT, count);
        return new RenderPassDescriptor.Ptr(segment);
    }

    public static RenderPassDescriptor clone(Arena arena, RenderPassDescriptor src) {
        RenderPassDescriptor ret = allocate(arena);
        ret.segment.copyFrom(src.segment);
        return ret;
    }

    public RenderPassDescriptor nextInChain(@Nullable IChainedStruct value) {
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

    public @NotNull StringView label() {
        return new StringView(segment.asSlice(OFFSET$label, LAYOUT$label));
    }

    public RenderPassDescriptor label(@NotNull StringView value) {
        MemorySegment.copy(value.segment(), 0, segment, OFFSET$label, SIZE$label);
        return this;
    }

    public RenderPassDescriptor label(Consumer<@NotNull StringView> consumer) {
        consumer.accept(label());
        return this;
    }

    public @Unsigned long colorAttachmentCount() {
        return NativeLayout.readCSizeT(segment, OFFSET$colorAttachmentCount);
    }

    public RenderPassDescriptor colorAttachmentCount(@Unsigned long value) {
        NativeLayout.writeCSizeT(segment, OFFSET$colorAttachmentCount, value);
        return this;
    }

    public RenderPassDescriptor colorAttachments(@Nullable IRenderPassColorAttachment value) {
        MemorySegment s = value == null ? MemorySegment.NULL : value.segment();
        colorAttachmentsRaw(s);
        return this;
    }

    @Unsafe public @Nullable RenderPassColorAttachment.Ptr colorAttachments(int assumedCount) {
        MemorySegment s = colorAttachmentsRaw();
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }

        s = s.reinterpret(assumedCount * RenderPassColorAttachment.BYTES);
        return new RenderPassColorAttachment.Ptr(s);
    }

    public @Nullable RenderPassColorAttachment colorAttachments() {
        MemorySegment s = colorAttachmentsRaw();
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }
        return new RenderPassColorAttachment(s);
    }

    public @Pointer(target=RenderPassColorAttachment.class) MemorySegment colorAttachmentsRaw() {
        return segment.get(LAYOUT$colorAttachments, OFFSET$colorAttachments);
    }

    public void colorAttachmentsRaw(@Pointer(target=RenderPassColorAttachment.class) MemorySegment value) {
        segment.set(LAYOUT$colorAttachments, OFFSET$colorAttachments, value);
    }

    public @NotNull RenderPassDepthStencilAttachment depthStencilAttachment() {
        return new RenderPassDepthStencilAttachment(segment.asSlice(OFFSET$depthStencilAttachment, LAYOUT$depthStencilAttachment));
    }

    public RenderPassDescriptor depthStencilAttachment(@NotNull RenderPassDepthStencilAttachment value) {
        MemorySegment.copy(value.segment(), 0, segment, OFFSET$depthStencilAttachment, SIZE$depthStencilAttachment);
        return this;
    }

    public RenderPassDescriptor depthStencilAttachment(Consumer<@NotNull RenderPassDepthStencilAttachment> consumer) {
        consumer.accept(depthStencilAttachment());
        return this;
    }

    public @Nullable QuerySet occlusionQuerySet() {
        MemorySegment s = segment.asSlice(OFFSET$occlusionQuerySet, SIZE$occlusionQuerySet);
        if (s.equals(MemorySegment.NULL)) {
            return null;
        }
        return new QuerySet(s);
    }

    public RenderPassDescriptor occlusionQuerySet(@Nullable QuerySet value) {
        segment.set(LAYOUT$occlusionQuerySet, OFFSET$occlusionQuerySet, value != null ? value.segment() : MemorySegment.NULL);
        return this;
    }

    public @NotNull PassTimestampWrites timestampWrites() {
        return new PassTimestampWrites(segment.asSlice(OFFSET$timestampWrites, LAYOUT$timestampWrites));
    }

    public RenderPassDescriptor timestampWrites(@NotNull PassTimestampWrites value) {
        MemorySegment.copy(value.segment(), 0, segment, OFFSET$timestampWrites, SIZE$timestampWrites);
        return this;
    }

    public RenderPassDescriptor timestampWrites(Consumer<@NotNull PassTimestampWrites> consumer) {
        consumer.accept(timestampWrites());
        return this;
    }

    public static final StructLayout LAYOUT = NativeLayout.structLayout(
        ValueLayout.ADDRESS.withTargetLayout(ChainedStruct.LAYOUT).withName("nextInChain"),
        StringView.LAYOUT.withName("label"),
        NativeLayout.C_SIZE_T.withName("colorAttachmentCount"),
        ValueLayout.ADDRESS.withTargetLayout(RenderPassColorAttachment.LAYOUT).withName("colorAttachments"),
        RenderPassDepthStencilAttachment.LAYOUT.withName("depthStencilAttachment"),
        ValueLayout.ADDRESS.withName("occlusionQuerySet"),
        PassTimestampWrites.LAYOUT.withName("timestampWrites")
    );
    public static final long BYTES = LAYOUT.byteSize();

    public static final PathElement PATH$nextInChain = PathElement.groupElement("nextInChain");
    public static final PathElement PATH$label = PathElement.groupElement("label");
    public static final PathElement PATH$colorAttachmentCount = PathElement.groupElement("colorAttachmentCount");
    public static final PathElement PATH$colorAttachments = PathElement.groupElement("colorAttachments");
    public static final PathElement PATH$depthStencilAttachment = PathElement.groupElement("depthStencilAttachment");
    public static final PathElement PATH$occlusionQuerySet = PathElement.groupElement("occlusionQuerySet");
    public static final PathElement PATH$timestampWrites = PathElement.groupElement("timestampWrites");

    public static final AddressLayout LAYOUT$nextInChain = (AddressLayout) LAYOUT.select(PATH$nextInChain);
    public static final StructLayout LAYOUT$label = (StructLayout) LAYOUT.select(PATH$label);
    public static final AddressLayout LAYOUT$colorAttachments = (AddressLayout) LAYOUT.select(PATH$colorAttachments);
    public static final StructLayout LAYOUT$depthStencilAttachment = (StructLayout) LAYOUT.select(PATH$depthStencilAttachment);
    public static final AddressLayout LAYOUT$occlusionQuerySet = (AddressLayout) LAYOUT.select(PATH$occlusionQuerySet);
    public static final StructLayout LAYOUT$timestampWrites = (StructLayout) LAYOUT.select(PATH$timestampWrites);

    public static final long SIZE$nextInChain = LAYOUT$nextInChain.byteSize();
    public static final long SIZE$label = LAYOUT$label.byteSize();
    public static final long SIZE$colorAttachmentCount = NativeLayout.C_SIZE_T.byteSize();
    public static final long SIZE$colorAttachments = LAYOUT$colorAttachments.byteSize();
    public static final long SIZE$depthStencilAttachment = LAYOUT$depthStencilAttachment.byteSize();
    public static final long SIZE$occlusionQuerySet = LAYOUT$occlusionQuerySet.byteSize();
    public static final long SIZE$timestampWrites = LAYOUT$timestampWrites.byteSize();

    public static final long OFFSET$nextInChain = LAYOUT.byteOffset(PATH$nextInChain);
    public static final long OFFSET$label = LAYOUT.byteOffset(PATH$label);
    public static final long OFFSET$colorAttachmentCount = LAYOUT.byteOffset(PATH$colorAttachmentCount);
    public static final long OFFSET$colorAttachments = LAYOUT.byteOffset(PATH$colorAttachments);
    public static final long OFFSET$depthStencilAttachment = LAYOUT.byteOffset(PATH$depthStencilAttachment);
    public static final long OFFSET$occlusionQuerySet = LAYOUT.byteOffset(PATH$occlusionQuerySet);
    public static final long OFFSET$timestampWrites = LAYOUT.byteOffset(PATH$timestampWrites);
}
