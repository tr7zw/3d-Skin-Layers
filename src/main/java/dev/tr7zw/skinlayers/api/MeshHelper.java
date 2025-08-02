package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.platform.NativeImage;

public interface MeshHelper {

    /**
     * Creates a renderable 3d cube mesh from a texture. Example values: 64x64 skin
     * player head layer: create3DMesh(skin, 8, 8, 8, 32, 0, false, 0.6f) 64x64 skin
     * player torso layer: create3DMesh(skin, 8, 12, 4, 16, 32, true, -0.8f)
     * 
     * @param natImage       Imaged used for the mesh generation
     * @param width          width of the cube, seen from the front
     * @param height         height of the cube
     * @param depth          depth of the cube
     * @param textureU       x offset of where the texture is located. Needs to
     *                       point to the top left corner
     * @param textureV       y offset of where the texture is located. Needs to
     *                       point to the top left corner
     * @param topPivot       decides where the piviot is placed. Arms/legs have it
     *                       at the top, the head has it at the bottom.
     * @param rotationOffset magic number to offset the piviot
     * @return
     */
    public Mesh create3DMesh(NativeImage natImage, int width, int height, int depth, int textureU, int textureV,
            boolean topPivot, float rotationOffset);

    /**
     * Creates a renderable 3d cube mesh from a texture. Example values: 64x64 skin
     * player head layer: create3DMesh(skin, 8, 8, 8, 32, 0, false, 0.6f) 64x64 skin
     * player torso layer: create3DMesh(skin, 8, 12, 4, 16, 32, true, -0.8f)
     * 
     * @param natImage       Imaged used for the mesh generation
     * @param width          width of the cube, seen from the front
     * @param height         height of the cube
     * @param depth          depth of the cube
     * @param textureU       x offset of where the texture is located. Needs to
     *                       point to the top left corner
     * @param textureV       y offset of where the texture is located. Needs to
     *                       point to the top left corner
     * @param topPivot       decides where the piviot is placed. Arms/legs have it
     *                       at the top, the head has it at the bottom.
     * @param rotationOffset magic number to offset the piviot
     * @param mirror         if the mesh should be mirrored, e.g. for the left arm
     * @return
     */
    public Mesh create3DMesh(NativeImage natImage, int width, int height, int depth, int textureU, int textureV,
            boolean topPivot, float rotationOffset, boolean mirror);

}
