package dev.tr7zw.skinlayers.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;

public class SolidPixelWrapper {

    public static CustomizableModelPart wrapBoxOptimized(NativeImage natImage, PlayerModel<AbstractClientPlayer> model, int width,
            int height, int depth, int textureU, int textureV, boolean topPivot, float rotationOffset) {
        List<Cube> cubes = new ArrayList<>();
        float pixelSize = 1f;
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        // Front/back
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                // front
                addPixel(natImage, cubes, pixelSize, u == width - 1, v == height - 1,
                        textureU + depth + u, textureV + depth + v, staticXOffset + u, staticYOffset + v, staticZOffset,
                        Direction.SOUTH, width, height, depth, u, v, textureU, textureV);
                // back
                addPixel(natImage, cubes, pixelSize, u == width - 1, v == height - 1,
                        textureU + 2 * depth + width + u, textureV + depth + v, staticXOffset + width - 1 - u,
                        staticYOffset + v, staticZOffset + depth - 1, Direction.NORTH, width, height, depth, u, v, textureU, textureV);
            }
        }

        // sides
        for (int u = 0; u < depth; u++) {
            for (int v = 0; v < height; v++) {
                // left
                addPixel(natImage, cubes, pixelSize, u == depth - 1, v == height - 1,
                        textureU - 1 + depth - u, textureV + depth + v, staticXOffset, staticYOffset + v,
                        staticZOffset + u, Direction.EAST, width, height, depth, u, v, textureU, textureV);
                // right
                addPixel(natImage, cubes, pixelSize, u == depth - 1, v == height - 1,
                        textureU + depth + width + u, textureV + depth + v, staticXOffset + width - 1f,
                        staticYOffset + v, staticZOffset + u, Direction.WEST, width, height, depth, u, v, textureU, textureV);

            }
        }
        // top/bottom
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < depth; v++) {
                // top
                addPixel(natImage, cubes, pixelSize, u == width - 1, v == depth - 1,
                        textureU + depth + u, textureV + depth - 1 - v, staticXOffset + u, staticYOffset,
                        staticZOffset + v, Direction.UP, width, height, depth, u, v, textureU, textureV); // Sides are flipped cause ?!?
                // bottom
                addPixel(natImage, cubes, pixelSize, u == width - 1, v == depth - 1,
                        textureU + depth + width + u, textureV + depth - 1 - v, staticXOffset + u,
                        staticYOffset + height - 1f, staticZOffset + v, Direction.DOWN, width, height, depth, u, v, textureU, textureV); // Sides are flipped cause ?!?
            }
        }

        return new CustomizableModelPart(cubes, new HashMap<>());
    }

    private static int[][] offsets = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
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
    //new Direction[] { Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH };
    
    private static void addPixel(NativeImage natImage, List<Cube> cubes, float pixelSize, boolean onUMax, boolean onVMax, int u,
            int v, float x, float y, float z, Direction dir, int width, int height, int depth, int onFaceU, int onFaceV, int textureU, int textureV) {
        if (natImage.getLuminanceOrAlpha(u, v) != 0) {
            Set<Direction> hide = new HashSet<>();
            for (int i = 0; i < offsets.length; i++) {
                if (checkNeighbor(natImage, u, v, offsets[i], u, v, width, height, depth, onFaceU, onFaceV, dir, textureU, textureV)) {
                    if (dir == Direction.NORTH)
                        hide.add(hiddenDirN[i]);
                    if (dir == Direction.SOUTH)
                        hide.add(hiddenDirS[i]);
                    if (dir == Direction.EAST)
                        hide.add(hiddenDirE[i]);
                    if (dir == Direction.WEST)
                        hide.add(hiddenDirW[i]);
                    if (dir == Direction.UP || dir == Direction.DOWN) {
                        hide.add(hiddenDirUD[i]);
                    }
                }
            }
            boolean removeBackside = true;
            if(textureU == 0 && !checkNeighbor(natImage, u, v, new int[]{-1,0}, u, v, width, height, depth, onFaceU, onFaceV, dir, textureU, textureV)) {
                removeBackside = false;
            }
            if(textureV == 0 && !checkNeighbor(natImage, u, v, new int[]{0,-1}, u, v, width, height, depth, onFaceU, onFaceV, dir, textureU, textureV)) {
                removeBackside = false;
            }
            if(onUMax && !checkNeighbor(natImage, u, v, new int[]{1,0}, u, v, width, height, depth, onFaceU, onFaceV, dir, textureU, textureV)) {
                removeBackside = false;
            }
            if(onVMax && !checkNeighbor(natImage, u, v, new int[]{0,1}, u, v, width, height, depth, onFaceU, onFaceV, dir, textureU, textureV)) {
                removeBackside = false;
            }
            if(removeBackside)
                hide.add(dir);
            cubes.addAll(CustomizableCubeListBuilder.create().texOffs(u - 2, v - 1)
                    .addBox(x, y, z, pixelSize, hide.toArray(new Direction[hide.size()])).getCubes());
        }
    }
    
    /**
     * @param natImage
     * @param u
     * @param v
     * @param offset
     * @param uOffset
     * @param vOffset
     * @param width
     * @param height
     * @param depth
     * @param onFaceU
     * @param onFaceV
     * @param dir
     * @param textureU
     * @param textureV
     * @return true when the pixel is not empty
     */
    private static boolean checkNeighbor(NativeImage natImage, int u, int v,int[] offset, int uOffset, int vOffset, int width, int height, int depth, int onFaceU, int onFaceV, Direction dir, int textureU, int textureV) {
        int[] pixel = getPixel(u, v, offset[0], offset[1], width, height, depth, onFaceU, onFaceV, dir, textureU, textureV);
        int tU = pixel[0];
        int tV = pixel[1];
        return (tU >= 0 && tU < 64 && tV >= 0 && tV < 64 && natImage.getLuminanceOrAlpha(tU, tV) != 0);
    }
    
    /**
     * Implements wrapped navigation on the model texture
     * 
     * @param u
     * @param v
     * @param uOffset one of the two offsets must be 0, and offset can only be 1 or -1
     * @param vOffset one of the two offsets must be 0, and offset can only be 1 or -1
     * @param width
     * @param height
     * @param depth
     * @param onFaceU
     * @param onFaceV
     * @return
     */
    private static int[] getPixel(int u, int v, int uOffset, int vOffset, int width, int height, int depth, int onFaceU, int onFaceV, Direction dir, int textureU, int textureV) {
        int[] val = new int[2];
        // default handeling
        val[0] = u + uOffset;
        val[1] = v + vOffset;
        // up offset
        if(onFaceV == 0 && vOffset == -1) { // this will go out of the current face to the top
            if(dir == Direction.EAST) { // left side to top
                val[0] = textureU + depth;
                val[1] = textureV + depth - onFaceU - 1;
            }
            if(dir == Direction.WEST) { // right side to top
                val[0] = textureU + depth + width - 1;
                val[1] = textureV + depth - onFaceU - 1;
            }
            if(dir == Direction.NORTH) { // back to top
                val[0] = textureU + depth + width - 1 - onFaceU;
                val[1] = textureV;
            }
        }
        if(dir == Direction.UP) { // top to sides
            if(onFaceV == height - 1 && vOffset == 1) { // top to back
                val[0] = textureU + 2 * depth + width + width - 1 - onFaceU;
                val[1] = textureV + depth;
            }
            if(onFaceU == 0 && vOffset == -1) { // top to left
                val[0] = textureU + depth - 1 - onFaceV;
                val[1] = textureV + depth;
            }
            if(onFaceU == width - 1 && uOffset == 1) { // top to right
                val[0] = textureU + depth + width + onFaceV;
                val[1] = textureV + depth;
                //System.out.println(u + " " + v + " -> " + val[0] + " " + val[1]);
            }
            //top to front is the default behavior
        }
        if(dir == Direction.NORTH) { // back to left side
            if(onFaceU == width - 1 && uOffset == 1) { // top to back
                val[0] = textureU;
                val[1] = textureV + depth;
            }
        }
        return val;
    }

}
