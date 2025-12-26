package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GuiRedirectToParent extends GuiCustomButton
{
    GuiOnOffButton parent;
    public GuiRedirectToParent(GuiOnOffButton parent,int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y,29,18, buttonText);
        this.parent = parent;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiOnOffButton.CUSTOM_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 20 + i * 18, this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        int i = 0;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (parent.isMouseOver())
        {
            i = 1;
        }

        return i;
    }
}
