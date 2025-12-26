package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.event.ClientEventHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public class MouseHelperMixin
{
    static
    {
        System.out.println("loaded MouseHelperMixin class redirecting setGrabbed(...) to dontGrabMouseInIsometric(...) in the grabMouseCursor() method ");
    }

//    @Redirect(method = "grabMouseCursor()V", at = @At(value = "INVOKE", target = "org/lwjgl/input/Mouse.setGrabbed(Z)V"))
    @Inject(method = "grabMouseCursor()V", at = @At(value = "INVOKE", target = "org/lwjgl/input/Mouse.setGrabbed(Z)V", remap = false), cancellable = true)
    public void dontGrabMouseInIsometric(CallbackInfo ci)
    {
//        System.out.println("Successfully done the mixin dontGrabMouseInIsometric in grabMouseCursor()V");
        if(!ModMouseHelper.ifFirstPersonGrabMouse())
        {
            ci.cancel();
            return;
        }
    }

}
