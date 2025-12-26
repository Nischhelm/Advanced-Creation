package com.deadtiger.advcreation.client.gui.vanilla_creative_gui_overlay;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jline.utils.AttributedStyle;

public class VanillaCreativeGuiOverlay
{
    protected int remainingHighlightTicks;
    protected String notificationText = "";

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            Minecraft mc = Minecraft.getMinecraft();
            renderNotification();
            tickNotificationText();


        }
    }

    protected  void tickNotificationText()
    {
        if (this.notificationText.isEmpty())
        {
            this.remainingHighlightTicks = 0;
        }
        else if (this.remainingHighlightTicks > 0)
        {
            --this.remainingHighlightTicks;
        }
        else
        {
            this.notificationText = "";
        }
    }

    public void setNotification(String notification)
    {
        this.notificationText = notification;
        this.remainingHighlightTicks = 40;
    }

    // copied from com.deadtiger.advcreation.client.gui.gui_overlay.SelectInventoryItemGuiOverlay
    protected void renderNotification() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft mc = Minecraft.getMinecraft();
        mc.profiler.startSection("toolHighlight");
        if (this.remainingHighlightTicks > 0 && !this.notificationText.isEmpty()) {

            String name = this.notificationText;

            int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 0)
            {
                int y = res.getScaledHeight() - 59 ;
                if (!mc.playerController.shouldDrawHUD()) y += 14;
                //christiaan added the -20 to y

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                FontRenderer font = Minecraft.getMinecraft().fontRenderer;
                if (font != null)
                {
                    int x = (res.getScaledWidth() - font.getStringWidth(name)) / 2;
                    font.drawStringWithShadow(name, x, y, 0xFFFFFFFF | (opacity << 24));
                }
                else
                {
                    int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(name)) / 2;
                    mc.fontRenderer.drawStringWithShadow(name, x, y, 0xFFFFFFFF | (opacity << 24));
                }
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        mc.profiler.endSection();
    }

}
