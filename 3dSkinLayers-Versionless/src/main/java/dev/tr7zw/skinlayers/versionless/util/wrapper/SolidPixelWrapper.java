package dev.tr7zw.skinlayers.versionless.util.wrapper;

import java.util.HashSet;
import java.util.Set;

import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.skinlayers.versionless.util.Direction;

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
     * Side to Direction: Top - DOWN Down - UP Front - NORTH Back - SOUTH Right -
     * WEST Left - EAST
     **/
    public static ModelBuilder wrapBox(ModelBuilder builder, TextureData natImage, int width, int height, int depth,
            int textureU, int textureV, boolean topPivot, float rotationOffset) {
        builder.textureSize(natImage.getWidth(), natImage.getHeight());
        float staticXOffset = -width / 2f;
        float staticYOffset = topPivot ? +rotationOffset : -height + rotationOffset;
        float staticZOffset = -depth / 2f;
        Position staticOffset = new Position(staticXOffset, staticYOffset, staticZOffset);
        Dimensions dimensions = new Dimensions(width, height, depth);
        UV textureUV = new UV(textureU, textureV);
        try {
            for (Direction face : Direction.values()) {
                UV sizeUV = getSizeUV(dimensions, face);
                for (int u = 0; u < sizeUV.u; u++) {
                    for (int v = 0; v < sizeUV.v; v++) {
                        addPixel(natImage, builder, staticOffset, face, dimensions, new UV(u, v), textureUV, sizeUV);
                    }
                }
            }
        } catch (Exception ex) { // Some calculation went wrong and out of bounds/some other issue
            ModBase.LOGGER.error("Error while creating 3d skin model. Please report on the Github/Discord.", ex);
            return null;
        }

        // if cubes is empty, there are no pixels. Don't add an empty box.
        if (ModBase.config.fastRender && !builder.isEmpty()) {
            builder.uv(textureU, textureV).addVanillaBox(staticXOffset, staticYOffset, staticZOffset, width, height,
                    depth);
        }

        return builder;
    }

    private static UV getSizeUV(Dimensions dimensions, Direction face) {
        if (face == Direction.DOWN || face == Direction.UP) {
            return new UV(dimensions.width, dimensions.depth);
        } else if (face == Direction.NORTH || face == Direction.SOUTH) {
            return new UV(dimensions.width, dimensions.height);
        } else { // Assuming the remaining cases are WEST or EAST
            return new UV(dimensions.depth, dimensions.height);
        }
    }

    private static UV getOnTextureUV(UV textureUV, UV onFaceUV, Dimensions dimensions, Direction face) {
        if (face == Direction.DOWN) {
            return new UV(textureUV.u + dimensions.depth + onFaceUV.u, textureUV.v + onFaceUV.v);
        } else if (face == Direction.UP) {
            return new UV(textureUV.u + dimensions.width + dimensions.depth + onFaceUV.u, textureUV.v + onFaceUV.v);
        } else if (face == Direction.NORTH) {
            return new UV(textureUV.u + dimensions.depth + onFaceUV.u, textureUV.v + dimensions.depth + onFaceUV.v);
        } else if (face == Direction.SOUTH) {
            return new UV(textureUV.u + dimensions.depth + dimensions.width + dimensions.depth + onFaceUV.u,
                    textureUV.v + dimensions.depth + onFaceUV.v);
        } else if (face == Direction.WEST) {
            return new UV(textureUV.u + onFaceUV.u, textureUV.v + dimensions.depth + onFaceUV.v);
        } else { // Assuming the remaining case is EAST
            return new UV(textureUV.u + dimensions.depth + dimensions.width + onFaceUV.u,
                    textureUV.v + dimensions.depth + onFaceUV.v);
        }
    }

    private static VoxelPosition UVtoXYZ(UV onFaceUV, Dimensions dimensions, Direction face) {
        if (face == Direction.DOWN) {
            return new VoxelPosition(onFaceUV.u, 0, dimensions.depth - 1 - onFaceUV.v);
        } else if (face == Direction.UP) {
            return new VoxelPosition(onFaceUV.u, dimensions.height - 1, dimensions.depth - 1 - onFaceUV.v);
        } else if (face == Direction.NORTH) {
            return new VoxelPosition(onFaceUV.u + 0, onFaceUV.v, 0);
        } else if (face == Direction.SOUTH) {
            return new VoxelPosition(dimensions.width - 1 - onFaceUV.u, onFaceUV.v, dimensions.depth - 1);
        } else if (face == Direction.WEST) {
            return new VoxelPosition(0, onFaceUV.v, dimensions.depth - 1 - onFaceUV.u);
        } else { // Assuming the remaining case is EAST
            return new VoxelPosition(dimensions.width - 1, onFaceUV.v, onFaceUV.u + 0);
        }
    }

    private static UV XYZtoUV(VoxelPosition voxelPosition, Dimensions dimensions, Direction face) {
        if (face == Direction.DOWN || face == Direction.UP) {
            return new UV(voxelPosition.x, dimensions.depth - 1 - voxelPosition.z);
        } else if (face == Direction.NORTH) {
            return new UV(voxelPosition.x + 0, voxelPosition.y);
        } else if (face == Direction.SOUTH) {
            return new UV(dimensions.width - 1 - voxelPosition.x, voxelPosition.y);
        } else if (face == Direction.WEST) {
            return new UV(dimensions.depth - 1 - voxelPosition.z, voxelPosition.y);
        } else { // Assuming the remaining case is EAST
            return new UV(voxelPosition.z + 0, voxelPosition.y);
        }
    }

    private static void addPixel(TextureData natImage, ModelBuilder cubes, Position staticOffset, Direction face,
            Dimensions dimensions, UV onFaceUV, UV textureUV, UV sizeUV) {
        UV onTextureUV = getOnTextureUV(textureUV, onFaceUV, dimensions, face);
        if (!natImage.isPresent(onTextureUV))
            return;

        VoxelPosition voxelPosition = UVtoXYZ(onFaceUV, dimensions, face);
        Position position = new Position(staticOffset.x + voxelPosition.x, staticOffset.y + voxelPosition.y,
                staticOffset.z + voxelPosition.z);
        boolean solidPixel = natImage.isSolid(onTextureUV);

        Set<Direction> hide = new HashSet<>();
        Set<Direction[]> corners = new HashSet<>();

        boolean isOnBorder = false;
        boolean backsideOverlaps = false;
        for (Direction neighbourFace : Direction.values()) {
            if (neighbourFace.getAxis() == face.getAxis())
                continue;

            VoxelPosition neighbourVoxelPosition = new VoxelPosition(voxelPosition.x + neighbourFace.getStepX(),
                    voxelPosition.y + neighbourFace.getStepY(), voxelPosition.z + neighbourFace.getStepZ());
            UV neighbourOnFaceUV = XYZtoUV(neighbourVoxelPosition, dimensions, face);
            if (isOnFace(neighbourOnFaceUV, sizeUV)) {
                if (natImage.isPresent(getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, face))) {
                    if (!(solidPixel
                            && !natImage.isSolid(getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, face)))) {
                        hide.add(neighbourFace);
                    }
                } else {
                    VoxelPosition farNeighbourVoxelPosition = new VoxelPosition(
                            neighbourVoxelPosition.x + neighbourFace.getStepX(),
                            neighbourVoxelPosition.y + neighbourFace.getStepY(),
                            neighbourVoxelPosition.z + neighbourFace.getStepZ());
                    UV farNeighbourOnFaceUV = XYZtoUV(farNeighbourVoxelPosition, dimensions, face);
                    if (!isOnFace(farNeighbourOnFaceUV, sizeUV)) {
                        farNeighbourOnFaceUV = XYZtoUV(farNeighbourVoxelPosition, dimensions, neighbourFace);
                        if (natImage.isPresent(
                                getOnTextureUV(textureUV, farNeighbourOnFaceUV, dimensions, neighbourFace))) {
                            if (!(solidPixel && !natImage.isSolid(
                                    getOnTextureUV(textureUV, farNeighbourOnFaceUV, dimensions, neighbourFace)))) {
                                hide.add(neighbourFace);
                            }
                        }
                    }
                }
            } else {
                isOnBorder = true;
                neighbourOnFaceUV = XYZtoUV(voxelPosition, dimensions, neighbourFace);
                if (natImage.isPresent(getOnTextureUV(textureUV, neighbourOnFaceUV, dimensions, neighbourFace))) {
                    backsideOverlaps = true;
                    hide.add(neighbourFace);
                    corners.add(new Direction[] { face.getOpposite(), neighbourFace });
                } else {
                    UV downNeighbourOnFaceUV = XYZtoUV(new VoxelPosition(voxelPosition.x - face.getStepX(),
                            voxelPosition.y - face.getStepY(), voxelPosition.z - face.getStepZ()), dimensions,
                            neighbourFace);
                    if (natImage
                            .isPresent(getOnTextureUV(textureUV, downNeighbourOnFaceUV, dimensions, neighbourFace))) {
                        backsideOverlaps = true;
                    }
                }
            }
        }

        if (!isOnBorder || backsideOverlaps) {
            hide.add(face.getOpposite());
        }
        if (ModBase.config.fastRender) {
            hide.add(face); // the front face gets handled in one big cube
        }

        cubes.uv(onTextureUV.u, onTextureUV.v).addBox(position.x, position.y, position.z, pixelSize,
                hide.toArray(new Direction[0]), corners.toArray(new Direction[0][0]));
    }

    private static boolean isOnFace(UV onFaceUV, UV sizeUV) {
        return onFaceUV.u >= 0 && onFaceUV.u < sizeUV.u && onFaceUV.v >= 0 && onFaceUV.v < sizeUV.v;
    }

}
