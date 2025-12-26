package com.deadtiger.advcreation.utility;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PlacementHelper
{

    public static IBlockState orientBasedOnCamera(IBlockState handBlockState)
    {

        Vec3d usedVec = IsometricCamera.CAMERA_LOOK_VECTOR;
        if (ModEntity.currMouseVec != null)
            usedVec = ModEntity.currMouseVec;

        ArrayList<EnumFacing> horizontalFaces;
        //when you have a blocklog you also want to place it upwards when orienting by camera
        if (handBlockState.getBlock() instanceof BlockLog || handBlockState.getBlock() instanceof BlockOldLog || handBlockState.getBlock() instanceof BlockDirectional)
            horizontalFaces = new ArrayList<>(Arrays.asList(EnumFacing.values()));
        else
            horizontalFaces = new ArrayList<>(Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST));


        EnumFacing cameraFace = EnumFacing.NORTH;

        double maxLength = 0;
        for (EnumFacing newFace : horizontalFaces)
        {
            Vec3i dir = newFace.getDirectionVec();
            double newLength = Math.max(Math.max(dir.getX() * usedVec.x,
                    dir.getY() * usedVec.y), dir.getZ() * usedVec.z);
            if (newLength > maxLength)
            {
                maxLength = newLength;
                cameraFace = newFace;
            }
        }
        if(handBlockState.getBlock() instanceof BlockDirectional || handBlockState.getBlock() instanceof BlockRedstoneDiode ||
                handBlockState.getBlock() instanceof BlockLadder)
            handBlockState = rotateToFace(handBlockState, cameraFace.getOpposite());
        else
            handBlockState = rotateToFace(handBlockState, cameraFace);

        return handBlockState;
    }

    public static IBlockState orientBasedOnSideHit(IBlockState handBlockState, EnumFacing face, Vec3d hitVec)
    {
        if (handBlockState.getBlock() instanceof BlockTorch ||
                handBlockState.getBlock() instanceof BlockRedstoneTorch ||
                handBlockState.getBlock() instanceof BlockDirectional ||
                handBlockState.getBlock() instanceof BlockRedstoneDiode ||
                handBlockState.getBlock() instanceof BlockTripWireHook ||
                        handBlockState.getBlock() instanceof BlockLadder )
            handBlockState = rotateToFace(handBlockState, face);
        else if (handBlockState.getBlock() instanceof BlockDoor)
        {
            if (face.getAxis() == EnumFacing.Axis.Z)
            {
                double hitX = hitVec.x - Math.floor(hitVec.x);
                face = (hitX > 0.5) ? EnumFacing.WEST : EnumFacing.EAST;
            }
            else
            {
                double hitZ = hitVec.z - Math.floor(hitVec.z);

                face = (hitZ > 0.5) ? EnumFacing.NORTH : EnumFacing.SOUTH;
            }

            handBlockState = rotateToFace(handBlockState, face);
        }
        else
            handBlockState = rotateToFace(handBlockState, face.getOpposite());

        return handBlockState;
    }

    public static IBlockState rotateToFace(IBlockState handBlockState, EnumFacing face)
    {
        for (IProperty property : handBlockState.getPropertyKeys())
        {
            if (property instanceof PropertyDirection)
            {
                try
                {
                    PropertyDirection propertyDir = (PropertyDirection) property;
                    if (!(handBlockState.getBlock() instanceof BlockLog))
                    {
                        handBlockState = handBlockState.withProperty(propertyDir, face);
                    }

                }
                catch (Exception e)
                {
                    System.out.println("exception catch " + e);
                }

            }
            else if (handBlockState.getValue(property) instanceof BlockLog.EnumAxis)
            {
                BlockLog.EnumAxis blockLogAxis = BlockLog.EnumAxis.Y;
                if (face.getAxis() == EnumFacing.Axis.X)
                    blockLogAxis = BlockLog.EnumAxis.X;
                else if (face.getAxis() == EnumFacing.Axis.Z)
                    blockLogAxis = BlockLog.EnumAxis.Z;
                //special case when using a log it doesn't not have the PropertyDirection property it has in stead an axis property
                handBlockState = handBlockState.withProperty(property, blockLogAxis);
            }
        }
        return handBlockState;
    }

    public static Vec3d getAdjustedHitVec(RayTraceResult objectMouseOver, double v)
    {
        //change the hitvec just a little so the hit is still in the block before the hitblock
        Vec3d direction = new Vec3d(
                objectMouseOver.sideHit.getDirectionVec().getX(),
                objectMouseOver.sideHit.getDirectionVec().getY(),
                objectMouseOver.sideHit.getDirectionVec().getZ());
        //trying to compensate for the hitvecs on stairs and fences not being at the edge of the block
//        return objectMouseOver.hitVec.add(direction.scale(v));
        Vec3d hitVec = objectMouseOver.hitVec;
        double newVecX = (direction.x == 0)? hitVec.x : ((direction.x < 0)?Math.floor(hitVec.x) : Math.ceil(hitVec.x));
        double newVecY = (direction.y == 0)? hitVec.y : ((direction.y < 0)?Math.floor(hitVec.y) : Math.ceil(hitVec.y));
        double newVecZ = (direction.z == 0)? hitVec.z : ((direction.z < 0)?Math.floor(hitVec.z) : Math.ceil(hitVec.z));
        return new Vec3d(newVecX,newVecY,newVecZ).add(direction.scale(v));

    }

    public static boolean isInteractableBlock(Block block, BlockPos pos)
    {
        return block instanceof BlockDoor ||
                block instanceof BlockTrapDoor ||
                block instanceof BlockRedstoneDiode ||
                block instanceof BlockButton ||
                block instanceof BlockLever ||
                block instanceof BlockTripWireHook ||
                (block instanceof BlockContainer &&
            !(Minecraft.getMinecraft().player.getDistanceSqToCenter(pos) > 64.0D) );

    }

    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec, boolean dontDrawFirst, boolean dontDrawLast, TemplateBlock startBlock, ArrayList<TemplateBlock> blockList)
    {
        Vec3d endVec = startVec.add(toAddVec);
        TemplateBlock endBlock = new TemplateBlock(startBlock.getFace(),new BlockPos(endVec),startBlock.getBlockState());
        Vec3d toAddVecFirst = toAddVec.scale(0.5);
        Vec3d toAddVecSecond = toAddVec.subtract(toAddVecFirst).add(toAddVec.normalize().scale(0.5));
        drawBlockLineHalf(startVec,toAddVecFirst,dontDrawFirst,false,startBlock,blockList);//forward line from start to middle
        drawBlockLineHalf(endVec,toAddVecSecond.scale(-1f),dontDrawLast,true,endBlock,blockList);//backwards line from end to middle

    }


    public static void drawBlockLineHalf(Vec3d startVec, Vec3d toAddVec, boolean dontDrawFirst, boolean dontDrawLast, TemplateBlock startBlock, ArrayList<TemplateBlock> blockList)
    {

        //this begining and the end position
        BlockPos firstPosition = new BlockPos(startVec);
        Vec3d currEndVec = startVec.add(toAddVec);
        BlockPos lastPosition = new BlockPos(currEndVec);

        //unit vec and the number of times to iterate
        Vec3d unitVector = toAddVec.normalize().scale(0.10);
        double toAddVeclenght = toAddVec.lengthVector();
        int times = (int) Math.ceil(toAddVeclenght / (unitVector.lengthVector()));

        //variables that are used in the loop
        Vec3d stepsTo = new Vec3d(0, 0, 0);
        BlockPos currPosition = new BlockPos(startBlock.getBlockPos());
        BlockPos[] prevPositions = {null,null,null};//{previousPosition,previousPreviousPosition,lastCheckedPosition}
        ArrayList<BlockPos> positionsList = new ArrayList<>();

        int[] votes = new int[2];


        for (int i = 0; i < times+4; i++) {
            Vec3d checkStepsTo = stepsTo.add(unitVector);

            //if the new vector is to long half the unitVector once and see if it fits
            if(checkStepsTo.lengthVector() <= toAddVeclenght)
            {
                stepsTo = checkStepsTo;
            }
            else
            {
                unitVector = unitVector.scale(0.5);
                stepsTo = stepsTo.add(unitVector);
            }

            if(stepsTo.lengthVector() <= toAddVeclenght)
            {
                currPosition = new BlockPos(startVec.add(stepsTo));

                if (startBlock.getBlockState() != null && isWireRailOrNeedsConnection(startBlock))
                {
                    if (processNewWirePosition(currPosition,startVec.add(stepsTo), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                        break;
                }
                if (startBlock.getBlockState() != null && isFourWayBlock(startBlock))
                {
                    if (processNewFourWayBlockPosition(currPosition,startVec.add(stepsTo), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                        break;
                }
//                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast,votes, null, null))
                    break;
            }
        }

        //place a block for all the positions in the positionslist
        for (BlockPos pos: positionsList)
        {
            placePositionDirect(pos,startBlock, blockList);
        }


        //make sure the end vector is also drawn
        if (startBlock.getBlockState() != null && isWireRailOrNeedsConnection(startBlock))
        {
            if ((prevPositions[0] == null || (prevPositions[0].equals(currPosition) && !currPosition.equals(firstPosition) && (prevPositions[1]==null || !(prevPositions[1].getX() == currPosition.getX() && prevPositions[1].getZ() == currPosition.getZ())) )|| !(prevPositions[0].getX() == currPosition.getX() && prevPositions[0].getZ() == currPosition.getZ())) && !dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
            {
                placePositionDirect(currPosition, startBlock, blockList);
                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
                {
                    BlockPos intermediatePosition = calculateIntermediatePosition(currPosition,startVec.add(stepsTo), prevPositions[0]);
                    if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
                        placePositionDirect(intermediatePosition, startBlock, blockList);
                }
            }
        }
        else if (startBlock.getBlockState() != null && isFourWayBlock(startBlock))
        {
            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size()-1).equals(currPosition)))
            {
                placePositionDirect(currPosition, startBlock, blockList);
                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
                {
                    BlockPos intermediatePosition = calculateIntermediatePosition3D(currPosition,startVec.add(stepsTo), prevPositions[0]);
                    placePositionDirect(intermediatePosition, startBlock, blockList);
                }
            }
        }
        else
        {
            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
                placePositionDirect(currPosition, startBlock, blockList);
        }

    }



    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec, TemplateBlock startBlock, HashMap<Vec3d,TemplateBlock> dict)
    {
        drawBlockLine(startVec,toAddVec,startBlock,false,false,dict);

    }

    /**
     * draws a line of blocks and put them into a Hashmap called dict this is used further in later code
     */

    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec,TemplateBlock startBlock,boolean dontDrawFirst,boolean dontDrawLast, HashMap<Vec3d,TemplateBlock> dict) {

        Vec3d endVec = startVec.add(toAddVec);
        TemplateBlock endBlock = new TemplateBlock(startBlock.getFace(),new BlockPos(endVec),startBlock.getBlockState());

//        Vec3d norm = toAddVec.normalize();
//        double maxDir = Math.max(Math.abs(norm.x),Math.abs(norm.y));
//        maxDir = Math.max(maxDir,Math.abs(norm.z));
//        Vec3d adding = new Vec3d(norm.x/maxDir,norm.y/maxDir,norm.z/maxDir);
//
//        Vec3d toAddVecFirst = toAddVec.scale(0.5).add(adding.scale(1.05));
//        Vec3d toAddVecSecond = toAddVec.subtract(toAddVecFirst).add(adding.scale(1.05));
//        BlockPos[] lastPlacedPosition = drawBlockLineHalf(startVec,toAddVecFirst,startBlock,dontDrawFirst,true,dict,null);//forward line from start to middle
////        BlockPos lastPlacedPosition = null;
//        drawBlockLineHalf(endVec,toAddVecSecond.scale(-1f),endBlock,dontDrawLast,true,dict,lastPlacedPosition);//backwards line from end to middle

        //always go from low coordinate to high coordinate

        double startVecPoints = startVec.x + startVec.y + startVec.z;
        double endVecPoints = endVec.x + endVec.y + endVec.z;
        if(endVecPoints < startVecPoints)
            drawBlockLineHalf(startVec, toAddVec, startBlock,dontDrawFirst,dontDrawLast,dict,null);
        else
            drawBlockLineHalf(endVec, toAddVec.scale(-1),startBlock,dontDrawFirst,dontDrawLast,dict,null);


//        drawBlockLineHalf(startVec,toAddVec,startBlock,dontDrawFirst,dontDrawLast,dict,null);


//        drawBlockLineHalf(startVec,toAddVec,startBlock,dontDrawFirst,dontDrawLast,dict);
    }

    public static BlockPos[] drawBlockLineHalf(Vec3d startVec, Vec3d toAddVec,TemplateBlock startBlock,boolean dontDrawFirst,boolean dontDrawLast, HashMap<Vec3d,TemplateBlock> dict, BlockPos[] dontPlaceNextTo) {

        //this beginning and the end position
        BlockPos firstPosition = new BlockPos(startVec);
        Vec3d currEndVec = startVec.add(toAddVec);
        BlockPos lastPosition = new BlockPos(currEndVec);
        BlockPos lastPlacedPosition[]  = {null};

        //unit vec and the number of times to iterate
        Vec3d unitVector = toAddVec.normalize().scale(0.10);
        double toAddVeclenght = toAddVec.lengthVector();
        int times = (int) Math.ceil(toAddVeclenght / (unitVector.lengthVector()));

        //variables that are used in the loop
        Vec3d currStep = new Vec3d(0, 0, 0);
        BlockPos currPosition = new BlockPos(startBlock.getBlockPos());
        BlockPos[] prevPositions = {null,null,null};//{previousPosition,previousPreviousPosition,lastCheckedPosition}

        int[] votes = new int[2];

        ArrayList<BlockPos> positionsList = new ArrayList<>();


        Vec3d nextStep = currStep;
        for (int i = 0; i < times+4; i++) {

            //if the new vector is to long half the unitVector once and see if it fits
            if(nextStep.lengthVector() <= toAddVeclenght)
            {
                currStep = nextStep;
            }
            else
            {
                unitVector = unitVector.scale(0.5);
                currStep = currStep.add(unitVector);
            }

            if(currStep.lengthVector() <= toAddVeclenght)
            {
                currPosition = new BlockPos(startVec.add(currStep));
                if (startBlock.getBlockState() != null && isWireRailOrNeedsConnection(startBlock))
                {
                    if (processNewWirePosition(currPosition,startVec.add(currStep), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                        break;
                }
                if (startBlock.getBlockState() != null && isFourWayBlock(startBlock))
                {
                    if (processNewFourWayBlockPosition(currPosition,startVec.add(currStep), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                        break;
                }
//                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast,votes, dontPlaceNextTo,lastPlacedPosition ))
                    break;
            }
            nextStep = currStep.add(unitVector);

        }
        //place a block for all the positions in the positionslist
        for (BlockPos pos: positionsList)
        {
            placePositionInDict(pos,startBlock, dict);
        }

        //make sure the end vector is also drawn
        if (startBlock.getBlockState() != null && isWireRailOrNeedsConnection(startBlock))
        {
            if ((prevPositions[0] == null || (prevPositions[0].equals(currPosition) && !currPosition.equals(firstPosition) && (prevPositions[1]==null || !(prevPositions[1].getX() == currPosition.getX() && prevPositions[1].getZ() == currPosition.getZ())) )|| !(prevPositions[0].getX() == currPosition.getX() && prevPositions[0].getZ() == currPosition.getZ())) && !dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
            {
                placePositionInDict(currPosition,startBlock, dict);
                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
                {
                    BlockPos intermediatePosition = calculateIntermediatePosition(currPosition,startVec.add(currStep), prevPositions[0]);
                    if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
//                        placePositionInDict(currPosition,startBlock, dict);
                        placePositionInDict(intermediatePosition ,startBlock, dict);
                }
            }
            lastPlacedPosition  = null;

        }
        else if (startBlock.getBlockState() != null && isFourWayBlock(startBlock))
        {
            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size()-1).equals(currPosition)))
            {
                placePositionInDict(currPosition,startBlock, dict);
                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
                {
                    BlockPos intermediatePosition = calculateIntermediatePosition3D(currPosition,startVec.add(currStep), prevPositions[0]);
                    placePositionInDict(intermediatePosition ,startBlock, dict);
                }
            }
            lastPlacedPosition  = null;
        }
        else
        {
            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size()-1).equals(currPosition)))
            {
                if((prevPositions[1] == null || dontPlaceNextTo == null ||  (!isDiagonal(prevPositions[1],dontPlaceNextTo[0]) || !isNext(prevPositions[0],dontPlaceNextTo[0]))))
                {
                    placePositionInDict(currPosition,startBlock, dict);
                    if(lastPlacedPosition != null)
                        lastPlacedPosition[0]  = currPosition;
                }
            }
        }
        return lastPlacedPosition;
    }

    public static boolean isNext(BlockPos toCheckPos, BlockPos refPos)
    {
        return refPos.getDistance (toCheckPos.getX(),toCheckPos.getY(),toCheckPos.getZ()) == 1;
    }

    public static boolean processNewPosition(BlockPos currPosition, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast, int[] votes, BlockPos[] dontDrawNextTo, BlockPos lastPlacedPosition[])
    {
        //complicated mess to ensure no duplicates and no corner blocks
        //Checks if the previous position is not the same as the first
        //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
        boolean changePrev = false;
        boolean changePrevPrev = false;
        boolean equalVotes = false;
        BlockPos lastCheckedPosition = prevPositions[2];
        if(!currPosition.equals(lastCheckedPosition))
        {
            if( votes[1] == votes[0])
                equalVotes = true;

            votes[1] = votes[0];
            votes[0] = 0;
            prevPositions[2] = currPosition;
        }
        else
            votes[0]++;

        if (prevPositions[0] == null)
            changePrev = true;
        else if (!prevPositions[0].equals(currPosition))
        {
            if (prevPositions[1] == null)
            {
                if((!dontDrawFirst || !prevPositions[0].equals(firstPosition)))
                {
                    positionsList.add(prevPositions[0]);
                    if(lastPlacedPosition != null)
                        lastPlacedPosition[0] = prevPositions[0];
                }
                changePrev = true;
                changePrevPrev = true;
            }
            else
            {
                if (isDiagonal(prevPositions[1], currPosition))
                {
                    if(equalVotes)
                    {
                        //when you come to a new blockPos that is diagonal with the previousPreviousBlockPos then
                        //take the one that is has to lowest coordinate thereby al lines will always be drawn the same
                        //no matter the direction you are drawing
                        if(currPosition.getZ() < prevPositions[0].getZ())
                            changePrev = true;
                        if(currPosition.getX() < prevPositions[0].getX())
                            changePrev = true;
                        if(currPosition.getY() < prevPositions[0].getY())
                            changePrev = true;
                    }
                    else if(votes[0] > votes[1])
                        changePrev = true;
                }
                else {
                    if((!dontDrawFirst || !prevPositions[0].equals(firstPosition)) &&
                            (dontDrawNextTo == null || !isDiagonal(prevPositions[1],dontDrawNextTo[0]) || !isNext(prevPositions[0],dontDrawNextTo[0])))
                    {
                        positionsList.add(prevPositions[0]);
                        if(lastPlacedPosition != null)
                            lastPlacedPosition[0] = prevPositions[0];
                    }
                    changePrev = true;
                    changePrevPrev = true;
                }
            }
            if(dontDrawLast && currPosition.equals(lastPosition))
                return true;

        }
        if (changePrevPrev)
            //prevPrevPositions = prevPosition;
            prevPositions[1] = prevPositions[0];
        if (changePrev)
            prevPositions[0] = currPosition;
        return false;
    }

//    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec, boolean dontDrawFirst, boolean dontDrawLast, TemplateBlock startBlock, ArrayList<TemplateBlock> blockList) {
//
//        //this begining and the end position
//        BlockPos firstPosition = new BlockPos(startVec);
//        Vec3d currEndVec = startVec.add(toAddVec);
//        BlockPos lastPosition = new BlockPos(currEndVec);
//
//        //unit vec and the number of times to iterate
//        Vec3d unitVector = toAddVec.normalize().scale(0.25);
//        double toAddVeclenght = toAddVec.lengthVector();
//        int times = (int) Math.ceil(toAddVeclenght / (unitVector.lengthVector()));
//
//        //variables that are used in the loop
//        Vec3d stepsTo = new Vec3d(0, 0, 0);
//        BlockPos currPosition = new BlockPos(startBlock.getBlockPos());
//        BlockPos[] prevPositions = {null,null};//{previousPosition,previousPreviousPosition}
//        ArrayList<BlockPos> positionsList = new ArrayList<>();
//
//        for (int i = 0; i < times+4; i++) {
//            Vec3d checkStepsTo = stepsTo.add(unitVector);
//
//            //if the new vector is to long half the unitVector once and see if it fits
//            if(checkStepsTo.lengthVector() <= toAddVeclenght)
//            {
//                stepsTo = checkStepsTo;
//            }
//            else
//            {
//                unitVector = unitVector.scale(0.5);
//                stepsTo = stepsTo.add(unitVector);
//            }
//
//            if(stepsTo.lengthVector() <= toAddVeclenght)
//            {
//                currPosition = new BlockPos(startVec.add(stepsTo));
////                if (startBlock.getBlockState() != null && startBlock.getBlockState().getBlock() instanceof BlockRedstoneWire &&  (prevPositions[0] == null || !prevPositions[0].equals(currPosition)))
////                {
////                    positionsList.add(currPosition);
////                    prevPositions[0] = currPosition;
////                }
//                if (isWireRailOrNeedsConnection(startBlock))
//                {
//                    if (processNewWirePosition(currPosition,startVec.add(stepsTo), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
//                        break;
//                }
//                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
//                    break;
//            }
//        }
//
//        //place a block for all the positions in the positionslist
//        for (BlockPos pos: positionsList)
//        {
//            placePositionDirect(pos,startBlock, blockList);
//        }
//
//
//        //make sure the end vector is also drawn
//        if (isWireRailOrNeedsConnection(startBlock))
//        {
//            if ((prevPositions[0] == null || (prevPositions[0].equals(currPosition) && !currPosition.equals(firstPosition) && (prevPositions[1]==null || !(prevPositions[1].getX() == currPosition.getX() && prevPositions[1].getZ() == currPosition.getZ())) )|| !(prevPositions[0].getX() == currPosition.getX() && prevPositions[0].getZ() == currPosition.getZ())) && !dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
//            {
//                placePositionDirect(currPosition, startBlock, blockList);
//                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
//                {
//                    BlockPos intermediatePosition = calculateIntermediatePosition(currPosition,startVec.add(stepsTo), prevPositions[0]);
//                    if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
//                        placePositionDirect(intermediatePosition, startBlock, blockList);
//                }
//            }
//
//
//
//        }
//        else
//        {
//            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
//                placePositionDirect(currPosition, startBlock, blockList);
//        }
//
//    }

    public static boolean isWireRailOrNeedsConnection(TemplateBlock startBlock)
    {
        return startBlock.getBlockState() != null &&
                isWireRailOrNeedsConnection(startBlock.getBlockState());
    }

    public static boolean isWireRailOrNeedsConnection(IBlockState state)
    {
        return (state.getBlock() instanceof BlockRedstoneWire || state.getBlock() instanceof BlockRailBase);

    }

    public static boolean isFourWayBlock(TemplateBlock startBlock)
    {
        return startBlock.getBlockState() != null &&
                (startBlock.getBlockState().getBlock() instanceof BlockPane );
    }
    
//    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec, TemplateBlock startBlock, HashMap<Vec3d,TemplateBlock> dict)
//    {
//        drawBlockLine(startVec,toAddVec,startBlock,false,false,dict);
//    }

//    /**
//     * draws a line of blocks and put them into a Hashmap called dict this is used further in later code
//     * @param startVec
//     * @param toAddVec
//     * @param dontDrawFirst
//     * @param dontDrawLast
//     * @param dict
//     * @return
//     */
//    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec,TemplateBlock startBlock,boolean dontDrawFirst,boolean dontDrawLast, HashMap<Vec3d,TemplateBlock> dict) {
//
//        //this begining and the end position
//        BlockPos firstPosition = new BlockPos(startVec);
//        Vec3d currEndVec = startVec.add(toAddVec);
//        BlockPos lastPosition = new BlockPos(currEndVec);
//
//        //unit vec and the number of times to iterate
//        Vec3d unitVector = toAddVec.normalize().scale(0.25);
//        double toAddVeclenght = toAddVec.lengthVector();
//        int times = (int) Math.ceil(toAddVeclenght / (unitVector.lengthVector()));
//
//        //variables that are used in the loop
//        Vec3d currStep = new Vec3d(0, 0, 0);
//        BlockPos currPosition = new BlockPos(startBlock.getBlockPos());
//        BlockPos[] prevPositions = {null,null};//{previousPosition,previousPreviousPosition}
//
//        ArrayList<BlockPos> positionsList = new ArrayList<>();
//
//        for (int i = 0; i < times+4; i++) {
//
//            Vec3d nextStep = currStep.add(unitVector);
//
//            //if the new vector is to long half the unitVector once and see if it fits
//            if(nextStep.lengthVector() <= toAddVeclenght)
//            {
//                currStep = nextStep;
//            }
//            else
//            {
//                unitVector = unitVector.scale(0.5);
//                currStep = currStep.add(unitVector);
//            }
//
//            if(currStep.lengthVector() <= toAddVeclenght)
//            {
//                currPosition = new BlockPos(startVec.add(currStep));
//                if (isWireRailOrNeedsConnection(startBlock))
//                {
//                    if (processNewWirePosition(currPosition,startVec.add(currStep), positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
//                        break;
//                }
//                else if (processNewPosition(currPosition, positionsList, prevPositions, firstPosition, dontDrawFirst, lastPosition, dontDrawLast))
//                    break;
//            }
//
//        }
//        //place a block for all the positions in the positionslist
//        for (BlockPos pos: positionsList)
//        {
//            placePositionInDict(pos,startBlock, dict);
//        }
//
//        //make sure the end vector is also drawn
//        if (isWireRailOrNeedsConnection(startBlock))
//        {
//            if ((prevPositions[0] == null || (prevPositions[0].equals(currPosition) && !currPosition.equals(firstPosition) && (prevPositions[1]==null || !(prevPositions[1].getX() == currPosition.getX() && prevPositions[1].getZ() == currPosition.getZ())) )|| !(prevPositions[0].getX() == currPosition.getX() && prevPositions[0].getZ() == currPosition.getZ())) && !dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size() - 1).equals(currPosition)))
//            {
//                placePositionInDict(currPosition,startBlock, dict);
//                if(prevPositions[0] != null && isDiagonal(prevPositions[0],currPosition))
//                {
//                    BlockPos intermediatePosition = calculateIntermediatePosition(currPosition,startVec.add(currStep), prevPositions[0]);
//                    if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
//                        placePositionInDict(currPosition,startBlock, dict);
//                }
//            }
//
//
//        }
//        else
//        {
//            if (!dontDrawLast && currPosition != null && (prevPositions[0] == null || positionsList.size() == 0 || !positionsList.get(positionsList.size()-1).equals(currPosition)))
//                placePositionInDict(currPosition,startBlock, dict);
//        }
//
//
//    }

    public static boolean processNewPosition(BlockPos currPosition, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast)
    {
        //complicated mess to ensure no duplicates and no corner blocks
        //Checks if the previous position is not the same as the first
        //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
        boolean changePrev = false;
        boolean changePrevPrev = false;
        if (prevPositions[0] == null)
            changePrev = true;
        else if (!prevPositions[0].equals(currPosition))
        {
            if (prevPositions[1] == null)
            {
                if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                {
                    positionsList.add(prevPositions[0]);
                }

                changePrev = true;
                changePrevPrev = true;
            } else {
                if (isDiagonal(prevPositions[1], currPosition))
                    changePrev = true;
                else {
                    if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                    {
                        positionsList.add(prevPositions[0]);
                    }


                    changePrev = true;
                    changePrevPrev = true;
                }

            }
            if(dontDrawLast && currPosition.equals(lastPosition))
                return true;

        }
        if (changePrevPrev)
            //prevPrevPositions = prevPosition;
            prevPositions[1] = prevPositions[0];
        if (changePrev)
            prevPositions[0] = currPosition;
        return false;
    }

    public static boolean processNewWirePosition(BlockPos currPosition,Vec3d currStepVec, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast)
    {
        return processNewWirePosition(currPosition,currStepVec,positionsList,prevPositions,firstPosition,dontDrawFirst,lastPosition,dontDrawLast,null);
    }

    public static boolean processNewWirePosition(BlockPos currPosition,Vec3d currStepVec, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast,BlockPos center)
    {
        //complicated mess to ensure no duplicates and no corner blocks
        //Checks if the previous position is not the same as the first
        //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
        boolean changePrev = false;
        boolean changePrevPrev = false;

        BlockPos intermediatePosition = currPosition;

        if (prevPositions[0] == null  )
            changePrev = true;
        else if (!prevPositions[0].equals(currPosition))
        {
            if (prevPositions[1] == null)
            {
                if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                {
                    positionsList.add(prevPositions[0]);
                }
                // add an extra position closest to the direction of the vector between the prevPos[0] and currPos
                // but only when the prevPos and currPos are diagonal from each other
                if(prevPositions[0].getX() == currPosition.getX() && prevPositions[0].getZ() == currPosition.getZ())
                {

                }
                else if(isDiagonal(prevPositions[0],currPosition))
                {
                    intermediatePosition = calculateIntermediatePosition(currPosition, currStepVec, prevPositions[0]);
                    if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
                    {
                        positionsList.add(intermediatePosition);
                        if(center != null)
                        {
                            int newX = center.getX()-intermediatePosition.getX();
                            int newZ = center.getZ()-intermediatePosition.getZ();
                            positionsList.add( center.add(newX,0, newZ));
                            positionsList.add( center.add(newZ,0, -newX));
                            positionsList.add( center.add(-newZ,0, newX));
                        }

                    }

                }


                changePrev = true;
                changePrevPrev = true;
            } else {
                if(prevPositions[0].getX() == prevPositions[1].getX() && prevPositions[0].getZ() == prevPositions[1].getZ())
                {
                    changePrev = true;
                }
                else if (isDiagonal(prevPositions[1], currPosition))
                {
                    if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                    {
                        positionsList.add(prevPositions[0]);
                    }
                    changePrev = true;
                    changePrevPrev = true;
                }
                else
                {
                    if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                    {
                        positionsList.add(prevPositions[0]);
                    }

                    // add an extra position closest to the direction of the vector between the prevPos[0] and currPos
                    // but only when the prevPos and currPos are diagonal from each other
                    if(isDiagonal(prevPositions[0],currPosition))
                    {
                        intermediatePosition = calculateIntermediatePosition(currPosition, currStepVec, prevPositions[0]);
                        if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
                        {
                            positionsList.add(intermediatePosition);
                            if(center != null)
                            {
                                int newX = center.getX()-intermediatePosition.getX();
                                int newZ = center.getZ()-intermediatePosition.getZ();
                                positionsList.add( center.add(newX,0, newZ));
                                positionsList.add( center.add(newZ,0, -newX));
                                positionsList.add( center.add(-newZ,0, newX));
                            }
                        }

                    }

                    changePrev = true;
                    changePrevPrev = true;
                }

            }
            if(dontDrawLast && currPosition.equals(lastPosition))
                return true;

        }
        if (changePrevPrev)
            //prevPrevPositions = prevPosition;
            prevPositions[1] = prevPositions[0];
        if (changePrev)
            prevPositions[0] = currPosition;
        return false;
    }

//    public static BlockPos calculateIntermediatePosition(BlockPos currPosition, Vec3d currStepVec, BlockPos prevPosition)
//    {
//        BlockPos intermediatePosition;
//        Vec3d prevVec = new Vec3d(prevPosition.getX(), prevPosition.getY(), prevPosition.getZ());
//        Vec3d diff = currStepVec.subtract(prevVec);
//        if (Math.abs(diff.x) < Math.abs(diff.z))
//        {
//            if (diff.x > 0)
//                intermediatePosition = currPosition.add(-1, 0, 0);
//            else
//                intermediatePosition = currPosition.add(1, 0, 0);
//        }
//        else
//        {
//            if (diff.z > 0)
//                intermediatePosition = currPosition.add(0, 0, -1);
//            else
//                intermediatePosition = currPosition.add(0, 0, 1);
//        }
//        return intermediatePosition;
//    }

    public static boolean processNewFourWayBlockPosition(BlockPos currPosition,Vec3d currStepVec, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast)
    {
        return processNewFourWayBlockPosition(currPosition,currStepVec,positionsList,prevPositions,firstPosition,dontDrawFirst,lastPosition,dontDrawLast,null);
    }

    public static boolean processNewFourWayBlockPosition(BlockPos currPosition,Vec3d currStepVec, ArrayList<BlockPos> positionsList, BlockPos[] prevPositions, BlockPos firstPosition, boolean dontDrawFirst, BlockPos lastPosition, boolean dontDrawLast,BlockPos center)
    {
        //complicated mess to ensure no duplicates and no corner blocks
        //Checks if the previous position is not the same as the first
        //Checks if the current position is diagonal to the prevPrevPosition if it is don't place the prevPosition
        boolean changePrev = false;
        boolean changePrevPrev = false;

        BlockPos intermediatePosition = currPosition;

        if (prevPositions[0] == null  )
            changePrev = true;
        else if (!prevPositions[0].equals(currPosition))
        {
            if (prevPositions[1] == null)
            {
                if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                {
                    positionsList.add(prevPositions[0]);
                }
                // add an extra position closest to the direction of the vector between the prevPos[0] and currPos
                // but only when the prevPos and currPos are diagonal from each other
                if(isDiagonal(prevPositions[0],currPosition))
                {
                    intermediatePosition = calculateIntermediatePosition3D(currPosition, currStepVec, prevPositions[0]);
                    positionsList.add(intermediatePosition);
                    if(center != null)
                    {
                        //only when placing circles is this used
                        addMirroredIntermediateBlocksForCircles(positionsList, center, intermediatePosition);
                    }

                }


                changePrev = true;
                changePrevPrev = true;
            } else
            {
                if (isDiagonal(prevPositions[1], currPosition))
                {
                    if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                    {
                        positionsList.add(prevPositions[0]);
                    }
                    changePrev = true;
                    changePrevPrev = true;
                }
                else
                {
                    if(!dontDrawFirst || !prevPositions[0].equals(firstPosition))
                    {
                        positionsList.add(prevPositions[0]);
                    }

                    // add an extra position closest to the direction of the vector between the prevPos[0] and currPos
                    // but only when the prevPos and currPos are diagonal from each other
                    if(isDiagonal(prevPositions[0],currPosition))
                    {
                        intermediatePosition = calculateIntermediatePosition3D(currPosition, currStepVec, prevPositions[0]);
                        positionsList.add(intermediatePosition);
                        if(center != null)
                        {
                            addMirroredIntermediateBlocksForCircles(positionsList, center, intermediatePosition);
                        }


                    }

                    changePrev = true;
                    changePrevPrev = true;
                }

            }
            if(dontDrawLast && currPosition.equals(lastPosition))
                return true;

        }
        if (changePrevPrev)
            //prevPrevPositions = prevPosition;
            prevPositions[1] = prevPositions[0];
        if (changePrev)
            prevPositions[0] = currPosition;
        return false;
    }

    private static void addMirroredIntermediateBlocksForCircles(ArrayList<BlockPos> positionsList, BlockPos center, BlockPos intermediatePosition)
    {
        int newX = center.getX()- intermediatePosition.getX();
        int newZ = center.getZ()- intermediatePosition.getZ();
        int newY = center.getY()- intermediatePosition.getY();

        //for now (alpha2.0) circles can only be made in orthogonal planes
        if(newY == 0)
        {
            positionsList.add( center.add(newX,0, newZ));
            positionsList.add( center.add(newZ,0, -newX));
            positionsList.add( center.add(-newZ,0, newX));
        }
        else
        {
            if(newX == 0)
            {
                positionsList.add( center.add(0,newY, newZ));
                positionsList.add( center.add(0,newZ, -newY));
                positionsList.add( center.add(0,-newZ, newY));
            }
            else
            {
                positionsList.add( center.add(newX, newY,0));
                positionsList.add( center.add(newY, -newX,0));
                positionsList.add( center.add(-newY, newX,0));
            }
        }
    }

    public static BlockPos calculateIntermediatePosition(BlockPos currPosition, Vec3d currStepVec, BlockPos prevPosition)
    {
        BlockPos intermediatePosition;
        Vec3d prevVec = new Vec3d(prevPosition.getX(), prevPosition.getY(), prevPosition.getZ());
        Vec3d diff = currStepVec.subtract(prevVec);
        if (Math.abs(diff.x) < Math.abs(diff.z))
        {
            if (diff.x > 0)
                intermediatePosition = currPosition.add(-1, 0, 0);
            else
                intermediatePosition = currPosition.add(1, 0, 0);
        }
        else
        {
            if (diff.z > 0)
                intermediatePosition = currPosition.add(0, 0, -1);
            else
                intermediatePosition = currPosition.add(0, 0, 1);
        }
        return intermediatePosition;
    }

    public static BlockPos calculateIntermediatePosition3D(BlockPos currPosition, Vec3d currStepVec, BlockPos prevPosition)
    {
        BlockPos intermediatePosition;
        Vec3d prevVec = new Vec3d(prevPosition.getX(), prevPosition.getY(), prevPosition.getZ());
        Vec3d diff = currStepVec.subtract(prevVec);
        if (Math.abs(diff.x) < Math.abs(diff.z))
        {
            if (Math.abs(diff.y) < Math.abs(diff.x))
            {
                if (diff.y > 0)
                    intermediatePosition = currPosition.add(0, -1, 0);
                else
                    intermediatePosition = currPosition.add(0, 1, 0);
            }
            else
            {
                if (diff.x > 0)
                    intermediatePosition = currPosition.add(-1, 0, 0);
                else
                    intermediatePosition = currPosition.add(1, 0, 0);
            }

        }
        else
        {
            if (Math.abs(diff.y) < Math.abs(diff.z))
            {
                if (diff.y > 0)
                    intermediatePosition = currPosition.add(0, -1, 0);
                else
                    intermediatePosition = currPosition.add(0, 1, 0);
            }
            else
            {
                if (diff.z > 0)
                    intermediatePosition = currPosition.add(0, 0, -1);
                else
                    intermediatePosition = currPosition.add(0, 0, 1);
            }
        }
        return intermediatePosition;
    }
    
    public static boolean isDiagonal(BlockPos prevPrevPosition, BlockPos currPosition)
    {
        double length = Math.sqrt(currPosition.distanceSq(prevPrevPosition));
        return length < 2.0 && length > 1.0;
    }

    public static BlockPos placePositionDirect(BlockPos prevPosition,TemplateBlock startBlock, ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(startBlock.getFace(), prevPosition, startBlock.getBlockState(),startBlock.getTileEntity()));
        return prevPosition;
    }

    public static BlockPos placePositionInDict(BlockPos prevPosition,TemplateBlock startBlock, HashMap<Vec3d, TemplateBlock> dict)
    {
        if(prevPosition.getX() == -64 && prevPosition.getY() == 69 && prevPosition.getZ() == 129)
            System.out.println("startpos");
        dict.put(new Vec3d(prevPosition.getX(),prevPosition.getY(),prevPosition.getZ()),
                new TemplateBlock(startBlock.getFace(), prevPosition, startBlock.getBlockState(),startBlock.getTileEntity()));
        return prevPosition;
    }

    public static Vec3d parseVec(Vec3d vec)
    {
        double newX = Math.floor(vec.x) +0.5;
        double newZ = Math.floor(vec.z) +0.5;
        double newY = Math.floor(vec.y) +0.5;
        return new Vec3d(newX,newY,newZ);
    }

    public static boolean isNotGroundMaterial(IBlockState iBlockState)
    {
        if(iBlockState == null)
            return true;

        Material mat = iBlockState.getMaterial();
        Block block = iBlockState.getBlock();
        return (mat == Material.AIR ||
                mat == Material.SNOW ||
                mat == Material.VINE ||
                mat == Material.PLANTS ||
                mat == Material.CAKE ||
                mat == Material.SPONGE ||
                mat == Material.LEAVES ||
                mat == Material.CACTUS ||
                ((block instanceof BlockLog)&& IsometricCamera.isLeafRaytracingDisabled())||
                ((mat == Material.WATER || mat == Material.LAVA) && IsometricCamera.IGNORE_FLUIDS));
    }

    public static boolean isNotFullBlock(IBlockState iBlockState)
    {
        if(iBlockState == null)
            return true;

        Material mat = iBlockState.getMaterial();
        Block block = iBlockState.getBlock();
        return (mat == Material.ANVIL ||
                mat == Material.WEB ||
                mat == Material.CIRCUITS ||
                mat == Material.REDSTONE_LIGHT ||
                mat == Material.CARPET ||
                mat == Material.GLASS ||
                mat == Material.PORTAL);
    }

    public static boolean isAllowedNonItemBlocks(Item item)
    {
        return (item instanceof ItemDoor ||
                item instanceof ItemBed ||
                item instanceof ItemSign ||
                item instanceof ItemSkull ||
                item instanceof ItemRedstone ||
                item instanceof ItemBlockSpecial ||
                item instanceof ItemAir);
    }

    public static boolean isNotIllegalBlockForRightClick(Block block)
    {
        return !(block instanceof BlockTripWire);
    }

    public static boolean exceptionalConditionForRightClick(Item item)
    {
        return item instanceof ItemAir ||
                AdvCreation.getMode().equals(EnumMainMode.PLACE) ||
                (AdvCreation.getMode().equals(EnumMainMode.BUILD) && BuildMode.allowRightClickException(item));
//        return AdvCreation.getMode().equals(EnumMainMode.BUILD) && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode);
    }


    public static boolean isPlant(IBlockState currState)
    {
        Block block = currState.getBlock();
        Material mat = currState.getMaterial();
        return mat == Material.PLANTS ||
                mat == Material.SNOW||
                mat == Material.LEAVES ||
                mat == Material.CACTUS ||
                mat == Material.CRAFTED_SNOW ||
//                mat == Material.GRASS || //TODO: dirt with grass ontop is recognized as GRASS material but I only want tall grass in here :S
                mat == Material.VINE ||
                mat == Material.CORAL ||
                mat == Material.WEB ||
                mat == Material.GOURD ||
                block == Blocks.NETHER_WART_BLOCK ||
                block == Blocks.NETHER_WART ||
                block instanceof BlockCrops ||
                block instanceof BlockHugeMushroom ||
                block instanceof BlockSnow ||
                block instanceof BlockBush ||
                block instanceof BlockMycelium;
    }

    public static boolean isLog(IBlockState currState)
    {
        Block block = currState.getBlock();
        Material mat = currState.getMaterial();
        return  (block instanceof BlockLog);

    }

    public static Vec3d convertBlockPosToVector3d(BlockPos pos)
    {
        return new Vec3d(pos.getX(),pos.getY(),pos.getZ());
    }
}
