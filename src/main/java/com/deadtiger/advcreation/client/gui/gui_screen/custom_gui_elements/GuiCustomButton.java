package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public abstract class GuiCustomButton extends GuiButton
{
    FontRenderer fontRenderer;
    public boolean active = false;
    
    public String name = "";
    public String tooltip = "";
    
    public GuiCustomButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
        fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }
    
    public GuiCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }
    
    
    public void drawTooltip(int mouseX,int mouseY, int screenWidth, int screenHeight)
    {
        CustomGuiUtils.drawHoveringText(name + ": " + tooltip, mouseX-7, mouseY,screenWidth,screenHeight,-1,fontRenderer);
    }
    
    public void setNameTooltip(String name,String tooltip)
    {
        this.name = name;
        this.tooltip = tooltip;
    }
}
