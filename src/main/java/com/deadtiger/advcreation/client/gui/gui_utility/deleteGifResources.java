package com.deadtiger.advcreation.client.gui.gui_utility;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiHelpScreenVisual;
import net.minecraft.client.Minecraft;

public class deleteGifResources implements Runnable
{
    @Override
    public void run()
    {
        if(Minecraft.getMinecraft().currentScreen instanceof GuiHelpScreenVisual)
        {
            Minecraft.getMinecraft().currentScreen.onGuiClosed();
        }
    }
}
