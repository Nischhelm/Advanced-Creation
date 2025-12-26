package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiTemplateButton extends GuiBaseButton
{
    public Template template =null;
    public int templateIndex = -1;
    
    public int textureSize = (int)Math.floor(52*(256.0/156.0));
    public float scale = 1.0F;
    
    private boolean drawNewIcon = false;

    public GuiTemplateButton(int buttonId, int x, int y) {
        super(buttonId, GuiBigTemplateButton.background, x, y, 50, 50, "", 50, 50,70,90,70,150);
    }
    
    public GuiTemplateButton(int buttonId, int x, int y, int width,int height) {
        super(buttonId, GuiBigTemplateButton.background, x, y, 50, 50, "", 50, 50,70,90,70,150);

        float x_scale = width/50.0f;
        float y_scale = height/50.0f;
        
        if(x_scale > y_scale)
            this.scale = y_scale;
        else
            this.scale = x_scale;
        
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {

            if(!this.overRideHover)
            {
                //check if the mouse is above it
                if (mouseX > x &&
                        mouseX < x + width &&
                        mouseY > y &&
                        mouseY < y + height) {
                    hovered = true;
                } else {
                    hovered = false;
                }
            }
            if(template != null)
            {
                ResourceLocation icon = template.getIcon(this.hovered);

                //DEBUG
//                drawNewIcon = true;

                if(drawNewIcon || icon == null)
                {
                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                    float scale =1f;
                    CustomGuiUtils.DrawTemplateIcon(template, (float) (x + Math.ceil(width / 2.0)) - 1.5f, (float) ((y + Math.ceil(height / 2.0)) - 9.0f), 0, Minecraft.getMinecraft(),scale);
                }
                else
                {
                    mc.renderEngine.bindTexture(icon);
                    drawModalRectWithCustomSizedTexture(x-1,y-1, 0,0, 52, 52,52,52);
                    if(!TemplateManager.isAllowedMcVersion(template.getMcVersion()))
                    {
                        mc.renderEngine.bindTexture(texture);
                        if(this.isMouseOver())
                            drawModalRectWithCustomSizedTexture(x,y, 60,125, 50, 50,255,255);
                        else
                            drawModalRectWithCustomSizedTexture(x,y, 60,65, 50, 50,255,255);
                    }

                    this.overRideHover = false;
                }
           
        
            }
            else
            {
                mc.renderEngine.bindTexture(texture);
                if (this.isMouseOver())
                    drawModalRectWithCustomSizedTexture(x, y, 0, 125, 50, 50, 255, 255);
                else
                    drawModalRectWithCustomSizedTexture(x, y, 0, 65, 50, 50, 255, 255);



            }

        }
    }
    
    public Template getTemplate() {
        return template;
    }
    
    public void setTemplate(Template template,int index) {
        this.template = template;
        templateIndex = index;
    }
    
    public int getTemplateIndex() {
        return templateIndex;
    }
    
    public void setTemplateIndex(int templateIndex) {
        this.templateIndex = templateIndex;
    }
    
    
    public boolean isDrawNewIcon() {
        return drawNewIcon;
    }
    
    public void setDrawNewIcon(boolean drawNewIcon) {
        this.drawNewIcon = drawNewIcon;
    }
    
}
