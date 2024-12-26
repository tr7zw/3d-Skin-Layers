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
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import dev.tr7zw.util.NMSHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
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

    private static NativeImage getSkinTexture(AbstractClientPlayer player) {
        return getTexture(NMSHelper.getPlayerSkin(player), null);
    }

    private static NativeImage getTexture(ResourceLocation resourceLocation, SkullSettings settings) {
        try {
            // spotless:off
            //#if MC >= 11900
            Optional<Resource> optionalRes = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            if (optionalRes.isPresent()) {
                Resource resource = optionalRes.get();
                NativeImage skin = NativeImage.read(resource.open());
            //#else
            //$$    if(Minecraft.getInstance().getResourceManager().hasResource(resourceLocation)) {
            //$$        Resource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            //$$        NativeImage skin = NativeImage.read(resource.getInputStream());
            //#endif 
            // spotless:on
                return skin;
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
            if (texture == null) {
                return null;
            }
            NativeImage cachedImage = cache.getIfPresent(texture);
            if (cachedImage != null) {
                try {
                    checkAllocation(cachedImage);
                    return cachedImage;
                } catch (Exception ex) {
                    // got invalidated, remove from cache
                    cache.invalidate(texture);
                }
            }
            if (texture instanceof HttpTextureAccessor) {
                HttpTextureAccessor httpTexture = (HttpTextureAccessor) texture;
                try {
                    NativeImage img = httpTexture.getImage();
                    if (img != null) {
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
                    if (img != null) {
                        checkAllocation(img);
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
            settings.setInitialized(true); // initialize as invalid
            SkinLayersModBase.LOGGER.warn("Unable to handle skin " + resourceLocation
                    + ". Potentially a conflict with another mod. (" + texture.getClass().getName() + ")");
            return null;
        } catch (Exception ex) {
            SkinLayersModBase.LOGGER.error("Error while resolving a skin texture.", ex);
            return null;
        }
    }

    private static void checkAllocation(NativeImage image) throws Exception {
        // spotless:off 
        //#if MC >= 12102
        image.getLuminanceOrAlpha(0,0); // check that it's allocated
        //#else
        //$$ image.getPixelRGBA(0, 0); // check that it's allocated
        //#endif
        //spotless:on
    }

    public static boolean setup3dLayers(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings,
            boolean thinArms) {
        ResourceLocation skinLocation = NMSHelper.getPlayerSkin(abstractClientPlayerEntity);
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
        NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
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
        ResourceLocation playerSkin = NMSHelper.getPlayerSkin(gameprofile);
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
