package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.mojang.realmsclient.util.SkinProcessor;

import dev.tr7zw.skinlayers.Config;
import dev.tr7zw.skinlayers.SkinLayersMod;
import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testInjectedInterfaces() {
        Objenesis objenesis = new ObjenesisStd();
        assertTrue(objenesis.newInstance(RemotePlayer.class) instanceof PlayerSettings);
        assertTrue(objenesis.newInstance(PlayerModel.class) instanceof PlayerEntityModelAccessor);
        assertTrue(objenesis.newInstance(SkullModel.class) instanceof SkullModelAccessor);
    }

    @Test
    public void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        objenesis.newInstance(PlayerRenderer.class);
        objenesis.newInstance(BlockEntityWithoutLevelRenderer.class);
        objenesis.newInstance(CustomHeadLayer.class);
        objenesis.newInstance(RemotePlayer.class);
        objenesis.newInstance(PlayerModel.class);
        objenesis.newInstance(PlayerRenderer.class);
        objenesis.newInstance(SkullBlockEntity.class);
        objenesis.newInstance(SkullBlockRenderer.class);
        objenesis.newInstance(SkullModel.class);
    }
    
    @Test
    public void checkInjectedPlayerLayers() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        EntityRendererProvider.Context context = new Context(null, null, null, null, null, new DummyModelSet(), null);
        PlayerRenderer renderer = new PlayerRenderer(context, false);
        Field field =  LivingEntityRenderer.class.getDeclaredField("layers");
        field.setAccessible(true);
        List<RenderLayer<?,?>> layers = (List<RenderLayer<?, ?>>) field.get(renderer);
        System.out.println(layers);
        boolean foundBodyLayer = false;
        boolean foundHeadLayer = false;
        for(RenderLayer<?, ?> layer : layers) {
            if(layer instanceof BodyLayerFeatureRenderer) {
                foundBodyLayer = true;
            } else if(layer instanceof HeadLayerFeatureRenderer) {
                foundHeadLayer = true;
            }
        }
        assertTrue(foundBodyLayer);
        assertTrue(foundHeadLayer);
    }
    
    private class DummyModelSet extends EntityModelSet {

        @Override
        public ModelPart bakeLayer(ModelLayerLocation modelLayerLocation) {
            PartDefinition part = PlayerModel.createMesh(CubeDeformation.NONE, false).getRoot();
            part.getChild("head").addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("feather", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("box", CubeListBuilder.create(), PartPose.ZERO);
            return part.bake(0, 0);
        }
        
        
        
    }

}