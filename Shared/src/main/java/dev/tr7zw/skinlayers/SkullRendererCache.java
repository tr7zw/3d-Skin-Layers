package dev.tr7zw.skinlayers;

import java.util.WeakHashMap;

import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SkullRendererCache {

    public static boolean renderNext = false;
    public static SkullSettings lastSkull = null;
    public static WeakHashMap<ItemStack, SkullSettings> itemCache = new WeakHashMap<>();

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
        public void setLastTexture(ResourceLocation texture) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public ResourceLocation getLastTexture() {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
