package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCameraFocusPoint
{

    public static void renderPlayerCameraFocusPoint(EntityPlayer clientPlayer, EntityPlayer otherPlayer, double radius, Float partialTicks)
    {
        Vec3d eyes = otherPlayer.getPositionEyes(partialTicks);
        EnumDirectionMode autoDir = IsometricCamera.calcAutoDirectionMode();
        double extend = radius;
        if (autoDir == EnumDirectionMode.XY)
        {
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.ZY, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XZ, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XY, partialTicks, extend);
        }
        else if (autoDir == EnumDirectionMode.ZY)
        {
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XY, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XZ, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.ZY, partialTicks, extend);
        }
        else if (autoDir == EnumDirectionMode.XZ)
        {
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XY, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.ZY, partialTicks, extend);
            RenderPlaneSurface.render(eyes, radius, EnumDirectionMode.XZ, partialTicks, extend);
        }

        if (!otherPlayer.equals(Minecraft.getMinecraft().player))
        {
            double d0 = clientPlayer.lastTickPosX + (clientPlayer.posX - clientPlayer.lastTickPosX) * (double) partialTicks;
            double d1 = clientPlayer.lastTickPosY + (clientPlayer.posY - clientPlayer.lastTickPosY) * (double) partialTicks;
            double d2 = clientPlayer.lastTickPosZ + (clientPlayer.posZ - clientPlayer.lastTickPosZ) * (double) partialTicks;
            float pitch = 0f;
            float yaw = 0f;
            if (IsometricCamera.CAMERA_VECTOR != null)
            {
                Vec3d straightVec = clientPlayer.getPositionEyes(partialTicks).add(IsometricCamera.CAMERA_VECTOR).subtract(otherPlayer.getPositionEyes(partialTicks));
                Vec3d horVec = new Vec3d(straightVec.x, 0, straightVec.z);
                pitch = (float) (Math.atan2(straightVec.y, horVec.length()) / Math.PI) * 180.0f;
                yaw = (float) -((Math.atan2(straightVec.x, straightVec.z) / Math.PI) * 180.0f) - 180f;
            }

            Render render = Minecraft.getMinecraft().getRenderManager().<Entity>getEntityRenderObject(otherPlayer);
            EntityRenderer.drawNameplate(render.getFontRendererFromRenderManager(), otherPlayer.getName(), ((float) (otherPlayer.posX - d0)), ((float) (otherPlayer.posY - d1)) + 3.0f, ((float) (otherPlayer.posZ - d2)), 0, yaw, pitch, false, false);

        }
    }


    public static void renderPlayerAbsoluteCoordinate(EntityPlayer clientPlayer, Float partialTicks)
    {
        double d0 = clientPlayer.lastTickPosX + (clientPlayer.posX - clientPlayer.lastTickPosX) * (double) partialTicks;
        double d1 = clientPlayer.lastTickPosY + (clientPlayer.posY - clientPlayer.lastTickPosY) * (double) partialTicks;
        double d2 = clientPlayer.lastTickPosZ + (clientPlayer.posZ - clientPlayer.lastTickPosZ) * (double) partialTicks;
        float pitch = 0f;
        float yaw = 0f;
        if (IsometricCamera.CAMERA_VECTOR != null)
        {
            Vec3d straightVec = IsometricCamera.CAMERA_VECTOR;
            Vec3d horVec = new Vec3d(straightVec.x, 0, straightVec.z);
            pitch = (float) (Math.atan2(straightVec.y, horVec.length()) / Math.PI) * 180.0f;
            yaw = (float) -((Math.atan2(straightVec.x, straightVec.z) / Math.PI) * 180.0f) - 180f;
        }

        Render render = Minecraft.getMinecraft().getRenderManager().<Entity>getEntityRenderObject(clientPlayer);
        Vec3d eyes = clientPlayer.getPositionEyes(partialTicks);
        BlockPos eyesPos = new BlockPos(eyes);
        String absCoordinates = eyesPos.getX() + " " + eyesPos.getY() + " " + eyesPos.getZ();
        FontRenderer customFontRenderer = render.getFontRendererFromRenderManager();
        float scale = -0.025F;

        if(ModEntityRenderer.customCameraDistance > 14.0)
            scale = -0.025F - (ModEntityRenderer.customCameraDistance-14.0f)/300F;
        drawCustomNameplate(customFontRenderer, absCoordinates, ((float) (clientPlayer.posX - d0)), ((float) (clientPlayer.posY - d1)) + 3.0f - scale, ((float) (clientPlayer.posZ - d2)), 0, yaw, pitch, false, false,scale);
    }


    public static void drawCustomNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking,float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking)
        {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        if (!isSneaking)
        {
            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
            GlStateManager.enableDepth();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
