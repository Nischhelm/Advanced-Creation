package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiCustomWindow extends Gui
{
    protected static final ResourceLocation WINDOW_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/custom_windows.png");
    /** Button width in pixels */
    public int width;
    /** Button height in pixels */
    public int height;
    /** The x position of this control. */
    public int x;
    /** The y position of this control. */
    public int y;

   
    /** Hides the button completely if false. */
    public boolean visible;
    public GuiCustomWindow(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height= height;
        visible = true;
    }
    
    public void drawWindow(Minecraft mc)
    {
        if(this.visible)
        {
            mc.getTextureManager().bindTexture(WINDOW_TEXTURES);
    
            this.drawTexturedModalRect(this.x, this.y, 0,0, this.width , this.height);
            this.drawTexturedModalRect(this.x+(this.width-5) ,this.y,251,0,5,this.height-5);
            this.drawTexturedModalRect(this.x,this.y+(this.height-5),0,251,this.width-5,5);
            this.drawTexturedModalRect(this.x +(this.width-5),this.y+(this.height-5),251,251,5,5);
        }
    }
    
}
