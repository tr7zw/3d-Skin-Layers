package dev.tr7zw.skinlayers;

import com.mojang.authlib.GameProfile;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.opengl.NativeImage;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class SkinUtil {

    public static boolean hasCustomSkin(AbstractClientPlayer player) {
        return !DefaultPlayerSkin.getDefaultSkin((player).getUniqueID()).equals((player).getLocationSkin());
    }

    private static NativeImage getSkinTexture(AbstractClientPlayer player) {
        return getTexture(player.getLocationSkin());
    }
    
    private static NativeImage getTexture(ResourceLocation resource) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject abstractTexture = textureManager.getTexture(resource);
        if(abstractTexture == null)return null; // fail save
        GlStateManager.bindTexture(abstractTexture.getGlTextureId());
        // glGetTexImage writes the whole level, whatever its size, and LWJGL cannot
        // check that against the buffer. A texture that is not the 64x64 skin layout
        // (an HD skin, or a resource pack's HD steve.png standing in while the real
        // skin downloads) would overflow the buffer and corrupt the native heap, which
        // shows up later as a crash in an unrelated thread. Ask for the size first and
        // skip anything else, the same way the modern versions skip HD skins.
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        if(width != 64 || height != 64) {
            return null; // not a 64x64 skin, hd skins won't work
        }
        NativeImage skin = new NativeImage(width, height, false);
        skin.downloadTexture(0, false);
        return skin;
    }
    
    public static boolean setup3dLayers(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings, boolean thinArms, ModelPlayer model) {
        if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
            return false; // default skin
        }
        NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
        if(skin == null)return false; // fail save
        CustomizableModelPart[] layers = new CustomizableModelPart[5];
        layers[0] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 0, 48, true, 0f);
        layers[1] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 0, 32, true, 0f);
        if(thinArms) {
            layers[2] = SolidPixelWrapper.wrapBox(skin, 3, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBox(skin, 3, 12, 4, 40, 32, true, -2.5f);
        } else {
            layers[2] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 40, 32, true, -2.5f);
        }
        layers[4] = SolidPixelWrapper.wrapBox(skin, 8, 12, 4, 16, 32, true, -0.8f);
        settings.setupSkinLayers(layers);
        settings.setupHeadLayers(SolidPixelWrapper.wrapBox(skin, 8, 8, 8, 32, 0, false, 0.6f));
        skin.close();
        return true;
    }
    
    public static boolean setup3dLayers(GameProfile gameprofile, SkullSettings settings) {
        if(gameprofile == null) {
            return false; // no gameprofile
        }
        /*Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getMinecraft().getSkinManager()
                .loadProfileTextures(gameprofile);
        MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
        if(texture == null) {
            return false; // it's a gameprofile, but no skin.
        }
        NativeImage skin = SkinUtil.getTexture(Minecraft.getMinecraft().getSkinManager()
                .registerTexture(texture, MinecraftProfileTexture.Type.SKIN));
        settings.setupHeadLayers(SolidPixelWrapper.wrapBox(skin, 8, 8, 8, 32, 0, false, 0.6f));
        skin.close();
        return true;*/
        return false;
    }
    
}