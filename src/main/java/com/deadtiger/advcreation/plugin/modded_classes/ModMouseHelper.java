package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class ModMouseHelper
{
    public static boolean isLeftPressed;
    public static boolean isMiddlePressed;
    public static boolean isRightPressed;
    public static double oldXpos;
    public static double oldYpos;
    public static double xpos;
    public static double ypos;
    public static int fakeRightMouse;
    public static int activeButton = -1;
    public static boolean ignoreFirstMove = true;
    public static int clickDepth;
    public static double mousePressedTime;
//    public static final MouseSmoother smoothTurnX = new MouseSmoother();
//    public static final MouseSmoother smoothTurnY = new MouseSmoother();
    public static double accumulatedDX;
    public static double accumulatedDY;
    public static double accumulatedScroll;
    public static double lastMouseEventTime = Double.MIN_VALUE;
    public static boolean mouseGrabbed = false;
    public static boolean setMousePosNextTick = false;
    private static int setXpos;
    private static int setYpos;

//    public static void test_method()
//    {
//        ifFirstPersonGrabMouse();
//    }

//    public static void ifFirstPersonGrabMouse()
//    {
//        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
//        {
//            Minecraft.getMinecraft().mouseHandler.setGrabbed(true);
//        }
//    }

    public static void endZoom()
    {
        if(IsometricCamera.HAS_ZOOMED)
        {
            if(IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE)
            {
                IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE = false;
                ModMouseHelper.releaseMouse(IsometricCamera.xScreenZoomPos, IsometricCamera.yScreenZoomPos);
                GuiOverlayManager.setAllowShowPlacedCoord(true);
            }
            GuiOverlayManager.setZoomIconVisibility(false);
            IsometricCamera.HAS_ZOOMED = false;

        }
    }

    public static void startZoomMode(boolean toCursor, boolean butZoomToCursorModeIsOn)
    {

        if(toCursor)
        {
            if(!IsometricCamera.HAS_ZOOMED || !IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE)
            {
                IsometricCamera.xScreenZoomPos = (int) xpos;
                IsometricCamera.yScreenZoomPos = (int) ypos;
                grabMouse();
                GuiOverlayManager.setZoomIconVisibility(true);

                int[] coord = CustomGuiUtils.scaleMouseCoord(IsometricCamera.xScreenZoomPos, IsometricCamera.yScreenZoomPos);
                GuiOverlayManager.setZoomIcon(coord[0],coord[1]);
                GuiOverlayManager.setAllowShowPlacedCoord(false);
                IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE = true;
            }
        }
        else if(!butZoomToCursorModeIsOn)
        {
            if(IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE)
                ModMouseHelper.endZoom();

            final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();

            int xMiddle = width/2;
            int yMiddle = height/2;
            GuiOverlayManager.setZoomIcon(xMiddle,yMiddle);
            GuiOverlayManager.setZoomIconVisibility(true);
        }
        IsometricCamera.HAS_ZOOMED = true;

    }


//    public static void setAngles(double angleX, double angleY)
//    {
//        //change the rotation of the camera
//
//        ModEntity.ANGLE_X =angleX;
//        ModEntity.ANGLE_Y =angleY;
//
//        //### the unit vector of camera look ###
//        float cameraAngle_X = ((float) ModEntity.ANGLE_X)/180*((float)Math.PI);
//        float cameraAngle_Y = -((float) ModEntity.ANGLE_Y)/180*((float)Math.PI);
//
//        float Y_mouse = ModEntityRenderer.customCameraDistance;
//
//        // incalculate the angle at which the camera is tilted
//        //first calculate an intermediate vector between XY and XZ axis
//        float Y_mouse_XZ =  Y_mouse * MathHelper.cos(cameraAngle_X);
//
//        float X_angled = (Y_mouse_XZ * MathHelper.sin(cameraAngle_Y));
//        float Y_angled  = Y_mouse * MathHelper.sin(cameraAngle_X);
//        float Z_angled = Y_mouse_XZ * MathHelper.cos(cameraAngle_Y);
//
//        IsometricCamera.CAMERA_LOOK_VECTOR = new Vector3d(-X_angled,-Y_angled,-Z_angled).normalize();
//        IsometricCamera.CAMERA_VECTOR = new Vector3d(X_angled,Y_angled,Z_angled);
////        Mousebindings.initialise = false;
//    }

    public static void grabMouse()
    {
        if(!mouseGrabbed)
        {
            Mouse.setGrabbed(true);
            mouseGrabbed = true;
        }

    }

    public static void releaseMouse(int xpos,int ypos)
    {
        if(mouseGrabbed)
        {
            Mouse.setGrabbed(false);
            setCursorPos(xpos,ypos);
            mouseGrabbed = false;
        }

    }

    public static void setCursorPos(int xpos, int ypos)
    {
        System.out.println("reset cursor to (" + setXpos + "," + setYpos + ")");
        Mouse.setCursorPosition( xpos, ypos);
    }

    public static void setMouseToPosNextTick(int xpos,int ypos)
    {
        System.out.println("set cursor to pos (" + xpos + "," + ypos + ") next tick");
        setXpos = xpos;
        setYpos = ypos;
        setMousePosNextTick = true;
    }

    public static void checkIfMouseNeedsToBeSetThisTick()
    {
        if(setMousePosNextTick)
        {
            System.out.println("start cursor repos at client tick after screen");

            setMousePosNextTick = false;
            setCursorPos(setXpos,setYpos);
            System.out.println("end cursor repos at client tick after screen");

        }
    }



    public static void test_method()
    {
        ifFirstPersonGrabMouse();
    }
    
    public static boolean ifFirstPersonGrabMouse()
    {
        if(!IsometricCamera.isPlayerInIsometricPerspective())
        {
            Mouse.setGrabbed(true);
        }
        return false;
    }
}
