package dev.tr7zw.skinlayers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.XRandR.Screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import net.minecraft.entity.player.EntityPlayer;

public abstract class SkinLayersModBase {

    public static SkinLayersModBase instance;
    public static final Logger LOGGER = LogManager.getLogger();
    public static Config config = null;
    private File settingsFile = new File("config", "skinlayers.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void onInitialize() {
        instance = this;
        if (settingsFile.exists()) {
            try {
                config = new Gson().fromJson(
                        new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
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
        /*CustomConfigScreen screen = new CustomConfigScreen(parent, "text.skinlayers.title") {

            @Override
            public void initialize() {
                List<Option> options = new ArrayList<>();
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
                getOptions().addSmall(options.toArray(new Option[0]));

            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public void render(PoseStack poseStack, int xMouse, int yMouse, float f) {
                super.render(poseStack, xMouse, yMouse, f);
                if (this.minecraft.level != null) {
                    int x = minecraft.getWindow().getGuiScaledWidth()/2;
                    int y = minecraft.getWindow().getGuiScaledHeight()-45;
                    int size = (int) (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
                    int lookX = x - xMouse;
                    int lookY = y - 80 - yMouse;
                    // Prevent the model from clipping into the back of the gui^^
                    lookY = Math.min(lookY, 10);
                    InventoryScreen.renderEntityInInventory(x, y, size, lookX, lookY,
                            this.minecraft.player);
                }
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

        return screen;*/
        return null;
    }

    public void refreshLayers(EntityPlayer player) {
        if (player == null || !(player instanceof PlayerSettings))
            return;
        PlayerSettings settings = (PlayerSettings) player;
        settings.setupSkinLayers(null);
        settings.setupHeadLayers(null);
    }

}
