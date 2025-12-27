package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiPlaneButton extends GuiCustomButton
{
    protected ResourceLocation BASE_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/base_direction_selection.png");
    protected ResourceLocation ACTIVE_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/active_direction_selection.png");
    protected ResourceLocation HOVER_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/hover_direction_selection.png");
    
    int textureX = 0;
    int textureY = 0;
    
    public EnumDirectionMode plane = EnumDirectionMode.XY;
    
    public GuiPlaneButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
    
    public GuiPlaneButton(int buttonId, int x, int y,int textureX, int textureY, int widthIn, int heightIn, String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.textureX = textureX;
        this.textureY = textureY;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.hovered = checkHoverOver(mouseX, mouseY);
            ResourceLocation resLoc = BASE_TEXTURES;
            if(this.active)
                resLoc = ACTIVE_TEXTURES;
            else if(this.hovered)
                resLoc = HOVER_TEXTURES;
            
            mc.getTextureManager().bindTexture(resLoc);
            
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, textureX, textureY, this.width , this.height);
            long j1 = Minecraft.getMinecraft().world.getWorldTime();
            float f1 = (float)j1 / 100.0F;
            int k = (int)(220.0F * f1) << 24 | 1052704;
            //this.drawLine(this.x+ this.width/2,this.y+ this.height/2,this.x + this.width,this.y + this.height,-16777216);
            //this.drawVerticalLine(this.x,this.y,this.y + this.height/2,Integer.MIN_VALUE);
//            this.drawHorizontalLine(91,74,56-1,-16777216);
//            this.drawHorizontalLine(100,200,100,-16777216);
//            drawRect2(100,100,200,200,-16777216);
            //this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            
            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && hovered;
    }
    
    private boolean checkHoverOver(int mouseX, int mouseY)
    {
        int diffX = mouseX - this.x;
        int diffY = mouseY - this.y;
        
        if(mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height)
        {
            if(plane == EnumDirectionMode.XZ)
            {
                double maxY = (diffX < 18? (-0.505*diffX+8.495):(0.505*diffX-9.195));
                double minY = (diffX < 18? (0.505*diffX+9.51):(-0.505*diffX+27.2) );
                return diffY > maxY && diffY < minY;
            }
            if(plane == EnumDirectionMode.ZY)
            {
                double maxY = 0.505*diffX-0.09;
                double minY = 0.505*diffX+15.5;
                return diffY > maxY && diffY < minY;
            }
            if(plane == EnumDirectionMode.XY)
            {
                double maxY = 0.505*(this.width - diffX)-0.09;
                double minY = 0.505*(this.width - diffX)+15.5;
                return diffY > maxY && diffY < minY;
            }
        }
        
        
        return false;
        
    }
}
