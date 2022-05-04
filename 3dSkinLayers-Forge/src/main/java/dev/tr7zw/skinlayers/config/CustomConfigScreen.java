package dev.tr7zw.skinlayers.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class CustomConfigScreen extends GuiScreen {

    protected final GuiScreen lastScreen;
    protected String screenTitle = "Settings";
    private Map<GuiButton, Runnable> buttonActions = new HashMap<>();
    private GuiButtonRowList optionsRowList;

    public CustomConfigScreen(GuiScreen lastScreen, String title) {
        this.screenTitle = title;
        this.lastScreen = lastScreen;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format(screenTitle, new Object[0]);
        this.buttonList.clear();
        this.buttonActions.clear();
        addButton(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done", new Object[0])), () -> onClose());

        initialize();
    }
    
    public void addOptionsList(List<GuiButton> options) {
        this.optionsRowList = new GuiButtonRowList(this.mc, this.width, this.height, 32, this.height - 32, 25,
                options);
    }
    
    public void addButton(GuiButton button, Runnable action) {
        this.buttonList.add(button);
        buttonActions.put(button, action);
    }
    
    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        if (!p_actionPerformed_1_.enabled)
            return;
        buttonActions.get(p_actionPerformed_1_).run();
    }
    
    public void onClose() {
        save();
        this.mc.displayGuiScreen(this.lastScreen);
    }

    public abstract void initialize();

    public abstract void save();

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.optionsRowList.handleMouseInput();
    }

    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_)
            throws IOException {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
        this.optionsRowList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
    }

    protected void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_) {
        super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_);
        this.optionsRowList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_);
    }

    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        drawDefaultBackground();
        this.optionsRowList.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 5, 16777215);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

//
//    public GuiButton getBooleanOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
//        
//        return new BooleanOption(translationKey, title, (options) -> current.get(), (options, b) -> update.accept(b));
//    }
//
//    public BooleanOption getOnOffOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
//        return getBooleanOption(translationKey, current, update);
//    }
//
    public GuiButton getDoubleOption(String translationKey, float min, float max, float steps,
            Supplier<Double> current, Consumer<Double> update) {
//        TranslatableComponent comp = new TranslatableComponent(translationKey);
//        return new ProgressOption(translationKey, min, max, steps, (options) -> current.get(),
//                (options, val) -> update.accept(val),
//                (options, opt) -> comp.append(new TextComponent(": " + opt.get(options))));
        GuiSliderButton slider = new GuiSliderButton(translationKey, min, max, steps, current, update);
        return slider;
    }

    public GuiButton getIntOption(String translationKey, float min, float max, Supplier<Integer> current,
            Consumer<Integer> update) {
        GuiSliderButton slider = new GuiSliderButton(translationKey, min, max, 1, () -> (double)current.get(), (d) -> update.accept(d.intValue()));
        return slider;
    }

    public <T extends Enum> GuiButton getEnumOption(String translationKey, Class<T> targetEnum, Supplier<T> current,
            Consumer<T> update) {
        GuiEnumButton<T> button = new GuiEnumButton<>(translationKey, translationKey, targetEnum, current, update);
        return button;
    }
    
    public GuiButton getOnOffOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
        GuiEnumButton<OnOff> button = new GuiEnumButton<OnOff>(translationKey, "text.skinlayers.boolean", OnOff.class, () -> current.get() ? OnOff.ON : OnOff.OFF, (e) -> update.accept(e == OnOff.ON));
        return button;
    }

    public static enum OnOff {
        ON, OFF
    }
    
}