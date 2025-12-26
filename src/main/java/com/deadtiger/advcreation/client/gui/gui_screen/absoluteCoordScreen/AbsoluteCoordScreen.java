package com.deadtiger.advcreation.client.gui.gui_screen.absoluteCoordScreen;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.*;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.build_template.TemplateBuildingMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_screen.saveTemplateScreen.GuiSaveTemplateScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.mixin.accessor.GuiTextFieldAccessor;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockTripWire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.util.ArrayList;

import static com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter.drawText;

public class AbsoluteCoordScreen extends GuiScreen
{

    public static int minY = 1;
    public static int maxY = (int) Math.round(ConfigurationHandler.cameraConfig.MAX_CAMERA_HEIGHT);

    ResourceLocation refreshTexture = new ResourceLocation(Reference.MODID, "textures/gui/widgets.png");
    ResourceLocation texture = new ResourceLocation(Reference.MODID,"textures/gui/buildmode_gui_overlay.png");
    // size of the main gui window
    private int guiWidth = 270;
    private int guiHeight = 200;

    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;

    //gui screen texts
    private String title = "Set Absolute Coordinates";
    private String subTitle1 = "Here you can input absolute coordinates.";
    private String subTitle2 = "The current tool will be fixed to these coordinates.";

    private GuiCustomWindow window;

    //button on the GUI
    private boolean placeButtonShown = true;
    private GuiButton placeButton;
    private static final int PLACE_BUTTON_ID = 0;
    private boolean placePreviewIsNotConfirm = true;
    private GuiButton placePreviewButton;
    private static final int PLACE_PREVIEW_BUTTON_ID = 1;
    private GuiButton cancelButton;
    private static final int CANCEL_BUTTON_ID = 2;
    private GuiButton returnButton;
    private static final int RETURN_BUTTON_ID = 8;
    private boolean deleteButtonShown = true;
    private GuiButton deleteButton;
    private static final int DELETE_BUTTON_ID = 3;
    private boolean deletePreviewButtonShown = true;
    private GuiButton deletePreviewButton;
    private static final int DELETE_PREVIEW_BUTTON_ID = 7;

    private GuiLabel colX;
    private GuiLabel colY;
    private GuiLabel colZ;

    private GuiLabel startPosLabel;
    private GuiTextFieldFillIn startPosX;
    private GuiTextFieldFillIn startPosY;
    private GuiTextFieldFillIn startPosZ;
    private BlockPos initialStartPos;

    private GuiLabel middlePosLabel;
    private GuiTextFieldFillIn middlePosX;
    private GuiTextFieldFillIn middlePosY;
    private GuiTextFieldFillIn middlePosZ;
    private BlockPos initialMiddlePos;

    private GuiLabel endPosLabel;
    private GuiTextFieldFillIn endPosX;
    private GuiTextFieldFillIn endPosY;
    private GuiTextFieldFillIn endPosZ;
    private BlockPos initialEndPos;
    private ArrayList<GuiTextFieldFillIn> customTextFields = new ArrayList<>();

    private GuiFreeButton startPosLockButton;
    private static final int START_LOCK_BUTTON_ID = 4;
    private GuiFreeButton middlePosLockButton;
    private static final int MIDDLE_LOCK_BUTTON_ID = 5;
    private GuiFreeButton endPosLockButton;
    private static final int END_LOCK_BUTTON_ID = 6;


    private boolean startPosEdited = false;
    private boolean middlePosEdited = false;
    private boolean endPosEdited = false;

    private static final int ESCAPE_KEY_ID = 1;

    private GuiScreen previousScreen = null;

    private long firstHoverOverTime = 0;

    public AbsoluteCoordScreen() {
        super();
    }

    public AbsoluteCoordScreen(GuiScreen previousScreen) {
        super();
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int heigth = scaledResolution.getScaledHeight();

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (heigth / 2) - guiHeight / 2;

        int windowWidth = guiWidth;
        int windowHeight = guiHeight;
        int buttonHeight = 20;

        window = new GuiCustomBigWindow(centerX , centerY,  windowWidth, windowHeight);

        int secondScreenX = 0;

        //left arrow custom button
        initialiseButtons(centerX, centerY, windowHeight, buttonHeight, secondScreenX);

        int descreasedWidth = 65;
        int new_height = centerY + 50 ;
        int fillInWidth = 45;
        int offsetX = 75;

        colX = new GuiLabel(fontRenderer,3,offsetX  + centerX+ 8 +fillInWidth/2,new_height-buttonHeight/4,fillInWidth/2,buttonHeight/2,0xFFFFFF);
        colX.addLine("X");
        colY = new GuiLabel(fontRenderer,3,offsetX  + centerX+ 8 +fillInWidth/2+ (fillInWidth + 5)*1,new_height-buttonHeight/4,fillInWidth/2,buttonHeight/2,0xFFFFFF);
        colY.addLine("Y");
        colZ =new GuiLabel(fontRenderer,3,offsetX  + centerX+ 8 +fillInWidth/2+ (fillInWidth + 5)*2,new_height-buttonHeight/4,fillInWidth/2,buttonHeight/2,0xFFFFFF);
        colZ.addLine("Z");

        new_height = centerY + 4 + 15 + 20 + 10+8;

        startPosLabel = new GuiLabel(fontRenderer,3, centerX+ 11,new_height,100,buttonHeight,0xFFFFFF);
        startPosLabel.addLine("Start Position");
        startPosX = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11,
                new_height, fillInWidth, buttonHeight);
        startPosY = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*1,
                new_height, fillInWidth, buttonHeight);
        startPosZ = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*2,
                new_height, fillInWidth, buttonHeight);
        startPosX.setFirstClick(true);
        startPosY.setFirstClick(true);
        startPosZ.setFirstClick(true);

        //add buttons to lock position in place
        startPosLockButton = new GuiFreeToggleButton(START_LOCK_BUTTON_ID,refreshTexture,offsetX  + centerX+ 11 + (fillInWidth + 5)*3 +1+10,new_height+10,buttonHeight,buttonHeight,20,146,20,166,0,146,0,166);
        buttonList.add(startPosLockButton);


        new_height += (buttonHeight + 5);

        int textColor = 0xFFFFFF;
        if(!GuiOverlayManager.isShowMiddleEnabled())
        {
            textColor = 0x666666;
        }

        middlePosLabel = new GuiLabel(fontRenderer,3,+ centerX+ 11,new_height,100,buttonHeight,textColor);
        middlePosLabel.addLine("Middle Position");
        middlePosX = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11,
                new_height, fillInWidth, buttonHeight);
        middlePosY = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*1,
                new_height, fillInWidth, buttonHeight);
        middlePosZ = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*2,
                new_height, fillInWidth, buttonHeight);
        middlePosX.setFirstClick(true);
        middlePosY.setFirstClick(true);
        middlePosZ.setFirstClick(true);

        if(GuiOverlayManager.isShowMiddleEnabled())
        {
            //add buttons to lock position in place
            middlePosLockButton = new GuiFreeToggleButton(MIDDLE_LOCK_BUTTON_ID, refreshTexture, offsetX + centerX + 11 + (fillInWidth + 5) * 3 + 1+10, new_height+10, buttonHeight, buttonHeight, 20, 146, 20, 166, 0, 146, 0, 166);
            buttonList.add(middlePosLockButton);
        }

        new_height += (buttonHeight + 5);

        textColor = 0xFFFFFF;
        if(!GuiOverlayManager.isShowEndEnabled())
        {
            textColor = 0x666666;
        }

        endPosLabel = new GuiLabel(fontRenderer,3,+ centerX+ 11,new_height,100,buttonHeight,textColor);
        endPosLabel.addLine("End Position");
        endPosX = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11,
                new_height, fillInWidth, buttonHeight);
        endPosY = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*1,
                new_height, fillInWidth, buttonHeight);
        endPosZ = new GuiTextFieldFillIn(3, fontRenderer, offsetX  + centerX+ 11 + (fillInWidth + 5)*2,
                new_height, fillInWidth, buttonHeight);
        endPosX.setFirstClick(true);
        endPosY.setFirstClick(true);
        endPosZ.setFirstClick(true);

        if(GuiOverlayManager.isShowEndEnabled())
        {
            //add buttons to lock position in place
            endPosLockButton = new GuiFreeToggleButton(END_LOCK_BUTTON_ID, refreshTexture, offsetX + centerX + 11 + (fillInWidth + 5) * 3 + 1+10, new_height+10, buttonHeight, buttonHeight, 20, 146, 20, 166, 0, 146, 0, 166);
            buttonList.add(endPosLockButton);
        }

        labelList.add(startPosLabel);
        labelList.add(middlePosLabel);
        labelList.add(endPosLabel);
        labelList.add(colX);
        labelList.add(colY);
        labelList.add(colZ);

        customTextFields.add(startPosX);
        customTextFields.add(startPosY);
        customTextFields.add(startPosZ);

        customTextFields.add(middlePosX);
        customTextFields.add(middlePosY);
        customTextFields.add(middlePosZ);

        customTextFields.add(endPosX);
        customTextFields.add(endPosY);
        customTextFields.add(endPosZ);

        //fill in the textfield with data from an existing report
        // fix the positions that are already selected
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
        {
            if(isModeForTemplatePlacement())
            {
                if(BuildMode.RIGHT_CLICK_NUMBER > BuildMode.TOOLMODE.getFinalRightClick())
                    setMiddlePosEdited(true);
            }
            else
            {
                EnumPosOrder[] posOrder = BuildMode.getToolPosOrder();
                for (int i = 0; i < posOrder.length;i++)
                {
                    if(i < BuildMode.RIGHT_CLICK_NUMBER)
                    {
                        if(posOrder[i] == EnumPosOrder.START_POS)
                            setStartPosEdited(true);
                        else if(posOrder[i] == EnumPosOrder.MIDDLE_POS)
                            setMiddlePosEdited(true);
                        else if(posOrder[i] == EnumPosOrder.END_POS)
                            setEndPosEdited(true);
                    }
                }
            }

        }
        else if(AdvCreation.getMode() == EnumMainMode.PLACE)
        {
            if(PlaceTemplateMode.HOLD_PREVIEW)
                setMiddlePosEdited(true);
        }
        else if(AdvCreation.getMode() == EnumMainMode.CREATE)
        {
            if(BuildTemplateMode.MODE == TemplateBuildingMode.SELECT_END_POS)
                setStartPosEdited(true);
            else if(BuildTemplateMode.MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS)
            {
                setStartPosEdited(true);
                setEndPosEdited(true);
            }
        }

        initialStartPos = GuiOverlayManager.getStartPosCoord();
        if(initialStartPos!= null)
        {
            startPosX.setText(String.valueOf(initialStartPos.getX()));
            startPosY.setText(String.valueOf(initialStartPos.getY()));
            startPosZ.setText(String.valueOf(initialStartPos.getZ()));
        }
        else
        {
            startPosX.setEnabled(false);
            startPosY.setEnabled(false);
            startPosZ.setEnabled(false);
        }
        initialMiddlePos = GuiOverlayManager.getMiddlePosCoord();
        if(GuiOverlayManager.isShowMiddleEnabled())
        {
            if(initialMiddlePos != null)
            {
                middlePosX.setText(String.valueOf(initialMiddlePos.getX()));
                middlePosY.setText(String.valueOf(initialMiddlePos.getY()));
                middlePosZ.setText(String.valueOf(initialMiddlePos.getZ()));
            }
            else if(initialStartPos != null)
            {
                middlePosX.setText(String.valueOf(initialStartPos.getX()));
                middlePosY.setText(String.valueOf(initialStartPos.getY()));
                middlePosZ.setText(String.valueOf(initialStartPos.getZ()));
            }
        }
        else
        {
            middlePosX.setEnabled(false);
            middlePosY.setEnabled(false);
            middlePosZ.setEnabled(false);
        }

        initialEndPos = GuiOverlayManager.getEndPosCoord();
        if(GuiOverlayManager.isShowEndEnabled())
        {
            if(initialEndPos != null)
            {
                endPosX.setText(String.valueOf(initialEndPos.getX()));
                endPosY.setText(String.valueOf(initialEndPos.getY()));
                endPosZ.setText(String.valueOf(initialEndPos.getZ()));
            }
            else if(initialStartPos != null)
            {
                endPosX.setText(String.valueOf(initialStartPos.getX()));
                endPosY.setText(String.valueOf(initialStartPos.getY()));
                endPosZ.setText(String.valueOf(initialStartPos.getZ()));
            }
        }
        else
        {
            endPosX.setEnabled(false);
            endPosY.setEnabled(false);
            endPosZ.setEnabled(false);
        }

        super.initGui();
    }

    private void initialiseButtons(int centerX, int centerY, int windowHeight, int buttonHeight, int secondScreenX)
    {
        if(isModeForTemplateCreation())
        {
            placeButtonShown = false;
            placePreviewIsNotConfirm = false;
            deletePreviewButtonShown = false;
            deleteButtonShown = false;
            placePreviewButton = new GuiButton(PLACE_PREVIEW_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -45,
                    126*2, buttonHeight, "Preview");
            buttonList.add(placePreviewButton);
        }
        else if(isModeForTemplatePlacement() || (AdvCreation.getMode() == EnumMainMode.BUILD && BuildMode.TOOLMODE instanceof FillGapToolMode))
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD && BuildMode.TOOLMODE instanceof MoveToolMode)
            {
                deletePreviewButtonShown = false;
                placeButton = new GuiButton(PLACE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -65,
                        126, buttonHeight, "Place");
                buttonList.add(placeButton);

                placePreviewButton = new GuiButton(PLACE_PREVIEW_BUTTON_ID, secondScreenX + centerX +135, centerY + windowHeight -65,
                        126, buttonHeight, "Place Preview");
                buttonList.add(placePreviewButton);

                deleteButton = new GuiButton(DELETE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -45,
                        126, buttonHeight, "Delete");
                buttonList.add(deleteButton);
            }
            else
            {
                deletePreviewButtonShown = false;
                deleteButtonShown = false;

                placeButton = new GuiButton(PLACE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -45,
                        126, buttonHeight, "Place");
                buttonList.add(placeButton);

                placePreviewButton = new GuiButton(PLACE_PREVIEW_BUTTON_ID, secondScreenX + centerX +135, centerY + windowHeight -45,
                        126, buttonHeight, "Place Preview");
                buttonList.add(placePreviewButton);
            }
        }
        else
        {
            placeButton = new GuiButton(PLACE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -65,
                    126, buttonHeight, "Place");
            buttonList.add(placeButton);

            placePreviewButton = new GuiButton(PLACE_PREVIEW_BUTTON_ID, secondScreenX + centerX +135, centerY + windowHeight -65,
                    126, buttonHeight, "Place Preview");
            buttonList.add(placePreviewButton);

            deleteButton = new GuiButton(DELETE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -45,
                    126, buttonHeight, "Delete");
            buttonList.add(deleteButton);

            deletePreviewButton = new GuiButton(DELETE_PREVIEW_BUTTON_ID, secondScreenX + centerX +135, centerY + windowHeight -45,
                    126, buttonHeight, "Delete Preview");
            buttonList.add(deletePreviewButton);
        }


        cancelButton = new GuiButton(CANCEL_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight -25,
                126, buttonHeight, "Cancel");
        buttonList.add(cancelButton);

        returnButton = new GuiButton(RETURN_BUTTON_ID, secondScreenX + centerX +135, centerY + windowHeight -25,
                126, buttonHeight, "Return");
        buttonList.add(returnButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        //draw the main gui window mainTexture

        window.drawWindow(mc);

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int heigth = scaledResolution.getScaledHeight();

        //draw the title of the window
        fontRenderer.drawString(title, (width / 2) - fontRenderer.getStringWidth(title) / 2, (heigth / 2) - guiHeight / 2 + 4, 0x000000);

        drawText(subTitle1, (width / 2) - guiWidth / 2 +10, (heigth / 2) - guiHeight / 2 + 4 + 15, 0x000000, 0.8);
        drawText(subTitle2, (width / 2) - guiWidth / 2 +10, (heigth / 2) - guiHeight / 2 + 4 + 15 + 10, 0x000000, 0.8);


        for(GuiTextFieldFillIn field:customTextFields)
        {
            field.drawTextBox(mouseX,mouseY,partialTicks);
        }

        //draws all the buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);

        if(startPosEdited || (isModeForTemplatePlacement() && middlePosEdited))
        {
            drawEditedHighlight(startPosLabel, startPosLockButton);
        }
        if(middlePosEdited)
        {
            drawEditedHighlight(middlePosLabel,middlePosLockButton);
        }
        if(endPosEdited || (isModeForTemplatePlacement() && middlePosEdited))
        {
            drawEditedHighlight(endPosLabel, endPosLockButton);
        }
        if(startPosEdited)
        {
            placePreviewButton.enabled = true;
            if(deletePreviewButtonShown)
                deletePreviewButton.enabled = true;
        }
        else
        {
            placePreviewButton.enabled = false;
            if(deletePreviewButtonShown)
                deletePreviewButton.enabled = false;
        }

        if(isModeForTemplatePlacement() )
        {
            if(middlePosEdited)
            {
                if(placeButtonShown)
                    placeButton.enabled = true;
                if(deleteButtonShown)
                    deleteButton.enabled = true;
            }
            else
            {
                if(placeButtonShown)
                    placeButton.enabled = false;
                if(deleteButtonShown)
                    deleteButton.enabled = false;
            }
            placePreviewButton.enabled = true;
        }
        else if(AdvCreation.getMode() == EnumMainMode.BUILD &&  (BuildMode.TOOLMODE instanceof SingleToolMode || BuildMode.TOOLMODE instanceof FillGapToolMode) && startPosEdited)
        {
            if(placeButtonShown)
                placeButton.enabled = true;
            if(deleteButtonShown)
                deleteButton.enabled = true;
        }
        else if(endPosEdited)
        {
            if(placeButtonShown)
                placeButton.enabled = true;
            if(deleteButtonShown)
                deleteButton.enabled = true;
        }
        else
        {
            if(placeButtonShown)
                placeButton.enabled = false;
            if(deleteButtonShown)
                deleteButton.enabled = false;
        }




        long time = System.currentTimeMillis();
        if(firstHoverOverTime == 0)
            firstHoverOverTime = time;

        long delay = 500;
        if(((GuiTextFieldAccessor) endPosX).getIsEnabled() &&  endPosLockButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                if(!endPosLockButton.active)
                    CustomGuiUtils.drawHoveringText("FIX End Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
                else
                    CustomGuiUtils.drawHoveringText("UNFIX End Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(startPosLockButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                if(!startPosLockButton.active)
                    CustomGuiUtils.drawHoveringText("FIX Start Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
                else
                    CustomGuiUtils.drawHoveringText("UNFIX Start Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(((GuiTextFieldAccessor) middlePosX).getIsEnabled() && middlePosLockButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                if(!middlePosLockButton.active)
                    CustomGuiUtils.drawHoveringText("FIX Middle Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
                else
                    CustomGuiUtils.drawHoveringText("UNFIX Middle Position", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(placeButtonShown && placeButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                CustomGuiUtils.drawHoveringText("Perform FIXED PLACE Directly", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(deleteButtonShown && deleteButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                CustomGuiUtils.drawHoveringText("Perform FIXED DELETE Directly", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if( placePreviewButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                if(placePreviewIsNotConfirm)
                    CustomGuiUtils.drawHoveringText("Show PLACE Preview", mouseX+1, mouseY,width,height,-1,fontRenderer);
                else
                    CustomGuiUtils.drawHoveringText("Show Preview", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(deletePreviewButtonShown && deletePreviewButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                CustomGuiUtils.drawHoveringText("Show DELETE Preview", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(cancelButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                CustomGuiUtils.drawHoveringText("CANCEL Absolute Coordinates", mouseX+1, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else if(returnButton.isMouseOver())
        {
            if(time-firstHoverOverTime > delay)
            {
                CustomGuiUtils.drawHoveringText("Go Back To Game", mouseX+7, mouseY,width,height,-1,fontRenderer);
            }
            else if(firstHoverOverTime == time)
                firstHoverOverTime = time;
        }
        else
            firstHoverOverTime = time;





    }

    private void drawEditedHighlight(GuiLabel start, GuiBaseButton end)
    {
        this.drawHorizontalLine(start.x - 3, end.x+end.buttonWidth+3, start.y -2,0xFF1111DD);
        this.drawVerticalLine(end.x+end.buttonWidth+3, start.y -2, start.y +20 +2,0xFF1111DD);
        this.drawHorizontalLine(start.x - 3, end.x+end.buttonWidth+3, start.y +20 +2,0xFF1111DD);
        this.drawVerticalLine(start.x-3, start.y -2, start.y +20 +2,0xFF1111DD);
//        this.drawString(fontRenderer,"SET",start.x + 50,start.y+2,0xFF1111DD);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {

        boolean clicked = false;
        for(int i = customTextFields.size()-1; i >= 0; i--)
        {
            GuiTextFieldFillIn field = customTextFields.get(i);
            if(!clicked)
            {
                clicked = field.mouseClicked(mouseX,mouseY,mouseButton);

            }
            else
            {
                field.setFocused(false);
            }

        }
        if(!clicked)
            super.mouseClicked(mouseX,mouseY,mouseButton);
    }

    private void processFieldTyped(GuiTextFieldFillIn field)
    {
        if(isModeForTemplatePlacement())
            placeTemplateToolProcessing(field);
        else
            normalToolProcessing(field);
    }

    private void normalToolProcessing(GuiTextFieldFillIn field)
    {
        if(field == startPosX || field == startPosY || field == startPosZ)
        {
            setStartPosEdited(true);
        }
        else if(field == middlePosX || field == middlePosY || field == middlePosZ)
        {
            setStartPosEdited(true);
            if(GuiOverlayManager.isShowMiddleEnabled())
                setMiddlePosEdited(true);
        }
        else if(field == endPosX || field == endPosY || field == endPosZ)
        {
            setStartPosEdited(true);
            if(GuiOverlayManager.isShowMiddleEnabled())
                setMiddlePosEdited(true);
            if(GuiOverlayManager.isShowEndEnabled())
                setEndPosEdited(true);
        }
    }

    private boolean isModeForTemplatePlacement()
    {
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
            return (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode) &&
                    BuildMode.RIGHT_CLICK_NUMBER > 1;
        else if(AdvCreation.getMode() == EnumMainMode.PLACE && GuiTemplaceInventoryScreenFunctionality.selected_index > -1)
            return true;

        return false;
    }

    private boolean isModeForTemplateCreation()
    {
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
            return (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode) &&
                    BuildMode.RIGHT_CLICK_NUMBER < 2;
        else if(AdvCreation.getMode() == EnumMainMode.CREATE)
            return true;

        return false;
    }

    private void placeTemplateToolProcessing(GuiTextFieldFillIn field)
    {
        setMiddlePosEdited(true);
        if(field == startPosX || field == startPosY || field == startPosZ)
        {

            if(fieldsAreValidIntegers(this.startPosX, this.startPosY, this.startPosZ))
            {
                BlockPos newStartPos = extractNewBlockPos(this.startPosX, this.startPosY, this.startPosZ,initialStartPos );
                BlockPos size = GuiOverlayManager.getSizeCount();
                BlockPos mid = newStartPos.add(size.getX() / 2, 0, size.getZ() / 2);
                BlockPos end = newStartPos.add(size.add(-1, -1, -1));

                this.middlePosX.setText(String.valueOf(mid.getX()));
                this.middlePosY.setText(String.valueOf(mid.getY()));
                this.middlePosZ.setText(String.valueOf(mid.getZ()));

                this.endPosX.setText(String.valueOf(end.getX()));
                this.endPosY.setText(String.valueOf(end.getY()));
                this.endPosZ.setText(String.valueOf(end.getZ()));
            }

        }
        else if(field == middlePosX || field == middlePosY || field == middlePosZ)
        {
            if(fieldsAreValidIntegers(this.middlePosX, this.middlePosY, this.middlePosZ))
            {
                BlockPos newMiddlePos = extractNewBlockPos(this.middlePosX, this.middlePosY, this.middlePosZ,initialMiddlePos );
                BlockPos size = GuiOverlayManager.getSizeCount();
                BlockPos start = newMiddlePos.add( -size.getX()/2,0,-size.getZ()/2);
                BlockPos end = start.add(size.add(-1,-1,-1));

                this.startPosX.setText(String.valueOf(start.getX()));
                this.startPosY.setText(String.valueOf(start.getY()));
                this.startPosZ.setText(String.valueOf(start.getZ()));

                this.endPosX.setText(String.valueOf(end.getX()));
                this.endPosY.setText(String.valueOf(end.getY()));
                this.endPosZ.setText(String.valueOf(end.getZ()));
            }
        }
        else if(field == endPosX || field == endPosY || field == endPosZ)
        {
            if(fieldsAreValidIntegers(this.endPosX, this.endPosY, this.endPosZ))
            {
                BlockPos newEndPos = extractNewBlockPos(this.endPosX, this.endPosY, this.endPosZ,initialEndPos );
                BlockPos size = GuiOverlayManager.getSizeCount();
                BlockPos start = newEndPos.subtract(size.add(-1, -1, -1));
                BlockPos mid = start.add(size.getX() / 2, 0, size.getZ() / 2);


                this.startPosX.setText(String.valueOf(start.getX()));
                this.startPosY.setText(String.valueOf(start.getY()));
                this.startPosZ.setText(String.valueOf(start.getZ()));

                this.middlePosX.setText(String.valueOf(mid.getX()));
                this.middlePosY.setText(String.valueOf(mid.getY()));
                this.middlePosZ.setText(String.valueOf(mid.getZ()));
            }

        }
    }

    private boolean fieldsAreValidIntegers(GuiTextFieldFillIn p, GuiTextFieldFillIn p2, GuiTextFieldFillIn p3)
    {
        return (!p.getText().isEmpty() && !p2.getText().isEmpty() && !p3.getText().isEmpty()) &&
                (!p.getText().equals("-") && !p2.getText().equals("-") && !p3.getText().equals("-"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id) {
            case PLACE_BUTTON_ID:
                confirmAction(false, false);
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case PLACE_PREVIEW_BUTTON_ID:
                confirmAction(true, false);
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case DELETE_BUTTON_ID:
                confirmAction(false, true);
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case DELETE_PREVIEW_BUTTON_ID:
                confirmAction(true, true);
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case CANCEL_BUTTON_ID:
                cancelAction();
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case RETURN_BUTTON_ID:
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case START_LOCK_BUTTON_ID:
                setAllPosEdited(EnumPosOrder.START_POS,startPosLockButton.active);
                break;
            case MIDDLE_LOCK_BUTTON_ID:
                setAllPosEdited(EnumPosOrder.MIDDLE_POS,middlePosLockButton.active);
                break;
            case END_LOCK_BUTTON_ID:
                setAllPosEdited(EnumPosOrder.END_POS,endPosLockButton.active);
                break;
        }

        super.actionPerformed(button);
    }

    protected void setAllPosEdited(EnumPosOrder pos, boolean posEdited)
    {
        if(AdvCreation.getMode() == EnumMainMode.BUILD && !(isModeForTemplateCreation() || isModeForTemplatePlacement() ) )
        {
            int index = 0;
            EnumPosOrder[] posOrder = BuildMode.TOOLMODE.getPosOrder();
            for (int i = 0; i < posOrder.length; i++)
            {
                if(posOrder[i] == pos)
                    index = i;
            }

            if(posEdited)
            {
                for (int i = index; i >= 0 ; i--)
                {
                    setPosEdited(posOrder[i],true);
                }
            }
            else
            {
                for (int i = index; i < posOrder.length ; i++)
                {
                    setPosEdited(posOrder[i],false);
                }
            }
        }
        else
        {
            if(pos == EnumPosOrder.START_POS)
            {
                if(posEdited)
                {
                    if(isModeForTemplatePlacement())
                        setMiddlePosEdited(true);
                    else
                        startPosEdited = true;
                }
                else
                {
                    startPosEdited = false;
                    this.setMiddlePosEdited(false);
                    this.setEndPosEdited(false);
                }
            }
            else if(pos == EnumPosOrder.MIDDLE_POS)
            {
                if(posEdited)
                {
                    if(isModeForTemplatePlacement())
                        setMiddlePosEdited(true);
                    else
                    {
                        middlePosEdited = true;
                        setStartPosEdited(true);
                    }
                }
                else
                {
                    middlePosEdited = false;
                    setEndPosEdited(false);
                    if(isModeForTemplatePlacement())
                    {
                        setStartPosEdited(false);
                    }

                }
            }
            else if(pos == EnumPosOrder.END_POS)
            {
                if(posEdited)
                {
                    if(isModeForTemplatePlacement())
                        setMiddlePosEdited(true);
                    else
                    {
                        endPosEdited = true;
                        if(((GuiTextFieldAccessor) middlePosX).getIsEnabled())
                            setMiddlePosEdited(true);
                        setStartPosEdited(true);
                    }

                }
                else
                {
                    endPosEdited = false;
                    if(isModeForTemplatePlacement())
                    {
                        setMiddlePosEdited(false);
                        setStartPosEdited(false);
                    }
                }
            }
        }
    }

    protected void setPosEdited(EnumPosOrder pos,boolean posEdited)
    {
        if(pos == EnumPosOrder.START_POS)
            setStartPosEdited(posEdited);
        else if(pos == EnumPosOrder.MIDDLE_POS)
            setMiddlePosEdited(posEdited);
        else if(pos == EnumPosOrder.END_POS)
            setEndPosEdited(posEdited);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        int backspace = 14;
        int leftArrow= 203;
        int rightArrow = 205;
        int minus = 12;
        int altMinus = 74;
        int enter = 28;
        boolean typed = false;
        for(GuiTextFieldFillIn field:customTextFields)
        {
            if(!typed)
            {
                if(Character.isDigit(typedChar) || keyCode == leftArrow || keyCode == backspace ||
                        keyCode == rightArrow || keyCode == minus || keyCode == altMinus)
                {
                    typed = field.textboxKeyTyped(typedChar, keyCode);
                    if(typed && ((GuiTextFieldAccessor) field).getIsEnabled())
                        processFieldTyped(field);
                }

            }
            else
                break;
        }
        if(!typed) {
            if (keyCode == ESCAPE_KEY_ID)
            {
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                if (this.mc.currentScreen == null)
                {
                    this.mc.setIngameFocus();
                }
            }
            if (keyCode == enter)
            {
                confirmAction(true, false);
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                if (this.mc.currentScreen == null)
                {
                    this.mc.setIngameFocus();
                }
            }
        }
    }


    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();



    }


    private void confirmAction(boolean preview, boolean delete)
    {

        if(AdvCreation.getMode() == EnumMainMode.BUILD)
            confirmActionBuildMode(preview, delete);
        else if(AdvCreation.getMode() == EnumMainMode.PLACE)
            confirmActionPlaceMode(preview, delete);
        else if(AdvCreation.getMode() == EnumMainMode.CREATE)
            confirmActionCreateMode(preview, delete);

    }

    private void cancelAction()
    {
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.clearBuildMode();
        else if(AdvCreation.getMode() == EnumMainMode.PLACE)
        {
            if(PlaceTemplateMode.HOLD_PREVIEW)
                PlaceTemplateMode.toggleHoldPreview();
        }
        else if(AdvCreation.getMode() == EnumMainMode.CREATE)
            BuildTemplateMode.cancelTemplate();
    }

    private void confirmActionCreateMode(boolean preview, boolean delete)
    {
        if(initialStartPos == null)
        {
            initialStartPos = GuiOverlayManager.getStartPosCoord();
            if(initialStartPos == null)
                initialStartPos = BlockPos.ORIGIN;
        }

        if(initialMiddlePos == null)
        {
            initialMiddlePos = initialStartPos;
            if (initialEndPos == null)
            {
                initialEndPos = initialStartPos;
            }
        }
        else {
            if (initialMiddlePos == null)
            {
                initialEndPos = initialMiddlePos;
            }
        }

        BuildTemplateMode.cancelTemplate();
        if(startPosEdited )
        {
            BuildTemplateMode.clearMouseOffset();
            BlockPos newStartPos = extractNewBlockPos(this.startPosX, this.startPosY, this.startPosZ,initialStartPos );
            GuiOverlayManager.setStartPos(newStartPos);
            BuildTemplateMode.leftClick(newStartPos);
        }

        if(endPosEdited)
        {
            BuildTemplateMode.clearMouseOffset();
            BlockPos newEndPos = extractNewBlockPos(this.endPosX, this.endPosY, this.endPosZ,initialEndPos );
            GuiOverlayManager.setEndPos(newEndPos);
            BuildTemplateMode.leftClick(newEndPos);

            if(!preview)
                Minecraft.getMinecraft().displayGuiScreen(new GuiSaveTemplateScreen());
        }




    }

    private void confirmActionPlaceMode(boolean preview, boolean delete)
    {
        if(middlePosEdited)
        {
            if(initialMiddlePos == null)
            {
                initialMiddlePos = GuiOverlayManager.getMiddlePosCoord();
                if(initialMiddlePos == null)
                    initialMiddlePos = BlockPos.ORIGIN;
            }

            BlockPos newMiddlePos = extractNewBlockPos(this.middlePosX, this.middlePosY, this.middlePosZ, initialMiddlePos);
            Vec3d fakeHitVec = new Vec3d(newMiddlePos.getX()+0.5,newMiddlePos.getY()+0.5,newMiddlePos.getZ()+0.5);

            GuiOverlayManager.setMiddlePos(newMiddlePos);
            PlaceTemplateMode.clearMouseOffset();
            if(PlaceTemplateMode.HOLD_PREVIEW)
                PlaceTemplateMode.toggleHoldPreview();
            PlaceTemplateMode.updateAlterModeInactive(newMiddlePos, fakeHitVec);
            if(preview)
                PlaceTemplateMode.toggleHoldPreview();
            else
                PlaceTemplateMode.placeTemplate(newMiddlePos,mc.player);
        }
        else
        {
            if(PlaceTemplateMode.HOLD_PREVIEW)
                PlaceTemplateMode.toggleHoldPreview();
        }

    }

    private void confirmActionBuildMode(boolean preview, boolean delete)
    {
        BuildMode.setUsingAbsCoord(true);
        if(initialStartPos == null)
        {
            initialStartPos = GuiOverlayManager.getStartPosCoord();
            if(initialStartPos == null)
                initialStartPos = BlockPos.ORIGIN;
        }

        if(initialMiddlePos == null)
        {
            initialMiddlePos = initialStartPos;
            if (initialEndPos == null)
            {
                initialEndPos = initialStartPos;
            }
        }
        else {
            if (initialMiddlePos == null)
            {
                initialEndPos = initialMiddlePos;
            }
        }
        if(!(isModeForTemplatePlacement()))
            BuildMode.clearBuildMode();
        else
        {
            BuildMode.clearMouseOffset();
            PlaceTemplateMode.clearMouseOffset();
            if(BuildMode.RIGHT_CLICK_NUMBER > BuildMode.TOOLMODE.getFinalRightClick())
                BuildMode.RIGHT_CLICK_NUMBER = BuildMode.TOOLMODE.getFinalRightClick();
        }


        EnumDirectionMode prevDirMode = BuildMode.DIRECTION_MODE;

        EnumPosOrder[] posOrder = BuildMode.getToolPosOrder();

        //if in circle tool mode orientate the circle based on the direction between start and end pos
        if(BuildMode.TOOLMODE instanceof CircleToolMode)
        {
            if(startPosEdited && endPosEdited)
            {
                BlockPos newStartPos = extractNewBlockPos(this.startPosX, this.startPosY, this.startPosZ,initialStartPos  );
                Vec3d fakeStartHitVec = new Vec3d(newStartPos.getX()+0.5,newStartPos.getY()+0.5,newStartPos.getZ()+0.5);
                BlockPos newEndPos = extractNewBlockPos(this.endPosX, this.endPosY, this.endPosZ, initialEndPos);
                Vec3d fakeEndHitVec = new Vec3d(newEndPos.getX()+0.5,newEndPos.getY()+0.5,newEndPos.getZ()+0.5);


                Vec3d diffVec = fakeEndHitVec.subtract( fakeStartHitVec);
                if(Math.abs(diffVec.x) > Math.abs(diffVec.z))
                {
                    if(Math.abs(diffVec.y) > Math.abs(diffVec.z))
                        BuildMode.DIRECTION_MODE = EnumDirectionMode.XY;
                    else
                        BuildMode.DIRECTION_MODE = EnumDirectionMode.XZ;
                }
                else
                {
                    if(Math.abs(diffVec.y) > Math.abs(diffVec.x))
                        BuildMode.DIRECTION_MODE = EnumDirectionMode.ZY;
                    else
                        BuildMode.DIRECTION_MODE = EnumDirectionMode.XZ;
                }
            }

        }
        if(BuildMode.TOOLMODE instanceof FillGapToolMode)
            BuildMode.DIRECTION_MODE =BuildMode.DIRECTION_MODE;
        else
            BuildMode.DIRECTION_MODE = EnumDirectionMode.FREE;


        for (EnumPosOrder currPos:posOrder)
        {
            if(currPos == EnumPosOrder.START_POS)
            {
                if(startPosEdited)
                {
                    BlockPos newStartPos = extractNewBlockPos(this.startPosX, this.startPosY, this.startPosZ,initialStartPos );
                    Vec3d fakeHitVec = new Vec3d(newStartPos.getX()+0.5,newStartPos.getY()+0.5,newStartPos.getZ()+0.5);
                    RayTraceResult fakeObjectMouseOver = new RayTraceResult(RayTraceResult.Type.BLOCK,fakeHitVec, EnumFacing.NORTH,newStartPos);
                    Item item = mc.player.getHeldItemMainhand().getItem();

                    GuiOverlayManager.setStartPos(newStartPos);

                    boolean rightlclick = true;
                    if(isModeForTemplateCreation() || delete)
                        rightlclick = false;

                    HelpFunctions.formatNewBlockAndAddToBuildMode(fakeObjectMouseOver,mc.player, item, fakeHitVec, newStartPos, rightlclick);
                    goToNextStepInBuildMode(item,rightlclick);
                }
            }
            else if(currPos == EnumPosOrder.MIDDLE_POS)
            {
                if(middlePosEdited && BuildMode.TOOLMODE.hasMiddlePosition())
                {

                    BlockPos newMiddlePos = extractNewBlockPos(this.middlePosX, this.middlePosY, this.middlePosZ,initialMiddlePos );
                    Vec3d fakeHitVec = new Vec3d(newMiddlePos.getX()+0.5,newMiddlePos.getY()+0.5,newMiddlePos.getZ()+0.5);
                    RayTraceResult fakeObjectMouseOver = new RayTraceResult(RayTraceResult.Type.BLOCK,fakeHitVec, EnumFacing.NORTH,newMiddlePos);
                    Item item = mc.player.getHeldItemMainhand().getItem();
                    GuiOverlayManager.setMiddlePos(newMiddlePos);

                    boolean rightlclick = true;
                    if(delete)
                        rightlclick = false;

                    HelpFunctions.formatNewBlockAndAddToBuildMode(fakeObjectMouseOver,mc.player, item, fakeHitVec, newMiddlePos, rightlclick);
                    goToNextStepInBuildMode(item,rightlclick);
                }
            }
            else if(currPos == EnumPosOrder.END_POS)
            {
                if(endPosEdited && BuildMode.TOOLMODE.hasEndPosition())
                {
                    BlockPos newEndPos = extractNewBlockPos(this.endPosX, this.endPosY, this.endPosZ,initialEndPos );
                    Vec3d fakeHitVec = new Vec3d(newEndPos.getX()+0.5,newEndPos.getY()+0.5,newEndPos.getZ()+0.5);
                    RayTraceResult fakeObjectMouseOver = new RayTraceResult(RayTraceResult.Type.BLOCK,fakeHitVec, EnumFacing.NORTH,newEndPos);
                    Item item = mc.player.getHeldItemMainhand().getItem();

                    boolean rightlclick = true;
                    if(isModeForTemplateCreation() || delete)
                        rightlclick = false;

                    GuiOverlayManager.setEndPos(newEndPos);
                    HelpFunctions.formatNewBlockAndAddToBuildMode(fakeObjectMouseOver,mc.player, item, fakeHitVec, newEndPos, rightlclick);
                    goToNextStepInBuildMode( item,rightlclick);

                }
            }
        }

        BuildMode.setUsingAbsCoord(false);

        if(!preview)
        {
            Item item = mc.player.getHeldItemMainhand().getItem();
            if(isModeForTemplatePlacement())
            {
                if(middlePosEdited)
                    goToNextStepInBuildMode( item,!delete);
            }
            else if(AdvCreation.getMode() == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof SingleToolMode || BuildMode.TOOLMODE instanceof FillGapToolMode))
            {
                if(startPosEdited)
                    goToNextStepInBuildMode( item,!delete);
            }
            else
            {
                if(endPosEdited)
                    goToNextStepInBuildMode( item,!delete);
            }

        }
        else if(BuildMode.TOOLMODE instanceof MoveToolMode)
        {
            BuildMode.DELETE_MODE = true;
            if(BuildMode.RIGHT_CLICK_NUMBER > BuildMode.TOOLMODE.getFinalRightClick())
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),BuildMode.TOOLMODE.toolModeName + " FIXED COORD",true,false,0xFF0000FF,true);
            else
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
        }
        else if(BuildMode.RIGHT_CLICK_NUMBER > BuildMode.TOOLMODE.getFinalRightClick())
        {
            if(delete)
//                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),BuildMode.TOOLMODE.toolModeName + " FIXED COORD DELETE",true,false,0xFF6600FF,true);
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),BuildMode.TOOLMODE.toolModeName + " FIXED COORD DELETE",true,false,0xFF0000FF,true);
            else
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),BuildMode.TOOLMODE.toolModeName + " FIXED COORD",true,false,0xFF0000FF,true);

        }
        else
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());


        BuildMode.DIRECTION_MODE = prevDirMode;
    }

    private void goToNextStepInBuildMode(Item item,boolean rightClick)
    {

        if(rightClick)
        {
            if ((item instanceof ItemBlock && !(((ItemBlock) item).getBlock() instanceof BlockTripWire)) || PlacementHelper.isAllowedNonItemBlocks(item))
            {
                //Save the blockstates of the blocks that are being replace for the undo feature
                Action currAction = new Action();
                if (BuildMode.rightClick(currAction, mc.player))
                {
                    //means the right click did something and that action is being saved
                    if (!currAction.getPreviousBlockStates().isEmpty())
                        UndoFunctionality.addActionToHistory(currAction);
                }
            }
        }
        else
        {
            //Save the blockstates of the blocks that are being replace for the undo feature
            Action currAction = new Action();
            if (BuildMode.leftClick(currAction, mc.player))
            {
                //means the right click did something and that action is being saved
                UndoFunctionality.addActionToHistory(currAction);
            }
        }
    }

    private BlockPos extractNewBlockPos(GuiTextFieldFillIn startPosX, GuiTextFieldFillIn startPosY, GuiTextFieldFillIn startPosZ, BlockPos initialBlockPos)
    {
         int newStartPosX = initialBlockPos.getX();
        if(!startPosX.getText().isEmpty())
            newStartPosX = Integer.parseInt(startPosX.getText());

        int newStartPosY = initialBlockPos.getY();
        if(!startPosY.getText().isEmpty())
            newStartPosY = Integer.parseInt(startPosY.getText());

        if(newStartPosY < minY)
            newStartPosY = minY;
        else if( newStartPosY > maxY)
            newStartPosY = maxY;

        int newStartPosZ = initialBlockPos.getZ();
        if(!startPosZ.getText().isEmpty())
            newStartPosZ = Integer.parseInt(startPosZ.getText());

        return new BlockPos(newStartPosX,newStartPosY,newStartPosZ);
    }

    public static void openAbsoluteCoordScreen()
    {
        boolean canUseAbsCoord = false;
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
            canUseAbsCoord =BuildMode.canUseAbsoluteCoord();
        else if(AdvCreation.getMode() == EnumMainMode.EDIT)
            canUseAbsCoord = false;
        else if(AdvCreation.getMode() == EnumMainMode.PLACE)
            canUseAbsCoord = true;
        else if(AdvCreation.getMode() == EnumMainMode.CREATE)
            canUseAbsCoord = true;

        if(canUseAbsCoord)
            Minecraft.getMinecraft().displayGuiScreen(new AbsoluteCoordScreen());
        else
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Abs Coordinates Unavailable", true, true);
    }

    public void setStartPosEdited(boolean startPosEdited)
    {
        this.startPosEdited = startPosEdited;
        this.startPosLockButton.active = startPosEdited;
    }

    public void setMiddlePosEdited(boolean middlePosEdited)
    {
        if(((GuiTextFieldAccessor) middlePosX).getIsEnabled())
        {
            this.middlePosEdited = middlePosEdited;
            this.middlePosLockButton.active = middlePosEdited;
        }
        if(isModeForTemplatePlacement())
        {
            this.endPosLockButton.active = middlePosEdited;
            this.startPosLockButton.active = middlePosEdited;
        }
    }

    public void setEndPosEdited(boolean endPosEdited)
    {
        if(((GuiTextFieldAccessor) endPosX).getIsEnabled())
        {
            this.endPosEdited = endPosEdited;
            this.endPosLockButton.active = endPosEdited;
        }

    }
}
