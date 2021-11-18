package dev.tr7zw.skinlayers.render;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.core.Direction;

public class CustomizableCubeListBuilder {

    private final List<Cube> cubes = Lists.newArrayList();
    private int u;
    private int v;
    private boolean mirror;

    public static CustomizableCubeListBuilder create() {
        return new CustomizableCubeListBuilder();
    }

    public CustomizableCubeListBuilder uv(int u, int v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public CustomizableCubeListBuilder mirror(boolean bl) {
        this.mirror = bl;
        return this;
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public CustomizableCubeListBuilder addBox(float x, float y, float z, float pixelSize, Direction[] hide, Direction[][] corners) {
        int textureSize = 64;
        this.cubes.add(new CustomizableCube(this.u, this.v, x, y, z, pixelSize, pixelSize, pixelSize, 0, 0, 0,
                this.mirror, textureSize, textureSize, hide, corners));
        return this;
    }

}
