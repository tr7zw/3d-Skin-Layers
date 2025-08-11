package dev.tr7zw.skinlayers.render;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.versionless.render.CustomizableCube;
import dev.tr7zw.skinlayers.versionless.util.Direction;
import dev.tr7zw.skinlayers.versionless.util.wrapper.ModelBuilder;
import net.minecraft.client.model.geom.ModelPart.Cube;
//#if MC >= 11700
import net.minecraft.client.model.geom.builders.CubeListBuilder;
//#endif

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
            //#if MC >= 12000
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
                int ordinal = dir.ordinal();
                if (ordinal == 4) {
                    ordinal = 5;
                } else if (ordinal == 5) {
                    ordinal = 4;
                }
                cubeList.texOffs(uO, vO).mirror(mirror).addBox(x, y, z, pixelSize, pixelSize, pixelSize,
                        new HashSet<>(Arrays.asList(net.minecraft.core.Direction.values()[ordinal])));
                this.vanillaCubes.add(cubeList.getCubes().get(0).bake(textureWidth, textureHeight));
            }
            //#else
            //$$ this.cubes.add(new CustomizableCube(this.u, this.v, (mirror ? -1 : 1) * x, y, z, pixelSize, pixelSize,
            //$$        pixelSize, 0, 0, 0, this.mirror, textureWidth, textureHeight, hide, corners));
            //#endif
        }
        return this;
    }

    @Override
    public ModelBuilder addVanillaBox(float x, float y, float z, float width, float height, float depth) {
        if (mirror) {
            x = -1; // FIXME: Why
        }
        //#if MC <= 11605
        //$$         this.vanillaCubes.add(new Cube(u, v, x, y, z, width, height, depth, 0, 0, 0,
        //$$              this.mirror, textureWidth, textureHeight));
        //#else
        CubeListBuilder cubeList = CubeListBuilder.create();
        cubeList.texOffs(u, v).mirror(mirror).addBox(x, y, z, width, height, depth);
        this.vanillaCubes.add(cubeList.getCubes().get(0).bake(textureWidth, textureHeight));
        //#endif
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getCubes().isEmpty() && getVanillaCubes().isEmpty();
    }

}
