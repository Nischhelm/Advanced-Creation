package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class RenderTemplate
{
    public static void  drawSelectionBox( EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, Color color)
    {
        float[] components = color.getColorComponents(null);
        drawSelectionBox(player, movingObjectPositionIn,execute,partialTicks, components[0],components[1], components[2],0.4f);
    }

    public static void  drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks,float red,float green,float blue)
    {
        drawSelectionBox(player, movingObjectPositionIn,execute,partialTicks, red, green, blue,0.4f);
    }

    public static void  drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks,float red,float green,float blue,float alpha)
    {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderGlobal renderglobal = mc.renderGlobal;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = mc.world.getBlockState(blockpos);

            if (mc.world.getWorldBorder().contains(blockpos))
            {
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                renderglobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-d0, -d1, -d2), red, green, blue, alpha);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
    public static void  drawBlockSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks,float red,float green,float blue)
    {
        drawBlockSelectionBox(player, movingObjectPositionIn,execute,partialTicks, red, green, blue,0.4f);
    }

    public static void  drawBlockSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks,float red,float green,float blue,float alpha)
    {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderGlobal renderglobal = mc.renderGlobal;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = mc.world.getBlockState(blockpos);

            if (mc.world.getWorldBorder().contains(blockpos))
            {
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                AxisAlignedBB box = iblockstate.getSelectedBoundingBox(mc.world, blockpos);
                AxisAlignedBB newbox = new AxisAlignedBB(Math.floor(box.minX),Math.floor(box.minY),Math.floor(box.minZ),Math.ceil(box.maxX),Math.ceil(box.maxY),Math.ceil(box.maxZ));
                renderglobal.drawSelectionBoundingBox(newbox.grow(0.0020000000949949026D).offset(-d0, -d1, -d2), red, green, blue, alpha);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public static void  drawLine(Vec3d worldBeginVec, Vec3d worldEndVec, EntityPlayer player, int execute, float partialTicks, float red, float green, float blue)
    {
        RenderTemplate.drawLine(worldBeginVec.x, worldBeginVec.y, worldBeginVec.z, worldEndVec.x, worldEndVec.y, worldEndVec.z, player, execute, partialTicks, red,green,blue);
    }

    public static void  drawLine(double worldBeginX,double worldBeginY,double worldBeginZ,double worldEndX,double worldEndY,double worldEndZ, EntityPlayer player, int execute, float partialTicks,float red,float green,float blue)
    {
        if (execute == 0)
        {
            Minecraft mc = Minecraft.getMinecraft();

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
            drawSelectionLine(worldBeginX-d0,worldBeginY -d1, worldBeginZ-d2,
                worldEndX-d0,worldEndY -d1, worldEndZ-d2, red, green, blue, 0.4F);

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
    
    
    public static void  drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks)
    {
        drawSelectionBox(player,movingObjectPositionIn,execute,partialTicks,0.0F, 1.0F, 0.0F);
    }
    
    public static BlockPos getNewPosition(BlockPos originalBlockPos, Block hitBlock, EnumFacing face, Vec3d hitVec)
    {
//        if(hitBlock instanceof BlockVine || hitBlock instanceof BlockTallGrass || hitBlock instanceof BlockDoublePlant || hitBlock instanceof BlockSnow)
//        {
//            return originalBlockPos;
//        }
//        return  new BlockPos(hitVec.add (HelpFunctions.Vec3iToVec3d(face.getDirectionVec())));
        return originalBlockPos.offset(face);
    }


    public static void drawSelectionLine(double beginX,double beginY,double beginZ,double endX,double endY,double endZ, float red, float green, float blue, float alpha)
    {
        drawLine(beginX,beginY,beginZ,endX,endY,endZ, red, green, blue, alpha);
    }
    
    protected static void drawLine(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawLine(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }
    
    protected static void drawLine(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        buffer.pos(minX,minY,minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX,minY,minZ ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX,maxY,maxZ ).color(red, green, blue, alpha).endVertex();
    }

    public static void drawBlockOutline(MessagePlaceListsTemplateBlock.CompressedTemplateBlock block, RayTraceResult objectMouseOver, EntityPlayer entityplayer, Float partialTicks, float red, float green, float blue)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, objectMouseOver.hitVec,
                EnumFacing.NORTH, block.pos);
        drawSelectionBox(entityplayer, new_position, 0, partialTicks, red, green, blue);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
    }

    public static Vec3d drawCircleHelplines(EntityPlayer entityplayer, Float partialTicks, Vec3d middleVec, ArrayList<Vec3d> previewCircleVecPoints,float red,float green,float blue)
    {
        Vec3d addedVec = new Vec3d(1.0,0,0);
        if(BuildMode.LINE_VEC !=null)
            addedVec = BuildMode.LINE_VEC;

        Vec3d offsetCursorVec = middleVec.add(addedVec);
        Vec3d newCursorVec = offsetCursorVec;
        double minDistance = 100000.0;
        Vec3d previousPoint = null;
        for (Vec3d circleVecPoint : previewCircleVecPoints)
        {
            if (previousPoint == null)
            {
                previousPoint = circleVecPoint;
                continue;
            }
            //find the point that is closest to the current cursor vector
            double newDistance = offsetCursorVec.distanceTo(circleVecPoint);
            if(newDistance < minDistance)
            {
                minDistance = newDistance;
                newCursorVec = circleVecPoint;
            }

            drawLine(previousPoint.x, previousPoint.y, previousPoint.z, circleVecPoint.x, circleVecPoint.y, circleVecPoint.z, entityplayer, 0,
                    partialTicks, red,green,blue);

            previousPoint = circleVecPoint;
        }
        return newCursorVec;
    }

    public static void drawMiddleCross(EntityPlayer entityplayer, Float partialTicks, Vec3d startVecCopy)
    {
        drawLine(startVecCopy.x, startVecCopy.y, startVecCopy.z, startVecCopy.x, startVecCopy.y + 2.0, startVecCopy.z, entityplayer, 0, partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
        drawLine(startVecCopy.x - 0.5, startVecCopy.y, startVecCopy.z, startVecCopy.x + 0.5, startVecCopy.y, startVecCopy.z, entityplayer, 0, partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
        drawLine(startVecCopy.x, startVecCopy.y, startVecCopy.z - 0.5, startVecCopy.x, startVecCopy.y, startVecCopy.z + 0.5, entityplayer, 0, partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
    }

    public static void  drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, AxisAlignedBB boundingBox, int execute, float partialTicks,float red,float green,float blue,float alpha)
    {
        if (execute == 0)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderGlobal renderglobal = mc.renderGlobal;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
//            IBlockState iblockstate = mc.world.getBlockState(blockpos);


            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
//                AxisAlignedBB box = iblockstate.getSelectedBoundingBox(mc.world, blockpos);
//                AxisAlignedBB newbox = new AxisAlignedBB(Math.floor(box.minX),Math.floor(box.minY),Math.floor(box.minZ),Math.ceil(box.maxX),Math.ceil(box.maxY),Math.ceil(box.maxZ));
            renderglobal.drawSelectionBoundingBox(boundingBox.grow(0.0020000000949949026D).offset(-d0, -d1, -d2), red, green, blue, alpha);


            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
