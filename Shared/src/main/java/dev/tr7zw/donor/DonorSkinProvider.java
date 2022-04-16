package dev.tr7zw.donor;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.donor.ImageLoader.ImageFrame;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class DonorSkinProvider {

    private final UUID uuid;
    private ImageFrame[] frames = null;
    private int length = 0;
    
    public DonorSkinProvider(UUID uuid) {
        this.uuid = uuid;
        prepare();
    }
    
    public ResourceLocation getSkin() {
        if(frames == null) {
            return null;
        }
        long targetFrame = System.currentTimeMillis()%length;
        for(ImageFrame frame : frames) {
            targetFrame -= frame.getDelay()*10;
            if(targetFrame <= 0) {
                return frame.getResource();
            }
        }
        return null;
    }
    
    public NativeImage getNativeImage() {
        if(frames == null) {
            return null;
        }
        long targetFrame = System.currentTimeMillis()%length;
        for(ImageFrame frame : frames) {
            targetFrame -= frame.getDelay()*10;
            if(targetFrame <= 0) {
                return frame.getNativeImage();
            }
        }
        return null;
    }
    
    private void prepare() {
        Util.backgroundExecutor().execute(new Runnable() {
            
            @Override
            public void run() {
                try {
                    //System.out.println("Fetching " + "https://tr7zw.dev/skins/" + uuid + ".gif");
                    ImageFrame[] tmpFrames = ImageLoader.readGif(new URL("https://tr7zw.dev/skins/" + uuid + ".gif").openStream());
                    for(ImageFrame frame : tmpFrames) {
                        length += frame.getDelay()*10;
                    }
                    frames = tmpFrames;
                } catch (IOException e) {
                    // No skin
                }
            }
        });
    }
    
}
