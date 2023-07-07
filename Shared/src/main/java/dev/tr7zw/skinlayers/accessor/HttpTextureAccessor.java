package dev.tr7zw.skinlayers.accessor;

import java.io.FileNotFoundException;

import com.mojang.blaze3d.platform.NativeImage;

public interface HttpTextureAccessor {

    public NativeImage getImage() throws FileNotFoundException;

}
