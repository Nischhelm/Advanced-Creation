package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends Entity
{
    static
    {
        System.out.println("loaded EntityPlayerMixin class injecting setNoClip() into the OnUpdate() method ");
    }

    public EntityPlayerMixin(World worldIn)
    {
        super(worldIn);
    }

    @ModifyExpressionValue( //Modifies output of this.isSpectator() in EntityPlayer.onUpdate
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSpectator()Z", ordinal = 0)
    )
    public boolean advcreation_isometricNoClip(boolean original)
    {
//        System.out.println("Successfully done the mixin bro");
        return IsometricCamera.isPlayerInIsometricPerspective() || original;
    }
}
