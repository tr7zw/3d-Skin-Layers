package dev.tr7zw.skinlayers.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkullRendererCache;
import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.skinlayers.versionless.config.Config;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WPlayerPreview;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.trender.gui.widget.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.Items;

public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent).createScreen();
    }

    private static class CustomConfigScreen extends AbstractConfigScreen {

        public CustomConfigScreen(Screen previous) {
            super(ComponentProvider.translatable("text.skinlayers.title"), previous);

            WGridPanel root = new WGridPanel(8);
            root.setInsets(Insets.ROOT_PANEL);
            setRootPanel(root);

            WTabPanel wTabPanel = new WTabPanel();

            WGridPanel playerSettings = new WGridPanel();
            playerSettings.setInsets(new Insets(2, 4));

            // options page
            List<OptionInstance> options = new ArrayList<>();
            options.add(getOnOffOption("text.skinlayers.enable.hat", () -> SkinLayersModBase.config.enableHat,
                    (b) -> SkinLayersModBase.config.enableHat = b));
            options.add(getOnOffOption("text.skinlayers.enable.jacket", () -> SkinLayersModBase.config.enableJacket,
                    (b) -> SkinLayersModBase.config.enableJacket = b));
            options.add(
                    getOnOffOption("text.skinlayers.enable.leftsleeve", () -> SkinLayersModBase.config.enableLeftSleeve,
                            (b) -> SkinLayersModBase.config.enableLeftSleeve = b));
            options.add(getOnOffOption("text.skinlayers.enable.rightsleeve",
                    () -> SkinLayersModBase.config.enableRightSleeve,
                    (b) -> SkinLayersModBase.config.enableRightSleeve = b));
            options.add(
                    getOnOffOption("text.skinlayers.enable.leftpants", () -> SkinLayersModBase.config.enableLeftPants,
                            (b) -> SkinLayersModBase.config.enableLeftPants = b));
            options.add(
                    getOnOffOption("text.skinlayers.enable.rightpants", () -> SkinLayersModBase.config.enableRightPants,
                            (b) -> SkinLayersModBase.config.enableRightPants = b));
            options.add(getIntOption("text.skinlayers.renderdistancelod", 5, 40,
                    () -> SkinLayersModBase.config.renderDistanceLOD,
                    (i) -> SkinLayersModBase.config.renderDistanceLOD = i));
            options.add(getDoubleOption("text.skinlayers.basevoxelsize", 1.001f, 1.4f, 0.001f,
                    () -> SkinLayersModBase.config.baseVoxelSize, (i) -> {
                        SkinLayersModBase.config.baseVoxelSize = (float) i;
                        SkinLayersModBase.instance.refreshLayers(Minecraft.getInstance().player);
                    }));
            options.add(getDoubleOption("text.skinlayers.headvoxelsize", 1.001f, 1.25f, 0.001f,
                    () -> (double) SkinLayersModBase.config.headVoxelSize, (i) -> {
                        SkinLayersModBase.config.headVoxelSize = (float) i;
                        SkinLayersModBase.instance.refreshLayers(Minecraft.getInstance().player);
                    }));
            options.add(getDoubleOption("text.skinlayers.bodyvoxelwidthsize", 1.001f, 1.4f, 0.001f,
                    () -> (double) SkinLayersModBase.config.bodyVoxelWidthSize, (i) -> {
                        SkinLayersModBase.config.bodyVoxelWidthSize = (float) i;
                        SkinLayersModBase.instance.refreshLayers(Minecraft.getInstance().player);
                    }));

            var optionList = createOptionList(options);
            optionList.setGap(-1);
            //            optionList.setSize(14 * 20, 9 * 20);

            playerSettings.add(optionList, 0, 0, 12, 9);

            var playerPreview = new WPlayerPreview();
            playerPreview.setShowBackground(true);
            playerSettings.add(playerPreview, 13, 2);

            wTabPanel.add(playerSettings, b -> b.title(ComponentProvider.translatable("text.skinlayers.tab.player"))
                    .icon(new ItemIcon(Items.VILLAGER_SPAWN_EGG)));

            // Player Heads
            options = new ArrayList<>();
            options.add(getOnOffOption("text.skinlayers.skulls.enable", () -> SkinLayersModBase.config.enableSkulls,
                    (b) -> SkinLayersModBase.config.enableSkulls = b));
            options.add(getOnOffOption("text.skinlayers.skullsitems.enable",
                    () -> SkinLayersModBase.config.enableSkullsItems,
                    (b) -> SkinLayersModBase.config.enableSkullsItems = b));
            options.add(getDoubleOption("text.skinlayers.skulls.voxelsize", 1.001f, 1.2f, 0.001f,
                    () -> (double) SkinLayersModBase.config.skullVoxelSize, (i) -> {
                        SkinLayersModBase.config.skullVoxelSize = (float) i;
                    }));
            optionList = createOptionList(options);
            optionList.setGap(-1);

            wTabPanel.add(optionList, b -> b.title(ComponentProvider.translatable("text.skinlayers.tab.heads"))
                    .icon(new ItemIcon(Items.PLAYER_HEAD)));

            // Other Settings
            options = new ArrayList<>();
            options.add(getOnOffOption("text.skinlayers.fastrender.enable", () -> SkinLayersModBase.config.fastRender,
                    (b) -> SkinLayersModBase.config.fastRender = b));
            //? if < 1.21.2 {
            /*
             options.add(getOnOffOption("text.skinlayers.compatibilityMode.enable",
                    () -> SkinLayersModBase.config.compatibilityMode,
                    (b) -> SkinLayersModBase.config.compatibilityMode = b));
            *///? }
               //? if >= 1.20.0 {

            options.add(getOnOffOption("text.skinlayers.irisCompatibilityMode.enable",
                    () -> SkinLayersModBase.config.irisCompatibilityMode,
                    (b) -> SkinLayersModBase.config.irisCompatibilityMode = b));
            options.add(getOnOffOption("text.skinlayers.applySodiumWorkaround.enable",
                    () -> SkinLayersModBase.config.applySodiumWorkaround,
                    (b) -> SkinLayersModBase.config.applySodiumWorkaround = b));
            //? }
            options.add(getDoubleOption("text.skinlayers.firstperson.voxelsize", 1.02f, 1.3f, 0.001f,
                    () -> (double) SkinLayersModBase.config.firstPersonPixelScaling, (i) -> {
                        SkinLayersModBase.config.firstPersonPixelScaling = (float) i;
                    }));
            optionList = createOptionList(options);
            optionList.setGap(-1);

            wTabPanel.add(optionList, b -> b.title(ComponentProvider.translatable("text.skinlayers.tab.other"))
                    .icon(new ItemIcon(Items.COMMAND_BLOCK)));

            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                save();
                Minecraft.getInstance().setScreen(previous);
            });
            root.add(doneButton, 0, 26, 6, 2);

            wTabPanel.layout();
            root.add(wTabPanel, 0, 1);

            WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
            resetButton.setOnClick(() -> {
                reset();
                root.layout();
            });
            root.add(resetButton, 23, 26, 6, 2);

            root.setBackgroundPainter(BackgroundPainter.VANILLA);

            root.validate(this);
            root.setHost(this);
        }

        @Override
        public void reset() {
            ModBase.config = new Config();
        }

        @Override
        public void save() {
            SkinLayersModBase.instance.writeConfig();
            SkinLayersModBase.instance.refreshLayers(Minecraft.getInstance().player);
            SkullRendererCache.clearCache();
        }

    }

}
