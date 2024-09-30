package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;

public interface OffsetProvider {

    public void applyOffset(PoseStack stack, Mesh mesh);

    public final static OffsetProvider HEAD = createVanilla(Shape.HEAD, false);
    public final static OffsetProvider LEFT_LEG = createVanilla(Shape.LEGS, false);
    public final static OffsetProvider RIGHT_LEG = createVanilla(Shape.LEGS, false);
    public final static OffsetProvider LEFT_ARM = createVanilla(Shape.ARMS, false);
    public final static OffsetProvider LEFT_ARM_SLIM = createVanilla(Shape.ARMS_SLIM, false);
    public final static OffsetProvider RIGHT_ARM = createVanilla(Shape.ARMS, true);
    public final static OffsetProvider RIGHT_ARM_SLIM = createVanilla(Shape.ARMS_SLIM, true);
    public final static OffsetProvider BODY = createVanilla(Shape.BODY, false);
    
    private static OffsetProvider createVanilla(Shape shape, boolean mirrored) {
        return (stack, mesh) -> {
            float pixelScaling = SkinLayersModBase.config.baseVoxelSize;
            float heightScaling = 1.035f;
            float widthScaling = SkinLayersModBase.config.baseVoxelSize;

            float x = 0;
            float y = 0;
            if (shape == Shape.ARMS) {
                x = 0.998f;
            } else if (shape == Shape.ARMS_SLIM) {
                x = 0.499f;
            }
            if (shape == Shape.BODY) {
                widthScaling = SkinLayersModBase.config.bodyVoxelWidthSize;
            }
            if (mirrored) {
                x *= -1;
            }
            if (shape == Shape.HEAD) {
                float voxelSize = SkinLayersModBase.config.headVoxelSize;
                stack.translate(0, -0.25, 0);
                stack.scale(voxelSize, voxelSize, voxelSize);
                stack.translate(0, 0.25, 0);
                stack.translate(0, -0.04, 0);
            } else {
                stack.scale(widthScaling, heightScaling, pixelScaling);
                y = shape.yOffsetMagicValue();
            }

            mesh.setPosition(x, y, 0);

        };
    }

}
