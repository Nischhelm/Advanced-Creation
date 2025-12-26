package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public class GuiOverlayFreeSizeCustomButton extends GuiOverlayBaseElement
{
    protected GuiOverlayBaseElement topRightSide;
    protected GuiOverlayBaseElement bottomLeftSide;
    protected GuiOverlayBaseElement bottomRightSide;
    protected ArrayList<GuiOverlayBaseElement> middleParts = new ArrayList<>();

    protected String text;
    public int textYOffset = 0;

    public GuiOverlayFreeSizeCustomButton(String text, int x, int y, int width, int height)
    {
       super(new ResourceLocation("textures/gui/widgets.png"), x, y, 0, 66 , width, height, 0,20,0,20);
        this.text = text;
        int halfWidth = (int)Math.ceil(this.width/2.0);

        int middleLayers = (int) Math.floor((this.height -6) /14.0);
        int bottomSideHeight = this.height+3 - 14*middleLayers;

        bottomLeftSide = new GuiOverlayBaseElement(this.texture,
                this.x , this.y + this.height-bottomSideHeight,0, 66 + 20 -bottomSideHeight ,
                halfWidth, bottomSideHeight, 0,20,0,20);
        bottomRightSide = new GuiOverlayBaseElement(this.texture,
                this.x + halfWidth , this.y + this.height-bottomSideHeight,200- halfWidth, 66 + 20 -bottomSideHeight ,
                halfWidth, bottomSideHeight, 0,20,0,20);
        topRightSide= new GuiOverlayBaseElement(this.texture,
                this.x+ halfWidth , this.y,200- halfWidth, 66 ,
                halfWidth, 3, 0,20,0,20);

        bottomLeftSide.setZ(this.getZ()+1);
        bottomRightSide.setZ(this.getZ()+1);
        topRightSide.setZ(this.getZ()+1);

        addElement(bottomLeftSide);
        addElement(bottomRightSide);
        addElement(topRightSide);


        for (int i = 0; i < middleLayers; i++)
        {

            GuiOverlayBaseElement leftMiddlePart = new GuiOverlayBaseElement(this.texture,
                    this.x , this.y + 3 + i*14,0, 66 + 3 ,
                    halfWidth, 14, 0,20,0,20);
            GuiOverlayBaseElement rightMiddlePart = new GuiOverlayBaseElement(this.texture,
                    this.x + halfWidth, this.y + 3 + i*14,200 - halfWidth, 66 + 3 ,
                    halfWidth, 14, 0,20,0,20);
            addElement(rightMiddlePart);
            addElement(leftMiddlePart);
            rightMiddlePart.setZ(this.getZ()+1);
            leftMiddlePart.setZ(this.getZ()+1);
            middleParts.add(rightMiddlePart);
            middleParts.add(leftMiddlePart);
        }

        bottomLeftSide.setTimedDeactivation(true);
        bottomRightSide.setTimedDeactivation(true);
        topRightSide.setTimedDeactivation(true);
        this.setTimedDeactivation(true);
        for (GuiOverlayBaseElement middlePart: middleParts)
        {
            middlePart.setTimedDeactivation(true);
        }

    }

    @Override
    public void draw(Gui gui, int currZ, long time)
    {
        if(currZ != this.z)
            return;
        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


            int textColor = 14737632;
            if(!this.enabled)
                textColor = 10526880;

            GuiOverlayBaseElement currentSelectedItem = null;
            boolean selected = this.selected;
            if(selected)
                currentSelectedItem = this;
            if(!selected)
            {
                selected = this.bottomLeftSide.isSelected();
                if(selected)
                    currentSelectedItem = this.bottomLeftSide;
            }
            if(!selected)
            {
                selected = this.bottomRightSide.isSelected();
                if(selected)
                    currentSelectedItem = this.bottomRightSide;
            }
            if(!selected)
            {
                selected = this.topRightSide.isSelected();
                if(selected)
                    currentSelectedItem = this.topRightSide;
            }


            for (GuiOverlayBaseElement middlePart : this.middleParts)
            {
                if(!selected)
                {
                    selected = selected || middlePart.isSelected();
                    if(selected)
                        currentSelectedItem = middlePart;
                }
                else
                    break;

            }

            boolean hoveron = this.hoverOn;
            for (GuiOverlayBaseElement middlePart : this.middleParts)
            {
                hoveron = hoveron || middlePart.isHoverOn();
                if(hoveron)
                    break;
            }

            //if one is selected activate the other
            if(selected)
            {
                textColor = 16777120;
                activateTheOtherToo(currentSelectedItem);
            }
            //if one is hovered on set the other ot hover on
            else if (hoveron)
            {
                textColor = 16777120;
                this.setHoverOn(true);
                setHoverOnTheOtherToo(true);
            }
            else
            {
                this.setHoverOn(false);
                setHoverOnTheOtherToo(false);
            }

            super.draw(gui, currZ, time);

            int length = fontRenderer.getStringWidth(this.text);
            if(length > this.width)
            {


                int count = 0;
                String[] texts = this.text.split(" ");
                for (String word: texts)
                {
                    String text1 =word;
                    String text2 = word;
                    length = fontRenderer.getStringWidth(word);
                    while(length > this.width-4)
                    {
                        text1 = fontRenderer.trimStringToWidth(text2,this.width-4);
                        text2 = text2.replaceFirst(text1,"");
                        length = fontRenderer.getStringWidth(text2);
                        gui.drawCenteredString(fontRenderer, text1.trim(), this.x + this.width / 2, this.y +2 +9*count  + textYOffset, textColor);
                        count++;
                    }
                    gui.drawCenteredString(fontRenderer, text2, this.x + this.width / 2, this.y +2 +9*count  + textYOffset, textColor);

                    count++;
                }


            }
            else
                gui.drawCenteredString(fontRenderer, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2 + textYOffset, textColor);

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.popMatrix();

    }

    private void setHoverOnTheOtherToo(boolean hoverOn)
    {
        this.setSelected(false);
        bottomLeftSide.setHoverOn(hoverOn);
        bottomLeftSide.setSelected(false);
        bottomRightSide.setHoverOn(hoverOn);
        bottomRightSide.setSelected(false);
        topRightSide.setHoverOn(hoverOn);
        topRightSide.setSelected(false);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setHoverOn(hoverOn);
            middlePart.setSelected(false);
        }
    }

    private void activateTheOtherToo(GuiOverlayBaseElement currSelected)
    {
        long selectionTime = 0;
        if(this.selected)
            selectionTime = this.getSelectTime();

        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            if(middlePart.isSelected())
            {
                selectionTime = middlePart.getSelectTime();
                break;
            }
        }
        if(currSelected != this)
            this.activateTimedSelected(selectionTime);
        if(currSelected != bottomLeftSide)
            bottomLeftSide.activateTimedSelected(selectionTime);
        if(currSelected != bottomRightSide)
            bottomRightSide.activateTimedSelected(selectionTime);
        if(currSelected != topRightSide)
            topRightSide.activateTimedSelected(selectionTime);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            if(currSelected != middlePart)
                middlePart.activateTimedSelected(selectionTime);
        }
    }

    @Override
    public void setName(String name)
    {
        bottomLeftSide.setName(name);
        bottomRightSide.setName(name);
        topRightSide.setName(name);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setName(name);
        }
        super.setName(name);
    }

    @Override
    public void setVisibility(boolean visible)
    {
        bottomLeftSide.setVisibility(visible);
        bottomRightSide.setVisibility(visible);
        topRightSide.setVisibility(visible);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setVisibility(visible);
        }
        super.setVisibility(visible);
    }

    @Override
    public void setTooltip(String tooltip)
    {
        bottomLeftSide.setTooltip(tooltip);
        bottomRightSide.setTooltip(tooltip);
        topRightSide.setTooltip(tooltip);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setTooltip(tooltip);
        }
        super.setTooltip(tooltip);
    }

    @Override
    public void setHoverOn(boolean hoverOn)
    {
        bottomLeftSide.setHoverOn(hoverOn);
        bottomRightSide.setHoverOn(hoverOn);
        topRightSide.setHoverOn(hoverOn);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setHoverOn(hoverOn);
        }
        super.setHoverOn(hoverOn);
    }

    @Override
    public void setSelected(boolean selected)
    {
        bottomLeftSide.setSelected(selected);
        bottomRightSide.setSelected(selected);
        topRightSide.setSelected(selected);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setSelected(selected);
        }
        super.setSelected(selected);
    }

    @Override
    public void setEnabled(boolean selected)
    {
        bottomLeftSide.setEnabled(selected);
        bottomRightSide.setEnabled(selected);
        topRightSide.setEnabled(selected);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setEnabled(selected);
        }
        super.setEnabled(selected);
    }

    @Override
    public void setDisabledTooltip(String disabledTooltip)
    {
        bottomLeftSide.setDisabledTooltip(disabledTooltip);
        bottomRightSide.setDisabledTooltip(disabledTooltip);
        topRightSide.setDisabledTooltip(disabledTooltip);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setDisabledTooltip(disabledTooltip);
        }
        super.setDisabledTooltip(disabledTooltip);
    }

    @Override
    public void setButtonKeyToDisplayInTooltip(KeyBinding buttonKeyToDisplayInTooltip)
    {
        bottomLeftSide.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        bottomRightSide.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        topRightSide.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
        }
        super.setButtonKeyToDisplayInTooltip(buttonKeyToDisplayInTooltip);
    }

    @Override
    public void setButtonToggleKeyToDisplayInTooltip(KeyBinding buttonToggleKeyToDisplayInTooltip)
    {
        bottomLeftSide.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        bottomRightSide.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        topRightSide.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        for (GuiOverlayBaseElement middlePart : this.middleParts)
        {
            middlePart.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
        }
        super.setButtonToggleKeyToDisplayInTooltip(buttonToggleKeyToDisplayInTooltip);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }
}
