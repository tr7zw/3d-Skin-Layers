package dev.tr7zw.skinlayers.versionless.util.wrapper;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;

public interface TextureData {

    public boolean isPresent(UV onTextureUV);

    public boolean isSolid(UV onTextureUV);

    public default int getWidth() {
        return 64; // Default width, can be overridden if needed
    }

    public default int getHeight() {
        return 64; // Default height, can be overridden if needed
    }

}
