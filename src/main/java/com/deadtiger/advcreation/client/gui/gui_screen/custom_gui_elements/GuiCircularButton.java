package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiCircularButton extends GuiCustomButton
{
    protected ResourceLocation NORMAL_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/selectionwheelbuttons.png");
    protected ResourceLocation ACTIVE_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/selectionwheelactive.png");
    protected ResourceLocation HOVER_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/selectedwheelbuttons.png");
    
    int textureX = 0;
    int textureY = 0;
    
    int minDegrees = 0;
    int maxDegrees = 60;
    int innerRadius = 40;
    int outerRadius = 100;
    
    int circleMiddleX = 0;
    int cirleMiddleY = 0;
    
    
    public GuiCircularButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }
    
    public GuiCircularButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
    
    public GuiCircularButton(int buttonId, int x, int y,int textureX, int textureY, int widthIn, int heightIn, String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.textureX = textureX;
        this.textureY = textureY;
    }
    
    public GuiCircularButton(int buttonId, int x, int y,int textureX, int textureY, int widthIn, int heightIn, String buttonText,int minDegrees,int maxDegrees)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.textureX = textureX;
        this.textureY = textureY;
        this.minDegrees = minDegrees;
        this.maxDegrees = maxDegrees;
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
            ResourceLocation resLoc = NORMAL_TEXTURES;
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
    
    public void setCircleCoord(int x, int y)
    {
        this.circleMiddleX = x;
        this.cirleMiddleY = y;
    }
    
    private boolean checkHoverOver(int mouseX, int mouseY)
    {
        int diffX = mouseX - this.circleMiddleX;
        int diffY = -(mouseY - this.cirleMiddleY);
        double length = Math.sqrt(Math.pow(diffX,2) + Math.pow(diffY,2));

        
        double radials = Math.atan(((double)diffY)/diffX);
        
        
        if(diffX < 0)
            radials = Math.PI + radials;
        else if(diffY < 0)
            radials = Math.PI*2 + radials;
    
        double degrees = radials/Math.PI*180;
        
        int newMinDegrees = minDegrees;
        if(minDegrees > 180 && maxDegrees < 180)
        {
            newMinDegrees = minDegrees - 360;
            if(degrees > 180 )
                degrees = degrees - 360;
        }
        
        
        
        
        
        return(length > innerRadius && length < outerRadius && degrees >= newMinDegrees && degrees < maxDegrees);
        
    }
    
    public void setRadius(int inner,int outer)
    {
        this.innerRadius = inner;
        this.outerRadius = outer;
    }
    
    public void setTextures(ResourceLocation base, ResourceLocation hover,ResourceLocation active)
    {
        NORMAL_TEXTURES = base;
        HOVER_TEXTURES = hover;
        ACTIVE_TEXTURES =active;
    }
}
