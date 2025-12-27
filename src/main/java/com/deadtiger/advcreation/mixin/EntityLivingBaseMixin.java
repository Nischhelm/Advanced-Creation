package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin
{
    static
    {
        System.out.println("loaded EntityLivingBaseMixin class injecting tryToMoveRelativeToCamera() into the moveRelative() method ");
    }

    @Inject(method = "moveRelative(FFFF)V", at = @At(value = "HEAD"), cancellable = true)
    public void tryToMoveRelativeToCamera(float strafe, float up, float forward, float friction,CallbackInfo ci)
    {
        //only do this when the entity is a player and in isometric
        EntityLivingBase thisEntityLiving = (EntityLivingBase) (Object) this;
//        System.out.println("Successfully done the mixin tryToMoveRelativeToCamera()");
        if(thisEntityLiving instanceof EntityPlayer &&
                IsometricCamera.isPlayerInIsometricPerspective() &&
                ConfigurationHandler.general.MOVE_RELATIVE_TO_CAMERA)
        {
            ModEntity.reducedCustomMoveRelative(strafe,up,forward,friction,thisEntityLiving);
            ci.cancel();
        }
    }
}
