package com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiTextFieldDropDown extends GuiTextFieldFillIn
{
    private ArrayList<String> options = new ArrayList<>();
    private ArrayList<String> currOptions = new ArrayList<>();
    private ArrayList<GuiTextFieldOption> fields;
    private int scrollPosition = 0;
    private int maxOptionDisplay = 3;
    
    ResourceLocation slideTabTexture = new ResourceLocation(Reference.MODID, "textures/gui/tabs.png");
    private GuiFreeButton slideTab;
    private HashMap<Integer,Integer> slideTabPostions = new HashMap<>();// pixel pos => display index
    
    
    public GuiTextFieldDropDown(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, ArrayList<String> options,ArrayList<GuiTextFieldOption> fields)
    {
        this(componentId, fontrendererObj, x, y, par5Width, par6Height,options,fields,"");
    }
    
    public GuiTextFieldDropDown(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, ArrayList<String> options,ArrayList<GuiTextFieldOption> fields,String defaultText)
    {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height,defaultText);
        this.options = options;
        this.fields = fields;
        this.slideTab = new GuiFreeButton(13,slideTabTexture,this.x + this.width-6, this.y+this.height,12,15,232,0,244,0);
    
    }
    
    public GuiTextFieldDropDown(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
    }
    
    @Override
    public void drawTextBox(int mouseX, int mouseY, float partialTicks)
    {
        
        super.drawTextBox(mouseX,mouseY, partialTicks);
    
        if(isFocused() && !fields.isEmpty())
        {
            for (int i =0; i < fields.size(); i++)
            {
                this.fields.get(i).drawTextBox();
            }
            GlStateManager.disableAlpha();
            GlStateManager.color(1f,1f,1f,1f);
            this.slideTab.drawButton(Minecraft.getMinecraft(),mouseX,mouseY,partialTicks,232,0);
        }
        
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean wasFocused = this.isFocused();
        boolean clicked = super.mouseClicked(mouseX, mouseY, mouseButton);
        
        if(clicked)
        {
            if(!wasFocused)
                firstClick = false;
            updateOptions();
            scrollPosition =  0;
            updateTextFields(scrollPosition,maxOptionDisplay);
        }
        else
        {
            if(!getText().equals(getDefaultText()))
                setFirstClick(true);
            
            clicked = false;
            
            if(wasFocused)
            {
                for(int i=0;i< fields.size();i++)
                {
                    GuiTextFieldOption option = fields.get(i);
                    clicked = option.mouseClicked(mouseX,mouseY,mouseButton);
                    if(clicked)
                    {
                        setText(option.getText());
                        setFocused(false);
                        setFirstClick(true);
                        fields.clear();
                    }
        
                }
                if(!clicked)
                {
                    setFocused(false);
                    fields.clear();
                }
            }
           
            
        }
        
        return clicked;
    }
    
    protected void updateOptions() {
        
        if(this.getText().equals(getDefaultText()))
            currOptions = optionsContaining("");
        else
            currOptions = optionsContaining(this.getText());
        
    }
    
    protected void updateTextFields(int index, int maxOptions)
    {
        fields.clear();
        int currentY = this.y;
        
        int limit =index+maxOptions;
        if(limit > currOptions.size())
            limit = currOptions.size();
            
        for(int i = index;i < limit;i++)
        {
            String option = currOptions.get(i);
            currentY = currentY + this.height;
            GuiTextFieldOption field = new GuiTextFieldOption(-1,this.fontRenderer,this.x,currentY,this.width,this.height);
            field.setText(option);
            if(!this.getText().equals(this.getDefaultText()))
            {
                int highlightIndex = option.indexOf(this.getText());
    
                field.setCursorPosition(highlightIndex);
                field.setSelectionPos(highlightIndex + this.getText().length());
    
                
            }
            fields.add(field);
          
        
        }
    
    
        //calculate the all position the slideTab can be in and the corresponding display index
        slideTabPostions.clear();
        int pages = currOptions.size() -3;
        if(pages <= 0)
            pages = 1;
    
        int pixelPerPage = (3*this.height-7) /pages;
        for(int i = 0;i < pages;i++)
        {
            slideTabPostions.put(i*pixelPerPage + this.y + this.height,i);
        }
    
        //set the slideTab at the position of the current display index
        int value = scrollPosition;
        if(slideTabPostions.containsValue( value))
        {
            int key = 0;
            for(HashMap.Entry<Integer,Integer> entry :slideTabPostions.entrySet())
            {
                if(entry.getValue() == value)
                {
                    key = entry.getKey();
                    break;
                }
            }
        
            slideTab.y = key;
        }
        
    }
    
    public ArrayList<String> getOptions()
    {
        return this.options;
    }
    
    public void addOption(String option)
    {
        this.options.add(option);
    }
    
    public ArrayList<String> optionsContaining(String subString)
    {
        return CustomGuiUtils.getListStringsContaining(subString, this.options);
    }



    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode)
    {
        boolean typed = super.textboxKeyTyped(typedChar, keyCode);
        
        if(typed || (this.isFocused() && keyCode == 14))
//        if(typed)
        {
            this.scrollPosition = 0;
            updateOptions();
            updateTextFields(this.scrollPosition,this.maxOptionDisplay);
        }
        
        return typed;
    }
    
    @Override
    public boolean scrollOptions(int scroll)
    {
        if(this.isFocused())
        {
            int t = scroll/120;
    
    
            this. scrollPosition -= t;
            if(this.scrollPosition < 0)
                this.scrollPosition =0;
            else if(this.scrollPosition + maxOptionDisplay  > currOptions.size())
            {
                if(this.currOptions.size() <  maxOptionDisplay)
                    this.scrollPosition = 0;
                else
                    this.scrollPosition = this.currOptions.size()-maxOptionDisplay;
            }
            
            
            updateTextFields(this.scrollPosition,this.maxOptionDisplay);
            return true;
        }
        return false;
    }
    
    public int getMaxOptionDisplay() {
        return maxOptionDisplay;
    }
    
    public void setMaxOptionDisplay(int maxOptionDisplay) {
        this.maxOptionDisplay = maxOptionDisplay;
    }
    
    public int getScrollPosition() {
        return scrollPosition;
    }
    
    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }
    
    public GuiFreeButton getSlideTab() {
        return slideTab;
    }
    
    public void setSlideTab(GuiFreeButton slideTab) {
        this.slideTab = slideTab;
    }
}
