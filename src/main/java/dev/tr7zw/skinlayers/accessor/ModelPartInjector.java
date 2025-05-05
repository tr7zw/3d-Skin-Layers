package dev.tr7zw.skinlayers.accessor;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;

public interface ModelPartInjector {

    public void setInjectedMesh(Mesh mesh, OffsetProvider offsetProvider);

    public boolean isVisible();

    public Mesh getInjectedMesh();

    public OffsetProvider getOffsetProvider();

    public void prepareTranslateAndRotate(PoseStack poseStack);

}
