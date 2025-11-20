package dev.tr7zw.skinlayers;

import java.util.WeakHashMap;

import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.resources.*;
//? if >= 1.21.4 {

import com.mojang.authlib.GameProfile;
//? } else {
/*
 import net.minecraft.world.item.ItemStack;
*///? }

public class SkullRendererCache {

    public static boolean renderNext = false;
    public static SkullSettings lastSkull = null;
    //? if >= 1.21.4 {

    public static WeakHashMap<GameProfile, SkullSettings> itemCache = new WeakHashMap<>();
    //? } else {
    /*
     public static WeakHashMap<ItemStack, SkullSettings> itemCache = new WeakHashMap<>();
    *///? }

    public static class ItemSettings implements SkullSettings {

        private Mesh hatModel = null;
        private boolean initialized = false;

        @Override
        public Mesh getHeadLayers() {
            return hatModel;
        }

        @Override
        public void setupHeadLayers(Mesh box) {
            this.hatModel = box;
        }

        @Override
        public boolean initialized() {
            return initialized;
        }

        @Override
        public void setInitialized(boolean initialized) {
            this.initialized = initialized;
        }

        @Override
        public void setLastTexture(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ texture) {
            // TODO Auto-generated method stub

        }

        @Override
        public /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getLastTexture() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static void clearCache() {
        itemCache.clear();
    }

}
