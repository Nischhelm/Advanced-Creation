package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.gui_screen.warningScreen.GuiWarningScreenFactory;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.*;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplateInventoryScreenSimple;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class SelectTemplateGuiOverlay extends AbstractGuiOverlay {
    private final int buttonSize = 20;
    private final int numberOfButtons = 9;

    private ResourceLocation inventoryButtonTexture;
    private ResourceLocation cancelPlacementButtonTexture;
    
    private GuiOverlayBaseElement templateBackground1;
    private GuiOverlayBaseElement templateBackground2;
    private GuiOverlayBaseElement templateBackground3;
    private GuiOverlayBaseElement templateBackground4;
    
    private GuiOverlayTemplateButton templateButton1;
    private GuiOverlayTemplateButton templateButton2;
    private GuiOverlayTemplateButton templateButton3;
    private GuiOverlayTemplateButton templateButton4;

    private GuiOverlayCustomWindow window = null;
    private GuiOverlayText modeText = null;
    private GuiOverlayBaseButton inventoryButton;
    public  GuiOverlayBaseButton cancelPlacement = null;

    private final int barTotalLength = 58*4 - 5*2 -2 -1;
    private int barLength = 0;
    private final int normalColor = 0xFF00AA00;
    private int selectedColor = normalColor;
    private final int cancelColor = 0xFFAA0000;

    public long lastTextUpdate = 0;
    public final long textUpdateRate = 3000;

    private final int windowYOffset =  - 59 - 16;
    private final int modetextYOffset = - 59 + 2 - (int)Math.ceil(15/2.0) -5;
    private final double textScale = 0.76;

    public boolean holdMessage = false;

    @Override
    protected void initGuiOverlay() {
        elementlist.clear();
    
        mainTexture = new ResourceLocation(Reference.MODID, "textures/gui/template_selection_gui_overlay.png");
        inventoryButtonTexture = new ResourceLocation(  Reference.MODID,"textures/gui/template_inventory_button_done.png");
        cancelPlacementButtonTexture = new ResourceLocation(Reference.MODID, "textures/gui/undo_gui_overlay.png");

        GuiTemplaceInventoryScreenFunctionality.setSelected_index(-1);
        PlaceTemplateMode.clearMouseOffset();
        updateToolModeIndication(AdvCreation.getMode());
        PlaceTemplateMode.updateCoordInfoDisplay();

        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        
        templateBackground1 = new GuiOverlayBaseElement(mainTexture, ((int) Math.ceil(width / 2.0)) - 115, height - 59, 0, 0, 58, 59);
        templateBackground1.setName("templateBackground1");

        templateButton1 = new GuiOverlayTemplateButton(mainTexture,null,-1, ((int) Math.ceil(width / 2.0)) - 115 + 5, height - 59 + 5, 0, 65, 50, 50, 0, 60);
        templateButton1.setName("templateButton1");
        templateButton1.index = 36;

        elementlist.add(templateBackground1);
        templateBackground1.addElement(templateButton1);

        templateBackground2 = new GuiOverlayBaseElement(mainTexture, ((int) Math.ceil(width / 2.0)) - 115 + 58, height - 59, 58, 0, 57, 59);

        templateButton2 = new GuiOverlayTemplateButton(mainTexture, null,-1,((int) Math.ceil(width / 2.0)) - 115 + 58 + 4, height - 59 + 5, 0, 65, 50, 50, 0, 60);
        templateButton2.setName("templateButton2");
        templateButton2.index = 36;

        elementlist.add(templateBackground2);
        templateBackground2.addElement(templateButton2);
        
        templateBackground3 = new GuiOverlayBaseElement(mainTexture, ((int) Math.ceil(width / 2.0)) - 115 + 58 + 57, height - 59, 58 + 57, 0, 57, 59);
        templateBackground3.setName("templateBackground3");

        templateButton3 = new GuiOverlayTemplateButton(mainTexture,null,-1, ((int) Math.ceil(width / 2.0)) - 115 + 58 + 57 + 4, height - 59 + 5, 0, 65, 50, 50, 0, 60);
        templateButton3.setName("templateButton3");
        templateButton3.index = 36;

        elementlist.add(templateBackground3);
        templateBackground3.addElement(templateButton3);

        templateBackground4 = new GuiOverlayBaseElement(mainTexture, ((int) Math.ceil(width / 2.0)) - 115 + 58 + 57 + 57, height - 59, 58 + 57 + 57, 0, 58, 59);
        templateBackground4.setName("templateBackground4");

        templateButton4 = new GuiOverlayTemplateButton(mainTexture,null,-1,((int) Math.ceil(width / 2.0)) - 115 + 58 + 57 + 57 + 4, height - 59 + 5, 0, 65, 50, 50, 0, 60);
        templateButton4.setName("templateButton4");
        templateButton4.index = 36;

        elementlist.add(templateBackground4);
        templateBackground4.addElement(templateButton4);

        int windowBaseX = ((int)Math.ceil(width/2.0));

        window = new GuiOverlayCustomWindow(windowBaseX - 115, height + windowYOffset, 58 * 4 - 2, 16);
        modeText = new GuiOverlayText("", windowBaseX, height + modetextYOffset, textScale);

        cancelPlacement = new GuiOverlayBaseButton(cancelPlacementButtonTexture,windowBaseX - 115 + 58 * 4 - 2 -(buttonSize),height + windowYOffset  ,2,182,16,16,40,20);
        cancelPlacement.setName("cancelPlacement");
        cancelPlacement.setTooltip("Stop loading current placement");
        cancelPlacement.setButtonKeyToDisplayInTooltip(Keybindings.CANCEL_TEMPLATE_CREATION.getKeybind());
        cancelPlacement.setTimedDeactivation(true);

        window.addElement(cancelPlacement);
        this.elementlist.add(window);

        inventoryButton = new GuiOverlayBaseButton(inventoryButtonTexture,(int) Math.ceil(width / 2.0) - 115 -30  , height -30,0,0,30,30 ,20,20 ,true,20,20);
        inventoryButton.setName("inventory");
        inventoryButton.setTooltip("Open Inventory");
        inventoryButton.setButtonKeyToDisplayInTooltip(mc.gameSettings.keyBindInventory);
        inventoryButton.setVisibility(false);
        inventoryButton.setTimedDeactivation(true);

        elementlist.add(inventoryButton);

        if (AdvCreation.getMode() == EnumMainMode.PLACE)
            setVisibility(true);
        else
            setVisibility(false);

        super.initGuiOverlay();
    }



    @Override
    public void setVisibility(boolean visible)
    {
        super.setVisibility(visible);
        if(window != null)
            window.setVisibility(visible);
    }

    @Override
    protected void preDrawElements(int x_resized, int y_resized, long time)
    {
        
        for(GuiOverlayBaseElement el: elementlist)
        {
            if(!el.getElementlist().isEmpty() &&  ( el.getElementlist().get(0) instanceof GuiOverlayTemplateButton))
            {
                if(GuiTemplaceInventoryScreenFunctionality.selected_index == ((GuiOverlayTemplateButton)el.getElementlist().get(0)).getTemplateIndex())
                    el.getElementlist().get(0).setSelected(true);
                else
                    el.getElementlist().get(0).setSelected(false);
            }
        }

        if( time - lastTextUpdate > textUpdateRate)
            updateToolModeIndication(AdvCreation.getMode(),true);
        
        super.preDrawElements(x_resized, y_resized, time);
    }
    
    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if(templateBackground1.getX() != width/2-115)
        {
            templateBackground1.moveAllTo(((int)Math.ceil(width/2.0))-115,height-59);
            templateBackground2.moveAllTo(((int)Math.ceil(width/2.0))-115 + 58,height-59);
            templateBackground3.moveAllTo(((int)Math.ceil(width/2.0))-115+ 58+ 57,height-59);
            templateBackground4.moveAllTo(((int)Math.ceil(width/2.0))-115+ 58+ 57+ 57,height-59);
        }
        int currDist = modeText.getX() - window.getX();
        window.moveAllTo((int) Math.ceil(width / 2.0) - 115 ,height + windowYOffset);

        modeText.moveAllTo(window.getX() + currDist,height - 59 + 2 - (int)Math.ceil(15/2.0) -5);
        inventoryButton.moveAllTo((int) Math.ceil(width / 2.0) - 115 -30 , height -30);

        if(NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
            cancelPlacement.setVisibility(true);
        else
            cancelPlacement.setVisibility(false);
        
        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawTooltips(int x_resized, int y_resized, long time) {
        if(AdvCreation.mode == EnumMainMode.PLACE)
        {
            int left = window.getX() + 5 ;
            int top = window.getY()+5;
            window.draw(this,time);
            drawGradientRect(left-1,top-1,left + barTotalLength + 2,top + 7,0xFF2A2A2A, 0xFF151515);
            drawRect( left  ,top ,left + barLength,top+6,selectedColor);
            modeText.draw(this,time);
            if(cancelPlacement.isVisible())
            {
//                RenderSystem.enableAlphaTest();
                cancelPlacement.draw(this,time);
//                RenderSystem.disableAlphaTest();
            }
            super.preDrawTooltips(x_resized, y_resized, time);
        }


    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(element != null )
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
        
            int selected_index = -1;
            boolean selectionChanged = false;
            if(element.equals(templateButton1))
            {
                selected_index = templateButton1.getTemplateIndex();
                selectionChanged = true;
            }
            else if(element.equals(templateButton2))
            {
                selected_index = templateButton2.getTemplateIndex();
                selectionChanged = true;
            }
            else if(element.equals(templateButton3))
            {
                selected_index = templateButton3.getTemplateIndex();
                selectionChanged = true;
            }
            else if(element.equals(templateButton4))
            {
                selected_index = templateButton4.getTemplateIndex();
                selectionChanged = true;
            }
            else if(element.equals(inventoryButton))
            {
                mc.getTutorial().openInventory();
                mc.displayGuiScreen(new GuiTemplateInventoryScreenSimple());
            }
            else if(element != null && element.getName().equals("cancelPlacement"))
            {
                super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
//                NetworkPlaceBlockListFormatter.cancelPlacement();
                mc.displayGuiScreen(GuiWarningScreenFactory.factory.createCancelPlacementWarningScreen());
            }

            if(selectionChanged && GuiTemplaceInventoryScreenFunctionality.selected_index != selected_index)
            {
                GuiTemplaceInventoryScreenFunctionality.setSelected_index(selected_index);
                PlaceTemplateMode.clearMouseOffset();
            }
          
            updateToolModeIndication(AdvCreation.getMode());
        }
        PlaceTemplateMode.updateCoordInfoDisplay();

    }
    
    @Override
    public void updateChangedMainMode(EnumMainMode mode) {
        this.setVisibility(mode == EnumMainMode.PLACE);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode,boolean skipable)
    {
        if(!skipable || !holdMessage)
            updateToolModeIndication(mode,System.currentTimeMillis(),"",false,false, 0, false);
    }


    @Override
    public void updateToolModeIndication(EnumMainMode mode)
    {
        this.updateToolModeIndication(mode,"",false,false);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText, boolean fullbar, boolean secondColor, int overwriteColor)
    {
        this.updateToolModeIndication(mode, System.currentTimeMillis(), overwriteText, fullbar, secondColor, overwriteColor,false);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText, boolean fullbar, boolean secondColor, int overwriteColor, boolean holdMessage)
    {
        updateToolModeIndication(mode,System.currentTimeMillis(),overwriteText,fullbar,secondColor,overwriteColor,holdMessage);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText,boolean fullbar ,boolean secondColor )
    {
        updateToolModeIndication(mode,System.currentTimeMillis(),overwriteText,fullbar,secondColor,0,false);
    }

    public void updateToolModeIndication(EnumMainMode mode,long time,String overwriteText,boolean fullbar ,boolean secondColor, int overwriteColor,boolean holdMessage)
    {
        this.holdMessage = holdMessage;
        String newComment = overwriteText;
        if(overwriteText.length() == 0)
        {
            selectedColor = normalColor;
            barLength = 0;

            if(mode.equals(EnumMainMode.PLACE))
            {
                int select_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
                newComment = "No Template Selected" ;
                if(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index )
                {
                    String filename = TemplateManager.FILENAME_LIST.get(select_index);
                    newComment = "Place " + filename ;

                    if(PlaceTemplateMode.HOLD_PREVIEW)
                        newComment += " Detached";
                    else
                        newComment += " Attached";
                }
            }
        }
        else
        {
            if(secondColor)
                selectedColor = applyColor(overwriteColor, cancelColor);
            else
                selectedColor = applyColor(overwriteColor, cancelColor);

            if(fullbar)
                barLength = barTotalLength;
//            else
//                barLength = 0;
        }


        Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if(modeText != null)
        {
            modeText.setX((int)Math.ceil(width/2) - (int) Math.ceil((mc.fontRenderer.getStringWidth(newComment)*textScale)/2.0));
            modeText.setText(newComment);
        }
        lastTextUpdate = time;
    }


    public int getTemplateIndexFromTemplateButton(int index)
    {
        if(index == 0)
            return templateButton1.getTemplateIndex();
        else if(index == 1)
            return templateButton2.getTemplateIndex();
        else if(index == 2)
            return templateButton3.getTemplateIndex();
        else if(index == 3)
            return templateButton4.getTemplateIndex();
        return -1;
    }

    public void setTemplateOfTemplateButton(int buttonIndex, Template template, int templateIndex)
    {
        if(buttonIndex == 0)
            templateButton1.setTemplate(template,templateIndex);
        else if(buttonIndex == 1)
            templateButton2.setTemplate(template,templateIndex);
        else if(buttonIndex == 2)
            templateButton3.setTemplate(template,templateIndex);
        else if(buttonIndex == 3)
            templateButton4.setTemplate(template,templateIndex);

    }

    @Override
    public void setRelativeBarLength(int fraction, int max)
    {
        barLength = (int) ((barTotalLength/(double)max)*fraction);
    }

    @Override
    public void resetBarLength()
    {
        barLength = 0;
    }


}
