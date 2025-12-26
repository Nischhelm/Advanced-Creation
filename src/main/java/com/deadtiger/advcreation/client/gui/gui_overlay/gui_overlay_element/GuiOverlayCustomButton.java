package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

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
public class GuiOverlayCustomButton extends GuiOverlayBaseElement
{
    
    protected String text;
    protected GuiOverlayBaseElement rightSide;
    public int textYOffset = 0;

    public GuiOverlayCustomButton(String text, int x, int y, int width, int height)
    {
        super(new ResourceLocation("textures/gui/widgets.png"), x, y, 0, 66 , width, height, 0,20,0,20);
        this.y_disabledOffset = -20;
        this.text = text;
        int halfWidth = (int)Math.ceil(this.width/2.0);
        rightSide = new GuiOverlayBaseElement(this.texture,
            this.x + halfWidth, this.y,200 - halfWidth, 66 ,
            halfWidth, this.height, 0,20,0,20);
        rightSide.y_disabledOffset = -20;
        addElement(rightSide);

        
        rightSide.setTimedDeactivation(true);
        this.setTimedDeactivation(true);
    }

    @Override
    public void draw(Gui gui, int currZ, long time)
    {
        if(currZ != this.z)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            int textColor = this.enabled? 14737632 : Color.GRAY.getRGB();
            //if one is selected activate the other
            if(this.selected || rightSide.isSelected())
            {
                textColor = 16777120;
                activateTheOtherToo();
            }
            //if one is hovered on set the other ot hover on
            else if (this.hoverOn || rightSide.isHoverOn())
            {
                textColor = this.enabled? 16777120 : Color.GRAY.getRGB();
                this.setHoverOn(true);
                rightSide.setHoverOn(true);
                this.setSelected(false);
                rightSide.setSelected(false);
            }
            else
                {
                this.setHoverOn(false);
                rightSide.setHoverOn(false);
                this.setSelected(false);
                rightSide.setSelected(false);
            
            }

            super.draw(gui, currZ, time);

            gui.drawCenteredString(fontRenderer, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2 + textYOffset, textColor);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.popMatrix();
     
    }

    private void activateTheOtherToo()
    {
        if(!this.selected)
        {
            this.activateTimedSelected(rightSide.getSelectTime());
        }
        if(!rightSide.isSelected())
        {
            rightSide.activateTimedSelected(this.getSelectTime());
        }
    }

    @Override
    public void setName(String name)
    {
        rightSide.setName(name);
        super.setName(name);
    }
    
    @Override
    public void setVisibility(boolean visible)
    {
        rightSide.setVisibility(visible);
        super.setVisibility(visible);
    }
    
    @Override
    public void setTooltip(String tooltip)
    {
        rightSide.setTooltip(tooltip);
        super.setTooltip(tooltip);
    }
    
    @Override
    public void setHoverOn(boolean hoverOn)
    {
        rightSide.setHoverOn(hoverOn);
        super.setHoverOn(hoverOn);
    }
    
    @Override
    public void setSelected(boolean selected)
    {
        rightSide.setSelected(selected);
        super.setSelected(selected);
    }
    
    @Override
    public void setButtonKeyToDisplayInTooltip(KeyBinding buttonKeyToDisplayInTooltip)
    {
        rightSide.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        super.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
    }
    
    @Override
    public void setButtonToggleKeyToDisplayInTooltip(KeyBinding buttonToggleKeyToDisplayInTooltip)
    {
        rightSide.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        super.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
    }

    @Override
    public void setTimedDeactivation(boolean timedDeactivation)
    {
        super.setTimedDeactivation(timedDeactivation);
        rightSide.setTimedDeactivation(timedDeactivation);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        rightSide.setEnabled(enabled);
    }

    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public void setText(String text) {
        this.text = text;
    }
}
