package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiKeyboardButton extends Gui
{
    KeyBinding KeyBind;

    protected HashMap<String, String> altDisplayName = new HashMap<>();
    protected ArrayList<String> biggerKey = new ArrayList<>();

    protected static final ResourceLocation KEY_TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/buttons.png");
    protected static final ResourceLocation MOUSE_TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/mouse.png");
    /**
     * Button width in pixels
     */
    public int width;
    /**
     * Button height in pixels
     */
    public int height;
    /**
     * The x position of this control.
     */
    public int x;
    /**
     * The y position of this control.
     */
    public int y;
    public int originalY;

    /**
     * Hides the button completely if false.
     */
    public boolean visible;

    private final int mouseKeyWidth = 50;
    private final int mouseKeyheight = 65;

    private int offsetY;

    public GuiKeyboardButton(KeyBinding keyBind, int x, int y, int width, int height)
    {
        KeyBind = keyBind;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.visible = true;
        altDisplayName.put("LMENU", "LALT");
        biggerKey.add("LMENU");
        biggerKey.add("SPACE");
        biggerKey.add("LSHIFT");
        biggerKey.add("LCONTROL");
        biggerKey.add("ESCAPE");
        biggerKey.add("RETURN");
    }

    public void drawKey(Minecraft mc)
    {
        if (this.visible)
        {
            int newY = this.y + offsetY;
            mc.renderEngine.bindTexture(KEY_TEXTURE);
            if (KeyBind.getDisplayName().equals("DOWN"))
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * (60 / 256.0)), (int) ((this.height * 4.75) * (122 / 256.0)), this.width - 1, this.height - 1, (int) (this.width * 4.75), (int) (this.height * 4.75));
            else if (KeyBind.getDisplayName().equals("LEFT"))
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * (0 / 256.0)), (int) ((this.height * 4.75) * (61 / 256.0)), this.width - 1, this.height - 1, (int) (this.width * 4.75), (int) (this.height * 4.75));
            else if (KeyBind.getDisplayName().equals("RIGHT"))
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * (0 / 256.0)), (int) ((this.height * 4.75) * (122 / 256.0)), this.width - 1, this.height - 1, (int) (this.width * 4.75), (int) (this.height * 4.75));
            else if (KeyBind.getDisplayName().equals("UP"))
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * (60 / 256.0)), (int) ((this.height * 4.75) * (61 / 256.0)), this.width - 1, this.height - 1, (int) (this.width * 4.75), (int) (this.height * 4.75));
            else if (KeyBind.getKeyCode() == -98)
            {
                mc.renderEngine.bindTexture(MOUSE_TEXTURE);
                this.width = (int) (this.height * (mouseKeyWidth / ((double) mouseKeyheight)));
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * ((mouseKeyWidth * 3) / 256.0)), (int) ((this.height * 4.75) * ((mouseKeyheight * 0) / 256.0)), this.width - 1, this.height, (int) (this.width * 4.75), (int) (this.width * 4.75));
            }
            else if (KeyBind.getKeyCode() == -100)
            {
                mc.renderEngine.bindTexture(MOUSE_TEXTURE);
                this.width = (int) (this.height * (mouseKeyWidth / ((double) mouseKeyheight)));
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * ((mouseKeyWidth * 1) / 256.0)), (int) ((this.height * 4.75) * ((mouseKeyheight * 0) / 256.0)), this.width - 1, this.height, (int) (this.width * 4.75), (int) (this.width * 4.75));

            }
            else if (KeyBind.getKeyCode() == -99)
            {
                mc.renderEngine.bindTexture(MOUSE_TEXTURE);
                this.width = (int) (this.height * (mouseKeyWidth / ((double) mouseKeyheight)));
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * ((mouseKeyWidth * 2) / 256.0)), (int) ((this.height * 4.75) * ((mouseKeyheight * 0) / 256.0)), this.width - 1, this.height, (int) (this.width * 4.75), (int) (this.width * 4.75));

            }
            else if (KeyBind.getKeyCode() == -101)
            {
                mc.renderEngine.bindTexture(MOUSE_TEXTURE);
                this.width = (int) (this.height * (mouseKeyWidth / ((double) mouseKeyheight)));
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * ((mouseKeyWidth * 0) / 256.0)), (int) ((this.height * 4.75) * (((mouseKeyheight - 12) * 1) / 256.0)), this.width - 1, this.height, (int) (this.width * 4.75), (int) (this.width * 4.75));
            }
            else if (KeyBind.getKeyCode() == -102)
            {
                mc.renderEngine.bindTexture(MOUSE_TEXTURE);
                this.width = (int) (this.height * (mouseKeyWidth / ((double) mouseKeyheight)));
                drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * ((mouseKeyWidth * 1) / 256.0)), (int) ((this.height * 4.75) * (((mouseKeyheight - 12) * 1) / 256.0)), this.width - 1, this.height, (int) (this.width * 4.75), (int) (this.width * 4.75));

            }
            else
            {
                String text = KeyBind.getDisplayName();
                int posX = x + (this.width / 2);
                if (biggerKey.contains(text))
                {
                    drawModalRectWithCustomSizedTexture(x, newY, (int) ((this.width * 4.75) * (61 / 256.0)), (int) ((this.height * 4.75) * (0 / 256.0)), (int) (this.width * (78.0 / 54)), this.height, (int) (this.width * 4.75), (int) (this.height * 4.75));
                    posX = (int) (x + ((this.width * (78.0 / 54)) / 2));
                }
                else
                    drawModalRectWithCustomSizedTexture(x, newY, 0, 0, this.width, this.height, (int) (this.width * 4.75), (int) (this.height * 4.75));

                double scaleText = this.width / 30.0;

                if (altDisplayName.containsKey(text))
                    text = altDisplayName.get(text);

                GuiScreenTextPrinter.drawText(text, posX, newY + (this.height / 3), 14737632, scaleText, true);
            }
        }
    }

    public int getWidth()
    {
        String text = KeyBind.getDisplayName();
        if (biggerKey.contains(text))
            return (int) (this.width * (78.0 / 54));
        else
            return this.width;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }
}
