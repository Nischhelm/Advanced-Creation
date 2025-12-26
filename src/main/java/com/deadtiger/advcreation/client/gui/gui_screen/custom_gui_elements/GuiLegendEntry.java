package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;


import java.awt.*;
import java.util.ArrayList;

public class GuiLegendEntry extends GuiButton
{

    private final ResourceLocation legendIndicationTexture = new ResourceLocation(Reference.MODID,"textures/gui/legend_highlight_sign.png");

    private String[][] legendText;
    private ArrayList<ArrayList<String>> formattedMessage;
    private GuiImage legendImage;
    private Color color;


    int maxLengthOfFirstPart=0;
    int totalStringLimit = 0;
    double textScale = 0.85;

    public GuiLegendEntry(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, String[][] legendText, Color color)
    {
        super(0,p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, "");
        this.legendText = legendText;
        this.color = color;
        this.legendImage = new GuiImage(legendIndicationTexture,this.x,this.y,0,0,16,16,this.color);

        formatCurrText();

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if(this.visible)
        {
            this.legendImage.drawButton(mc,mouseX,mouseY,partialTicks);
            GuiScreenTextPrinter.drawBody((double)(this.x + 16 + 5) ,(double)(this.y),formattedMessage,maxLengthOfFirstPart,(int)(11*textScale),textScale);
        }

    }

    public int getTotalHeight()
    {
        int textHeight = (int) (formattedMessage.size()*(11.0*textScale));
        if(18 > textHeight)
            return 18;
        return textHeight;
    }

    public String[][] getLegendText()
    {
        return legendText;
    }

    public void setLegendText(String[][] legendText)
    {
        this.legendText = legendText;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
        this.legendImage.setColor(this.color);
    }

    public void formatCurrText()
    {
        int textWidth = (this.width-16);
        maxLengthOfFirstPart = GuiScreenTextPrinter.getMaxLengthOfFirstPartOfBody(this.legendText);
        totalStringLimit = GuiScreenTextPrinter.calcStringLimit(textWidth, GuiScreenTextPrinter.charSize*textScale)-6;
        formattedMessage = GuiScreenTextPrinter.formatBody(this.legendText,maxLengthOfFirstPart,totalStringLimit);
    }

    public void setY(int newY)
    {
        this.y = newY;
        this.legendImage.y = newY;
    }
}
