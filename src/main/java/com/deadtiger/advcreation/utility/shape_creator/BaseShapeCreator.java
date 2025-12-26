package com.deadtiger.advcreation.utility.shape_creator;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.EnumFillMode;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.deadtiger.advcreation.utility.PlacementHelper.isWireRailOrNeedsConnection;

public abstract class BaseShapeCreator
{
    public static final Color jumpToHardcodedRadiusColor = new Color(1.0f,0.0f,0.0f);
    public static final Color freeRadiusColor = new Color(0.5f,0.7f,0.7f);
    public static boolean addHardcodedRadius = false;//only for developers to add new circles to the hardcoded circles
    public static boolean jumpToHardcodedRadius = true;
    //list of hardcoded circle
    public ArrayList<Double> hardcodedRadiusLengthList = new ArrayList<>();
    public HashMap<Double, Vec3d> mapRadiusLengthToRadialVector = new HashMap<>();
    public boolean isInitialised = false;

    public HashMap<Double, Integer> squareDict = new HashMap<>();

    public abstract void init ();

    public ArrayList<BlockPos> generateCirclePositions(Vec3d middle, Vec3d radiusVec, double radiusLength, ArrayList<Vec3d> previewVecPoints, EnumDirectionMode directionMode)
    {
        ArrayList<BlockPos> positionsList = new ArrayList<>();
        double addedAngle = 0.10 / (Math.pow(radiusLength,2));
        int verticesToDraw = (int) Math.floor(2 * Math.PI / addedAngle);
        double startAngle = calcStartAngle(radiusVec,directionMode);

        BlockPos[] prevPositions = {null,null,null};//{previousPosition,previousPreviousPosition,lastCheckedPosition}

        int[] votes = new int[2];
        previewVecPoints.clear();
        for (int i = 1; i <= (verticesToDraw + 1); i++) {
            double newAngle = startAngle + addedAngle * i;

            Vec3d newPoint = getNewCircleVecPoint(middle, radiusLength, newAngle,directionMode);
            previewVecPoints.add(newPoint);

            if (isWireRailOrNeedsConnection(BuildMode.START_BLOCK))
            {
                if (PlacementHelper.processNewWirePosition(new BlockPos(newPoint),newPoint, positionsList, prevPositions,null,false, null, false,new BlockPos(middle)))
                    break;
            }
            else if (BuildMode.START_BLOCK.getBlockState() != null && PlacementHelper.isFourWayBlock(BuildMode.START_BLOCK))
            {
                if (PlacementHelper.processNewFourWayBlockPosition(new BlockPos(newPoint),newPoint, positionsList, prevPositions,null,false, null, false,new BlockPos(middle)))
                    break;
            }
            else if (PlacementHelper.processNewPosition(new BlockPos(newPoint), positionsList, prevPositions, null,false, null, false,votes, null, null))
                break;
        }
        return positionsList;
    }

    public boolean isInsideOfHardcodedRadiusRange(double radiusLength)
    {
        if(hardcodedRadiusLengthList.isEmpty())
            return false;


        double maxHardcode = hardcodedRadiusLengthList.get(hardcodedRadiusLengthList.size()-1);
        boolean isInsideOfHardcodedRadiusRange = radiusLength <= Math.floor(maxHardcode*10)/10.0;


        return isInsideOfHardcodedRadiusRange;
//        return false;
    }

    public boolean isInsideOfHardcodedRadiusRange(int radiusIndex)
    {
        if(hardcodedRadiusLengthList.isEmpty())
            return false;
        return (radiusIndex > -1 && radiusIndex < hardcodedRadiusLengthList.size());
    }

    public Vec3d getNewCircleVecPoint(Vec3d newStartVec, double radiusLength, double newAngle,EnumDirectionMode directionMode)
    {
        Vec3d addVec;
        if (directionMode == EnumDirectionMode.XY)
            addVec =new Vec3d(radiusLength * Math.cos(newAngle), radiusLength * Math.sin(newAngle),0.0);
        else if (directionMode == EnumDirectionMode.ZY)
            addVec =new Vec3d(0.0, radiusLength * Math.sin(newAngle), radiusLength * Math.cos(newAngle));
        else
            addVec = new Vec3d(radiusLength * Math.cos(newAngle),0.0, radiusLength * Math.sin(newAngle));

        return newStartVec.add(addVec);
    }

    public void placePosition(BlockPos prevPosition,TemplateBlock startBlock, ArrayList<Double> singleBlockKeys, HashMap<Vec3d, TemplateBlock> dict,boolean fill,
                              HashMap<Double, FillVector> fillVectorDict, EnumDirectionMode directionMode, Vec3d startVec) {
        if (fill) {
            processFillCircle(prevPosition, singleBlockKeys,dict,fillVectorDict,directionMode,startVec,startBlock);
        } else {
//            PlacementHelper.placePositionDirect(prevPosition,startBlock,BuildMode.CURR_TOOL_BLOCKS);
            PlacementHelper.placePositionInDict(prevPosition,startBlock,dict);
        }
    }

    /**
     * Draw filling lines between 2 points on the circle that have the same x coordinate
     * @param prevPosition
     * @param singleBlockKeys
     * @param dict
     */
    public void processFillCircle(BlockPos prevPosition, ArrayList<Double> singleBlockKeys, HashMap<Vec3d,TemplateBlock> dict, HashMap<Double, FillVector> fillVectorDict, EnumDirectionMode directionMode, Vec3d startVec,TemplateBlock startBlock)
    {
        double correction = 0.0;
        Vec3d newVec = PlacementHelper.parseVec(new Vec3d(prevPosition.getX(), prevPosition.getY(), prevPosition.getZ() + correction));
        double key = newVec.x;
        if (directionMode == EnumDirectionMode.ZY)
            key = newVec.y;
        else if (directionMode == EnumDirectionMode.XY)
            newVec = PlacementHelper.parseVec(new Vec3d(prevPosition.getX(), prevPosition.getY() + correction, prevPosition.getZ() + correction));

        if (fillVectorDict.containsKey(key)) {
            FillVector currFillVec = fillVectorDict.get(key);
            if (currFillVec.addVec(newVec, directionMode, startVec)) {
                PlacementHelper.drawBlockLine(currFillVec.startVec, currFillVec.endVec.subtract(currFillVec.startVec), startBlock,dict);
                singleBlockKeys.remove(key);
            }

        } else {
            fillVectorDict.put(key, new FillVector(newVec));
            singleBlockKeys.add(key);
        }
    }

    public double calcStartAngle(Vec3d diffVec, EnumDirectionMode directionMode) {
        double startAngleY = 0.0;
        if (directionMode == EnumDirectionMode.FREE || directionMode == EnumDirectionMode.XZ)
        {
            startAngleY = Math.atan(diffVec.z / diffVec.x);
            if (diffVec.x < 0.0)
                startAngleY = Math.PI + startAngleY;
        } else if (directionMode == EnumDirectionMode.XY)
        {
            startAngleY = Math.atan(diffVec.y / diffVec.x);
            if (diffVec.x < 0.0)
                startAngleY = Math.PI + startAngleY;
        } else if (directionMode == EnumDirectionMode.ZY)
        {
            startAngleY = Math.atan(diffVec.y / diffVec.z);
            if (diffVec.z < 0.0)
                startAngleY = Math.PI + startAngleY;
        }

        return startAngleY;
    }

    public double getClosestHardcodedCircleLength(double diffLength) {
        double minDist = 10000.0;
        double chosenLength = diffLength;
        for (double length : hardcodedRadiusLengthList) {
            if (Math.abs(length - diffLength) < minDist) {
                chosenLength = length;
                minDist = Math.abs(length - diffLength);
            }
        }
        return chosenLength;
    }

    public int getClosestHardcodedCircleIndex(double diffLength) {
        double minDist = 10000.0;
        int chosenIndex = 0;

        for (int i = 0; i < hardcodedRadiusLengthList.size(); i++)
        {
            double length = hardcodedRadiusLengthList.get(i);
            if (Math.abs(length - diffLength) < minDist) {
                chosenIndex = i;
                minDist = Math.abs(length - diffLength);
            }
        }

        return chosenIndex;
    }

    public Vec3d transformHardcodedCircleToCurrDir(Vec3d diffVec, double chosenLength, EnumDirectionMode directionMode) {
        if (hardcodedRadiusLengthList.contains(chosenLength))
        {
            diffVec = mapRadiusLengthToRadialVector.get(chosenLength);
            if(diffVec != null)
            {
                if (directionMode == EnumDirectionMode.XY)
                    diffVec = new Vec3d(diffVec.x, diffVec.z, -diffVec.y);
                else if (directionMode == EnumDirectionMode.ZY)
                    diffVec = new Vec3d(-diffVec.y, diffVec.x, diffVec.z);
            }
        }
        return diffVec;
    }

    public void addHardcodedRadius(Vec3d radiusVec, double radiusLength)
    {
        hardcodedRadiusLengthList.add(radiusLength);
        mapRadiusLengthToRadialVector.put(radiusLength, new Vec3d(radiusVec.x, radiusVec.y, radiusVec.z));
        //Please do not delete these comments they might be usefull in the future
        //System.out.printf("middleCircleLengths.add(%.4f);\n", diffLength);
        //System.out.printf("middleCircleDict.put(%.4f,new Vec3d(%.4f,%.4f,%.4f));\n", diffLength, diffVec.x, diffVec.y, diffVec.z);
        addHardcodedRadius = false;
    }


    public ArrayList<BlockPos> generateRectanglePositions(Vec3d newStartVec, double radiusLength, EnumDirectionMode directionMode)
    {
        ArrayList<BlockPos> blockPosList = new ArrayList<>();
        int size = squareDict.get(radiusLength);
        int halfSize =(int) Math.floor(size/2.0);
        for(int x = 0 ;x < size;x++)
        {
            for(int y = 0 ;y < size;y++)
            {
                for(int z = 0 ;z < size;z++)
                {
                    int newX = 0;
                    int newY = 0;
                    int newZ = 0;

                    if (directionMode == EnumDirectionMode.XY)
                    {
                        newX = (int)(Math.floor(newStartVec.x) - halfSize + x);
                        newY = (int)(Math.floor(newStartVec.y)- halfSize + y);
                        newZ = (int)(Math.floor(newStartVec.z) );

                    }
                    else if (directionMode == EnumDirectionMode.XZ)
                    {
                        newX = (int)(Math.floor(newStartVec.x) - halfSize + x);
                        newY = (int)(Math.floor(newStartVec.y));
                        newZ = (int)(Math.floor(newStartVec.z) - halfSize + z);
                    }
                    else if (directionMode == EnumDirectionMode.ZY) {
                        newX = (int)(Math.floor(newStartVec.x));
                        newY = (int)(Math.floor(newStartVec.y)- halfSize + y);
                        newZ = (int)(Math.floor(newStartVec.z) - halfSize + z);
                    }
                    blockPosList.add(new BlockPos(newX,newY,newZ));
                }
            }
        }
        return blockPosList;
    }
}
