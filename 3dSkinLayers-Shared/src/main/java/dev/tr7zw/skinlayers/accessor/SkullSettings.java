package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.render.CustomizableModelPart;

public interface SkullSettings {

    public CustomizableModelPart getHeadLayers();
    
    public void setupHeadLayers(CustomizableModelPart box);
    
}
