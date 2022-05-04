package dev.tr7zw.skinlayers.config;

import java.util.Set;

import dev.tr7zw.skinlayers.SkinLayersModBase.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft paramMinecraft) {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigScreen(parentScreen);
    }

}
