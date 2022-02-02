package dev.tr7zw.skinlayers.renderlayers;

import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HeadLayerFeatureRenderer implements LayerRenderer<AbstractClientPlayer> {

	private Set<Item> hideHeadLayers = Sets.newHashSet(Items.skull);
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	private RenderPlayer playerRenderer;
	
    public HeadLayerFeatureRenderer(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float paramFloat1, float paramFloat2, float paramFloat3,
            float deltaTick, float paramFloat5, float paramFloat6, float paramFloat7) {
//    }
//	
//	@Override
//    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
//            AbstractClientPlayer player, float f, float g, float h, float j, float k,
//			float l) {
		if (!player.hasSkin() || player.isInvisible() || !SkinLayersModBase.config.enableHat) {
			return;
		}
		if(mc.thePlayer.getPositionVector().squareDistanceTo(player.getPositionVector()) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;
		
		ItemStack itemStack = player.getEquipmentInSlot(1); //TODO
		if (itemStack != null && hideHeadLayers.contains(itemStack.getItem())) {
			return;
		}
		
		PlayerSettings settings = (PlayerSettings) player;
		// check for it being setup first to speedup the rendering
		if(settings.getHeadLayers() == null && !setupModel(player, settings)) {
			return; // no head layer setup and wasn't able to setup
		}

		this.playerRenderer.bindTexture(player.getLocationSkin());
		renderCustomHelmet(settings, player, deltaTick);
	}

	private boolean setupModel(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings) {
		
		if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
			return false; // default skin
		}
		SkinUtil.setup3dLayers(abstractClientPlayerEntity, settings, false, null); //TODO
		return true;
	}

	public void renderCustomHelmet(PlayerSettings settings, AbstractClientPlayer abstractClientPlayer, float deltaTick) {
		if(settings.getHeadLayers() == null)return;
		if(playerRenderer.getMainModel().bipedHead.isHidden)return;
		float voxelSize = SkinLayersModBase.config.headVoxelSize;
		GlStateManager.pushMatrix();
		playerRenderer.getMainModel().bipedHead.postRender(deltaTick);
		//this.getParentModel().head.translateAndRotate(matrixStack);
	    GlStateManager.scale(0.0625, 0.0625, 0.0625);
		GlStateManager.scale(voxelSize, voxelSize, voxelSize);
		// Overlay refuses to work correctly, this is a workaround for now
		boolean red = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
		float color = red ? 0.5f : 1f;
		settings.getHeadLayers().render();
		GlStateManager.popMatrix();

	}

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
	

}
