package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ModBlockRendererDispatcher
{
    public static boolean cuttThroughOn = false;
    public static boolean wasCutThrough = false;
    
    public static boolean refreshTerrain = false;

    
    public static boolean customRenderBlock(BlockRendererDispatcher blockrendererdispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRendererIn)
    {
        if(cuttThroughOn)
        {
            //if the camera is facing up draw the top half of the world
            boolean drawn = true;
            if(IsometricCamera.CAMERA_LOOK_VECTOR != null && IsometricCamera.CAMERA_LOOK_VECTOR.y < 0)
                drawn = pos.getY() < Minecraft.getMinecraft().player.getPosition().getY()+2;
            else
                drawn = pos.getY() > Minecraft.getMinecraft().player.getPosition().getY();

            if (drawn) {
                return blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
            }
            else
            {
                return false;
            }
        }
        else
            return blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
        
    }
    
}
