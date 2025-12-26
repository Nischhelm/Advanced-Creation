package com.deadtiger.advcreation.debug;

import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.utility.CursorVector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DebugInfo
{
    public static Vec3d CURR_HITPOS = Vec3d.ZERO;
        public static Vec3d hitPosition = Vec3d.ZERO;
        public static Vec3d playerPos = Vec3d.ZERO;

        public static CursorVector cursorVector = new CursorVector(Vec3d.ZERO,Vec3d.ZERO);

        public static Vec3d cameraFocusPoint = Vec3d.ZERO;
        public static Vec3d cameraVectorToCursorOnScreen = Vec3d.ZERO;
        public static Vec3d cameraVectorToCursorOnScreenAtZoom = Vec3d.ZERO;
        public static Vec3d cameraVectorToMiddleScreen = Vec3d.ZERO;

        //    public static Vec3d viewportMidPoint = Vec3d.ZERO;
//    public static Vec3d viewportSurfaceIntersection = Vec3d.ZERO;
        public static Vec3d leftdown = Vec3d.ZERO;
        public static Vec3d leftup = Vec3d.ZERO;
        public static Vec3d rightdown = Vec3d.ZERO;
        public static Vec3d rightup = Vec3d.ZERO;

        public static double viewAngle_Z = 0;
        public static double viewAngle_X = 0;
        public static double calc_viewAngle_Z = 0;
        public static double calc_viewAngle_X = 0;

        public static int screen_x = 0;
        public static int screen_z = 0;
        public static int calc_screen_x = 0;
        public static int calc_screen_z = 0;

        public static HashMap<Vec3d,Vec3d> viewportMidPointToIntersectPoint = new HashMap();

        public static Vec3d leftdown2Abs = Vec3d.ZERO;
        public static Vec3d leftup2Abs = Vec3d.ZERO;
        public static Vec3d rightdown2Abs = Vec3d.ZERO;
        public static Vec3d rightup2Abs = Vec3d.ZERO;

        public static Vec3d leftRightdown2Abs = Vec3d.ZERO;
        public static Vec3d leftRightup2Abs = Vec3d.ZERO;
        public static Vec3d rightleftdown2Abs = Vec3d.ZERO;
        public static Vec3d rightleftup2Abs = Vec3d.ZERO;

        public static Vec3d leftdown3Abs = Vec3d.ZERO;
        public static Vec3d leftup3Abs = Vec3d.ZERO;
        public static Vec3d rightdown3Abs = Vec3d.ZERO;
        public static Vec3d rightup3Abs = Vec3d.ZERO;

        public static Vec3d leftRightdown3Abs = Vec3d.ZERO;
        public static Vec3d leftRightup3Abs = Vec3d.ZERO;
        public static Vec3d rightleftdown3Abs = Vec3d.ZERO;
        public static Vec3d rightleftup3Abs = Vec3d.ZERO;

        public static Vec3d closestup3Abs = Vec3d.ZERO;
        public static Vec3d closestdown3Abs = Vec3d.ZERO;

        public static Vec3d calc05_3Abs = Vec3d.ZERO;
        public static Vec3d calc2_3Abs = Vec3d.ZERO;
        public static Vec3d calc4_3Abs = Vec3d.ZERO;

        public static ArrayList<ArrayList<HashMap<String,Vec3d>>> smallerCubes = new ArrayList<>();
        public static ArrayList<CursorVector> drawVectors = new ArrayList<>();

        //circle lengths
        private static double prevRadius =0;
        private static double prevChosenRadius =0;

        public static Color colors[] = {new Color(0,255,230),
                new Color(255,190,0),
                new Color(149, 255,0),
                new Color(255,0,170),
                new Color(185,185,255),
                new Color(255, 185, 185),
                new Color(107, 0, 0),
                new Color(119, 162, 102)};


        public static void renderDebugInformation(float PartialTicks)
        {

            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();
            GlStateManager.disableLighting();
            //DEBUG draw camera focus point path should you zoom
            lineOpaqueRender(playerPos,hitPosition,1.0f,1.0f,1f,PartialTicks);
            //DEBUG draw mouse point vector
            if(cursorVector != null && cursorVector.start != null && cursorVector.end != null)
                lineOpaqueRender(cursorVector.start,cursorVector.end,1.0f,0.0f,1f,PartialTicks);
//        DEBUG draw camera focus point to cursor vector
            if(cameraFocusPoint != null && cameraVectorToCursorOnScreenAtZoom != null)
                lineOpaqueRender(cameraFocusPoint,cameraFocusPoint.add(cameraVectorToCursorOnScreenAtZoom),0f,0f,1f,PartialTicks);
////        DEBUG draw camera focus point to screen vector
//            if(cameraFocusPoint != null && cameraVectorToMiddleScreen != null)
//                lineOpaqueRender(cameraFocusPoint,cameraFocusPoint.add(cameraVectorToMiddleScreen),0f,1f,0f,PartialTicks);

            Vec3d normCameraVector =  cameraVectorToMiddleScreen.normalize();

            double newZtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(normCameraVector);
            double newYtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(leftup.add(rightup).normalize());
            double newXtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(rightdown.add(rightup).normalize());

            double reverseCalcViewAngleY = ((Math.atan(newYtoHitpoint/(newZtoHitpoint- ModEntityRenderer.customCameraDistance)))/Math.PI)*180.0;
            double reverseCalcViewAngleX = -((Math.atan(newXtoHitpoint/(newZtoHitpoint- ModEntityRenderer.customCameraDistance)))/Math.PI)*180.0;


//        System.out.println("original angle and screen  X/Z " + String.format("%,.4f",((viewAngle_X/Math.PI)*180.0)) + "/" +String.format("%,.4f",((viewAngle_Z/Math.PI)*180.0)) + "° "
//                + screen_x + "/" + screen_z + "px calculated screen X/Z " +
//                String.format("%,.4f",((calc_viewAngle_X/Math.PI)*180.0)) + "/" + String.format("%,.4f",((calc_viewAngle_Z/Math.PI)*180.0)) + "° "
//                +  calc_screen_x + "/" + calc_screen_z +
//                " other calculation give X/Z " + String.format("%,.4f",reverseCalcViewAngleX) + "/" + String.format("%,.4f",reverseCalcViewAngleY) + "° " );
//        System.out.println("view_angle_Y/X " + ((viewAngle_Z/Math.PI)*180.0) + "/" + ((viewAngle_X/Math.PI)*180.0) + " reverse calc angle Y/X " + reverseCalcViewAngleY + "/" + reverseCalcViewAngleX);

//        //DEBUG render plane arround the camera focus point to show the viewport of the player
//        Vec3d viewportSurfaceIntersection = null;
//        if(leftdown != null && leftup != null && rightdown != null && rightup != null)
//        {
//            for (Vec3d viewportMidPoint :viewportMidPointToIntersectPoint.keySet())
//            {
//                viewportSurfaceIntersection = viewportMidPointToIntersectPoint.get(viewportMidPoint);
//
//                Vec3d leftdownF = leftdown;
//                Vec3d leftupF = leftup;
//                Vec3d rightupF = rightup;
//                Vec3d rightdownF = rightdown;
//
//                renderPlaneWireFrame(matrixStack, buffer, viewportMidPoint, leftdownF, leftupF, rightupF, rightdownF);
//
//                if(viewportSurfaceIntersection != null)
//                {
//                    lineOpaqueRender(viewportMidPoint.add(rightdown),viewportSurfaceIntersection,1f,0f,0f,PartialTicks);
//                    lineOpaqueRender(viewportMidPoint.add(rightup),viewportSurfaceIntersection,1f,0f,0f,PartialTicks);
//                    lineOpaqueRender(viewportMidPoint.add(leftdown),viewportSurfaceIntersection,1f,0f,0f,PartialTicks);
//                    lineOpaqueRender(viewportMidPoint.add(leftup),viewportSurfaceIntersection,1f,0f,0f,PartialTicks);
//                }
//            }
//
//            if(leftdown2Abs != null && leftup2Abs != null && rightdown2Abs != null && rightup2Abs != null &&
//                    leftRightdown2Abs != null && leftRightup2Abs != null && rightleftdown2Abs != null && rightleftup2Abs != null)
//            {
//                renderBox(matrixStack,buffer,leftdown2Abs,rightup2Abs,0f,1f,0f,1f);
//            }
//

//

            for (int i = 0; i < drawVectors.size(); i++)
            {
                Color newColor = debugColor(i);


                float[] comp = newColor.getColorComponents(null);
                CursorVector vector = drawVectors.get(i);
                lineOpaqueRender(vector.start,vector.end,comp[0], comp[1], comp[2],PartialTicks);
            }

            //proof that I can render a bunch of block surfaces in a plane in one go.
//        renderTexturedPlane(Blocks.GRASS_BLOCK.defaultBlockState(), Direction.EAST,cameraFocusPoint,cameraFocusPoint.add(5,5,0),1,1,1);
//        renderTexturedPlane(Blocks.GRASS_BLOCK.defaultBlockState(), Direction.UP,cameraFocusPoint,cameraFocusPoint.add(5,0,5),1,1,1);
//        renderTexturedPlane(Blocks.GRASS_BLOCK.defaultBlockState(), Direction.WEST,cameraFocusPoint,cameraFocusPoint.add(0,5,5),1,1,1);



//            for (ArrayList<HashMap<String,Vec3d>> areas:smallerCubes)
//            {
//                for (int i = 0; i < areas.size(); i++)
//                {
//                    HashMap<String,Vec3d> corners= areas.get(i);
//                    Color newColor;
//                    if(i >= colors.length)
//                    {
//                        newColor = new Color(0, 0, 0);
//                    }
//                    else
//                        newColor = colors[i];
//
//                    Vec3d leftdown = corners.get("leftdown");
//                    Vec3d leftup = corners.get("leftup");
//                    Vec3d rightdown = corners.get("rightdown");
//                    Vec3d rightup = corners.get("rightup");
//                    Vec3d midpoint;
//
//                    for (int j = 1; j < 5; j++)
//                    {
//                        if(corners.containsKey("midpoint" + j))
//                        {
//                            midpoint = corners.get("midpoint" + j);
//                            float[] comp = newColor.getColorComponents(null);
//                            renderPlaneWireFrame(matrixStack,buffer,midpoint,leftdown,leftup,rightup,rightdown,comp[0], comp[1], comp[2]);
//                        }
//
//                    }
//                }
//            }
//        }
            GlStateManager.enableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();
        }

        private static void renderPlaneWireFrame( Vec3d viewportMidPoint, Vec3d leftdownF, Vec3d leftupF, Vec3d rightupF, Vec3d rightdownF, float partialTicks)
        {
            renderPlaneWireFrame(viewportMidPoint,leftdownF,leftupF,rightupF,rightdownF,1f,0,0,partialTicks);
        }

        private static void renderPlaneWireFrame( Vec3d viewportMidPoint, Vec3d leftdownF, Vec3d leftupF, Vec3d rightupF, Vec3d rightdownF,float r, float g, float b, float partialTicks)
        {
            lineOpaqueRender(viewportMidPoint.add(leftdownF),viewportMidPoint.add(leftupF),r,g,b,partialTicks);
            lineOpaqueRender(viewportMidPoint.add(leftupF),viewportMidPoint.add(rightupF),r,g,b,partialTicks);
            lineOpaqueRender(viewportMidPoint.add(rightupF),viewportMidPoint.add(rightdownF),r,g,b,partialTicks);
            lineOpaqueRender(viewportMidPoint.add(rightdownF),viewportMidPoint.add(leftdownF),r,g,b,partialTicks);
        }


        public static void reverseCalculateViewAngles(Vec3d middleScreenVector,Vec3d hitPos)
        {
            Vec3d cursor_vectorAngled = hitPos.subtract(middleScreenVector);



//        Vec3d vec3d = entity.getEyePosition(partialTicks).add(X_mouseAngled, Y_mouseAngled, Z_mouseAngled);
        }



        public static void renderBox( Vec3d startPos, Vec3d endPos, float red, float green, float blue, float alpha, float partialTicks) {
//        RenderGlobal renderglobal = mc.renderGlobal;
//            RenderSystem.enableBlend();
//            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
//            RenderSystem.lineWidth(2.0F);
//            RenderSystem.disableTexture();
//            RenderSystem.depthMask(false);



            double startX = startPos.x;
            double endX = endPos.x;
            if(startPos.x > endPos.x)
            {
                startX = endPos.x;
                endX = startPos.x;
            }
            double startY = startPos.y;
            double endY = endPos.y;
            if(startPos.y > endPos.y)
            {
                startY = endPos.y;
                endY = startPos.y;
            }
            double startZ = startPos.z;
            double endZ = endPos.z;
            if(startPos.z > endPos.z)
            {
                startZ = endPos.z;
                endZ = startPos.z;
            }

            lineOpaqueRender(new Vec3d(startX,startY,startZ),new Vec3d(endX,startY,startZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(startX,startY,startZ),new Vec3d(startX,startY,endZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(endX,startY,startZ),new Vec3d(endX,startY,endZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(startX,startY,endZ),new Vec3d(endX,startY,endZ),red,green,blue,partialTicks);

            lineOpaqueRender(new Vec3d(startX,endY,startZ),new Vec3d(endX,endY,startZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(startX,endY,startZ),new Vec3d(startX,endY,endZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(endX,endY,startZ),new Vec3d(endX,endY,endZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(startX,endY,endZ),new Vec3d(endX,endY,endZ),red,green,blue,partialTicks);

            lineOpaqueRender(new Vec3d(startX,startY,startZ),new Vec3d(startX,endY,startZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(startX,startY,endZ),new Vec3d(startX,endY,endZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(endX,startY,startZ),new Vec3d(endX,endY,startZ),red,green,blue,partialTicks);
            lineOpaqueRender(new Vec3d(endX,startY,endZ),new Vec3d(endX,endY,endZ),red,green,blue,partialTicks);

//            RenderSystem.depthMask(true);
//            RenderSystem.enableTexture();
//            RenderSystem.disableBlend();

        }

        public static void debugPrint(String debugInfo)
        {
//            if(AdvCreation.debugMode)
                System.out.println(debugInfo);
        }

        public static Color debugColor(int ind)
        {
            Color newColor;
            if(ind >= colors.length)
            {
                newColor = new Color(0, 0, 0);
            }
            else
                newColor = colors[ind];
            return newColor;
        }

        public static void printRadius(double radius, double chosenRadius, int radiusIndex)
        {
            if(chosenRadius != prevChosenRadius)
            {
                System.out.println(radiusIndex + " new chosenLength: " + chosenRadius + " at radius " + radius);
            }
            else if(radius != prevRadius)
            {
                System.out.println(radiusIndex + " new radius: " + radius);
            }

            prevRadius = radius;
            prevChosenRadius = chosenRadius;
        }
        
        public static void lineOpaqueRender(Vec3d worldBeginVec, Vec3d worldEndVec, float r, float g, float b, float partialTicks)
        {
           
            RenderTemplate.drawLine(worldBeginVec.x, worldBeginVec.y, worldBeginVec.z, worldEndVec.x, worldEndVec.y, worldEndVec.z, Minecraft.getMinecraft().player, 0, partialTicks, r,g,b);
            
        }
        
}
