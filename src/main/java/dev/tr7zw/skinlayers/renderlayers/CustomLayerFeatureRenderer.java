package dev.tr7zw.skinlayers.renderlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.LayerFeatureTransformerAPI;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CustomLayerFeatureRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final Minecraft mc = Minecraft.getInstance();
    public static final Set<Item> hideHeadLayers = Sets.newHashSet(Items.ZOMBIE_HEAD, Items.CREEPER_HEAD,
            Items.DRAGON_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL);
    private final List<Layer> bodyLayers = new ArrayList<>();
    private final boolean thinArms;

    public CustomLayerFeatureRenderer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
        thinArms = ((PlayerEntityModelAccessor) getParentModel()).hasThinArms();
        bodyLayers.add(new Layer(PlayerSettings::getHeadMesh, PlayerModelPart.HAT, OffsetProvider.HEAD,
                () -> this.getParentModel().head, (player) -> {
                    ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
                    if (itemStack != null && hideHeadLayers.contains(itemStack.getItem())) {
                        return false;
                    }
                    return SkinLayersModBase.config.enableHat;
                }));
        bodyLayers
                .add(new Layer(PlayerSettings::getLeftLegMesh, PlayerModelPart.LEFT_PANTS_LEG, OffsetProvider.LEFT_LEG,
                        () -> this.getParentModel().leftLeg, (player) -> SkinLayersModBase.config.enableLeftPants));
        bodyLayers.add(
                new Layer(PlayerSettings::getRightLegMesh, PlayerModelPart.RIGHT_PANTS_LEG, OffsetProvider.RIGHT_LEG,
                        () -> this.getParentModel().rightLeg, (player) -> SkinLayersModBase.config.enableRightPants));
        bodyLayers.add(new Layer(PlayerSettings::getLeftArmMesh, PlayerModelPart.LEFT_SLEEVE,
                thinArms ? OffsetProvider.LEFT_ARM_SLIM : OffsetProvider.LEFT_ARM, () -> this.getParentModel().leftArm,
                (player) -> SkinLayersModBase.config.enableLeftSleeve));
        bodyLayers.add(new Layer(PlayerSettings::getRightArmMesh, PlayerModelPart.RIGHT_SLEEVE,
                thinArms ? OffsetProvider.RIGHT_ARM_SLIM : OffsetProvider.RIGHT_ARM,
                () -> this.getParentModel().rightArm, (player) -> SkinLayersModBase.config.enableRightSleeve));
        bodyLayers.add(new Layer(PlayerSettings::getTorsoMesh, PlayerModelPart.JACKET, OffsetProvider.BODY,
                () -> this.getParentModel().body, (player) -> SkinLayersModBase.config.enableJacket));
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer player,
            float f, float g, float h, float j, float k, float l) {
        if (SkinLayersModBase.config.compatibilityMode || player.isInvisible()) {
            return;
        }
        if (mc.level == null) {
            return; // in a menu or something and the model gets rendered
        }
        if (player.distanceToSqr(
                mc.gameRenderer.getMainCamera().getPosition()) > SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD)
            return;

        PlayerSettings settings = (PlayerSettings) player;
        // check for it being setup first to speedup the rendering
        if (!SkinUtil.setup3dLayers(player, settings, thinArms, this.getParentModel())) {
            return; // no head layer setup and wasn't able to setup
        }
        // spotless:off
        //#if MC >= 12002
        VertexConsumer vertexConsumer = multiBufferSource
                .getBuffer(RenderType.entityTranslucent(player.getSkin().texture(), true));
        //#else
        //$$ VertexConsumer vertexConsumer = multiBufferSource
        //$$        .getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation(), true));
        //#endif
        //spotless:on
        int m = LivingEntityRenderer.getOverlayCoords(player, 0.0f);
        renderLayers(player, settings, poseStack, vertexConsumer, i, m);
    }

    public void renderLayers(AbstractClientPlayer abstractClientPlayer, PlayerSettings settings, PoseStack matrixStack,
            VertexConsumer vertices, int light, int overlay) {
        for (Layer layer : bodyLayers) {
            Mesh mesh = layer.meshGetter.apply(settings);
            if (mesh != null && abstractClientPlayer.isModelPartShown(layer.modelPart)
                    && layer.vanillaGetter.get().visible && layer.configGetter.apply(abstractClientPlayer)) {
                matrixStack.pushPose();
                LayerFeatureTransformerAPI.getTransformer().transform(abstractClientPlayer, matrixStack,
                        layer.vanillaGetter.get());
                layer.vanillaGetter.get().translateAndRotate(matrixStack);
                layer.offset.applyOffset(matrixStack, mesh);

                mesh.render(layer.vanillaGetter.get(), matrixStack, vertices, light, overlay, 0xFFFFFFFF);
                matrixStack.popPose();
            }
        }

    }

    @AllArgsConstructor
    private static class Layer {
        private final Function<PlayerSettings, Mesh> meshGetter;
        private final PlayerModelPart modelPart;
        private final OffsetProvider offset;
        private final Supplier<ModelPart> vanillaGetter;
        private final Function<AbstractClientPlayer, Boolean> configGetter;
    }

}
