package dev.tr7zw.skinlayers.versionless.util.wrapper;

import dev.tr7zw.skinlayers.versionless.util.Direction;

public interface ModelBuilder {

    ModelBuilder uv(int u, int v);

    ModelBuilder mirror(boolean bl);

    ModelBuilder addBox(float x, float y, float z, float pixelSize, Direction[] hide, Direction[][] corners);

    ModelBuilder addVanillaBox(float x, float y, float z, float width, float height, float depth);

    boolean isEmpty();

}