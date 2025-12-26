package com.deadtiger.advcreation.edit_mode.adjust_modes;

import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainShapeMode;
import com.deadtiger.advcreation.edit_mode.utility.TerrainEditData;
import com.deadtiger.advcreation.edit_mode.utility.TerrainEditProcessors;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.shape_creator.BaseShapeCreator;
import com.deadtiger.advcreation.utility.shape_creator.CircleRectangleCreator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;

public class DigRaiseAdjustMode extends BaseAdjustMode
{
    //good positions to create nice circles
    private final ArrayList<Vec3d> previewCircleHelpLineVecPoints = new ArrayList<>();
    public EnumDirectionMode paintDirection = EnumDirectionMode.XZ;
    public boolean sqaure = false;
    public ArrayList<Integer> indicesOfToGroundChangedIndication = new ArrayList<>();
    public HashMap<Integer, TemplateBlock> blockIndicesToOldBlock = new HashMap<>();
    public HashMap<Integer, Integer> blockIndicesToNewHeights = new HashMap<>();
    public ArrayList<Integer> indicesOfLowestLevelIndication = new ArrayList<>();
    public ArrayList<Integer> indicesOfHighestLevelIndication = new ArrayList<>();
    public ArrayList<Integer> indicesOfIgnoredBlocksIndication = new ArrayList<>();
    //of all the block to be painted are the same as the brush block then don't change anything
    private boolean allWorldBlocksTheSameAsBrush = false;

    public boolean alternative = false;

    public int newTopCenterHeight = 0;
    public int newBottomCenterHeight = 0;

    public DigRaiseAdjustMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "Dig/Raise Mode";
        this.buttonText = "DIG/RAISE";
        this.tooltipText = "Dig/Raise terrain";
        this.identificationIndex = 2;

        addLegendEntry(LevelAdjustMode.existingLowerLevelColor,"Already at the lowest height");
        addLegendEntry(LevelAdjustMode.toBeAddedLowerLevelColor,"Will change to the lowest height");
        addLegendEntry(LevelAdjustMode.existingHighLevelColor,"Already at the highest height");
        addLegendEntry(LevelAdjustMode.toBeAddedHighLevelColor,"Will change to the highest height");
        addLegendEntry(LevelAdjustMode.changedColor,"Will change to a slope");
        addLegendEntry(LevelAdjustMode.unchangedColor,"Do not change");
        addLegendEntry(LevelAdjustMode.ignoredBlockColor,"Ignored by 'ONLY TERRAIN' setting");
    }
    
    @Override
    public int addToRightClickNumber() {
        return 0;
    }

    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber) {
        if (rightClickNumber >= this.finalRightClick && !indicesOfToGroundChangedIndication.isEmpty())
        {
            EditMode.CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();
            EditMode.CURR_TOOL_BLOCKS.clear();
            addTopLayerOfTerrainToToolBlocks();
            EditMode.CURR_TOOL_BLOCKS_LOCK.releaseLock();

            return true;
        }
        return false;
    }

    @Override
    public boolean leftClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        if (rightClickNumber >= this.finalRightClick && !indicesOfToGroundChangedIndication.isEmpty())
        {
            EditMode.CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();
            EditMode.CURR_TOOL_BLOCKS.clear();
            addTopLayerOfTerrainToToolBlocks();
            EditMode.CURR_TOOL_BLOCKS_LOCK.releaseLock();
            return true;
        }
        return false;
    }

    @Override
    public boolean startCancelBuilding(int rightClickNumber)
    {
//        if(!SculptMode.deleteMode && !allWorldBlocksTheSameAsBrush)
        if(EditMode.DELETE_MODE && !indicesOfToGroundChangedIndication.isEmpty())
            return super.startCancelBuilding(rightClickNumber);
        else if(!indicesOfToGroundChangedIndication.isEmpty())
            return (this.finalRightClick == rightClickNumber);
        return false;
    }
    
    @Override
    public void activateDeleteMode()
    {
        EditMode.CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();
        EditMode.CURR_TOOL_BLOCKS.clear();
        EditMode.FILL_VECTOR_MAP.clear();
        this.updateBlocksRightClick0(EditMode.getStartBlock(),EditMode.getSTARTVEC(),EditMode.getSTARTVEC());
        EditMode.CURR_TOOL_BLOCKS.clear();
        addTopLayerOfTerrainToToolBlocks();
        EditMode.CURR_TOOL_BLOCKS_LOCK.releaseLock();

//       Vec3d invFaceDirVec = Vec3d.ZERO;
//        if(EditMode.START_BLOCK != null)
//            EditMode.setStartBlock(new TemplateBlock(EditMode.getStartBlock().getFace(), EditMode.getStartBlock().getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), EditMode.getStartBlock().getBlockState(),EditMode.getStartBlock().getTileEntity()));
//        else
//            EditMode.setStartBlock(new TemplateBlock(Direction.NORTH,BlockPos.ZERO.add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), Blocks.AIR.getDefaultState()));
//
//        if(EditMode.getSTARTVEC() == null)
//            EditMode.setSTARTVEC(Vec3d.ZERO);
//        else
//            EditMode.setSTARTVEC(EditMode.getSTARTVEC().add(invFaceDirVec));
    }

    @Override
    public void deactivateDeleteMode()
    {
        EditMode.CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();
        EditMode.CURR_TOOL_BLOCKS.clear();
        EditMode.FILL_VECTOR_MAP.clear();
        this.updateBlocksRightClick0(EditMode.getStartBlock(),EditMode.getSTARTVEC(),EditMode.getSTARTVEC());
        EditMode.CURR_TOOL_BLOCKS.clear();
        addTopLayerOfTerrainToToolBlocks();
        EditMode.CURR_TOOL_BLOCKS_LOCK.releaseLock();
    }

    @Override
    public void addNewBlockRightClick0(TemplateBlock block,Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        Vec3d invFaceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-EditMode.CURR_HITBLOCK_WIDTH);
//        EditMode.DELETE_MODE = false;

//        if(face.equals(Direction.NORTH) || face.equals(Direction.SOUTH))
//            paintDirection = EnumDirectionMode.XY;
//        else if(face.equals(Direction.WEST) || face.equals(Direction.EAST))
//            paintDirection = EnumDirectionMode.ZY;
//        else
            paintDirection = EnumDirectionMode.XZ;

        EditMode.setStartBlock(new TemplateBlock(face,block.getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), block.getBlockState(),block.getTileEntity()));
        EditMode.setSTARTVEC(hitVec.add(invFaceDirVec));
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block,hitVec);
    }

    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock,Vec3d hitVec,Vec3d newStartVec)
    {
        CircleRectangleCreator.INSTANCE.init();

        normalHitPos = new BlockPos(newStartVec);
        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setStartPos(normalHitPos);

        //the initial radiusVec
       Vec3d radiusVec = new Vec3d(radius, 0, 0);
        EditMode.LINEVEC = radiusVec;
        double radiusLength = radiusVec.length();

        // Jumps to one of the nice circles in the middleCircleDict when the length is of the radius is close to it
        // These are recorded in the XZ plane and are transformed to other planes when necessary
        sqaure = false;
        if (CircleRectangleCreator.INSTANCE.jumpToHardcodedRadius)
        {
            if (CircleRectangleCreator.INSTANCE.isInsideOfHardcodedRadiusRange(radiusIndex))
            {
//                radiusLength = CircleRectangleCreator.INSTANCE.getClosestHardcodedCircleLength(radiusLength);
                radiusLength = CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.get(radiusIndex);
                radiusVec = CircleRectangleCreator.INSTANCE.transformHardcodedCircleToCurrDir(radiusVec, radiusLength, paintDirection);
                if (radiusVec == null)
                    sqaure = true;
                EditMode.setColor(CircleRectangleCreator.INSTANCE.jumpToHardcodedRadiusColor);
            }
            else
                EditMode.setColor(CircleRectangleCreator.INSTANCE.freeRadiusColor);
        }
        else
            EditMode.setColor(CircleRectangleCreator.INSTANCE.freeRadiusColor);

        //logic to add a point to the middleCircleDicts that allows to save nice circle sizes when leftclicked
        if (BaseShapeCreator.addHardcodedRadius && !sqaure)
            CircleRectangleCreator.INSTANCE.addHardcodedRadius(radiusVec, radiusLength);

        HashMap<Vec3d, TemplateBlock> blockDict = new HashMap<Vec3d, TemplateBlock>();


        if (!sqaure)
        {
            //draw a circle with a radius
            ArrayList<Double> singleBlockKeys = new ArrayList<>();
            ArrayList<BlockPos> positionsList = CircleRectangleCreator.INSTANCE.generateCirclePositions(newStartVec, radiusVec, radiusLength, previewCircleHelpLineVecPoints, paintDirection);

            for (BlockPos pos : positionsList)
            {
                CircleRectangleCreator.INSTANCE.placePosition(pos, EditMode.START_BLOCK, singleBlockKeys, blockDict, true, EditMode.FILL_VECTOR_MAP, paintDirection, EditMode.NEW_START_VEC);
            }

            //get all the keys for which there was no second block and place them too
            for (double key : singleBlockKeys)
            {
                PlacementHelper.placePositionInDict(new BlockPos(EditMode.FILL_VECTOR_MAP.get(key).startVec), EditMode.START_BLOCK, blockDict);
            }
        }
        else
        {
            //draw a sqaure
            ArrayList<BlockPos> blockPosList = CircleRectangleCreator.INSTANCE.generateRectanglePositions(newStartVec, radiusLength, paintDirection);
            for (BlockPos pos : blockPosList)
            {
                PlacementHelper.placePositionInDict(pos, EditMode.START_BLOCK, blockDict);
            }
        }
        ArrayList<TemplateBlock> toGround = new ArrayList<>(blockDict.values());
//        EnumFacing face = EditMode.START_BLOCK.getFace();
        EnumFacing face = EnumFacing.UP;

        blockIndicesToNewHeights = new HashMap<>();
        blockIndicesToOldBlock = new HashMap<>();
        HelpFunctions.updateListToAirBlockAboveFirstSolidBlockInDir(toGround, face, 100);
        copyCurrentWorldBlocks(toGround, face);

        //add toGround to preview blocks
//        if (EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
//        {
//            EditMode.CURR_PREVIEW_BLOCKS = new ArrayList<>(toGround);
//            indicesOfToGroundChangedIndication = new ArrayList<>();
//            indicesOfHighestLevelIndication = new ArrayList<>();
//            indicesOfLowestLevelIndication = new ArrayList<>();
//            EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
//        }

        if (EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.attemptLocking())
        {
            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS = new ArrayList<>(toGround);
            indicesOfToGroundChangedIndication = new ArrayList<>();
            indicesOfHighestLevelIndication = new ArrayList<>();
            indicesOfLowestLevelIndication = new ArrayList<>();
            indicesOfIgnoredBlocksIndication = new ArrayList<>();
            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();
        }

        if(toGround.isEmpty())
            return;
        //calculate
        ExtremaXYZ extremaXYZ = new ExtremaXYZ(toGround);
        float data[][] = getProcessData(EditMode.TERRAIN_SHAPE_MODE.getDataIndex(), true);
        BlockPos size2DArray = new BlockPos(extremaXYZ.maxX - extremaXYZ.minX + data.length+2, 0, extremaXYZ.maxZ - extremaXYZ.minZ + data.length+2);
        int heights[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int newHeights[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int newerHeights[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int blockIndices[][] = new int[size2DArray.getX()][size2DArray.getZ()];

        ArrayList<TemplateBlock> filteredToGround = fillInitialHeightsArrayAndBlockIndices(toGround, extremaXYZ, heights, blockIndices, data.length);
        extremaXYZ = new ExtremaXYZ(filteredToGround);

        filterOutBadIndices(size2DArray, heights, blockIndices);
//        detectTopAndBottomLevelIndices(toGround,extremaXYZ,heights,blockIndices);



        int middlePos = (int) size2DArray.getX()/2;
        int amplitude = EditMode.TERRAIN_SHAPE_MODE.intensityNumber;

        if(EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.STRAIGHT)
        {
            //in drop mode bring al blocks in the brush to the new height
            if(EditMode.DELETE_MODE)
            {
                newBottomCenterHeight =  heights[middlePos][middlePos] - amplitude;
                //Lower leveling tool
                for (int x = 0; x < size2DArray.getX(); x++)
                {
                    for (int z = 0; z < size2DArray.getZ(); z++)
                    {
                        if(heights[x][z] > 0)
                        {
                            if(heights[x][z] >= newBottomCenterHeight)
                                newHeights[x][z] = newBottomCenterHeight;
                            else
                                newHeights[x][z] = heights[x][z];
                        }

                    }
                }
                applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world, extremaXYZ);
            }
            else
            {
                newTopCenterHeight =  heights[middlePos][middlePos]  + amplitude;
                //Upper leveling tool
                for (int x = 0; x < size2DArray.getX(); x++)
                {
                    for (int z = 0; z < size2DArray.getZ(); z++)
                    {
                        if(heights[x][z] > 0)
                        {
                            if(heights[x][z] <= newTopCenterHeight)
                                newHeights[x][z] = newTopCenterHeight;
                            else
                                newHeights[x][z] = heights[x][z];
                        }
                    }
                }

                applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world, extremaXYZ);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newHeights, blockIndices, data, size2DArray, extremaXYZ);
            }
            return;
        }

        if(EditMode.DELETE_MODE)
        {
            //Digging tool
            double[][] dataDigRaise = TerrainEditProcessors.calculate3DDataDigRaise(-amplitude,radiusLength,size2DArray);
//            int centerHeight = heights[middlePos][middlePos] + (int) Math.round(dataDigRaise[middlePos][middlePos] +0.3);
            newBottomCenterHeight = heights[middlePos][middlePos] - amplitude;


            TerrainEditProcessors.applyDigData(size2DArray, heights, newHeights, dataDigRaise, newBottomCenterHeight);
            TerrainEditProcessors.applyDataDigRaise(size2DArray, newHeights, newerHeights, data);
            onlyAllowChangeInOneDirection(size2DArray,heights,newerHeights,false, newBottomCenterHeight);

        }
        else
        {
            //Raissing tool
            double[][] dataDigRaise = TerrainEditProcessors.calculate3DDataDigRaise(amplitude,radiusLength,size2DArray);
            newTopCenterHeight = heights[middlePos][middlePos] + amplitude;

            TerrainEditProcessors.applyRaiseData(size2DArray, heights, newHeights, dataDigRaise, newTopCenterHeight);
            TerrainEditProcessors.applyDataDigRaise(size2DArray, newHeights, newerHeights, data);
            onlyAllowChangeInOneDirection(size2DArray,heights,newerHeights,true, newTopCenterHeight);
        }

//        applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world);

        if(blockIndices[middlePos][middlePos] == -1)
        {
            filterOutBadIndices(size2DArray, newerHeights, blockIndices);
            applyNewHeightsToTool(toGround, size2DArray, heights, newerHeights, blockIndices, Minecraft.getMinecraft().world, extremaXYZ);

            if(!EditMode.DELETE_MODE)
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newerHeights, blockIndices, data, size2DArray, extremaXYZ);

            return;
        }
        else
        {
            // if the data hasn't resulted in atleast a change in the middle block of the clicked area recompute with
            // a higher amplitude untill there is a change
            int count= 0;
            int threshold = 10 ;
            int dataIndex = EditMode.TERRAIN_SHAPE_MODE.getDataIndex();
            if(EditMode.DELETE_MODE)
            {
                while(newBottomCenterHeight < newerHeights[middlePos][middlePos])
                {
                    amplitude++;


                    //Digging tool
                    double[][] dataDigRaise = TerrainEditProcessors.calculate3DDataDigRaise(-amplitude, radiusLength, size2DArray);
                    TerrainEditProcessors.applyDigData(size2DArray, heights, newHeights, dataDigRaise, heights[middlePos][middlePos] - EditMode.TERRAIN_SHAPE_MODE.intensityNumber);

                    if(amplitude >= TerrainEditData.smoothingData.size())
                        dataIndex = TerrainEditData.smoothingData.size()-1;
                    else
                        dataIndex = amplitude;

                    TerrainEditProcessors.applyDataDigRaise(size2DArray, newHeights, newerHeights, getProcessData(dataIndex, true));
                    onlyAllowChangeInOneDirection(size2DArray,heights,newerHeights,false, newBottomCenterHeight);
                    count++;
                    if(count > threshold)
                    {
                        break;
                    }
                }
            }
            else
            {
                while(newTopCenterHeight > newerHeights[middlePos][middlePos])
                {
                    amplitude++;
                    //Raissing tool
                    double[][] dataDigRaise = TerrainEditProcessors.calculate3DDataDigRaise(amplitude, radiusLength, size2DArray);

                    TerrainEditProcessors.applyRaiseData(size2DArray, heights, newHeights, dataDigRaise, heights[middlePos][middlePos] + EditMode.TERRAIN_SHAPE_MODE.intensityNumber);

                    if(amplitude >= TerrainEditData.smoothingData.size())
                        dataIndex = TerrainEditData.smoothingData.size()-1;
                    else
                        dataIndex = amplitude;

                    TerrainEditProcessors.applyDataDigRaise(size2DArray, newHeights, newerHeights, getProcessData(dataIndex, true));
                    onlyAllowChangeInOneDirection(size2DArray,heights,newerHeights,true, newTopCenterHeight);
                    count++;
                    if(count > threshold)
                    {
                        break;
                    }

                }
            }

            filterOutBadIndices(size2DArray, newerHeights, blockIndices);
            applyNewHeightsToTool(toGround, size2DArray, heights, newerHeights, blockIndices, Minecraft.getMinecraft().world, extremaXYZ);

            addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newerHeights, blockIndices, data, size2DArray, extremaXYZ);
        }



    }


    private boolean checkIfChangedBetween(int[][] heights,int[][] newheights,BlockPos size2DArray)
    {

        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {
                if(heights[x][z] != newheights[x][z])
                    return true;
            }
        }
        return false;
    }


    private ArrayList<TemplateBlock> fillInitialHeightsArrayAndBlockIndices(ArrayList<TemplateBlock> toGround, ExtremaXYZ extremaXYZ, int[][] heights, int[][] blockIndices, int dataSize )
    {
        int relXPos;
        int relZPos;
        ArrayList<TemplateBlock> filteredToGround = new ArrayList<>();
        //fill heigts 2D array with the blocks Y value
        for (int i = 0; i < toGround.size(); i++)
        {
            TemplateBlock block = toGround.get(i);
            relXPos = block.getX_offset() - (extremaXYZ.minX - dataSize/2-1);
            relZPos = block.getZ_offset() - (extremaXYZ.minZ - dataSize/2-1);
            if(HelpFunctions.stateIsAllowed(block.getBlockState(),EditMode.ONLY_TERRAIN_MODE))
            {
                int height = block.getY_offset();
                heights[relXPos][relZPos] = height;
                blockIndices[relXPos][relZPos] = i;
//                if(height == extremaXYZ.maxY)
//                    indicesOfHighestLevelIndication.add(i);
//                else if(height == extremaXYZ.minY)
//                    indicesOfLowestLevelIndication.add(i);
                filteredToGround.add(block);
            }
            else
            {
                heights[relXPos][relZPos] = 0;
                blockIndices[relXPos][relZPos] = -1;
                indicesOfIgnoredBlocksIndication.add(i);

            }


        }
        return filteredToGround;
    }

    private void detectTopAndBottomLevelIndices(ArrayList<TemplateBlock> toGround, ExtremaXYZ extremaXYZ, int[][] heights, int[][] blockIndices)
    {
        for (int i = 0; i < toGround.size(); i++)
        {
            TemplateBlock block = toGround.get(i);
            int height = block.getY_offset();
            if(height == extremaXYZ.maxY)
                indicesOfHighestLevelIndication.add(i);
            else if(height == extremaXYZ.minY)
                indicesOfLowestLevelIndication.add(i);
        }
    }

    private void applyNewHeightsToTool(ArrayList<TemplateBlock> toGround, BlockPos size2DArray, int[][] heights, int[][] newHeights, int[][] blockIndices, World world, ExtremaXYZ extremaXYZ)
    {
        int blockCount = 0;
        int maxY = -1000;
        int minY = 1000;

        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {
                int blockIndex = blockIndices[x][z];

                if (blockIndex == -1)
                    continue;
                else if (heights[x][z] < newHeights[x][z])
                {
                    indicesOfToGroundChangedIndication.add(blockIndices[x][z]);

                    int newHeightY =newHeights[x][z];
                    TemplateBlock oldBlock = toGround.get(blockIndices[x][z]);
                    int oldHeightY = oldBlock.getY_offset();

                    blockIndicesToNewHeights.put(blockIndices[x][z],newHeightY);
                    blockIndicesToOldBlock.put(blockIndices[x][z],oldBlock);

                    maxY = Math.max(newHeightY,maxY);
                    maxY = Math.max(oldHeightY,maxY);
                    minY = Math.min(newHeightY,minY);
                    minY = Math.min(oldHeightY,minY);

                    blockCount += (int)  Math.abs(newHeightY - oldHeightY) ;

                }
                else if (heights[x][z] == newHeights[x][z])
                    continue;
                else
                {
                    indicesOfToGroundChangedIndication.add(blockIndices[x][z]);

                    int newHeightY =newHeights[x][z];
                    TemplateBlock oldBlock = toGround.get(blockIndices[x][z]);
                    int oldHeightY = oldBlock.getY_offset();

                    blockIndicesToNewHeights.put(blockIndices[x][z],newHeightY);
                    blockIndicesToOldBlock.put(blockIndices[x][z],oldBlock);

                    maxY = Math.max(newHeightY,maxY);
                    maxY = Math.max(oldHeightY,maxY);
                    minY = Math.min(newHeightY,minY);
                    minY = Math.min(oldHeightY,minY);

                    blockCount += (int)  Math.abs(newHeightY - oldHeightY) ;

                }
            }

        }
        if(indicesOfToGroundChangedIndication.isEmpty())
            GuiOverlayManager.setBlockCount(0,0,0,0);
        else
            GuiOverlayManager.setBlockCount(extremaXYZ.maxX-extremaXYZ.minX+1,maxY-minY,extremaXYZ.maxZ -extremaXYZ.minZ+1,blockCount);


    }

    protected void addTopLayerOfTerrainToToolBlocks()
    {
        World world = Minecraft.getMinecraft().world;
        for (int index:indicesOfToGroundChangedIndication)
        {
            TemplateBlock originalGroundBlock = blockIndicesToOldBlock.get(index);
            int height = originalGroundBlock.getY_offset();
            int newHeight = blockIndicesToNewHeights.get(index);

            if (height < newHeight)
            {
                int diff = newHeight - height;

                //go down to copy the bottom layer of the terrain so it still appear like normal terrain
                //try to find stone,sandstone or nether if you don't find stone just copy the last blockstate and use that as filler
                //if you find air that probably means there is a cave don't disturb it and just copy the last block
                IBlockState lastBlockState = originalGroundBlock.getBlockState();
                IBlockState thisBlockState;
                BlockPos thisGetBlockPos = originalGroundBlock.getBlockPos();
                BlockPos thisSetBlockPos = originalGroundBlock.getBlockPos().add(0, diff, 0);
                for (int h = 0; h < 6; h++)
                {
                    thisBlockState = world.getBlockState(thisGetBlockPos);
                    if (thisBlockState.getBlock() instanceof BlockAir || HelpFunctions.isSameBlockType(thisBlockState,Blocks.BEDROCK))
                        break;
                    lastBlockState = thisBlockState;

                    if ( HelpFunctions.isSameBlockType(lastBlockState,Blocks.STONE) ||
                            HelpFunctions.isSameBlockType(lastBlockState,Blocks.SANDSTONE) ||
                            HelpFunctions.isSameBlockType(lastBlockState,Blocks.DIRT))
                        break;

                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(originalGroundBlock.getFace(), thisSetBlockPos, originalGroundBlock.getBlockState()));
                    thisGetBlockPos = thisGetBlockPos.down();
                    thisSetBlockPos = thisSetBlockPos.down();
                }

                //fill up the gap that has been made with te blockstate of the lastblock in found
                for (int extraY = 0; extraY <= diff; extraY++)
                {
                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(originalGroundBlock.getFace(), thisSetBlockPos.add(0, -extraY, 0), lastBlockState));
                }

                //check if there are plants on the first 2 layers on top of the current terrain
                //if there are also put the up
                //logs remain unaffected though
                thisGetBlockPos = originalGroundBlock.getBlockPos().add(0,1,0);
                thisSetBlockPos = thisGetBlockPos.add(0, diff, 0);
                for (int p = 0; p < 2; p++)
                {
                    thisBlockState = world.getBlockState(thisGetBlockPos);
                    if (thisBlockState.getBlock() instanceof BlockAir || HelpFunctions.isSameBlockType(thisBlockState,Blocks.BEDROCK))
                        break;
                    lastBlockState = thisBlockState;
                    Material mat = lastBlockState.getMaterial();
                    Block block = lastBlockState.getBlock();
                    if (!PlacementHelper.isPlant(lastBlockState) ||
                            ((block instanceof BlockLog) && mat == Material.WOOD ))
                        break;

                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(EnumFacing.NORTH, thisSetBlockPos, thisBlockState, null,TemplateBlock.EnumBlockType.PLANT));
                    thisGetBlockPos = thisGetBlockPos.up();
                    thisSetBlockPos = thisSetBlockPos.up();
                }
            }
            else
            {

                int diff = height - newHeight;
                //go down to copy the bottom layer of the terrain so it still appear like normal terrain
                //try to find stone,sandstone or nether if you don't find stone just copy the last blockstate and use that as filler
                //if you find air that probably means there is a cave don't disturb it and just copy the last block
                IBlockState lastBlockState = originalGroundBlock.getBlockState();
                IBlockState thisBlockState;
                BlockPos thisGetBlockPos = originalGroundBlock.getBlockPos();
                BlockPos thisSetBlockPos = originalGroundBlock.getBlockPos().add(0, -diff, 0);


                for (int h = 0; h < 6; h++)
                {
                    thisBlockState = world.getBlockState(thisGetBlockPos);
                    if (HelpFunctions.isSameBlockType(thisBlockState,Blocks.BEDROCK))
                        break;

                    if (thisBlockState.getBlock() instanceof BlockAir)
                    {
                        if (!(h < 3))
                            break;
                    }
                    else
                    {
                        lastBlockState = thisBlockState;
                    }

                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(originalGroundBlock.getFace(), thisSetBlockPos, lastBlockState, null,TemplateBlock.EnumBlockType.PLANT));
                    thisGetBlockPos = thisGetBlockPos.down();
                    thisSetBlockPos = thisSetBlockPos.down();
                }
//
                //check if there are plants on the first 2 layers on top of the current terrain
                //if there are also put the up
                //logs remain unaffected though
                int plantCount = 0;
                thisGetBlockPos = originalGroundBlock.getBlockPos().add(0,1,0);
                thisSetBlockPos = thisGetBlockPos.add(0, -diff, 0);
                for (int p = 0; p < 2; p++)
                {
                    thisBlockState = world.getBlockState(thisGetBlockPos);
                    if (thisBlockState.getBlock() instanceof BlockAir || HelpFunctions.isSameBlockType(thisBlockState,Blocks.BEDROCK))
                        break;
                    lastBlockState = thisBlockState;
                    Material mat = lastBlockState.getMaterial();
                    Block block = lastBlockState.getBlock();
                    if (!PlacementHelper.isPlant(lastBlockState) ||
                            ((block instanceof BlockLog) && mat == Material.WOOD ))
                        break;

                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(EnumFacing.NORTH, thisSetBlockPos, thisBlockState, null,TemplateBlock.EnumBlockType.PLANT));
                    plantCount++;
                    thisGetBlockPos = thisGetBlockPos.up();
                    thisSetBlockPos = thisSetBlockPos.up();
                }

                int toAddNumber = (diff-plantCount);

                for (int extraY = -plantCount; extraY < toAddNumber; extraY++)
                {
                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(originalGroundBlock.getFace(), originalGroundBlock.getBlockPos().add(0, -extraY, 0), Blocks.AIR.getDefaultState()));
                }

            }

        }
    }

    private void filterOutBadIndices(BlockPos size2DArray, int[][] newHeights, int[][] blockIndices)
    {
        filterOutBadIndices(size2DArray,newHeights,blockIndices,null,null);
    }

    private void filterOutBadIndices(BlockPos size2DArray, int[][] newHeights, int[][] blockIndices,int[] minYPerQaurter,int[] maxYPerQaurter)
    {
        for (int x = 0; x < size2DArray.getX() ; x++)
        {
            for (int z = 0; z < size2DArray.getZ() ; z++)
            {
                if (newHeights[x][z] == 0)
                    blockIndices[x][z] = -1;

            }
        }
    }

    private void onlyAllowChangeInOneDirection(BlockPos size2DArray, int[][] heights, int[][] newHeights, boolean raise, int aimHeight)
    {
        for (int x = 0; x < size2DArray.getX() ; x++)
        {
            for (int z = 0; z < size2DArray.getZ() ; z++)
            {
                if(newHeights[x][z] != 0)
                {
                    if(raise)
                    {
                        if (newHeights[x][z] <= heights[x][z] )
                            newHeights[x][z] = heights[x][z];
                        else if( heights[x][z] <= newHeights[x][z] && newHeights[x][z] > aimHeight)
                            newHeights[x][z] = aimHeight;
                    }
                    else
                    {
                        if (newHeights[x][z] >= heights[x][z] )
                            newHeights[x][z] = heights[x][z];
                        else if(heights[x][z] >= newHeights[x][z] && newHeights[x][z] < aimHeight)
                            newHeights[x][z] = aimHeight;
                    }
                }

            }
        }
    }


    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Float partialTicks) {

        if(EditMode.STARTVEC != null)
        {
            //draws the cross indicating the mouse position
           Vec3d middleVec = EditMode.STARTVEC;
            RenderTemplate.drawMiddleCross( entityplayer, partialTicks, middleVec);

            if (EditMode.LINEVEC != null && !sqaure)
            {
                // draw the lines of the circle
               Vec3d newCursorVec = RenderTemplate.drawCircleHelplines(entityplayer, partialTicks, middleVec,previewCircleHelpLineVecPoints,EditMode.RED ,EditMode.GREEN ,EditMode.BLUE );

                //draw the radius line
                RenderTemplate.drawLine(middleVec.x, middleVec.y, middleVec.z, newCursorVec.x,
                        newCursorVec.y, newCursorVec.z, entityplayer, 0, partialTicks,
                        EditMode.RED, EditMode.GREEN, EditMode.BLUE);

                //draw the line of the fill vectors
//                for (FillVector fillVec : EditMode.FILL_VECTOR_MAP.values())
//                {
//                    if (fillVec.startVec != null && fillVec.endVec != null)
//                        RenderTemplate.drawLine( fillVec.startVec, fillVec.endVec, entityplayer, 0,
//                                partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
//                }
            }
        }
    }

    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer,Vec3d hitVec, EnumFacing face, Float partialTicks) {
        int hashcode = 0;
        if(EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.attemptLocking())
        {
            EditMode.DRAW_PREVIEW_OUTLINE_ONLY = true;
            for (int i = 0; i < EditMode.CURR_PREVIEW_OUTLINE_BLOCKS.size(); i++)
            {
                TemplateBlock block = EditMode.CURR_PREVIEW_OUTLINE_BLOCKS.get(i);

                //when in delete mode change all outline blocks to air
                //this has no effect on the client preview but has the effect of drawing red outlines for all other players when they see your preview
                if(EditMode.DELETE_MODE)
                    block.setBlockState(Blocks.AIR.getDefaultState());

                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK,hitVec, face, block.getBlockPos());

//                if(indicesOfHighestLevelIndication.contains(i))
//                    RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.1F,0F,1.0F);
//                else if(indicesOfLowestLevelIndication.contains(i))
//                    RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,1.0F,0.1F,0.1F);
                if(indicesOfIgnoredBlocksIndication.contains(i))
                    drawIgnoredBlockIndication(entityplayer,partialTicks,new_position);
                else if(indicesOfToGroundChangedIndication.contains(i))
                {
                    if(blockIndicesToNewHeights.containsKey(i))
                    {
                        int thisY = (blockIndicesToNewHeights.get(i));
                        if(EditMode.DELETE_MODE && thisY == newBottomCenterHeight)
                        {
                            if(blockIndicesToOldBlock.containsKey(i) && blockIndicesToOldBlock.get(i).getY_offset() == thisY)
                                RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.existingLowerLevelColor);
                            else
                                RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.toBeAddedLowerLevelColor);
                        }
                        else if(!EditMode.DELETE_MODE && thisY == newTopCenterHeight)
                        {
                            if(blockIndicesToOldBlock.containsKey(i) && blockIndicesToOldBlock.get(i).getY_offset() == thisY)
                                RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.existingHighLevelColor);
                            else
                                RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.toBeAddedHighLevelColor);

                        }
                        else
                            RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.1F,1.0F,0.0F);
                    }
                    else
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.1F,1.0F,0.0F);


                }
                else
                {

                    int thisY = (new_position).getBlockPos().getY();
                    if(EditMode.DELETE_MODE && thisY == newBottomCenterHeight)
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.existingLowerLevelColor);
                    else if(!EditMode.DELETE_MODE && thisY == newTopCenterHeight)
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,LevelAdjustMode.existingHighLevelColor);
                    else
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.1F,0F,1.0F);
                }


                if(block.getBlockState() == null )
                     block.setBlockState(Blocks.AIR.getDefaultState());

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();;
                hashcode += ~~currHashcode;
            }

            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();
        }
        if(!EditMode.DELETE_MODE)
        {
            if (EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
            {
                for (TemplateBlock block : EditMode.CURR_PREVIEW_BLOCKS)
                {

                    RenderPreview.drawPreviewBlock( block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity()), entityplayer, partialTicks,face);

                    int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();
                    hashcode += ~~currHashcode;
                }
                EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
            }
        }


        return hashcode;
    }


    private void drawIgnoredBlockIndication(EntityPlayer entityplayer, Float partialTicks, RayTraceResult new_position)
    {
        RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, LevelAdjustMode.ignoredBlockColor);
    }


    private void copyCurrentWorldBlocks(ArrayList<TemplateBlock> toGround, EnumFacing face)
    {
        World world = Minecraft.getMinecraft().world;
        //is true while there is no different world block found
        Boolean NoWorldBlocksTheDiffFound = true;
        for (TemplateBlock tempBlock: toGround)
        {
            BlockPos pos  = tempBlock.getBlockPos();
            IBlockState currState = world.getBlockState(pos);
            if(!isNotGroundMaterial(currState))
            {
                tempBlock.setBlockState(currState);
            }
        }
        allWorldBlocksTheSameAsBrush =NoWorldBlocksTheDiffFound;
    }

    private static float[][] getProcessData(int dataIndex, boolean deleteMode)
    {

        if(deleteMode)
            return TerrainEditData.smoothingData.get(dataIndex);
        else
        {
            return TerrainEditData.sharpeningData.get(dataIndex);
        }

    }


    @Override
    public boolean allowRightClickException(Item item)
    {
        return true;
    }


    @Override
    public boolean managesPreviewBlocksItself()
    {
        return true;
    }

    @Override
    public boolean managesDeleteModeItself()
    {
        return true;
    }

    @Override
    public boolean usesOnlyTerrainOption()
    {
        return true;
    }

    @Override
    public boolean usesTerrainShapeOption()
    {
        return true;
    }


    @Override
    public String getGuiOverlayMessage(boolean deleteMode)
    {

        if(deleteMode)
            return TextFormatting.RED + "Dig" + TextFormatting.WHITE + " Mode";
        else
            return "Raise Mode";

    }
}
