package com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.template.Template;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;

public class GuiOverlayBaseElement
{
    protected Minecraft mc = Minecraft.getMinecraft();
    protected String name;
    public int index = 0;
    //Screen parameters
    protected int width, height; //also for mainTexture
    protected int x, y;
    protected int z;    //determines the order elements are drawn/hoveredOn or selected: 0 is drawn first but selected

    //Texture parameters
    protected ResourceLocation texture;
    protected int x_texture, y_texture;
    protected int x_hoverOffset, y_hoverOffset;// offset in mainTexture coordinates for the hover mainTexture
    protected int x_activeOffset, y_activeOffset;// offset in mainTexture coordinates for the active mainTexture
    protected int x_disabledOffset = 0, y_disabledOffset = 0;// offset in mainTexture coordinates for the disabled mainTexture

    //interaction parameters
    protected boolean hoverOn;
    protected boolean selected;
    protected boolean visible = true;
    protected boolean enabled = true;
    
    //tooltip string to be shown
    protected String tooltip;
    protected String disabledTooltip = "Button is Disabled";
    protected boolean tooltipOn = false;
    protected final FontRenderer fontRenderer = mc.fontRenderer;
    protected KeyBinding buttonKeyToDisplayInTooltip = null;
    protected KeyBinding buttonToggleKeyToDisplayInTooltip = null;
    
    //any elements within this element
    //elements should be within the parent element
    protected ArrayList<GuiOverlayBaseElement> elementlist;
    
    //deactivate selection of a button
    protected long selectTime = 0;
    protected long selectDeactivationDelay = 200;
    protected boolean timedDeactivation = false;
    

    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height)
    {
        this(texture,x,y,textureX,textureY,width,height, 0,0,0,0);
    }

    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int x_activeOffset)
    {
        this(texture,x,y,textureX,textureY,width,height, x_hoverOffset,0,x_activeOffset,0);
    }
    
    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset)
    {
        this(texture,x,y,0,textureX,textureY,width,height, x_hoverOffset,y_hoverOffset,x_activeOffset,y_activeOffset);
    }

    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y, int z, int textureX, int textureY, int width, int height)
    {
        this(texture,x,y,z,textureX,textureY,width,height, 0,0,0,0);
    }
    
    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y, int z, int textureX, int textureY, int width, int height, int x_hoverOffset, int x_activeOffset)
    {
        this(texture,x,y,z,textureX,textureY,width,height, x_hoverOffset,0,x_activeOffset,0);
    }
    
    public GuiOverlayBaseElement(ResourceLocation texture, int x, int y,int z, int textureX, int textureY, int width, int height, int x_hoverOffset, int y_hoverOffset, int x_activeOffset, int y_activeOffset)
    {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.z = z;
        x_texture = textureX;
        y_texture = textureY;
        this.width = width;
        this.height = height;
        this.x_hoverOffset = x_hoverOffset;
        this.y_hoverOffset = y_hoverOffset;
        this.x_activeOffset = x_activeOffset;
        this.y_activeOffset = y_activeOffset;
        elementlist = new ArrayList<>();
        name = "unnamed!";
        tooltip = "no tip for " + name + " element";
    }
    
    public void draw(Gui gui, long time)
    {
        draw(gui,getZ(), time);
    }
    
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
            float zlevel = ObfuscationReflectionHelper.getPrivateValue(Gui.class,gui,"field_73735_i");//zLevel
            ObfuscationReflectionHelper.setPrivateValue(Gui.class,gui,(float) this.getZ(),"field_73735_i");
            if(enabled)
            {

                if (selected)
                    gui.drawTexturedModalRect(x, y, x_texture + x_activeOffset, y_texture + y_activeOffset, width, height);
                else if (hoverOn)
                    gui.drawTexturedModalRect(x, y, x_texture + x_hoverOffset, y_texture + y_hoverOffset, width, height);
                else
                    gui.drawTexturedModalRect(x, y, x_texture, y_texture, width, height);
            }
            else
                gui.drawTexturedModalRect(x, y, x_texture + x_disabledOffset, y_texture + y_disabledOffset, width, height);

            ObfuscationReflectionHelper.setPrivateValue(Gui.class,gui,zlevel,"field_73735_i");

            drawChildElements(gui, time);
        }
    }

    protected void drawChildElements(Gui gui, long time)
    {
        //draw the element with lowest z coordinate first
        for(int z_i = 0;z_i <= getHighestZ();z_i++)
        {
            //draw all the child elements
            for (GuiOverlayBaseElement element : elementlist) {
                element.draw(gui, z_i, time);
            }
        }
    }

    public  GuiOverlayBaseElement checkHoverOn(int mouseX, int mouseY)
    {
        return checkHoverOn(mouseX,mouseY,getZ());
    }
    
    /**
     * check if coordinates are within this button and return this object if it is
     * @param mouseX
     * @param mouseY
     * @return
     */
    public  GuiOverlayBaseElement checkHoverOn(int mouseX, int mouseY, int currZ)
    {
        if (visible && getZ() == currZ)
        {
            
            if (CustomGuiUtils.isWithin(mouseX, mouseY, x, y, width, height))
            {
                //try to hover on the elements with the highest z coordinate first
                for(int z_i = getHighestZ();z_i >= 0;z_i--)
                {
                    //check all the child elements if the do not return null return the child element instead of this parent element
                    for (GuiOverlayBaseElement element : elementlist)
                    {
                        GuiOverlayBaseElement newElement = element.checkHoverOn(mouseX, mouseY, z_i);
                        if (newElement != null)
                        {
                            return newElement;
                        }
                    }
                }
                this.setHoverOn(true);
                return this;
            }
        }
        return null;
    }
    
    public void resetAllHover()
    {
        //reset the condition of all elements
        for (GuiOverlayBaseElement element : elementlist)
        {
            element.resetAllHover();
        }
    
        hoverOn = false;
    }

    public GuiOverlayBaseElement trySelect(int mouseX, int mouseY, CustomGuiUtils.EnumMouseButtonClick mouseButton, long time)
    {
        return trySelect(mouseX,mouseY,getZ(),mouseButton, time);
    }
    
    /**
     * Try if the click was within this button return this object if it is
     * @param mouseX
     * @param mouseY
     * @param time
     * @return
     */
    public GuiOverlayBaseElement trySelect(int mouseX, int mouseY, int currZ, CustomGuiUtils.EnumMouseButtonClick mouseButton, long time)
    {
        if(visible && getZ() == currZ)
        {
            if(hoverOn && enabled)
            {
                //try to select the elements with the highest z coordinate first
                for(int z_i = getHighestZ();z_i >= 0;z_i--)
                {
                    //check all the child elements if the do not return null return the child element instead of this parent element
                    for (GuiOverlayBaseElement element : elementlist)
                    {
                        GuiOverlayBaseElement newElement = element.trySelect(mouseX, mouseY, z_i, mouseButton, time);
                        if (newElement != null)
                            return newElement;
                    }
                }
                
                if(!isSelected() && mouseButton == CustomGuiUtils.EnumMouseButtonClick.LEFT_CLICK)
                    activateTimedSelected( System.currentTimeMillis());

                return this;
            }
        }
        return null;
    }

    public void drawTooltip(int mouseX, int mouseY, int screenWidth, int screenHeight)
    {
        if(visible && tooltipOn)
        {
            int X_offset = -5;
            int tooltipTextWidth = fontRenderer.getStringWidth(this.tooltip);
            
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                X_offset = 13;
            }
            
            String tooltipString = tooltip;
            if(!enabled)
                tooltipString = disabledTooltip;
            
            if(buttonKeyToDisplayInTooltip != null && enabled)
                tooltipString += TextFormatting.AQUA +"<" + buttonKeyToDisplayInTooltip.getDisplayName() + ">";
    
            if(buttonToggleKeyToDisplayInTooltip != null&& enabled)
                tooltipString += TextFormatting.BLUE + "<Toggle: " + buttonToggleKeyToDisplayInTooltip.getDisplayName() + ">";
            
            CustomGuiUtils.drawHoveringText(tooltipString, mouseX+X_offset, mouseY+20,screenWidth,screenHeight,-1,fontRenderer);
        }
    }
    
    public void setVisibility(boolean visible)
    {
        this.visible = visible;
        for (GuiOverlayBaseElement element : elementlist)
        {
            element.setVisibility(visible);
        }
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public String getTooltip()
    {
        return tooltip;
    }
    
    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
        this.setTooltipOn(true);
    }

    public boolean isTooltipOn() {
        return tooltipOn;
    }
    
    public void setTooltipOn(boolean tooltipOn) {
        this.tooltipOn = tooltipOn;
    }

    public void addElement(GuiOverlayBaseElement element)
    {
        elementlist.add(element);
    }

    public boolean isHoverOn() {
        return hoverOn;
    }
    
    public void setHoverOn(boolean hoverOn) {
        this.hoverOn = hoverOn;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void activateTimedSelected(long time)
    {
        if(!isSelected())
        {
            this.setSelected(true);
            setSelectTime(time);
        }
    }
    


    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public void moveAllTo(int x,int y)
    {
        int xDiff = x - this.x ;
        int yDiff = y - this.y;
        this.x =x;
        this.y =y;
    
        for (GuiOverlayBaseElement element:elementlist)
        {
            element.moveAllTo(element.getX()+xDiff,element.getY()+yDiff);
        }
        
    }
    
    public int getHighestZ()
    {
        int maxZ = getZ();
        for (GuiOverlayBaseElement element : elementlist)
        {
            if(element.getZ() > maxZ)
            {
                maxZ = element.getHighestZ();
            }
        }
        return maxZ;
    }
    
    public int getX_texture() {
        return x_texture;
    }
    
    public void setX_texture(int x_texture) {
        this.x_texture = x_texture;
    }
    
    public int getY_texture() {
        return y_texture;
    }
    
    public void setY_texture(int y_texture) {
        this.y_texture = y_texture;
    }
    
    public ArrayList<GuiOverlayBaseElement> getElementlist() {
        return elementlist;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public long getSelectTime() {
        return selectTime;
    }
    
    public void setSelectTime(long selectTime) {
        this.selectTime = selectTime;
    }
    
    public boolean isTimedDeactivation() {
        return timedDeactivation;
    }
    
    public void setTimedDeactivation(boolean timedDeactivation) {
        this.timedDeactivation = timedDeactivation;
    }
    
    public String getText()
    {
        return null;
    }
    
    public void setText(String text)
    {
    
    }

    public KeyBinding getButtonKeyToDisplayInTooltip()
    {
        return buttonKeyToDisplayInTooltip;
    }
    
    public void setButtonKeyToDisplayInTooltip(KeyBinding buttonKeyToDisplayInTooltip)
    {
        this.buttonKeyToDisplayInTooltip = buttonKeyToDisplayInTooltip;
    }
    
    public KeyBinding getButtonToggleKeyToDisplayInTooltip()
    {
        return buttonToggleKeyToDisplayInTooltip;
    }
    
    public void setButtonToggleKeyToDisplayInTooltip(KeyBinding buttonToggleKeyToDisplayInTooltip)
    {
        this.buttonToggleKeyToDisplayInTooltip = buttonToggleKeyToDisplayInTooltip;
    }
    
    public void setTemplate(Template temp, int index)
    {}

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getDisabledTooltip()
    {
        return disabledTooltip;
    }

    public void setDisabledTooltip(String disabledTooltip)
    {
        this.disabledTooltip = disabledTooltip;
    }


    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }


    public int getX_disabledOffset()
    {
        return x_disabledOffset;
    }

    public void setX_disabledOffset(int x_disabledOffset)
    {
        this.x_disabledOffset = x_disabledOffset;
    }

    public int getY_disabledOffset()
    {
        return y_disabledOffset;
    }

    public void setY_disabledOffset(int y_disabledOffset)
    {
        this.y_disabledOffset = y_disabledOffset;
    }
}
