package com.deadtiger.advcreation.utility.shape_creator;

import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class CircleRectangleCreator extends BaseShapeCreator
{


    public static final CircleRectangleCreator INSTANCE = new CircleRectangleCreator();
    @Override
    public void init()
    {
        if(isInitialised)
            return;
        //this is a list of good positions where a nice circle is drawn the preview will jump to these position dependent on the mouse position
//        hardcodedRadiusLengthList.add(0.25);//one block
//        mapRadiusLengthToRadialVector.put(0.25,null);
//        squareDict.put(0.25,1);
//
//        hardcodedRadiusLengthList.add(0.75);//two block
//        mapRadiusLengthToRadialVector.put(0.75,null);
//        squareDict.put(0.75,2);
//
//        hardcodedRadiusLengthList.add(1.2500);
//        mapRadiusLengthToRadialVector.put(1.2500, new Vec3d(0.0000, 0.0000, -1.2500));
//
//        hardcodedRadiusLengthList.add(1.5);//three blocks
//        mapRadiusLengthToRadialVector.put(1.5,null);
//        squareDict.put(1.5,3);
//
//        hardcodedRadiusLengthList.add(1.7500);
//        mapRadiusLengthToRadialVector.put(1.7500, new Vec3d(0.0000, 0.0000, -1.7500));
//
//        hardcodedRadiusLengthList.add(2.0);//four blocks
//        mapRadiusLengthToRadialVector.put(2.0,null);
//        squareDict.put(2.0,4);
//
//        hardcodedRadiusLengthList.add(2.2500);
//        mapRadiusLengthToRadialVector.put(2.2500, new Vec3d(0.0000, 0.0000, -2.2500));
//
//        hardcodedRadiusLengthList.add(2.5);//five blocks
//        mapRadiusLengthToRadialVector.put(2.5,null);
//        squareDict.put(2.5,5);
//
//        hardcodedRadiusLengthList.add(2.75);//six blocks
//        mapRadiusLengthToRadialVector.put(2.75,null);
//        squareDict.put(2.75,6);
//
//        hardcodedRadiusLengthList.add(3.0000);
//        mapRadiusLengthToRadialVector.put(3.0000, new Vec3d(0.0000, 0.0000, -3.0000));
//
//        hardcodedRadiusLengthList.add(3.15);//seven blocks
//        mapRadiusLengthToRadialVector.put(3.15,null);
//        squareDict.put(3.15,7);
//
//        hardcodedRadiusLengthList.add(3.30);//eight blocks
//        mapRadiusLengthToRadialVector.put(3.30,null);
//        squareDict.put(3.30,8);
//
//        hardcodedRadiusLengthList.add(3.5355);
//        mapRadiusLengthToRadialVector.put(3.5355, new Vec3d(-0.5000, 0.0000, -3.5000));
//
//
//
//        hardcodedRadiusLengthList.add(4.0000);
//        mapRadiusLengthToRadialVector.put(4.0000, new Vec3d(0.0000, 0.0000, -4.0000));
//        hardcodedRadiusLengthList.add(4.2793);
//        mapRadiusLengthToRadialVector.put(4.2793, new Vec3d(-0.5000, 0.0000, -4.2500));
//
//        hardcodedRadiusLengthList.add(4.37);//nine blocks
//        mapRadiusLengthToRadialVector.put(4.37,null);
//        squareDict.put(4.37,9);
//
//        hardcodedRadiusLengthList.add(4.45);//ten blocks
//        mapRadiusLengthToRadialVector.put(4.45,null);
//        squareDict.put(4.45,10);
//
//        hardcodedRadiusLengthList.add(4.5069);
//        mapRadiusLengthToRadialVector.put(4.5069, new Vec3d(-0.2500, 0.0000, -4.5000));
//        hardcodedRadiusLengthList.add(4.7500);
//        mapRadiusLengthToRadialVector.put(4.7500, new Vec3d(0.0000, 0.0000, -4.7500));
//
//
//        hardcodedRadiusLengthList.add(5.10);//elven blocks
//        mapRadiusLengthToRadialVector.put(5.10,null);
//        squareDict.put(5.10,11);
//
//        hardcodedRadiusLengthList.add(5.5227);
//        mapRadiusLengthToRadialVector.put(5.5227, new Vec3d(0.5000, 0.0000, -5.5000));
//
//
//
//        hardcodedRadiusLengthList.add(5.7009);
//        mapRadiusLengthToRadialVector.put(5.7009, new Vec3d(-1.5000, 0.0000, -5.5000));
//        //bigger than 6
//        hardcodedRadiusLengthList.add(6.0879);
//        mapRadiusLengthToRadialVector.put(6.0879, new Vec3d(5.7500, 0.0000, -2.0000));
//
//        hardcodedRadiusLengthList.add(6.5000);
//        mapRadiusLengthToRadialVector.put(6.5000, new Vec3d(6.5000, 0.0000, 0.0000));
//
//        hardcodedRadiusLengthList.add(6.5015);//twelve blocks
//        mapRadiusLengthToRadialVector.put(6.5015,null);
//        squareDict.put(6.5015,12);
//
//        hardcodedRadiusLengthList.add(6.5030);//thirteen blocks
//        mapRadiusLengthToRadialVector.put(6.5030,null);
//        squareDict.put(6.5030,13);
//
//        hardcodedRadiusLengthList.add(6.5048);
//        mapRadiusLengthToRadialVector.put(6.5048, new Vec3d(6.5000, 0.0000, -0.2500));
//        hardcodedRadiusLengthList.add(6.6708);
//        mapRadiusLengthToRadialVector.put(6.6708, new Vec3d(6.5000, 0.0000, -1.5000));
//
//        hardcodedRadiusLengthList.add(7.0000);
//        mapRadiusLengthToRadialVector.put(7.0000, new Vec3d(7.0000, 0.0000, 0.0000));
//
//
//
//        hardcodedRadiusLengthList.add(7.4204);
//        mapRadiusLengthToRadialVector.put(7.4204, new Vec3d(6.2500, 0.0000, 4.0000));
//
//        hardcodedRadiusLengthList.add(7.46);//fourteen blocks
//        mapRadiusLengthToRadialVector.put(7.46,null);
//        squareDict.put(7.46,14);
//
//        hardcodedRadiusLengthList.add(7.5166);
//        mapRadiusLengthToRadialVector.put(7.5166, new Vec3d(7.5000, 0.0000, -0.5000));
//        hardcodedRadiusLengthList.add(7.9392);
//        mapRadiusLengthToRadialVector.put(7.9392, new Vec3d(7.9271, 0.0000, 0.4380));
//        //bigger then 8
//        hardcodedRadiusLengthList.add(8.2651);
//        mapRadiusLengthToRadialVector.put(8.2651, new Vec3d(0.5000, 0.0000, -8.2500));
//        hardcodedRadiusLengthList.add(8.8667);
//        mapRadiusLengthToRadialVector.put(8.8667, new Vec3d(8.8651, 0.0000, -0.1703));
//        hardcodedRadiusLengthList.add(9.8194);
//        mapRadiusLengthToRadialVector.put(9.8194, new Vec3d(9.0659, 1.0000, 3.6374));


        hardcodedRadiusLengthList.add(0.40);//two block
        mapRadiusLengthToRadialVector.put(0.40,null);
        squareDict.put(0.40,1);

        hardcodedRadiusLengthList.add(0.75);//two block
        mapRadiusLengthToRadialVector.put(0.75,null);
        squareDict.put(0.75,2);

        hardcodedRadiusLengthList.add(1.1929);
        mapRadiusLengthToRadialVector.put(1.1929,new Vec3d(0.0381,0.0500,1.1912));

        hardcodedRadiusLengthList.add(1.4);//three blocks
        mapRadiusLengthToRadialVector.put(1.4,null);
        squareDict.put(1.4,3);

        hardcodedRadiusLengthList.add(1.85);//four blocks
        mapRadiusLengthToRadialVector.put(1.85,null);
        squareDict.put(1.85,4);

        hardcodedRadiusLengthList.add(1.7599);
        mapRadiusLengthToRadialVector.put(1.7599,new Vec3d(1.1918,0.0500,1.2940));
        hardcodedRadiusLengthList.add(1.9694);
        mapRadiusLengthToRadialVector.put(1.9694,new Vec3d(-1.6331,0.0500,-1.0995));

        hardcodedRadiusLengthList.add(2.25);//five blocks
        mapRadiusLengthToRadialVector.put(2.25,null);
        squareDict.put(2.25,5);

        hardcodedRadiusLengthList.add(2.90);//six blocks
        mapRadiusLengthToRadialVector.put(2.90,null);
        squareDict.put(2.90,6);
        hardcodedRadiusLengthList.add(2.5542);
        mapRadiusLengthToRadialVector.put(2.5542,new Vec3d(-2.3299,0.0500,1.0455));
        hardcodedRadiusLengthList.add(2.7377);
        mapRadiusLengthToRadialVector.put(2.7377,new Vec3d(-2.2418,0.0500,1.5707));
        hardcodedRadiusLengthList.add(3.4263);
        mapRadiusLengthToRadialVector.put(3.4263,new Vec3d(-2.6171,0.0500,2.2108));

        hardcodedRadiusLengthList.add(3.10);//seven blocks
        mapRadiusLengthToRadialVector.put(3.10,null);
        squareDict.put(3.10,7);

        hardcodedRadiusLengthList.add(3.90);//eight blocks
        mapRadiusLengthToRadialVector.put(3.90,null);
        squareDict.put(3.90,8);

        hardcodedRadiusLengthList.add(3.6040);
        mapRadiusLengthToRadialVector.put(3.6040,new Vec3d(-2.7332,0.0500,2.3486));
        hardcodedRadiusLengthList.add(3.7346);
        mapRadiusLengthToRadialVector.put(3.7346,new Vec3d(-2.7683,0.0500,2.5062));
        hardcodedRadiusLengthList.add(4.1149);
        mapRadiusLengthToRadialVector.put(4.1149,new Vec3d(-2.8327,0.0500,2.9843));

        hardcodedRadiusLengthList.add(4.37);//nine blocks
        mapRadiusLengthToRadialVector.put(4.37,null);
        squareDict.put(4.37,9);

        hardcodedRadiusLengthList.add(4.80);//ten blocks
        mapRadiusLengthToRadialVector.put(4.80,null);
        squareDict.put(4.80,10);

        hardcodedRadiusLengthList.add(4.5934);
        mapRadiusLengthToRadialVector.put(4.5934,new Vec3d(-2.0477,0.0500,4.1115));
        hardcodedRadiusLengthList.add(4.6163);
        mapRadiusLengthToRadialVector.put(4.6163,new Vec3d(-0.2816,0.0500,4.6075));
        hardcodedRadiusLengthList.add(4.6401);
        mapRadiusLengthToRadialVector.put(4.6401,new Vec3d(-2.2438,0.0500,4.0612));

        hardcodedRadiusLengthList.add(4.9688);
        mapRadiusLengthToRadialVector.put(4.9688,new Vec3d(-3.1095,0.0500,3.8752));
        hardcodedRadiusLengthList.add(5.4838);
        mapRadiusLengthToRadialVector.put(5.4838,new Vec3d(-2.5914,0.0500,4.8326));

        hardcodedRadiusLengthList.add(5.20);//elven blocks
        mapRadiusLengthToRadialVector.put(5.20,null);
        squareDict.put(5.20,11);

        hardcodedRadiusLengthList.add(5.8);//twelve blocks
        mapRadiusLengthToRadialVector.put(5.8,null);
        squareDict.put(5.8,12);

        hardcodedRadiusLengthList.add(5.5560);
        mapRadiusLengthToRadialVector.put(5.5560,new Vec3d(-2.6085,0.0500,4.9053));
        hardcodedRadiusLengthList.add(5.6279);
        mapRadiusLengthToRadialVector.put(5.6279,new Vec3d(-2.6255,0.0500,4.9777));
        hardcodedRadiusLengthList.add(5.9795);
        mapRadiusLengthToRadialVector.put(5.9795,new Vec3d(-1.3994,0.0500,5.8132));
        hardcodedRadiusLengthList.add(6.0439);
        mapRadiusLengthToRadialVector.put(6.0439,new Vec3d(-1.4789,0.0500,5.8600));
        hardcodedRadiusLengthList.add(6.3054);
        mapRadiusLengthToRadialVector.put(6.3054,new Vec3d(-1.7334,0.0500,6.0622));

        hardcodedRadiusLengthList.add(6.45);//thirteen blocks
        mapRadiusLengthToRadialVector.put(6.45,null);
        squareDict.put(6.45,13);

        hardcodedRadiusLengthList.add(6.7);//fourteen blocks
        mapRadiusLengthToRadialVector.put(6.7,null);
        squareDict.put(6.7,14);

        hardcodedRadiusLengthList.add(6.6381);
        mapRadiusLengthToRadialVector.put(6.6381,new Vec3d(-2.6069,0.0500,6.1046));
        hardcodedRadiusLengthList.add(6.8225);
        mapRadiusLengthToRadialVector.put(6.8225,new Vec3d(-3.6919,0.0500,5.7371));
        hardcodedRadiusLengthList.add(7.1875);
        mapRadiusLengthToRadialVector.put(7.1875,new Vec3d(-4.3982,0.0500,5.6844));
        hardcodedRadiusLengthList.add(7.4710);
        mapRadiusLengthToRadialVector.put(7.4710,new Vec3d(-5.5466,0.0500,5.0048));

        hardcodedRadiusLengthList.add(7.325);//fifteen blocks
        mapRadiusLengthToRadialVector.put(7.325,null);
        squareDict.put(7.325,15);

        hardcodedRadiusLengthList.add(7.74);//sixteen blocks
        mapRadiusLengthToRadialVector.put(7.74,null);
        squareDict.put(7.74,16);

        hardcodedRadiusLengthList.add(7.5398);
        mapRadiusLengthToRadialVector.put(7.5398,new Vec3d(0.0251,0.0500,7.5396));
        hardcodedRadiusLengthList.add(7.6085);
        mapRadiusLengthToRadialVector.put(7.6085,new Vec3d(-0.0367,0.0500,7.6082));
        hardcodedRadiusLengthList.add(7.6584);
        mapRadiusLengthToRadialVector.put(7.6584,new Vec3d(-5.2223,0.0500,5.6014));
        hardcodedRadiusLengthList.add(7.8258);
        mapRadiusLengthToRadialVector.put(7.8258,new Vec3d(-0.1823,0.0500,7.8235));
        hardcodedRadiusLengthList.add(8.0908);
        mapRadiusLengthToRadialVector.put(8.0908,new Vec3d(-6.2188,0.0500,5.1755));
        hardcodedRadiusLengthList.add(8.2641);
        mapRadiusLengthToRadialVector.put(8.2641,new Vec3d(-6.1333,0.0500,5.5385));

        hardcodedRadiusLengthList.add(8.38);//seventeen blocks
        mapRadiusLengthToRadialVector.put(8.38,null);
        squareDict.put(8.38,17);
        hardcodedRadiusLengthList.add(8.6);//eighteen blocks
        mapRadiusLengthToRadialVector.put(8.6,null);
        squareDict.put(8.6,18);

        hardcodedRadiusLengthList.add(8.5024);
        mapRadiusLengthToRadialVector.put(8.5024,new Vec3d(0.1740,0.0500,8.5005));
        hardcodedRadiusLengthList.add(8.5095);
        mapRadiusLengthToRadialVector.put(8.5095,new Vec3d(0.0451,0.0500,8.5092));
        hardcodedRadiusLengthList.add(8.7233);
        mapRadiusLengthToRadialVector.put(8.7233,new Vec3d(0.0681,0.0500,8.7229));
        hardcodedRadiusLengthList.add(8.7815);
        mapRadiusLengthToRadialVector.put(8.7815,new Vec3d(-6.1705,0.0500,6.2480));
        hardcodedRadiusLengthList.add(8.9657);
        mapRadiusLengthToRadialVector.put(8.9657,new Vec3d(-6.1162,0.0500,6.5555));
        hardcodedRadiusLengthList.add(8.9657);
        mapRadiusLengthToRadialVector.put(8.9657,new Vec3d(-6.1162,0.0500,6.5555));
        hardcodedRadiusLengthList.add(9.0714);
        mapRadiusLengthToRadialVector.put(9.0714,new Vec3d(-6.1436,0.0500,6.6741));
        hardcodedRadiusLengthList.add(9.4622);
        mapRadiusLengthToRadialVector.put(9.4622,new Vec3d(-7.0919,0.0500,6.2638));

        hardcodedRadiusLengthList.add(9.2);//nineteen blocks
        mapRadiusLengthToRadialVector.put(9.2,null);
        squareDict.put(9.2,19);
        hardcodedRadiusLengthList.add(9.35);//twenty blocks
        mapRadiusLengthToRadialVector.put(9.35,null);
        squareDict.put(9.35,20);

        hardcodedRadiusLengthList.add(9.5247);
        mapRadiusLengthToRadialVector.put(9.5247,new Vec3d(-0.3686,0.0500,9.5174));
        hardcodedRadiusLengthList.add(9.6804);
        mapRadiusLengthToRadialVector.put(9.6804,new Vec3d(-7.7098,0.0500,5.8537));
        hardcodedRadiusLengthList.add(9.7708);
        mapRadiusLengthToRadialVector.put(9.7708,new Vec3d(-8.9662,0.0500,3.8825));
        hardcodedRadiusLengthList.add(9.9400);
        mapRadiusLengthToRadialVector.put(9.9400,new Vec3d(-8.8872,0.0500,4.4517));
        hardcodedRadiusLengthList.add(9.9892);
        mapRadiusLengthToRadialVector.put(9.9892,new Vec3d(-8.9059,0.0500,4.5240));
        hardcodedRadiusLengthList.add(10.2904);
        mapRadiusLengthToRadialVector.put(10.2904,new Vec3d(-8.9668,0.0500,5.0483));
        hardcodedRadiusLengthList.add(10.3434);
        mapRadiusLengthToRadialVector.put(10.3434,new Vec3d(-8.9343,0.0500,5.2116));
        hardcodedRadiusLengthList.add(10.4516);
        mapRadiusLengthToRadialVector.put(10.4516,new Vec3d(-8.9207,0.0500,5.4457));

        hardcodedRadiusLengthList.add(10.47);//twenty-one blocks
        mapRadiusLengthToRadialVector.put(10.47,null);
        squareDict.put(10.47,21);
        hardcodedRadiusLengthList.add(10.49);//twenty-two blocks
        mapRadiusLengthToRadialVector.put(10.49,null);
        squareDict.put(10.49,22);

        hardcodedRadiusLengthList.add(10.5098);
        mapRadiusLengthToRadialVector.put(10.5098,new Vec3d(-8.8883,0.0500,5.6081));
        hardcodedRadiusLengthList.add(10.5702);
        mapRadiusLengthToRadialVector.put(10.5702,new Vec3d(-8.8562,0.0500,5.7700));
        hardcodedRadiusLengthList.add(10.7338);
        mapRadiusLengthToRadialVector.put(10.7338,new Vec3d(-8.9118,0.0500,5.9826));
        hardcodedRadiusLengthList.add(10.7886);
        mapRadiusLengthToRadialVector.put(10.7886,new Vec3d(-8.9303,0.0500,6.0532));
        hardcodedRadiusLengthList.add(10.9541);
        mapRadiusLengthToRadialVector.put(10.9541,new Vec3d(-8.9857,0.0500,6.2648));
        hardcodedRadiusLengthList.add(10.9648);
        mapRadiusLengthToRadialVector.put(10.9648,new Vec3d(-8.9353,0.0500,6.3550));
        hardcodedRadiusLengthList.add(11.0295);
        mapRadiusLengthToRadialVector.put(11.0295,new Vec3d(-9.2252,0.0500,6.0451));
        hardcodedRadiusLengthList.add(11.3067);
        mapRadiusLengthToRadialVector.put(11.3067,new Vec3d(-11.3062,0.0500,0.0875));

        hardcodedRadiusLengthList.add(11.42);//twenty-three blocks
        mapRadiusLengthToRadialVector.put(11.42,null);
        squareDict.put(11.42,23);
        hardcodedRadiusLengthList.add(11.52);//twenty-four blocks
        mapRadiusLengthToRadialVector.put(11.52,null);
        squareDict.put(11.52,24);

        hardcodedRadiusLengthList.add(11.6490);
        mapRadiusLengthToRadialVector.put(11.6490,new Vec3d(-11.6465,0.0500,0.2360));
        hardcodedRadiusLengthList.add(11.7201);
        mapRadiusLengthToRadialVector.put(11.7201,new Vec3d(-11.7181,0.0500,0.2155));
        hardcodedRadiusLengthList.add(11.8817);
        mapRadiusLengthToRadialVector.put(11.8817,new Vec3d(-11.8789,0.0500,0.2512));
        hardcodedRadiusLengthList.add(11.8180);
        mapRadiusLengthToRadialVector.put(11.8180,new Vec3d(-11.8177,0.0500,0.0665));
        hardcodedRadiusLengthList.add(11.9400);
        mapRadiusLengthToRadialVector.put(11.9400,new Vec3d(-11.9397,0.0500,0.0625));
        hardcodedRadiusLengthList.add(12.1026);
        mapRadiusLengthToRadialVector.put(12.1026,new Vec3d(-12.1023,0.0500,0.0571));
        hardcodedRadiusLengthList.add(12.1840);
        mapRadiusLengthToRadialVector.put(12.1840,new Vec3d(-12.1837,0.0500,0.0544));
        hardcodedRadiusLengthList.add(12.4371);
        mapRadiusLengthToRadialVector.put(12.4371,new Vec3d(-12.4345,0.0500,0.2498));

        hardcodedRadiusLengthList.add(12.46);//twenty-five blocks
        mapRadiusLengthToRadialVector.put(12.46,null);
        squareDict.put(12.46,25);
        hardcodedRadiusLengthList.add(12.49);//twenty-six blocks
        mapRadiusLengthToRadialVector.put(12.49,null);
        squareDict.put(12.49,26);

        hardcodedRadiusLengthList.add(12.5255);
        mapRadiusLengthToRadialVector.put(12.5255,new Vec3d(-2.3949,0.0500,12.2944));
        hardcodedRadiusLengthList.add(12.6100);
        mapRadiusLengthToRadialVector.put(12.6100,new Vec3d(-12.5780,0.0500,0.8967));
        hardcodedRadiusLengthList.add(12.6449);
        mapRadiusLengthToRadialVector.put(12.6449,new Vec3d(-9.1708,0.0500,8.7056));
        hardcodedRadiusLengthList.add(12.7605);
        mapRadiusLengthToRadialVector.put(12.7605,new Vec3d(-9.2154,0.0500,8.8263));
        hardcodedRadiusLengthList.add(12.9326);
        mapRadiusLengthToRadialVector.put(12.9326,new Vec3d(-9.3809,0.0500,8.9021));
        hardcodedRadiusLengthList.add(12.9902);
        mapRadiusLengthToRadialVector.put(12.9902,new Vec3d(-9.4228,0.0500,8.9416));
        hardcodedRadiusLengthList.add(13.1410);
        mapRadiusLengthToRadialVector.put(13.1410,new Vec3d(-8.1247,0.0500,10.3283));
        hardcodedRadiusLengthList.add(13.3773);
        mapRadiusLengthToRadialVector.put(13.3773,new Vec3d(-8.2534,0.0500,10.5276));
        hardcodedRadiusLengthList.add(13.4999);
        mapRadiusLengthToRadialVector.put(13.4999,new Vec3d(-8.2982,0.0500,10.6482));

        hardcodedRadiusLengthList.add(13.53);//twenty-seven blocks
        mapRadiusLengthToRadialVector.put(13.53,null);
        squareDict.put(13.53,27);
        hardcodedRadiusLengthList.add(13.56);//twenty-eight blocks
        mapRadiusLengthToRadialVector.put(13.56,null);
        squareDict.put(13.56,28);

        hardcodedRadiusLengthList.add(13.5324);
        mapRadiusLengthToRadialVector.put(13.5324,new Vec3d(-8.6169,0.0500,10.4342));
        hardcodedRadiusLengthList.add(13.5897);
        mapRadiusLengthToRadialVector.put(13.5897,new Vec3d(-8.3415,0.0500,10.7283));
        hardcodedRadiusLengthList.add(13.6692);
        mapRadiusLengthToRadialVector.put(13.6692,new Vec3d(-9.1769,0.0500,10.1306));
        hardcodedRadiusLengthList.add(13.9068);
        mapRadiusLengthToRadialVector.put(13.9068,new Vec3d(-13.9030,0.0500,0.3234));

        isInitialised = true;
    }
}
