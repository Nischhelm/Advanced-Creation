package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 *  The same sort of button like the normal minecraft button.
 *  Uses another GuiOverlayBaseElement to act as a nice right sideof the button
 *  This and the rightSide element are always changed and operated together making them seem like one button.
 */
public class GuiOverlayOnOffButton extends GuiOverlayBaseElement {

    protected String textOn = "ON";
    protected String textOff = "OFF";
    protected GuiOverlayBaseElement tab;
    public int textYOffset = 0;

    public boolean stateIsOn = true;

    public GuiOverlayOnOffButton(int x, int y, boolean stateIsOn) {
        super(new ResourceLocation(Reference.MODID,"textures/gui/disable_tools.png"), x, y, 0, 0, 55, 20, 0, 0, 0, 0);

        tab = new GuiOverlayRedirectToParent(this,this.texture,
                this.x + 1, this.y + 1, 0, 20,
                29, 18, 0, 18, 0, 18);
        tab.setZ(2);
        addElement(tab);
        tab.setTimedDeactivation(true);
    }

    @Override
    public void draw(Gui gui, int currZ, long time) {
        if (currZ != this.z)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            int textColor = this.enabled ? 14737632 : Color.GRAY.getRGB();
            //if one is selected activate the other
            if (this.selected || tab.isSelected()) {
                textColor = 16777120;
                activateTheOtherToo();
            }
            //if one is hovered on set the other ot hover on
            else if (this.hoverOn || tab.isHoverOn()) {
                textColor = this.enabled ? 16777120 : Color.GRAY.getRGB();
                this.setHoverOn(true);
                tab.setHoverOn(true);
                this.setSelected(false);
                tab.setSelected(false);
            } else {
                this.setHoverOn(false);
                tab.setHoverOn(false);
                this.setSelected(false);
                tab.setSelected(false);

            }

            if(this.stateIsOn)
            {
                if(this.tab.getX() != this.getX() + this.getWidth() - this.tab.getWidth()-1)
                {
                    this.tab.setX(this.getX() + this.getWidth() - this.tab.getWidth()-1);
                }
            }
            else
            {
                if(this.tab.getX() != this.getX() + 1)
                {
                    this.tab.setX(this.getX() +1);
                }
            }

            super.draw(gui, currZ, time);
            if(this.stateIsOn)
                gui.drawCenteredString(fontRenderer, this.textOn, this.x + 24 / 2, this.y + (this.height - 8) / 2 + textYOffset, textColor);
            else
                gui.drawCenteredString(fontRenderer, this.textOff, this.x +this.width - 28 / 2, this.y + (this.height - 8) / 2 + textYOffset, textColor);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.popMatrix();

    }


    @Override
    public void activateTimedSelected(long time)
    {
        super.activateTimedSelected(time);
    }

//    @Override
//    public GuiOverlayBaseElement trySelect(int mouseX, int mouseY, CustomGuiUtils.EnumMouseButtonClick mouseButton, long time) {
//        GuiOverlayBaseElement selectedElement = super.trySelect(mouseX, mouseY, mouseButton, time);
//        boolean thisOrChildSelected = false;
//        if(selectedElement == this)
//        {
//            thisOrChildSelected = true;
//        }
//
//        if(!thisOrChildSelected)
//        {
//            for (GuiOverlayBaseElement element : elementlist)
//            {
//                if(element == selectedElement)
//                {
//                    thisOrChildSelected = true;
//                    break;
//                }
//            }
//        }
//
//        if(thisOrChildSelected)
//            this.stateIsOn = !this.stateIsOn;
//
//        return selectedElement;
//    }

    private void activateTheOtherToo() {
        if (!this.selected) {
            this.activateTimedSelected(tab.getSelectTime());
        }
        if (!tab.isSelected()) {
            tab.activateTimedSelected(this.getSelectTime());
        }
    }


    @Override
    public void setName(String name) {
        tab.setName(name);
        super.setName(name);
    }

    @Override
    public void setVisibility(boolean visible) {
        tab.setVisibility(visible);
        super.setVisibility(visible);
    }

    @Override
    public void setTooltip(String tooltip) {
        tab.setTooltip(tooltip);
        super.setTooltip(tooltip);
    }

    @Override
    public void setHoverOn(boolean hoverOn) {
        tab.setHoverOn(hoverOn);
        super.setHoverOn(hoverOn);
    }

    @Override
    public void setSelected(boolean selected) {
//        tab.setSelected(selected);
//        super.setSelected(selected);
    }

    @Override
    public void setButtonKeyToDisplayInTooltip(KeyBinding buttonKeyToDisplayInTooltip) {
        tab.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        super.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
    }

    @Override
    public void setButtonToggleKeyToDisplayInTooltip(KeyBinding buttonToggleKeyToDisplayInTooltip) {
        tab.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        super.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
    }

    @Override
    public void setTimedDeactivation(boolean timedDeactivation) {
        super.setTimedDeactivation(timedDeactivation);
        tab.setTimedDeactivation(timedDeactivation);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tab.setEnabled(enabled);
    }


}

