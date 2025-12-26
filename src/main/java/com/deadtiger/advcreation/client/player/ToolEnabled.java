package com.deadtiger.advcreation.client.player;

import com.deadtiger.advcreation.client.gui.gui_overlay.ToolDisableGuiOverlay;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.message.MessageUpdatePlayerSetting;
import net.minecraft.client.Minecraft;

public class ToolEnabled
{
    public static void toggleToolsEnabled()
    {
        ConfigurationHandler.general.TOOLS_ENABLED = !ConfigurationHandler.general.TOOLS_ENABLED;
        ToolDisableGuiOverlay.updateToolDisableTooltip();
        NetworkHandler.sendPlayerSettingsUpdateToServer(new MessageUpdatePlayerSetting(!IsometricCamera.isPlayerInIsometricPerspective(),!ConfigurationHandler.general.TOOLS_ENABLED, Minecraft.getMinecraft().player.getName()));
    }

}
