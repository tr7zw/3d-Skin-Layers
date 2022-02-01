package dev.tr7zw.skinlayers.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Keep player specific settings, data and modifies the eye location when enabled
 *
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerSettings {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	private CustomizableModelPart headLayer;
	private CustomizableModelPart[] skinLayer;
	private ResourceLocation currentSkin = null;
	

	@Override
	public CustomizableModelPart[] getSkinLayers() {
		return skinLayer;
	}
	
	@Override
	public void setupSkinLayers(CustomizableModelPart[] box) {
		this.skinLayer = box;
	}
	
	@Override
	public CustomizableModelPart getHeadLayers() {
		return headLayer;
	}
	
	@Override
	public void setupHeadLayers(CustomizableModelPart box) {
		this.headLayer = box;
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
	
}
