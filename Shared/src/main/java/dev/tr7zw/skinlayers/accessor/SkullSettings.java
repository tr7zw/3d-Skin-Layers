package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.SkullData;

public interface SkullSettings extends SkullData {

    public Mesh getHeadLayers();
    
    public void setupHeadLayers(Mesh box);

    @Override
    default Mesh getMesh() {
        return getHeadLayers();
    }
    
}
