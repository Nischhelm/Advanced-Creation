package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.util.ResourceLocation;

/**
 * Looks like the normal minecraft menu but is able to be made with any width or height.
 * Like the GuiOverlayCustomButton is uses multiple elements to draw nice edges on the window.
 *
 */
public class GuiOverlayCustomWindow extends GuiOverlayBaseElement
{
    
    public GuiOverlayCustomWindow(int x, int y, int width, int height)
    {
        super(new ResourceLocation(Reference.MODID,"textures/gui/custom_windows.png" ),
        x, y,0, 0, width, height);

        GuiOverlayBaseElement rightEdge = new GuiOverlayBaseElement(texture,x+(width-5) ,y,0,251,0,5,height-5);
        GuiOverlayBaseElement bottomEdge = new GuiOverlayBaseElement(texture,x ,y+(height-5),0,0,251,width-5,5);
        GuiOverlayBaseElement rightBottomEdge = new GuiOverlayBaseElement(texture,x +(width-5),y+(height-5),0,251,251,5,5);
        addElement(rightEdge);
        addElement(bottomEdge);
        addElement(rightBottomEdge);
    }
    
}
