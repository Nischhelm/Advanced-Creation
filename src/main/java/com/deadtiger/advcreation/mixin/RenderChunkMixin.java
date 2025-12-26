package com.deadtiger.advcreation.mixin;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(RenderChunk.class)
public abstract class RenderChunkMixin
{
    static
    {
        System.out.println("loaded RenderChunkMixin class redirecting renderBlock to renderCutthroughBlocks(...) in the rebuildChunk() method ");
    }


    @Redirect(method = "rebuildChunk(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/BlockRendererDispatcher.renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z"))
//    @Inject(method = "onUpdate()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;noClip:Z", opcode = Opcodes.PUTFIELD,shift = At.Shift.AFTER))
//    @Inject(method = "onUpdate()V", at =  @At("HEAD"))
    public boolean renderCutthroughBlocks(BlockRendererDispatcher blockRendererDispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn)
    {
//        System.out.println("Successfully done the mixin in renderWorldPass(IFJ)V");
        if(IsometricCamera.isPlayerInIsometricPerspective())
            return ModBlockRendererDispatcher.customRenderBlock(blockRendererDispatcher,state,pos,blockAccess,bufferBuilderIn);
        return blockRendererDispatcher.renderBlock(state, pos, blockAccess, bufferBuilderIn);
    }
}
