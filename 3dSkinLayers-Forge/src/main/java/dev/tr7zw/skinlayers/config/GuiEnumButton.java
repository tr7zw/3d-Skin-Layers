package dev.tr7zw.skinlayers.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiEnumButton<T extends Enum> extends GuiButton {
    private final String translationKey;
    private final String enumTranslationKey;
    private final Class<T> targetEnum;
    private final Supplier<T> current;
    private final Consumer<T> update;

    public GuiEnumButton(String translationKey, String enumTranslationKey, Class<T> targetEnum, Supplier<T> current, Consumer<T> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.enumTranslationKey = enumTranslationKey;
        this.targetEnum = targetEnum;
        this.current = current;
        this.update = update;
    }

    @Override
    public void drawButton(Minecraft p_drawButton_1_, int p_drawButton_2_, int p_drawButton_3_) {
        this.displayString = I18n.format(translationKey) + ": "
                + I18n.format(enumTranslationKey + "." + current.get().name(), new Object[0]);
        super.drawButton(p_drawButton_1_, p_drawButton_2_, p_drawButton_3_);
    }

    @Override
    public boolean mousePressed(Minecraft p_mousePressed_1_, int p_mousePressed_2_, int p_mousePressed_3_) {
        if (super.mousePressed(p_mousePressed_1_, p_mousePressed_2_, p_mousePressed_3_)) {
            update.accept(targetEnum.getEnumConstants()[(current.get().ordinal() + 1)
                    % targetEnum.getEnumConstants().length]);
            return true;
        }
        return false;
    }

}