package dev.tr7zw.skinlayers.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.core.Direction;

public class SolidPixelWrapper {

    public record UV(int u, int v) {
    }

    private record UVFlags(boolean u, boolean v) {
    }

    public record Dimensions(int width, int height, int depth) {
    }

    public record Position(float x, float y, float z) {
    }

    private static final float pixelSize = 1f;

    public static CustomizableModelPart wrapBoxOptimized(NativeImage natImage,
            int width, int height, int depth, int textureU, int textureV, boolean topPivot, float rotationOffset) {
        List<Cube> cubes = new ArrayList<>();
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        Dimensions dimensions = new Dimensions(width, height, depth);
        UV textureUV = new UV(textureU, textureV);
        //StaticData staticData = new StaticData(natImage, cubes, textureUV, dimensions);
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
                            new UV(textureU - 1 + depth - u, textureV + depth + v),
                            new Position(staticXOffset, staticYOffset + v, staticZOffset + u), Direction.EAST,
                            dimensions, new UV(u, v), textureUV);
                    // right
                    addPixel(natImage, cubes, new UVFlags(u == depth - 1, v == height - 1),
                            new UV(textureU + depth + width + u, textureV + depth + v),
                            new Position(staticXOffset + width - 1f, staticYOffset + v, staticZOffset + u),
                            Direction.WEST, dimensions, new UV(u, v), textureUV);

                }
            }
            // top/bottom
            for (int u = 0; u < width; u++) {
                for (int v = 0; v < depth; v++) {
                    // top
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == depth - 1),
                            new UV(textureU + depth + u, textureV + depth - 1 - v),
                            new Position(staticXOffset + u, staticYOffset, staticZOffset + v), Direction.UP, dimensions,
                            new UV(u, v), textureUV); // Sides are flipped cause ?!?
                    // bottom
                    addPixel(natImage, cubes, new UVFlags(u == width - 1, v == depth - 1),
                            new UV(textureU + depth + width + u, textureV + depth - 1 - v),
                            new Position(staticXOffset + u, staticYOffset + height - 1f, staticZOffset + v),
                            Direction.DOWN, dimensions, new UV(u, v), textureUV); // Sides are flipped cause ?!?
                }
            }
        } catch (Exception ex) { // Some calculation went wrong and out of bounds/some other issue
            SkinLayersModBase.LOGGER.error("Error while creating 3d skin model. Please report on the Github/Discord.",
                    ex);
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

    private static void addPixel(NativeImage natImage, List<Cube> cubes, UVFlags onMax, UV position,
            Position cubePosition, Direction dir, Dimensions dimensions, UV onFace, UV texturePosition) {
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
            if (onFace.u == 0
                    && !checkNeighbor(natImage, position, new UV(-1, 0), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if (onFace.v == 0
                    && !checkNeighbor(natImage, position, new UV(0, -1), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if (onMax.u && !checkNeighbor(natImage, position, new UV(1, 0), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if (onMax.v && !checkNeighbor(natImage, position, new UV(0, 1), dimensions, onFace, dir, texturePosition)) {
                removeBackside = false;
            }
            if (removeBackside)
                hide.add(dir);
            cubes.addAll(CustomizableCubeListBuilder.create().texOffs(position.u - 2, position.v - 1)
                    .addBox(cubePosition.x, cubePosition.y, cubePosition.z, pixelSize,
                            hide.toArray(new Direction[hide.size()]))
                    .getCubes());
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
    private static boolean checkNeighbor(NativeImage natImage, UV basePixel, UV offset, Dimensions dimensions,
            UV onFace, Direction dir, UV texturePosition) {
        UV pixel = getOffsetPosition(basePixel, offset, dimensions, texturePosition, dir);
        return (natImage.getLuminanceOrAlpha(pixel.u, pixel.v) != 0);
    }

    private enum Change {
        NONE, TOP, BOTTOM, LEFT, RIGHT;
    }

    private static Map<Direction, Map<Change, BiFunction<UV, Dimensions, UV>>> connections = new HashMap<>() {

        private static final long serialVersionUID = 1L;

        {
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> top = new HashMap<>();
                top.put(Change.TOP,
                        (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() + dims.width() - uv.u() - 1,
                                dims.depth()));
                top.put(Change.BOTTOM, (uv, dims) -> null);
                top.put(Change.LEFT, (uv, dims) -> new UV(uv.v(), dims.depth()));
                top.put(Change.RIGHT,
                        (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() - uv.v() - 1, dims.depth()));
                put(Direction.UP, top);
            }
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> down = new HashMap<>();
                down.put(Change.TOP, (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() + uv.u(),
                        dims.depth() + dims.height() - 1));
                down.put(Change.BOTTOM, (uv, dims) -> new UV(dims.depth() + uv.u(), dims.depth() + dims.height() - 1));
                down.put(Change.LEFT, (uv, dims) -> new UV(uv.v(), dims.depth() + dims.height() - 1));
                down.put(Change.RIGHT, (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() - uv.v() - 1,
                        dims.depth() + dims.height() - 1));
                put(Direction.DOWN, down);
            }
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> east = new HashMap<>();
                east.put(Change.TOP, (uv, dims) -> new UV(dims.depth(),uv.u()));
                east.put(Change.BOTTOM, (uv, dims) -> new UV(dims.depth() + dims.width(), uv.u()));
                east.put(Change.LEFT, (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() + dims.width() - 1, dims.depth() + uv.v()));
                east.put(Change.RIGHT, (uv, dims) -> null);
                put(Direction.EAST, east);
            }
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> south = new HashMap<>();
                south.put(Change.TOP, (uv, dims) -> null);
                south.put(Change.BOTTOM, (uv, dims) -> new UV(dims.depth() + dims.width() + uv.u(), dims.depth()-1));
                south.put(Change.LEFT, (uv, dims) -> null);
                south.put(Change.RIGHT, (uv, dims) -> null);
                put(Direction.SOUTH, south);
            }
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> west = new HashMap<>();
                west.put(Change.TOP, (uv, dims) -> new UV(dims.depth() + dims.width() - 1, dims.depth() - uv.u() - 1));
                west.put(Change.BOTTOM, (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() - 1, dims.depth() - uv.u() - 1));
                west.put(Change.LEFT, (uv, dims) -> null);
                west.put(Change.RIGHT, (uv, dims) -> null);
                put(Direction.WEST, west);
            }
            {
                Map<Change, BiFunction<UV, Dimensions, UV>> north = new HashMap<>();
                north.put(Change.TOP, (uv, dims) -> new UV(dims.depth() + dims.width() - uv.u() - 1, 0));
                north.put(Change.BOTTOM, (uv, dims) -> new UV(dims.depth() + dims.width() + dims.depth() - uv.u() - 1, 0));
                north.put(Change.LEFT, (uv, dims) -> null);
                north.put(Change.RIGHT, (uv, dims) -> new UV(0, dims.depth() + uv.v()));
                put(Direction.NORTH, north);
            }
        }
    };

    public static UV getOffsetPosition(UV pos, UV offset, Dimensions dims, UV texturePos, Direction dir) {
        UV cube = new UV(pos.u() - texturePos.u(), pos.v() - texturePos.v()); // Position relative to the cube origin
        UV onFace = getFacePos(cube, dims, dir); // Position relative on the face
        Change change = Change.NONE; // Default at no border
        if (onFace.v() == 0 && offset.v() == -1) { // at the top of the face and want to go out of the top
            change = Change.TOP;
        }
        if (onFace.v() == getMaxV(dims, dir) && offset.v() == 1) { // at the bottom of the face and want to go out down
            change = Change.BOTTOM;
        }
        if (onFace.u() == 0 && offset.u() == -1) { // at the left of the face and want to go out left
            change = Change.LEFT;
        }
        if (onFace.u() == getMaxU(dims, dir) && offset.u() == 1) { // at the right of the face and want to go out right
            change = Change.RIGHT;
        }
        if (change == Change.NONE) { // staying inside the same face
            return new UV(pos.u() + offset.u(), pos.v() + offset.v());
        }
        // we are leaving the face, calculate the new position
        UV cubePos = connections.get(dir).get(change).apply(onFace, dims);
        if (cubePos == null) { // use default behavior
            return new UV(pos.u() + offset.u(), pos.v() + offset.v());
        }
        return new UV(texturePos.u() + cubePos.u(), texturePos.v() + cubePos.v());
    }

    private static int getMaxU(Dimensions dims, Direction dir) {
        if (dir == Direction.UP || dir == Direction.DOWN || dir == Direction.SOUTH || dir == Direction.NORTH)
            return dims.width() - 1;
        return dims.depth() - 1;
    }

    private static int getMaxV(Dimensions dims, Direction dir) {
        if (dir == Direction.UP || dir == Direction.DOWN)
            return dims.depth() - 1;
        return dims.height() - 1;
    }

    private static UV getFacePos(UV cube, Dimensions dims, Direction dir) {
        int onFaceU = -1;
        int onFaceV = -1;
        switch (dir) {
        case UP:
            onFaceU = cube.u() - dims.depth();
            onFaceV = cube.v();
            break;
        case DOWN:
            onFaceU = cube.u() - dims.depth() - dims.width();
            onFaceV = cube.v();
            break;
        case EAST:
            onFaceU = cube.u();
            onFaceV = cube.v() - dims.depth();
            break;
        case SOUTH:
            onFaceU = cube.u() - dims.depth();
            onFaceV = cube.v() - dims.depth();
            break;
        case WEST:
            onFaceU = cube.u() - dims.depth() - dims.width();
            onFaceV = cube.v() - dims.depth();
            break;
        case NORTH:
            onFaceU = cube.u() - dims.depth() - dims.width() - dims.depth();
            onFaceV = cube.v() - dims.depth();
            break;
        }
        return new UV(onFaceU, onFaceV);
    }

}
