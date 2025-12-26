package com.deadtiger.advcreation.server.player;

import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.plugin.modded_classes.ModPlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MPPlayerProperties
{
    public static void updatePlayerProperties(TickEvent.PlayerTickEvent event)
    {
        if (!NetworkManager.isPlayerInFirstPerson(event.player.getName()))
        {
            if (event.player instanceof EntityPlayerMP){
                if(((EntityPlayerMP)event.player).interactionManager.isCreative()){
                    //make the player clip through the ground and walls
                    event.player.noClip = true;

                    // make the player avatar invisible
                    event.player.setInvisible(true);

                    ModPlayerControllerMP.giveCustomReachDistance = true;

                    event.player.capabilities.disableDamage = true;
                    event.player.setEntityInvulnerable(true);
                    event.player.capabilities.allowFlying = true;
                }
            }
        }
        else
        {
            //if your in the firstperson view go back to the old ways
            event.player.noClip = false;
            if (!event.player.isCreative() && !event.player.isSpectator())
            {
                event.player.setInvisible(false);
                event.player.capabilities.disableDamage = false;
                event.player.setEntityInvulnerable(false);

                ModPlayerControllerMP.giveCustomReachDistance = false;
            }
            else
            {
                event.player.setInvisible(false);
                event.player.capabilities.disableDamage = true;
                event.player.setEntityInvulnerable(true);

                ModPlayerControllerMP.giveCustomReachDistance = true;
            }

        }
        event.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(ModPlayerControllerMP.getCustomReachDistance());
//        if (event.player instanceof EntityPlayerMP)
//            ((EntityPlayerMP) event.player).interactionManager.setBlockReachDistance(ModPlayerControllerMP.getCustomReachDistance());

    }
}
