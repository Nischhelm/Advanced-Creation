package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Draw simple unboxed text on the GuiOverlay
 */
public class GuiOverlayText extends GuiOverlayBaseElement
{
    private String text = "";
    private double fontScale = 1.0;
    private int color = 0xFFFFFF;
    
    public GuiOverlayText(String text, int x, int y, double scale) {
        super(null,x,y,2,0,0,0,0, 0,0,0,0);
        this.text = text;
        this.fontScale =scale;
    }
    
    public void draw(Gui gui, int currZ, long time)
    {
        if (visible && getZ() == currZ)
        {
            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(fontScale, fontScale, fontScale);
                this.fontRenderer.drawStringWithShadow(text, (float)(x/fontScale), (float)(y/fontScale), color);
                GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
            }
            GlStateManager.popMatrix();
        }
    }
    
    public void setText(String text)
    {
        this.text = text;
    }

    public void setFontScale(double scale)
    {
        fontScale = scale;
    }

    public double getFontScale()
    {
        return fontScale;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public String getText()
    {
        return text;
    }
}
