package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiImage extends GuiButton
{
    ResourceLocation texture;
    int u;
    int v;
    float r;
    float g;
    float b;

    public GuiImage(ResourceLocation texture, int x, int y, int u, int v , int width, int height, Color color)
    {
        super(0,x, y, width,height,"");
        this.texture = texture;
        this.u = u;
        this.v = v;
        formatColor(color);
    }

    private void formatColor(Color color)
    {
        float[] comp = color.getColorComponents(null);
        this.r = comp[0];
        this.g = comp[1];
        this.b = comp[2];
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(r,g,b, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(this.x, this.y, this.u,this.v,  this.width, this.height);
        GlStateManager.color(1f,1f,1f,0f);
        GlStateManager.disableColorLogic();
    }

    public void setColor(Color color)
    {
        formatColor(color);
    }
}
