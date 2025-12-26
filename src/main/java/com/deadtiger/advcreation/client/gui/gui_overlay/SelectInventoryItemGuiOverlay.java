package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.CopyPasteToolMode;
import com.deadtiger.advcreation.build_mode.tool_mode.MoveToolMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.build_template.TemplateBuildingMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.*;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_screen.warningScreen.GuiWarningScreenFactory;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class SelectInventoryItemGuiOverlay extends AbstractGuiOverlay
{

    private ResourceLocation inventoryButtonTexture;
    private ResourceLocation cancelPlacementButtonTexture;

    private final int buttonSize = 20;
    private final int numberOfButtons = 9;

    public  GuiOverlayBaseButton inventoryButton;
    
    public  GuiOverlayBaseElement crosshair = null;
    public  GuiOverlayBaseElement zoomIcon = null;
    public  GuiOverlayBaseButton cancelPlacement = null;

    public  GuiOverlayCustomWindow window = null;
    public  GuiOverlayText modeText = null;
    public  long lastTextUpdate = 0;
    public  long textUpdateRate = 1000;

    public  int barTotalLength = 173;
    public  int barLength = 0;
    public  int normalColor = 0xFF00AA00;
    public  int selectedColor = normalColor;
    public  int cancelColor = 0xFFAA0000;

    public boolean holdMessage = false;

    private static final int WHITE = 0xFFFFFF;

    protected int remainingHighlightTicks;
    protected ItemStack highlightingItemStack = ItemStack.EMPTY;

    @Override
    protected void initGuiOverlay() {
        elementlist.clear();
    
        mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/widgets.png");
        inventoryButtonTexture = new ResourceLocation(  Reference.MODID,"textures/gui/template_inventory_button_done.png");
        cancelPlacementButtonTexture = new ResourceLocation(Reference.MODID, "textures/gui/undo_gui_overlay.png");


        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();


        // the inventory slot buttons in the window
        for(int i = 0; i < 9;i++)
        {
            GuiOverlayBaseButton button = new GuiOverlayInvisibleButton(mainTexture,((int)Math.ceil(width/2.0)) +1 - 92 + (buttonSize)*i,height - buttonSize -2 ,90,23,buttonSize,buttonSize);
            button.setName("" + i);
            elementlist.add(button);
            button.index = 35;
        }

        //zoomIcon that indicates you initial position of the mouse cursor when you alt in certain modes
        zoomIcon = new GuiOverlayBaseElement(new ResourceLocation(Reference.MODID,"textures/gui/magnifier.png"),0, 0,0, 0, 12,12);
        zoomIcon.setVisibility(false);
        setZoomIcon(width/2,height/2);
        
        //crosshair that indicates you initial position of the mouse cursor when you alt in certain modes
        crosshair = new GuiOverlayBaseElement(new ResourceLocation(Reference.MODID,"textures/gui/crosshair.png"),0, 0,0, 0, 15,15);
        crosshair.setVisibility(false);
        setCrossHair(width/2,height/2);

        int windowBaseY = height - buttonSize -2;
        int windowBaseX = ((int)Math.ceil(width/2.0));
        
        window = new GuiOverlayCustomWindow(windowBaseX - 92,windowBaseY - buttonSize, buttonSize*9 +4 ,buttonSize);
        modeText = new GuiOverlayText("",windowBaseX,windowBaseY - (int)Math.ceil(buttonSize/2.0)-4,1.0);

        cancelPlacement = new GuiOverlayBaseButton(cancelPlacementButtonTexture,windowBaseX- 92 +buttonSize*9 -(buttonSize),windowBaseY - buttonSize ,0,180,buttonSize,buttonSize,40,20);
        cancelPlacement.setName("cancelPlacement");
        cancelPlacement.setTooltip("Stop loading current placement");
        cancelPlacement.setButtonKeyToDisplayInTooltip(Keybindings.CANCEL_TEMPLATE_CREATION.getKeybind());
        cancelPlacement.setTimedDeactivation(true);
        cancelPlacement.setVisibility(false);

        window.addElement(cancelPlacement);

        elementlist.add(window);
        inventoryButton = new GuiOverlayBaseButton(inventoryButtonTexture,((int)Math.ceil(width/2.0)) -1 - 92 - (buttonSize), height - buttonSize -1, 0,0, 20,20,20,20);
        inventoryButton.setName("inventory");
        inventoryButton.setTooltip("Open Inventory");
        inventoryButton.setButtonKeyToDisplayInTooltip(mc.gameSettings.keyBindInventory);
        inventoryButton.setTimedDeactivation(true);

        elementlist.add(inventoryButton);

        super.initGuiOverlay();
    }

    @Override
    protected void preDrawElements(int x_resized, int y_resized, long time)
    {
        for(int i = 0;i< numberOfButtons;i++)
        {
            GuiOverlayBaseElement element = elementlist.get(i);
            if(element != null)
            {
                element.setSelected(false);
            }
        }
        
        if (mc.getRenderViewEntity() instanceof EntityPlayer)
            elementlist.get(((EntityPlayer) mc.getRenderViewEntity()).inventory.currentItem);

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


        for (int i = 0;i<9;i++)
        {
            GuiOverlayBaseButton button = (GuiOverlayBaseButton)elementlist.get(i);
            if(button.getY() != height - buttonSize -2)
            {
                button.moveAllTo(((int)Math.ceil(width/2.0))+1 - 92 + (buttonSize)*i,height - buttonSize -2);
            }
        }
        int currDist = modeText.getX() - window.getX();
        window.moveAllTo(((int)Math.ceil(width/2.0)) - 92,height - buttonSize -2 - buttonSize );


         modeText.moveAllTo(window.getX() + currDist,height - buttonSize -2 - (int)Math.ceil(buttonSize/2.0)-4);
        inventoryButton.moveAllTo(((int)Math.ceil(width/2.0)) -1 - 92 - (buttonSize), height - buttonSize -1);

        if(NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
            cancelPlacement.setVisibility(true);
        else
            cancelPlacement.setVisibility(false);

        updateToolSelectionHighlight();

        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawTooltips(int x_resized, int y_resized, long time)
    {
        if(AdvCreation.mode != EnumMainMode.PLACE)
        {
            //draw progression bar
            int left = window.getX() + 5 ;
            int top = window.getY()+5;
            drawGradientRect(left-1,top-1,left + barTotalLength  ,top + 11,0xFF2A2A2A, 0xFF151515);
            drawRect( left  ,top ,left + barLength,top+10,selectedColor);
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
    protected void postDrawTooltips(int x_resized, int y_resized, long time) {
        super.postDrawTooltips(x_resized, y_resized, time);
        crosshair.draw(this,time);
        zoomIcon.draw(this,time);
        this.renderToolHighlight(new ScaledResolution(mc));
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {

        if(element != null && element.getName().equals("inventory"))
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            mc.getTutorial().openInventory();
            mc.displayGuiScreen(new GuiInventory(mc.player));
        }
        else if(element != null && element.getName().equals("cancelPlacement"))
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            mc.displayGuiScreen(GuiWarningScreenFactory.factory.createCancelPlacementWarningScreen());
        }
        else if(element != null && !(element ==crosshair))
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            if(!element.getName().equals("unnamed!"))
            {
                int i = Integer.parseInt(element.getName());
                if (mc.getRenderViewEntity() instanceof EntityPlayer)
                    ((EntityPlayer) mc.getRenderViewEntity()).inventory.currentItem = i;
            }
        }

    }
    
    public void setCrossHair(int x,int y)
    {
        if(crosshair != null)
        {
            crosshair.setX(x-15/2);
            crosshair.setY(y-15/2);
        }
    }
    
    public void setCrosshairVisibility(boolean vis)
    {
        if(crosshair != null)
            crosshair.setVisibility(vis);
    }

    public void setZoomIcon(int x,int y)
    {
        if(zoomIcon != null)
        {
            zoomIcon.setX(x-5);
            zoomIcon.setY(y-5);
        }
    }

    public void setZoomIconVisibility(boolean vis)
    {
        if(zoomIcon != null)
            zoomIcon.setVisibility(vis);
    }


    @Override
    public void updateChangedMainMode(EnumMainMode mode)
    {
        super.updateChangedMainMode(mode);
        updateToolModeIndication(mode);
        if(mode == EnumMainMode.PLACE)
            setVisibility(false);
        else
            setVisibility(true);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode)
    {
        updateToolModeIndication(mode,false);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode,boolean skipable)
    {
        if(!skipable || !holdMessage)
            updateToolModeIndication(mode,System.currentTimeMillis(),"",false,false, 0, false);
    }
    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText,boolean fullbar ,boolean secondColor )
    {
        updateToolModeIndication(mode,System.currentTimeMillis(),overwriteText,fullbar,secondColor, 0, false);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText, boolean fullbar, boolean secondColor, int overwriteColor)
    {
        updateToolModeIndication(mode,System.currentTimeMillis(),overwriteText,fullbar,secondColor, overwriteColor, false);
    }

    @Override
    public void updateToolModeIndication(EnumMainMode mode, String overwriteText,boolean fullbar ,boolean secondColor, int overwriteColor,boolean holdMessage)
    {
        updateToolModeIndication(mode,System.currentTimeMillis(),overwriteText,fullbar,secondColor, overwriteColor, holdMessage);
    }

    public void updateToolModeIndication(EnumMainMode mode, long time, String overwriteText, boolean fullbar, boolean secondColor, int overwriteColor, boolean holdMessage)
    {
        this.holdMessage = holdMessage;
        String newComment = overwriteText;

        if(newComment.contains("Edit"))
            System.out.println("placed a sign");

        if(overwriteText.length() == 0)
        {
            selectedColor = applyColor(overwriteColor, normalColor);

            if(mode.equals(EnumMainMode.BUILD))
            {
                newComment = BuildMode.TOOLMODE.toolModeName + " (Clicks " + BuildMode.RIGHT_CLICK_NUMBER +"/" + (BuildMode.TOOLMODE.getFinalRightClick() +1)+")" ;
                if(BuildMode.RIGHT_CLICK_NUMBER != 0 &&  BuildMode.DELETE_MODE && !(BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode))
                    newComment += " DELETE";

                barLength = (barTotalLength/(BuildMode.TOOLMODE.getFinalRightClick()+1))*BuildMode.RIGHT_CLICK_NUMBER;
            }
            else if(mode.equals(EnumMainMode.EDIT))
            {
//                newComment =  EditMode.ADJUST_MODE.toolModeName  ;
                newComment =  EditMode.ADJUST_MODE.getGuiOverlayMessage(EditMode.DELETE_MODE)  ;
                barLength = 0;

            }
            else if(mode.equals(EnumMainMode.PLACE))
            {
                int select_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
                newComment = "No Template Selected" ;
                if(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index )
                {
                    String filename = TemplateManager.FILENAME_LIST.get(select_index);
                    newComment = "Place " + filename;
                }
            }
            else
            {
                if(BuildTemplateMode.SELECTED_SIDE != null)
                {
                    newComment = "Adjusting Selection";
                    barLength = 0;
                    selectedColor = applyColor(overwriteColor, normalColor);
                }
                else
                {
                    newComment = BuildTemplateMode.MODE.name();
                    if(BuildTemplateMode.MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS)
                        newComment += " (Press " + Keybindings.CONFIRM_TEMPLATE_CREATION.getKeybind().getDisplayName() + ")";

                    barLength = (barTotalLength/(2))*BuildTemplateMode.MODE.index;
                }
            }
        }
        else
        {

            if(secondColor)
                selectedColor = applyColor(overwriteColor, cancelColor);
            else
                selectedColor = applyColor(overwriteColor, normalColor);

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
            modeText.setX((int)Math.ceil(width/2) - (int) Math.ceil(mc.fontRenderer.getStringWidth(newComment)/2.0));
            modeText.setText(newComment);
        }

        lastTextUpdate = time;
    }


    protected void updateToolSelectionHighlight()
    {
        if (this.mc.player != null)
        {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (itemstack.isEmpty())
            {
                this.remainingHighlightTicks = 0;
            }
            else if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            }
            else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    // copied from net.minecraftforge.client.GuiIngameForge
    protected void renderToolHighlight(ScaledResolution res)
    {
        if (!this.mc.playerController.isSpectator())
        {
            mc.profiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty())
            {
                String name = this.highlightingItemStack.getDisplayName();
                if (this.highlightingItemStack.hasDisplayName())
                    name = TextFormatting.ITALIC + name;

                name = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, name);

                int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) opacity = 255;

                if (opacity > 0)
                {
                    int y = res.getScaledHeight() - 59 ;
                    if (!mc.playerController.shouldDrawHUD()) y += 14;
                    //christiaan added the -20 to y
                    y -= 10;

                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                    if (font != null)
                    {
                        int x = (res.getScaledWidth() - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    else
                    {
                        int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(name)) / 2;
                        mc.fontRenderer.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }

            mc.profiler.endSection();
        }

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
