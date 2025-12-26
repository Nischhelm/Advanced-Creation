package com.deadtiger.advcreation.mixin.accessor;

import net.minecraft.util.ScreenShotHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.IntBuffer;

@Mixin(ScreenShotHelper.class)
public interface ScreenshotHelperAccessor {
    @Accessor("pixelBuffer")
    static IntBuffer getPixelBuffer(){return null;}
    @Accessor("pixelBuffer")
    static void setPixelBuffer(IntBuffer val){}
    @Accessor("pixelValues")
    static int[] getPixelValues(){return null;}
    @Accessor("pixelValues")
    static void setPixelValues(int[] val){}
}
