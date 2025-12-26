package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiFreeButton extends GuiBaseButton
{
    public boolean active = false;

    public GuiFreeButton(int buttonId, ResourceLocation texture, int x, int y, int widthIn, int heightIn,int u, int v, int u_h, int v_h) {
        super(buttonId,texture, x, y, widthIn, heightIn,"", widthIn, heightIn,u,v,u_h,v_h);

    }


    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        boolean pressed = super.mousePressed(mc, mouseX, mouseY);
        this.active = pressed;
        
        return pressed;
        
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
}
