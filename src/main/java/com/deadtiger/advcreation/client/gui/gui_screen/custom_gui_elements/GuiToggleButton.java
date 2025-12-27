package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

public class GuiToggleButton extends GuiCustomButton
{
    
    
    public GuiToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
    
    @Override
    protected int getHoverState(boolean mouseOver)
    {
        if(active)
            return 2;
        else
            return super.getHoverState(mouseOver);
        
    }
}
