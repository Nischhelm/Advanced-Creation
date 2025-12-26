package com.deadtiger.advcreation.client.player;

import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.plugin.modded_classes.ModPlayerControllerMP;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SPPlayerProperties
{
    public static void updatePlayerProperties(TickEvent.PlayerTickEvent event)
    {
        boolean isometricPerspective;
        if(event.player == Minecraft.getMinecraft().player)
            isometricPerspective = IsometricCamera.isPlayerInIsometricPerspective();
        else
            isometricPerspective = !NetworkManager.isPlayerInFirstPerson(event.player.getName());


        if (isometricPerspective)
        {

            ModPlayerControllerMP.giveCustomReachDistance = true;
            //make the player clip through the ground and walls
            event.player.noClip = true;
            // make the player avatar invisible
            event.player.setInvisible(true);

            event.player.capabilities.disableDamage = true;
            event.player.setEntityInvulnerable(true);
            event.player.capabilities.allowFlying = true;

            GuiIngameForge.renderFood = false;
            Minecraft.getMinecraft().gameSettings.heldItemTooltips = false;
        }
        else
        {
            //if your in the firstperson view go back to the old ways
            Minecraft.getMinecraft().gameSettings.heldItemTooltips = true;
            event.player.noClip = false;
            if (!event.player.isCreative() && !event.player.isSpectator())
            {
                ModPlayerControllerMP.giveCustomReachDistance = false;
                event.player.setInvisible(false);
                event.player.capabilities.disableDamage = false;
                event.player.setEntityInvulnerable(false);
            }
            else
            {
                ModPlayerControllerMP.giveCustomReachDistance = ConfigurationHandler.general.FIRST_PERSON_INFINITE_REACH_IN_CREATIVE;
                event.player.setInvisible(false);
                event.player.capabilities.disableDamage = true;
                event.player.setEntityInvulnerable(true);
            }

        }
        event.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(ModPlayerControllerMP.getCustomReachDistance());
    }
}
