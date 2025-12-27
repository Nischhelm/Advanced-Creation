package com.deadtiger.advcreation.client.player;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.client.event.ClientEventHandler;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.debug.DebugInfo;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.message.MessageUpdatePlayerSetting;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.client.GuiIngameForge;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IsometricCamera
{

    public static Vec3d CAMERA_LOOK_VECTOR = Vec3d.ZERO;
    public static Vec3d CAMERA_VECTOR = Vec3d.ZERO;
    public static Vec3d CAMERA_FOCUS_POINT = Vec3d.ZERO;
    public static Vec3d FOCUS_POINT_TO_SCREEN_MIDDLE = Vec3d.ZERO;

    public static float ZOOMING = 0.0f;
    public static Vec3d CURR_ZOOM_IN_VEC = null;
    public static long LAST_TIME = 0;
    public static long DELAY = 300;

    public static boolean HAS_ZOOMED = false;
    public static double INTIAL_TOTAL_MOVE_DISTANCE = 0.0;
    public static boolean REPOSITION_ZOOM_ICON_ACTIVE = false;
    public static float TOTAL_ZOOMING = 0.0f;
    public static int xScreenZoomPos = 0;
    public static int yScreenZoomPos = 0;

    public static boolean IGNORE_PLANTS = true;
    public static boolean IGNORE_FLUIDS = true;

    public static int CUSTOM_THIRD_PERSON_VIEW_SETTING = 0;
    public static int CUSTOM_MAX_THIRD_PERSON_VIEW_VALUE = 3;
    public static int PREV_CUSTOM_THIRD_PERSON_VIEW_SETTING = 0;

    public static boolean ENTERED_CREATIVE = false;
    public static boolean ENTERED_SURVIVAL = false;
    public static boolean CHANGED_PERSPECTIVE = false;
    public static int newThirdPersonViewValue = 0;
    public static int previousNormalView = 0;

    public static void updateModifiedFov(float fov)
    {
        if ((int) ModEntity.prevModifiedFov == (int) fov)
        {
            ModEntity.currentModifiedFov = fov;
        }
        ModEntity.prevModifiedFov = fov;
    }


//    public static void executeCameraZoom(RayTraceResult objectMouseOver, EntityPlayer entityplayer)
//    {
//        double scaling = Math.log10(ConfigurationHandler.cameraConfig.ZOOM_SPEED);
//        if ( ConfigurationHandler.cameraConfig.ZOOM_TOWARDS_CURSOR &&  (  ZOOMING > 0.0f && (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)) )
//        {
//
//            //only change the block you are zooming to after a delay, so that fast zooming goes to the same point
//            getDelayedZoomInVec(objectMouseOver);
//
//            Vec3d newPos = CURR_ZOOM_IN_VEC;
//            Vec3d refMoveVec = new Vec3d(newPos.x - entityplayer.posX, newPos.y - entityplayer.posY, newPos.z - entityplayer.posZ);
//
//            //Not really the right way to do it but it seems to work
//            Vec3d extraDistance = new Vec3d(CAMERA_LOOK_VECTOR.x * refMoveVec.x, CAMERA_LOOK_VECTOR.y * refMoveVec.y, CAMERA_LOOK_VECTOR.z * refMoveVec.z);
//
//            //calculate vector direction
//            int dir = 1;
//            if (extraDistance.x < 0
//                    || extraDistance.y < 0
//                    || extraDistance.z < 0)
//                dir = -1;
//
//
//            float extraDistanceLength = (float) (dir * extraDistance.length());
//
//            double newDistance = extraDistanceLength + ModEntityRenderer.customCameraDistance;
//            double scaledZoom = ZOOMING* scaling;
//            double zoomDiff = scaledZoom/ (scaledZoom + Math.abs(newDistance));
//
//            // speed of the movement needs to correspond with the vector
//            Vec3d unitDiffVec = refMoveVec.normalize();
//            float max_direction = (float) Math.max(Math.abs(unitDiffVec.x), Math.abs(unitDiffVec.y));
//            max_direction = (float) Math.max(max_direction, Math.abs(unitDiffVec.z));
//            IsometricMovement.X_FRACTION_VELOCITY = (float) Math.abs(unitDiffVec.x) / max_direction;
//            IsometricMovement.Y_FRACTION_VELOCITY = (float) Math.abs(unitDiffVec.y) / max_direction;
//            IsometricMovement.Z_FRACTION_VELOCITY = (float) Math.abs(unitDiffVec.z) / max_direction;
//
//            float viewDistance = extraDistanceLength;
//
//            if (zoomDiff < 0.98)
//            {
//                Vec3d scaledVec = refMoveVec.scale(zoomDiff);
//                viewDistance = (float) (extraDistanceLength * zoomDiff);
//                newPos = entityplayer.getPositionVector().add(scaledVec);
//            }
//
//            IsometricMovement.TARGET_X = newPos.x;
//            IsometricMovement.FOLLOW_TARGET_X_MODE = true;
//            IsometricMovement.TARGET_Z = newPos.z;
//            IsometricMovement.FOLLOW_TARGET_Z_MODE = true;
//
//            if (!IsometricMovement.isSetTargetToGround())
//                IsometricMovement.setTargetY(newPos.y);
//
//            ModEntityRenderer.newCustomCameraDistance += -scaledZoom + viewDistance;
//
//        }
//        else
//        {
//            double scaledZoom = ZOOMING* scaling;;
//            ModEntityRenderer.newCustomCameraDistance -= scaledZoom;
//        }
//
//        // if new camera distance exceeds min or max difference set to min or max distance
//        if (ModEntityRenderer.newCustomCameraDistance < ModEntityRenderer.customCameraDistance_min)
//            ModEntityRenderer.newCustomCameraDistance = ModEntityRenderer.customCameraDistance_min;
//        else if (ModEntityRenderer.newCustomCameraDistance > ModEntityRenderer.customCameraDistance_max)
//            ModEntityRenderer.newCustomCameraDistance = ModEntityRenderer.customCameraDistance_max;
//
//        ModEntityRenderer.cameraDistanceChange = false;
//    }

    public static void executeCameraZoom(RayTraceResult objectMouseOver, EntityPlayer entityplayer, float partialTicks, boolean towardsCursor)
    {
        double scaling = 1.5f*Math.log10(ConfigurationHandler.cameraConfig.ZOOM_SPEED);
        if (towardsCursor &&  (ZOOMING > 0.0f))
        {

            //only change the block you are zooming to after a delay, so that fast zooming goes to the same point
            if(!IsometricCamera.HAS_ZOOMED || !IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE )
                getDelayedZoomInVec(objectMouseOver);

            Vec3d  focuspointToCamera = IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE;
            Vec3d  playerPos = entityplayer.getPositionEyes(ClientEventHandler.renderTickTime).add(focuspointToCamera.normalize().scale(ModEntityRenderer.customCameraDistance_min + 3));
            Vec3d  newPos = CURR_ZOOM_IN_VEC.add(focuspointToCamera.normalize().scale(ModEntityRenderer.customCameraDistance_min + 3 +0.5));
            Vec3d  refMoveVec = new Vec3d (newPos.x - playerPos.x, newPos.y -  playerPos.y, newPos.z -  playerPos.z);

            //if the distance between the current focuspoint and the current zoom in point is less then 0.5 block don't bother moving the camera focus point
            if(refMoveVec.length() > 0.5)
            {
                double scaledZoom = ZOOMING* scaling;

                Vec3d  zoomVector =  ModEntity.currMouseVec.normalize().scale(scaledZoom);
                Vec3d  cameraLookNorm = IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE.normalize().scale(-1.0);
                Vec3d  refMoveNorm = refMoveVec.normalize();

//                double moveDistance = Math.abs(zoomVector.dot(refMoveNorm));
//                double zoomDistance = Math.abs(zoomVector.dot(cameraLookNorm));
                double pointDistance = newPos.distanceTo(entityplayer.getPositionEyes(ClientEventHandler.renderTickTime).add(IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE));

                //move distance as scaled verison of the ratio between distance from camera to zoompoint and distance from camera focus point to zoom point
                double relativeMoveDistance = (refMoveVec.length()*(scaledZoom/pointDistance))*1.5;
                //move distance as fraction of the distance from camera focus point to zoom point
                if(!HAS_ZOOMED)
                    INTIAL_TOTAL_MOVE_DISTANCE = refMoveVec.length();
                double partialMoveDistance = INTIAL_TOTAL_MOVE_DISTANCE/5;
                //take which ever one is the fastest
                double moveDistance = relativeMoveDistance;
                if(partialMoveDistance > relativeMoveDistance)
                {
//                    DebugInfo.debugPrint("1. partialMoveDistance is higher, partial: " + String.format("%,.4f",partialMoveDistance) + " relative: " + String.format("%,.4f",relativeMoveDistance) );
                    moveDistance = partialMoveDistance;
                    //to minimize overshoot for when the partialMoveDistance is way too fast for the distance that needs to be traveled
                    if(partialMoveDistance*1.5 > refMoveVec.length())
                    {
                        INTIAL_TOTAL_MOVE_DISTANCE = INTIAL_TOTAL_MOVE_DISTANCE/2;
                        moveDistance = relativeMoveDistance*(INTIAL_TOTAL_MOVE_DISTANCE/refMoveVec.length());
                    }

                }
//                else
//                    DebugInfo.debugPrint("1. relativeMoveDistance is higher, partial: " + String.format("%,.4f",partialMoveDistance) + " relative: " + String.format("%,.4f",relativeMoveDistance) );
                double zoomDistance = (playerPos.distanceTo(entityplayer.getPositionEyes(ClientEventHandler.renderTickTime).add(IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE))) *(scaledZoom/pointDistance);


                Vec3d  moveVector = refMoveNorm.scale(moveDistance);
                float max = (float) Math.max(Math.abs(moveVector.x), Math.abs(moveVector.y));
                max = (float) Math.max(max, Math.abs(moveVector.z));
                if(max < 0.8)
                {
//                    DebugInfo.debugPrint("2. increase moveVector to have some movement original " + moveVector);
                    moveVector = moveVector.scale(0.8/max);
//                    DebugInfo.debugPrint("2.5. new moveVector " + moveVector);
                }

                //if the zoom position you are zooming too is closer to the playerPos then the calculated moveVector directly move to the zoom pos
                if(refMoveVec.length() > moveVector.length())
                    newPos = entityplayer.getPositionVector().add(moveVector);
                else
                {
//                    DebugInfo.debugPrint("3. moveVector.lenght is longer then refMoveVec.lenght() " + newPos);
                    newPos = CURR_ZOOM_IN_VEC.add(focuspointToCamera.normalize().scale(1)).add(0,-entityplayer.getEyeHeight(),0);
//                    DebugInfo.debugPrint("3.5 newPos is changed to " + newPos);
                }


//                Vec3d  currPlayerPos = entityplayer.getPosition(ClientEventHandler.renderTickTime);
//                String moveVectorString = String.format("(%,.4f, %,.4f, %,.4f)",moveVector.x,moveVector.y,moveVector.z);
//                String playerPosString = String.format("(%,.4f, %,.4f, %,.4f)",currPlayerPos.x,currPlayerPos.y,currPlayerPos.z);
//                String newPosString = String.format("(%,.4f, %,.4f, %,.4f)",newPos.x,newPos.y,newPos.z);

//                System.out.println("moveVector " + moveVectorString + " lenght "+  String.format(" %,.4f ",moveVector.length()) +"oldpos "+ playerPosString + " newpos " + newPosString);

//                if(BuildMode.FILL_MODE == EnumFillMode.FILL && true)
//                {
//                    DebugInfo.cameraVectorToMiddleScreen =IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE;
//                    DebugInfo.playerPos = Minecraft.getMinecraft().player.getPositionVector();
//                    DebugInfo.hitPosition = new Vec3d (CURR _ZOOM_IN_VEC.x,CURR_ZOOM_IN_VEC.y,CURR_ZOOM_IN_VEC.z);
//                    DebugInfo.cursorVector = new CursorVector( ModEntity.currCursorVec);
//                    DebugInfo.cameraFocusPoint = Minecraft.getMinecraft().player.getPositionEyes(partialTicks);
//                    DebugInfo.cameraVectorToCursorOnScreenAtZoom = DebugInfo.cameraVectorToCursorOnScreen;
//
//
//                    DebugInfo.drawVectors.clear();
//                    DebugInfo.drawVectors.add(new CursorVector(entityplayer.getPositionVector(),newPos));
//
//                    Vec3d  middleScreen = DebugInfo.cameraFocusPoint.add(DebugInfo.cameraVectorToMiddleScreen);
//                    Vec3d  cameraZoom = cameraLookNorm.scale(Math.abs(zoomDistance));
//                    DebugInfo.drawVectors.add(new CursorVector(middleScreen,middleScreen.add(zoomVector)));
//                    DebugInfo.drawVectors.add(new CursorVector(middleScreen.add(cameraZoom),middleScreen.add(cameraZoom).add(refMoveNorm.scale(moveDistance))));
//                    DebugInfo.drawVectors.add(new CursorVector(middleScreen,middleScreen.add(cameraZoom)));
//                    DebugInfo.drawVectors.add(new CursorVector(CURR_ZOOM_IN_VEC,CURR_ZOOM_IN_VEC.add(focuspointToCamera.normalize().scale(ModEntityRenderer.customCameraDistance_min + 3 +0.5))));
//                    DebugInfo.drawVectors.add(new CursorVector(CURR_ZOOM_IN_VEC,CURR_ZOOM_IN_VEC.add(focuspointToCamera.normalize().scale(1))));
//                    DebugInfo.drawVectors.add(new CursorVector(DebugInfo.cameraFocusPoint,DebugInfo.cameraFocusPoint.add(refMoveNorm.scale(moveDistance))));
//                }

                //apply the calculated moveVector
                IsometricMovement.TARGET_X = newPos.x;
                IsometricMovement.FOLLOW_TARGET_X_MODE = true;
                IsometricMovement.TARGET_Z = newPos.z;
                IsometricMovement.FOLLOW_TARGET_Z_MODE = true;
                if (!IsometricMovement.isSetTargetToGround())
                    IsometricMovement.setTargetY(newPos.y);

                ModMouseHelper.startZoomMode(true, true);

                ModEntityRenderer.newCustomCameraDistance -= zoomDistance;
                IsometricCamera.TOTAL_ZOOMING = ModEntityRenderer.customCameraDistance - ModEntityRenderer.newCustomCameraDistance;

//                // speed of the movement needs to correspond with the vector
//                Vec3d  unitDiffVec = refMoveNorm;
////                double zoom_velocity = (float)(Math.log10(ConfigurationHandler.ZOOM_SPEED.get()));
//                DebugInfo.debugPrint("moveVector.length() " + moveVector.length() + " zoomDistance " + zoomDistance + " zoom_velocity " + scaling);
//                double move_scale = moveVector.length()/(zoomDistance/scaling);
//                if(zoomDistance <= 1.0 )
//                    move_scale = moveVector.length()/5.0;
//
//                float max_direction = (float) Math.max(Math.abs(unitDiffVec.x), Math.abs(unitDiffVec.y));
//                max_direction = (float) Math.max(max_direction, Math.abs(unitDiffVec.z));
//
//                IsometricMovement.X_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.x) / max_direction)*move_scale);
//                IsometricMovement.Y_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.y) / max_direction)*move_scale);
//                IsometricMovement.Z_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.z) / max_direction)*move_scale);


                // speed of the movement needs to correspond with the vector


                double stepNbr = (zoomDistance/scaling)*4 ;
                if(stepNbr < 1.0)
                    stepNbr = 4.0;
//                DebugInfo.debugPrint("4. moveVector.length() " + moveVector.length() + " zoomDistance " + zoomDistance + " zoom_velocity " + scaling + " stepSize " + stepNbr);
                Vec3d  unitDiffVec = refMoveVec.scale(1.0/stepNbr);

                float max_direction = (float) Math.max(Math.abs(unitDiffVec.x), Math.abs(unitDiffVec.y));
                max_direction = (float) Math.max(max_direction, Math.abs(unitDiffVec.z));
                if(max_direction < 1.0f)
                    max_direction = 1.1f;
                else
                    max_direction = max_direction*1.1f;

                IsometricMovement.X_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.x) / max_direction));
                IsometricMovement.Y_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.y) / max_direction));
                IsometricMovement.Z_FRACTION_VELOCITY = (float) ((Math.abs(unitDiffVec.z) / max_direction));

//                IsometricMovement.X_FRACTION_VELOCITY = (float) (Math.abs(unitDiffVec.x));
//                IsometricMovement.Y_FRACTION_VELOCITY = (float) (Math.abs(unitDiffVec.y));
//                IsometricMovement.Z_FRACTION_VELOCITY = (float) (Math.abs(unitDiffVec.z));

                String fractionalVelocity = String.format("(%,.4f, %,.4f, %,.4f)",IsometricMovement.X_FRACTION_VELOCITY,IsometricMovement.Y_FRACTION_VELOCITY,IsometricMovement.Z_FRACTION_VELOCITY);
//                DebugInfo.debugPrint("5. relMoveVec.length " + refMoveVec.length() + " fractionalVelocity " + fractionalVelocity + " unitDiffVec.length " + unitDiffVec.length());
            }
            else
            {
                double scaledZoom = ZOOMING* scaling;
                ModMouseHelper.startZoomMode(false, true);
                ModEntityRenderer.newCustomCameraDistance -= scaledZoom;
            }
        }
        else
        {
            double scaledZoom = ZOOMING* scaling;;
            ModMouseHelper.startZoomMode(false, false);
            ModEntityRenderer.newCustomCameraDistance -= scaledZoom;
        }

        // if new camera distance exceeds min or max difference set to min or max distance
        if (ModEntityRenderer.newCustomCameraDistance < ModEntityRenderer.customCameraDistance_min)
            ModEntityRenderer.newCustomCameraDistance = ModEntityRenderer.customCameraDistance_min;
        else if (ModEntityRenderer.newCustomCameraDistance > ModEntityRenderer.customCameraDistance_max)
            ModEntityRenderer.newCustomCameraDistance = ModEntityRenderer.customCameraDistance_max;

        ModEntityRenderer.cameraDistanceChange = false;
    }

    public static void cancelZoom()
    {
        IsometricMovement.FOLLOW_TARGET_X_MODE = false;
        IsometricMovement.FOLLOW_TARGET_Z_MODE = false;
        ModEntityRenderer.newCustomCameraDistance = ModEntityRenderer.customCameraDistance;
        ModMouseHelper.endZoom();
    }

    private static void getDelayedZoomInVec(RayTraceResult objectMouseOver)
    {
        long currTime = System.currentTimeMillis();
        long timeDiff = currTime - LAST_TIME;
        LAST_TIME = currTime;
        DELAY = DELAY - timeDiff;
        if (CURR_ZOOM_IN_VEC == null || 0 > DELAY)
        {
            CURR_ZOOM_IN_VEC = objectMouseOver.hitVec;
            DELAY = 300;
        }
    }

    public static EnumDirectionMode calcAutoDirectionMode() {
        EnumDirectionMode newDir = null;
        Vec3d camLook = CAMERA_LOOK_VECTOR;
        double[] camCoords = {Math.abs(camLook.x),Math.abs(camLook.y),Math.abs(camLook.z)};
        List b = Arrays.asList(ArrayUtils.toObject(camCoords));
        int maxIndex = b.indexOf(Collections.max(b));

        if(maxIndex == 0)//means looking in the X direction
            newDir = EnumDirectionMode.ZY;
        else if(maxIndex == 1)//means looking in the Y direction
            newDir = EnumDirectionMode.XZ;
        else if(maxIndex == 2)//means looking in the Z direction
            newDir = EnumDirectionMode.XY;

        return newDir;
    }

    public static boolean isPlayerInIsometricPerspective()
    {
        return isPlayerInIsometricPerspective(CUSTOM_THIRD_PERSON_VIEW_SETTING);
    }

    public static boolean isPlayerInIsometricPerspective(int thirdPersonViewSettings) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(minecraft.playerController != null && minecraft.playerController.getCurrentGameType() != null)
        {
            if(minecraft.playerController.getCurrentGameType().equals(GameType.CREATIVE))
                return thirdPersonViewSettings > 2;
        }
        return false;
    }

    public static int getThirdPersonViewSetting()
    {
        Minecraft minecraft = Minecraft.getMinecraft();

        int thirdPersonView =  minecraft.gameSettings.thirdPersonView;
        if(minecraft.playerController != null && minecraft.playerController.getCurrentGameType() != null &&(minecraft.playerController.getCurrentGameType().equals(GameType.CREATIVE)) )
            thirdPersonView = IsometricCamera.CUSTOM_THIRD_PERSON_VIEW_SETTING;

        return thirdPersonView;
    }

    public static void setThirdPersonViewSetting(int thirdPersonView)
    {
        setThirdPersonViewSetting(thirdPersonView,false);
    }

    public static void setThirdPersonViewSetting(int thirdPersonView,boolean compensateForF5Press)
    {

        Minecraft minecraft = Minecraft.getMinecraft();
        if(minecraft.playerController != null)
        {
            if(minecraft.playerController.isInCreativeMode())
            {
                if(thirdPersonView > CUSTOM_MAX_THIRD_PERSON_VIEW_VALUE)
                    thirdPersonView = 0;

                //anything above 2  is to high for the regular thirdPersonView variable so we keep it at 1 to keep it from going crazy when necessary
                if(thirdPersonView > 2)
                    Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
                else
                    Minecraft.getMinecraft().gameSettings.thirdPersonView = thirdPersonView;

            }
            else
            {
                if(thirdPersonView > 2)
                    thirdPersonView = 0;

                minecraft.gameSettings.thirdPersonView = thirdPersonView;
            }
            IsometricCamera.CUSTOM_THIRD_PERSON_VIEW_SETTING = thirdPersonView;


            //update the gui to the new setting and
            thirdPersonView = updateGuiToThirdPersonView(thirdPersonView,IsometricCamera.PREV_CUSTOM_THIRD_PERSON_VIEW_SETTING);
            boolean playerCharacterIsVisible = !isPlayerInIsometricPerspective(thirdPersonView);
            NetworkHandler.sendPlayerSettingsUpdateToServer(new MessageUpdatePlayerSetting(playerCharacterIsVisible,!ConfigurationHandler.general.TOOLS_ENABLED, Minecraft.getMinecraft().player.getName()));

            //if you change the perspective in the keyInput event for F5 button then the game will increase the thirdPersonView value
            //after you are done so to accommodate this, the value is decreased by 1
            if(minecraft.gameSettings.thirdPersonView > 0 && compensateForF5Press)
                minecraft.gameSettings.thirdPersonView -= 1;

            IsometricCamera.PREV_CUSTOM_THIRD_PERSON_VIEW_SETTING = IsometricCamera.CUSTOM_THIRD_PERSON_VIEW_SETTING;
        }
    }




    public static boolean isLeafRaytracingDisabled() {
        return IGNORE_PLANTS;
    }

    public static void handlePerspectiveChange()
    {
        //is triggered right before the actual key is handled so
        // when going out of thirdpersonview you grab the mouse otherise you don't
//        if (Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.isKeyDown())
//        {
            int thirdPersonView = 0;
            if(Minecraft.getMinecraft().playerController.isInCreativeMode() )
                thirdPersonView = CUSTOM_THIRD_PERSON_VIEW_SETTING + 1;
            else
                thirdPersonView = Minecraft.getMinecraft().gameSettings.thirdPersonView + 1;

            setThirdPersonViewSetting(thirdPersonView,false);
//        }
    }

    public static int updateGuiToThirdPersonView(int thirdPersonView, int prevThirdPersonView)
    {
        if (isPlayerInIsometricPerspective(thirdPersonView))
        {
            GuiIngameForge.renderHealth = false;
            GuiIngameForge.renderExperiance = false;
            GuiIngameForge.renderFood = false;
            GuiIngameForge.renderJumpBar = false;
            GuiOverlayManager.setGuiOverlayVisible(true);
            EntityPlayer player = Minecraft.getMinecraft().player;
            IsometricMovement.setTargetY(player.getPositionVector().y);
            player.setNoGravity(true);

            GuiIngameForge.renderHotbar = AdvCreation.mode != EnumMainMode.PLACE;

            if (!player.capabilities.isFlying)
            {
                player.capabilities.isFlying = true;
                player.sendPlayerAbilities();
            }

            Mouse.setGrabbed(false);
        }
        else if(prevThirdPersonView > 2)
        {
            //if you were previously in the isometric perspective you need to change some gui proporties and others
            GuiIngameForge.renderHealth = true;
            GuiIngameForge.renderExperiance = true;
            GuiIngameForge.renderFood = true;
            GuiIngameForge.renderJumpBar = true;
            GuiIngameForge.renderHotbar = true;
            GuiOverlayManager.setGuiOverlayVisible(false);

            Mouse.setGrabbed(true);
            //turn off the cuttThrough mode
            ModBlockRendererDispatcher.cuttThroughOn = false;

            EntityPlayer player = Minecraft.getMinecraft().player;
            IsometricMovement.setTargetY(player.getPositionVector().y);
            player.setNoGravity(false);
            player.capabilities.isFlying = false;

            //teleport the player to the closed ground level
            BlockPos playerPos = player.getPosition();
            IsometricMovement.teleportToClosestGround(playerPos);

        }
        return thirdPersonView;
    }

    public static void renderZoomLocationHighlighting( EntityPlayer playerEntity, float partialticks)
    {
        if(IsometricCamera.HAS_ZOOMED && IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE)
            RenderSelectionHighlight.drawSelectionBlockOutline(playerEntity,new BlockPos(IsometricCamera.CURR_ZOOM_IN_VEC),partialticks,0f,0f,1f);

    }

    public static void repositionMouseToZoomLocation(Minecraft mc,Vec3d  hitPosition)
    {
        //calculate a new position for the cursor
        float cameraAngle_X = ((float) ConfigurationHandler.cameraConfig.X_angle)/180*((float)Math.PI);
        float cameraAngle_Y = -((float) ConfigurationHandler.cameraConfig.Y_angle)/180*((float)Math.PI);

        //calculate surface to adjust mouse position after zoom
        //rotate plane arround the X-axis
        Vec3d  leftup = new Vec3d (-1,1* MathHelper.cos(cameraAngle_X),-1*MathHelper.sin(cameraAngle_X));
        Vec3d  rightdown = new Vec3d (1,-1* MathHelper.cos(cameraAngle_X),1*MathHelper.sin(cameraAngle_X));
        Vec3d  rightup = new Vec3d (1,1* MathHelper.cos(cameraAngle_X),-1*MathHelper.sin(cameraAngle_X));

        //rotate plane around the Y-axis
        leftup = new Vec3d (leftup.x*MathHelper.cos(cameraAngle_Y)+leftup.z*MathHelper.sin(cameraAngle_Y),leftup.y,leftup.z*MathHelper.cos(cameraAngle_Y)-leftup.x*MathHelper.sin(cameraAngle_Y));
        rightdown = new Vec3d ( rightdown.x*MathHelper.cos(cameraAngle_Y)+rightdown.z*MathHelper.sin(cameraAngle_Y),rightdown.y,rightdown.z*MathHelper.cos(cameraAngle_Y)-rightdown.x*MathHelper.sin(cameraAngle_Y));
        rightup = new Vec3d (rightup.x*MathHelper.cos(cameraAngle_Y)+rightup.z*MathHelper.sin(cameraAngle_Y),rightup.y,rightup.z*MathHelper.cos(cameraAngle_Y)-rightup.x*MathHelper.sin(cameraAngle_Y));

        Vec3d  up = leftup.add(rightup).normalize();
        Vec3d  right = rightdown.add(rightup).normalize();

        Vec3d  normCameraVector = FOCUS_POINT_TO_SCREEN_MIDDLE.normalize();;
        Vec3d  cameraFocusPoint = CAMERA_FOCUS_POINT;

        double newZtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(normCameraVector);
        double newYtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(up);
        double newXtoHitpoint = hitPosition.subtract(cameraFocusPoint).dotProduct(right);

        double reverseCalcViewAngleY = (Math.atan(newYtoHitpoint/(newZtoHitpoint- ModEntityRenderer.customCameraDistance)));
        double reverseCalcViewAngleX = -(Math.atan(newXtoHitpoint/(newZtoHitpoint- ModEntityRenderer.customCameraDistance)));

        // calculate the pixel distance of the camera camera focus point of the screen lens
//        double fov = (ModEntity.currentModifiedFov/180.0)*Math.PI;
        int half_screen_width = mc.displayWidth/2; // mc.screen.getWidth()/2;
        int half_screen_height = mc.displayHeight/2; //mc.getWindow().getHeight()/2;
        //XZ distance of the camera focus point in pixel
        //half of the fov angle
        double fov = 0.0;
        float modifiedFov  = ModEntity.currentModifiedFov; //dirty way of keeping the modifiedFov up to date
        if(modifiedFov< 0.0F) {
            fov = (mc.gameSettings.fovSetting/180.0)*Math.PI;
        }
        else {
            fov = (modifiedFov/180.0)*Math.PI;
        }
        //XZ distance of the camera focus point in pixel
        int camera_focus_point_height = ((int) (half_screen_height/Math.tan(fov/2.0)));
//        int camera_focus_point_height = ((int) ((half_screen_height/Math.tan(fov/2.0))*ModEntity.distanceMultiplierFocusPoint));

        int screen_X_mouse_to_center =  (int) (camera_focus_point_height*Math.tan(reverseCalcViewAngleX));
        int screen_Z_mouse_to_center =  (int) (camera_focus_point_height*Math.tan(reverseCalcViewAngleY));
        int screen_X_mouse = (int) half_screen_width + screen_X_mouse_to_center;
        int screen_Z_mouse = (int) half_screen_height + screen_Z_mouse_to_center;

        DebugInfo.calc_screen_z = screen_Z_mouse;
        DebugInfo.calc_screen_x = screen_X_mouse;
//
        DebugInfo.calc_viewAngle_X = reverseCalcViewAngleX;
        DebugInfo.calc_viewAngle_Z = reverseCalcViewAngleY;
        int[] coord = CustomGuiUtils.scaleMouseCoord(xScreenZoomPos,yScreenZoomPos);

        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        IsometricCamera.xScreenZoomPos = screen_X_mouse;

        int recalculatedYScreen = mc.displayHeight - screen_Z_mouse;
        IsometricCamera.yScreenZoomPos = recalculatedYScreen;

        int recalculatedY = coord[1];


        //prevent to mouse from going out of screen
        if( coord[0] < 0 || coord[0] > width || recalculatedY  < 0 || recalculatedY  > height)
            return;

//        System.out.println("X,Y angle (" + ConfigurationHandler.cameraConfig.Y_angle + ","  + ConfigurationHandler.cameraConfig.X_angle + ")");
//        System.out.println("screen_mouse (" + xScreenZoomPos + "," + recalculatedYScreen +") = (" + (((float)xScreenZoomPos)/ mc.displayWidth) +"," + (((float)recalculatedYScreen)/mc.displayHeight) + "), coord (" + coord[0] + "," +recalculatedY  + ") = (" + ((float)coord[0])/width + "," + ((float)recalculatedY)/height +")" );

        GuiOverlayManager.setZoomIcon(coord[0],recalculatedY );
//
        Mouse.setCursorPosition(screen_X_mouse,  recalculatedYScreen); //GLFW.glfwSetCursorPos(Minecraft.getMinecraft().getWindow().getWindow(), (double) screen_X_mouse, (double) screen_Z_mouse);
    }

    public static void checkIfNeedForCompensateForPressF5()
    {
        //is triggered right before the actual key is handled so
        // when going out of thirdpersonview you grab the mouse otherise you don't
        if (Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.isKeyDown())
        {
            if(isPlayerInIsometricPerspective())
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
        }
    }
}
