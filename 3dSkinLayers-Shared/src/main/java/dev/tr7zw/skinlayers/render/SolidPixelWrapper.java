package dev.tr7zw.skinlayers.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;

public class SolidPixelWrapper {

    private record UV(int u, int v) {}
    private record UVFlags(boolean u, boolean v) {}
    public record Dimensions(int width, int height, int depth) {}
    public record Position(float x, float y, float z) {}
    
    private static final float pixelSize = 1f;
    
    public static CustomizableModelPart wrapBoxOptimized(NativeImage natImage, PlayerModel<AbstractClientPlayer> model, int width,
            int height, int depth, int textureU, int textureV, boolean topPivot, float rotationOffset) {
        List<Cube> cubes = new ArrayList<>();
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        Dimensions dimensions = new Dimensions(width, height, depth);
        UV textureUV = new UV(textureU, textureV);
        try {
            // Front/back
            for (int u = 0; u < width; u++) {
                for (int v = 0; v < height; v++) {
                    // front
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == height - 1),
                            new UV(textureU + depth + u, textureV + depth + v), new Position(staticXOffset + u, staticYOffset + v, staticZOffset),
                            Direction.SOUTH, dimensions, new UV(u, v), textureUV);
                    // back
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == height - 1),
                            new UV(textureU + 2 * depth + width + u, textureV + depth + v), new Position(staticXOffset + width - 1 - u,
                            staticYOffset + v, staticZOffset + depth - 1), Direction.NORTH, dimensions, new UV(u, v), textureUV);
                }
            }
    
            // sides
            for (int u = 0; u < depth; u++) {
                for (int v = 0; v < height; v++) {
                    // left
                    addPixel(natImage, cubes, new UVFlags(u == depth - 1, v == height - 1),
                            new UV(textureU - 1 + depth - u, textureV + depth + v), new Position(staticXOffset, staticYOffset + v,
                            staticZOffset + u), Direction.EAST, dimensions, new UV(u, v), textureUV);
                    // right
                    addPixel(natImage, cubes, new UVFlags(u == depth - 1, v == height - 1),
                            new UV(textureU + depth + width + u, textureV + depth + v), new Position(staticXOffset + width - 1f,
                            staticYOffset + v, staticZOffset + u), Direction.WEST, dimensions, new UV(u, v), textureUV);
    
                }
            }
            // top/bottom
            for (int u = 0; u < width; u++) {
                for (int v = 0; v < depth; v++) {
                    // top
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == depth - 1),
                            new UV(textureU + depth + u, textureV + depth - 1 - v), new Position(staticXOffset + u, staticYOffset,
                            staticZOffset + v), Direction.UP, dimensions, new UV(u, v), textureUV); // Sides are flipped cause ?!?
                    // bottom
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == depth - 1),
                            new UV(textureU + depth + width + u, textureV + depth - 1 - v), new Position(staticXOffset + u,
                            staticYOffset + height - 1f, staticZOffset + v), Direction.DOWN, dimensions, new UV(u, v), textureUV); // Sides are flipped cause ?!?
                }
            }
        }catch(Exception ex) { // Some calculation went wrong and out of bounds/some other issue
            SkinLayersModBase.LOGGER.error("Error while creating 3d skin model. Please report on the Github/Discord.", ex);
            return new CustomizableModelPart(new ArrayList<Cube>(), new HashMap<>()); // empty model
        }

        return new CustomizableModelPart(cubes, new HashMap<>());
    }
    
    private static UV[] offsets = new UV[] { new UV(1, 0), new UV(-1, 0), new UV(0, 1), new UV(0, -1) };
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
    
    private static void addPixel(NativeImage natImage, List<Cube> cubes, UVFlags onMax, UV position, Position cubePosition, Direction dir, Dimensions dimensions, UV onFace, UV texturePosition) {
        if (natImage.getLuminanceOrAlpha(position.u, position.v) != 0) {
            Set<Direction> hide = new HashSet<>();
            for (int i = 0; i < offsets.length; i++) {
                if (checkNeighbor(natImage, position, offsets[i], dimensions, onFace, dir, texturePosition)) {
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
            if(onFace.u == 0 && !checkNeighbor(natImage, position, new UV(-1,0), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if(onFace.v == 0 && !checkNeighbor(natImage, position, new UV(0,-1), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if(onMax.u && !checkNeighbor(natImage, position, new UV(1,0), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if(onMax.v && !checkNeighbor(natImage, position, new UV(0,1), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if(removeBackside)
                hide.add(dir);
            cubes.addAll(CustomizableCubeListBuilder.create().texOffs(position.u - 2, position.v - 1)
                    .addBox(cubePosition.x, cubePosition.y, cubePosition.z, pixelSize, hide.toArray(new Direction[hide.size()])).getCubes());
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
    private static boolean checkNeighbor(NativeImage natImage, UV basePixel, UV offset, Dimensions dimensions, UV onFace, Direction dir, UV texturePosition) {
        UV pixel = getOffsetPosition(basePixel, offset, dimensions, onFace, dir, texturePosition);
        return (natImage.getLuminanceOrAlpha(pixel.u, pixel.v) != 0);
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
    private static UV getOffsetPosition(UV basePixel, UV offset, Dimensions dimensions, UV onFace, Direction dir, UV texturePosition) {
        UV result = null;
        // up offset
        if(onFace.v == 0 && offset.v == -1) { // this will go out of the current face to the top
            if(dir == Direction.EAST) { // left side to top
                result = new UV(texturePosition.u + dimensions.depth, texturePosition.v + dimensions.depth - onFace.u - 1);
            }
            if(dir == Direction.WEST) { // right side to top
                result = new UV(texturePosition.u + dimensions.depth + dimensions.width - 1, texturePosition.v + dimensions.depth - onFace.u - 1);
            }
            if(dir == Direction.NORTH) { // back to top
                result = new UV(texturePosition.u + dimensions.depth + dimensions.width - 1 - onFace.u, texturePosition.v);
            }
        }
        if(dir == Direction.UP) { // top to sides
            if(onFace.v == dimensions.height - 1 && offset.v == -1) { // top to back
                result = new UV(texturePosition.u + 2 * dimensions.depth + dimensions.width + dimensions.width - 1 - onFace.u, texturePosition.v + dimensions.depth);
            }
            if(onFace.u == 0 && offset.v == -1) { // top to left
                result = new UV(texturePosition.u + dimensions.depth - 1 - onFace.v, texturePosition.v + dimensions.depth);
            }
            if(onFace.u == dimensions.width - 1 && offset.u == 1) { // top to right
                result = new UV(texturePosition.u + dimensions.depth + dimensions.width + onFace.v, texturePosition.v + dimensions.depth);
            }
            //top to front is the default behavior
        }
        if(dir == Direction.NORTH) { // back to left side
            if(onFace.u == dimensions.width - 1 && offset.u == 1) { // top to back
                result = new UV(texturePosition.u, texturePosition.v + dimensions.depth);
            }
        }
        if(dir == Direction.EAST) { // left to back
            if(onFace.u == 0 && offset.u == -1) { // top to back
                result = new UV(texturePosition.u + dimensions.depth*2 + dimensions.width*2 -1, basePixel.v);
            }
        }
        if(result == null) { // Default handeling
            result = new UV(basePixel.u + offset.u, basePixel.v + offset.v);
        }
        return result;
    }

}
