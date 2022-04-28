package dev.tr7zw.donor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.donor.ImageLoader.ImageFrame;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class DonorSkinProvider {

    public static final Logger LOGGER = LogManager.getLogger();
    private static File settingsFile = new File("config", "tr7zwDonorSettings.json");
    private static DonorSettings settings;
    private static final String userAgent = "DonorSkin/1.0.0";
    
    static {
        if(settingsFile.exists()) {
            try {
                settings = new Gson().fromJson(new FileReader(settingsFile), DonorSettings.class);
            }catch(Exception ex) {
                LOGGER.error("Error while loading " + settingsFile.getAbsolutePath() + ". Loading default values.", ex);
                settings = new DonorSettings();
            }
        } else {
            settings = new DonorSettings();
            try {
                Files.write(settingsFile.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(settings).getBytes());
            }catch(Exception ex) {
                LOGGER.error("Error while saving " + settingsFile.getAbsolutePath() + ".", ex);
            }
        }
        if(!settings.enabled) {
            LOGGER.info("Animated skins are disabled!");
        } else {
            LOGGER.info("Animated skins loaded and ready to use!");
        }
    }
    
    private final UUID uuid;
    private ImageFrame[] frames = null;
    private int length = 0;
    
    public DonorSkinProvider(UUID uuid) {
        this.uuid = uuid;
        if(!settings.enabled) {
            return;//don't run prepare, no attempt at downloading a skin
        }
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
                    URLConnection con = new URL("https://skins.trsha.re/" + uuid + ".gif").openConnection();
                    con.setRequestProperty("User-Agent", userAgent);
                    ImageFrame[] tmpFrames = ImageLoader.readGif(con.getInputStream());
                    for(ImageFrame frame : tmpFrames) {
                        length += frame.getDelay()*10;
                    }
                    frames = tmpFrames;
                } catch (Exception e) {
                    if(e instanceof FileNotFoundException) {
                     // No skin
                    }else {
                        LOGGER.error("Error while loading the animated skin.", e);
                    }
                }
            }
        });
    }
    
}
