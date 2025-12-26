package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * A button for the custom template inventory toolbar it contains a templateIcon that will be drawn inside of the button
 */
public class GuiOverlayTemplateButton extends GuiOverlayBaseButton
{
    GuiOverlayTemplateIcon templateIcon;
    public static ResourceLocation  background = new ResourceLocation(Reference.MODID,"textures/gui/template_selection_gui_overlay.png");



    public GuiOverlayTemplateButton(ResourceLocation texture, Template template,int index, int x, int y, int textureX, int textureY, int width, int height, int x_activeOffset, int y_activeOffset) {
        super(texture, x, y, textureX, textureY, width, height, x_activeOffset, y_activeOffset, x_activeOffset, y_activeOffset);
        templateIcon = new  GuiOverlayTemplateIcon(texture,x-1,y-1,0,0,52,52);
        templateIcon.setTemplate(template,index);
        elementlist.add(templateIcon);
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
            
            templateIcon.setSelected(this.selected);

            if(templateIcon.getTemplate() != null && templateIcon.getTemplate().getIcon(this.selected) != null)
            {
                mc.renderEngine.bindTexture(texture);
                if (selected)
                {
                    gui.drawTexturedModalRect(x, y, x_texture + x_activeOffset, y_texture + y_activeOffset, width, height);
                    if (!TemplateManager.isAllowedMcVersion(templateIcon.getTemplate().getMcVersion()))
                    {
                        mc.renderEngine.bindTexture(texture);
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 60, 125, 50, 50, 255, 255);
                    }
                }
                else if (hoverOn)
                {
                    gui.drawTexturedModalRect(x, y, x_texture + x_hoverOffset, y_texture + y_hoverOffset, width, height);
                    if (!TemplateManager.isAllowedMcVersion(templateIcon.getTemplate().getMcVersion()))
                    {
                        mc.renderEngine.bindTexture(texture);
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 60, 125, 50, 50, 255, 255);
                    }
                }
                else
                {
                    gui.drawTexturedModalRect(x, y, x_texture, y_texture, width, height);
                    if(!TemplateManager.isAllowedMcVersion(templateIcon.getTemplate().getMcVersion()))
                    {
                        mc.renderEngine.bindTexture(texture);
                        Gui.drawModalRectWithCustomSizedTexture(x,y, 60,65, 50, 50,255,255);
                    }
                }

            }
            else
            {
                mc.renderEngine.bindTexture(background);
                if (selected)
                    gui.drawTexturedModalRect(x, y, 0,125, width, height);
                else if (hoverOn)
                    gui.drawTexturedModalRect(x, y, 0,125, width, height);
                else
                    gui.drawTexturedModalRect(x, y, 0, 65, width, height);

            }

            drawChildElements(gui,time);

            if(templateIcon.getTemplate() != null && templateIcon.getTemplate().getIcon(this.selected) != null && !TemplateManager.isAllowedMcVersion(templateIcon.getTemplate().getMcVersion()))
            {

                mc.renderEngine.bindTexture(background);
                if (selected)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x,y, 60,125, 50, 50,255,255);
                }
                else if (hoverOn)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x,y, 60,125, 50, 50,255,255);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x,y, 60,65, 50, 50,255,255);
                }
            }
        }
    }
    
    
    @Override
    public void setTemplate(Template temp, int index) {
        templateIcon.setTemplate(temp,index);
    }
    
    public int getTemplateIndex()
    {
        return templateIcon.getTemplateIndex();
    }
    
    
}
