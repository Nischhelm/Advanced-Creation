package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.plugin.transformer.GeneralTransformer;
import net.ilexiconn.llibrary.server.core.patcher.LLibraryHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

//Christiaan: class containing a static method that will replace all thirdperson view code in EntityRenderer (line: 661)
public class ModEntityRenderer {

    public static float customCameraDistance_min = 2.0F;
    public static float customCameraDistance_max = 100.0F;
    public static float customCameraDistance = 15.0F;
    public static float newCustomCameraDistance = 15.0F;
    public static boolean cameraDistanceChange = false;



    public static float changeThirdPersonToIsometric(float partialTicks, double d0, double d1, double d2, Minecraft mc, float thirdPersonDistancePrev,
                                                    Entity entity )
    {
//        double d3 = (double)(thirdPersonDistancePrev + (4.0F - thirdPersonDistancePrev) * partialTicks);
        double d3 = (double)(thirdPersonDistancePrev + (customCameraDistance - thirdPersonDistancePrev) * partialTicks);

        if (mc.gameSettings.debugCamEnable)
        {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
        }
        else
        {
            float f1 = entity.rotationYaw;
            float f2 = entity.rotationPitch;

            GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);

        }
        return (float) d3;
    }

    public static void changeThirdPersonToIsometricNoReturn(float partialTicks, double d0, double d1, double d2, Minecraft mc, float thirdPersonDistancePrev,
                                                    Entity entity )
    {
        changeThirdPersonToIsometric(partialTicks,d0,d1,d2,mc,thirdPersonDistancePrev,entity);
    }

    public static float changeThirdPersonToIsometric(float partialTicks, double d0, double d1, double d2, float thirdPersonDistancePrev,
                                                    float rotationYaw, float rotationPitch)
    {
//        double d3 = (double)(thirdPersonDistancePrev + (4.0F - thirdPersonDistancePrev) * partialTicks);

        double newCameraDistance = customCameraDistance;
        if(GeneralTransformer.usingLLibrary && ConfigurationHandler.modComp.USE_LLIBRARY_CAMERA_DISTANCE)
            newCameraDistance = LLibraryHooks.getViewDistance(Minecraft.getMinecraft().player,partialTicks);
        else if( !IsometricCamera.isPlayerInIsometricPerspective() && Minecraft.getMinecraft().gameSettings.thirdPersonView > 0)
            newCameraDistance = 4.0F;

        double d3 = (double)(thirdPersonDistancePrev + (newCameraDistance - thirdPersonDistancePrev) * partialTicks);

        if (Minecraft.getMinecraft().gameSettings.debugCamEnable)
        {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
        }
        else if(IsometricCamera.isPlayerInIsometricPerspective())
        {
            float f1 = rotationYaw;
            float f2 = rotationPitch;

            GlStateManager.rotate(rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotate(f1 - rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f2 - rotationPitch, 1.0F, 0.0F, 0.0F);

        }
        else
        {
            float f1 = rotationYaw;
            float f2 = rotationPitch;
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.gameSettings.thirdPersonView == 2)
            {
                f2 += 180.0F;
            }

            double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
            double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
            double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

            for (int i = 0; i < 8; ++i)
            {
                float f3 = (float)((i & 1) * 2 - 1);
                float f4 = (float)((i >> 1 & 1) * 2 - 1);
                float f5 = (float)((i >> 2 & 1) * 2 - 1);
                f3 = f3 * 0.1F;
                f4 = f4 * 0.1F;
                f5 = f5 * 0.1F;
                RayTraceResult raytraceresult = mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

                if (raytraceresult != null)
                {
                    double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

                    if (d7 < d3)
                    {
                        d3 = d7;
                    }
                }
            }

            if (mc.gameSettings.thirdPersonView == 2)
            {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotate(f1 - rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f2 - rotationPitch, 1.0F, 0.0F, 0.0F);
        }

        if(GeneralTransformer.usingLLibrary)
            LLibraryHooks.prevRenderViewDistance = (float)d3;
        return (float) d3;
    }

    public static void changeThirdPersonToIsometricNoReturn(float partialTicks, double d0, double d1, double d2, float thirdPersonDistancePrev,
                                                            float rotationYaw, float rotationPitch)
    {
        changeThirdPersonToIsometric(partialTicks,d0,d1,d2,thirdPersonDistancePrev,rotationYaw,rotationPitch);
    }
    
    public static void changePlayerRotation(EntityPlayerSP player, float relYaw, float relPitch, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if ( IsometricCamera.isPlayerInIsometricPerspective() && !mc.isGamePaused())
        {

            Vec3d newLookVector =  ModEntity.getCurrPointedBlockVec();
            if(newLookVector == null)
            {
                player.rotationYaw = (float) ConfigurationHandler.cameraConfig.Y_angle;
                player.rotationPitch = (float) ConfigurationHandler.cameraConfig.X_angle;
//            event.getInfo().setup(event.getInfo().getEntity().level,event.getInfo().getEntity(),true,false, (float) event.getRenderPartialTicks());
            }
            else
            {
                Vec3d eyes = player.getPositionEyes(partialTicks);
                double d0 = newLookVector.x - eyes.x;
                double d1 = newLookVector.y - eyes.y;
                double d2 = newLookVector.z - eyes.z;
                double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                player.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
                player.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
                player.rotationYawHead = player.rotationYaw;
                player.prevRotationPitch = player.rotationPitch;
                player.prevRotationYaw = player.rotationYaw;
            }

        }
        else
        {
            player.turn(relYaw, relPitch);
        }

    }

    public static float getUpdateRendererCameraDistance()
    {
        float newCameraDistance = customCameraDistance;
        if(GeneralTransformer.usingLLibrary && ConfigurationHandler.modComp.USE_LLIBRARY_CAMERA_DISTANCE)
            newCameraDistance = LLibraryHooks.prevRenderViewDistance;
        else if( !IsometricCamera.isPlayerInIsometricPerspective() && Minecraft.getMinecraft().gameSettings.thirdPersonView > 0)
            newCameraDistance = 4.0F;

        return newCameraDistance;
    }

    public static Vec3d getCurrStartCursorVector(Entity entity,float partialTicks)
    {
        if(entity instanceof EntityPlayer && IsometricCamera.isPlayerInIsometricPerspective())
        {
            if(ModEntity.currMouseVec!= null && ModEntity.currCursorVec.start != null)
                return ModEntity.currCursorVec.start;
        }
        return entity.getPositionEyes(partialTicks);
    }

    public static Vec3d getCurrCursorPointVector(Vec3d eyes,Vec3d lookVector,double reach, float partialTicks,Entity entity)
    {
        if(entity instanceof EntityPlayer && IsometricCamera.isPlayerInIsometricPerspective()  && !Minecraft.getMinecraft().isGamePaused() && ModEntity.currCursorVec != null)
        {
//           return currCursorVec.end.subtract(eyes);
            if(ModEntity.currMouseVec!= null && ModEntity.currCursorVec.end != null)
                return ModEntity.currCursorVec.end;
        }
        return  eyes.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);

    }

    public static Vec3d getCurrCursorPointVector(Vec3d eyes,double lookX,double lookY,double lookZ,Entity entity)
    {
        if(entity instanceof EntityPlayer && IsometricCamera.isPlayerInIsometricPerspective() && ModEntity.currCursorVec != null)
        {
//           return currCursorVec.end.subtract(eyes);
            if(ModEntity.currMouseVec!= null && ModEntity.currCursorVec.end != null)
                return ModEntity.currCursorVec.end;
        }
        return  eyes.add(lookX,lookY,lookZ);

    }

    
}