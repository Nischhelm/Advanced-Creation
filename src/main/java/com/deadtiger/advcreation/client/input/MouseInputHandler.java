package com.deadtiger.advcreation.client.input;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.CopyPasteToolMode;
import com.deadtiger.advcreation.build_mode.tool_mode.MoveToolMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MouseInputHandler
{
    public static boolean wasInScreen = false;

    @SubscribeEvent
//    public void handleMouseInput(InputEvent.MouseInputEvent event)
    public void handleMouseInput(MouseEvent event)
    {

        //change the zoom of the isometric camera
//        float wheel = ((float) Mousebindings.getWheel())/50.0f;
        Keybindings key = KeyInputHandler.getPressedKey();
        if((Minecraft.getMinecraft().currentScreen == null || Mousebindings.initialiseCameraRotation) && IsometricCamera.isPlayerInIsometricPerspective())
        {
            float wheel = ((float) Mouse.getEventDWheel())/50.0f;
            boolean actionIsDone = false;
            if(Mousebindings.isMiddleButtonDown() && Keybindings.ROT_CAMERA.getKeybind().getKeyCode()  == -98 || Mousebindings.initialiseCameraRotation)
            {
                //change the rotation of the camera
                double dx = Mousebindings.getDX()*(ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED /100.0);
                double dy = Mousebindings.getDY()*(ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED /100.0);

                rotateCameraAngles(dx, dy);
                Mousebindings.initialiseCameraRotation = false;
                actionIsDone = true;
                ModMouseHelper.endZoom();
            }
            else if(Mousebindings.getDX() != 0 && Mousebindings.getDY() != 0)
                ModMouseHelper.endZoom();


            if(wasInScreen)
                wasInScreen = false;
            else if(!(wheel <= 0.0001F && wheel >= -0.0001F))
            {
                event.setCanceled(true);
                boolean executeZoom = true;
                if(wheel > 0.00F)
                {
                    if( Keybindings.ZOOM_IN.getKeybind().getKeyCode() == -101)
                    {

                    }
                    else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() == -101)
                        wheel = -wheel;
                    else
                    {
                        executeZoom = false;
                    }
                }
                else if(wheel < 0.00F)
                {
                    if( Keybindings.ZOOM_OUT.getKeybind().getKeyCode() == -102)
                    {

                    }
                    else if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() == -102)
                        wheel = -wheel;
                    else
                    {
                        executeZoom = false;
                    }
                }



                if(executeZoom)
                {
                    processScrollLikeInput(wheel);
                }

                actionIsDone = true;
            }

            if(!actionIsDone )
            {
                KeyInputHandler.checkKeybindingPressed(key);

            }


        }
        if(Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().playerController.isInCreativeMode())
            KeyInputHandler.checkUndoOrRedoKeybindingPressed(key);

        if(Keybindings.ROT_CAMERA == key)
        {
            if(!IsometricCamera.isPlayerInIsometricPerspective() && Keybindings.ROT_CAMERA.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode())
            {
                Method method = ObfuscationReflectionHelper.findMethod(Minecraft.class,"func_147112_ai",Void.TYPE);
                method.setAccessible(true);
                try
                {
                    method.invoke(Minecraft.getMinecraft(),null);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

//    @SubscribeEvent
//    public void handleMouseInput(MouseEvent event)
//    {
//        //prevent minecraft from changing the selected item with a scroll
//        if(Minecraft.getMinecraft().currentScreen == null && IsometricCamera.isPlayerInIsometricPerspective())
//        {
//            if(!(event.getDwheel() <= 0.0001F && event.getDwheel() >= -0.0001F))
//
//        }
//
//    }

    public static void rotateCameraAngles(double dx, double dy)
    {
        ConfigurationHandler.cameraConfig.add_X_angle(dy);
        ConfigurationHandler.cameraConfig.add_Y_angle(dx);

        //### the unit vector of camera look ###
        float cameraAngle_X = ((float) ConfigurationHandler.cameraConfig.X_angle)/180*((float)Math.PI);
        float cameraAngle_Y = -((float) ConfigurationHandler.cameraConfig.Y_angle)/180*((float)Math.PI);

        float Y_mouse = ModEntityRenderer.customCameraDistance;

        // incalculate the angle at which the camera is tilted
        //first calculate an intermediate vector between XY and XZ axis
        float Y_mouse_XZ =  Y_mouse * MathHelper.cos(cameraAngle_X);

        float X_angled = (Y_mouse_XZ * MathHelper.sin(cameraAngle_Y));
        float Y_angled  = Y_mouse * MathHelper.sin(cameraAngle_X);
        float Z_angled = Y_mouse_XZ * MathHelper.cos(cameraAngle_Y);

        IsometricCamera.CAMERA_LOOK_VECTOR = new Vec3d(-X_angled,-Y_angled,-Z_angled).normalize();
        IsometricCamera.CAMERA_VECTOR = new Vec3d(X_angled,Y_angled,Z_angled);
    }

    public static void processScrollLikeInput(float wheel)
    {
        if(KeyInputHandler.alterToolMode)
        {
            if(AdvCreation.getMode().equals(EnumMainMode.EDIT))
            {
                wheel = (wheel /10F);
                EditMode.addRadius(wheel);
            }
            else if((AdvCreation.getMode().equals(EnumMainMode.PLACE))||
                    ((AdvCreation.getMode().equals(EnumMainMode.BUILD) &&
                            ((BuildMode.TOOLMODE instanceof CopyPasteToolMode) || (BuildMode.TOOLMODE instanceof MoveToolMode)) && BuildMode.RIGHT_CLICK_NUMBER == 2)))
                PlaceTemplateMode.changeYTemplate(wheel);
            else if(AdvCreation.getMode().equals(EnumMainMode.BUILD))
                BuildMode.changeYBuildMode(wheel);
            else if(AdvCreation.getMode().equals(EnumMainMode.CREATE))
                BuildTemplateMode.changeYCreateMode(wheel);
        }
        else
        {
            ModEntityRenderer.cameraDistanceChange = true;
            IsometricCamera.ZOOMING = wheel;
        }
    }
}
