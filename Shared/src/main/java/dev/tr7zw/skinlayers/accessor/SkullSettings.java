package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.SkullData;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;

public interface SkullSettings extends SkullData {

    public CustomizableModelPart getHeadLayers();
    
    public void setupHeadLayers(CustomizableModelPart box);

    @Override
    default Mesh getMesh() {
        return getHeadLayers();
    }
    
}
