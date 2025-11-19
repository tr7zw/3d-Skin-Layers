package dev.tr7zw.skinlayers.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Keep player specific settings, data and modifies the eye location when
 * enabled
 *
 */
@Mixin(
//? if >= 1.21.9 {

net.minecraft.world.entity.Avatar.class
//? } else {
/*
 net.minecraft.world.entity.player.Player.class
*///? }
)
public abstract class PlayerMixin extends LivingEntity implements PlayerSettings {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    private Mesh headMesh;
    private Mesh torsoMesh;
    private Mesh leftArmMesh;
    private Mesh rightArmMesh;
    private Mesh leftLegMesh;
    private Mesh rightLegMesh;
    private ResourceLocation currentSkin = null;
    private boolean thinarms = false;

    @Override
    public Mesh getHeadMesh() {
        return headMesh;
    }

    @Override
    public void setHeadMesh(Mesh headMesh) {
        this.headMesh = headMesh;
    }

    @Override
    public Mesh getTorsoMesh() {
        return torsoMesh;
    }

    @Override
    public void setTorsoMesh(Mesh torsoMesh) {
        this.torsoMesh = torsoMesh;
    }

    @Override
    public Mesh getLeftArmMesh() {
        return leftArmMesh;
    }

    @Override
    public void setLeftArmMesh(Mesh leftArmMesh) {
        this.leftArmMesh = leftArmMesh;
    }

    @Override
    public Mesh getRightArmMesh() {
        return rightArmMesh;
    }

    @Override
    public void setRightArmMesh(Mesh rightArmMesh) {
        this.rightArmMesh = rightArmMesh;
    }

    @Override
    public Mesh getLeftLegMesh() {
        return leftLegMesh;
    }

    @Override
    public void setLeftLegMesh(Mesh leftLegMesh) {
        this.leftLegMesh = leftLegMesh;
    }

    @Override
    public Mesh getRightLegMesh() {
        return rightLegMesh;
    }

    @Override
    public void setRightLegMesh(Mesh rightLegMesh) {
        this.rightLegMesh = rightLegMesh;
    }

    @Override
    public ResourceLocation getCurrentSkin() {
        return currentSkin;
    }

    @Override
    public void setCurrentSkin(ResourceLocation skin) {
        this.currentSkin = skin;
    }

    @Override
    public UUID getUUID() {
        return super.uuid;
    }

    @Override
    public boolean hasThinArms() {
        return thinarms;
    }

    @Override
    public void setThinArms(boolean thin) {
        this.thinarms = thin;
    }

}
