package dev.tr7zw.skinlayers.util;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;
import dev.tr7zw.skinlayers.versionless.util.wrapper.TextureData;

public class NMSWrapper {

    public static class WrappedNativeImage implements TextureData {

        private static final int SOLID_THRESHOLD = 230;
        private final NativeImage natImage;

        public WrappedNativeImage(NativeImage natImage) {
            this.natImage = natImage;
        }

        @Override
        public boolean isPresent(UV onTextureUV) {
            byte unsignedAlpha = natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v());
            int alpha = unsignedAlpha & 0xFF; // convert to int [0, 255]

            return alpha > (0xFF - SOLID_THRESHOLD);
        }

        @Override
        public boolean isSolid(UV onTextureUV) {
            byte unsignedAlpha = natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v());
            int alpha = unsignedAlpha & 0xFF; // convert to int [0, 255]

            return alpha > SOLID_THRESHOLD;
        }

        @Override
        public int getWidth() {
            return natImage.getWidth();
        }

        @Override
        public int getHeight() {
            return natImage.getHeight();
        }

    }

}
