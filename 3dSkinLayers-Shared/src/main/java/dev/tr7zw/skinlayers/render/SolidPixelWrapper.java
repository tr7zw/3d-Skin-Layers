package dev.tr7zw.skinlayers.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.core.Direction;

public class SolidPixelWrapper {

    public record UV(int u, int v) {
    }

    public record Dimensions(int width, int height, int depth) {
    }

    public record Position(float x, float y, float z) {
    }

    public record VoxelPosition(int x, int y, int z) {
    }

    private static final float pixelSize = 1f;

    /**
     * Side to Direction:
     * Top   - DOWN
     * Down  - UP
     * Front - NORTH
     * Back  - SOUTH
     * Right - WEST
     * Left  - EAST
     **/
    public static CustomizableModelPart wrapBoxOptimized(NativeImage natImage,
            int width, int height, int depth, int textureU, int textureV, boolean topPivot, float rotationOffset) {
        List<Cube> cubes = new ArrayList<>();
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        Position staticOffset = new Position(staticXOffset, staticYOffset, staticZOffset);
        Dimensions dimensions = new Dimensions(width, height, depth);
        UV textureUV = new UV(textureU, textureV);
        //StaticData staticData = new StaticData(natImage, cubes, textureUV, dimensions);
        try {
            for(Direction face : Direction.values()) {
                UV sizeUV = getSizeUV(dimensions, face);
                for(int u = 0; u < sizeUV.u; u++) {
                    for(int v = 0; v < sizeUV.v; v++) {
                        addPixel(natImage, cubes, staticOffset, face, dimensions, new UV(u, v), textureUV, sizeUV);
                    }
                }
            }
        } catch (Exception ex) { // Some calculation went wrong and out of bounds/some other issue
            SkinLayersModBase.LOGGER.error("Error while creating 3d skin model. Please report on the Github/Discord.",
                    ex);
            return new CustomizableModelPart(new ArrayList<Cube>(), new HashMap<>()); // empty model
        }

        return new CustomizableModelPart(cubes, new HashMap<>());
    }

    private static UV getSizeUV(Dimensions dimensions, Direction face) {
        return switch(face) {
            case DOWN , UP    -> new UV(dimensions.width, dimensions.depth );
            case NORTH, SOUTH -> new UV(dimensions.width, dimensions.height);
            case WEST , EAST  -> new UV(dimensions.depth, dimensions.height);
        };
    }

    private static UV getOnTextureUV(UV textureUV, UV onFaceUV, Dimensions dimensions, Direction face) {
        return switch(face) {
            case DOWN  -> new UV(textureUV.u + dimensions.depth + onFaceUV.u                                      , textureUV.v + onFaceUV.v                   );
            case UP    -> new UV(textureUV.u + dimensions.width + dimensions.depth + onFaceUV.u                   , textureUV.v + onFaceUV.v                   );
            case NORTH -> new UV(textureUV.u + dimensions.depth + onFaceUV.u                                      , textureUV.v + dimensions.depth + onFaceUV.v);
            case SOUTH -> new UV(textureUV.u + dimensions.depth + dimensions.width + dimensions.depth + onFaceUV.u, textureUV.v + dimensions.depth + onFaceUV.v);
            case WEST  -> new UV(textureUV.u + onFaceUV.u                                                         , textureUV.v + dimensions.depth + onFaceUV.v);
            case EAST  -> new UV(textureUV.u + dimensions.depth + dimensions.width + onFaceUV.u                   , textureUV.v + dimensions.depth + onFaceUV.v);
        };
    }

    private static VoxelPosition UVtoXYZ(UV onFaceUV, Dimensions dimensions, Direction face) {
        return switch(face) {
            case DOWN  -> new VoxelPosition(onFaceUV.u, 0                    , dimensions.depth - 1 - onFaceUV.v);
            case UP    -> new VoxelPosition(onFaceUV.u, dimensions.height - 1, dimensions.depth - 1 - onFaceUV.v);
            case NORTH -> new VoxelPosition(onFaceUV.u + 0                   , onFaceUV.v, 0                   );
            case SOUTH -> new VoxelPosition(dimensions.width - 1 - onFaceUV.u, onFaceUV.v, dimensions.depth - 1);
            case WEST  -> new VoxelPosition(0                   , onFaceUV.v, dimensions.depth - 1 - onFaceUV.u);
            case EAST  -> new VoxelPosition(dimensions.width - 1, onFaceUV.v, onFaceUV.u + 0                   );
        };
    }

    private static UV XYZtoUV(VoxelPosition voxelPosition, Dimensions dimensions, Direction face) {
        return switch(face) {
            case DOWN, UP -> new UV(voxelPosition.x, dimensions.depth - 1 - voxelPosition.z);
            case NORTH    -> new UV(voxelPosition.x + 0                   , voxelPosition.y);
            case SOUTH    -> new UV(dimensions.width - 1 - voxelPosition.x, voxelPosition.y);
            case WEST     -> new UV(dimensions.depth - 1 - voxelPosition.z, voxelPosition.y);
            case EAST     -> new UV(voxelPosition.z + 0                   , voxelPosition.y);
        };
    }

    private static void addPixel(NativeImage natImage, List<Cube> cubes,
                Position staticOffset, Direction face, Dimensions dimensions, UV onFaceUV, UV textureUV, UV sizeUV) {
        UV onTextureUV = getOnTextureUV(textureUV, onFaceUV, dimensions, face);
        if(!isPresent(natImage, onTextureUV)) return;

        VoxelPosition voxelPosition = UVtoXYZ(onFaceUV, dimensions, face);
        Position position = new Position(staticOffset.x + voxelPosition.x, staticOffset.y + voxelPosition.y, staticOffset.z + voxelPosition.z);
        boolean solidPixel = isSolid(natImage, onTextureUV);

        Set<Direction> hide = new HashSet<>();
        Set<Direction[]> corners = new HashSet<>();

        boolean isOnBorder = false;
        boolean backsideOverlaps = false;
        for (Direction neighbourFace : Direction.values()) {
            if(neighbourFace.getAxis() == face.getAxis()) continue;

            VoxelPosition neighbourVoxelPosition = new VoxelPosition(voxelPosition.x + neighbourFace.getStepX(), voxelPosition.y + neighbourFace.getStepY(), voxelPosition.z + neighbourFace.getStepZ());
            UV neighbourOnFaceUV = XYZtoUV(neighbourVoxelPosition, dimensions, face);
            if(isOnFace(neighbourOnFaceUV, sizeUV)) {
                if(isPresent(natImage, getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, face))) {
                    if(!(solidPixel && !isSolid(natImage, getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, face)))) {
                        hide.add(neighbourFace);
                    }
                } else {
                    VoxelPosition farNeighbourVoxelPosition = new VoxelPosition(neighbourVoxelPosition.x + neighbourFace.getStepX(), neighbourVoxelPosition.y + neighbourFace.getStepY(), neighbourVoxelPosition.z + neighbourFace.getStepZ());
                    UV farNeighbourOnFaceUV = XYZtoUV(farNeighbourVoxelPosition, dimensions, face);
                    if(!isOnFace(farNeighbourOnFaceUV, sizeUV)) {
                        farNeighbourOnFaceUV = XYZtoUV(farNeighbourVoxelPosition, dimensions, neighbourFace);
                        if(isPresent(natImage, getOnTextureUV(textureUV, farNeighbourOnFaceUV, dimensions, neighbourFace))) {
                            if(!(solidPixel && !isSolid(natImage, getOnTextureUV(textureUV, farNeighbourOnFaceUV, dimensions, neighbourFace)))) {
                                hide.add(neighbourFace);
                            }
                        }
                    }
                }
            } else {
                isOnBorder = true;
                neighbourOnFaceUV = XYZtoUV(voxelPosition, dimensions, neighbourFace);
                if(isPresent(natImage, getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, neighbourFace))) {
                    backsideOverlaps = true;
                    hide.add(neighbourFace);
                    corners.add(new Direction[]{ face.getOpposite(), neighbourFace });
                } else {
                    UV downNeighbourOnFaceUV = XYZtoUV(new VoxelPosition(voxelPosition.x - face.getStepX(), voxelPosition.y - face.getStepY(), voxelPosition.z - face.getStepZ()), dimensions, neighbourFace);
                    if(isPresent(natImage, getOnTextureUV(textureUV, downNeighbourOnFaceUV, dimensions, neighbourFace))) {
                        backsideOverlaps = true;
                    }
                }
            }
        }

        if(!isOnBorder || backsideOverlaps) {
            hide.add(face.getOpposite());
        }

        cubes.addAll(CustomizableCubeListBuilder.create().uv(onTextureUV.u, onTextureUV.v)
                .addBox(position.x, position.y, position.z, pixelSize,
                        hide.toArray(Direction[]::new), corners.toArray(Direction[][]::new))
                .getCubes());
    }

    private static boolean isPresent(NativeImage natImage, UV onTextureUV) {
        return natImage.getLuminanceOrAlpha(onTextureUV.u, onTextureUV.v) != 0;
    }
    
    private static boolean isSolid(NativeImage natImage, UV onTextureUV) {
        return natImage.getLuminanceOrAlpha(onTextureUV.u, onTextureUV.v) == -1;
    }

    private static boolean isOnFace(UV onFaceUV, UV sizeUV) {
        return onFaceUV.u >= 0 && onFaceUV.u < sizeUV.u && onFaceUV.v >= 0 && onFaceUV.v < sizeUV.v;
    }

}
