package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.player.IsometricCamera;

public class ModGuiIngame
{
    public static int getSubtractedSelectedItemNamePosY()
    {
        if(IsometricCamera.isPlayerInIsometricPerspective())
            return 40;
        return 0;
    }
}
