package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public abstract class AbstractPage
{
    String title;
    public ArrayList<Paragraph> paragraphs = new ArrayList<>();
    public int startX = 200;
    int offsetY = 0;
    Minecraft mc;
    public int offsetX;
    public int paraWidth;

    public AbstractPage(String title)
    {
        this.title = title;
        mc = Minecraft.getMinecraft();
        offsetX = 175;

        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int factor = scaledresolution.getScaleFactor();

        double widthscale = width/((double)mc.displayWidth);
        double scaleX = ((width-offsetX)/((double)width));
        paraWidth = (int) ((mc.displayWidth*scaleX)*widthscale);
        double enlargscale = 0.8-0.2*((1920-mc.displayWidth)/1600.0);
        startX = (int) (offsetX*enlargscale);

    }

    public void addParagraph(Paragraph paragraph)
    {
        paragraphs.add(paragraph);
    }

    public void setOffsetY(int offset)
    {
        this.offsetY = offset;
        for (Paragraph para : paragraphs)
        {
            para.setOffsetY(offset);
        }
    }

    public void drawPage(Minecraft mc, int mouseX, int mouseY, float partialTick)
    {
        GuiScreenTextPrinter.drawTitle(this.title, startX, 20 + offsetY, 1.2);
        int posY = 50;
        for (Paragraph para : paragraphs)
        {
            para.y = posY;
            para.drawButton(mc, mouseX, mouseY, partialTick);
            posY += para.height + 5;
        }
    }

    public int getUpdatedHeight()
    {
        int newHeight = 50;
        for (Paragraph para : paragraphs)
        {
            newHeight += para.height;
        }
        return newHeight;
    }

    public void initPage(Minecraft mc,double charSize)
    {
        for (Paragraph para : paragraphs)
        {
            para.initGif(mc);
            para.formatText(charSize*(ConfigurationHandler.general.HELP_TEXT_SIZE/100.0));
        }
    }

    public void removeGif()
    {
        for (Paragraph para : paragraphs)
        {
            para.removeGif();
        }
    }

    public int getParagraphPosY(int paraIndex)
    {
        if (paraIndex == 0)
            return 50;
        else if (paraIndex < paragraphs.size())
            return paragraphs.get(paraIndex).y + 50;
        else
            return 0;
    }


}
