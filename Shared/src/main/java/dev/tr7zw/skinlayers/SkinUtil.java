package dev.tr7zw.skinlayers;

import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.accessor.HttpTextureAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class SkinUtil {

    private static NativeImage getSkinTexture(AbstractClientPlayer player) {
        return getTexture(player.getSkinTextureLocation());
    }
    
    private static NativeImage getTexture(ResourceLocation resourceLocation) {
        try {
            if(Minecraft.getInstance().getResourceManager().hasResource(resourceLocation)) {
                Resource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
                NativeImage skin = NativeImage.read(resource.getInputStream());
                return skin;
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
            if(texture instanceof HttpTextureAccessor) {
                HttpTextureAccessor httpTexture = (HttpTextureAccessor) texture;
                try {
                    return httpTexture.getImage();
                }catch(Exception ex) {
                    //not there
                }
            }
           SkinLayersModBase.LOGGER.warn("Unable to handle skin " + resourceLocation + ". Potentially a conflict with another mod.");
            return null;
        }catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static boolean setup3dLayers(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings, boolean thinArms, PlayerModel<AbstractClientPlayer> model) {
        ResourceLocation skinLocation = abstractClientPlayerEntity.getSkinTextureLocation();
        if(skinLocation == null) {
            return false;//this *should* never happen, but just to be sure
        }
        if(skinLocation.equals(settings.getCurrentSkin()) && thinArms == settings.hasThinArms()) { // if they are equal, the skin is processed and either failed or is ready
            return settings.getSkinLayers() != null;
        }
        // Starting here should only run in case the skin has changed by getting loaded/another mod changed the skin
        NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
        try {
            if(skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) { // Skin is null or not a 64x64 skin, hd skins won't work
                settings.setCurrentSkin(skinLocation);
                settings.setThinArms(thinArms);
                settings.setupHeadLayers(null);
                settings.setupSkinLayers(null);
                return false;
            }
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
            settings.setCurrentSkin(skinLocation);
            settings.setThinArms(thinArms);
            return true;
        }finally {
            if(skin != null)
                skin.close();
        }
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
        ResourceLocation resourceLocation = Minecraft.getInstance().getSkinManager()
                .registerTexture(texture, MinecraftProfileTexture.Type.SKIN);
        NativeImage skin = SkinUtil.getTexture(resourceLocation);
        try {
            if(skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) { 
                return false;
            }
            settings.setupHeadLayers(SolidPixelWrapper.wrapBox(skin, 8, 8, 8, 32, 0, false, 0.6f));
            return true;
        }finally {
            if(skin != null)
                skin.close();
        }
    }
    
}
