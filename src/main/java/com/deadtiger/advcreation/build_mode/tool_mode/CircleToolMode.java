package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumFillMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.utility.shape_creator.CircleCreator;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import scala.reflect.internal.Trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CircleToolMode extends BaseToolMode
{
    private final ArrayList<Vec3d> previewCircleHelpLineVecPoints = new ArrayList<>();

    public CircleToolMode()
    {
        this.finalRightClick = 1;
        this.toolModeName = "Circle Mode";
        this.buttonText = "CIRCLE";
        this.tooltipText = "Draw/Delete Circle";
        this.identificationIndex = 5;
    }

    @Override
    public void addNewBlockRightClick2(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {

    }

    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        if(BuildMode.MOUSE_X_OFFSET != 0 || BuildMode.MOUSE_Y_OFFSET != 0 || BuildMode.MOUSE_Z_OFFSET != 0)
            BuildMode.clearMouseOffset();

        normalHitPos = new BlockPos(BuildMode.START_VEC.add(BuildMode.LINE_VEC));
        this.addPositionToGuiOverlay(normalHitPos);
        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        CircleCreator.INSTANCE.init();

        // has 1 of coordinates of the fill Vectors in the same order as the fillVectors
        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<Vec3d,TemplateBlock>();
        BuildMode.FILL_VECTOR_DICT = new HashMap<>();
        
        //the initial radiusVec
        Vec3d radiusVec = BuildMode.LINE_VEC;
        double radiusLength = radiusVec.lengthVector();
    
        //constraining the max radius(difflenght)
        if (radiusLength > ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS) {
            double scale = ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS / radiusLength;
            radiusLength = ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS;
            radiusVec = radiusVec.scale(scale);
        }
    
        // Jumps to one of the nice circles in the mapRadiusLengthToRadialVector HashMap when the length of the radius is close to it
        // These are recorded in the XZ plane and are transformed to other planes when necessary
        if (CircleCreator.INSTANCE.jumpToHardcodedRadius) {
            if (CircleCreator.INSTANCE.isInsideOfHardcodedRadiusRange(radiusLength))
            {
                radiusLength = CircleCreator.INSTANCE.getClosestHardcodedCircleLength(radiusLength);
                radiusVec = CircleCreator.INSTANCE.transformHardcodedCircleToCurrDir(radiusVec, radiusLength,BuildMode.WORK_DIRECTION_MODE);
                BuildMode.setColor(CircleCreator.INSTANCE.jumpToHardcodedRadiusColor);
            } else
                BuildMode.setColor(CircleCreator.INSTANCE.freeRadiusColor);
        } else
            BuildMode.setColor(CircleCreator.INSTANCE.freeRadiusColor);
    
        //logic to add a point to the middleCircleDicts that allows to save nice circle sizes when leftclicked
        if (CircleCreator.INSTANCE.addHardcodedRadius) {
            CircleCreator.INSTANCE.addHardcodedRadius(radiusVec, radiusLength);
        }
    
        if (radiusLength > 1.0)
        {
            ArrayList<Double> singleBlockKeys = new ArrayList<>();
            ArrayList<BlockPos> positionsList = CircleCreator.INSTANCE.generateCirclePositions( newStartVec, radiusVec, radiusLength,previewCircleHelpLineVecPoints,BuildMode.WORK_DIRECTION_MODE);

            for (BlockPos pos: positionsList)
            {
                CircleCreator.INSTANCE.placePosition(pos,BuildMode.START_BLOCK, singleBlockKeys,blockDict,(BuildMode.FILL_MODE == EnumFillMode.FILL),BuildMode.FILL_VECTOR_DICT,BuildMode.WORK_DIRECTION_MODE,BuildMode.START_VEC);
            }
    
            if(BuildMode.FILL_MODE == EnumFillMode.FILL)
            {
                //get all the keys for which there was no second block and place them too
                for(double key: singleBlockKeys)
                {
                    PlacementHelper.placePositionInDict(new BlockPos(BuildMode.FILL_VECTOR_DICT.get(key).startVec),BuildMode.START_BLOCK,blockDict);
                }
                BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
            }
            else
            {
                if(PlacementHelper.isWireRailOrNeedsConnection(BuildMode.getStartBlock()))
                {
                    ArrayList<TemplateBlock> finalCircleBlocks = adjustCircleForWireRailsOrNeedsConnection(newStartVec, blockDict);
                    BuildMode.CURR_TOOL_BLOCKS.addAll(finalCircleBlocks);
                }
                else
                    BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
            }
        }
        else
            BuildMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(BuildMode.START_BLOCK.getFace(),new BlockPos(newStartVec),BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity()));
    }

    private ArrayList<TemplateBlock> adjustCircleForWireRailsOrNeedsConnection(Vec3d newStartVec, HashMap<Vec3d, TemplateBlock> blockDict)
    {
        HashMap<Double,TemplateBlock> radialAngleToBlock = new HashMap<>();

        for (Vec3d vec: blockDict.keySet())
        {
            Vec3d blockMiddleVec = vec.addVector(0.5,0.5,0.5);
            double s = newStartVec.distanceTo(blockMiddleVec);
            double a = newStartVec.z - blockMiddleVec.z;
            double angle = Math.acos(a/s);
            if(vec.x < newStartVec.x)
                angle = 2*Math.PI - angle;
            radialAngleToBlock.put(angle, blockDict.get(vec));
        }

        ArrayList<TemplateBlock> finalCircleBlocks = new ArrayList<>();
        List<Double> angles = radialAngleToBlock.keySet().stream().sorted().collect(Collectors.toList());

        //add the first block to the final list as starting point
        int index = 0;
        Double prevAngle = angles.get(index);
        TemplateBlock prevBlock = radialAngleToBlock.get(prevAngle);
        finalCircleBlocks.add(prevBlock);
        boolean keepGoing = true;

        while(index < angles.size() && keepGoing)
        {
            int jumpIndex = 1;
            if(index + jumpIndex < angles.size())
            {
                double currAngle = angles.get(index+jumpIndex);
                TemplateBlock currBlock = radialAngleToBlock.get(currAngle);
                BlockPos currPos = currBlock.getBlockPos();
                double minDist = prevBlock.getBlockPos().getDistance(currPos.getX(),currPos.getY(),currPos.getZ());
                double minDistToCenter = BuildMode.getStartBlock().getBlockPos().getDistance(currPos.getX(),currPos.getY(),currPos.getZ());
                for(int i = 2; i < 6;i++)
                {
                    if(index+i < angles.size())
                    {
                        currAngle = angles.get(index+i);
                        currBlock = radialAngleToBlock.get(currAngle);
                        currPos = currBlock.getBlockPos();
                        double currDist = prevBlock.getBlockPos().getDistance(currPos.getX(),currPos.getY(),currPos.getZ());

                        if(currDist < minDist)
                        {
                            minDist = currDist;
                            minDistToCenter = BuildMode.getStartBlock().getBlockPos().getDistance(currPos.getX(),currPos.getY(),currPos.getZ());
                            if(minDist <= 1.0)
                                jumpIndex = i;
                        }
                        else if(currDist == minDist)
                        {
                            double currDistToCenter = BuildMode.getStartBlock().getBlockPos().getDistance(currPos.getX(),currPos.getY(),currPos.getZ());
                            if(currDistToCenter > minDistToCenter)
                            {
                                minDist = currDist;
                                minDistToCenter = currDistToCenter;
                                if(minDist <= 1.0)
                                    jumpIndex = i;

                            }
                        }
                    }
                    else
                        break;

                }
                if(minDist > 1.0)
                    System.out.println(index + " angle " + angles.get(index) + " jumpindex " + jumpIndex + " minDist " + minDist);

                if(minDist <= 1.0)
                {
                    index += jumpIndex;
                    prevAngle = angles.get(index);
                    prevBlock = radialAngleToBlock.get(prevAngle);
                    finalCircleBlocks.add(prevBlock);
                }
                else if(index == 0)
                {
//                    //if the first block is a bad block remove it from the list and take the block that is at index 0 in its place
//                    angles.remove(index);
//                    finalCircleBlocks.remove(prevBlock);
//                    prevAngle = angles.get(index);
//                    prevBlock = radialAngleToBlock.get(prevAngle);
                    finalCircleBlocks.clear();
                    break;
                }
                else
                {
                    //if the previously added block does not have a block horizontal to it delete that block and go one step back
                    //by removing the offending block from the angles list you are preventing that block from ever getting back into the algorithm
                    angles.remove(index);
                    index -=  jumpIndex;
                    finalCircleBlocks.remove(prevBlock);
                    prevAngle = angles.get(index);
                    prevBlock = radialAngleToBlock.get(prevAngle);
                }


            }
            else
                keepGoing = false;

        }

        //check the distance between the first and the last block to see if they are horizontally connected
        //if they are not remove the one farthest from the middle
        if(finalCircleBlocks.size() > 1)
        {
            BlockPos firstPos = finalCircleBlocks.get(0).getBlockPos();
            BlockPos lastPos = finalCircleBlocks.get(finalCircleBlocks.size()-1).getBlockPos();
            double distLastAndFirst = firstPos.getDistance(lastPos.getX(),lastPos.getY(),lastPos.getZ());
            if(distLastAndFirst > 1.0)
            {
                double firstDistToCenter = BuildMode.getStartBlock().getBlockPos().getDistance(firstPos.getX(),firstPos.getY(),firstPos.getZ());
                double lastDistToCenter = BuildMode.getStartBlock().getBlockPos().getDistance(lastPos.getX(),lastPos.getY(),lastPos.getZ());
                if(firstDistToCenter >= lastDistToCenter)
                   finalCircleBlocks.remove(0);
                else
                    finalCircleBlocks.remove(finalCircleBlocks.size()-1);
            }
        }




        return finalCircleBlocks;
    }

    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
    
        if(BuildMode.START_VEC != null) {
            //draws the cross indicating the mouse position
            Vec3d middleVec = BuildMode.START_VEC;
            RenderTemplate.drawMiddleCross(entityplayer, partialTicks, middleVec);

            if (BuildMode.LINE_VEC != null)
            {
                // draw the lines of the circle
                Vec3d newCursorVec = RenderTemplate.drawCircleHelplines(entityplayer, partialTicks, middleVec,previewCircleHelpLineVecPoints,BuildMode.RED ,BuildMode.GREEN ,BuildMode.BLUE );

                //draw the radius line
                RenderTemplate.drawLine(middleVec.x, middleVec.y, middleVec.z, newCursorVec.x,
                        newCursorVec.y, newCursorVec.z, entityplayer, 0, partialTicks,
                        BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);

                //draw the line of the fill vectors
                for (FillVector fillVec : BuildMode.FILL_VECTOR_DICT.values()) {
                    if (fillVec.startVec != null && fillVec.endVec != null)
                        RenderTemplate.drawLine(fillVec.startVec, fillVec.endVec, entityplayer, 0,
                            partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
                }
            }
        }
    }

    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        int hashcode = 0;
        if(BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
            {
                if(BuildMode.DELETE_MODE || BuildMode.getStartBlock().getBlockState().getBlock() instanceof BlockAir)
                {
                    RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                        face, block.getBlockPos());
                
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,1.0F,0.0F,0.0F);
                }
                else
                {
                    RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity())
                        , entityplayer, partialTicks, face);
                }

                if(BuildMode.START_BLOCK != null)
                    RenderSelectionHighlight.drawGreenBlockHighlight(entityplayer,partialTicks,BuildMode.START_BLOCK.getBlockPos());



                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();

                hashcode += ~~currHashcode;
            }
            BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
        return hashcode;
    }

    @Override
    protected void drawMiddleBlockHighlight(EntityPlayer entityplayer, float partialTicks)
    {
        super.drawMiddleBlockHighlight(entityplayer, partialTicks);
    }

    @Override
    public boolean hasNoAlterPositionMode()
    {
        if(BuildMode.RIGHT_CLICK_NUMBER > 0)
            return true;
        return super.hasNoAlterPositionMode();
    }

    @Override
    public EnumPosOrder[] getPosOrder()
    {
        return new EnumPosOrder[]{EnumPosOrder.START_POS,EnumPosOrder.END_POS};
    }
}
