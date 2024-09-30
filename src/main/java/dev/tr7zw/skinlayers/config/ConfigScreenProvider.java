package dev.tr7zw.skinlayers.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.skinlayers.versionless.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
//spotless:off 
//#if MC <= 11904
//$$ import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#else
import dev.tr7zw.skinlayers.render.PreviewHelper;
//#endif
//#if MC >= 11900
import net.minecraft.client.OptionInstance;
//#else
//$$ import net.minecraft.client.Option;
//#endif
//spotless:on

public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.skinlayers.title") {

            @Override
            public void initialize() {
                Config config = SkinLayersModBase.config;
                List<Object> options = new ArrayList<>();
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
                            SkinLayersModBase.instance.refreshLayers(this.minecraft.player);
                        }));
                options.add(getDoubleOption("text.skinlayers.headvoxelsize", 1.001f, 1.25f, 0.001f,
                        () -> (double) config.headVoxelSize, (i) -> {
                            config.headVoxelSize = i.floatValue();
                            SkinLayersModBase.instance.refreshLayers(this.minecraft.player);
                        }));
                options.add(getDoubleOption("text.skinlayers.bodyvoxelwidthsize", 1.001f, 1.4f, 0.001f,
                        () -> (double) config.bodyVoxelWidthSize, (i) -> {
                            config.bodyVoxelWidthSize = i.floatValue();
                            SkinLayersModBase.instance.refreshLayers(this.minecraft.player);
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
                options.add(getOnOffOption("text.skinlayers.compatibilityMode.enable", () -> config.compatibilityMode,
                        (b) -> config.compatibilityMode = b));
                options.add(getDoubleOption("text.skinlayers.firstperson.voxelsize", 1.02f, 1.2f, 0.001f,
                        () -> (double) config.firstPersonPixelScaling, (i) -> {
                            config.firstPersonPixelScaling = i.floatValue();
                        }));
                // spotless:off
                //#if MC >= 11900
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                //#else
                //$$getOptions().addSmall(options.toArray(new Option[0]));
                //#endif
                // spotless:on
            }

            @Override
            public void save() {
                SkinLayersModBase.instance.writeConfig();
                SkinLayersModBase.instance.refreshLayers(this.minecraft.player);
            }

            @Override
            // spotless:off 
            //#if MC >= 12001
            public void render(GuiGraphics guiGraphics, int xMouse, int yMouse, float f) {
                super.render(guiGraphics, xMouse, yMouse, f);
            //#else
            //$$ public void render(PoseStack poseStack, int xMouse, int yMouse, float f) {
            //$$    super.render(poseStack, xMouse, yMouse, f);
            //#endif
            // spotless:on
                if (this.minecraft.level != null) {
                    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
                    int y = minecraft.getWindow().getGuiScaledHeight() - 45;
                    int size = (int) (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
                    float lookX = x - xMouse;
                    float lookY = y - 80 - yMouse;
                    // Prevent the model from clipping into the back of the gui^^
                    lookY = Math.min(lookY, 10);
                    // spotless:off 
                    //#if MC >= 12001
                    PreviewHelper.renderEntityInInventoryFollowsMouse(guiGraphics, x, y, size, lookX, lookY,
                            this.minecraft.player);
                    //#elseif MC >= 11904
                    //$$ InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack, x, y, size, lookX, lookY,
                    //$$        this.minecraft.player);
                    //#else
                    //$$ InventoryScreen.renderEntityInInventory(x, y, size, lookX, lookY,
                    //$$ this.minecraft.player);
                    //#endif
                    // spotless:on
                }
            }

            @Override
            public void reset() {
                ModBase.config = new Config();
                SkinLayersModBase.instance.writeConfig();
                SkinLayersModBase.instance.refreshLayers(this.minecraft.player);
            }

        };
    }

}
