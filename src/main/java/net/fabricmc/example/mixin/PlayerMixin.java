package net.fabricmc.example.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.example.accessor.PlayerSettings;
import net.minecraft.client.model.geom.ModelPart;
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

	private ModelPart headLayer;
	private ModelPart[] skinLayer;
	

	@Override
	public ModelPart[] getSkinLayers() {
		return skinLayer;
	}
	
	@Override
	public void setupSkinLayers(ModelPart[] box) {
		this.skinLayer = box;
	}
	
	@Override
	public ModelPart getHeadLayers() {
		return headLayer;
	}
	
	@Override
	public void setupHeadLayers(ModelPart box) {
		this.headLayer = box;
	}

	@Override
	public UUID getUUID() {
		return super.uuid;
	}
	
}
