package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;

;

public class GuiToggleLongTextButton extends GuiToggleButton
{


    private ArrayList<ArrayList<String>> formattedMessage;

    int baseHeight;

    int maxLengthOfFirstPart=0;
    int totalStringLimit = 0;
    double textScale = 0.85;

    public GuiToggleLongTextButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        baseHeight = heightIn;
        formatCurrText();

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if(this.height > 20)
            {
                this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, 17);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, 17);
                int restingHeight = this.height-17;
                this.drawTexturedModalRect( this.x, this.y+17, 0, 46 + i * 20+ (20-restingHeight), this.width / 2, restingHeight);
                this.drawTexturedModalRect( this.x + this.width / 2, this.y+17, 200 - this.width / 2, 46 + i * 20+(20-restingHeight), this.width / 2, restingHeight);

            }
            else
            {
                this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            }

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

            GuiScreenTextPrinter.drawBody((double)(this.x+3) ,(double)(this.y+3),formattedMessage,maxLengthOfFirstPart,(int)(11*textScale*1.3),textScale*1.3);
        }

//        drawCenteredString(p_230431_1_, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public void formatCurrText()
    {
        int textWidth = (this.width-5);
        maxLengthOfFirstPart = GuiScreenTextPrinter.getMaxLengthOfFirstPartOfBody(new String[][]  {{this.displayString}});
        totalStringLimit = GuiScreenTextPrinter.calcStringLimit(textWidth, GuiScreenTextPrinter.charSize*textScale)-6;
        formattedMessage = GuiScreenTextPrinter.formatBody(new String[][]  {{this.displayString}},maxLengthOfFirstPart,totalStringLimit);
        int lines = this.formattedMessage.size();
        this.height = (int) (this.baseHeight + (lines -1)*(10*textScale*1.3));

    }

}
