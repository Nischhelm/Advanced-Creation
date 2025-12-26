package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.GuiTextFieldFillIn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class GuiTextFieldTextBox extends GuiTextFieldFillIn
{



    public GuiTextFieldTextBox(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.setMaxStringLength((par5Width/6) *((par6Height/10)-1));
    }

    public GuiTextFieldTextBox(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, String defaultText) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height, defaultText);
        this.setMaxStringLength((par5Width/6) *((par6Height/10)-1));
    }

    @Override
    public void drawTextBox(int mouseX,int mouseY,float partialTicks)
    {
        if((this.getText().isEmpty() || this.getText().equals(this.defaultText) )&& !this.isFocused())
        {
            firstClick = false;
            this.setText(this.defaultText);
        }

        if(!firstClick)
        {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
        }

//        if(this.mouseHeld)
//        {
//            dragDelayLimit = 3;
//            if(dragDelay >= dragDelayLimit)
//            {
//                int i = mouseX - this.x;
//
//                if (this.getEnableBackgroundDrawing())
//                {
//                    i -= 4;
//                }
//
//                String selectionString = this.fontRenderer.trimStringToWidth(this.getText(), i);
//                String cursorString = this.fontRenderer.trimStringToWidth(this.getText(), this.initialDragStart);
//                setSelectionPos(this.fontRenderer.trimStringToWidth(this.getText(), i).length() );
//                if(this.getSelectionEnd() <= cursorString.length())
//                {
//                    this.setCursorPosition(cursorString.length()+1);
//                    setSelectionPos(selectionString.length() );
//                }
//                else if(this.getSelectionEnd() > cursorString.length())
//                {
//                    this.setCursorPosition(cursorString.length());
//                    setSelectionPos(selectionString.length() +1 );
//                }
//
//                System.out.println("[holdMouse] textbutton clicked mouseheld "+ mouseHeld + " firstclick " + firstClick + " selectionPos " + this.getSelectionEnd() + " cursorPos " + this.getCursorPosition());
//            }
//            else
//                dragDelay++;
//
//        }
        if(!Mouse.isButtonDown(0))
            this.setMouseHeld(false);

        if (this.getEnableBackgroundDrawing())
        {
            drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }

        int color = 14737632;
        int curPos = this.getCursorPosition();
        int selEnd = this.getSelectionEnd();
        String newText = this.getText();
//                this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
        boolean flag = curPos >= 0 && curPos <= newText.length();
        boolean flag1 = this.isFocused() && flag;
//        int l = this.getEnableBackgroundDrawing() ? this.x + 4 : this.x;
//        int i1 = this.getEnableBackgroundDrawing() ? this.y + (this.height - 8) / 2 : this.y;
        int l = this.getEnableBackgroundDrawing() ? this.x + 4 : this.x;
        int i1 = this.y + 4;
        int j1 = l;

        if (selEnd > newText.length())
        {
            selEnd = newText.length();
        }

        ArrayList<String> textBoxText = new ArrayList<>();
        int totalLength = 0;
        String line = this.fontRenderer.trimStringToWidth(this.getText(), this.getWidth());
        textBoxText.add(line);
        while(this.getText().length() > totalLength)
        {
            totalLength += line.length();
            line = this.fontRenderer.trimStringToWidth(this.getText().substring(totalLength), this.getWidth());
            if(!line.isEmpty())
                textBoxText.add(line);

        }
        boolean flag3 = this.getCursorPosition() < this.getText().length() || this.getText().length() >= this.getMaxStringLength();

        int lineCount = 0;
        int charCount = 0;
        for(String newLine: textBoxText)
        {

            if (!newLine.isEmpty())
            {
                String s1 =newLine;
                if(flag && ((charCount +newLine.length()) >= curPos))
                {
                    s1 = newLine.substring(0, curPos-charCount);

                }

                j1 = this.fontRenderer.drawStringWithShadow(s1, (float)l, (float)(i1 + lineCount*10), color);
                boolean flag2 = (this.getCursorPosition() < charCount + newLine.length() && this.getCursorPosition() > charCount ) || this.getText().length() >= this.getMaxStringLength();
                int k1 = j1;

                if (!flag)
                {
                    k1 = curPos > 0 ? l + this.width : l;
                }
                else if (flag2)
                {
                    k1 = j1 - 1;
                    --j1;
                }

                if (!newLine.isEmpty() && flag && (curPos-charCount) < newLine.length())
                {
                    j1 = this.fontRenderer.drawStringWithShadow(newLine.substring(curPos-charCount), (float)j1,(float)(i1 + lineCount*10), color);
                    flag = false;
                }


                    if (flag1) {
                        if (flag2)
                        {
                                Gui.drawRect(k1, (i1 + lineCount * 10) - 1, k1 + 1, (i1 + lineCount * 10) + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);



                        } else if(!flag3)
                            {
                            if (lineCount == (textBoxText.size() - 1)) {
                                this.fontRenderer.drawStringWithShadow("_", (float) k1, (float) (i1 + lineCount * 10), color);
                            }

                    }
                 }

            }





            charCount += newLine.length();
            lineCount++;
        }



//        if (k != j)
//        {
//            int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
//            this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
//        }


        GlStateManager.disableAlpha();
        GlStateManager.color(1f,1f,1f,1f);
        GlStateManager.colorLogicOp(5379);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean flag = isHoveredOn(mouseX,mouseY);

        if(flag)
        {

            int i = mouseX - this.x + (((mouseY - (this.y+5))/10))*(this.width-11);
//            int i = mouseX - this.x;
            if (this.getEnableBackgroundDrawing())
            {
                i -= 4;
            }

            if(!mouseHeld)
            {
                String subString1 = this.fontRenderer.trimStringToWidth(this.getText(), i);
                int pos = subString1.length();
                String subString2 = this.getText();

                if(  pos+1 <= this.getText().length() )
                    subString2 = this.getText().substring(0,pos+1);

                int diff1 = this.fontRenderer.getStringWidth(subString1)-i;
                int diff2 = this.fontRenderer.getStringWidth(subString2)-i;

                boolean closerToSecondCharacter = false;
                if(Math.abs(diff1) >= Math.abs(diff2))
                    closerToSecondCharacter = true;

                if(closerToSecondCharacter)
                    this.setCursorPosition(subString2.length());
                else
                    this.setCursorPosition(pos);

                initialDragStart = i;
            }
            if(isFocused())
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


//        boolean clicked = super.mouseClicked(mouseX, mouseY, mouseButton);


        return flag;

    }


    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode)
    {
        return super.textboxKeyTyped(typedChar, keyCode);
    }
}
