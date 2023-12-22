package dev.tr7zw.skinlayers.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;
import dev.tr7zw.skinlayers.versionless.util.wrapper.TextureData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
//spotless:off 
//#if MC >= 12002
import net.minecraft.client.resources.PlayerSkin;
//#else
//$$ import com.mojang.authlib.minecraft.MinecraftProfileTexture;
//$$ import java.util.Map;
//#endif
//spotless:on

public class NMSWrapper {

    public static class WrappedNativeImage implements TextureData {

        private final NativeImage natImage;

        public WrappedNativeImage(NativeImage natImage) {
            this.natImage = natImage;
        }

        @Override
        public boolean isPresent(UV onTextureUV) {
            return natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v()) != 0;
        }

        @Override
        public boolean isSolid(UV onTextureUV) {
            return natImage.getLuminanceOrAlpha(onTextureUV.u(), onTextureUV.v()) == -1;
        }

    }

    public static ResourceLocation getPlayerSkin(AbstractClientPlayer player) {
        // spotless:off
        //#if MC >= 12002
        return player.getSkin().texture();
        //#else
        //$$ return player.getSkinTextureLocation();
        //#endif
        //spotless:on
    }

    public static ResourceLocation getPlayerSkin(GameProfile gameprofile) {
        // spotless:off 
    	//#if MC >= 12002
        PlayerSkin playerSkin = Minecraft.getInstance().getSkinManager().getInsecureSkin(gameprofile);
        if (playerSkin.textureUrl() == null) {
            return null;
        }
        return playerSkin.texture();
        //#else
        //$$ Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager()
        //$$         .getInsecureSkinInformation(gameprofile);
        //$$ MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
        //$$  if (texture == null) {
        //$$      return null;
        //$$  }
        //$$  ResourceLocation resourceLocation = Minecraft.getInstance().getSkinManager().registerTexture(texture,
        //$$          MinecraftProfileTexture.Type.SKIN);
        //$$  return resourceLocation;
        //#endif
        //spotless:on
    }

}
