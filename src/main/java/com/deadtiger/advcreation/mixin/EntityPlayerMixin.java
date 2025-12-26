package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "onUpdate()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;noClip:Z", opcode = Opcodes.PUTFIELD,shift = At.Shift.AFTER))
    public void setNoClip(CallbackInfo ci)
    {
        EntityPlayer thisPlayer = (EntityPlayer) (Object) this;
//        System.out.println("Successfully done the mixin bro");
        if(IsometricCamera.isPlayerInIsometricPerspective())
            thisPlayer.noClip = true;
    }
}
