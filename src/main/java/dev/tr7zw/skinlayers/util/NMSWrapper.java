package dev.tr7zw.skinlayers.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.UV;
import dev.tr7zw.skinlayers.versionless.util.wrapper.TextureData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
//spotless:off 
//#if MC >= 12002
import net.minecraft.client.resources.PlayerSkin;
//#else
//$$ import com.mojang.authlib.minecraft.MinecraftProfileTexture;
//$$ import java.util.Map;
//#endif
//#if MC <= 12004
//$$ import net.minecraft.Util;
//$$ import org.apache.commons.lang3.StringUtils;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.nbt.NbtUtils;
//#else
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
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

    public static GameProfile getGameProfile(ItemStack itemStack) {
        // spotless:off 
        //#if MC >= 12005
        if(itemStack.getComponents().has(DataComponents.CUSTOM_MODEL_DATA)) {
            return null;
        }
        if (itemStack.getComponents().has(DataComponents.PROFILE)) {
            ResolvableProfile resolvableProfile = (ResolvableProfile) itemStack.get(DataComponents.PROFILE);
            if (resolvableProfile != null && !resolvableProfile.isResolved()) {
                    itemStack.remove(DataComponents.PROFILE);
                    resolvableProfile.resolve().thenAcceptAsync(
                                    resolvableProfile2 -> itemStack.set(DataComponents.PROFILE, resolvableProfile2),
                                    Minecraft.getInstance());
                    resolvableProfile = null;
            }
            if(resolvableProfile != null) {
                return resolvableProfile.gameProfile();
            }
        }
        return null;
        //#else
        //$$ if (itemStack.hasTag()) {
        //$$     CompoundTag compoundTag = itemStack.getTag();
        //$$     if (compoundTag.contains("CustomModelData")) {
        //$$         return null; // do not try to 3d-fy custom head models
        //$$     }
        //$$     if (compoundTag.contains("SkullOwner", 10)) {
        //$$         return NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
        //$$     } else if (compoundTag.contains("SkullOwner", 8)
        //$$             && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
        //$$         return new GameProfile(Util.NIL_UUID, compoundTag.getString("SkullOwner"));
        //$$     }
        //$$ }
        //$$ return null;
        //#endif
        //spotless:on
    }

}
