package dev.tr7zw.skinlayers.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonRowList extends GuiListExtended {
    private final List<Row> field_148184_k = Lists.newArrayList();

    public GuiButtonRowList(Minecraft p_i45015_1_, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_,
            int p_i45015_6_, List<GuiButton> buttons) {
        super(p_i45015_1_, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
        this.field_148163_i = false;
        for (int lvt_8_1_ = 0; lvt_8_1_ < buttons.size(); lvt_8_1_ += 2) {
            buttons.get(lvt_8_1_).xPosition = p_i45015_2_ / 2 - 155;
            if(lvt_8_1_ < buttons.size() - 1) {
                buttons.get(lvt_8_1_+1).xPosition = p_i45015_2_ / 2 - 155 + 160;
            }
            this.field_148184_k.add(new Row(buttons.get(lvt_8_1_), (lvt_8_1_ < buttons.size() - 1) ? buttons.get(lvt_8_1_+1) : null));
        }
    }

    public Row getListEntry(int p_getListEntry_1_) {
        return this.field_148184_k.get(p_getListEntry_1_);
    }

    protected int getSize() {
        return this.field_148184_k.size();
    }

    public int getListWidth() {
        return 400;
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 32;
    }
    
    public static class Row implements GuiListExtended.IGuiListEntry {
        private final Minecraft field_148325_a;

        private final GuiButton field_148323_b;

        private final GuiButton field_148324_c;

        public Row(GuiButton p_i45014_1_, GuiButton p_i45014_2_) {
            this.field_148325_a = Minecraft.getMinecraft();
            this.field_148323_b = p_i45014_1_;
            this.field_148324_c = p_i45014_2_;
        }

        public void drawEntry(int p_drawEntry_1_, int p_drawEntry_2_, int p_drawEntry_3_, int p_drawEntry_4_,
                int p_drawEntry_5_, int p_drawEntry_6_, int p_drawEntry_7_, boolean p_drawEntry_8_) {
            if (this.field_148323_b != null) {
                this.field_148323_b.yPosition = p_drawEntry_3_;
                this.field_148323_b.drawButton(this.field_148325_a, p_drawEntry_6_, p_drawEntry_7_);
            }
            if (this.field_148324_c != null) {
                this.field_148324_c.yPosition = p_drawEntry_3_;
                this.field_148324_c.drawButton(this.field_148325_a, p_drawEntry_6_, p_drawEntry_7_);
            }
        }

        public boolean mousePressed(int p_mousePressed_1_, int p_mousePressed_2_, int p_mousePressed_3_,
                int p_mousePressed_4_, int p_mousePressed_5_, int p_mousePressed_6_) {
            if (this.field_148323_b.mousePressed(this.field_148325_a, p_mousePressed_2_, p_mousePressed_3_)) {
                if (this.field_148323_b instanceof GuiOptionButton) {
                    this.field_148325_a.gameSettings
                            .setOptionValue(((GuiOptionButton) this.field_148323_b).returnEnumOptions(), 1);
                    this.field_148323_b.displayString = this.field_148325_a.gameSettings
                            .getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148323_b.id));
                }
                return true;
            }
            if (this.field_148324_c != null
                    && this.field_148324_c.mousePressed(this.field_148325_a, p_mousePressed_2_, p_mousePressed_3_)) {
                return true;
            }
            return false;
        }

        public void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_,
                int p_mouseReleased_4_, int p_mouseReleased_5_, int p_mouseReleased_6_) {
            if (this.field_148323_b != null)
                this.field_148323_b.mouseReleased(p_mouseReleased_2_, p_mouseReleased_3_);
            if (this.field_148324_c != null)
                this.field_148324_c.mouseReleased(p_mouseReleased_2_, p_mouseReleased_3_);
        }

        public void setSelected(int p_setSelected_1_, int p_setSelected_2_, int p_setSelected_3_) {
        }
    }
}