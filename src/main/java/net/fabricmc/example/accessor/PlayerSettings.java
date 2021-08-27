package net.fabricmc.example.accessor;

import net.fabricmc.example.render.CustomizableModelPart;

public interface PlayerSettings {

	public CustomizableModelPart getHeadLayers();
	
	public void setupHeadLayers(CustomizableModelPart box);
	
	public CustomizableModelPart[] getSkinLayers();
	
	public void setupSkinLayers(CustomizableModelPart[] box);

}
