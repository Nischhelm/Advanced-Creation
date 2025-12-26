package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.handler.ConfigurationHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class RenderSelectionHighlight
{

    public static void drawBlueBlockHighlight(EntityPlayer entityplayer, BlockPos drawPos, float partialTicks)
    {
        drawSelectionBlockOutline(entityplayer, drawPos, partialTicks,  0,0,1f);
    }

    public static void drawFixedBlockHighlight(EntityPlayer entityplayer, BlockPos drawPos, float partialTicks)
    {
        drawSelectionBlockOutline(entityplayer, drawPos, partialTicks, 0.7F,0.7F,0.7F);
    }

    public static void drawGreenBlockHighlight(EntityPlayer entityplayer, float partialTicks, BlockPos drawPos)
    {
        drawSelectionBlockOutline(entityplayer, drawPos, partialTicks, 0,1f,0);
    }

    public static void drawWhiteBlockHighlight(EntityPlayer entityplayer, BlockPos normalHitPos, float partialTicks)
    {
        drawSelectionBlockOutline(entityplayer, normalHitPos, partialTicks, 1.0F,1.0F,1.0F);
    }

    public static void drawRedBlockHighlight(EntityPlayer entityplayer, BlockPos deletePos, float partialTicks)
    {
        drawSelectionBlockOutline(entityplayer, deletePos, partialTicks, 1.0F,0.0F,0.0F);
    }

    public static void drawSelectionBlockOutline(EntityPlayer entityplayer, BlockPos drawPos, float partialTicks,float r,float g, float b)
    {
        float brightness = (float)(ConfigurationHandler.general.SELECTION_HIGHLIGHT_BRIGHTNESS/100.f);
        RenderPreview.drawBlockOutline(entityplayer, partialTicks, drawPos, r*brightness,g*brightness,b*brightness,0.4f);
    }

    public static void drawFadingBlocksToPos(EntityPlayer entityplayer, Float partialTicks, BlockPos startPos, int relEndPosX, int relEndPosY, int relEndPosZ)
    {
        float newColor = 0.8f;

        int X = 0;
        int Y = 0;
        int Z = 0;

        float limit = 0.3F;
        float change = 0.04F;


        for(int x = 0; x < Math.abs(relEndPosX);x++)
        {
            if(relEndPosX < 0)
                X--;
            else
                X++;

            drawSelectionBlockOutline(entityplayer, startPos.add(X,Y,Z), partialTicks, newColor,newColor,newColor);
            if(newColor <= limit)
                newColor = limit;
            else
                newColor -= change;
        }
        for(int z = 0; z < Math.abs(relEndPosZ);z++)
        {
            if(relEndPosZ < 0)
                Z--;
            else
                Z++;

            drawSelectionBlockOutline(entityplayer, startPos.add(X,Y,Z), partialTicks, newColor,newColor,newColor);
            if(newColor <= limit)
                newColor = limit;
            else
                newColor -= change;
        }
        for(int y = 0; y < Math.abs(relEndPosY);y++)
        {
            if(relEndPosY < 0)
                Y--;
            else
                Y++;

            drawSelectionBlockOutline(entityplayer, startPos.add(X,Y,Z), partialTicks, newColor,newColor,newColor);
            if(newColor <= limit)
                newColor = limit;
            else
                newColor -= change;
        }
    }
}
