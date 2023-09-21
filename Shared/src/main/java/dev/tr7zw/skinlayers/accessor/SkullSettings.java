package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.SkullData;
import net.minecraft.resources.ResourceLocation;

public interface SkullSettings extends SkullData {

    public Mesh getHeadLayers();

    public void setupHeadLayers(Mesh box);

    public boolean initialized();

    public void setInitialized(boolean initialized);
    
    public void setLastTexture(ResourceLocation texture);
    
    public ResourceLocation getLastTexture();

    @Override
    default Mesh getMesh() {
        return getHeadLayers();
    }

}
