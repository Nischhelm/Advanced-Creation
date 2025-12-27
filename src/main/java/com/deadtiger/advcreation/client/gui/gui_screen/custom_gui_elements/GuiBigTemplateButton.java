package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.Template;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiBigTemplateButton extends GuiBaseButton
{

    public static ResourceLocation  background = new ResourceLocation(Reference.MODID,"textures/gui/template_selection_gui_overlay.png");
    
    public Template template =null;
    public int templateIndex = -1;
    
    public int textureSize = (int)Math.floor(52*(256.0/156.0));
    public float scale = 1.0F;
    
    private boolean drawNewIcon = false;

    public GuiBigTemplateButton(int buttonId, int x, int y) {
        super(buttonId,background, x, y, 50, 50, "", 50, 50,70,90,70,150);
    }
    
    public GuiBigTemplateButton(int buttonId, int x, int y, int width, int height) {
        super(buttonId,background, x, y, width, height, "", width, height,0,65,0,126);


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
            if(template != null)
            {
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
                
                ResourceLocation icon = template.getIcon(this.hovered);
                if(drawNewIcon || icon == null)
                {

                    if (this.visible)
                    {
                        mc.renderEngine.bindTexture(background);

                        float scale_up = width/54.0F ;
                        drawModalRectWithCustomSizedTexture(x,y, (int)(5*scale_up),(int)(5*scale_up),(int)(54*scale_up), (int)(54*scale_up), (int)(width*scale_up),(int)(height*scale_up));
                        mc.renderEngine.bindTexture(texture);
                        if(!overRideHover)
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

                        int u = u_unhovered;
                        int v = v_unhovered;
                        if(hovered)
                        {
                            u = u_hovered;
                            v = v_hovered;
                        }

                        drawModalRectWithCustomSizedTexture((int)((x+9)),(int)((y+3)), (int)(u*scale_up),(int)(v*scale_up), (int)(52*scale_up), (int)(52*scale_up) , (int)((width-10)*scale_up),(int)((height-10)*scale_up));
                        overRideHover = false;
                        CustomGuiUtils.DrawTemplateIcon(template, (float) (x + Math.ceil(width / 2.0)) - 1.5f, (float) ((y + Math.ceil(height / 2.0))- 45F), 0, Minecraft.getMinecraft(),scale_up);
                    }
                }
                else
                {
                    mc.renderEngine.bindTexture(icon);
                    drawModalRectWithCustomSizedTexture(x-1,y-1, 0,0, 52, 52,52,52);
                    this.overRideHover = false;
                }
           
        
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
