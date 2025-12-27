package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.event.ClientEventHandler;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.plugin.transformer.GeneralTransformer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    @Shadow private float thirdPersonDistancePrev;
    @Shadow private boolean cloudFog;

    static
    {
        System.out.println("loaded EntityRendererMixin (1) class redirecting isSpectator() to setSpectatorToTrue(...) in the renderWorldPass() method ");
        System.out.println("loaded EntityRendererMixin (2) class injecting allowIsometricView(...) in the orientCamera(...) method ");
        System.out.println("loaded EntityRendererMixin (3) class injecting setThirdPersonDistance(...) in the updateRenderer(...) method ");
        System.out.println("loaded EntityRendererMixin (4) class redirecting raytrace(...) to customRaytrace(...) in the getMouseOver() method ");
        System.out.println("loaded EntityRendererMixin (5) class redirecting getPositionEyes(...) to getCustomPositionEyes(...) in the getMouseOver() method ");
        System.out.println("loaded EntityRendererMixin (6) class redirecting addVector(...) to customVectorCalculation(...) in the getMouseOver() method ");
        System.out.println("loaded EntityRendererMixin (7) class redirecting turn(...) to turnPlayerToCursor(...) in the updateCameraAndRender() method ");

    }

    //This mixin has no reason not to be compatible with other mods
    @WrapOperation(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.isSpectator()Z"))
    public boolean setSpectatorToTrue(EntityPlayerSP entityPlayerSP, Operation<Boolean> original)
    {
//        System.out.println("Successfully done the mixin in renderWorldPass(IFJ)V");
        if(IsometricCamera.isPlayerInIsometricPerspective())
            return true;
        return original.call(entityPlayerSP);
    }

    @Inject(method = "orientCamera(F)V", at = @At(value = "HEAD"), cancellable = true)
    public void allowIsometricView(float partialTicks, CallbackInfo ci)
    {
        GeneralTransformer.checkForLlibrary();

//        System.out.println("Successfully done the mixin in orientCamera(F)V");
        if(IsometricCamera.isPlayerInIsometricPerspective())
        {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
            float f = entity.getEyeHeight();
            double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
            double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
            double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;

            float thirdPersonDistancePrev = this.thirdPersonDistancePrev;
            float rotationYaw = entity.rotationYaw;
            float rotationPitch = entity.rotationPitch;
            ModEntityRenderer.changeThirdPersonToIsometricNoReturn(partialTicks,d0,d1,d2,thirdPersonDistancePrev,rotationYaw,rotationPitch);
            ci.cancel();

            //extra code after going into the thirdpersonview > 0
            // from the orient camera method
            if (!Minecraft.getMinecraft().gameSettings.debugCamEnable)
            {
                float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
                float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                float roll = 0.0F;
                if (entity instanceof EntityAnimal)
                {
                    EntityAnimal entityanimal = (EntityAnimal)entity;
                    yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
                }
                IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(Minecraft.getMinecraft().world, entity, partialTicks);
                net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup((EntityRenderer) (Object) this, entity, state, partialTicks, yaw, pitch, roll);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.translate(0.0F, -f, 0.0F);
            d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
            d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
            d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;

            this.cloudFog = Minecraft.getMinecraft().renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
        }
    }

// // This worked to edit the hardcoded 4.0 but not what I need I need more
//    @ModifyVariable(
//            method = "Lnet/minecraft/client/renderer/EntityRenderer;orientCamera(F)V",
//            index = 10,
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/settings/GameSettings;debugCamEnable:Z",
//                    ordinal = 1
//            ))
//    public double allowIsometricView2(double value)
//    {
//
//        System.out.println("Successfully done the mixin allowIsometricView2 into orientCamera(F)V");
//
//        if(!GeneralTransformer.checkedLLibraryUsage)
//        {
//            System.out.println("Checking your mods for incompatibilities with Advanced Creation");
//            for (ModContainer mod : Loader.instance().getModList())
//            {
//                System.out.println("modid: " + mod.getModId());
//                if(mod.getModId().contains("llibrary"))
//                    GeneralTransformer.usingLLibrary =true;
//            }
//            GeneralTransformer.checkedLLibraryUsage = true;
//
//            if(GeneralTransformer.usingLLibrary)
//                System.out.println("you are using Llibrary, Advanced Creation will make the necessary adjustments");
//            else
//                System.out.println("you are NOT using Llibrary, Advanced Creation DOES NOT need to make any adjustments");
//        }
//
////        return (double)ModEntityRenderer.getUpdateRendererCameraDistance();
//
//        double thirdPersonDistance = (double)ModEntityRenderer.getUpdateRendererCameraDistance();
//        EntityRenderer thisRenderer= (EntityRenderer) (Object) this;
//        float thirdPersonDistancePrev = ((float) ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class,thisRenderer,"field_78491_C")); // thirdPersonDistancePrev
//////
//        float partialTicks =  ClientEventHandler.renderTickTime;
//
//        return (double)(thirdPersonDistancePrev + (thirdPersonDistance - thirdPersonDistancePrev) * partialTicks);
//    }

    @Inject(method = "updateRenderer()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistancePrev:F", opcode = Opcodes.PUTFIELD,shift = At.Shift.AFTER))
    public void setThirdPersonDistance(CallbackInfo ci)
    {
//        System.out.println("Successfully done the mixin setThirdPersonDistance");
        this.thirdPersonDistancePrev = ModEntityRenderer.getUpdateRendererCameraDistance();
    }

    @WrapOperation(method = "getMouseOver(F)V", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.rayTrace(DF)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult customRaytrace(Entity entity, double blockReachDistance, float partialTicks, Operation<RayTraceResult> original)
    {
//        System.out.println("Successfully done the mixin of customRaytrace in getMouseOver(F)V");

        return ModEntity.rayTrace(entity,blockReachDistance,partialTicks);
    }

    @WrapOperation(method = "getMouseOver(F)V", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.getPositionEyes(F)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d getCustomPositionEyes(Entity entity, float partialTicks, Operation<Vec3d> original)
    {
//        System.out.println("Successfully done the mixin getCustomPositionEyes in getMouseOver(F)V");

        return ModEntityRenderer.getCurrStartCursorVector(entity, partialTicks);
    }

    @WrapOperation(method = "getMouseOver(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d customVectorCalculation(Vec3d vec3d, double x, double y, double z, Operation<Vec3d> original)
    {
//        System.out.println("Successfully done the mixin customVectorCalculation in getMouseOver(F)V");
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        return ModEntityRenderer.getCurrCursorPointVector(vec3d,x,y,z,entity);
    }

    @WrapOperation(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.turn(FF)V"),require = 2)
    public void turnPlayerToCursor(EntityPlayerSP entityPlayerSP, float yaw, float pitch, Operation<Void> original)
    {
//        System.out.println("Successfully done the mixin turnPlayerToCursor in updateCameraAndRender(FJ)V");
        ModEntityRenderer.changePlayerRotation(entityPlayerSP,yaw,pitch, ClientEventHandler.renderTickTime);
    }
}
