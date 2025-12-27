package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiFreeToggleButton extends GuiFreeButton
{
    private int u_a;    //u when button is active
    private int v_a;    //v when button is active
    private int u_a_h;  //u when button is active and hovered
    private int v_a_h;  //v when button is active and hovered

    private int u;    //u when button is inactive
    private int v;    //v when button is inactive
    private int u_h;  //u when button is inactive and hovered
    private int v_h;  //v when button is inactive and hovered


    public GuiFreeToggleButton(int buttonId, ResourceLocation texture, int x, int y, int widthIn, int heightIn, int u, int v, int u_h, int v_h,int u_a, int v_a, int u_a_h, int v_a_h)
    {
        super(buttonId, texture, x, y, widthIn, heightIn, u, v, u_h, v_h);
        this.u_a = u_a;
        this.v_a = v_a;
        this.u_a_h = u_a_h;
        this.v_a_h = v_a_h;
        this.u = this.u_unhovered;
        this.v = this.v_unhovered;
        this.u_h = this.u_hovered;
        this.v_h = this.v_hovered;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        boolean prevActiveStatus = this.active;
        boolean pressed = super.mousePressed(mc, mouseX, mouseY);
        if(pressed)
            this.active = !prevActiveStatus;
        else
            this.active = prevActiveStatus;
        return pressed;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if(this.active)
        {
            this.u_unhovered = this.u_a;
            this.v_unhovered = this.v_a;
            this.u_hovered = this.u_a_h;
            this.v_hovered = this.v_a_h;
        }
        else
        {
            this.u_unhovered = this.u;
            this.v_unhovered = this.v;
            this.u_hovered = this.u_h;
            this.v_hovered = this.v_h;
        }

        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

}
