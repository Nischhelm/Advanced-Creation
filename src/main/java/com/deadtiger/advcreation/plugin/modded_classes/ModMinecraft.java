package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplateInventoryScreenSimple;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.tutorial.Tutorial;

public class ModMinecraft
{
    
    public static void handleChangeCurrentSelectionToolbar(Minecraft mc,int k)
    {
        if(!IsometricCamera.isPlayerInIsometricPerspective())
            mc.player.inventory.changeCurrentItem(k);
    }
    
    public static void openCustomInventory(Tutorial tutorial, Minecraft mc)
    {
        if(IsometricCamera.isPlayerInIsometricPerspective())
        {

            if(AdvCreation.mode == EnumMainMode.PLACE)
            {
                mc.displayGuiScreen(new GuiTemplateInventoryScreenSimple());
                System.out.println("Open Custom inventory");
            }
            else
            {
                tutorial.openInventory();
                mc.displayGuiScreen(new GuiInventory(mc.player));
            }
        }
        else
        {
            tutorial.openInventory();
            mc.displayGuiScreen(new GuiInventory(mc.player));
        }
    }
    
}
