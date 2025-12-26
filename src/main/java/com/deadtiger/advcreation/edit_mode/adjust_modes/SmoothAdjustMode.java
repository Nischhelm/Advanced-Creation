package com.deadtiger.advcreation.edit_mode.adjust_modes;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.debug.DebugInfo;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;

public class SmoothAdjustMode extends BaseAdjustMode
{
    //good positions to create nice circles
    private final ArrayList<Vec3d> previewCircleHelpLineVecPoints = new ArrayList<>();
    public EnumDirectionMode paintDirection = EnumDirectionMode.XZ;
    public boolean sqaure = false;
    public ArrayList<Integer> indicesOfToGroundChangedIndication = new ArrayList<>();
    public HashMap<Integer, Color> edgeColor = new HashMap<>();
    public HashMap<Integer, TemplateBlock> blockIndicesToOldBlock = new HashMap<>();
    public HashMap<Integer, Integer> blockIndicesToNewHeights = new HashMap<>();
    public ArrayList<Integer> indicesOfIgnoredBlocksIndication = new ArrayList<>();
    public ExtremaXYZ extremaXYZ = new ExtremaXYZ();

    //of all the block to be painted are the same as the brush block then don't change anything
    private boolean allWorldBlocksTheSameAsBrush = false;

    public boolean alternative = false;


    public SmoothAdjustMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "Smooth/Sharpen Mode";
        this.buttonText = "SMOOTH";
        this.tooltipText = "Smooth/Sharpen the terrain";
        this.identificationIndex = 2;

        addLegendEntry(LevelAdjustMode.toBeAddedLowerLevelColor,"Will move down");
        addLegendEntry(LevelAdjustMode.toBeAddedHighLevelColor,"Will move up");
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

//        Vec3d invFaceDirVec = Vec3d.ZERO;
//        if(EditMode.START_BLOCK != null)
//            EditMode.setStartBlock(new TemplateBlock(EditMode.getStartBlock().getFace(), EditMode.getStartBlock().getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), EditMode.getStartBlock().getBlockState(),EditMode.getStartBlock().getTileEntity()));
//        else
//            EditMode.setStartBlock(new TemplateBlock(EnumFacing.NORTH,BlockPos.ZERO.add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), Blocks.AIR.defaultBlockState()));
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
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        Vec3d invFaceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-EditMode.CURR_HITBLOCK_WIDTH);

//        if(face.equals(EnumFacing.NORTH) || face.equals(EnumFacing.SOUTH))
//            paintDirection = EnumDirectionMode.XY;
//        else if(face.equals(EnumFacing.WEST) || face.equals(EnumFacing.EAST))
//            paintDirection = EnumDirectionMode.ZY;
//        else
            paintDirection = EnumDirectionMode.XZ;

        EditMode.setStartBlock(new TemplateBlock(face,block.getBlockPos().add(invFaceDirVec.x,invFaceDirVec.y,invFaceDirVec.z), block.getBlockState(),block.getTileEntity()));
        EditMode.setSTARTVEC(hitVec.add(invFaceDirVec));
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block,hitVec);
    }

    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
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

        HelpFunctions.updateListToAirBlockAboveFirstSolidBlockInDir(toGround, face, 100);
        copyCurrentWorldBlocks(toGround, face);

        //add toGround to preview blocks
        if (EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.attemptLocking())
        {
            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS = new ArrayList<>(toGround);
            indicesOfToGroundChangedIndication = new ArrayList<>();
            indicesOfIgnoredBlocksIndication = new ArrayList<>();
            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();
        }

        if(toGround.isEmpty())
            return;
        //calculate smoothening data
        extremaXYZ = new ExtremaXYZ(toGround);
        float data[][] = this.getData(EditMode.DELETE_MODE);
        BlockPos size2DArray = new BlockPos(extremaXYZ.maxX - extremaXYZ.minX +data.length, 0, extremaXYZ.maxZ - extremaXYZ.minZ +data.length);
        int heights[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int newHeights[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int blockIndices[][] = new int[size2DArray.getX()][size2DArray.getZ()];
        int minPerQaurter[] = new int[8];
        int maxPerQaurter[] = new int[8];
        double avgSum = fillInitialHeightsArrayAndBlockIndices(toGround, extremaXYZ, heights, blockIndices);



        //calculate the egdes for sharpening but also for displaying the incline color map
        edgeColor = new HashMap<>();
        blockIndicesToOldBlock = new HashMap<>();
        blockIndicesToNewHeights = new HashMap<>();
        //first calculate the edges with a edgeData
        double[][] edgeData = calculateEdgeData(size2DArray, heights);

        double smoothEdgeData[][] =  new double[size2DArray.getX()][size2DArray.getZ()];
        TerrainEditProcessors.applyDataSmooth(size2DArray, edgeData, smoothEdgeData, TerrainEditData.data5);

        filterOutBadIndices(size2DArray, heights, blockIndices,minPerQaurter,maxPerQaurter);

        HashMap<Integer,ArrayList<BlockPos>> heightToCoordinates = new HashMap<>();
        HashMap<Integer,Color> heightsToColor = new HashMap<>();

        divideIntoPlatformsWhereEdgeDataIsNull(size2DArray, heights, blockIndices, smoothEdgeData, heightToCoordinates, heightsToColor);


        if(EditMode.DELETE_MODE)
        {
            //smoothening tool
            if(EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.STRAIGHT)
            {
                applyNewHeightsToTool(toGround, size2DArray, heights, heights, blockIndices, Minecraft.getMinecraft().world);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, heights, blockIndices, data, size2DArray, extremaXYZ);
            }
            else
            {
                TerrainEditProcessors.applyDataSmooth(size2DArray, heights, newHeights, data);
                filterOutBadIndices(size2DArray, newHeights, blockIndices);
                applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newHeights, blockIndices, data, size2DArray, extremaXYZ);
            }


        }
        else
        {

            //sharpening tool
            if(EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.CLIFF)
            {
                addRestingSlopesToClosestPlatform(size2DArray, heights, newHeights, blockIndices, heightToCoordinates, heightsToColor);

                int newestHeights[][] = new int[size2DArray.getX()][size2DArray.getZ()];

                TerrainEditProcessors.applyDataSmooth(size2DArray, newHeights, newestHeights, TerrainEditData.data4,avgSum,extremaXYZ.maxY,extremaXYZ.minY,minPerQaurter,maxPerQaurter);

                filterOutBadIndices(size2DArray, newestHeights, blockIndices);
                applyNewHeightsToTool(toGround, size2DArray, heights, newestHeights, blockIndices, Minecraft.getMinecraft().world);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newestHeights, blockIndices, data, size2DArray, extremaXYZ);
            }
            else if(EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.STRAIGHT)
            {
                addRestingSlopesToClosestPlatformForDrop(size2DArray, heights, newHeights, blockIndices, heightToCoordinates,minPerQaurter,maxPerQaurter, radiusLength);
                filterOutBadIndices(size2DArray, newHeights, blockIndices);
                applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newHeights, blockIndices, data, size2DArray, extremaXYZ);
            }
            else
            {
                TerrainEditProcessors.applyDataSmooth(size2DArray, heights, newHeights, data,avgSum,extremaXYZ.maxY,extremaXYZ.minY);
                filterOutBadIndices(size2DArray, newHeights, blockIndices);
                applyNewHeightsToTool(toGround, size2DArray, heights, newHeights, blockIndices, Minecraft.getMinecraft().world);
                addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(toGround, newHeights, blockIndices, data, size2DArray, extremaXYZ);

            }


        }
    }

    private void addRestingSlopesToClosestPlatform(BlockPos size2DArray, int[][] heights, int[][] newHeights, int[][] blockIndices, HashMap<Integer, ArrayList<BlockPos>> heightToCoordinates, HashMap<Integer, Color> heightsToColor)
    {
        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {

                int chosenHeight = 0;
                double minDist = 1000;
                for (int surfaceHeight: heightToCoordinates.keySet())
                {
                    for(BlockPos part: heightToCoordinates.get(surfaceHeight))
                    {
                        double dist = part.distanceSq(new BlockPos(x,0,z));
                        if(dist < minDist)
                        {
                            chosenHeight = surfaceHeight;
                            minDist = dist;
                        }
                    }
                }




                if(heights[x][z] == 0 || heights[x][z] == chosenHeight )
                    newHeights[x][z] = heights[x][z];
                else if( heights[x][z] >= chosenHeight)
                    newHeights[x][z] = heights[x][z] -1;
                else
                    newHeights[x][z] = heights[x][z] +1;
            }
        }
    }

    private void addRestingSlopesToClosestPlatformForDrop(BlockPos size2DArray, int[][] heights, int[][] newHeights, int[][] blockIndices, HashMap<Integer, ArrayList<BlockPos>> heightToCoordinates, int[] minYPerQaurter, int[] maxYPerQaurter, double radiusLength)
    {
        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {


//                int qaurterIndex = getQaurterIndex(heights,x,z);
////                if(heights[x][z] == extremaXYZ.maxY || heights[x][z] == extremaXYZ.minY)
//                if(heights[x][z] == maxYPerQaurter[qaurterIndex] || heights[x][z] == minYPerQaurter[qaurterIndex])
//                {
//                    newHeights[x][z] = heights[x][z];
//                    continue;
//                }


                int chosenHeight = 0;
                double minDist = 1000;
                int numberOfBlocksInSurface = 0;
                boolean skipChecks = false;


                for (int surfaceHeight: heightToCoordinates.keySet())
                {
                    numberOfBlocksInSurface = heightToCoordinates.get(surfaceHeight).size();
                    if(numberOfBlocksInSurface < (radiusLength*0.75))
                    {
                        continue;
                    }
                    for(BlockPos part: heightToCoordinates.get(surfaceHeight))
                    {
                        double dist = part.distanceSq(new BlockPos(x,0,z));

                        //if the block is next to a surface at the same height and only 3 or less surfaces remain let it remain there
                        if(dist < 5 && numberOfBlocksInSurface > (radiusLength*0.75) &&  heights[x][z] == surfaceHeight)
                        {
                            chosenHeight = surfaceHeight;
                            skipChecks = true;
                            break;
                        }
                        if(dist < minDist)
                        {
                            chosenHeight = surfaceHeight;


                            minDist = dist;
                        }

                    }
                    if(skipChecks)
                        break;
                }

                if(heights[x][z] == 0 || heights[x][z] == chosenHeight )
                    newHeights[x][z] = heights[x][z];
                else if( heights[x][z] >= chosenHeight)
                    newHeights[x][z] = heights[x][z] -1;
                else
                    newHeights[x][z] = heights[x][z] +1;
            }
        }
    }

    private void divideIntoPlatformsWhereEdgeDataIsNull(BlockPos size2DArray, int[][] heights, int[][] blockIndices, double[][] smoothEdgeData, HashMap<Integer, ArrayList<BlockPos>> heightToCoordinates, HashMap<Integer, Color> heightsToColor)
    {
        double highestEdgeDataValue = 0;
        int count = 0;
        double sumEdgeDataValue = 0;
        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {

                float red= 0.2f ;
                float green = 0.2f;
                float blue= 0.2f;

                if(smoothEdgeData[x][z] < 5)
                {
                    blue = 1f;
                    green = (float)(smoothEdgeData[x][z]/5f);
                    if(green > 1f)
                        green = 1f;
                }
                else
                {
                    if(smoothEdgeData[x][z] < 10)
                    {
                        blue =  (float)((10- smoothEdgeData[x][z])/5f);
                        green = 1f;
                        if(blue < 0)
                            blue = 0f;
                    }
                    else
                    {
                        if(smoothEdgeData[x][z] < 15)
                        {
                            red = (float)((smoothEdgeData[x][z]-10)/5f);
                            green = 1f;
                            if(red > 1f)
                                red = 1f;
                        }
                        else
                        {
                            red = 1f;
                            green = (float)((20- smoothEdgeData[x][z])/5f);
                            if(green < 0)
                                green = 0f;
                        }
                    }


                }


                if(smoothEdgeData[x][z]<= 1.0)
                {
                    if(heights[x][z] != 0)
                    {



                        if(heightToCoordinates.containsKey(heights[x][z]))
                            heightToCoordinates.get(heights[x][z]).add(new BlockPos(x,0,z));
                        else
                        {
                            heightsToColor.put(heights[x][z], DebugInfo.debugColor(heightToCoordinates.size()));
                            heightToCoordinates.put(heights[x][z],new ArrayList<>());
                            heightToCoordinates.get(heights[x][z]).add(new BlockPos(x,0,z));
                        }
                    }
                }
                else
                {
                    sumEdgeDataValue += smoothEdgeData[x][z];
                    count++;

                    if(highestEdgeDataValue < smoothEdgeData[x][z])
                        highestEdgeDataValue = smoothEdgeData[x][z];
                }
//                    else
//                    {
//                        red = 1f;
//                        green = 0f;
//                        blue = 1f;
//                    }

                Color newColor = new Color(red,green,blue);
                edgeColor.put(blockIndices[x][z], newColor);
            }
        }
//        if(AdvCreation.debugMode && !Minecraft.getMinecraft().isGamePaused())
//            System.out.println("avgEdgeData = " + sumEdgeDataValue/count + " ; count = " + count + " ; max = " + highestEdgeDataValue);

    }

    private double[][] calculateEdgeData(BlockPos size2DArray, int[][] heights)
    {
        double edgeProcessData[][] =  new double[size2DArray.getX()][size2DArray.getZ()];
        double edges1[][] = new double[size2DArray.getX()][size2DArray.getZ()];
        double edges2[][] = new double[size2DArray.getX()][size2DArray.getZ()];

        TerrainEditProcessors.applyDataSmooth(size2DArray, heights, edges1, TerrainEditData.data9);
        TerrainEditProcessors.applyDataSmooth(size2DArray, heights, edges2, TerrainEditData.data10);


        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {
                double newEdgeData = Math.sqrt(Math.pow(edges1[x][z], 2) + Math.pow(edges2[x][z], 2));
                edgeProcessData[x][z] = newEdgeData;
            }
        }
        return edgeProcessData;
    }

    private double fillInitialHeightsArrayAndBlockIndices(ArrayList<TemplateBlock> toGround, ExtremaXYZ extremaXYZ, int[][] heights, int[][] blockIndices)
    {
        int relXPos;
        int relZPos;
        int avgSum = 0;
        //fill heigts 2D array with the blocks Y value
        for (int i = 0; i < toGround.size(); i++)
        {
            TemplateBlock block = toGround.get(i);
            relXPos = block.getX_offset() - (extremaXYZ.minX - 1);
            relZPos = block.getZ_offset() - (extremaXYZ.minZ - 1);
            if(HelpFunctions.stateIsAllowed(block.getBlockState(),EditMode.ONLY_TERRAIN_MODE))
            {
                heights[relXPos][relZPos] = block.getY_offset();
                blockIndices[relXPos][relZPos] = i;
            }
            else
            {
                heights[relXPos][relZPos] = 0;
                blockIndices[relXPos][relZPos] = -1;
                indicesOfIgnoredBlocksIndication.add(i);

            }

            avgSum += block.getY_offset();
        }
        return avgSum/((double) toGround.size());
    }

    private void applyNewHeightsToTool(ArrayList<TemplateBlock> toGround, BlockPos size2DArray, int[][] heights, int[][] newHeights, int[][] blockIndices, World world)
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

                    if ( HelpFunctions.isSameBlockType(lastBlockState,Blocks.STONE) ||
                            HelpFunctions.isSameBlockType(lastBlockState,Blocks.SANDSTONE) ||
                            HelpFunctions.isSameBlockType(lastBlockState,Blocks.DIRT))
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

                    EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(originalGroundBlock.getFace(), thisSetBlockPos, lastBlockState));
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
                {
                    blockIndices[x][z] = -1;
                }
                else
                {
                    if(minYPerQaurter != null)
                    {
                        //calculate minimum height for each qaurter of the circle/sqaure
                        //this is done by calculating the angle in radials of the coordinate versus the middle of the area in the XZ plane
                        int qaurterIndex = getQaurterIndex(newHeights, x, z);
                        trySetNewMinHeight(newHeights[x][z], minYPerQaurter, qaurterIndex);
                        trySetNewMaxHeight(newHeights[x][z], maxYPerQaurter, qaurterIndex);
                    }
                }
            }
        }
    }

    public static int getQaurterIndex(int[][] newHeights, int x, int z)
    {
        int qaurterIndex;
        int middleX = newHeights.length/2;
        int middleZ = 0;
        if(middleX> 0)
            middleZ = newHeights[0].length/2;

        int relX = x - middleX;
        int relZ = z - middleZ;

        if(relX == 0)
        {
            //relX == 0 meant atan(relZ/relX) wont work so I need to chose where it goes now
            if(relZ < 0)
                qaurterIndex = 7;
            else
                qaurterIndex = 4;
        }
        else
        {
            double angle = MathHelper.atan2(relZ,relX);
            double edge = Math.PI/4;
            if(relZ > 0)
            {
                //underside of the circle
                if(angle > edge)
                   qaurterIndex = 7;
                else if(angle > 0)
                    qaurterIndex =6;
                else if(angle >= -edge)
                    qaurterIndex =1;
                else
                    qaurterIndex =0;
            }
            else
            {
                //top side of circle
                if(angle >= edge)
                    qaurterIndex =3;
                else if(angle > 0)
                    qaurterIndex =2;
                else if(angle >= -edge)
                    qaurterIndex =5;
                else
                    qaurterIndex =4;
            }
        }
        return qaurterIndex;
    }

    private void trySetNewMinHeight(int height, int[] minYPerQaurter, int qaurterIndex)
    {
        if(height != 0)
            minYPerQaurter[qaurterIndex] =  (minYPerQaurter[qaurterIndex]==0)? height:Math.min(height, minYPerQaurter[qaurterIndex]);
    }

    private void trySetNewMaxHeight(int height, int[] minYPerQaurter, int qaurterIndex)
    {
        if(height != 0)
            minYPerQaurter[qaurterIndex] =  (minYPerQaurter[qaurterIndex]==0)? height:Math.max(height, minYPerQaurter[qaurterIndex]);
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
                Vec3d newCursorVec = RenderTemplate.drawCircleHelplines(entityplayer, partialTicks, middleVec,previewCircleHelpLineVecPoints,EditMode.RED ,EditMode.GREEN ,EditMode.BLUE );

                //draw the radius line
                RenderTemplate.drawLine(middleVec.x, middleVec.y, middleVec.z, newCursorVec.x,
                        newCursorVec.y, newCursorVec.z, entityplayer, 0, partialTicks,
                        EditMode.RED, EditMode.GREEN, EditMode.BLUE);

                //draw the line of the fill vectors
//                for (FillVector fillVec : EditMode.FILL_VECTOR_MAP.values())
//                {
//                    if (fillVec.startVec != null && fillVec.endVec != null)
//                        RenderTemplate.drawLine(matrixStack, buffer, fillVec.startVec, fillVec.endVec, entityplayer, 0,
//                                partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
//                }
            }
        }
    }

    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
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

                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec, face, block.getBlockPos());

                if(indicesOfIgnoredBlocksIndication.contains(i))
                    drawIgnoredBlockIndication(entityplayer,partialTicks,new_position);
                else if(indicesOfToGroundChangedIndication.contains(i))
                {
                    Color color;
                    int newHeight = blockIndicesToNewHeights.get(i);
                            int oldHeight = blockIndicesToOldBlock.get(i).getY_offset();
                    if( newHeight > oldHeight)
                        color =  new Color(120, 255, 197);
                    else
                        color =  new Color(255,130,130);

                    RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,color);

                }
                else
                    RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.1F,0F,1.0F);

                if(block.getBlockState() == null )
                     block.setBlockState(Blocks.AIR.getDefaultState());

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();;
                hashcode += ~~currHashcode;
            }

            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();
        }

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
//            IBlockState startWorldState = world.getBlockState(EditMode.START_BLOCK.getBlockPos());
//            boolean excludeActive = (startWorldState.getBlock() instanceof SlabBlock ||
//                    startWorldState.getBlock() instanceof StairsBlock ||
//                    startWorldState.getBlock() instanceof FenceBlock);

//            if(!isNotGroundMaterial(currState) && isNotGroundMaterial(iblockstate3))
            if(!isNotGroundMaterial(currState))
            {
                tempBlock.setBlockState(currState);
//                boolean sameBlockClassAsStart = startWorldState.getBlock().getClass().equals(currState.getBlock().getClass());
//                if(!excludeActive || sameBlockClassAsStart)
//                {
//                    if(tempBlock.getBlockState() != null)
//                    {
//                        //if you want to replace a block with the same block type all properties are copied
//                        if(tempBlock.getBlockState().getBlock().getClass().equals(currState.getBlock().getClass()))
//                        {
//                            //the class of the current block is the same as
//                            IBlockState newBlockstate = tempBlock.getBlockState();
//                            for (Property key :currState.getProperties())
//                            {
//                                if(!(key.getName().equals("color")) && !(key.getName().equals("variant")))
//                                {
//                                    newBlockstate = newBlockstate.setValue(key,currState.getValue(key));
//                                }
//                            }
//
//                            if(!currState.equals(newBlockstate))
//                                NoWorldBlocksTheDiffFound = false;
//
//                            tempBlock.setBlockState(newBlockstate);
//                        }
//                        else
//                            NoWorldBlocksTheDiffFound = false;
//                    }
//                    EditMode.CURR_TOOL_BLOCKS.add(tempBlock);
//                }
            }
        }
        allWorldBlocksTheSameAsBrush =NoWorldBlocksTheDiffFound;
    }

    private float[][] getData(boolean deleteMode)
    {

        if(deleteMode)
            return TerrainEditData.smoothingData.get( EditMode.TERRAIN_SHAPE_MODE.getDataIndex());
        else
        {
            return TerrainEditData.sharpeningData.get( EditMode.TERRAIN_SHAPE_MODE.getDataIndex());
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
            return TextFormatting.RED + "Smooth" + TextFormatting.WHITE + " Mode";
        else
            return "Sharpen Mode";

    }
}
