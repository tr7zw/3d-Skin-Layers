package dev.tr7zw.skinlayers.exporter;

import java.io.File;
import java.io.IOException;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class MultiBufferObjConsumer implements MultiBufferSource {

    private RenderType lastType = null;
    private ObjConsumer lastConsumer = null;
    private final File outputFolder;
    private int id = 0;
    
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
