//? if >= 1.18.0 {

package dev.tr7zw.tests;

import com.mojang.blaze3d.platform.*;
import net.minecraft.*;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
//? if >= 1.21.11 {

import net.minecraft.client.model.object.skull.*;
import net.minecraft.client.model.player.*;
//? } else {
/*
import net.minecraft.client.model.*;
*///? }
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.server.*;
import net.minecraft.world.level.block.entity.*;
import org.junit.jupiter.api.*;
import org.objenesis.*;

import static org.junit.jupiter.api.Assertions.*;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        //? if >= 1.21.6 {

        objenesis.newInstance(net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer.class);
        //? } else if >= 1.21.4 {
        /*
        objenesis.newInstance(net.minecraft.client.renderer.special.SkullSpecialRenderer.class);
        *///? } else {
        /*
        objenesis.newInstance(net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer.class);
        *///? }
        objenesis.newInstance(CustomHeadLayer.class);
        objenesis.newInstance(Deadmau5EarsLayer.class);
        objenesis.newInstance(ModelPart.class);
        objenesis.newInstance(NativeImage.class);
        objenesis.newInstance(PlayerModel.class);
        objenesis.newInstance(SkullBlockEntity.class);
        objenesis.newInstance(SkullBlockRenderer.class);
        objenesis.newInstance(SkullModel.class);
        //? if >= 1.21.9 {

        objenesis.newInstance(net.minecraft.client.renderer.entity.player.AvatarRenderer.class);
        //? } else {
        /*
        objenesis.newInstance(net.minecraft.client.renderer.entity.player.PlayerRenderer.class);
        *///? }
    }

}
//? }
