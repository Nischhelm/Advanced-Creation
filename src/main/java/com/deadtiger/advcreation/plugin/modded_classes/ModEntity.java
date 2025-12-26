package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.player.IsometricMovement;
import com.deadtiger.advcreation.debug.DebugInfo;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.utility.CursorVector;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.util.*;

public class ModEntity
{
    // for when the view is isometric
//    private static final float strafeAngle = -135.0F;
//    private static final float forwardAngle = -135.0F;

    // for when the view is top down
    private static final float strafeAngle = -180.0F ;
    private static final float forwardAngle = -180.0F;
    public static float distanceMultiplierFocusPoint = 0.883f;

    // static variable to keep the previous selected position in
    private static BlockPos PrevSelectedBlockPos;
    
    // Variable that is update to reflect the modified fov in vanilla minecraft (changes when flying)
    public static float prevModifiedFov = 1.0F;
    public static float currentModifiedFov = 1.0F;
    
    public static Vec3d currMouseVec = Vec3d.ZERO;
    public static CursorVector currCursorVec = new CursorVector();
    public static Vec3d currPointedBlockVec = Vec3d.ZERO;
    public static boolean newPointedBlockVec = true;
    
    //player speed
    public static float standardSpeed =  3.0f;
    
    //test variables
    private Entity entity = null;

    public static float distanceMultiplierX = 7000000f;
    public static float distanceMultiplierY = 400000f;
    
//        public void test_method(float strafe,float up, float forward, float friction)
//    {
//        customMoveRelative(strafe,up,forward,friction,this.entity);
//
//    }
    
    public void test_method_2(float partialTicks)
    {
        double d0 = 0.0;
        Entity entity_local = null;
        rayTrace(entity_local,d0,partialTicks);
    }
    @Deprecated
    public static void customMoveRelative(float strafe, float up, float forward, float friction, Entity entity)
    {
//        System.out.println("Thank you for trying to move relative to the isometric perspective");
        float movement_scale = 0.0f;
        float f = strafe * strafe + up * up + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;

            float f1 = MathHelper.sin(entity.rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(entity.rotationYaw * 0.017453292F);


            if (entity instanceof EntityPlayer &&
                    IsometricCamera.isPlayerInIsometricPerspective() &&
                    ConfigurationHandler.general.MOVE_RELATIVE_TO_CAMERA)
            {
                float forwardAngle_new = forwardAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);
                float strafeAngle_new = strafeAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);

                f1 = MathHelper.sin(forwardAngle_new * 0.017453292F);
                f2 = MathHelper.cos(strafeAngle_new * 0.017453292F);

                // increase the movement speed as the player is higher above groundlevel
                if(IsometricCamera.CAMERA_LOOK_VECTOR != null)
                {
                    float player_height_above_ground =(float) Math.max(0,(entity.posY - IsometricMovement.getGroundY())/20);
                    movement_scale = (float) (Math.abs(IsometricCamera.CAMERA_LOOK_VECTOR.y*ModEntityRenderer.customCameraDistance)/20 + player_height_above_ground);
                }
                entity.motionX += (double)(strafe * f2 - forward * f1)*(standardSpeed + movement_scale);
                entity.motionY += (double)up*(standardSpeed + movement_scale);
                entity.motionZ += (double)(forward * f2 + strafe * f1)*(standardSpeed + movement_scale);
//                System.out.println("motion " + Math.sqrt(Math.pow(entity.motionX,2) + Math.pow(entity.motionZ,2)) + " move_scale " + movement_scale);
            }
            else
            {
                entity.motionX += (double)(strafe * f2 - forward * f1)*(1.0f);
                entity.motionY += (double)up*(1.0f);
                entity.motionZ += (double)(forward * f2 + strafe * f1)*(1.0f);
            }
        }
    }

    public static void reducedCustomMoveRelative(float strafe, float up, float forward, float friction, Entity entity)
    {
//        System.out.println("Thank you for trying to move relative to the isometric perspective");
        float movement_scale = 0.0f;
        float f = strafe * strafe + up * up + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;

            float forwardAngle_new = forwardAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);
            float strafeAngle_new = strafeAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);

            float f1 = MathHelper.sin(forwardAngle_new * 0.017453292F);
            float f2 = MathHelper.cos(strafeAngle_new * 0.017453292F);

            // increase the movement speed as the player is higher above groundlevel
            if(IsometricCamera.CAMERA_LOOK_VECTOR != null)
            {
                float player_height_above_ground =(float) Math.max(0,(entity.posY - IsometricMovement.getGroundY())/20);
                movement_scale = (float) (Math.abs(IsometricCamera.CAMERA_LOOK_VECTOR.y*ModEntityRenderer.customCameraDistance)/20 + player_height_above_ground);
            }
            entity.motionX += (double)(strafe * f2 - forward * f1)*(standardSpeed + movement_scale);
            entity.motionY += (double)up*(standardSpeed + movement_scale);
            entity.motionZ += (double)(forward * f2 + strafe * f1)*(standardSpeed + movement_scale);
//                System.out.println("motion " + Math.sqrt(Math.pow(entity.motionX,2) + Math.pow(entity.motionZ,2)) + " move_scale " + movement_scale);

        }
    }

    public static void calcMotionX()
    {
        System.out.println("arguments of calcMotionX strafe: ");
    }

    public static double calcMotionX(float strafe, float up, float forward, float friction)
    {
        System.out.println("arguments of calcMotionX strafe: " + strafe + " up: " + up + " forward: " + forward + " friction: " + friction);
        return 0.0;
    }

    public static double calcMotionX(float strafe, float up, float forward, float friction,float rotationYaw, double entityPosY, boolean isEntityPlayer)
    {
//        System.out.println("arguments of calcMotionX strafe: " + strafe + " up: " + up + " forward: " + forward + " friction: " + friction + " rotationYaw: " + rotationYaw + " entityPosY: " + entityPosY + " isEntityPlayer: " + isEntityPlayer );
        float movement_scale = 0.0f;
        float f = strafe * strafe + up * up + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;

            float f1 = MathHelper.sin(rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(rotationYaw * 0.017453292F);


            if (isEntityPlayer &&
                    IsometricCamera.isPlayerInIsometricPerspective() &&
                    ConfigurationHandler.general.MOVE_RELATIVE_TO_CAMERA)
            {
                float forwardAngle_new = forwardAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);
                float strafeAngle_new = strafeAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);

                f1 = MathHelper.sin(forwardAngle_new * 0.017453292F);
                f2 = MathHelper.cos(strafeAngle_new * 0.017453292F);

                // increase the movement speed as the player is higher above groundlevel
                if(IsometricCamera.CAMERA_LOOK_VECTOR != null)
                {
                    float player_height_above_ground =(float) Math.max(0,(entityPosY - IsometricMovement.getGroundY())/20);
                    movement_scale = (float) (Math.abs(IsometricCamera.CAMERA_LOOK_VECTOR.y*ModEntityRenderer.customCameraDistance)/20 + player_height_above_ground);
                }
                return (double)(strafe * f2 - forward * f1)*(standardSpeed + movement_scale);
            }

                return (double)(strafe * f2 - forward * f1)*(1.0f);

        }
        return 0.0;

    }

    public static double calcMotionY(float strafe, float up, float forward, float friction,float rotationYaw, double entityPosY, boolean isEntityPlayer)
    {
        float movement_scale = 0.0f;
        float f = strafe * strafe + up * up + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;

            float f1 = MathHelper.sin(rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(rotationYaw * 0.017453292F);


            if (isEntityPlayer &&
                    IsometricCamera.isPlayerInIsometricPerspective() &&
                    ConfigurationHandler.general.MOVE_RELATIVE_TO_CAMERA)
            {
                float forwardAngle_new = forwardAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);
                float strafeAngle_new = strafeAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);

                f1 = MathHelper.sin(forwardAngle_new * 0.017453292F);
                f2 = MathHelper.cos(strafeAngle_new * 0.017453292F);

                // increase the movement speed as the player is higher above groundlevel
                if(IsometricCamera.CAMERA_LOOK_VECTOR != null)
                {
                    float player_height_above_ground =(float) Math.max(0,(entityPosY - IsometricMovement.getGroundY())/20);
                    movement_scale = (float) (Math.abs(IsometricCamera.CAMERA_LOOK_VECTOR.y*ModEntityRenderer.customCameraDistance)/20 + player_height_above_ground);
                }
                return (double)up*(standardSpeed + movement_scale);
            }
            return (double)up*(1.0f);

        }
        return 0.0;

    }

    public static double calcMotionZ(float strafe, float up, float forward, float friction,float rotationYaw, double entityPosY, boolean isEntityPlayer)
    {
        float movement_scale = 0.0f;
        float f = strafe * strafe + up * up + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;

            float f1 = MathHelper.sin(rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(rotationYaw * 0.017453292F);


            if (isEntityPlayer &&
                    IsometricCamera.isPlayerInIsometricPerspective() &&
                    ConfigurationHandler.general.MOVE_RELATIVE_TO_CAMERA)
            {
                float forwardAngle_new = forwardAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);
                float strafeAngle_new = strafeAngle + ((float) ConfigurationHandler.cameraConfig.Y_angle);

                f1 = MathHelper.sin(forwardAngle_new * 0.017453292F);
                f2 = MathHelper.cos(strafeAngle_new * 0.017453292F);

                // increase the movement speed as the player is higher above groundlevel
                if(IsometricCamera.CAMERA_LOOK_VECTOR != null)
                {
                    float player_height_above_ground =(float) Math.max(0,(entityPosY - IsometricMovement.getGroundY())/20);
                    movement_scale = (float) (Math.abs(IsometricCamera.CAMERA_LOOK_VECTOR.y*ModEntityRenderer.customCameraDistance)/20 + player_height_above_ground);
                }
                return (double)(forward * f2 + strafe * f1)*(standardSpeed + movement_scale);
            }

            return (double)(forward * f2 + strafe * f1)*(1.0f);

        }
        return 0.0;

    }


    public static RayTraceResult rayTrace(Entity entity,double blockReachDistance, float partialTicks)
    {
        return rayTrace(entity,blockReachDistance,partialTicks,-1.0F);
    }
    
    //a method used instead of Entity.raytrace in EntityRenderer.getMouseOver(...)
    public static RayTraceResult rayTrace(Entity entity,double blockReachDistance, float partialTicks,float modifiedFov)
    {
        //normal code used in firstperson perspective
        Vec3d vec3d = entity.getPositionEyes(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        
        // the if checks for if the game is paused which is only for debug purposes
        Minecraft mc = Minecraft.getMinecraft();
        // custom raytrace when in thirdperson mode
        if (entity instanceof EntityPlayer && IsometricCamera.isPlayerInIsometricPerspective() && mc.isGamePaused() == false)
        {
            CursorVector res = calculateVectorFromCursor(entity,partialTicks,mc,modifiedFov);
            currCursorVec = res;
            vec3d = res.start;
            vec3d2 = res.end;

            //        RayTraceResult result = entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
//        RayTraceResult result = ModWorld.rayTraceBlocks(vec3d, vec3d2, false, false, true,entity.world);
            RayTraceResult result = ModWorld.rayTraceBlocks(vec3d, vec3d2, !IsometricCamera.IGNORE_FLUIDS, false, true,entity.world);

            if(result != null && result.hitVec != null)
            {
                RayTraceResult entityResult = ModWorld.raytraceEntities(entity,vec3d,result.hitVec,entity.world);
               //if the entity is closer then the block choose that!
                if(entityResult != null)
                {
                    double blockDist = vec3d.distanceTo(result.hitVec);
                    double entityDist = vec3d.distanceTo(entityResult.hitVec);
                    if(blockDist > entityDist)
                        result = entityResult;
                }
            }
            else
                result = ModWorld.raytraceEntities(entity,vec3d,vec3d2,entity.world);;



            if(result == null)
            {
                vec3d = entity.getPositionEyes(partialTicks);
                vec3d1 = entity.getLook(partialTicks);
                vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
                //result =  entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
//            result = ModWorld.rayTraceBlocks(vec3d, vec3d2, false, false, true,entity.world);
                result = ModWorld.rayTraceBlocks(vec3d, vec3d2, !IsometricCamera.IGNORE_FLUIDS, false, true,entity.world);
                System.out.println("Bad raytrace from isometric perspective");

            }
            if(result == null)
                result = new RayTraceResult(RayTraceResult.Type.MISS,new Vec3d(0,0,0),EnumFacing.NORTH,new BlockPos(0,0,0));

            if(result.typeOfHit == RayTraceResult.Type.MISS)
            {
                BlockPos pos = result.getBlockPos();
                EnumFacing face = result.sideHit;
                Block hitBlock = mc.world.getBlockState(pos).getBlock();

                //gives back the position next to the hit block not the block itselft
                BlockPos position = pos.add(face.getDirectionVec().getX(),face.getDirectionVec().getY(),face.getDirectionVec().getZ());
                // But when you hit a Vine of Grass it does give back the actual hit position
                if(hitBlock instanceof BlockVine || hitBlock instanceof BlockTallGrass)
                {
                    position = pos;
                }

                if(PrevSelectedBlockPos == null)
                {
                    PrevSelectedBlockPos = position;
                }

                // put the current selected position into the previous variable
                PrevSelectedBlockPos = position;
            }


            return result;


        }
        else
        {
            return entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
        }
    }

    //a method used to raytrace both blocks and entities and returns only the block or entities hitvec
    public static Vec3d rayTrace_everything(Entity entity,double blockReachDistance, float partialTicks,float modifiedFov)
    {
        //get the block under the cursor
        Vec3d hitVecBlock = rayTrace(entity,blockReachDistance,partialTicks,modifiedFov).hitVec;
        
        //try to find if there is a living entity or item under the cursor give that priority
        Minecraft mc = Minecraft.getMinecraft();
        CursorVector cursorVector = calculateVectorFromCursor(entity,partialTicks,mc,modifiedFov);
        
        
        float d0 = ModPlayerControllerMP.getCustomReachDistance();
        Vec3d lookDirVec = entity.getLook(1.0F);
        Vec3d positionEyesVec = entity.getPositionEyes(partialTicks);
        
        //result parameters
        Vec3d resultVec3d3 = hitVecBlock;
        Entity pointedEntity;
        
        //get the distance of the selected block to cursor
        double d1 = d0;
        if (hitVecBlock != null)
        {
            d1 = hitVecBlock.distanceTo(cursorVector.start);
        }
        
        //get all entities in the vision of the player
        List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(lookDirVec.x * d0, lookDirVec.y * d0, lookDirVec.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
            }
        }));
        //this is the distance from the cursor to the block or the maximum distance
        double distBlock = d1;
        
        for (int j = 0; j < list.size(); ++j)
        {
            
            Entity entity1 = list.get(j);
            //get the boundingbox of the entity
            AxisAlignedBB entityCollisionBox = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
            //calculate if the entity's boundingbox intercedes with the cursor vector
            RayTraceResult raytraceresult = entityCollisionBox.calculateIntercept(cursorVector.start, cursorVector.end);
        
            //if the players eyes are in the hitbox of the entity the distance to a block needs to be more then 0
            //otherwise something invalid must be happening
            if (entityCollisionBox.contains(positionEyesVec))
            {
                
                if (distBlock >= 0.0D)
                {
                    pointedEntity = entity1;
                    resultVec3d3 = raytraceresult == null ? positionEyesVec : raytraceresult.hitVec;
                    distBlock = 0.0D;
                }
            }
            else if (raytraceresult != null)
            {
                // distance between cursor and entity
                double distEntity = cursorVector.start.distanceTo(raytraceresult.hitVec);
                
                // checks if the entity is between block and cursor
                if (distEntity < distBlock || distBlock == 0.0D)
                {
                    if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
                    {
                        if (distBlock == 0.0D)
                        {
                            pointedEntity = entity1;
                            resultVec3d3 = raytraceresult.hitVec;
                        }
                    }
                    else
                    {
                        pointedEntity = entity1;
                        resultVec3d3 = raytraceresult.hitVec;
                        distBlock = distEntity;
                    }
                }
            }
        }
        
        return resultVec3d3;
    }

    
    private static CursorVector calculateVectorFromCursor(Entity entity,float partialTicks, Minecraft mc)
    {
        return calculateVectorFromCursor(entity,partialTicks, mc,-1.0F);
    }
    
    private static CursorVector calculateVectorFromCursor(Entity entity,float partialTicks, Minecraft mc,float modifiedFov)
    {
        //christiaan: different vector calculations to find the block that the cursor is pointing too
        // vec3d =  the vector coordinates of the postion of the cursor calculated relative to the position of the eyes
        //size of the screen in pixels
        int half_screen_width = mc.displayWidth/2;
        int half_screen_height = mc.displayHeight/2;
    
        //position of the mouse on screen in pixels
        int screen_X_mouse = -(half_screen_width - Mouse.getX());
        int screen_Z_mouse = half_screen_height - Mouse.getY();

        DebugInfo.screen_x = (int) ModMouseHelper.xpos;
        DebugInfo.screen_z = (int) ModMouseHelper.ypos;

        //half of the fov angle
        double fov = 0.0;
        modifiedFov  = currentModifiedFov; //dirty way of keeping the modifiedFov up to date
        if(modifiedFov< 0.0F) {
             fov = (mc.gameSettings.fovSetting/180.0)*Math.PI;
        }
        else {
             fov = (modifiedFov/180.0)*Math.PI;
        }
        //XZ distance of the camera focus point in pixel
        int camera_focus_point_height = ((int) (half_screen_height/Math.tan(fov/2.0)));
    
        //calculate angle from pixel coordinates
        double view_angle_X = MathHelper.atan2((double)screen_X_mouse,(double) camera_focus_point_height);
        double view_angle_Z = MathHelper.atan2((double)screen_Z_mouse,(double) camera_focus_point_height);
    
        //float cursor_vector_Y = 200.0f;
        float cursor_vector_Y = 500.0f;
        //float cursor_vector_Y = 16.85f;
        float cursor_vector_X = 0.0f;
        float cursor_vector_Z = 0.0f;
    
        //calculate the fov vector with block coordinates
        cursor_vector_Y = -cursor_vector_Y;
        cursor_vector_X = (float)(Math.tan(view_angle_X)*cursor_vector_Y);
        cursor_vector_Z = (float)(Math.tan(view_angle_Z)*cursor_vector_Y);
    
        float cameraAngle_X = ((float) ConfigurationHandler.cameraConfig.X_angle)/180*((float)Math.PI);
        float cameraAngle_Y = -((float) ConfigurationHandler.cameraConfig.Y_angle)/180*((float)Math.PI);
    
        //### the vector from  focus point through (cursor on screen) to ground ###
        float cursor_vector_Y_mouse_XZ =  cursor_vector_Y * MathHelper.cos(cameraAngle_X);
        float cursor_vector_Z_mouse_XZ =  cursor_vector_Z * MathHelper.sin(cameraAngle_X);
    
    
        float cursor_vectorAngled_Y = cursor_vector_Y * MathHelper.sin(cameraAngle_X) + cursor_vector_Z * MathHelper.cos(cameraAngle_X);
        float cursor_vectorAngled_X = (cursor_vector_Y_mouse_XZ * MathHelper.sin(cameraAngle_Y) - cursor_vector_X * MathHelper.cos(cameraAngle_Y) - cursor_vector_Z_mouse_XZ * MathHelper.sin(cameraAngle_Y));
        float cursor_vectorAngled_Z = cursor_vector_Y_mouse_XZ  * MathHelper.cos(cameraAngle_Y) + cursor_vector_X  * MathHelper.sin(cameraAngle_Y) - cursor_vector_Z_mouse_XZ * MathHelper.cos(cameraAngle_Y);
        
        //### the vector from eyes of player character to mouse on camera ###
    
        float X_mouse = ((float)screen_X_mouse) / ((20000f)/960.0f*half_screen_width);
        float Y_mouse = ModEntityRenderer.customCameraDistance;
        float Z_mouse = ((float)screen_Z_mouse) / ((20000f)/508.0f*half_screen_height);
    
        // incalculate the angle at which the camera is tilted
        //first calculate an intermediate vector between XY and XZ axis
        float Y_mouse_XZ =  Y_mouse * MathHelper.cos(cameraAngle_X);
        float Z_mouse_XZ = Z_mouse * MathHelper.sin(cameraAngle_X);
    
    
    
        float X_mouseAngled = (Y_mouse_XZ * MathHelper.sin(cameraAngle_Y) - X_mouse * MathHelper.cos(cameraAngle_Y) + Z_mouse_XZ * MathHelper.sin(cameraAngle_Y));
//            float Y_mouseAngled = Y_mouse;
//            float Z_mouseAngled = Z_mouse;
        float Y_mouseAngled = Y_mouse * MathHelper.sin(cameraAngle_X) + Z_mouse * MathHelper.cos(cameraAngle_X);
        float Z_mouseAngled = Y_mouse_XZ * MathHelper.cos(cameraAngle_Y) + X_mouse * MathHelper.sin(cameraAngle_Y) - Z_mouse_XZ * MathHelper.cos(cameraAngle_Y);


        DebugInfo.cameraVectorToCursorOnScreen = new Vec3d(X_mouseAngled, Y_mouseAngled, Z_mouseAngled);

        //vec3d = entity.getPositionEyes(partialTicks).addVector(-(((float) 960 - Mouse.getX()) / 43.8), 15, ((float) 508 - Mouse.getY()) / 43.8);
        //vec3d = entity.getPositionEyes(partialTicks).addVector((((float)screen_X_mouse) / (960.0f*1.25f)), 15, (((float)screen_Z_mouse) / (508.0f*1.25f)));
        Vec3d vec3d = entity.getPositionEyes(partialTicks).addVector(X_mouseAngled, Y_mouseAngled, Z_mouseAngled);
        //setting: FOV 30
        //vec3d = entity.getPositionEyes(partialTicks).addVector(((float)screen_X_mouse )/ 115.0f, 15, ((float) screen_Z_mouse) / 115.0f);
        Vec3d vec3d2 = vec3d.addVector(cursor_vectorAngled_X, cursor_vectorAngled_Y, cursor_vectorAngled_Z);

        calculateViewMiddlePointVector(half_screen_width,half_screen_height,cameraAngle_X,cameraAngle_Y,entity,partialTicks);


        //when the world is cut-through change the start vector to in the plane of cut-through
        //that takes a lot of computations so I'm going to use a approximate method
        if(ModBlockRendererDispatcher.cuttThroughOn)
        {
            BlockPos pos = entity.getPosition();


            Vec3d posPlane = new Vec3d(pos.getX(),pos.getY()+2.5,pos.getZ());

            if(vec3d.y < pos.getY()+2)
            {
                Vec3d unit = (vec3d2.subtract(vec3d).normalize());
                posPlane = posPlane.addVector(0,-2 - (unit.y),0);
            }
            else
            {
                Vec3d unit = (vec3d.subtract(vec3d2).normalize());
                posPlane = posPlane.addVector(0,(unit.y),0);
            }

            
//            Vec3d diffPlaneScreen = vec3d.subtract(posPlane);
//
//            if(diffPlaneScreen.y > 1.0)
//            {
//                Vec3d normPointVec = new Vec3d(cursor_vectorAngled_X, cursor_vectorAngled_Y, cursor_vectorAngled_Z).normalize();
//                double Yscale = Math.abs(diffPlaneScreen.y/normPointVec.y);
//
//                Vec3d addVec = new Vec3d(normPointVec.x*Yscale,normPointVec.y*Yscale,normPointVec.z*Yscale);
//
//                vec3d = vec3d.add(addVec);
//            }


            //simpler version of changing the start positon to the cutt-through plane
            AxisAlignedBB selectionBox = new AxisAlignedBB(posPlane.x-1000,posPlane.y,posPlane.z-1000,posPlane.x+1000,posPlane.y,posPlane.z+1000);
            RayTraceResult raytraceresult = selectionBox.calculateIntercept(vec3d, vec3d2);

            if(raytraceresult != null)
                vec3d = raytraceresult.hitVec;

        }
        
        
        
//            RayTraceResult result_2 = entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
//            //do debug printing here
//            if(result_2 != null)
//            {
//                System.out.printf(
//                    "mouse pixel XY,ZY,XZ %.6f,%.6f,%.6f mouse Angle XY,ZY,XZ %.3f,%.3f,%.3f cursor_vectorAngled XY,ZY,XZ %.2f,%.2f,%.2f hitvec XY,ZY,XZ %.2f,%.2f,%.2f \n",
//                    X_mouse,Y_mouse,Z_mouse,
//                    X_mouseAngled,Y_mouseAngled,Z_mouseAngled,
//                    cursor_vectorAngled_X,cursor_vectorAngled_Y,cursor_vectorAngled_Z,
//                    result_2.hitVec.x,result_2.hitVec.y,result_2.hitVec.z);
//            }
//        //calculate surface to adjust mouse position after zoom
//        //rotate plane arround the X-axis
//        Vec3d leftdown = new Vec3d(-1 ,-1* MathHelper.cos(cameraAngle_X),1*MathHelper.sin(cameraAngle_X));
//        Vec3d leftup = new Vec3d(-1,1* MathHelper.cos(cameraAngle_X),-1*MathHelper.sin(cameraAngle_X));
//        Vec3d rightdown = new Vec3d(1,-1* MathHelper.cos(cameraAngle_X),1*MathHelper.sin(cameraAngle_X));
//        Vec3d rightup = new Vec3d(1,1* MathHelper.cos(cameraAngle_X),-1*MathHelper.sin(cameraAngle_X));
//        //rotate plane around the Y-axis
//        leftdown = new Vec3d( leftdown.x*MathHelper.cos(cameraAngle_Y)+leftdown.z*MathHelper.sin(cameraAngle_Y),leftdown.y,leftdown.z*MathHelper.cos(cameraAngle_Y)-leftdown.x*MathHelper.sin(cameraAngle_Y));
//        leftup = new Vec3d(leftup.x*MathHelper.cos(cameraAngle_Y)+leftup.z*MathHelper.sin(cameraAngle_Y),leftup.y,leftup.z*MathHelper.cos(cameraAngle_Y)-leftup.x*MathHelper.sin(cameraAngle_Y));
//        rightdown = new Vec3d( rightdown.x*MathHelper.cos(cameraAngle_Y)+rightdown.z*MathHelper.sin(cameraAngle_Y),rightdown.y,rightdown.z*MathHelper.cos(cameraAngle_Y)-rightdown.x*MathHelper.sin(cameraAngle_Y));
//        rightup = new Vec3d(rightup.x*MathHelper.cos(cameraAngle_Y)+rightup.z*MathHelper.sin(cameraAngle_Y),rightup.y,rightup.z*MathHelper.cos(cameraAngle_Y)-rightup.x*MathHelper.sin(cameraAngle_Y));
//

//        if(true && BuildMode.FILL_MODE == EnumFillMode.FILL)
//        {
////            DebugInfo.smallerCubes = smallerCubes;
////            DebugInfo.viewportMidPointToIntersectPoint = viewportMidPointToIntersectPoint;
//            DebugInfo.leftdown = leftdown;
//            DebugInfo.leftup = leftup;
//            DebugInfo.rightdown = rightdown;
//            DebugInfo.rightup = rightup;
//            DebugInfo.viewAngle_Z = view_angle_Z;
//            DebugInfo.viewAngle_X = view_angle_X;
//        }
//        DebugInfo.viewAngle_Z = view_angle_Z;
//        DebugInfo.viewAngle_X = view_angle_X;
    
        currMouseVec = new Vec3d(cursor_vectorAngled_X,cursor_vectorAngled_Y,cursor_vectorAngled_Z);
    
        return new CursorVector(vec3d,vec3d2);
    }

    public static void calculateViewMiddlePointVector(int halfScreenWidth, int halfScreenHeight, float cameraAngle_X, float cameraAngle_Y, Entity entity,float partialTicks)
    {
        float X_mouse = (-(halfScreenWidth/(float)halfScreenWidth)*(0.1f/ModEntity.distanceMultiplierX) );
        float Y_mouse = ModEntityRenderer.customCameraDistance;
        float Z_mouse = (-(halfScreenHeight/(float)halfScreenHeight )*(0.1f/ModEntity.distanceMultiplierY ));


//        System.out.println("X / Y / Z " + X_mouse +  " / " + Y_mouse +  " / " + Z_mouse + " view_angle X / Z " + ((float)view_angle_X)/(float)Math.PI*(180) + " / " + ((float)view_angle_Z)/(float)Math.PI*(180));

        // incalculate the angle at which the camera is tilted
        //first calculate an intermediate vector between XY and XZ axis
        float Y_mouse_XZ =  Y_mouse * MathHelper.cos(cameraAngle_X);
        float Z_mouse_XZ = Z_mouse * MathHelper.sin(cameraAngle_X);



        float X_mouseAngled = (Y_mouse_XZ * MathHelper.sin(cameraAngle_Y) - X_mouse * MathHelper.cos(cameraAngle_Y) + Z_mouse_XZ * MathHelper.sin(cameraAngle_Y));
//            float Y_mouseAngled = Y_mouse;
//            float Z_mouseAngled = Z_mouse;
        float Y_mouseAngled = Y_mouse * MathHelper.sin(cameraAngle_X) + Z_mouse * MathHelper.cos(cameraAngle_X);
        float Z_mouseAngled = Y_mouse_XZ * MathHelper.cos(cameraAngle_Y) + X_mouse * MathHelper.sin(cameraAngle_Y) - Z_mouse_XZ * MathHelper.cos(cameraAngle_Y);

        IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE= new Vec3d (X_mouseAngled, Y_mouseAngled, Z_mouseAngled);
        DebugInfo.cameraVectorToMiddleScreen =IsometricCamera.FOCUS_POINT_TO_SCREEN_MIDDLE;
//        Vec3d  vec3d = entity.getEyePosition(partialTicks).add(X_mouseAngled, Y_mouseAngled, Z_mouseAngled);
    }

    /***
     * This method can only be called once every render tick because newPointedBlockVec is set to false after getting it once
     * This stays until setCurrPointedBlockVec is called again
     * @return
     */
    public static Vec3d getCurrPointedBlockVec()
    {
        if(newPointedBlockVec)
        {
            ModEntity.newPointedBlockVec = false;
            return currPointedBlockVec;
        }
        else
            return null;
    }

    public static void setCurrPointedBlockVec(Vec3d pointedBlockVec)
    {
        ModEntity.currPointedBlockVec = pointedBlockVec;
        ModEntity.newPointedBlockVec = true;
    }
    public static void resetCurrPointedBlockVec()
    {
        ModEntity.currPointedBlockVec = null;
        ModEntity.newPointedBlockVec = true;
    }

}


