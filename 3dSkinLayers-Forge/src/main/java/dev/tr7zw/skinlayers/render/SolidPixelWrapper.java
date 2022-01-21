package dev.tr7zw.skinlayers.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.tr7zw.skinlayers.Direction;
import dev.tr7zw.skinlayers.opengl.NativeImage;

public class SolidPixelWrapper {

    public static CustomizableModelPart wrapBox(NativeImage natImage, int width,
            int height, int depth, int textureU, int textureV, boolean topPivot, float rotationOffset) {
        List<CustomizableCube> cubes = new ArrayList<>();
        float pixelSize = 1f;
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        // Front/back
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                // front
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == height - 1,
                        textureU + depth + u, textureV + depth + v, staticXOffset + u, staticYOffset + v, staticZOffset,
                        Direction.SOUTH);
                // back
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == height - 1,
                        textureU + 2 * depth + width + u, textureV + depth + v, staticXOffset + width - 1 - u,
                        staticYOffset + v, staticZOffset + depth - 1, Direction.NORTH);
            }
        }

        // sides
        for (int u = 0; u < depth; u++) {
            for (int v = 0; v < height; v++) {
                // left
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == depth - 1 || v == height - 1,
                        textureU - 1 + depth - u, textureV + depth + v, staticXOffset, staticYOffset + v,
                        staticZOffset + u, Direction.EAST);
                // right
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == depth - 1 || v == height - 1,
                        textureU + depth + width + u, textureV + depth + v, staticXOffset + width - 1f,
                        staticYOffset + v, staticZOffset + u, Direction.WEST);

            }
        }
        // top/bottom
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < depth; v++) {
                // top
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == depth - 1,
                        textureU + depth + u, textureV + depth - 1 - v, staticXOffset + u, staticYOffset,
                        staticZOffset + v, Direction.UP); // Sides are flipped cause ?!?
                // bottom
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == depth - 1,
                        textureU + depth + width + u, textureV + depth - 1 - v, staticXOffset + u,
                        staticYOffset + height - 1f, staticZOffset + v, Direction.DOWN); // Sides are flipped cause ?!?
            }
        }

        return new CustomizableModelPart(cubes);
    }

    private static int[][] offsets = new int[][] { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
    private static Direction[] hiddenDirN = new Direction[] { Direction.WEST, Direction.EAST, Direction.UP,
            Direction.DOWN };
    private static Direction[] hiddenDirS = new Direction[] { Direction.EAST, Direction.WEST, Direction.UP,
            Direction.DOWN };
    private static Direction[] hiddenDirW = new Direction[] { Direction.SOUTH, Direction.NORTH, Direction.UP,
            Direction.DOWN };
    private static Direction[] hiddenDirE = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.UP,
            Direction.DOWN };
    private static Direction[] hiddenDirUD = new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH,
            Direction.SOUTH };

    private static void addPixel(NativeImage natImage, List<CustomizableCube> cubes, float pixelSize, boolean onBorder, int u,
            int v, float x, float y, float z, Direction dir) {
        if (natImage.getLuminanceOrAlpha(u, v) != 0) {
            Set<Direction> hide = new HashSet<>();
            if (!onBorder) {
                for (int i = 0; i < offsets.length; i++) {
                    int tU = u + offsets[i][1];
                    int tV = v + offsets[i][0];
                    if (tU >= 0 && tU < 64 && tV >= 0 && tV < 64 && natImage.getLuminanceOrAlpha(tU, tV) != 0) {
                        if (dir == Direction.NORTH)
                            hide.add(hiddenDirN[i]);
                        if (dir == Direction.SOUTH)
                            hide.add(hiddenDirS[i]);
                        if (dir == Direction.EAST)
                            hide.add(hiddenDirE[i]);
                        if (dir == Direction.WEST)
                            hide.add(hiddenDirW[i]);
                        if (dir == Direction.UP || dir == Direction.DOWN)
                            hide.add(hiddenDirUD[i]);
                    }
                }
                hide.add(dir);
            }
            cubes.addAll(CustomizableCubeListBuilder.create().texOffs(u - 2, v - 1)
                    .addBox(x, y, z, pixelSize, hide.toArray(new Direction[hide.size()])).getCubes());
            // wrapper.setTextureOffset(u-2, v-1);
            // wrapper.addCustomCuboid(x, y, z, pixelSize, pixelSize, pixelSize,
            // hide.toArray(new Direction[hide.size()]));
        }
    }

}
