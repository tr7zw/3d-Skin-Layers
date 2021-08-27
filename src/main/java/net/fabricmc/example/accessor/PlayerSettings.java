package net.fabricmc.example.accessor;

import net.minecraft.client.model.geom.ModelPart;

public interface PlayerSettings {

	public ModelPart getHeadLayers();
	
	public void setupHeadLayers(ModelPart box);
	
	public ModelPart[] getSkinLayers();
	
	public void setupSkinLayers(ModelPart[] box);

}
