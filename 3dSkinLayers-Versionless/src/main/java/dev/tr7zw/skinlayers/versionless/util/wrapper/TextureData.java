package dev.tr7zw.skinlayers.versionless.util.wrapper;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;

public interface TextureData {

    public boolean isPresent(UV onTextureUV);

    public boolean isSolid(UV onTextureUV);

}
