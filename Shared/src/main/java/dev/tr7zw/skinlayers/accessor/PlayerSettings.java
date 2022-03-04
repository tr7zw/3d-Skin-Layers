package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.resources.ResourceLocation;

public interface PlayerSettings {

	public CustomizableModelPart getHeadLayers();
	
	public void setupHeadLayers(CustomizableModelPart box);
	
	public CustomizableModelPart[] getSkinLayers();
	
	public void setupSkinLayers(CustomizableModelPart[] box);
	
	public ResourceLocation getCurrentSkin();
    
    public void setCurrentSkin(ResourceLocation skin);
    
    public boolean hasThinArms();
    
    public void setThinArms(boolean thin);

}
