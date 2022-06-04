package dev.tr7zw.skinlayers;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
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
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class SkinUtil {

    private static Cache<AbstractTexture, NativeImage> cache = CacheBuilder.newBuilder().expireAfterAccess(60L, TimeUnit.SECONDS).removalListener(new RemovalListener<AbstractTexture, NativeImage>() {

        @Override
        public void onRemoval(RemovalNotification<AbstractTexture, NativeImage> notification) {
            try {
                notification.getValue().close();
            }catch(Exception ex) {
                SkinLayersModBase.LOGGER.error("Error while closing a texture.", ex);
            }
        }
    }).build();
    
    private static NativeImage getSkinTexture(AbstractClientPlayer player) {
        return getTexture(player.getSkinTextureLocation());
    }
    
    private static NativeImage getTexture(ResourceLocation resourceLocation) {
        try {
            Optional<Resource> optionalRes = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            if(optionalRes.isPresent()) {
                Resource resource = optionalRes.get();
                NativeImage skin = NativeImage.read(resource.open());
                return skin;
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
            if(texture == null) {
                return null;
            }
            NativeImage cachedImage = cache.getIfPresent(texture);
            if(cachedImage != null) {
                try {
                    cachedImage.getPixelRGBA(0, 0); // check that it's allocated
                    return cachedImage;
                }catch(Exception ex) {
                    // got invalidated, remove from cache
                    cache.invalidate(texture);
                }
            }
            if(texture instanceof HttpTextureAccessor) {
                HttpTextureAccessor httpTexture = (HttpTextureAccessor) texture;
                try {
                    NativeImage img = httpTexture.getImage();
                    cache.put(texture, img);
                    return img;
                }catch(FileNotFoundException ex) {
                    //not there
                }
            }
            if(texture instanceof DynamicTexture) {
                try {
                    NativeImage img = ((DynamicTexture) texture).getPixels();
                    img.getPixelRGBA(0, 0); // check that it's allocated
                    // Do not cache dynamic textures. It's a O(1) call to get them, and the cache would close them after 60 seconds
                    //cache.put(texture, img);
                    return img;
                }catch(Exception ex) {
                    // not backed by an image
                }
            }
            // This would work, but hd skins will crash the JVM. Only 
            /*
            try {
                NativeImage img = new NativeImage(Format.RGBA, 64, 64, true);
                GlStateManager._bindTexture(texture.getId());
                img.downloadTexture(0, false);
                cache.put(texture, img);
                return img;
            }catch(Exception ex) {
                SkinLayersModBase.LOGGER.error("Error while trying to grab a texture from the GPU.", ex);
            }
            */
           SkinLayersModBase.LOGGER.warn("Unable to handle skin " + resourceLocation + ". Potentially a conflict with another mod.");
            return null;
        }catch(Exception ex) {
            SkinLayersModBase.LOGGER.error("Error while resolving a skin texture.", ex);
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
        if(skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) { 
            return false;
        }
        settings.setupHeadLayers(SolidPixelWrapper.wrapBox(skin, 8, 8, 8, 32, 0, false, 0.6f));
        return true;
    }
    
}
