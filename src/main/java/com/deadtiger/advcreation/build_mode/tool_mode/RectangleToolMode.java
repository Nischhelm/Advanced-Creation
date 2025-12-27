package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumFillMode;
import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RectangleToolMode extends BaseToolMode
{
    public int withinBlockCount = 0;
    public Vec3d startInBlock = null;
    
    public RectangleToolMode()
    {
        this.finalRightClick = 2;
        this.toolModeName = "Rectangle Mode";
        this.buttonText = "RECTANGLE";
        this.tooltipText = "Draw/Delete Rectangle";
        this.identificationIndex = 3;
    }
    
    @Override
    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        if(BuildMode.DELETE_MODE)
        {
            Vec3d faceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-1);
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec).add(faceDirVec));
            BuildMode.updateCurrToolBlocks(block,hitVec.add(faceDirVec));
        }
        else
        {
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec));
            BuildMode.updateCurrToolBlocks(block,hitVec);
        }
    }

    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        normalHitPos = new BlockPos(BuildMode.END_VEC);
        BuildMode.END_VEC = BuildMode.END_VEC.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
        BlockPos offsetPos = new BlockPos( BuildMode.END_VEC);
        this.addPositionToGuiOverlay(offsetPos);
//        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setPlacePointedCoordinate(offsetPos);

        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<Vec3d,TemplateBlock>();
        
        BuildMode.SECONDARY_LINE_VEC = HelpFunctions.parseVec(BuildMode.END_VEC).subtract(BuildMode.START_VEC);
    
        ArrayList<Vec3d> sides = new ArrayList<>();
        sides.add(BuildMode.SECONDARY_LINE_VEC);
      
        drawRectSidesToDict(sides, blockDict);
        BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
        //make sure you can put one block with the RECT tool
        if( BuildMode.START_BLOCK.getBlockPos().equals(BuildMode.LAST_BLOCK)&& BuildMode.CURR_TOOL_BLOCKS.isEmpty())
            BuildMode.CURR_TOOL_BLOCKS.add(BuildMode.getStartBlock());
    }
    
    @Override
    public void updateBlocksRightClick2(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        normalHitPos = new BlockPos(BuildMode.END_VEC);
        BuildMode.END_VEC = BuildMode.END_VEC.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
        BlockPos offsetPos = new BlockPos( BuildMode.END_VEC);
        this.addPositionToGuiOverlay(offsetPos);
//        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setPlacePointedCoordinate(offsetPos);
        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<Vec3d,TemplateBlock>();
    
        Vec3d newLineVec = HelpFunctions.parseVec(BuildMode.END_VEC).subtract(BuildMode.START_VEC).subtract(BuildMode.SECONDARY_LINE_VEC);
        ArrayList<Vec3d> sides = constructSideVectors(newLineVec);

        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();
        ArrayList<HashMap<Vec3d,TemplateBlock>> sidesListBlockDict = drawRectSidesToDict(sides, blockDict);
        if(BuildMode.FILL_MODE == EnumFillMode.FILL)
        {
            //bottom to top
            List<Vec3d> bottom = new ArrayList<>(sidesListBlockDict.get(0).keySet());
            Vec3d correctBottom;

            //copy the side lines for each block of the bottom line
            for (int i = 0; i < bottom.size(); i++)
            {
                correctBottom = bottom.get(i).add(0.5,0.5,0.5);
                addRectVector(correctBottom, newLineVec, blockDict);
            }
            //detect and close any holes in the resulting plane
            closeHolesInPlane(blockDict);
        }
        //make sure you can put one block or line with the RECT tool
        Vec3d blockDictFirstVec = PlacementHelper.convertBlockPosToVector3d(BuildMode.getStartBlock().getBlockPos());
//        Vec3d blockDictLastVec = PlacementHelper.convertBlockPosToVector3d(endGlobalblock.getBlockPos());
        blockDict.put(  blockDictFirstVec,BuildMode.getStartBlock());
        blockDict.put(  BuildMode.END_VEC,BuildMode.getStartBlock().createCopyWith(offsetPos));

        BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
        FILL_VECTOR_LIST_LOCK.releaseLock();
    }

    private static void closeHolesInPlane(HashMap<Vec3d, TemplateBlock> blockDict) {
        ArrayList<TemplateBlock> currBlockList = new ArrayList<>(blockDict.values());
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rows = HelpFunctions.getRowDirectionXYZ(currBlockList);

        ArrayList<BlockPos[]> OnePlanePos = new ArrayList<>();
        OnePlanePos.add(new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
        });

        OnePlanePos.add(new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
                new BlockPos(0, 1, 0), new BlockPos(0, -1, 0),

        });

        OnePlanePos.add(new BlockPos[]{
                new BlockPos(0, 1, 0), new BlockPos(0, -1, 0),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
        });

        int[][] combinator = new int[][]{
//                    new int[]{1,1,0,0},
                new int[]{1,0,1,0},
                new int[]{1,0,0,1},
                new int[]{0,1,1,0},
                new int[]{0,1,0,1},
                new int[]{0,0,0,0},
//                    new int[]{0,0,1,1}
        };


        ExtremaXYZ extrema = new ExtremaXYZ(currBlockList);

        int widthX = (extrema.maxX) - (extrema.minX);
        int height = (extrema.maxY) - (extrema.minY);
        int widthZ = (extrema.maxZ) - (extrema.minZ);

        for (int i = 0; i <= widthX ; i++)
        {
            for (int j = 0; j <= height; j++)
            {

                for (int k = 0; k <= widthZ; k++)
                {
                    TemplateBlock tempBlock = rows.get(i).get(j).get(k);
                    if (tempBlock == null)
                    {
                        for (int a = 0; a < OnePlanePos.size(); a++)
                        {
                            boolean condition = true;

                            int newX = a == 2 ? i + 1: i ;
                            int newY = a == 0 ? j + 1: j ;
                            int newZ = a == 1 ? k + 1: k;

                            if(!(newX < 0 || newX > widthX || newY < 0 || newY > height || newZ < 0 || newZ > widthZ))
                            {
                                if( rows.get(newX).get(newY).get(newZ) != null)
                                {
                                    continue;
                                }
                            }

                            newX = a == 2 ? i - 1: i ;
                            newY = a == 0 ? j - 1: j ;
                            newZ = a == 1 ? k - 1: k;

                            if(!(newX < 0 || newX > widthX || newY < 0 || newY > height || newZ < 0 || newZ > widthZ))
                            {
                                if( rows.get(newX).get(newY).get(newZ) != null)
                                {
                                    continue;
                                }
                            }

                            for(int[] combination : combinator)
                            {
                                condition = true;
                                BlockPos[] newpositions = OnePlanePos.get(a);
                                for (int b = 0; b < newpositions.length; b++)
                                {

                                    BlockPos newpos = newpositions[b];
                                    newX = a == 2 ? i + newpos.getX() +combination[b]: i + newpos.getX();
                                    newY = a == 0 ? j + newpos.getY()+combination[b]: j + newpos.getY();
                                    newZ = a == 1 ? k + newpos.getZ()+combination[b]: k + newpos.getZ();
                                    if(newX < 0 || newX > widthX || newY < 0 || newY > height || newZ < 0 || newZ > widthZ)
                                    {
                                        condition = false;
                                        continue;
                                    }

                                    if( rows.get(newX).get(newY).get(newZ) == null)
                                    {
                                        condition = false;
                                        break;
                                    }
                                }
                                if(condition)
                                    break;

                                condition = true;
                                for (int b = 0; b < newpositions.length; b++)
                                {

                                    BlockPos newpos = newpositions[b];

                                    newX = a == 2 ? i + newpos.getX() -combination[b]: i + newpos.getX();
                                    newY = a == 0 ? j + newpos.getY()-combination[b]: j + newpos.getY();
                                    newZ = a == 1 ? k + newpos.getZ()-combination[b]: k + newpos.getZ();
                                    if(newX < 0 || newX > widthX || newY < 0 || newY > height || newZ < 0 || newZ > widthZ)
                                    {
                                        condition = false;
                                        continue;
                                    }

                                    if( rows.get(newX).get(newY).get(newZ) == null)
                                    {
                                        condition = false;
                                        break;
                                    }
                                }
                                if(condition)
                                    break;
                            }

                            if(condition)
                            {
                                BlockPos addPos = new BlockPos(i+extrema.minX,j+extrema.minY ,k+extrema.minZ );
                                PlacementHelper.placePositionInDict(addPos, BuildMode.getStartBlock(), blockDict);
                            }
                        }

                    }
                }
            }
        }
    }

    private ArrayList<Vec3d> constructSideVectors(Vec3d newLineVec)
    {
        ArrayList<Vec3d> sides = new ArrayList<>();
        sides.add(BuildMode.SECONDARY_LINE_VEC); //bottom side
        sides.add(newLineVec);  //left side
        sides.add(newLineVec);  //right side
        sides.add(BuildMode.SECONDARY_LINE_VEC); //top side
        return sides;
    }

    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        if(FILL_VECTOR_LIST_LOCK.attemptLocking())
        {
            for (FillVector fillVec: FILL_VECTOR_LIST)
            {
                RenderTemplate.drawLine(fillVec.startVec,
                        fillVec.endVec,
                        entityplayer, 0, partialTicks, fillVec.red,fillVec.green,fillVec.blue);

            }
            FILL_VECTOR_LIST_LOCK.releaseLock();
        }
    }
    
    @Override
    public Vec3d getNewStartVec(Vec3d startVec, Vec3d secondaryLineVec) {
        if(BuildMode.RIGHT_CLICK_NUMBER >= 2 && secondaryLineVec != null)
            return startVec.add(secondaryLineVec);
        else
            return startVec;
    }
    
    private ArrayList< HashMap<Vec3d, TemplateBlock>> drawRectSidesToDict(ArrayList<Vec3d> sides, HashMap<Vec3d, TemplateBlock> blockDict) {
        //make sure that the edges of the rectangle aren't added twice
        //by not drawing the last and the first block of the right and left side vertex
        //also makes sure that sides look identical by making sure the parallell vectors are always drawn
        //in the same direction. That is why this loop as if's in it
        Vec3d prevVec = BuildMode.START_VEC;
        ArrayList< HashMap<Vec3d, TemplateBlock>> sidesListBlockDict = new ArrayList<>();
        for (int i = 0;i<sides.size();i++) {
            sidesListBlockDict.add(new HashMap<>());
    
            if (i == 0) {
                //bottom of rectangle
                addRectVector(prevVec, sides.get(i), sidesListBlockDict.get(i), 1.0f, 1.0f, 1.0f);
                prevVec = BuildMode.START_VEC.add(sides.get(i));
            } else if (i == 1) {
                //(left) side of rectangle
                addRectVector(prevVec, sides.get(i), sidesListBlockDict.get(i), true, true, 1.0f, 1.0f, 1.0f);
                prevVec = prevVec.add(sides.get(i));
            } else if (i == 2) {
                //(right) side of rectangle
                addRectVector(BuildMode.START_VEC, sides.get(i), sidesListBlockDict.get(i), true, true, 1.0f, 1.0f, 1.0f);
                prevVec = BuildMode.START_VEC.add(sides.get(i));
            } else if (i == 3) {
                //top of rectangle
                addRectVector(prevVec, sides.get(i), sidesListBlockDict.get(i), 1.0f, 1.0f, 1.0f);
                prevVec = prevVec.add(sides.get(i));
            }
            blockDict.putAll(sidesListBlockDict.get(i));
        }
        return sidesListBlockDict;
    }
    
    public void drawFilledRectangleToDict(Vec3d startVec, Vec3d toAddVec1, Vec3d toAddVec2, HashMap<Vec3d,TemplateBlock> dict)
    {
        //unit vec and the number of times to iterate
        double scale = 0.25;
        Vec3d unitVector = toAddVec1.normalize().scale(scale);
        double lenght = toAddVec1.length();
        int times = (int) Math.ceil(lenght / (unitVector.length()));
        //variables that are used in the loop
        Vec3d stepsTo = new Vec3d(0, 0, 0);
        BlockPos currPosition = new BlockPos(BuildMode.START_BLOCK.getBlockPos());
        Vec3d newVec = null;
        BlockPos prevPrevPosition = null;
        BlockPos prevPosition = null;
        Vec3d prevVec = null;
        BlockPos lastPlacedPos = null;

        for (int i = 0; i < times+4; i++) {
            boolean changePrev = false;
            boolean changePrevPrev = false;
            Vec3d checkStepsTo = stepsTo.add(unitVector);
            //if the new vector is to long half the unitVector once and see if it fits
            if(checkStepsTo.length() <= lenght)
            {
                stepsTo = checkStepsTo;
            }
            else
            {
                unitVector = unitVector.scale(0.5);
                stepsTo = stepsTo.add(unitVector);
            }
            toAddVec1.normalize().scale(0.25);
            if(stepsTo.length() <= lenght)
            {
                newVec = startVec.add(stepsTo);
                currPosition = new BlockPos(newVec);


                //complicated mess to ensure no duplicates and no corner blocks
                //Checks if the previous position is not the same as the first
                //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
                if (prevPosition == null)
                {
                    changePrev = true;
                }
                else if (!prevPosition.equals(currPosition))
                {
                    if (prevPrevPosition == null) {
                        lastPlacedPos =placeExtraRectStepVectors(prevPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, 3.0, dict);

                        changePrev = true;
                        changePrevPrev = true;
                    } else {
                        if (PlacementHelper.isDiagonal(prevPrevPosition,currPosition))
                        {
                            withinBlockCount = 0;
                            changePrev = true;
                        } else {
                            lastPlacedPos =placeRectStepVectors(prevPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, 3.0, dict);
                            withinBlockCount = 0;
                            changePrev = true;
                            changePrevPrev = true;
                        }

                    }
                }
                else
                {
                    withinBlockCount++;
                }
            }
            if (changePrevPrev)
                prevPrevPosition = prevPosition;
            if (changePrev)
            {
                prevPosition = currPosition;
                prevVec = newVec;
            }
        }

        //make sure the end vector is also drawn
        if (currPosition != null && prevVec != null && (prevPosition == null || lastPlacedPos == null || !lastPlacedPos.equals(currPosition)))
        {
            placeExtraRectStepVectors(currPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, -3.0, dict);
        }
    }

    public void drawFilledRectangleToDict2(Vec3d startVec, Vec3d toAddVec1, Vec3d toAddVec2, HashMap<Vec3d,TemplateBlock> dict)
    {
        //unit vec and the number of times to iterate
        double scale = 0.25;
        Vec3d unitVector = toAddVec1.normalize().scale(scale);
        double lenght = toAddVec1.length();
        int times = (int) Math.ceil(lenght / (unitVector.length()));

        //variables that are used in the loop
        Vec3d stepsTo = new Vec3d(0, 0, 0);
        BlockPos currPosition = new BlockPos(BuildMode.START_BLOCK.getBlockPos());
        Vec3d newVec = null;
        BlockPos prevPrevPosition = null;
        BlockPos prevPosition = null;
        Vec3d prevVec = null;
        BlockPos lastPlacedPos = null;
        
        for (int i = 0; i < times+4; i++) {
            boolean changePrev = false;
            boolean changePrevPrev = false;
            Vec3d checkStepsTo = stepsTo.add(unitVector);
            //if the new vector is to long half the unitVector once and see if it fits
            if(checkStepsTo.length() <= lenght)
            {
                stepsTo = checkStepsTo;
            }
            else
            {
                unitVector = unitVector.scale(0.5);
                stepsTo = stepsTo.add(unitVector);
            }
            toAddVec1.normalize().scale(0.25);
            if(stepsTo.length() <= lenght)
            {
                newVec = startVec.add(stepsTo);
                currPosition = new BlockPos(newVec);
                
                
                //complicated mess to ensure no duplicates and no corner blocks
                //Checks if the previous position is not the same as the first
                //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
                if (prevPosition == null)
                {
                    changePrev = true;
                }
                else if (!prevPosition.equals(currPosition))
                {
                    if (prevPrevPosition == null) {
                        lastPlacedPos =placeExtraRectStepVectors(prevPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, 3.0, dict);
    
                        changePrev = true;
                        changePrevPrev = true;
                    } else {
                        if (PlacementHelper.isDiagonal(prevPrevPosition,currPosition))
                        {
                            withinBlockCount = 0;
                            changePrev = true;
                        } else {
                            lastPlacedPos =placeRectStepVectors(prevPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, 3.0, dict);
                            withinBlockCount = 0;
                            changePrev = true;
                            changePrevPrev = true;
                        }
                        
                    }
                }
                else
                {
                    withinBlockCount++;
                }
            }
            if (changePrevPrev)
                prevPrevPosition = prevPosition;
            if (changePrev)
            {
                prevPosition = currPosition;
                prevVec = newVec;
            }
        }
        
        //make sure the end vector is also drawn
        if (currPosition != null && prevVec != null && (prevPosition == null || lastPlacedPos == null || !lastPlacedPos.equals(currPosition)))
        {
            placeExtraRectStepVectors(currPosition,prevVec.add(unitVector.scale(withinBlockCount*0.4)), toAddVec1, toAddVec2, unitVector, -3.0, dict);
        }
    }
    
    private BlockPos placeRectStepVectors(BlockPos position, Vec3d vec, Vec3d toAddVec1, Vec3d toAddVec2, Vec3d unitVector, double scaleExtraVector, HashMap<Vec3d, TemplateBlock> dict)
    {
    
        Vec3d baseVec = new Vec3d(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
        Vec3d lineStartVec = baseVec.add(toAddVec2.normalize().scale(0.0));
        Vec3d newToAddVec = toAddVec2.add(toAddVec2.normalize().scale(0.0));
        
        addRectVector(lineStartVec, newToAddVec, dict);
        //add 3 unitvec
        if (rectIs3D(toAddVec1, toAddVec2))
            addRectVector(lineStartVec.add(unitVector.scale(scaleExtraVector)), newToAddVec, dict,true,true,0.0f,1.0f,0.0f);
        return position;
    }
    
    
    private BlockPos placeExtraRectStepVectors(BlockPos position, Vec3d vec, Vec3d toAddVec1, Vec3d toAddVec2, Vec3d unitVector, double scaleExtraVector, HashMap<Vec3d, TemplateBlock> dict)
    {
        
        Vec3d baseVec = new Vec3d(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
        Vec3d lineStartVec = baseVec.add(toAddVec2.normalize().scale(0.0));
        Vec3d newToAddVec = toAddVec2.add(toAddVec2.normalize().scale(0.0));

        //add 3 unitvec
        if (rectIs3D(toAddVec1, toAddVec2))
            addRectVector(lineStartVec.add(unitVector.scale(scaleExtraVector)), newToAddVec, dict,true,true,0.0f,1.0f,0.0f);
        return position;
    }

    private boolean rectIs3D(Vec3d toAddVec1, Vec3d toAddVec2)
    {
        return ((toAddVec1.x != 0.0 && toAddVec1.y != 0.0) || (toAddVec1.x != 0.0 && toAddVec1.z != 0.0) || (toAddVec1.y != 0.0 && toAddVec1.z != 0.0)) &&
                ((toAddVec2.x != 0.0 && toAddVec2.y != 0.0) || (toAddVec2.x != 0.0 && toAddVec2.z != 0.0) || (toAddVec2.y != 0.0 && toAddVec2.z != 0.0));
    }

    @Override
    public boolean hasMiddlePosition()
    {
        return true;
    }

    @Override
    protected void drawMiddleBlockHighlight(EntityPlayer entityplayer,float partialTicks)
    {
        RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, new BlockPos(BuildMode.getStartVec().add(BuildMode.SECONDARY_LINE_VEC)), partialTicks);
    }
}
