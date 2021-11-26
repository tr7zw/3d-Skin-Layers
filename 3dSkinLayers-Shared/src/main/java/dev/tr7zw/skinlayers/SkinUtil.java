package dev.tr7zw.skinlayers;

import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class SkinUtil {

    public static boolean hasCustomSkin(AbstractClientPlayer player) {
        return !DefaultPlayerSkin.getDefaultSkin((player).getUUID()).equals((player).getSkinTextureLocation());
    }

    private static NativeImage getSkinTexture(AbstractClientPlayer player) {
        return getTexture(player.getSkinTextureLocation());
    }
    
    private static NativeImage getTexture(ResourceLocation resource) {
        NativeImage skin = new NativeImage(Format.RGBA, 64, 64, true);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(resource);
        if(abstractTexture == null)return null; // fail save
        GlStateManager._bindTexture(abstractTexture.getId());
        skin.downloadTexture(0, false);
        return skin;
    }
    
    public static boolean setup3dLayers(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings, boolean thinArms, PlayerModel<AbstractClientPlayer> model) {
        if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
            return false; // default skin
        }
        NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
        if(skin == null)return false; // fail save
        CustomizableModelPart[] layers = new CustomizableModelPart[5];
        layers[0] = SolidPixelWrapper.wrapBoxOptimized(skin, 4, 12, 4, 0, 48, true, 0f);
        layers[1] = SolidPixelWrapper.wrapBoxOptimized(skin, 4, 12, 4, 0, 32, true, 0f);
        if(thinArms) {
            layers[2] = SolidPixelWrapper.wrapBoxOptimized(skin, 3, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBoxOptimized(skin, 3, 12, 4, 40, 32, true, -2.5f);
        } else {
            layers[2] = SolidPixelWrapper.wrapBoxOptimized(skin, 4, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBoxOptimized(skin, 4, 12, 4, 40, 32, true, -2.5f);
        }
        layers[4] = SolidPixelWrapper.wrapBoxOptimized(skin, 8, 12, 4, 16, 32, true, -0.8f);
        settings.setupSkinLayers(layers);
        settings.setupHeadLayers(SolidPixelWrapper.wrapBoxOptimized(skin, 8, 8, 8, 32, 0, false, 0.6f));
        skin.close();
        return true;
    }
    
    public static boolean setup3dLayers(GameProfile gameprofile, SkullSettings settings) {
        if(gameprofile == null) {
            return false; // no gameprofile
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager()
                .getInsecureSkinInformation(gameprofile);
        MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
        if(texture == null) {
            return false; // it's a gameprofile, but no skin.
        }
        NativeImage skin = SkinUtil.getTexture(Minecraft.getInstance().getSkinManager()
                .registerTexture(texture, MinecraftProfileTexture.Type.SKIN));
        settings.setupHeadLayers(SolidPixelWrapper.wrapBoxOptimized(skin, 8, 8, 8, 32, 0, false, 0.6f));
        skin.close();
        return true;
    }
    
}
