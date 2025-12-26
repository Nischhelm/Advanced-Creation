package com.deadtiger.advcreation.undo_actions;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

/***
 * Class to indicate a single action on the world by a player
 * should contain only TemplateBlocks with absolute world coordinates
 */
public class Action
{
    private ArrayList<TemplateBlock> previousBlockStates = new ArrayList<>();
    private boolean giganticAction = false;

    public void add(EnumFacing face, BlockPos blockPos, IBlockState blockState)
    {
        previousBlockStates.add(new TemplateBlock(face, blockPos, blockState, null));
    }

    public void add(EnumFacing face, BlockPos blockPos, IBlockState blockState, TileEntity tileEntity)
    {
        previousBlockStates.add(new TemplateBlock(face, blockPos, blockState, tileEntity));
    }

    public void add(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState)
    {
        previousBlockStates.add(new TemplateBlock(face, x_offset, y_offset, z_offset, blockState));
    }

    public void add(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState, TileEntity tileEntity)
    {
        previousBlockStates.add(new TemplateBlock(face, x_offset, y_offset, z_offset, blockState, tileEntity));
    }

    public void add(TemplateBlock templateBlock)
    {
        previousBlockStates.add(templateBlock);
    }

    public ArrayList<TemplateBlock> getPreviousBlockStates()
    {
        return previousBlockStates;
    }

    public boolean isGiganticAction()
    {
        return giganticAction;
    }

    public void setGiganticAction(boolean giganticAction)
    {
        this.giganticAction = giganticAction;
    }
}
