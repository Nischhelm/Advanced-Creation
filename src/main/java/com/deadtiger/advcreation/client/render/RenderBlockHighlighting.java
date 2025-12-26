package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.template.Template;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class exists to draw white transparent highlighting boxes arround blocks between the 2 positions given to the render function
 *
 * As of MC1.12-Alpha1.3 it used when:
 *  -  selecting an area to make it into a template in the CREATE mode
 *  -  Selecting an area to copy or move in BUILD mode
 *
 *  If the selected area contains more blocks than the SIZE_LIMIT  only the blocks that are 5 blocks from the vertical sides
 *  are highlighted massively reducing the amount of blocks highlighted making it more performant
 *
 *  In some cases the for loop for highlighting gets stuck somehow, when the loop reaches the BREAK_LIMIT of iterations is breaks the loop
 */
@SideOnly(Side.CLIENT)
public class RenderBlockHighlighting
{
    private static float red = 0.8F;
    private static float green = 0.8F;
    private static float blue = 0.8F;

    public static int SIZE_LIMIT = 50*25*50;
    public static int BREAK_LIMIT = 100*100*50;
    
    public static void render(BlockPos pos1,BlockPos pos2,boolean tooBig,float alpha,Minecraft mc, float partialTicks)
    {

        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
        World world = mc.player.world;
        Iterable<BlockPos> iterable = BlockPos.getAllInBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();

        GlStateManager.depthMask(false);

        int count = 0;
        for (BlockPos blockpos : iterable)
        {

            IBlockState iblockstate = world.getBlockState(blockpos);
            if (iblockstate.getBlock() != Blocks.AIR)
            {

                if (tooBig &&
                        ((Math.abs(pos1.getX() - blockpos.getX()) > 5) && (Math.abs(pos2.getX() - blockpos.getX()) > 5) &&
                        (Math.abs(pos1.getZ() - blockpos.getZ()) > 5) && (Math.abs(pos2.getZ() - blockpos.getZ()) > 5) ))
                    continue;

                AxisAlignedBB axisalignedbb = iblockstate.getSelectedBoundingBox(world, blockpos).grow(0.002D).offset(-d0, -d1, -d2);
                double x = axisalignedbb.minX;
                double y = axisalignedbb.minY;
                double z = axisalignedbb.minZ;
                double x2 = axisalignedbb.maxX;
                double y2 = axisalignedbb.maxY;
                double z2 = axisalignedbb.maxZ;

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.WEST) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate)) &&
                        world.isAirBlock(blockpos.add(EnumFacing.WEST.getDirectionVec())))
                    drawHighlightXAxisSurface(x, y, z, y2, z2, red, green, blue, alpha);

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.SOUTH) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate))&&
                        world.isAirBlock(blockpos.add(EnumFacing.SOUTH.getDirectionVec())))
                    drawHighlightZAxisSurface(x, y, x2, y2, z2, red, green, blue, alpha);

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.EAST) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate))&&
                        world.isAirBlock(blockpos.add(EnumFacing.EAST.getDirectionVec())))
                    drawHighlightXAxisSurface(x2, y, z2, y2, z, red, green, blue, alpha);

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.NORTH) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate))&&
                        world.isAirBlock(blockpos.add(EnumFacing.NORTH.getDirectionVec())))
                    drawHighlightZAxisSurface(x2, y, x, y2, z, red, green, blue, alpha);

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate))&&
                        world.isAirBlock(blockpos.add(EnumFacing.DOWN.getDirectionVec())))
                    drawHighlightYAxisSurface(x, y, z, x2, z2, red, green, blue, alpha);

                if ((iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.UP) == BlockFaceShape.SOLID || Template.isFurnitureBlock(iblockstate))&&
                        world.isAirBlock(blockpos.add(EnumFacing.UP.getDirectionVec())))
                    drawHighlightYAxisSurface(x2,y2,z2,x,z,red,green,blue,alpha);
            }

            count++;
            if(count > BREAK_LIMIT)
                break;
        }
        
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }



    private static void drawHighlightYAxisSurface(double x, double y, double z, double x2, double z2, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator4 = Tessellator.getInstance();
        BufferBuilder bufferbuilder4 = tessellator4.getBuffer();
        bufferbuilder4.begin(5, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder4.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder4.pos(x2, y, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder4.pos(x, y, z2).color(red, green, blue, alpha).endVertex();
        bufferbuilder4.pos(x2, y, z2).color(red, green, blue, alpha).endVertex();
        tessellator4.draw();
    }

    public static void drawHighlightZAxisSurface(double x, double y, double x2, double y2, double z2, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator1 = Tessellator.getInstance();
        BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
        bufferbuilder1.begin(5, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder1.pos(x, y2, z2).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(x, y, z2).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(x2, y, z2).color(red, green, blue, alpha).endVertex();
        tessellator1.draw();
    }

    public static void drawHighlightXAxisSurface(double x, double y, double z, double y2, double z2, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, z2).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y2, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y2, z2).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }
}
