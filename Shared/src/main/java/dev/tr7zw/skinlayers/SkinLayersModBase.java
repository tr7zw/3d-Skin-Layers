package dev.tr7zw.skinlayers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;

public abstract class SkinLayersModBase {

    public static SkinLayersModBase instance;
    public static final Logger LOGGER = LogManager.getLogger();
    public static Config config = null;
    private File settingsFile = new File("config", "skinlayers.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean disguiseHeadsCompatibility = false;

    public void onInitialize() {
        instance = this;
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

    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.skinlayers.title") {

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getOnOffOption("text.skinlayers.enable.hat", () -> config.enableHat,
                        (b) -> config.enableHat = b));
                options.add(getOnOffOption("text.skinlayers.enable.jacket", () -> config.enableJacket,
                        (b) -> config.enableJacket = b));
                options.add(getOnOffOption("text.skinlayers.enable.leftsleeve", () -> config.enableLeftSleeve,
                        (b) -> config.enableLeftSleeve = b));
                options.add(getOnOffOption("text.skinlayers.enable.rightsleeve", () -> config.enableRightSleeve,
                        (b) -> config.enableRightSleeve = b));
                options.add(getOnOffOption("text.skinlayers.enable.leftpants", () -> config.enableLeftPants,
                        (b) -> config.enableLeftPants = b));
                options.add(getOnOffOption("text.skinlayers.enable.rightpants", () -> config.enableRightPants,
                        (b) -> config.enableRightPants = b));
                options.add(getIntOption("text.skinlayers.renderdistancelod", 5, 40, () -> config.renderDistanceLOD,
                        (i) -> config.renderDistanceLOD = i));
                options.add(getDoubleOption("text.skinlayers.basevoxelsize", 1.001f, 1.4f, 0.001f,
                        () -> (double) config.baseVoxelSize, (i) -> {
                            config.baseVoxelSize = i.floatValue();
                            refreshLayers(this.minecraft.player);
                        }));
                options.add(getDoubleOption("text.skinlayers.headvoxelsize", 1.001f, 1.25f, 0.001f,
                        () -> (double) config.headVoxelSize, (i) -> {
                            config.headVoxelSize = i.floatValue();
                            refreshLayers(this.minecraft.player);
                        }));
                options.add(getDoubleOption("text.skinlayers.bodyvoxelwidthsize", 1.001f, 1.4f, 0.001f,
                        () -> (double) config.bodyVoxelWidthSize, (i) -> {
                            config.bodyVoxelWidthSize = i.floatValue();
                            refreshLayers(this.minecraft.player);
                        }));
                options.add(getOnOffOption("text.skinlayers.skulls.enable", () -> config.enableSkulls,
                        (b) -> config.enableSkulls = b));
                options.add(getOnOffOption("text.skinlayers.skullsitems.enable", () -> config.enableSkullsItems,
                        (b) -> config.enableSkullsItems = b));
                options.add(getDoubleOption("text.skinlayers.skulls.voxelsize", 1.001f, 1.2f, 0.001f,
                        () -> (double) config.skullVoxelSize, (i) -> {
                            config.skullVoxelSize = i.floatValue();
                        }));
                options.add(getOnOffOption("text.skinlayers.fastrender.enable", () -> config.fastRender,
                        (b) -> config.fastRender = b));
                options.add(getDoubleOption("text.skinlayers.firstperson.voxelsize", 1.02f, 1.2f, 0.001f,
                        () -> (double) config.firstPersonPixelScaling, (i) -> {
                            config.firstPersonPixelScaling = i.floatValue();
                        }));
                getOptions().addSmall(options.toArray(new OptionInstance[0]));

            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public void render(GuiGraphics guiGraphics, int xMouse, int yMouse, float f) {
                super.render(guiGraphics, xMouse, yMouse, f);
                if (this.minecraft.level != null) {
                    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
                    int y = minecraft.getWindow().getGuiScaledHeight() - 45;
                    float size = (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
                    float lookX = x - xMouse;
                    float lookY = y - 80 - yMouse;
                    // Prevent the model from clipping into the back of the gui^^
                    lookY = Math.min(lookY, 10);
                    InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x, y, x+500, y+500, 30, size, lookX, lookY,
                            this.minecraft.player);
                }
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

        return screen;
    }

    public void refreshLayers(Player player) {
        if (player == null || !(player instanceof PlayerSettings))
            return;
        PlayerSettings settings = (PlayerSettings) player;
        settings.clearMeshes();
        settings.setCurrentSkin(null);
    }

}
