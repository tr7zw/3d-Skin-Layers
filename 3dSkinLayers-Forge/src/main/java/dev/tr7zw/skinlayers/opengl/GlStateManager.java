package dev.tr7zw.skinlayers.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class GlStateManager {

    public static void _getTexImage(int i, int j, int k, int l, ByteBuffer m) {
        GL11.glGetTexImage(i, j, k, l, m);
    }
    
    public static void _pixelStore(int i, int j) {
        GL11.glPixelStorei(i, j);
    }

}
