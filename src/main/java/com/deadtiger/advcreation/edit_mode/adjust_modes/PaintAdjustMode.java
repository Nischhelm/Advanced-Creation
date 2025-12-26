package com.deadtiger.advcreation.edit_mode.adjust_modes;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.shape_creator.BaseShapeCreator;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.shape_creator.CircleRectangleCreator;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;

public class PaintAdjustMode extends BaseAdjustMode
{
    //good positions to create nice circles
    private final ArrayList<Vec3d> previewCircleHelpLineVecPoints = new ArrayList<>();
    public EnumDirectionMode paintDirection = EnumDirectionMode.XZ;
    public boolean sqaure = false;
    
    //of all the block to be painted are the same as the brush block then don't change anything
    private Boolean allWorldBlocksTheSameAsBrush = false;
    
    public PaintAdjustMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "Paint Mode";
        this.buttonText = "PAINT";
        this.tooltipText = "Paint over existing blocks with current block";
        this.identificationIndex = 2;

        addLegendEntry(LevelAdjustMode.existingLowerLevelColor,"Will be deleted");
        addLegendEntry(LevelAdjustMode.changedColor,"Will be painted");
        addLegendEntry(LevelAdjustMode.unchangedColor,"Do not change");

    }
    
    @Override
    public int addToRightClickNumber() {
        return 0;
    }
    
    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber) {
        if (rightClickNumber >= this.finalRightClick && !allWorldBlocksTheSameAsBrush && EditMode.START_BLOCK.getBlockState() != null)
            return true;

        return false;
    }
    
    @Override
    public boolean startCancelBuilding(int rightClickNumber)
    {
//        if(!SculptMode.deleteMode && !allWorldBlocksTheSameAsBrush)
        if(EditMode.DELETE_MODE)
            return super.startCancelBuilding(rightClickNumber);
        else if(!allWorldBlocksTheSameAsBrush && EditMode.START_BLOCK.getBlockState() != null)
            return super.startCancelBuilding(rightClickNumber);
        return false;
    }
    
    @Override
    public void activateDeleteMode()
    {
        Vec3d invFaceDirVec = Vec3d.ZERO;
        if(EditMode.START_BLOCK != null)
            EditMode.setStartBlock(new TemplateBlock(EditMode.getStartBlock().getFace(), EditMode.getStartBlock().getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), EditMode.getStartBlock().getBlockState(),EditMode.getStartBlock().getTileEntity()));
        else
            EditMode.setStartBlock(new TemplateBlock(EnumFacing.NORTH,BlockPos.ORIGIN.add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), Blocks.AIR.getDefaultState()));

        if(EditMode.getSTARTVEC() == null)
            EditMode.setSTARTVEC(Vec3d.ZERO);
        else
            EditMode.setSTARTVEC(EditMode.getSTARTVEC().add(invFaceDirVec));
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        Vec3d invFaceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-EditMode.CURR_HITBLOCK_WIDTH);
        
        if(face.equals(EnumFacing.NORTH) || face.equals(EnumFacing.SOUTH))
            paintDirection = EnumDirectionMode.XY;
        else if(face.equals(EnumFacing.WEST) || face.equals(EnumFacing.EAST))
            paintDirection = EnumDirectionMode.ZY;
        else
            paintDirection = EnumDirectionMode.XZ;
        
        EditMode.setStartBlock(new TemplateBlock(face,block.getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), block.getBlockState(),block.getTileEntity()));
        EditMode.setSTARTVEC(hitVec.add(invFaceDirVec));
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block,hitVec);
    }
    
    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec) {
        CircleRectangleCreator.INSTANCE.init();

        normalHitPos = new BlockPos(newStartVec);
        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setStartPos(normalHitPos);

        //the initial radiusVec
        Vec3d radiusVec = new Vec3d(radius,0,0);
        EditMode.LINEVEC = radiusVec;
        double radiusLength = radiusVec.lengthVector();
        
        // Jumps to one of the nice circles in the middleCircleDict when the length is of the radius is close to it
        // These are recorded in the XZ plane and are transformed to other planes when necessary
        sqaure = false;
        if (CircleRectangleCreator.INSTANCE.jumpToHardcodedRadius) {
            if (CircleRectangleCreator.INSTANCE.isInsideOfHardcodedRadiusRange(radiusIndex))
            {
                radiusLength = CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.get(radiusIndex);
                radiusVec = CircleRectangleCreator.INSTANCE.transformHardcodedCircleToCurrDir(radiusVec, radiusLength,paintDirection);
                if(radiusVec == null)
                    sqaure = true;
                EditMode.setColor(CircleRectangleCreator.INSTANCE.jumpToHardcodedRadiusColor);
            } else
                EditMode.setColor(CircleRectangleCreator.INSTANCE.freeRadiusColor);
        } else
            EditMode.setColor(CircleRectangleCreator.INSTANCE.freeRadiusColor);
        
        //logic to add a point to the middleCircleDicts that allows to save nice circle sizes when leftclicked
        if (BaseShapeCreator.addHardcodedRadius && !sqaure)
            CircleRectangleCreator.INSTANCE.addHardcodedRadius(radiusVec, radiusLength);

        HashMap<Vec3d, TemplateBlock> blockDict = new HashMap<Vec3d, TemplateBlock>();
        if (!sqaure)
        {
            //draw a circle with a radius
            ArrayList<Double> singleBlockKeys = new ArrayList<>();
            ArrayList<BlockPos> positionsList = CircleRectangleCreator.INSTANCE.generateCirclePositions(newStartVec, radiusVec, radiusLength,previewCircleHelpLineVecPoints, paintDirection);

            for (BlockPos pos: positionsList)
            {
                CircleRectangleCreator.INSTANCE.placePosition(pos,EditMode.START_BLOCK, singleBlockKeys,blockDict,true,EditMode.FILL_VECTOR_MAP,paintDirection,EditMode.NEW_START_VEC);
            }

            //get all the keys for which there was no second block and place them too
            for(double key: singleBlockKeys)
            {
                PlacementHelper.placePositionInDict(new BlockPos(EditMode.FILL_VECTOR_MAP.get(key).startVec),EditMode.START_BLOCK,blockDict);
            }
        }
        else
        {
            //draw a sqaure
            ArrayList<BlockPos> blockPosList = CircleRectangleCreator.INSTANCE.generateRectanglePositions(newStartVec, radiusLength,paintDirection);
            for (BlockPos pos: blockPosList)
            {
                PlacementHelper.placePositionInDict(pos,EditMode.START_BLOCK, blockDict);
            }
        }
        ArrayList<TemplateBlock> toGround = new ArrayList<>(blockDict.values());
        EnumFacing face = EditMode.START_BLOCK.getFace();
        HelpFunctions.updateListToAirBlockAboveFirstSolidBlockInDir(toGround,face,2);
        if(EditMode.DELETE_MODE)
            fillCurrToolBlocksWithAirBlocks(toGround,face);
        else
            copyPropertiesOfCurrentWorldBlocks(toGround, face);
    }




    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Float partialTicks) {
        
        if(EditMode.STARTVEC != null)
        {
            //draws the cross indicating the mouse position
            Vec3d middleVec = EditMode.STARTVEC;
            RenderTemplate.drawMiddleCross(entityplayer, partialTicks, middleVec);

            if (EditMode.LINEVEC != null && !sqaure)
            {
                // draw the lines of the circle
                Vec3d newCursorVec = RenderTemplate.drawCircleHelplines(entityplayer, partialTicks, middleVec,previewCircleHelpLineVecPoints,EditMode.RED,EditMode.GREEN,EditMode.BLUE);

                //draw the radius line
                RenderTemplate.drawLine(middleVec.x, middleVec.y, middleVec.z, newCursorVec.x,
                        newCursorVec.y, newCursorVec.z, entityplayer, 0, partialTicks,
                        EditMode.RED, EditMode.GREEN, EditMode.BLUE);

                //draw the line of the fill vectors
//                for (FillVector fillVec : EditMode.FILL_VECTOR_MAP.values())
//                {
//                    if (fillVec.startVec != null && fillVec.endVec != null)
//                        RenderTemplate.drawLine(fillVec.startVec, fillVec.endVec, entityplayer, 0,
//                                partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
//                }
            }
        }
    }
    
    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        int hashcode = 0;
        if(EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            EditMode.DRAW_PREVIEW_OUTLINE_ONLY = true;
            for (TemplateBlock block : EditMode.CURR_PREVIEW_BLOCKS)
            {
                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                    face, block.getBlockPos());

                if(EditMode.DELETE_MODE)
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,1.0F,0.0F,0.1F);
                else
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,0.1F,1.0F,0.0F);

                if(block.getBlockState() == null )
                     block.setBlockState(Blocks.AIR.getDefaultState());

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();;
                hashcode += ~~currHashcode;
            }

            EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
        return hashcode;
    }


    private void copyPropertiesOfCurrentWorldBlocks(ArrayList<TemplateBlock> toGround, EnumFacing face)
    {
        World world = Minecraft.getMinecraft().world;
        //is true while there is no different world block found
        Boolean NoWorldBlocksTheDiffFound = true;
        for (TemplateBlock tempBlock: toGround)
        {
            BlockPos pos  = tempBlock.getBlockPos();
            IBlockState currState = world.getBlockState(pos);
            IBlockState iblockstate3 = null;
            if ( face.equals(EnumFacing.SOUTH))
                iblockstate3 = world.getBlockState(pos.south());
            else if (face.equals(EnumFacing.NORTH))
                iblockstate3 = world.getBlockState(pos.north());
            else if (face.equals(EnumFacing.EAST))
                iblockstate3 = world.getBlockState(pos.east());
            else if (face.equals(EnumFacing.WEST))
                iblockstate3 = world.getBlockState(pos.west());
            else if (face.equals(EnumFacing.UP) )
                iblockstate3 = world.getBlockState(pos.up());
            else if (face.equals(EnumFacing.DOWN))
                iblockstate3 = world.getBlockState(pos.down());

            //when replacing stairs, slabs and fences you don't want regular blocks to also be replaced
            IBlockState startWorldState = world.getBlockState(EditMode.START_BLOCK.getBlockPos());
            boolean excludeActive = (startWorldState.getBlock() instanceof BlockSlab ||
                    startWorldState.getBlock() instanceof BlockStairs ||
                    startWorldState.getBlock() instanceof BlockFence);

            if(!isNotGroundMaterial(currState) && isNotGroundMaterial(iblockstate3))
            {
                boolean sameBlockClassAsStart = startWorldState.getBlock().getClass().equals(currState.getBlock().getClass());
                if(!excludeActive || sameBlockClassAsStart)
                {
                    if(tempBlock.getBlockState() != null)
                    {
                        //if you want to replace a block with the same block type all properties are copied
                        if(tempBlock.getBlockState().getBlock().getClass().equals(currState.getBlock().getClass()))
                        {
                            //the class of the current block is the same as
                            IBlockState newBlockstate = tempBlock.getBlockState();
                            for (IProperty key :currState.getPropertyKeys())
                            {
                                if(!(key.getName().equals("color")) && !(key.getName().equals("variant")))
                                {
                                    newBlockstate = newBlockstate.withProperty(key,currState.getValue(key));
                                }
                            }

                            if(!currState.equals(newBlockstate))
                                NoWorldBlocksTheDiffFound = false;

                            tempBlock.setBlockState(newBlockstate);
                        }
                        else
                            NoWorldBlocksTheDiffFound = false;
                    }
                    EditMode.CURR_TOOL_BLOCKS.add(tempBlock);
                }
            }
        }
        allWorldBlocksTheSameAsBrush =NoWorldBlocksTheDiffFound;
    }

    private void fillCurrToolBlocksWithAirBlocks(ArrayList<TemplateBlock> toGround, EnumFacing face)
    {
        for (TemplateBlock tempBlock: toGround)
        {
            BlockPos pos  = tempBlock.getBlockPos();
            World world = Minecraft.getMinecraft().world;
            IBlockState currState = world.getBlockState(pos);
            if(currState == Blocks.AIR.getDefaultState())
                continue;

            IBlockState iblockstate3 = null;
            if ( face.equals(EnumFacing.SOUTH))
                iblockstate3 = world.getBlockState(pos.south());
            else if (face.equals(EnumFacing.NORTH))
                iblockstate3 = world.getBlockState(pos.north());
            else if (face.equals(EnumFacing.EAST))
                iblockstate3 = world.getBlockState(pos.east());
            else if (face.equals(EnumFacing.WEST))
                iblockstate3 = world.getBlockState(pos.west());
            else if (face.equals(EnumFacing.UP) )
                iblockstate3 = world.getBlockState(pos.up());
            else if (face.equals(EnumFacing.DOWN))
                iblockstate3 = world.getBlockState(pos.down());

            if(!isNotGroundMaterial(currState) && isNotGroundMaterial(iblockstate3))
            {
                tempBlock.setBlockState(Blocks.AIR.getDefaultState());
                EditMode.CURR_TOOL_BLOCKS.add(tempBlock);
            }
        }
    }

    @Override
    public String getGuiOverlayMessage(boolean deleteMode)
    {

        if(deleteMode)
            return TextFormatting.RED + "Eraser" + TextFormatting.WHITE + " Mode";
        else
            return "Paint Mode";

    }
}
