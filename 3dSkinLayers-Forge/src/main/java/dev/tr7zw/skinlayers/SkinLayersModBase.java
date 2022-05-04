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

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.config.CustomConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
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

    public static class ConfigScreen extends CustomConfigScreen {

        private Minecraft minecraft;
        
        public ConfigScreen(GuiScreen lastScreen) {
            super(lastScreen, "text.skinlayers.title");
            minecraft = Minecraft.getMinecraft();
        }
        
        @Override
        public void initialize() {
            List<GuiButton> options = new ArrayList<>();
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
                        SkinLayersModBase.instance.refreshLayers(this.minecraft.thePlayer);
                    }));
            options.add(getDoubleOption("text.skinlayers.headvoxelsize", 1.001f, 1.25f, 0.001f,
                    () -> (double) config.headVoxelSize, (i) -> {
                        config.headVoxelSize = i.floatValue();
                        SkinLayersModBase.instance.refreshLayers(this.minecraft.thePlayer);
                    }));
            options.add(getDoubleOption("text.skinlayers.bodyvoxelwidthsize", 1.001f, 1.4f, 0.001f,
                    () -> (double) config.bodyVoxelWidthSize, (i) -> {
                        config.bodyVoxelWidthSize = i.floatValue();
                        SkinLayersModBase.instance.refreshLayers(this.minecraft.thePlayer);
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
            addOptionsList(options);

        }

        @Override
        public void save() {
            SkinLayersModBase.instance.writeConfig();
        }

        @Override
        public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
            super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
            if (this.minecraft.theWorld != null) {
                int x = this.width/2;
                int y = this.height-45;
                int size = (int) (40f * (this.height / 200f));
                int lookX = x - p_drawScreen_1_;
                int lookY = y - 80 - p_drawScreen_2_;
                // Prevent the model from clipping into the back of the gui^^
                lookY = Math.min(lookY, 10);
                GlStateManager.enableDepth();
                GuiInventory.drawEntityOnScreen(x, y, size, lookX, lookY,
                        this.minecraft.thePlayer);
            }
        }
        
    }

    public void refreshLayers(EntityPlayer player) {
        if (player == null || !(player instanceof PlayerSettings))
            return;
        PlayerSettings settings = (PlayerSettings) player;
        settings.setupSkinLayers(null);
        settings.setupHeadLayers(null);
    }

}
