package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.PlayerData;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.resources.ResourceLocation;

public interface PlayerSettings extends PlayerData{

	public CustomizableModelPart getHeadLayers();
	
	public void setupHeadLayers(CustomizableModelPart box);
	
	public CustomizableModelPart[] getSkinLayers();
	
	public void setupSkinLayers(CustomizableModelPart[] box);
	
	public ResourceLocation getCurrentSkin();
    
    public void setCurrentSkin(ResourceLocation skin);
    
    public boolean hasThinArms();
    
    public void setThinArms(boolean thin);

    @Override
    default Mesh getHeadMesh() {
        return getHeadLayers();
    }

    @Override
    default Mesh getTorsoMesh() {
        if(getSkinLayers() == null)
            return null;
        return getSkinLayers()[4];
    }

    @Override
    default Mesh getLeftArmMesh() {
        if(getSkinLayers() == null)
            return null;
        return getSkinLayers()[2];
    }

    @Override
    default Mesh getRightArmMesh() {
        if(getSkinLayers() == null)
            return null;
        return getSkinLayers()[3];
    }

    @Override
    default Mesh getLeftLegMesh() {
        if(getSkinLayers() == null)
            return null;
        return getSkinLayers()[0];
    }

    @Override
    default Mesh getRightLegMesh() {
        if(getSkinLayers() == null)
            return null;
        return getSkinLayers()[1];
    }

    
    
}
