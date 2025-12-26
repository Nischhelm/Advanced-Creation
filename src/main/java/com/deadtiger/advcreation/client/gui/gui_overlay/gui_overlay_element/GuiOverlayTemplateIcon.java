package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.template.Template;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiOverlayTemplateIcon extends GuiOverlayBaseElement
{
    public Template template = null;
    public int templateIndex = -1;
    public int textureSize = (int)Math.floor(52*(256.0/156.0));
    
    public GuiOverlayBaseButton button;
    
    public GuiOverlayTemplateIcon(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height)
    {
        super(texture, x, y, textureX, textureY, width, height);
        this.setZ(1);
    }
    
    @Override
    public void draw(Gui gui, int currZ, long time) {
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
            
            if(template != null)
            {
                ResourceLocation texture = template.getIcon(this.selected);
                if(texture == null)
                    CustomGuiUtils.DrawTemplateIcon(template,(float) (x+Math.ceil(width/2.0))-1.5f,(float) (y+Math.ceil(height/2.0))-9.0f,0, Minecraft.getMinecraft());
                else
                {
                    mc.renderEngine.bindTexture(texture);
                    Gui.drawModalRectWithCustomSizedTexture(x-1,y-1, 0,0, 52, 52,52,52);
                }
            }

            drawChildElements(gui,time);
        }
    }
    
    @Override
    public GuiOverlayBaseElement trySelect(int mouseX, int mouseY, int currZ, CustomGuiUtils.EnumMouseButtonClick mouseButton, long time) {
        //is not considered when try to select an element with a click
        return null;
    }
    
    @Override
    public GuiOverlayBaseElement checkHoverOn(int mouseX, int mouseY, int currZ) {
        //is not considered when try to hover on an element with a click
        return null;
    }
    
    @Override
    public void setTemplate(Template temp,int index) {
        template = temp;
        this.templateIndex = index;
    }
    
    public int getTemplateIndex() {
        return templateIndex;
    }
    
    public Template getTemplate() {
        return template;
    }
    
}
