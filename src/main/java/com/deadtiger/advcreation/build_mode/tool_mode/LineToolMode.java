package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LineToolMode extends BaseToolMode
{
    public LineToolMode()
    {
        this.finalRightClick = 1;
        this.toolModeName = "Line Mode";
        this.buttonText = "LINE";
        this.tooltipText = "Draw/Delete Line";
        this.identificationIndex = 2;
    }
    
    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec,Vec3d newStartVec)
    {
        try
        {
            normalHitPos = new BlockPos(newStartVec.add(BuildMode.LINE_VEC));
            BuildMode.LINE_VEC = BuildMode.LINE_VEC.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
            drawBlockLine(newStartVec, BuildMode.LINE_VEC);

            //make sure you can put one block with the line tool
            if( BuildMode.START_BLOCK.getBlockPos().equals(BuildMode.LAST_BLOCK)&& BuildMode.CURR_TOOL_BLOCKS.isEmpty())
                BuildMode.CURR_TOOL_BLOCKS.add(BuildMode.getStartBlock());

            BlockPos offsetPos = new BlockPos(newStartVec.add(BuildMode.LINE_VEC));
            this.addPositionToGuiOverlay(new BlockPos(offsetPos));
    //        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
            GuiOverlayManager.setPlacePointedCoordinate(offsetPos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void addNewBlockRightClick2(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {

    }

    @Override
    public EnumPosOrder[] getPosOrder()
    {
        return new EnumPosOrder[]{EnumPosOrder.START_POS,EnumPosOrder.END_POS};
    }
}
