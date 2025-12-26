package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.Minecraft;

public class GuiCustomBigWindow extends GuiCustomWindow
{
    private int secondWidth = 5;
    private int secondHeight = 5;

    public GuiCustomBigWindow(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        if(width > 256)
        {
            secondWidth += (width- 256);
        }
        if(height > 256)
        {
            secondHeight += (height- 256);
        }
    }

    public void drawWindow(Minecraft mc)
    {
        if(this.visible)
        {
            mc.getTextureManager().bindTexture(WINDOW_TEXTURES);

            this.drawTexturedModalRect(this.x, this.y, 0,0, this.width , this.height);
            this.drawTexturedModalRect(this.x+(this.width-this.secondWidth) ,this.y,256-secondWidth,0,this.secondWidth,this.height-this.secondHeight);
            this.drawTexturedModalRect(this.x,this.y+(this.height-this.secondHeight),0,256-secondHeight,this.width-this.secondWidth,this.secondHeight);
            this.drawTexturedModalRect(this.x +(this.width-this.secondWidth),this.y+(this.height-this.secondHeight),256-secondWidth,256-secondHeight,this.secondWidth,this.secondHeight);
        }
    }
}
