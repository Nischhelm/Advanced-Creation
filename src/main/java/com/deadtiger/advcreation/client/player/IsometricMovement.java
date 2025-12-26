package com.deadtiger.advcreation.client.player;

import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class IsometricMovement
{

    // ### PLAYER HEIGHT VARIABLES ###
    //Y_level to go to
    public static double TARGET_Y = -1;
    public static double TARGET_X = -1;
    public static double TARGET_Z = -1;
    // the height of the ground under the player
    public static double GROUND_Y = -1;
    //is targetY set to the ground underneither the the player
    public static boolean SET_TARGET_TO_GROUND = false;
    //is the player folloing targetY
    public static boolean FOLLOW_TARGET_Y_MODE = true;
    public static boolean FOLLOW_TARGET_Z_MODE = false;
    public static boolean FOLLOW_TARGET_X_MODE = false;


    public static float X_FRACTION_VELOCITY = 1.0f;
    public static float Y_FRACTION_VELOCITY = 1.0f;
    public static float Z_FRACTION_VELOCITY = 1.0f;

    private static PrevMovement PREV_Y_MOVE = PrevMovement.NONE;
    private static PrevMovement PREV_X_MOVE = PrevMovement.NONE;
    private static PrevMovement PREV_Z_MOVE = PrevMovement.NONE;

    public static boolean TELEPORT_ACTIVE = false;


    public static double getTargetY() {
        return TARGET_Y;
    }

    public static void setTargetY(double targetY) {
        if (targetY >= ConfigurationHandler.cameraConfig.MAX_CAMERA_HEIGHT)
            TARGET_Y = ConfigurationHandler.cameraConfig.MAX_CAMERA_HEIGHT;
        else if (targetY <= 0.0) {
            TARGET_Y = 0.0;
        } else {
            TARGET_Y = targetY;
        }

    }

    public static boolean isSetTargetToGround() {
        return SET_TARGET_TO_GROUND;
    }

    public static void setSetTargetToGround(boolean setTargetToGround) {
        SET_TARGET_TO_GROUND = setTargetToGround;
    }

    public static double getGroundY() {
        return GROUND_Y;
    }

    public static void setGroundY(double groundY) {
        IsometricMovement.GROUND_Y = groundY;
    }

    public static void teleportToClosestGround(BlockPos playerPos)
    {
        BlockPos teleportPosition = findClosestGround(playerPos);
        teleportPlayerTo(teleportPosition);
    }

    public static BlockPos findClosestGround(BlockPos playerPos)
    {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos blockPos = playerPos;
        IBlockState iblockstate1 = mc.world.getBlockState(blockPos.down());
        IBlockState iblockstate2 = mc.world.getBlockState(blockPos);
        IBlockState iblockstate3 = mc.world.getBlockState(blockPos.up());
        int countDown = 0;
        int countUp = 0;

        int max = 200;

        //keep going down while the block is made from an invalid material
        while ((!(!PlacementHelper.isNotGroundMaterial(iblockstate1) && PlacementHelper.isNotGroundMaterial(iblockstate2) && PlacementHelper.isNotGroundMaterial(iblockstate3))) && (countDown < max))
        {
            blockPos = blockPos.down();

            iblockstate1 = mc.world.getBlockState(blockPos.down());
            iblockstate2 = mc.world.getBlockState(blockPos);
            iblockstate3 = mc.world.getBlockState(blockPos.up());
            countDown++;
        }

        BlockPos blockPosUp = playerPos;
        iblockstate1 = mc.world.getBlockState(blockPosUp.down());
        iblockstate2 = mc.world.getBlockState(blockPosUp);
        iblockstate3 = mc.world.getBlockState(blockPosUp.up());

        //keep going up while the block is made from an invalid material
        while ((!(!PlacementHelper.isNotGroundMaterial(iblockstate1) && PlacementHelper.isNotGroundMaterial(iblockstate2) && PlacementHelper.isNotGroundMaterial(iblockstate3))) && (countUp < max))
        {
            blockPosUp = blockPosUp.up();

            iblockstate1 = mc.world.getBlockState(blockPosUp.down());
            iblockstate2 = mc.world.getBlockState(blockPosUp);
            iblockstate3 = mc.world.getBlockState(blockPosUp.up());
            countUp++;
        }
        boolean teleportUp = countDown < countUp;

        BlockPos teleportPosition;
        if (teleportUp)
            teleportPosition = new BlockPos(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        else
            teleportPosition = new BlockPos(blockPosUp.getX() + 0.5, blockPosUp.getY() + 1.0 + 0.5, blockPosUp.getZ() + 0.5);
        return teleportPosition;
    }

    public static void teleportPlayerTo(BlockPos teleportPosition)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.player.attemptTeleport(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
    }

    public static void updateMovement(EntityPlayerSP player)
    {
        //### ALWAYS FLY ###
        forceFlying(player);
        //###  never sprint ###
//        TODO: I'm not sure if this is the right replacement for sprintToggleTimer
        player.sprintToggleTimer = 7;
        player.setSprinting(false);

        //if the sprint key is pressed I will unpress it
        if(Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(),false);

        //### INITIALISE THE CAMERA LEVEL SLIDER ###
        initialiseCameraLevelGuiOverlayIndicator(player);

        //### adjust player movement speed ###
        // stop the player as soon as you are finished moving
        Minecraft mc = Minecraft.getMinecraft();
        if (!ModEntityRenderer.cameraDistanceChange)
        {
            if (    !isSetTargetToGround() &&
                    !mc.gameSettings.keyBindJump.isKeyDown() &&
                    !mc.gameSettings.keyBindSneak.isKeyDown())
                movePlayerVertical(player);

            if (!mc.gameSettings.keyBindLeft.isKeyDown() &&
                    !mc.gameSettings.keyBindRight.isKeyDown() &&
                    !mc.gameSettings.keyBindForward.isKeyDown() &&
                    !mc.gameSettings.keyBindBack.isKeyDown())
                movePlayerHorizontal(player);
        }

        //### disable the on ground boolean  ###
        player.onGround = false;

        //### PROCESS JUMP AND SNEAK ###
        // check if player is jumping and replace it with a new way of jumping

        double jumpSneakVelocity = 0.5;
        if (player.movementInput.jump)
            handleJumpInput(player, jumpSneakVelocity);

        if (player.movementInput.sneak)
            handleSneakInput(player, jumpSneakVelocity);
        GuiOverlayManager.setCameraOverlayCurrYSlideTab(TARGET_Y);

        //### PROCESS MANUAL HORIZONTAL MOVE ###
        // check if player is moving in de horizontal plane manually is he is disable auto-move
        if (player.movementInput.backKeyDown || player.movementInput.forwardKeyDown ||
                player.movementInput.leftKeyDown || player.movementInput.rightKeyDown)
        {
            FOLLOW_TARGET_X_MODE = false;
            FOLLOW_TARGET_Z_MODE = false;
            ModMouseHelper.endZoom();
        }

        //### SEARCH HIGHEST BLOCK ABOVE PLAYER ###
        //is the player going up to a targetY
        boolean blockAbovePlayerFound = findHighestBlockAbovePlayer(player);
        //### SEARCH HIGHEST BLOCK UNDER THE PLAYER
        if (!blockAbovePlayerFound)
            findHighestBlockBelowPlayer(player);

        //### APPLY GROUND COORD TO TARGET_Y ###
        if (SET_TARGET_TO_GROUND)
            TARGET_Y = GROUND_Y;

        double base_velocity = 0.42;
        double X_velocity = base_velocity;
        double Y_velocity = base_velocity;
        double Z_velocity = base_velocity;

        if(TELEPORT_ACTIVE)
        {
            player.setPositionAndUpdate(player.posX,getTargetY() , player.posZ);
            TELEPORT_ACTIVE = false;
            return;
        }
//        if(FREE_TELEPORT_ACTIVE)
//        {
//            if(TELEPORT_TARGET != null)
//                NetworkHandler.sendTeleportRequestToServer(new MessageTeleportRequestFromClient(TELEPORT_TARGET));
//            FREE_TELEPORT_ACTIVE = false;
//            return;
//        }


        float oldDist = ModEntityRenderer.customCameraDistance;
        float newDist = ModEntityRenderer.newCustomCameraDistance;
//        float zoom_velocity = (float)(1.5 * Math.pow(ConfigurationHandler.ZOOM_SPEED.get(),0.30));
//        float zoom_velocity = 1.0f;//(float)(1.25 * Math.pow(ConfigurationHandler.ZOOM_SPEED.get(),0.30));
//        float zoom_velocity = (float)(1.5 *Math.log10(ConfigurationHandler.ZOOM_SPEED.get()));
        float zoom_velocity =  (float) (1.5 * Math.log10(ConfigurationHandler.cameraConfig.ZOOM_SPEED));

//        System.out.println("newDist " +newDist + " oldDist " + oldDist);
        if (newDist - zoom_velocity > oldDist)
        {
            ModEntityRenderer.customCameraDistance += zoom_velocity;
        }
        else if (newDist + zoom_velocity < oldDist)
        {
//            IsometricCamera.ZOOMING =  IsometricCamera.ZOOMING ;
            ModEntityRenderer.customCameraDistance -= zoom_velocity;
//            if(ConfigurationHandler.ZOOM_TOWARDS_CURSOR.get())
//            {
//                IsometricCamera.ZOOMING -= zoom_velocity/IsometricCamera.TOTAL_ZOOMING;
//            }


        }
        else
        {
            ModEntityRenderer.customCameraDistance = newDist;
//            if(!ModEntityRenderer.cameraDistanceChange)
//                IsometricCamera.ZOOMING = 0;
        }

        if (IsometricMovement.FOLLOW_TARGET_X_MODE && IsometricMovement.FOLLOW_TARGET_Z_MODE)
        {
            base_velocity = 1.0 ;
//            base_velocity = zoom_velocity ;
//            base_velocity = 0.9;// + zoom_velocity/4;
            X_velocity = base_velocity * X_FRACTION_VELOCITY;
            Y_velocity = base_velocity * Y_FRACTION_VELOCITY;
            Z_velocity = base_velocity * Z_FRACTION_VELOCITY;
        }

        //### MOVE PLAYER TO TARGET_Y ###
        //Move the player to the set targetY if followTargetYMode is true and
        //if there is a valid height target or a target different from the players position
        if (IsometricMovement.TARGET_Y >= 0 &&
                (IsometricMovement.TARGET_Y < (player.posY - 0.1) || IsometricMovement.TARGET_Y > (player.posY + 0.1)) &&
                IsometricMovement.FOLLOW_TARGET_Y_MODE)
        {
            MoveToTargetY(player, Y_velocity);
        }

        if ((IsometricMovement.TARGET_X < (player.posX - 0.1) || IsometricMovement.TARGET_X > (player.posX + 0.1)) &&
                IsometricMovement.FOLLOW_TARGET_X_MODE)
        {
            MoveToTargetX(player, X_velocity);
        }

        if ((IsometricMovement.TARGET_Z < (player.posZ - 0.1) || IsometricMovement.TARGET_Z > (player.posZ + 0.1)) &&
                IsometricMovement.FOLLOW_TARGET_Z_MODE)
        {
            MoveToTargetZ(player, Z_velocity);
        }
//        System.out.println("player motion: (" + player.motionX+ "," + player.motionY + "," + player.motionZ + ")");

    }


//    public static void updateMovement(EntityPlayerSP player)
//    {
//
//
//        //### ALWAYS FLY ###
//        forceFlying(player);
//        //###  never sprint ###
//        player.sprintToggleTimer = 7;
//        player.setSprinting(false);
//        //if the sprint key is pressed I will unpress it
//        if(Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
//            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(),false);
//
//        //### INITIALISE THE CAMERA LEVEL SLIDER ###
//        initialiseCameraLevelGuiOverlayIndicator(player);
//
//        //### adjust player movement speed ###
//        // stop the player as soon as you are finished moving
//        Minecraft mc = Minecraft.getMinecraft();
//        if (!ModEntityRenderer.cameraDistanceChange)
//        {
//            if (!isSetTargetToGround() &&
//                    !mc.gameSettings.keyBindJump.isKeyDown() &&
//                    !mc.gameSettings.keyBindSneak.isKeyDown())
//                movePlayerVertical(player);
//
//            if (!mc.gameSettings.keyBindLeft.isKeyDown() &&
//                    !mc.gameSettings.keyBindRight.isKeyDown() &&
//                    !mc.gameSettings.keyBindForward.isKeyDown() &&
//                    !mc.gameSettings.keyBindBack.isKeyDown())
//                movePlayerHorizontal(player);
//        }
//
//        //### disable the on ground boolean  ###
//        player.onGround = false;
//
//        //### PROCESS JUMP AND SNEAK ###
//        // check if player is jumping and replace it with a new way of jumping
//
//        double jumpSneakVelocity = 0.5;
//        if (player.movementInput.jump)
//            handleJumpInput(player, jumpSneakVelocity);
//
//        if (player.movementInput.sneak)
//            handleSneakInput(player, jumpSneakVelocity);
//
//        GuiOverlayManager.setCameraOverlayCurrYSlideTab(TARGET_Y);
//
//        //### PROCESS MANUAL HORIZONTAL MOVE ###
//        // check if player is moving in de horizontal plane manually is he is disable auto-move
//        if (player.movementInput.backKeyDown || player.movementInput.forwardKeyDown ||
//                player.movementInput.leftKeyDown || player.movementInput.rightKeyDown)
//        {
//            FOLLOW_TARGET_X_MODE = false;
//            FOLLOW_TARGET_Z_MODE = false;
//            ModMouseHelper.endZoom();
//        }
//
//        //### SEARCH HIGHEST BLOCK ABOVE PLAYER ###
//        //is the player going up to a targetY
//        boolean blockAbovePlayerFound = findHighestBlockAbovePlayer(player);
//        //### SEARCH HIGHEST BLOCK UNDER THE PLAYER
//        if (!blockAbovePlayerFound)
//            findHighestBlockBelowPlayer(player);
//
//        //### APPLY GROUND COORD TO TARGET_Y ###
//        if (SET_TARGET_TO_GROUND)
//            TARGET_Y = GROUND_Y;
//        if(!TELEPORT_ACTIVE)
//        {
//
//            double base_velocity = 0.42;
//            double X_velocity = base_velocity;
//            double Y_velocity = base_velocity;
//            double Z_velocity = base_velocity;
//
//
//            float oldDist = ModEntityRenderer.customCameraDistance;
//            float newDist = ModEntityRenderer.newCustomCameraDistance;
//            float zoom_velocity = (float) (1.5 * Math.log10(ConfigurationHandler.cameraConfig.ZOOM_SPEED));
//            if (IsometricMovement.FOLLOW_TARGET_X_MODE && IsometricMovement.FOLLOW_TARGET_Z_MODE)
//            {
//                base_velocity = zoom_velocity;
//                X_velocity = base_velocity * X_FRACTION_VELOCITY;
//                Y_velocity = base_velocity * Y_FRACTION_VELOCITY;
//                Z_velocity = base_velocity * Z_FRACTION_VELOCITY;
//            }
//            if (newDist - zoom_velocity > oldDist)
//            {
////                System.out.println("zoom_velocity " + zoom_velocity);
//                ModEntityRenderer.customCameraDistance += zoom_velocity;
//
//            }
//            else if (newDist + zoom_velocity < oldDist)
//            {
////                System.out.println("zoom_velocity " + zoom_velocity);
//                ModEntityRenderer.customCameraDistance -= zoom_velocity;
//            }
//            else
//            {
//                ModEntityRenderer.customCameraDistance = newDist;
//            }
//
//            //### MOVE PLAYER TO TARGET_Y ###
//            //Move the player to the set targetY if followTargetYMode is true and
//            //if there is a valid height target or a target different from the players position
//            if (IsometricMovement.TARGET_Y >= 0 &&
//                    (IsometricMovement.TARGET_Y < (player.posY - 0.1) || IsometricMovement.TARGET_Y > (player.posY + 0.1)) &&
//                    IsometricMovement.FOLLOW_TARGET_Y_MODE)
//            {
//                MoveToTargetY(player, Y_velocity);
//            }
//
//            if ((IsometricMovement.TARGET_X < (player.posX - 0.1) || IsometricMovement.TARGET_X > (player.posX + 0.1)) &&
//                    IsometricMovement.FOLLOW_TARGET_X_MODE)
//            {
//                MoveToTargetX(player, X_velocity);
//            }
//
//            if ((IsometricMovement.TARGET_Z < (player.posZ - 0.1) || IsometricMovement.TARGET_Z > (player.posZ + 0.1)) &&
//                    IsometricMovement.FOLLOW_TARGET_Z_MODE)
//            {
//                MoveToTargetZ(player, Z_velocity);
//            }
//        }
//        else
//        {
//            player.setPositionAndUpdate(player.posX,getTargetY() , player.posZ);
//            TELEPORT_ACTIVE = false;
//        }
//
//
//
//    }

    private static void handleJumpInput(EntityPlayerSP player, double jumpSneakVelocity)
    {
        player.setJumping(false);
        if (SET_TARGET_TO_GROUND)
            SET_TARGET_TO_GROUND = false;
        if (FOLLOW_TARGET_Y_MODE)
            TARGET_Y += jumpSneakVelocity;
        ModMouseHelper.endZoom();
    }

    private static void handleSneakInput(EntityPlayerSP player, double jumpSneakVelocity)
    {
        if (SET_TARGET_TO_GROUND)
            SET_TARGET_TO_GROUND = false;
        player.setSneaking(false);
        if (FOLLOW_TARGET_Y_MODE)
            TARGET_Y -= jumpSneakVelocity;
        ModMouseHelper.endZoom();
    }

    private static void findHighestBlockBelowPlayer(EntityPlayerSP player)
    {
        BlockPos blockpos1;
        IBlockState iblockstate2;
        Vec3d vec3d;

        //check if there is a block below the player, if not move the player to the first block under the player
        vec3d = new Vec3d(player.posX, Math.ceil(player.getEntityBoundingBox().minY), player.posZ);// lowest point of the player entity box
        blockpos1 = (new BlockPos(vec3d)).down();
        iblockstate2 = player.world.getBlockState(blockpos1);

        if(blockpos1.getY() <= 0)
            GROUND_Y = 0.5;

        while (PlacementHelper.isNotGroundMaterial(iblockstate2) && (blockpos1.getY()> 0))
        {
            blockpos1 = blockpos1.down();
            iblockstate2 = player.world.getBlockState(blockpos1);

            //if the next block under the player is lower than the minimum_block_diff then the player goes to it
            int minimumBlockDiff = 5;
            if ((!PlacementHelper.isNotGroundMaterial(iblockstate2) &&
                    player.posY - minimumBlockDiff > blockpos1.getY()) || GROUND_Y <= 0)
            {
                GROUND_Y = blockpos1.getY() + 0.5;
            }
        }
    }

    private static boolean findHighestBlockAbovePlayer(EntityPlayerSP player)
    {
        boolean playerGoesUp = false;
        Vec3d vec3d = new Vec3d(player.posX, ConfigurationHandler.cameraConfig.MAX_CAMERA_HEIGHT, player.posZ);// lowest point of the player entity box
        BlockPos blockpos1 = (new BlockPos(vec3d)).down();
        IBlockState iblockstate2 = player.world.getBlockState(blockpos1);

        //keep going up while the block is made from an invalid material
        while (PlacementHelper.isNotGroundMaterial(iblockstate2))
        {
            blockpos1 = blockpos1.down();
            iblockstate2 = player.world.getBlockState(blockpos1);

            // when going below the player stop going down
            if (blockpos1.getY() <= (int) player.getEntityBoundingBox().minY)
            {
                break;
            }

            if (!PlacementHelper.isNotGroundMaterial(iblockstate2))
            {
                GROUND_Y = blockpos1.getY() + 0.5;
                playerGoesUp = true;
            }
        }
        return playerGoesUp;
    }

    private static void movePlayerHorizontal(EntityPlayerSP player)
    {
        if (player.motionX + player.motionZ > 0.05f)
        {
            float decelerate = 2.0F;
            Vec3d newMovement = new Vec3d(player.motionX / decelerate,0.0,player.motionZ / decelerate);
            player.motionX = newMovement.x;
            player.motionZ = newMovement.z;
//            System.out.println("new movement " +  newMovement);
//            player.motionX = player.motionX / 1.5f;
//            player.motionZ = player.motionZ / 1.5f;
        }
        else
        {
            player.motionZ = 0.0;
            player.motionX = 0.0;
        }
    }

    private static void movePlayerVertical(EntityPlayerSP player)
    {
        if (player.motionY > 0.05f)
        {
            player.motionY = player.motionY / 1.5f;
        }
        else
        {
            player.motionY = 0.0;
        }
    }

    private static void initialiseCameraLevelGuiOverlayIndicator(EntityPlayerSP player)
    {
        if (TARGET_Y < 0)
        {
            setTargetY(player.posY);
        }
        if (GuiOverlayManager.getCameraOverlayCurrYSlideTab() <= 0)
        {
            GuiOverlayManager.setCameraOverlayCurrYSlideTab(TARGET_Y);
        }
    }

    private static void forceFlying(EntityPlayerSP player)
    {
        if (!player.capabilities.isFlying)
        {
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }
    }

    public static void MoveToTargetY(EntityPlayerSP player, double base_velocity)
    {
        //standard the player goes down to the target
        double start_scaling = 0.8;
        double motionY = -base_velocity;
        double exp_base = 10.0;
        double scaling = 2.0;

        //previous scaling was 2.0 and start scaling was 0.8
        //if the player is above the target go up to the target
        if (TARGET_Y > player.posY && (PREV_Y_MOVE != PrevMovement.DECREASE))
        {
            //### MOVE TO TARGET ABOVE PLAYER
            PREV_Y_MOVE = PrevMovement.INCREASE;
            motionY = base_velocity;
            // if the player is nearly at the target set motion to 0.0
            if ((TARGET_Y - 0.2) <= player.posY)
            {
                motionY = 0.0;
                //will avoid overshoot by setting a new targetY when overschoot occurs
                setTargetY(player.posY);
                PREV_Y_MOVE = PrevMovement.NONE;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_Y - start_scaling) < player.posY)
            {
                double x = player.posY - TARGET_Y; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);
                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionY = base_velocity - deduction;
            }
        }
        else if ((PREV_Y_MOVE != PrevMovement.INCREASE))
        {
            //### MOVE TO TARGET UNDER PLAYER
            // if the player is nearly at the target set motion to 0.0
            PREV_Y_MOVE = PrevMovement.DECREASE;
            if ((TARGET_Y + 0.2) >= player.posY)
            {
                motionY = 0.0;
                //will avoid overshoot by setting a new targetY when overschoot occurs
                setTargetY(player.posY);
                PREV_Y_MOVE = PrevMovement.NONE;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_Y + start_scaling) > player.posY)
            {
                double x = TARGET_Y - player.posY; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);
                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionY = -(base_velocity - deduction);
            }
        }
        else
        {
            motionY = 0.0;
            setTargetY(player.posY);
            PREV_Y_MOVE = PrevMovement.NONE;
        }

        player.motionY = motionY;
    }

    public static void MoveToTargetX(EntityPlayerSP player, double base_velocity)
    {
        //standard the player goes down to the target
        double start_scaling = 0.8;
        double motionX = -base_velocity;
        double exp_base = 10.0;
        double scaling = 2.0;



        //previous scaling was 2.0 and start scaling was 0.8
        //if the player is above the target go up to the target
        if (TARGET_X > player.posX && (PREV_X_MOVE != PrevMovement.DECREASE))
        {
            //### MOVE TO TARGET ABOVE PLAYER
            PREV_X_MOVE = PrevMovement.INCREASE;
            motionX = base_velocity;
            // if the player is nearly at the target set motion to 0.0
            if ((TARGET_X - 0.2) <= player.posX)
            {
                motionX = 0.0;
                TARGET_X = player.posX;
                PREV_X_MOVE = PrevMovement.NONE;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_X - start_scaling) < player.posX)
            {
                double x = player.posX - TARGET_X; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);

                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionX = base_velocity - deduction;
            }
        }
        else if ((PREV_X_MOVE != PrevMovement.INCREASE))
        {
            //### MOVE TO TARGET UNDER PLAYER
            // if the player is nearly at the target set motion to 0.0
            PREV_X_MOVE = PrevMovement.DECREASE;
            if ((TARGET_X + 0.2) >= player.posX)
            {
                motionX = 0.0;
                TARGET_X = player.posX;
                PREV_X_MOVE = PrevMovement.NONE;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_X + start_scaling) > player.posX)
            {
                double x = TARGET_X - player.posX; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);
                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionX = -(base_velocity - deduction);
            }
        }
        else
        {
            motionX = 0.0;
            TARGET_X = player.posX;
            PREV_X_MOVE = PrevMovement.NONE;
        }

        player.motionX = motionX;
    }

    public static void MoveToTargetZ(EntityPlayerSP player, double base_velocity)
    {
        //standard the player goes down to the target
        double start_scaling = 0.8;
        double motionZ = -base_velocity;
        double exp_base = 10.0;
        double scaling = 2.0;

        //previous scaling was 2.0 and start scaling was 0.8
        //if the player is above the target go up to the target
        if (TARGET_Z > player.posZ && PREV_Z_MOVE != PrevMovement.DECREASE)
        {
            //### MOVE TO TARGET ABOVE PLAYER
            PREV_Z_MOVE = PrevMovement.INCREASE;
            motionZ = base_velocity;
            // if the player is nearly at the target set motion to 0.0
            if ((TARGET_Z - 0.2) <= player.posZ)
            {
                motionZ = 0.0;
                PREV_Z_MOVE = PrevMovement.NONE;
                TARGET_Z = player.posZ;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_Z - start_scaling) < player.posZ)
            {
                double x = player.posZ - TARGET_Z; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);
                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionZ = base_velocity - deduction;
            }
        }
        else if ((PREV_Z_MOVE != PrevMovement.INCREASE))
        {
            //### MOVE TO TARGET UNDER PLAYER
            // if the player is nearly at the target set motion to 0.0
            PREV_Z_MOVE = PrevMovement.DECREASE;
            if ((TARGET_Z + 0.2) >= player.posZ)
            {
                motionZ = 0.0;
                PREV_Z_MOVE = PrevMovement.NONE;
                TARGET_Z = player.posZ;
            }
            //if the player is less than a block away from the target scale the motion based ont
            else if ((TARGET_Z + start_scaling) > player.posZ)
            {
                double x = TARGET_Z - player.posZ; // should be between -0.95 and 0.05
                double new_x = x / (start_scaling + 0.2);
                double deduction = Math.pow(exp_base, (-scaling - (new_x * scaling)));
                if(deduction < 0 || deduction > base_velocity)
                    deduction = 0;
                motionZ = -(base_velocity - deduction);
            }
        }
        else
        {
            motionZ = 0.0;
            TARGET_Z = player.posZ;
            PREV_Z_MOVE = PrevMovement.NONE;
        }

        player.motionZ = motionZ;
    }

    public enum PrevMovement
    {INCREASE, DECREASE, NONE}

    public static void teleportTo(double newTargetY)
    {
        setTargetY(newTargetY);
        TELEPORT_ACTIVE = true;
    }
}
