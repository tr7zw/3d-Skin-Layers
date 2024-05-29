package dev.tr7zw.skinlayers.util;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;
import dev.tr7zw.skinlayers.versionless.util.wrapper.TextureData;

public class NMSWrapper {

    public static class WrappedNativeImage implements TextureData {

        private final NativeImage natImage;

        public WrappedNativeImage(NativeImage natImage) {
            this.natImage = natImage;
        }

        @Override
        public boolean isPresent(UV onTextureUV) {
            return natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v()) != 0;
        }

        @Override
        public boolean isSolid(UV onTextureUV) {
            return natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v()) == -1;
        }

    }

}
