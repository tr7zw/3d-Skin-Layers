package dev.tr7zw.skinlayers.opengl;

import com.google.common.base.Charsets;

import net.minecraft.client.renderer.texture.TextureUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NativeImage implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int OFFSET_A = 24;

    private static final int OFFSET_B = 16;

    private static final int OFFSET_G = 8;

    private static final int OFFSET_R = 0;

    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    private final Format format;

    private final int width;

    private final int height;

    private final boolean useStbFree;

    private long pixels;

    private final long size;

    public NativeImage(int i, int j, boolean bl) {
        this(Format.RGBA, i, j, bl);
    }

    public NativeImage(Format format, int i, int j, boolean bl) {
        if (i <= 0 || j <= 0)
            throw new IllegalArgumentException("Invalid texture size: " + i + "x" + j);
        this.format = format;
        this.width = i;
        this.height = j;
        this.size = i * j * format.components();
        this.useStbFree = false;
        if (bl) {
            this.pixels = MemoryUtil.nmemCalloc(1L, this.size);
        } else {
            this.pixels = MemoryUtil.nmemAlloc(this.size);
        }
    }

    private NativeImage(Format format, int i, int j, boolean bl, long l) {
        if (i <= 0 || j <= 0)
            throw new IllegalArgumentException("Invalid texture size: " + i + "x" + j);
        this.format = format;
        this.width = i;
        this.height = j;
        this.useStbFree = bl;
        this.pixels = l;
        this.size = i * j * format.components();
    }

    public String toString() {
        return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pixels
                + (this.useStbFree ? "S" : "N") + "]";
    }

    private boolean isOutsideBounds(int i, int j) {
        return (i < 0 || i >= this.width || j < 0 || j >= this.height);
    }

    public static NativeImage read(InputStream inputStream) throws IOException {
        return read(Format.RGBA, inputStream);
    }

    public static NativeImage read(Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = readResource(inputStream);
            byteBuffer.rewind();
            return read(format, byteBuffer);
        } finally {
            MemoryUtil.memFree(byteBuffer);
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer;
        if (inputStream instanceof FileInputStream) {
            FileInputStream fileInputStream = (FileInputStream) inputStream;
            FileChannel fileChannel = fileInputStream.getChannel();
            byteBuffer = MemoryUtil.memAlloc((int) fileChannel.size() + 1);
            while (fileChannel.read(byteBuffer) != -1);
        } else {
            byteBuffer = MemoryUtil.memAlloc(8192);
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
            while (readableByteChannel.read(byteBuffer) != -1) {
                if (byteBuffer.remaining() == 0)
                    byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }
        }
        return byteBuffer;
    }

    public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
        return read(Format.RGBA, byteBuffer);
    }

    public static NativeImage read(Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.supportedByStb())
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        if (MemoryUtil.memAddress(byteBuffer) == 0L)
            throw new IllegalArgumentException("Invalid buffer");
        MemoryStack memoryStack = MemoryStack.stackPush();
        try {
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3,
                    (format == null) ? 0 : format.components);
            if (byteBuffer2 == null)
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            NativeImage nativeImage = new NativeImage(
                    (format == null) ? Format.getStbFormat(intBuffer3.get(0)) : format, intBuffer.get(0),
                    intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2));
            if (memoryStack != null)
                memoryStack.close();
            return nativeImage;
        } catch (Throwable throwable) {
            if (memoryStack != null)
                try {
                    memoryStack.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }

    private void checkAllocated() {
        if (this.pixels == 0L)
            throw new IllegalStateException("Image is not allocated.");
    }

    public void close() {
        if (this.pixels != 0L)
            if (this.useStbFree) {
                STBImage.nstbi_image_free(this.pixels);
            } else {
                MemoryUtil.nmemFree(this.pixels);
            }
        this.pixels = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format format() {
        return this.format;
    }

    public int getPixelRGBA(int i, int j) {
        if (this.format != Format.RGBA)
            throw new IllegalArgumentException(
                    String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        checkAllocated();
        long l = (i + j * this.width) * 4L;
        return MemoryUtil.memGetInt(this.pixels + l);
    }

    public void setPixelRGBA(int i, int j, int k) {
        if (this.format != Format.RGBA)
            throw new IllegalArgumentException(
                    String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        checkAllocated();
        long l = (i + j * this.width) * 4L;
        MemoryUtil.memPutInt(this.pixels + l, k);
    }

    public void setPixelLuminance(int i, int j, byte b) {
        //RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminance())
            throw new IllegalArgumentException(String.format(
                    "setPixelLuminance only works on image with luminance; have %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        checkAllocated();
        long l = (i + j * this.width) * this.format.components() + (this.format.luminanceOffset() / 8);
        MemoryUtil.memPutByte(this.pixels + l, b);
    }

    public byte getRedOrLuminance(int i, int j) {
        //RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrRed())
            throw new IllegalArgumentException(String.format("no red or luminance in %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrRedOffset() / 8;
        return MemoryUtil.memGetByte(this.pixels + k);
    }

    public byte getGreenOrLuminance(int i, int j) {
        //RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrGreen())
            throw new IllegalArgumentException(String.format("no green or luminance in %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrGreenOffset() / 8;
        return MemoryUtil.memGetByte(this.pixels + k);
    }

    public byte getBlueOrLuminance(int i, int j) {
        //RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrBlue())
            throw new IllegalArgumentException(String.format("no blue or luminance in %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrBlueOffset() / 8;
        return MemoryUtil.memGetByte(this.pixels + k);
    }

    public byte getLuminanceOrAlpha(int i, int j) {
        if (!this.format.hasLuminanceOrAlpha())
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return MemoryUtil.memGetByte(this.pixels + k);
    }

    public void blendPixel(int i, int j, int k) {
        if (this.format != Format.RGBA)
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        int l = getPixelRGBA(i, j);
        float f = getA(k) / 255.0F;
        float g = getB(k) / 255.0F;
        float h = getG(k) / 255.0F;
        float m = getR(k) / 255.0F;
        float n = getA(l) / 255.0F;
        float o = getB(l) / 255.0F;
        float p = getG(l) / 255.0F;
        float q = getR(l) / 255.0F;
        float r = f;
        float s = 1.0F - f;
        float t = f * r + n * s;
        float u = g * r + o * s;
        float v = h * r + p * s;
        float w = m * r + q * s;
        if (t > 1.0F)
            t = 1.0F;
        if (u > 1.0F)
            u = 1.0F;
        if (v > 1.0F)
            v = 1.0F;
        if (w > 1.0F)
            w = 1.0F;
        int x = (int) (t * 255.0F);
        int y = (int) (u * 255.0F);
        int z = (int) (v * 255.0F);
        int aa = (int) (w * 255.0F);
        setPixelRGBA(i, j, combine(x, y, z, aa));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA)
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        checkAllocated();
        int[] is = new int[getWidth() * getHeight()];
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                int k = getPixelRGBA(j, i);
                int l = getA(k);
                int m = getB(k);
                int n = getG(k);
                int o = getR(k);
                int p = l << 24 | o << 16 | n << 8 | m;
                is[j + i * getWidth()] = p;
            }
        }
        return is;
    }

    public void downloadTexture(int i, boolean bl) {
        //RenderSystem.assertOnRenderThread();
        checkAllocated();
        this.format.setPackPixelStoreState();
        GlStateManager._getTexImage(3553, i, this.format.glFormat(), 5121, this.pixels);
        if (bl && this.format.hasAlpha())
            for (int j = 0; j < getHeight(); j++) {
                for (int k = 0; k < getWidth(); k++)
                    setPixelRGBA(k, j, getPixelRGBA(k, j) | 255 << this.format.alphaOffset());
            }
    }

//    public void downloadDepthBuffer(float f) {
//        //RenderSystem.assertOnRenderThread();
//        if (this.format.components() != 1)
//            throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
//        checkAllocated();
//        this.format.setPackPixelStoreState();
//        GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pixels);
//    }


    public void writeToFile(String string) throws IOException {
        writeToFile(FileSystems.getDefault().getPath(string, new String[0]));
    }

    public void writeToFile(File file) throws IOException {
        writeToFile(file.toPath());
    }

    private static class WriteCallback extends STBIWriteCallback {
        private final WritableByteChannel output;

        private IOException exception;

        WriteCallback(WritableByteChannel writableByteChannel) {
            this.output = writableByteChannel;
        }

        public void invoke(long l, long m, int i) {
            ByteBuffer byteBuffer = getData(m, i);
            try {
                this.output.write(byteBuffer);
            } catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwIfException() throws IOException {
            if (this.exception != null)
                throw this.exception;
        }
    }

    public void writeToFile(Path path) throws IOException {
        if (!this.format.supportedByStb())
            throw new UnsupportedOperationException("Don't know how to write format " + this.format);
        checkAllocated();
        WritableByteChannel writableByteChannel = Files.newByteChannel(path, (Set) OPEN_OPTIONS,
                (FileAttribute<?>[]) new FileAttribute[0]);
        try {
            if (!writeToChannel(writableByteChannel))
                throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": "
                        + STBImage.stbi_failure_reason());
            if (writableByteChannel != null)
                writableByteChannel.close();
        } catch (Throwable throwable) {
            if (writableByteChannel != null)
                try {
                    writableByteChannel.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }

    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
            try {
                if (!writeToChannel(writableByteChannel))
                    throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
                byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
                if (writableByteChannel != null)
                    writableByteChannel.close();
                byteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Throwable throwable) {
                if (writableByteChannel != null)
                    try {
                        writableByteChannel.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        } catch (Throwable throwable) {
            try {
                byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
    }

    private boolean writeToChannel(WritableByteChannel writableByteChannel) throws IOException {
        WriteCallback writeCallback = new WriteCallback(writableByteChannel);
        try {
            int i = Math.min(getHeight(), Integer.MAX_VALUE / getWidth() / this.format.components());
            if (i < getHeight())
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int",
                        Integer.valueOf(getHeight()), Integer.valueOf(i));
            if (STBImageWrite.nstbi_write_png_to_func(writeCallback.address(), 0L, getWidth(), i,
                    this.format.components(), this.pixels, 0) == 0)
                return false;
            writeCallback.throwIfException();
            return true;
        } finally {
            writeCallback.free();
        }
    }

    public void fillRect(int i, int j, int k, int l, int m) {
        for (int n = j; n < j + l; n++) {
            for (int o = i; o < i + k; o++)
                setPixelRGBA(o, n, m);
        }
    }

    public void copyRect(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
        for (int o = 0; o < n; o++) {
            for (int p = 0; p < m; p++) {
                int q = bl ? (m - 1 - p) : p;
                int r = bl2 ? (n - 1 - o) : o;
                int s = getPixelRGBA(i + p, j + o);
                setPixelRGBA(i + k + q, j + l + r, s);
            }
        }
    }

    public void flipY() {
        checkAllocated();
        MemoryStack memoryStack = MemoryStack.stackPush();
        try {
            int i = this.format.components();
            int j = getWidth() * i;
            long l = memoryStack.nmalloc(j);
            for (int k = 0; k < getHeight() / 2; k++) {
                int m = k * getWidth() * i;
                int n = (getHeight() - 1 - k) * getWidth() * i;
                MemoryUtil.memCopy(this.pixels + m, l, j);
                MemoryUtil.memCopy(this.pixels + n, this.pixels + m, j);
                MemoryUtil.memCopy(l, this.pixels + n, j);
            }
            if (memoryStack != null)
                memoryStack.close();
        } catch (Throwable throwable) {
            if (memoryStack != null)
                try {
                    memoryStack.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }

    public void resizeSubRectTo(int i, int j, int k, int l, NativeImage nativeImage) {
        checkAllocated();
        if (nativeImage.format() != this.format)
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        int m = this.format.components();
        STBImageResize.nstbir_resize_uint8(this.pixels + ((i + j * getWidth()) * m), k, l, getWidth() * m,
                nativeImage.pixels, nativeImage.getWidth(), nativeImage.getHeight(), 0, m);
    }

    public static NativeImage fromBase64(String string) throws IOException {
        byte[] bs = Base64.getDecoder().decode(string.replaceAll("\n", "").getBytes(Charsets.UTF_8));
        MemoryStack memoryStack = MemoryStack.stackPush();
        try {
            ByteBuffer byteBuffer = memoryStack.malloc(bs.length);
            byteBuffer.put(bs);
            byteBuffer.rewind();
            NativeImage nativeImage = read(byteBuffer);
            if (memoryStack != null)
                memoryStack.close();
            return nativeImage;
        } catch (Throwable throwable) {
            if (memoryStack != null)
                try {
                    memoryStack.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }

    public static int getA(int i) {
        return i >> 24 & 0xFF;
    }

    public static int getR(int i) {
        return i >> 0 & 0xFF;
    }

    public static int getG(int i) {
        return i >> 8 & 0xFF;
    }

    public static int getB(int i) {
        return i >> 16 & 0xFF;
    }

    public static int combine(int i, int j, int k, int l) {
        return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | (l & 0xFF) << 0;
    }

    public enum InternalGlFormat {
        RGBA(6408), RGB(6407), RG(33319), RED(6403);

        private final int glFormat;

        InternalGlFormat(int j) {
            this.glFormat = j;
        }

        public int glFormat() {
            return this.glFormat;
        }
    }

    public enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true), RGB(3, 6407, true, true, true, false,
                false, 0, 8, 16, 255, 255, true), LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255,
                        255, 0, 8, true), LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        final int components;

        private final int glFormat;

        private final boolean hasRed;

        private final boolean hasGreen;

        private final boolean hasBlue;

        private final boolean hasLuminance;

        private final boolean hasAlpha;

        private final int redOffset;

        private final int greenOffset;

        private final int blueOffset;

        private final int luminanceOffset;

        private final int alphaOffset;

        private final boolean supportedByStb;

        Format(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o,
                int p, boolean bl6) {
            this.components = j;
            this.glFormat = k;
            this.hasRed = bl;
            this.hasGreen = bl2;
            this.hasBlue = bl3;
            this.hasLuminance = bl4;
            this.hasAlpha = bl5;
            this.redOffset = l;
            this.greenOffset = m;
            this.blueOffset = n;
            this.luminanceOffset = o;
            this.alphaOffset = p;
            this.supportedByStb = bl6;
        }

        public int components() {
            return this.components;
        }

        public void setPackPixelStoreState() {
            //RenderSystem.assertOnRenderThread();
            GlStateManager._pixelStore(3333, components());
        }

        public void setUnpackPixelStoreState() {
            GlStateManager._pixelStore(3317, components());
        }

        public int glFormat() {
            return this.glFormat;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int redOffset() {
            return this.redOffset;
        }

        public int greenOffset() {
            return this.greenOffset;
        }

        public int blueOffset() {
            return this.blueOffset;
        }

        public int luminanceOffset() {
            return this.luminanceOffset;
        }

        public int alphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasLuminanceOrRed() {
            return (this.hasLuminance || this.hasRed);
        }

        public boolean hasLuminanceOrGreen() {
            return (this.hasLuminance || this.hasGreen);
        }

        public boolean hasLuminanceOrBlue() {
            return (this.hasLuminance || this.hasBlue);
        }

        public boolean hasLuminanceOrAlpha() {
            return (this.hasLuminance || this.hasAlpha);
        }

        public int luminanceOrRedOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.redOffset;
        }

        public int luminanceOrGreenOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
        }

        public int luminanceOrBlueOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
        }

        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean supportedByStb() {
            return this.supportedByStb;
        }

        static Format getStbFormat(int i) {
            switch (i) {
                case 1 :
                    return LUMINANCE;
                case 2 :
                    return LUMINANCE_ALPHA;
                case 3 :
                    return RGB;
            }
            return RGBA;
        }
    }
}