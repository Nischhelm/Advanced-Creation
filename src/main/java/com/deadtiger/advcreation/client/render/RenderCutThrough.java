package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCutThrough
{

    public static void handleCutthroughRendering()
    {
        if (ModBlockRendererDispatcher.cuttThroughOn)
        {
            ModBlockRendererDispatcher.wasCutThrough = true;
            Minecraft mc = Minecraft.getMinecraft();
            if (Minecraft.getMinecraft().world != null)
            {
                int playerX = mc.player.getPosition().getX();
                int playerY = mc.player.getPosition().getY();
                int playerZ = mc.player.getPosition().getZ();

                int diffBlock = 40;
                mc.world.markBlockRangeForRenderUpdate(playerX - diffBlock, playerY - diffBlock, playerZ - diffBlock, playerX + diffBlock, playerY + diffBlock, playerZ + diffBlock);
            }
        }
        else if (ModBlockRendererDispatcher.wasCutThrough)
        {
            //if cut-through vision was on before, update a lot of blocks in the the vicinity of the player to
            //return them back to normal
            Minecraft mc = Minecraft.getMinecraft();
            if (Minecraft.getMinecraft().world != null)
            {
                int playerX = mc.player.getPosition().getX();
                int playerY = mc.player.getPosition().getY();
                int playerZ = mc.player.getPosition().getZ();

                int diffBlock = 6 * 24;
                mc.world.markBlockRangeForRenderUpdate(playerX - diffBlock, 5, playerZ - diffBlock, playerX + diffBlock, 250, playerZ + diffBlock);
            }
            ModBlockRendererDispatcher.wasCutThrough = false;
        }
        else if (ConfigurationHandler.general.CONSTANT_TERRAIN_REFRESH && Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null)
        {
            Minecraft mc = Minecraft.getMinecraft();

            int playerX = mc.player.getPosition().getX();
            int playerY = mc.player.getPosition().getY();
            int playerZ = mc.player.getPosition().getZ();

            int diffBlock = 8;
            mc.world.markBlockRangeForRenderUpdate(playerX - diffBlock, playerY - diffBlock, playerZ - diffBlock, playerX + diffBlock, playerY + diffBlock, playerZ + diffBlock);

        }
        else if (ModBlockRendererDispatcher.refreshTerrain)
        {
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
            ModBlockRendererDispatcher.refreshTerrain = false;
        }
    }
}
