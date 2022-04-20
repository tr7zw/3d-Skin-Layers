package dev.tr7zw.skinlayers.renderlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class BodyLayerFeatureRenderer 
implements LayerRenderer<AbstractClientPlayer> {
    
    private RenderPlayer playerRenderer;
    private final boolean thinArms;
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public BodyLayerFeatureRenderer(
            RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
            thinArms = ((PlayerEntityModelAccessor)playerRenderer).hasThinArms();
            bodyLayers.add(new Layer(0, false, EnumPlayerModelParts.LEFT_PANTS_LEG, Shape.LEGS, () -> playerRenderer.getMainModel().bipedLeftLeg, () -> SkinLayersModBase.config.enableLeftPants));
            bodyLayers.add(new Layer(1, false, EnumPlayerModelParts.RIGHT_PANTS_LEG, Shape.LEGS, () -> playerRenderer.getMainModel().bipedRightLeg, () -> SkinLayersModBase.config.enableRightPants));
            bodyLayers.add(new Layer(2, false, EnumPlayerModelParts.LEFT_SLEEVE, thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> playerRenderer.getMainModel().bipedLeftArm, () -> SkinLayersModBase.config.enableLeftSleeve));
            bodyLayers.add(new Layer(3, true, EnumPlayerModelParts.RIGHT_SLEEVE, thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> playerRenderer.getMainModel().bipedRightArm, () -> SkinLayersModBase.config.enableRightSleeve));
            bodyLayers.add(new Layer(4, false, EnumPlayerModelParts.JACKET, Shape.BODY, () -> playerRenderer.getMainModel().bipedBody, () -> SkinLayersModBase.config.enableJacket));
    }
    
    @Override
    public void doRenderLayer(AbstractClientPlayer player, float paramFloat1, float paramFloat2, float paramFloat3,
            float deltaTick, float paramFloat5, float paramFloat6, float paramFloat7) {
        if (!player.hasSkin() || player.isInvisible()) {
            return;
        }
        if(mc.theWorld == null) {
            return; // in a menu or something and the model gets rendered
        }
        if(mc.thePlayer.getPositionVector().squareDistanceTo(player.getPositionVector()) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;
        
        PlayerSettings settings = (PlayerSettings) player;
        // check for it being setup first to speedup the rendering
        if(settings.getSkinLayers() == null && !setupModel(player, settings)) {
            return; // no head layer setup and wasn't able to setup
        }

        
        //this.playerRenderer.bindTexture(player.getLocationSkin());
        renderLayers(player, (CustomizableModelPart[]) settings.getSkinLayers(), deltaTick);
    }

    private boolean setupModel(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings) {
        if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
            return false; // default skin
        }
        SkinUtil.setup3dLayers(abstractClientPlayerEntity, settings, thinArms, null);
        return true;
    }
    
    private final List<Layer> bodyLayers = new ArrayList<>();
    
    class Layer{
        int layersId;
        boolean mirrored;
        EnumPlayerModelParts modelPart;
        Shape shape;
        Supplier<ModelRenderer> vanillaGetter;
        Supplier<Boolean> configGetter;
        public Layer(int layersId, boolean mirrored, EnumPlayerModelParts modelPart, Shape shape,
                Supplier<ModelRenderer> vanillaGetter, Supplier<Boolean> configGetter) {
            this.layersId = layersId;
            this.mirrored = mirrored;
            this.modelPart = modelPart;
            this.shape = shape;
            this.vanillaGetter = vanillaGetter;
            this.configGetter = configGetter;
        }
        
    }
    
    
    private enum Shape {
        HEAD(0), BODY(0.6f), LEGS(-0.2f), ARMS(0.4f), ARMS_SLIM(0.4f)
        ;
        
        private final float yOffsetMagicValue;

        private Shape(float yOffsetMagicValue) {
            this.yOffsetMagicValue = yOffsetMagicValue;
        }

    }
    
    public void renderLayers(AbstractClientPlayer abstractClientPlayer, CustomizableModelPart[] layers, float deltaTick) {
        if(layers == null)return;
        float pixelScaling = SkinLayersModBase.config.baseVoxelSize;
        float heightScaling = 1.035f;
        float widthScaling = SkinLayersModBase.config.baseVoxelSize;
        // Overlay refuses to work correctly, this is a workaround for now
        boolean redTint = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
        for(Layer layer : bodyLayers) {
            if(abstractClientPlayer.isWearing(layer.modelPart) && !layer.vanillaGetter.get().isHidden && layer.configGetter.get()) {
                GlStateManager.pushMatrix();
                if(abstractClientPlayer.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }
                layer.vanillaGetter.get().postRender(0.0625F);
                if(layer.shape == Shape.ARMS) {
                    layers[layer.layersId].x = 0.998f*16;
                } else if(layer.shape == Shape.ARMS_SLIM) {
                    layers[layer.layersId].x = 0.499f*16;
                }
                if(layer.shape == Shape.BODY) {
                    widthScaling = SkinLayersModBase.config.bodyVoxelWidthSize;
                }else {
                    widthScaling = SkinLayersModBase.config.baseVoxelSize;
                }
                if(layer.mirrored) {
                    layers[layer.layersId].x *= -1;
                }
                GlStateManager.scale(0.0625, 0.0625, 0.0625);
                GlStateManager.scale(widthScaling, heightScaling, pixelScaling);
                layers[layer.layersId].y = layer.shape.yOffsetMagicValue;
                
                layers[layer.layersId].render(redTint);
                GlStateManager.popMatrix();
            }
        }
        
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
    
}