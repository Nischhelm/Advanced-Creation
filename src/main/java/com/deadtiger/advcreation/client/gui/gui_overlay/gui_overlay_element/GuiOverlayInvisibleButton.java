package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * An invisible button that doesn't have its own texture but can be clicked and interacted with
 * From MC1.12-alpha1.3 it is used as buttons for the minecraft inventory quick select toolbar replacing the normal scrolling.
 *
 */
public class GuiOverlayInvisibleButton extends GuiOverlayBaseButton
{
    public GuiOverlayInvisibleButton(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height) {
        super(texture, x, y, textureX, textureY, width, height);
    }

    @Override
    public void draw(Gui gui, int currZ, long time) {
    
    }
}
