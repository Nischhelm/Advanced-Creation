package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public abstract class GuiBaseButton extends GuiButton
{
    protected  ResourceLocation texture = new ResourceLocation(Reference.MODID,"textures/gui/selecttemplatescreen.png");

    public int buttonWidth;
    public int buttonHeight ;
    
    //coordinates of the image in the mainTexture
    protected int u_unhovered = 7;
    protected int u_hovered = 7;
    protected int v_unhovered = 87;
    protected int v_hovered = 119;
    
    protected boolean overRideHover = false;
    
    public GuiBaseButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,int buttonWidth, int buttonHeight) {
        super(buttonId, x - buttonWidth/2, y - buttonHeight/2, widthIn, heightIn, buttonText);
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    public GuiBaseButton(int buttonId, ResourceLocation texture, int x, int y, int widthIn, int heightIn, String buttonText,int buttonWidth, int buttonHeight,
                         int u_unhovered,int v_unhovered,int u_hovered,int v_hovered) {
        super(buttonId, x - buttonWidth/2, y - buttonHeight/2, widthIn, heightIn, buttonText);
        this.texture = texture;

        this.u_unhovered = u_unhovered;
        this.u_hovered = u_hovered;
        this.v_unhovered = v_unhovered;
        this.v_hovered = v_hovered;

        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.renderEngine.bindTexture(texture);
            if(!overRideHover)
            {
                //check if the mouse is above it
                if (mouseX > x &&
                        mouseX < x + width &&
                        mouseY > y &&
                        mouseY < y + height) {
                    hovered = true;
                } else {
                    hovered = false;
                }
            }

            int u = u_unhovered;
            int v = v_unhovered;
            if(hovered)
            {
                u = u_hovered;
                v = v_hovered;
            }

            drawTexturedModalRect(x,y,u,v,width,height);
            overRideHover = false;
        }
    }
    
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks, int u, int v)
    {
        if (this.visible)
        {
            mc.renderEngine.bindTexture(texture);
            if(!overRideHover)
            {
                //check if the mouse is above it
                if (mouseX > x &&
                        mouseX < x + width &&
                        mouseY > y &&
                        mouseY < y + height) {
                    hovered = true;
                } else {
                    hovered = false;
                }
            }
    
            //if hovered over change the mainTexture
            v = v_unhovered;
            if (hovered)
            {
                v = v_hovered;
            }
            drawTexturedModalRect(x,y,u,v,width,height);
            overRideHover = false;
        }
    }
    
    
    public void setOverRideHover (boolean override)
    {
        this.overRideHover = override;
    }
    
    public void setHovered(boolean hovered)
    {
        this.hovered = hovered;
    }
}
