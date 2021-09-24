package dev.tr7zw.skinlayers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;

public class SkinUtil {

    public static boolean hasCustomSkin(AbstractClientPlayer player) {
        return player.hasSkin();
    }

    public static BufferedImage getSkinTexture(AbstractClientPlayer player) throws IOException {
        IResource resource =  Minecraft.getMinecraft().getResourceManager().getResource(player.getLocationSkin());
        return TextureUtil.readBufferedImage(resource.getInputStream());
    }
    
}
