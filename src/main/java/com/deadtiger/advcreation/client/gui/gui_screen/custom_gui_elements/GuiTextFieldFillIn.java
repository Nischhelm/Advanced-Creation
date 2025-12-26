package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class GuiTextFieldFillIn extends GuiTextField
{
    protected boolean firstClick = false;
    protected boolean mouseHeld = false;
    protected FontRenderer fontRenderer;

    protected int dragDelay = 0;
    protected int dragDelayLimit = 10;
    protected int initialDragStart = 0;

    protected String defaultText = "";


    public GuiTextFieldFillIn(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height)
    {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        fontRenderer = fontrendererObj;
    }

    public GuiTextFieldFillIn(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, String defaultText)
    {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        fontRenderer = fontrendererObj;
        this.defaultText = defaultText;
        this.setText(defaultText);
    }


    @Override
    public void drawTextBox()
    {

    }


    public void drawTextBox(int mouseX, int mouseY, float partialTicks)
    {
        if ((this.getText().isEmpty() || this.getText().equals(defaultText)) && !this.isFocused())
        {
            firstClick = false;
            this.setText(defaultText);
        }

        if (!firstClick)
        {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
        }

        if (this.mouseHeld)
        {
            dragDelayLimit = 3;
            if (dragDelay >= dragDelayLimit)
            {
                int i = mouseX - this.x;

                if (this.getEnableBackgroundDrawing())
                {
                    i -= 4;
                }

                String selectionString = this.fontRenderer.trimStringToWidth(this.getText(), i);
                String cursorString = this.fontRenderer.trimStringToWidth(this.getText(), this.initialDragStart);
                setSelectionPos(this.fontRenderer.trimStringToWidth(this.getText(), i).length());
                if (this.getSelectionEnd() <= cursorString.length())
                {
                    this.setCursorPosition(cursorString.length() + 1);
                    setSelectionPos(selectionString.length());
                }
                else if (this.getSelectionEnd() > cursorString.length())
                {
                    this.setCursorPosition(cursorString.length());
                    setSelectionPos(selectionString.length() + 1);
                }
            }
            else
                dragDelay++;

        }
        if (!Mouse.isButtonDown(0))
            this.setMouseHeld(false);

        super.drawTextBox();
        GlStateManager.disableAlpha();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.colorLogicOp(5379);
    }

    public boolean isHoveredOn(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean flag = isHoveredOn(mouseX, mouseY);

        if (flag)
        {
            int i = mouseX - this.x;

            if (this.getEnableBackgroundDrawing())
            {
                i -= 4;
            }

            if (!mouseHeld)
            {
                String subString1 = this.fontRenderer.trimStringToWidth(this.getText(), i);
                int pos = subString1.length();
                String subString2 = this.getText();

                if (pos + 1 <= this.getText().length())
                    subString2 = this.getText().substring(0, pos + 1);

                int diff1 = this.fontRenderer.getStringWidth(subString1) - i;
                int diff2 = this.fontRenderer.getStringWidth(subString2) - i;

                boolean closerToSecondCharacter = false;
                if (Math.abs(diff1) >= Math.abs(diff2))
                    closerToSecondCharacter = true;

                if (closerToSecondCharacter)
                    this.setCursorPosition(subString2.length());
                else
                    this.setCursorPosition(pos);

                initialDragStart = i;
            }
            if (isFocused())
            {
                mouseHeld = true;
                firstClick = true;
                dragDelay = 0;
            }
            this.setFocused(true);

        }
        else
        {
            this.setFocused(false);
        }

        return flag;

    }

    public boolean isMouseHeld()
    {
        return mouseHeld;
    }

    public void setMouseHeld(boolean mouseHeld)
    {
        this.mouseHeld = mouseHeld;
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode)
    {
        firstClick = true;
        boolean hasSelection = false;
        if (this.getSelectionEnd() < this.getCursorPosition())
        {
            hasSelection = true;
            this.deleteFromCursor(this.getSelectionEnd() - this.getCursorPosition());
            this.setCursorPosition(this.getSelectionEnd());
        }
        else if (this.getSelectionEnd() > this.getCursorPosition())
        {
            hasSelection = true;
            this.deleteFromCursor(this.getCursorPosition() - this.getSelectionEnd());
            this.setSelectionPos(this.getCursorPosition());
        }

        if ((hasSelection && keyCode == 14))
            return false;
        else
            return super.textboxKeyTyped(typedChar, keyCode);
    }

    public boolean scrollOptions(int scroll)
    {
        return false;
    }

    public String getDefaultText()
    {
        return defaultText;
    }

    public void setDefaultText(String defaultText)
    {
        this.defaultText = defaultText;
    }

    public boolean isFirstClick()
    {
        return firstClick;
    }

    public void setFirstClick(boolean firstClick)
    {
        this.firstClick = firstClick;
    }

}
