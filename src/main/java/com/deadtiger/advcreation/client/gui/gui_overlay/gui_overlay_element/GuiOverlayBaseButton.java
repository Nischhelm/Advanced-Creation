package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiOverlayBaseButton extends GuiOverlayBaseElement
{

    private boolean scaleTexture = false;
    private int textureWidth = 0;
    private int textureHeight = 0;
    private int textureSide = 0;

    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height)
    {
        super(texture,x,y,textureX,textureY,width,height, 0,0,0,0);
    }
    
    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int x_activeOffset)
    {
        super(texture, x, y, textureX, textureY, width, height, x_hoverOffset, x_activeOffset);
    }
    
    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset)
    {
        super(texture, x, y, textureX, textureY, width, height, x_hoverOffset, y_hoverOffset, x_activeOffset, y_activeOffset);
    }

    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y,int z, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset)
    {
        super(texture,x,y,z,textureX,textureY,width,height, x_hoverOffset,y_hoverOffset,x_activeOffset,y_activeOffset);
    }
    
    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int z, int textureX, int textureY, int width, int height)
    {
        this(texture,x,y,z,textureX,textureY,width,height, 0,0,0,0);
    }
    
    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int z, int textureX, int textureY, int width, int height, int x_hoverOffset, int x_activeOffset)
    {
        this(texture,x,y,z,textureX,textureY,width,height, x_hoverOffset,0,x_activeOffset,0);
    }

    public GuiOverlayBaseButton(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int x_activeOffset,boolean drawScaled,int textureWidth, int textureHeight)
    {
        super(texture, x, y, textureX, textureY, width, height, x_hoverOffset, x_activeOffset);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scaleTexture= drawScaled;
        double diagonalTexture = Math.sqrt(Math.pow(textureWidth,2) + Math.pow(textureHeight,2));
        double diagonalScreen = Math.sqrt(Math.pow(width,2) + Math.pow(height,2));
        double scale = (diagonalScreen/diagonalTexture);
        this.textureSide = (int)(256*(scale));
        this.x_hoverOffset =(int) (x_hoverOffset*scale);
        this.x_activeOffset = (int) (x_activeOffset*scale);
    }


    @Override
    public void draw(Gui gui, int currZ, long time)
    {
        if (visible && getZ() == currZ)
        {
            //if the timed deactivation is on then turn selected off after that delay
            if(timedDeactivation)
            {
                if(selectTime !=0)
                {
                    if(time-selectTime > selectDeactivationDelay)
                    {
                        setSelected(false);
                        setSelectTime(0);
                    }
                }
                else
                    setSelected(false);

            }

            mc.renderEngine.bindTexture(texture);
            if(scaleTexture)
            {
                if(!enabled)
                    Gui.drawModalRectWithCustomSizedTexture(x,y,x_texture + x_disabledOffset, y_texture + y_disabledOffset,width, height, textureSide,textureSide);
                else
                {
                    if (selected)
                        Gui.drawModalRectWithCustomSizedTexture(x, y, x_texture + x_activeOffset, y_texture + y_activeOffset, width, height, textureSide, textureSide);
                    else if (hoverOn)
                        Gui.drawModalRectWithCustomSizedTexture(x, y, x_texture + x_activeOffset, y_texture + y_activeOffset, width, height, textureSide, textureSide);
                    else
                        Gui.drawModalRectWithCustomSizedTexture(x, y, x_texture, y_texture, width, height, textureSide, textureSide);
                }
            }
            else
            {
                if(!enabled)
                    gui.drawTexturedModalRect(x, y, x_texture + x_disabledOffset, y_texture + y_disabledOffset, width, height);
                else
                {
                    if (selected)
                        gui.drawTexturedModalRect(x, y, x_texture + x_activeOffset, y_texture + y_activeOffset, width, height);
                    else if (hoverOn)
                        gui.drawTexturedModalRect(x, y, x_texture + x_hoverOffset, y_texture + y_hoverOffset, width, height);
                    else
                        gui.drawTexturedModalRect(x, y, x_texture, y_texture, width, height);
                }
            }

            drawChildElements(gui,time);
        }
    }
}
