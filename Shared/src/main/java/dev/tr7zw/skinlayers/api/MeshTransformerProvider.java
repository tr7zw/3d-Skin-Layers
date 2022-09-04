package dev.tr7zw.skinlayers.api;


import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Interface for mods like bendy-lib to interface.
 * 
 * @author tr7zw
 *
 */
public interface MeshTransformerProvider {

    public MeshTransformer prepareTransformer(@Nullable ModelPart vanillaModel);
    
    public static final MeshTransformerProvider EMPTY_PROVIDER = (cube) -> MeshTransformer.EMPTY_TRANSFORMER;
    
}
