package dev.tr7zw.skinlayers.exporter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.GL15;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class MultiBufferObjConsumer implements MultiBufferSource {

    private RenderType lastType = null;
    private ObjConsumer lastConsumer = null;
    private final File outputFolder;
    private int id = 0;
    private Set<Integer> dumpedTextureIds = new HashSet<>();
    
    public MultiBufferObjConsumer(File outputFolder) {
        this.outputFolder = outputFolder;
    }
    
    @Override
    public VertexConsumer getBuffer(RenderType paramRenderType) {
        if(paramRenderType != lastType) {
            saveCurrent();
            lastType = paramRenderType;
            lastConsumer = new ObjConsumer();
        }
        return lastConsumer;
    }
    
    private void saveCurrent() {
        if(lastConsumer != null) {
            try {
                lastConsumer.writeData(new File(outputFolder, id++ + ".obj"));
                lastType.setupRenderState();
                int textureId = 0;
                int shaderId = 0;
                do {
                    textureId = RenderSystem.getShaderTexture(shaderId);
                    shaderId++;
                    if(textureId == 0 || dumpedTextureIds.contains(textureId)) {
                        break;
                    }
                    dumpedTextureIds.add(textureId);
                    GL15.glBindTexture(GL15.GL_TEXTURE_2D, textureId);
                    float width = GL15.glGetTexLevelParameterf(GL15.GL_TEXTURE_2D, 0, GL15.GL_TEXTURE_WIDTH);
                    float height = GL15.glGetTexLevelParameterf(GL15.GL_TEXTURE_2D, 0, GL15.GL_TEXTURE_HEIGHT);
                    if(width <= 0 || height <= 0) {
                        continue;
                    }
                    try(NativeImage img = new NativeImage(Format.RGBA, (int)width, (int)height, false)){
                        img.downloadTexture(0, false);
                        img.writeToFile(new File(outputFolder, "texture_" + textureId + ".png"));
                    }
                }while(textureId != 0);
//                ByteBuffer buffer = ByteBuffer.allocate(50*1024*1024);
//                GL15.glGetTexImage(GL15.GL_TEXTURE_2D, 0, GL15.GL_RGBA8, GL15.GL_BYTE, buffer);
//                byte[] data = new byte[buffer.position()];
//                buffer.rewind();
//                buffer.get(data);
//                Files.write(new File(outputFolder, id++ + ".png").toPath(), data);
//                System.out.println("Saved image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void close() {
        saveCurrent();
        lastConsumer = null;
        lastType = null;
    }

}
