package dev.tr7zw.skinlayers.config;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSliderButton extends GuiButton {
    private float sliderValue = 1.0F;
    private final String translationKey;
    public boolean dragging;
    private final float min,max,steps;
    private final Supplier<Double> current;
    private final Consumer<Double> update;


    public GuiSliderButton(String translationKey, float min, float max, float steps,
            Supplier<Double> current, Consumer<Double> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.min = min;
        this.max = max;
        this.steps = steps;
        this.current = current;
        this.update = update;
        this.sliderValue = (float) ((current.get()-min) / (max-min));
        this.displayString = I18n.format(translationKey) + ": " + getRounded(current.get());
    }

    protected int getHoverState(boolean p_getHoverState_1_) {
        return 0;
    }

    protected void mouseDragged(Minecraft p_mouseDragged_1_, int p_mouseDragged_2_, int p_mouseDragged_3_) {
        if (!this.visible)
            return;
        if (this.dragging) {
            this.sliderValue = ((float)p_mouseDragged_2_ - (float)this.xPosition + 4f) / ((float)this.width - 8f);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            float lvt_4_1_ = min + (sliderValue * (max-min));
            lvt_4_1_ = (int)(lvt_4_1_/steps);
            lvt_4_1_ *= steps;
            update.accept((double) lvt_4_1_);
            this.sliderValue = (float) ((current.get()-min) / (max-min));
            this.displayString = I18n.format(translationKey) + ": " + getRounded(current.get());
        }
        p_mouseDragged_1_.getTextureManager().bindTexture(buttonTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4,
                20);
        drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66,
                4, 20);
    }

    public boolean mousePressed(Minecraft p_mousePressed_1_, int p_mousePressed_2_, int p_mousePressed_3_) {
        if (super.mousePressed(p_mousePressed_1_, p_mousePressed_2_, p_mousePressed_3_)) {
            this.sliderValue = ((float)p_mousePressed_2_ - (float)this.xPosition + 4f) / ((float)this.width - 8f);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            float lvt_4_1_ = min + (sliderValue * (max-min));
            update.accept((double) lvt_4_1_);
            this.sliderValue = (float) (max /current.get());
            this.displayString = I18n.format(translationKey) + ": " + getRounded(current.get());
            this.dragging = true;
            return true;
        }
        return false;
    }

    public void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_) {
        this.dragging = false;
    }
    
    public String getRounded(double d) {
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(d);
    }
    
}