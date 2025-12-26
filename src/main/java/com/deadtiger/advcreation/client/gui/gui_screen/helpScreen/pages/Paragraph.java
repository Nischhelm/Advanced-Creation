package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements.GuiAnimatedImage;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements.GuiKeyboardButton;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements.GuiTextButton;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

public class Paragraph extends GuiButton
{
    private ResourceLocation texture = null;
    private GuiAnimatedImage gifImage;
    private ArrayList<GuiKeyboardButton> keys = new ArrayList<>();
    private ArrayList<Position> plusses = new ArrayList<>();
    private ArrayList<GuiTextButton> keyDescriptions = new ArrayList<>();
    private String[][] text;
    private String title;
    private int originalHeight;
    private int offsetY;

    int maxLengthOfFirstPart;
    private ArrayList<ArrayList<String>> formatedText;


    public Paragraph(int x, int y, int width, String imageName, String fileType, KeyInformation[] keys, String title, String[][] text, IResourceManager resourceManager)
    {
        super(-1, x, y, "");
        int startContentY = 30;


        if (imageName != null)
        {
            if (fileType.equals("gif") || fileType.equals("mp4"))
            {
                String gifResource =  "textures/gui/"+ fileType+"s/"+  imageName +"."+fileType;
                this.gifImage = new GuiAnimatedImage(x + 5, startContentY, width / 2, gifResource, Minecraft.getMinecraft(),imageName);
                this.height = this.gifImage.height + 10 + 30;
            }
            else
            {
                String gifResource = "textures/gui/"+  imageName +"." + fileType;
                this.texture = new ResourceLocation(Reference.MODID, gifResource);
                this.height = (int) (((width / 2.0) / 16.0) * 9.0) + 10 + 30;
            }
        }
        else
        {
            this.height = startContentY + 40;
        }

        this.originalHeight = this.height;


        this.setWidth(width);
        this.title = title;
        this.offsetY = 0;

        int sizeKeyBoard = (int) (30 - ((760 / 38.0) - (width / 38.0)));

        if (keys != null)
        {

            int offset = sizeKeyBoard / 6;
            int posY = startContentY + offset;


            for (KeyInformation key : keys)
            {
                int posX;
                if (imageName == null)
                {
                    posX = x + offset + offset;
                }
                else
                {
                    if (width > 760 / 2)
                    {
                        posX = x + offset + width / 2 + offset;
                    }
                    else
                    {
                        posX = x + offset + width / 2 + offset;
                    }
                }


                int count = 0;
                for (KeyBinding k : key.keybindings)
                {
                    if (count > 0)
                    {
                        this.plusses.add(new Position(posX + (sizeKeyBoard / 3), posY + sizeKeyBoard / 3));
                        posX += sizeKeyBoard * 2 / 3;
                    }
                    GuiKeyboardButton button = new GuiKeyboardButton(k, posX, posY, sizeKeyBoard, sizeKeyBoard);
                    this.keys.add(button);
                    posX += button.getWidth();
                    count++;
                }

                int infoPosX = posX - Minecraft.getMinecraft().fontRenderer.getStringWidth(key.description) / 4 + 3;
//                int infoPosX = posX + 3;
                GuiTextButton newKeyInfo = new GuiTextButton(-1, infoPosX, posY, key.description);
                newKeyInfo.enabled = false;
                newKeyInfo.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(key.description);
                keyDescriptions.add(newKeyInfo);

                posY += sizeKeyBoard + offset;
            }
        }

        this.text = text;
    }

    public static class Position
    {
        int x;
        int y;

        public Position(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public void formatText(double charSize)
    {
        int textWidth = ((this.width) - 20);
        maxLengthOfFirstPart = GuiScreenTextPrinter.getMaxLengthOfFirstPartOfBody(text);
        int totalStringLimit = GuiScreenTextPrinter.calcStringLimit(textWidth, charSize);
        formatedText = GuiScreenTextPrinter.formatBody(text, maxLengthOfFirstPart, totalStringLimit);
    }

    public void initGif(Minecraft mc)
    {
        if (gifImage != null)
        {
            gifImage.initGif(mc);
            this.height = this.gifImage.height + 10 + 30;
            this.originalHeight = this.height;
        }

    }

    public void removeGif()
    {
        if (gifImage != null)
            gifImage.removeGif();
    }

    public void setFreezeEndGif(boolean freezeEnd)
    {
        if (gifImage != null)
            gifImage.setFreezeEnd(freezeEnd);
    }
    public void tickGIF()
    {
        if(gifImage != null)
        {
            gifImage.nextFrame();
        }
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            int newY = this.y + offsetY;



            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();
            int factor = scaledresolution.getScaleFactor();

            if (gifImage != null)
            {
                gifImage.y = gifImage.originalY + this.y;
                gifImage.setOffsetY(this.offsetY);
                if (isGifImageInView(height))
                {
                    gifImage.drawWindow(mc);
                    long time = System.currentTimeMillis();
                    gifImage.tick(time);
                }

            }
            else if (this.texture != null)
            {
                mc.renderEngine.bindTexture(this.texture);
                int newWidth = this.width / 2;
                int newHeight = (int) ((newWidth / 16.0) * 9.0);
                drawModalRectWithCustomSizedTexture(x - 1 + 5, y - 1 + offsetY + 30, 0, 0, newWidth, newHeight, newWidth, newHeight);
            }


            for (GuiKeyboardButton keyButton : keys)
            {
                keyButton.y = keyButton.originalY + this.y;
                keyButton.setOffsetY(offsetY);
                keyButton.drawKey(mc);
            }

            for (Position pos : plusses)
            {
                this.drawCenteredString(mc.fontRenderer, "+", pos.x, this.y + pos.y + offsetY, 14737632);
            }

            for (GuiTextButton textButton : keyDescriptions)
            {
                textButton.y = this.y + textButton.getOriginal_y();
                textButton.setOffsetY(offsetY);
                textButton.drawButton(mc, mouseX, mouseY, partialTicks);
            }

            GuiScreenTextPrinter.drawTitle(this.title, this.x + 10, newY + 10, 1.0);
            int end = GuiScreenTextPrinter.drawBody(this.x + 5, newY + this.originalHeight,formatedText,maxLengthOfFirstPart,(int) (11* (ConfigurationHandler.general.HELP_TEXT_SIZE/100.0)), 0.9*(ConfigurationHandler.general.HELP_TEXT_SIZE/100.0));
            this.height = this.originalHeight + (int)((end + 1) * (11* (ConfigurationHandler.general.HELP_TEXT_SIZE/100.0)));
            drawBorderBox(mouseX, mouseY, newY);
        }
    }

    private boolean isGifImageInView(int height)
    {
        return (gifImage.y - 1 + offsetY < height) && (gifImage.y - 1 + gifImage.height + offsetY > 0);
    }

    private void drawBorderBox(int mouseX, int mouseY, int newY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.x && mouseY >= newY && mouseX < this.x + this.width && mouseY < newY + this.height;

        int color = Color.BLACK.getRGB();
        if (this.hovered)
            color = -6250336;

        this.drawVerticalLine(x, newY, newY + this.height, color);
        this.drawVerticalLine(x + this.width - 10, newY, newY + this.height, color);
        this.drawHorizontalLine(x, x + this.width - 10, newY, color);
        this.drawHorizontalLine(x, x + this.width - 10, newY + this.height, color);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    public static class KeyInformation
    {
        public KeyBinding[] keybindings;
        public String description;
        public boolean canBeScrollUp; //if the keybind in GLFW.GLFW_KEY_UNKOWN
        public boolean canBeScrollDown; //if the keybind in GLFW.GLFW_KEY_UNKOWN

        public KeyInformation(KeyBinding[] keybindings, String description)
        {
            this(keybindings,description,false,false);
        }

        public KeyInformation(KeyBinding[] keybindings, String description, boolean canBeScrollUp, boolean canBeScrollDown)
        {
            this.keybindings = keybindings;
            this.description = description;
            this.canBeScrollDown = canBeScrollDown;
            this.canBeScrollUp = canBeScrollUp;
        }

    }
}