package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.PlayerData;
import net.minecraft.resources.*;

public interface PlayerSettings extends PlayerData {

    public void setHeadMesh(Mesh box);

    public void setTorsoMesh(Mesh box);

    public void setLeftArmMesh(Mesh box);

    public void setRightArmMesh(Mesh box);

    public void setLeftLegMesh(Mesh box);

    public void setRightLegMesh(Mesh box);

    public /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getCurrentSkin();

    public void setCurrentSkin(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ skin);

    public boolean hasThinArms();

    public void setThinArms(boolean thin);

    public default void clearMeshes() {
        setHeadMesh(null);
        setTorsoMesh(null);
        setLeftArmMesh(null);
        setRightArmMesh(null);
        setLeftLegMesh(null);
        setRightLegMesh(null);
    }

}
