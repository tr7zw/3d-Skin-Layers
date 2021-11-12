package dev.tr7zw.skinlayers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;

public class SkinUtil {

    public static boolean hasCustomSkin(AbstractClientPlayer player) {
        return !DefaultPlayerSkin.getDefaultSkin((player).getUUID()).equals((player).getSkinTextureLocation());
    }

    public static NativeImage getSkinTexture(AbstractClientPlayer player) {
        NativeImage skin = new NativeImage(Format.RGBA, 64, 64, true);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(player.getSkinTextureLocation());
        if(abstractTexture == null)return null; // fail save
        GlStateManager._bindTexture(abstractTexture.getId());
        skin.downloadTexture(0, false);
        return skin;
    }
    
}
