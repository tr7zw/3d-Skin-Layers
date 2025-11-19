package dev.tr7zw.skinlayers;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.accessor.HttpTextureAccessor;
import dev.tr7zw.skinlayers.accessor.NativeImageAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import dev.tr7zw.transition.mc.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class SkinUtil {

    private static Cache<AbstractTexture, NativeImage> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(60L, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<AbstractTexture, NativeImage>() {

                @Override
                public void onRemoval(RemovalNotification<AbstractTexture, NativeImage> notification) {
                    try {
                        notification.getValue().close();
                    } catch (Exception ex) {
                        SkinLayersModBase.LOGGER.error("Error while closing a texture.", ex);
                    }
                }
            }).build();

    public static NativeImage getTexture(ResourceLocation resourceLocation, SkullSettings settings) {
        if (resourceLocation == null) {
            return null;
        }
        try {
            //? if >= 1.19.0 {

            Optional<Resource> optionalRes = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            if (optionalRes.isPresent()) {
                Resource resource = optionalRes.get();
                NativeImage skin = NativeImage.read(resource.open());
                //? } else {

                //    if(Minecraft.getInstance().getResourceManager().hasResource(resourceLocation)) {
                //        Resource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
                //        NativeImage skin = NativeImage.read(resource.getInputStream());
                //? }
                return skin;
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
            if (texture == null) {
                return null;
            }
            NativeImage cachedImage = cache.getIfPresent(texture);
            if (cachedImage != null && (Object) cachedImage instanceof NativeImageAccessor ac
                    && ac.skinlayers$isAllocated()) {
                return cachedImage;
            } else {
                // got invalidated, remove from cache
                cache.invalidate(texture);
            }
            if (texture instanceof HttpTextureAccessor) {
                HttpTextureAccessor httpTexture = (HttpTextureAccessor) texture;
                try {
                    NativeImage img = httpTexture.getImage();
                    if (img != null && (Object) img instanceof NativeImageAccessor ac && ac.skinlayers$isAllocated()) {
                        cache.put(texture, img);
                        return img;
                    }
                } catch (Exception ex) {
                    // not there
                }
                return null; // not yet initialized, but also not ready
            }
            if (texture instanceof DynamicTexture) {
                try {
                    NativeImage img = ((DynamicTexture) texture).getPixels();
                    if (img != null && (Object) img instanceof NativeImageAccessor ac && ac.skinlayers$isAllocated()) {
                        return img;
                    }
                } catch (Exception ex) {
                    // not backed by an image
                }
                return null; // not yet initialized, but also not ready
            }
            // This would work, but hd skins will crash the JVM. Only
            /*
             * try { NativeImage img = new NativeImage(Format.RGBA, 64, 64, true);
             * GlStateManager._bindTexture(texture.getId()); img.downloadTexture(0, false);
             * cache.put(texture, img); return img; }catch(Exception ex) {
             * SkinLayersModBase.LOGGER.
             * error("Error while trying to grab a texture from the GPU.", ex); }
             */
            if (settings != null) {
                settings.setInitialized(false); // initialize as invalid
            }
            SkinLayersModBase.LOGGER.warn("Unable to handle skin " + resourceLocation
                    + ". Potentially a conflict with another mod. (" + texture.getClass().getName() + ")");
            return null;
        } catch (Exception ex) {
            SkinLayersModBase.LOGGER.error("Error while resolving a skin texture.", ex);
            return null;
        }
    }

    public static boolean setup3dLayers(
            //? if >= 1.21.9 {

            net.minecraft.world.entity.Avatar abstractClientPlayerEntity,
            //? } else {
            /*
                     net.minecraft.client.player.AbstractClientPlayer abstractClientPlayerEntity,
                    *///? }
            PlayerSettings settings, boolean thinArms) {
        ResourceLocation skinLocation = PlayerUtil.getPlayerSkin(abstractClientPlayerEntity);
        if (skinLocation == null) {
            return false;// this *should* never happen, but just to be sure
        }
        if (skinLocation.equals(settings.getCurrentSkin()) && thinArms == settings.hasThinArms()) { // if they are
                                                                                                    // equal, the skin
                                                                                                    // is processed and
                                                                                                    // either failed or
                                                                                                    // is ready
            return settings.getHeadMesh() != null;
        }
        // Starting here should only run in case the skin has changed by getting
        // loaded/another mod changed the skin
        NativeImage skin = getTexture(skinLocation, null);
        if (skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) { // Skin is null or not a 64x64 skin, hd
                                                                               // skins won't work
            settings.setCurrentSkin(skinLocation);
            settings.setThinArms(thinArms);
            settings.clearMeshes();
            return false;
        }
        settings.setLeftLegMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 4, 12, 4, 0, 48, true, 0f));
        settings.setRightLegMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 4, 12, 4, 0, 32, true, 0f));
        if (thinArms) {
            settings.setLeftArmMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 3, 12, 4, 48, 48, true, -2f));
            settings.setRightArmMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 3, 12, 4, 40, 32, true, -2f));
        } else {
            settings.setLeftArmMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 4, 12, 4, 48, 48, true, -2));
            settings.setRightArmMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 4, 12, 4, 40, 32, true, -2));
        }
        settings.setTorsoMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 8, 12, 4, 16, 32, true, 0));
        settings.setHeadMesh(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 8, 8, 8, 32, 0, false, 0.6f));
        settings.setCurrentSkin(skinLocation);
        settings.setThinArms(thinArms);
        return true;
    }

    public static boolean setup3dLayers(GameProfile gameprofile, SkullSettings settings) {
        if (gameprofile == null) {
            return false; // no gameprofile
        }
        ResourceLocation playerSkin = PlayerUtil.getPlayerSkin(gameprofile);
        if (playerSkin == null) {
            return false; // no skin
        }
        NativeImage skin = SkinUtil.getTexture(playerSkin, settings);
        if (skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) {
            return false;
        }
        settings.setupHeadLayers(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 8, 8, 8, 32, 0, false, 0.6f));
        settings.setInitialized(true);
        return true;
    }

    public static boolean setup3dLayers(ResourceLocation playerSkin, SkullSettings settings) {
        if (playerSkin == null) {
            return false; // no skin
        }
        NativeImage skin = SkinUtil.getTexture(playerSkin, settings);
        if (skin == null || skin.getWidth() != 64 || skin.getHeight() != 64) {
            return false;
        }
        settings.setupHeadLayers(SkinLayersAPI.getMeshHelper().create3DMesh(skin, 8, 8, 8, 32, 0, false, 0.6f));
        settings.setInitialized(true);
        return true;
    }

}
