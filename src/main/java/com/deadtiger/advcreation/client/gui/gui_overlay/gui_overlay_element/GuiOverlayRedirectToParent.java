package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiOverlayRedirectToParent extends GuiOverlayBaseElement
{

    GuiOverlayBaseElement parent;

    public GuiOverlayRedirectToParent(GuiOverlayBaseElement parent, ResourceLocation texture, int x, int y, int z, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset) {
        super(texture, x, y, z, textureX, textureY, width, height, x_hoverOffset, y_hoverOffset, x_activeOffset, y_activeOffset);
        this.parent = parent;
    }

    public GuiOverlayRedirectToParent(GuiOverlayBaseElement parent, ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset) {
        this(parent,texture,x,y,0,textureX,textureY,width,height, x_hoverOffset,y_hoverOffset,x_activeOffset,y_activeOffset);
    }

    @Override
    public void activateTimedSelected(long time) {
        parent.activateTimedSelected(time);
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        parent.drawTooltip(mouseX, mouseY, screenWidth, screenHeight);
    }

    @Override
    public GuiOverlayBaseElement checkHoverOn(int mouseX, int mouseY) {
        return super.checkHoverOn(mouseX, mouseY);
    }

    @Override
    public void draw(Gui gui, long time) {
        super.draw(gui, time);
    }
}
