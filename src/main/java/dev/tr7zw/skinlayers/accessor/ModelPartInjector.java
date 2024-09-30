package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;

public interface ModelPartInjector {

    public void setInjectedMesh(Mesh mesh, OffsetProvider offsetProvider);
    
}
