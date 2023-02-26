package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.skinlayers.Config;
import dev.tr7zw.skinlayers.SkinLayersMod;
import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.locale.Language;
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
        PlayerRenderer renderer = TestUtil.getPlayerRenderer();
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
    
    @Test
    public void langTests() throws Throwable {
//        Language.getInstance().loadFromJson(new FileInputStream("../3d-Skin-Layers/Shared/src/main/resources/assets/3dskinlayers/lang/en_us.json"), null);
        Language lang = TestUtil.loadDefault("/assets/3dskinlayers/lang/en_us.json");
        SkinLayersModBase.instance = new ObjenesisStd().newInstance(SkinLayersMod.class);
        SkinLayersModBase.config = new Config();
        CustomConfigScreen screen = (CustomConfigScreen) SkinLayersModBase.instance.createConfigScreen(null);
        List<OptionInstance<?>> options = TestUtil.bootStrapCustomConfigScreen(screen);
        assertNotEquals(screen.getTitle().getString(), lang.getOrDefault(screen.getTitle().getString()));
        for(OptionInstance<?> option : options) {
            Set<String> keys = TestUtil.getKeys(option, false);
            for(String key : keys) {
                System.out.println(key + " " + lang.getOrDefault(key));
                assertNotEquals(key, lang.getOrDefault(key));
            }
        }
    }

}