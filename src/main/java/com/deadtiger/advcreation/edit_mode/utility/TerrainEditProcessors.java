package com.deadtiger.advcreation.edit_mode.utility;

import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.edit_mode.adjust_modes.SmoothAdjustMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class TerrainEditProcessors
{

    public static void applyRaiseData(BlockPos size2DArray, int[][] heights, int[][] newHeights, double[][] data, int centerHeight)
    {
        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {
                if (heights[x][z] > 0)
                {
                    if (heights[x][z] >= centerHeight)
                    {
                        newHeights[x][z] = heights[x][z];
                    }
                    else
                    {
                        int newHeight = heights[x][z] + (int) Math.round(data[x][z] + 0.3);
                        newHeights[x][z] = newHeight;
                    }
                }

            }
        }
    }

    public static void applyDigData(BlockPos size2DArray, int[][] heights, int[][] newHeights, double[][] data, int centerHeight)
    {
        for (int x = 0; x < size2DArray.getX(); x++)
        {
            for (int z = 0; z < size2DArray.getZ(); z++)
            {
                if (heights[x][z] > 0)
                {
                    if (heights[x][z] <= centerHeight)
                    {
                        newHeights[x][z] = heights[x][z];
                    }
                    else
                    {
                        int newHeight = heights[x][z] + (int) Math.round(data[x][z] + 0.3);
                        newHeights[x][z] = newHeight;
                    }
                }

            }
        }
    }

    public static double[][] calculate3DDataDigRaise(double amplitude, double radius, BlockPos size2DArray)
    {
        int side = size2DArray.getX();
        if(side == 0)
            return null;
        double offset = ((side-1) /2.0);
        double[][] data = new double[side][side];
        double var = (1/Math.pow(radius,2))*3;

        for (int x = 0; x < side; x++)
        {
            for (int z = 0; z < side; z++)
            {
                data[x][z] = amplitude*Math.exp(-((Math.pow(x - offset ,2)*var)+(Math.pow(z - offset ,2)*var)));
            }
        }

        return data;

    }

    public static void applyDataDigRaise(BlockPos size2DArray, int[][] heights, int[][] newHeights, float[][] data)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int height;
        float sum;
        int newHeight;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = heights[x][z];
                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {


                        newHeight = heights[x + xOffset][z + zOffset];
                        if (newHeight == 0)
                            newHeight = height;

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }
                newHeights[x][z] = (int) Math.round(sum);

            }
        }
    }

    public static int applyDataLevel(BlockPos size2DArray, int[][] heights, int[][] newHeights, int[][] extendedHeights, float[][] data, ExtremaXYZ extremaXYZ, boolean ceil, int[][] blockIndices , ArrayList<Integer> indicesOfHighestLevelIndication, ArrayList<Integer> indicesOfLowestLevelIndication)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int height;
        float sum;
        int newHeight;
        int countHighestLevel = 0;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {

                height = heights[x][z];

                //don't apply the data on the already at the level you are trying to get to
                if(ceil)
                {
                    if(blockIndices[x][z] >= 0 && indicesOfHighestLevelIndication.contains(blockIndices[x][z]))
                    {
                        newHeights[x][z] = height ;
                        continue;
                    }
                }
                else
                {
                    if(blockIndices[x][z] >= 0 && indicesOfLowestLevelIndication.contains(blockIndices[x][z]))
                    {
                        newHeights[x][z] = height ;
                        continue;
                    }
                }

                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {
                        newHeight = heights[x + xOffset][z + zOffset];
                        if(newHeight == 0)
                            newHeight = extendedHeights[x + xOffset][z + zOffset];

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }

                newHeights[x][z] = Math.round(sum);

                if(ceil)
                {
                    if(newHeights[x][z] >= extremaXYZ.maxY)
                    {
                        newHeights[x][z] = extremaXYZ.maxY;
                        countHighestLevel++;
                    }

                }
                else
                {
                    if(newHeights[x][z] <= extremaXYZ.minY)
                    {
                        newHeights[x][z] = extremaXYZ.minY;
                        countHighestLevel++;
                    }
                }


            }
        }
        return countHighestLevel;
    }

    public static  void applyDataSmooth(BlockPos size2DArray, int[][] heights, int[][] newHeights, float[][] data)
    {
        applyDataSmooth(size2DArray,heights,newHeights,data,false);
    }

    public static  void applyDataSmooth(BlockPos size2DArray, int[][] heights, int[][] newHeights, float[][] data, boolean ceil)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int height;
        float sum;
        int newHeight;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = heights[x][z];
                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {
                        newHeight = heights[x + xOffset][z + zOffset];
                        if(newHeight == 0)
                            newHeight = height;

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }
                if(ceil)
                    newHeights[x][z] = (int) Math.ceil(sum);
                else
                    newHeights[x][z] = Math.round(sum);

            }
        }
    }

    public static void applyDataSmooth(BlockPos size2DArray, int[][] heights, int[][] newHeights, float[][] data, double middleHeight, int maxHeight, int minHeight)
    {
        applyDataSmooth(size2DArray,heights,newHeights, data,middleHeight,maxHeight,minHeight,null,null);
    }

    public static void applyDataSmooth(BlockPos size2DArray, int[][] heights, int[][] newHeights, float[][] data, double middleHeight, int maxHeight, int minHeight, int[] minYPerQaurter, int[] maxYPerQaurter)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int height;
        float sum;
        int newHeight;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = heights[x][z];
                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {


                        newHeight = heights[x + xOffset][z + zOffset];
                        if(newHeight == 0)
                            newHeight = height;

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }
                if(minYPerQaurter == null && maxYPerQaurter == null)
                {
                    if(sum > middleHeight)
                        newHeights[x][z] = (int) Math.ceil(sum);
                    else
                        newHeights[x][z] = (int) Math.floor(sum);

                    if(newHeights[x][z]> maxHeight)
                        newHeights[x][z] = maxHeight;
                    else if(newHeights[x][z] < minHeight)
                        newHeights[x][z] = minHeight;
                }
                else
                {
                    int qaurterMaxHeight = maxHeight;
                    int qaurterIndex = SmoothAdjustMode.getQaurterIndex(newHeights,x,z);
                    if(maxYPerQaurter != null)
                        qaurterMaxHeight = maxYPerQaurter[qaurterIndex];

                    int qaurterMinHeight= minHeight;
                    if(minYPerQaurter != null)
                        qaurterMinHeight = minYPerQaurter[qaurterIndex];

                    double qaurterMiddleHeight =  (double) (qaurterMaxHeight + qaurterMinHeight)/2.0;
                    if(sum > qaurterMiddleHeight)
                        newHeights[x][z] = (int) Math.ceil(sum);
                    else
                        newHeights[x][z] = (int) Math.floor(sum);

                    if(newHeights[x][z]> qaurterMaxHeight)
                        newHeights[x][z] = qaurterMaxHeight;
                    else if(newHeights[x][z] < qaurterMinHeight)
                        newHeights[x][z] = qaurterMinHeight;

                }


            }
        }
    }

    public static void applyDataSmooth(BlockPos size2DArray, double[][] heights, double[][] newHeights, float[][] data)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        double height;
        float sum;
        double newHeight;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = heights[x][z];
                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {


                        newHeight = heights[x + xOffset][z + zOffset];
                        if(newHeight == 0)
                            newHeight = height;

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }
                newHeights[x][z] = sum;

            }
        }
    }

    public static void applyDataSmooth(BlockPos size2DArray, int[][] heights, double[][] newHeights, float[][] data)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int height;
        float sum;
        int newHeight;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = heights[x][z];
                sum = 0f;
                if (height == 0)
                {
                    newHeights[x][z] = 0;
                    continue;
                }

                for (int xOffset = -dataHalf; xOffset <= dataHalf; xOffset++)
                {
                    for (int zOffset = -dataHalf; zOffset <= dataHalf; zOffset++)
                    {


                        newHeight = heights[x + xOffset][z + zOffset];
                        if(newHeight == 0)
                            newHeight = height;

                        sum += newHeight * data[dataHalf + xOffset][dataHalf + zOffset];
                    }
                }
                newHeights[x][z] = sum;

            }
        }
    }
}
