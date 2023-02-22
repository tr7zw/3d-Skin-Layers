package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
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

}