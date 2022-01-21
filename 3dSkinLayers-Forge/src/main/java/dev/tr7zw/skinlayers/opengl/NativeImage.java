package dev.tr7zw.skinlayers.opengl;

import java.nio.ByteBuffer;

public final class NativeImage implements AutoCloseable {
    
    private final Format format;

    private final int width;

    private final int height;

    private ByteBuffer buffer;

    private final int size;

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
        buffer = ByteBuffer.allocateDirect(this.size);
    }

    private boolean isOutsideBounds(int i, int j) {
        return (i < 0 || i >= this.width || j < 0 || j >= this.height);
    }

    public void close() {
        // nothing to do?
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
        int l = (i + j * this.width) * 4;
        return buffer.getInt(l);
    }

    public void setPixelRGBA(int i, int j, int k) {
        if (this.format != Format.RGBA)
            throw new IllegalArgumentException(
                    String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int l = (i + j * this.width) * 4;
        buffer.putInt(l, k);
    }

    public byte getLuminanceOrAlpha(int i, int j) {
        if (!this.format.hasLuminanceOrAlpha())
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        if (isOutsideBounds(i, j))
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(i),
                            Integer.valueOf(j), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
        int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return buffer.get(k);
    }


    public void downloadTexture(int i, boolean bl) {
        //RenderSystem.assertOnRenderThread();
        this.format.setPackPixelStoreState();
        GlStateManager._getTexImage(3553, i, this.format.glFormat(), 5121, this.buffer);
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