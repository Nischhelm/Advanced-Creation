package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayRedirectToParent;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;


public class GuiOnOffButton extends GuiCustomButton
{
    protected String textOn = "ON";
    protected String textOff = "OFF";
    public GuiRedirectToParent tab;
    public int textYOffset = 0;

    public boolean stateIsOn = true;

    public static ResourceLocation CUSTOM_TEXTURE = new ResourceLocation(Reference.MODID,"textures/gui/disable_tools.png");

    public GuiOnOffButton(int buttonId, int x, int y,boolean stateIsOn )
    {
        super(buttonId, x, y,55,20, "");

        tab = new GuiRedirectToParent(this,buttonId, this.x + 1, this.y + 1, "");
        this.stateIsOn = stateIsOn;
    }
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(CUSTOM_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0,  i * 20, this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            if(this.stateIsOn)
                this.drawCenteredString(fontRenderer, this.textOn, this.x + 24 / 2, this.y + (this.height - 8) / 2 + textYOffset,j);
            else
                this.drawCenteredString(fontRenderer, this.textOff, this.x +this.width - 28 / 2, this.y + (this.height - 8) / 2 + textYOffset, j);

            if(this.stateIsOn)
            {
                if(this.tab.x != this.x + this.width - this.tab.width -1)
                {
                    this.tab.x = this.x + this.width - this.tab.width-1;
                }
            }
            else
            {
                if(this.tab.x != this.x + 1)
                {
                    this.tab.x = (this.x +1);
                }
            }

            tab.drawButton(mc, mouseX,mouseY,partialTicks);
        }
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
       return  this.getPublicHoverState(mouseOver);
    }

    public int getPublicHoverState(boolean mouseOver)
    {
       return 0;
    }

    @Override
    public void setNameTooltip(String name, String tooltip) {
        super.setNameTooltip(name, tooltip);
        tab.setNameTooltip(name,tooltip);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        tab.enabled = (enabled);
    }


}
