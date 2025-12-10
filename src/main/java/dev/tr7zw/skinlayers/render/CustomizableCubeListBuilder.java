package dev.tr7zw.skinlayers.render;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.api.*;
import dev.tr7zw.skinlayers.util.*;
import dev.tr7zw.skinlayers.versionless.render.CustomizableCube;
import dev.tr7zw.skinlayers.versionless.util.Direction;
import dev.tr7zw.skinlayers.versionless.util.wrapper.ModelBuilder;
import net.minecraft.client.model.geom.ModelPart.Cube;
//? if >= 1.17.0 {

import net.minecraft.client.model.geom.builders.CubeListBuilder;
//? }

public class CustomizableCubeListBuilder implements ModelBuilder {

    private final List<CustomizableCube> cubes = Lists.newArrayList();
    private final List<Cube> vanillaCubes = Lists.newArrayList();
    private int u;
    private int v;
    private boolean mirror;
    private int textureWidth = 64;
    private int textureHeight = 64;

    public static ModelBuilder create() {
        return new CustomizableCubeListBuilder();
    }

    public ModelBuilder textureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    @Override
    public ModelBuilder uv(int u, int v) {
        this.u = u;
        this.v = v;
        return this;
    }

    @Override
    public ModelBuilder mirror(boolean bl) {
        this.mirror = bl;
        return this;
    }

    public List<CustomizableCube> getCubes() {
        return cubes;
    }

    public List<Cube> getVanillaCubes() {
        return vanillaCubes;
    }

    @Override
    public ModelBuilder addBox(float x, float y, float z, float pixelSize, Direction[] hide, Direction[][] corners) {
        if (!SkinLayersModBase.config.irisCompatibilityMode) {
            this.cubes.add(new CustomizableCube(this.u, this.v, (mirror ? -1 : 1) * x, y, z, pixelSize, pixelSize,
                    pixelSize, 0, 0, 0, this.mirror, textureWidth, textureHeight, hide, corners));
        } else {
            //? if >= 1.20.0 {

            // Hacky workaround for Iris compatibility
            dir: for (Direction dir : Direction.values()) {
                for (Direction hideDir : hide) {
                    if (hideDir == dir) {
                        continue dir;
                    }
                }
                int uO = this.u;
                int vO = this.v;
                // Adjust UV coordinates based on the direction to use the correct pixel
                switch (dir) {
                case DOWN:
                    uO -= pixelSize;
                    break;
                case UP:
                    uO -= pixelSize * 2;
                    break;
                case NORTH:
                    uO -= pixelSize;
                    vO -= pixelSize;
                    break;
                case SOUTH:
                    uO -= pixelSize * 3;
                    vO -= pixelSize;
                    break;
                case WEST:

                    vO -= pixelSize;
                    break;
                case EAST:
                    uO -= pixelSize * 2;
                    vO -= pixelSize;
                    break;
                }
                CubeListBuilder cubeList = CubeListBuilder.create();
                var mcDir = switch (dir) {
                case UP:
                    yield net.minecraft.core.Direction.UP;
                case DOWN:
                    yield net.minecraft.core.Direction.DOWN;
                case NORTH:
                    yield net.minecraft.core.Direction.NORTH;
                case EAST:
                    yield SodiumWorkaround.applySodiumWorkaround() ? net.minecraft.core.Direction.WEST
                            : net.minecraft.core.Direction.EAST;
                case WEST:
                    yield SodiumWorkaround.applySodiumWorkaround() ? net.minecraft.core.Direction.EAST
                            : net.minecraft.core.Direction.WEST;
                case SOUTH:
                    yield net.minecraft.core.Direction.SOUTH;
                };
                cubeList.texOffs(uO, vO).mirror(mirror).addBox(x, y, z, pixelSize, pixelSize, pixelSize,
                        new HashSet<>(Arrays.asList(mcDir)));
                this.vanillaCubes.add(cubeList.getCubes().get(0).bake(textureWidth, textureHeight));
            }
            //? } else {
            /*
             this.cubes.add(new CustomizableCube(this.u, this.v, (mirror ? -1 : 1) * x, y, z, pixelSize, pixelSize,
                    pixelSize, 0, 0, 0, this.mirror, textureWidth, textureHeight, hide, corners));
            *///? }
        }
        return this;
    }

    @Override
    public ModelBuilder addVanillaBox(float x, float y, float z, float width, float height, float depth) {
        if (mirror) {
            x = -1; // FIXME: Why
        }
        this.vanillaCubes.add(SkinLayersAPI.getBoxBuilder().build(new BoxBuilder.BoxDefinition(u, v, mirror, x, y, z,
                width, height, depth, textureWidth, textureHeight)));
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getCubes().isEmpty() && getVanillaCubes().isEmpty();
    }

}
