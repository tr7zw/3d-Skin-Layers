package dev.tr7zw.skinlayers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class SkinLayersModBase {
	
    public static Config config = null;
    
	public void onInitialize() {
        File settingsFile = new File("config", "skinlayers.json");
        if(settingsFile.exists()) {
            try {
                config = new Gson().fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
            }catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        if(config == null) {
            config = new Config();
            try {
                Files.write(settingsFile.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(config).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
