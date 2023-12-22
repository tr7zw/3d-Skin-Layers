package dev.tr7zw.skinlayers.versionless;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.skinlayers.versionless.config.Config;

public abstract class ModBase {

    public static final Logger LOGGER = LogManager.getLogger();
    public static Config config = null;
    private File settingsFile = new File("config", "skinlayers.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean disguiseHeadsCompatibility = false;

    public void onInitialize() {
        if (settingsFile.exists()) {
            try {
                config = new Gson().fromJson(
                        new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        }
        try {
            Class<?> clientClass = Class.forName("dev.tr7zw.disguiseheads.DisguiseHeadsShared");
            disguiseHeadsCompatibility = clientClass != null; // to shut up the compiler that the var is not used
            LOGGER.info("Found DisguiseHeads, enable compatibility!");
        } catch (Throwable ex) {
            // not installed
        }
    }

    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
