package com.deadtiger.advcreation.edit_mode.utility;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TerrainEditData
{

    public static float data1[][];
    public static float data2[][];
    public static float data3[][];
    public static float data4[][];
    public static float data5[][];
    public static float data6[][];


    public static float data7[][];
    public static float data8[][];

    public static float data9[][];
    public static float data10[][];

    public static int data0[][];

    public static ArrayList<float[][]> smoothingData =  new ArrayList<>();
    public static ArrayList<float[][]> sharpeningData =  new ArrayList<>();
    public static ArrayList<float[][]> levelData =  new ArrayList<>();
    public static HashMap<String,float[][]> allData =  new HashMap<>();

    static
    {
        processTerrainData();

        data1 = allData.get("data1");
        data2 = allData.get("data2");
        data3 = allData.get("data3");
        data4 = allData.get("data4");
        data5 = allData.get("data5");
        data6 = allData.get("data6");
        data7 = allData.get("data7");
        data8 = allData.get("data8");
        data9 = allData.get("data9");
        data10 = allData.get("data10");

        sharpeningData.add(data3); //1 - plane
        sharpeningData.add(data6); //2 - rise
        sharpeningData.add(data5); //3 - slope
        sharpeningData.add(data2);//4 - hill
        sharpeningData.add(data4);//5 - valley
        sharpeningData.add(data1);//6 - mountain
        sharpeningData.add(data1);//7 - cliff


        smoothingData.add(data3); //1 - plane
        smoothingData.add(data6); //2 - rise
        smoothingData.add(data2); //3 - slope
        smoothingData.add(data5);//4 - hill
        smoothingData.add(data1);//5 - valley
        smoothingData.add(data4);//6 - mountain
        smoothingData.add(data4);//7 - cliff


        levelData.add(data6); //1 - plane
        levelData.add(data3); //2 - rise
        levelData.add(data2); //3 - slope
        levelData.add(data2);//4 - hill
        levelData.add(data5);//5 - valley
        levelData.add(data1);//6 - mountain
        levelData.add(data4);//7 - cliff
    }



    public static void processTerrainData()
    {
        loadData0();
        loadTerrainData();

    }

    private static void loadTerrainData()
    {
        ResourceLocation dataFile = new ResourceLocation(Reference.MODID, "terraineditdata/data");
        loadTerrainData(dataFile,Minecraft.getMinecraft().getResourceManager());
    }

    public static void loadData0()
    {
        ResourceLocation data0File = new ResourceLocation(Reference.MODID, "terraineditdata/data0");
        data0 = loadData0(data0File ,Minecraft.getMinecraft().getResourceManager());
    }

    public static int[][] loadData0(ResourceLocation propertiesLocation, IResourceManager resourceManager)
    {
        try
        {
            IResource iresource =  resourceManager.getResource(propertiesLocation);
            Scanner reader = new Scanner(iresource.getInputStream(),"utf-8");
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                if (data.contains("data0:"))
                {
                   String coreData = data.split(":")[1];
                   if(!coreData.isEmpty())
                   {
                       String extractedData =  extractDataPoint(coreData);
                       int firstNumber = Integer.parseUnsignedInt(extractedData,2);

                       ArrayList<Integer> numbers = new ArrayList<>();
                       coreData = coreData.replace(extractedData,"");
                       while(!coreData.isEmpty())
                       {
                           extractedData =  extractDataPoint(coreData);
                           numbers.add(Integer.parseUnsignedInt(extractedData,2));
                           coreData = coreData.replace(extractedData,"");
                       }
                       int[][] newData0 = new int[firstNumber][firstNumber];

                       for (int i = 0; i < newData0.length; i++)
                       {
                           for (int j = 0; j < newData0[0].length; j++)
                           {
                               newData0[i][j] = numbers.get(i*firstNumber+j);
                           }
                       }

                       return newData0;
                   }

                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadTerrainData(ResourceLocation propertiesLocation, IResourceManager resourceManager)
    {
        try
        {
            IResource iresource =  resourceManager.getResource(propertiesLocation);
            Scanner reader = new Scanner(iresource.getInputStream(),"utf-8");
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                String[] lineData = data.split(":");
                String name = lineData[0];
                String coreData = lineData[1];
                if(!coreData.isEmpty())
                {
                    String extractedData =  extractDataPoint(coreData);
                    int firstNumber = Integer.parseUnsignedInt(extractedData,2);

                    ArrayList<Integer> numbers = new ArrayList<>();
                    coreData = coreData.replace(extractedData,"");
                    while(!coreData.isEmpty())
                    {
                        extractedData =  extractDataPoint(coreData);
                        numbers.add(Integer.parseUnsignedInt(extractedData,2));
                        coreData = coreData.replace(extractedData,"");
                    }
                    float[][] currData = new float[firstNumber][firstNumber];

                    for (int i = 0; i < currData.length; i++)
                    {
                        for (int j = 0; j < currData[0].length; j++)
                        {
                            currData[i][j] = decodeBinDate(numbers.get(i*firstNumber+j),data0[i][j]);
                        }
                    }
                    allData.put(name,currData);

                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String extractDataPoint(String coreData)
    {
        String currData = coreData.substring(0,32);
        return  currData;
    }

    private static float decodeBinDate(int data, int shift)
    {
        int deshiftBits = data ^ shift;
        return Float.intBitsToFloat(deshiftBits);
    }
}
