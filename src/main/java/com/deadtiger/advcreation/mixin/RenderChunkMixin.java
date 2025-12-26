package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(RenderChunk.class)
public abstract class RenderChunkMixin
{
    static
    {
        System.out.println("loaded RenderChunkMixin class redirecting renderBlock to renderCutthroughBlocks(...) in the rebuildChunk() method ");
    }

    @WrapOperation( //WrapOperation gets the original call as a parameter so it doesn't conflict with other mods targeting this piece of code like Redirect does (never use Redirect unless you want this code to conflict with other mods)
            method = "rebuildChunk(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/BlockRendererDispatcher.renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z")
    )
    public boolean advcreation_renderCutThroughBlocks(BlockRendererDispatcher blockRendererDispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, Operation<Boolean> original)
    {
//        System.out.println("Successfully done the mixin in renderWorldPass(IFJ)V");
        if(IsometricCamera.isPlayerInIsometricPerspective())
            return ModBlockRendererDispatcher.customRenderBlock(blockRendererDispatcher,state,pos,blockAccess,bufferBuilderIn);
        return original.call(blockRendererDispatcher, state, pos, blockAccess, bufferBuilderIn); //this is to ensure that other mixins will also be able to modify the target call
    }
}
