package com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen;

import com.deadtiger.advcreation.place_template.PlaceTemplateMode;

//a class to
public class GuiTemplaceInventoryScreenFunctionality
{
    //functional variables
    public static int displayed_index = 0;
    public static int selected_index = -1;


    public static void setSelected_index(int i)
    {
        selected_index = i;
        PlaceTemplateMode.updateCoordInfoDisplay();
    }

    public static int getSelected_index()
    {
        return selected_index;
    }
    
}