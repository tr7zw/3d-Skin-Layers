package dev.tr7zw.skinlayers;

import java.util.WeakHashMap;

import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.world.item.ItemStack;

public class SkullRendererCache {

    public static boolean renderNext = false;
    public static SkullSettings lastSkull = null;
    public static WeakHashMap<ItemStack, SkullSettings> itemCache = new WeakHashMap<>();
    
    public static class ItemSettings implements SkullSettings {

        private Mesh hatModel = null;
        
        @Override
        public Mesh getHeadLayers() {
            return hatModel;
        }

        @Override
        public void setupHeadLayers(Mesh box) {
            this.hatModel = box;
        }
        
    }
    
}
