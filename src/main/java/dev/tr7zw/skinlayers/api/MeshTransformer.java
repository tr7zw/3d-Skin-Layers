package dev.tr7zw.skinlayers.api;

//#if MC >= 11903
import org.joml.Vector3f;
import org.joml.Vector4f;
//#else
//$$ import com.mojang.math.Vector3f;
//$$ import com.mojang.math.Vector4f;
//#endif

import net.minecraft.client.model.geom.ModelPart.Cube;

/**
 * Interface for mods like bendy-lib to interface.
 * 
 * @author tr7zw
 *
 */
public interface MeshTransformer {

    public void transform(Vector3f position, Vector4f[] vertexData);

    public void transform(Cube cube);

    public static final MeshTransformer EMPTY_TRANSFORMER = new MeshTransformer() {

        @Override
        public void transform(Cube cube) {
            // do nothing
        }

        @Override
        public void transform(Vector3f position, Vector4f[] vertexData) {
            // do nothing
        }
    };

}
