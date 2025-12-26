package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.FpsOptimiser;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.absoluteCoordScreen.AbsoluteCoordScreen;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.*;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiHelpScreenVisual;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.awt.*;
import java.util.ArrayList;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class MainModeGuiOverlay extends AbstractGuiOverlay
{
    private int buttonWidth = 50, buttonHeight = 20;

    //buttons and elements
    // the window
    private GuiOverlayBaseElement window;
    // the buttons in the window
    private GuiOverlayCustomButton buildButton, placeButton, createButton, editButton;
    // the help and fps window
    private GuiOverlayBaseElement sideWindow;
    // the seperate help button
    private GuiOverlayBaseButton helpButton;

    private double textScale = 0.76;
    private int breakDist = (int)(textScale*10);
    private GuiOverlayText startPos;
    private GuiOverlayText middlePos;
    private GuiOverlayText endPos;
    private GuiOverlayText offset;
    private GuiOverlayText sizeCount;
    private GuiOverlayText blockCount;

    private GuiOverlayBaseElement fpsBackground;
    private GuiOverlayText fpsCounter;
    private GuiOverlayBaseButton toggleShowFpsOptimisationButtonsButton;
    private GuiOverlayBaseButton increaseFpsButton;
    private GuiOverlayBaseButton decreaseFpsButton;

    private boolean displayOptimiseButton = false;
    private boolean displayOptimiseHelpMessage = false;
    private boolean lowFps = false;
    private int showButtonCountdown = 3;
    private long startTimeFpsOptimiseMessage = 0;
    private long timeLimitFpsOptimiseMessage = 10000;
    private boolean fpsOptimiseMessageShownOnceAlready = false;

    private ArrayList<String> fpsOptimisationFeedback = new ArrayList<>();
    private long startTimeFpsOptimisationFeedback = 0;
    private long timeLimitFpsOptimisationFeedback = 5000;

    private GuiOverlayText placePointedCoord;
    public boolean allowShowPlacedCoord = true;
    private GuiOverlayText deletePointedCoord;
    private GuiOverlayText offsetPointedText;
    private GuiOverlayText offsetPointedCoord;
    private GuiOverlayText cameraFocusPointCoord;
    private BlockPos cameraFocusPointCoordPos = new BlockPos(0,0,0);

    private boolean prevShowPointedCoord = false;



    public boolean displaySizeCount = false;
    public int sizeX;
    public int sizeY;
    public int sizeZ;
    public int blockCounter;
    public BlockPos startBlockPos = null;
    public BlockPos middleBlockPos = null;
    public BlockPos offsetBlockPos = null;
    public BlockPos endBlockPos = null;
    public BlockPos placeCoordBlockPos = null;
    public BlockPos deleteCoordBlockPos = null;
    public boolean displayPosCoord;
    public boolean smallScreenText = false;
    public boolean positionTextAboveHotbar = false;

    public boolean middleEnabled = false;
    public boolean prevMiddleEnabled = true;
    public boolean prevChangePos = false;
    public boolean endEnabled = false;
    public boolean prevEndEnabled = true;
    public boolean refreshCoordInfoDisplay = true;

    private boolean firstHovered = false; //whether the player has already hovered on the window
    private boolean displayInitialHelpMessage = false;
    private long startTimeHelpMessage = 0;
    private long timeLimitHelpMessage = 5000;

    private GuiOverlayBaseElement coordWindow;
    private GuiOverlayFreeSizeCustomButton setAbsCoordButton;
    private GuiOverlayFreeSizeCustomButton clearOffsetButton;


    @Override
    protected void initGuiOverlay() {
        elementlist.clear();

        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int buttonSize = 20;

        mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/buildmode_gui_overlay.png");
        buttonHeight = 20;
        // the window containing mode buttons
        window = new GuiOverlayCustomWindow(0,0,228,30);
        window.setName("mode window");
        // the buttons in the window
        buildButton = new GuiOverlayCustomButton("BUILD",5,5,buttonWidth,buttonHeight);
        buildButton.setName("build mode");
        buildButton.setTooltip("Placing and deleting blocks");
        buildButton.setTimedDeactivation(false);
        buildButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_MODE.getKeybind());
        buildButton.index = 4;
        buildButton.setZ(1);

        editButton =new GuiOverlayCustomButton("EDIT",61,5,buttonWidth,buttonHeight);
        editButton.setName("edit button");
        editButton.setTooltip("Edit the world");
        editButton.setTimedDeactivation(false);
        editButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_MODE.getKeybind());
        editButton.index = 4;
        editButton.setZ(1);

        placeButton = new GuiOverlayCustomButton("PLACE",117,5,buttonWidth,buttonHeight);
        placeButton.setName("place button");
        placeButton.setTooltip("Place new structures from templates");
        placeButton.setTimedDeactivation(false);
        placeButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_MODE.getKeybind());
        placeButton.index = 4;
        placeButton.setZ(1);

        createButton = new GuiOverlayCustomButton("CREATE",173,5,buttonWidth,buttonHeight);
        createButton.setName("create button");
        createButton.setTooltip("Make new templates");
        createButton.setTimedDeactivation(false);
        createButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_MODE.getKeybind());
        createButton.index = 4;
        createButton.setZ(1);

        window.addElement(buildButton);
        window.addElement(editButton);
        window.addElement(placeButton);
        window.addElement(createButton);

        //the helpbutton
        helpButton = new GuiOverlayBaseButton(mainTexture,228 + 5,7,174,5,20,20,50,25);
        helpButton.setName("help button");
        helpButton.index = 24;

        //the size indicator
//        int posX = (int)(Math.ceil(width/2.0) + buttonSize*9 - 92 + 4*(guiScale) );
//        int posY = height - (buttonSize)*2 - (int)(2*guiScale);

        int posX = (int)(Math.ceil(width/2.0) + buttonSize*9 - 92 + 4 );
        int posY = height - (buttonSize)*2 - 2;

        coordWindow = new GuiOverlayCustomWindow(posX,posY,(int)((buttonSize)*2.4 + 12),(buttonSize)*2 + 2);
        coordWindow.setName("coordWindow");
        setAbsCoordButton = new GuiOverlayFreeSizeCustomButton("SET ABS CRD",posX +5,posY +4,(int) (buttonSize*1.2),buttonSize*2-6);
        setAbsCoordButton.setName("absCoordButton");
        setAbsCoordButton.setTooltip("Open screen to input absolute coordinates");
        setAbsCoordButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_ABS_COORD_SCREEN.getKeybind());
        setAbsCoordButton.setZ(1);
        displayPosCoord = ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI;
        setAbsCoordButton.setEnabled(ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI);
        setAbsCoordButton.setDisabledTooltip("UNAVAILABLE: first enable in mod options: 'Show absolute positions on HUD'");

        clearOffsetButton = new GuiOverlayFreeSizeCustomButton("CLR OFF SET",posX +5+((int) (buttonSize*1.2)+2),posY +4 ,(int) (buttonSize*1.2),buttonSize*2 -6);
        clearOffsetButton.setName("clearOffsetButton");
        clearOffsetButton.setTooltip("Clear offset from cursor");
        clearOffsetButton.setButtonKeyToDisplayInTooltip(Keybindings.CLEAR_OFFSET.getKeybind());
        clearOffsetButton.setZ(1);

//        posX = ((int)Math.ceil(width/2.0)) - 92 + (int)(6*(guiScale)  + buttonSize*9 +(int)(((buttonSize)*3 +(12*(guiScale)))*(2.5/3.0)));
//        posY = height -8 ;
        posX = ((int)Math.ceil(width/2.0)) - 92 + (int)(6  + buttonSize*9 +(int)(((buttonSize)*3 +12)*(2.5/3.0)));
        posY = height -10 ;

        updateTextScale(scaledresolution.getScaleFactor(),width,height);


        startPos = new GuiOverlayText("STRT: 100 100 100",posX,posY - breakDist*3,textScale);
        middlePos = new GuiOverlayText("MIDD: 100 100 100",posX,posY - breakDist*4,textScale);
        endPos = new GuiOverlayText("END : 100 100 100",posX,posY - breakDist*3,textScale);
        offset = new GuiOverlayText("OFFS: 100 100 100",posX,posY - breakDist*2,textScale);
        sizeCount = new GuiOverlayText("SIZE: 100 100 100",posX,posY -breakDist ,textScale);
        blockCount = new GuiOverlayText("1000 blocks",posX,posY,textScale);

        placePointedCoord = new GuiOverlayText("100 100 100",100,100,0.60);
        deletePointedCoord = new GuiOverlayText("100 100 100",100,94,0.60);
        deletePointedCoord.setColor(0xFF0000);

        offsetPointedText = new GuiOverlayText("OFFSET",100,100,0.60);
        offsetPointedCoord = new GuiOverlayText("0 0 0",100,107,0.60);

        cameraFocusPointCoord = new GuiOverlayText("100 100 100",0,0,0.60);


        // the window containing mode buttons
        sideWindow = new GuiOverlayCustomWindow(228,0,30,30);
        sideWindow.setName("side window");

        fpsBackground = new GuiOverlayBaseElement( new ResourceLocation("textures/gui/widgets.png"),228,0,5,50,25,8);
        fpsBackground.setZ(0);
        fpsBackground.setName("fpsBackground");
        fpsCounter = new GuiOverlayText("FPS: 30",228 + 2,2,0.60);
        fpsCounter.setZ(1);
        fpsCounter.setName("fpsCounter");

        toggleShowFpsOptimisationButtonsButton = new GuiOverlayBaseButton(mainTexture,228+25,0,174,35,5,8,50,25);
        toggleShowFpsOptimisationButtonsButton.setName("show optimise buttons button");
        toggleShowFpsOptimisationButtonsButton.setTimedDeactivation(true);
        toggleShowFpsOptimisationButtonsButton.setZ(2);

        increaseFpsButton = new GuiOverlayBaseButton(mainTexture,228+30,0,174,85,10,20,50,25);
        increaseFpsButton.setName("increase fps button");
        increaseFpsButton.setVisibility(false);
        increaseFpsButton.setTimedDeactivation(true);
        increaseFpsButton.setTooltip(FpsOptimiser.increaseFpsTooltip());

        decreaseFpsButton = new GuiOverlayBaseButton(mainTexture,228+40,0,184,85,10,20,50,25);
        decreaseFpsButton.setName("decrease fps button");
        decreaseFpsButton.setVisibility(false);
        decreaseFpsButton.setTimedDeactivation(true);
        decreaseFpsButton.setTooltip(FpsOptimiser.decreaseFpsTooltip());

        sideWindow.addElement(fpsBackground);
        sideWindow.addElement(fpsCounter);
        sideWindow.addElement(helpButton);
        sideWindow.addElement(toggleShowFpsOptimisationButtonsButton);
        //all first level element to elementlist
        elementlist.add(window);
//        elementlist.add(helpButton);
        elementlist.add(startPos);
        elementlist.add(middlePos);
        elementlist.add(endPos);
        elementlist.add(offset);
        elementlist.add(sizeCount);
        elementlist.add(blockCount);
        elementlist.add(increaseFpsButton);
        elementlist.add(decreaseFpsButton);


        elementlist.add(placePointedCoord);
        elementlist.add(deletePointedCoord);
        elementlist.add(offsetPointedText);
        elementlist.add(offsetPointedCoord);
        elementlist.add(cameraFocusPointCoord);

        offsetPointedText.setVisibility(false);
        offsetPointedCoord.setVisibility(false);

        coordWindow.addElement(setAbsCoordButton);
        coordWindow.addElement(clearOffsetButton);

        elementlist.add(coordWindow);
        elementlist.add(sideWindow);

        firstHovered = false;

        this.setPosVisibility(ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI);

        super.initGuiOverlay();
    }

    private void updateTextScale(double scaleFactor, int totWidth,int totHeight)
    {

        double textScale1 = 1.4 -0.2*scaleFactor;
        breakDist = (int)(textScale1*10);

        if(AdvCreation.getMode() == EnumMainMode.PLACE)
        {
           int heightSpace = totHeight -  30 - 110 - 30*2;
           int heightNeeded = 10+breakDist*4;
           if(heightNeeded > heightSpace)
           {
               textScale1 = textScale1*(heightSpace/((double)heightNeeded));
               breakDist = (int)(textScale1*10);
           }
        }

        if(sizeCount != null)
        {
            int widthSpace = totWidth -  (((int)Math.ceil(totWidth/2.0)) - 92 + (int)(6  + 20*9 +(int)(((20)*3 +12)*(2.5/3.0))));
            if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                widthSpace = totWidth -  (((int)Math.ceil(totWidth/2.0)) - 92 + (int)(6  + 58*4 ));

            int widthNeeded = (int)(Minecraft.getMinecraft().fontRenderer.getStringWidth(sizeCount.getText()) * textScale1);

            if(widthNeeded > widthSpace)
            {
                textScale1 = textScale1*(widthSpace/((double)widthNeeded));
                breakDist = (int)(textScale1*10);
            }
        }
        if(offset != null)
        {
            int widthSpace = totWidth -  (((int)Math.ceil(totWidth/2.0)) - 92 + (int)(6  + 20*9 +(int)(((20)*3 +12)*(2.5/3.0))));
            if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                    widthSpace = totWidth -  (((int)Math.ceil(totWidth/2.0)) - 92 + (int)(6  + 58*4 ));

            int widthNeeded = (int)(Minecraft.getMinecraft().fontRenderer.getStringWidth(offset.getText()) * textScale1);

            if(widthNeeded > widthSpace)
            {
                textScale1 = textScale1*(widthSpace/((double)widthNeeded));
                breakDist = (int)(textScale1*10);
            }
        }


        textScale = textScale1;
        if(startPos != null)
        {
            startPos.setFontScale(textScale);
            middlePos.setFontScale(textScale);
            endPos.setFontScale(textScale);
            offset.setFontScale(textScale);
            blockCount.setFontScale(textScale);
            sizeCount.setFontScale(textScale);
        }
    }

    @Override
    public void updateChangedMainMode(EnumMainMode mode)
    {

        if(mode == EnumMainMode.BUILD)
        {
            BuildMode.updateCoordInfoDisplay();
            BuildMode.clearMouseOffset();
        }
        else if(mode == EnumMainMode.EDIT)
        {
            EditMode.updateCoordInfoDisplay();
        }
        else if(mode == EnumMainMode.PLACE)
        {
            PlaceTemplateMode.updateCoordInfoDisplay();
            PlaceTemplateMode.clearMouseOffset();
        }
        else if(mode == EnumMainMode.CREATE)
        {
            BuildTemplateMode.updateCoordInfoDisplay();
            BuildTemplateMode.clearMouseOffset();
        }

        refreshCoordInfoDisplay = true;

    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        if(sizeCount.isVisible() || startPos.isVisible())
        {
            if(startBlockPos!= null)
                startPos.setText("STRT: " + startBlockPos.getX() + " " + startBlockPos.getY() + " " + startBlockPos.getZ());
            else
                startPos.setText("STRT: --- --- ---");

            if(middleBlockPos!= null)
                middlePos.setText("MIDD: " + middleBlockPos.getX() + " " + middleBlockPos.getY() + " " + middleBlockPos.getZ());
            else
                middlePos.setText("MIDD: --- --- ---");

            if(endBlockPos!= null)
                endPos.setText("END : " + endBlockPos.getX() + " " + endBlockPos.getY() + " " + endBlockPos.getZ());
            else
                endPos.setText("END : --- --- ---");

            if(offsetBlockPos!= null)
                offset.setText("OFFS: " + offsetBlockPos.getX() + " " + offsetBlockPos.getY() + " " + offsetBlockPos.getZ());
            else
                offset.setText("OFFS: --- --- ---");

            sizeCount.setText("SIZE: " + sizeX + " " + sizeY+ " " + sizeZ );
            blockCount.setText("BLKS: " + blockCounter);
            fpsCounter.setText("FPS: " + FpsOptimiser.currFps);


            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();

            if(ConfigurationHandler.absCoordConfig.SHOW_ABS_MOUSE_COORD && allowShowPlacedCoord)
            {
                if(!prevShowPointedCoord)
                {
                    placePointedCoord.setVisibility(true);
                    deletePointedCoord.setVisibility(true);
                }
                prevShowPointedCoord = true;

                // some code as test to change the position of the placePointedCoordinate on screen to the new offset position
//                Vec3d offsetVec= new Vec3d(offsetBlockPos.getX(),offsetBlockPos.getY(),offsetBlockPos.getY());
//                Vec3d cursorOffset = Vec3d.ZERO;
//                if(offsetVec.length() >= 1.0)
//                    cursorOffset = IsometricCamera.CAMERA_LOOK_VECTOR.crossProduct(offsetVec.normalize());

//                int placedPointedTextPosX = x_resized + (int) (cursorOffset.z*width);
//                int placedPointedTextPosY = y_resized - (int) (cursorOffset.x * height);

                int placedPointedTextPosX = x_resized;
                int placedPointedTextPosY = y_resized;

                if(GuiOverlayManager.isCrosshairVisible())
                {
                    placePointedCoord.setX(GuiOverlayManager.getCrosshairX()+5);
                    placePointedCoord.setY(GuiOverlayManager.getCrosshairY()-1);
                    deletePointedCoord.setX(GuiOverlayManager.getCrosshairX()+5);

                    if(!offsetPointedText.isVisible())
                    {
                        offsetPointedCoord.setVisibility(true);
                        offsetPointedText.setVisibility(true);
                    }

                    offsetPointedCoord.setText("" + offsetBlockPos.getX() + " " + offsetBlockPos.getY() + " " + offsetBlockPos.getZ());

                    offsetPointedText.setX(x_resized);
                    offsetPointedText.setY(y_resized+5);
                    offsetPointedCoord.setX(offsetPointedText.getX());
                    offsetPointedCoord.setY(offsetPointedText.getY()+6);

                }
                else
                {
                    if(offsetPointedText.isVisible())
                    {
                        offsetPointedText.setVisibility(false);
                        offsetPointedCoord.setVisibility(false);
                    }
                    placePointedCoord.setX(placedPointedTextPosX);
                    placePointedCoord.setY(placedPointedTextPosY-6);
                    deletePointedCoord.setX(placedPointedTextPosX);
//                    placePointedCoord.setX(x_resized);
//                    placePointedCoord.setY(y_resized-6);
//                    deletePointedCoord.setX(x_resized);
                }

                if(placeCoordBlockPos != null)
                {
                    placePointedCoord.setText(" "+ placeCoordBlockPos.getX() + " " + placeCoordBlockPos.getY() + " " +placeCoordBlockPos.getZ());
                    deletePointedCoord.setY(placePointedCoord.getY()- 6);
                }
                else
                {
                    placePointedCoord.setText("");
                    deletePointedCoord.setY(placePointedCoord.getY());
                }

                if(deleteCoordBlockPos != null)
                    deletePointedCoord.setText(" "+ deleteCoordBlockPos.getX() + " " + deleteCoordBlockPos.getY() + " " + deleteCoordBlockPos.getZ());
                else
                    deletePointedCoord.setText("");

                if(deleteCoordBlockPos != null && placeCoordBlockPos != null)
                {
                    if(deleteCoordBlockPos.getX() != placeCoordBlockPos.getX())
                        deletePointedCoord.setText(" "+ deleteCoordBlockPos.getX());
                    else if(deleteCoordBlockPos.getY() != placeCoordBlockPos.getY())
                    {
                        String toBeMeasured = " " +  placeCoordBlockPos.getX() + " ";
                        int pixelLength = (int) (Minecraft.getMinecraft().fontRenderer.getStringWidth(toBeMeasured)*0.60);
                        deletePointedCoord.setX(deletePointedCoord.getX() + pixelLength);
                        deletePointedCoord.setText(String.valueOf( deleteCoordBlockPos.getY()));
                    }
                    else if(deleteCoordBlockPos.getZ() != placeCoordBlockPos.getZ())
                    {
                        String toBeMeasured = " " +  placeCoordBlockPos.getX() + " " +  placeCoordBlockPos.getY() + " ";
                        int pixelLength = (int) (Minecraft.getMinecraft().fontRenderer.getStringWidth(toBeMeasured)*0.60);
                        deletePointedCoord.setX(deletePointedCoord.getX() + pixelLength+ 1);
                        deletePointedCoord.setText(String.valueOf( deleteCoordBlockPos.getZ()));
                    }
                }
            }
            else
            {
                placePointedCoord.setVisibility(false);
                deletePointedCoord.setVisibility(false);
                prevShowPointedCoord = false;
            }


            int buttonSize = 20;
            boolean changedPos = false;

            if(ConfigurationHandler.absCoordConfig.SHOW_ABS_CAMERA_FOCUS_COORD)
            {
                if(!cameraFocusPointCoord.isVisible())
                    cameraFocusPointCoord.setVisibility(true);

                String newCameraFocusPointText  = cameraFocusPointCoordPos.getX() + " " + cameraFocusPointCoordPos.getY() + " " + cameraFocusPointCoordPos.getZ();
                cameraFocusPointCoord.setText(newCameraFocusPointText);
                cameraFocusPointCoord.setX(width/2- (int)((mc.fontRenderer.getStringWidth((newCameraFocusPointText))/2.0)*cameraFocusPointCoord.getFontScale()));
                int newYdistance = 10 - (int)(ModEntityRenderer.customCameraDistance/10f);
                cameraFocusPointCoord.setY(height/2 -newYdistance - (int) (10*cameraFocusPointCoord.getFontScale()));
            }
            else
            {
                if(cameraFocusPointCoord.isVisible())
                    cameraFocusPointCoord.setVisibility(false);
            }

            updateTextScale(scaledresolution.getScaleFactor(),width,height);
            if(AdvCreation.mode.equals(EnumMainMode.PLACE))
            {
                if(coordWindow.getX() != (int) Math.ceil(width / 2.0) - 117 + 58*4)
                    refreshCoordInfoDisplay = true;
            }
            else
            {
                if(coordWindow.getX() != (int)(Math.ceil(width/2.0) - 92+4  + buttonSize*9))
                    refreshCoordInfoDisplay = true;
            }


            int maxSizeText =(int) (mc.fontRenderer.getStringWidth("STRT: -1000 200 -1000")*startPos.getFontScale());
            smallScreenText = false;
            if(startPos.getX() + maxSizeText > width)
            {
                smallScreenText = true;
                refreshCoordInfoDisplay = true;
            }

            if(changedPos != prevChangePos || prevMiddleEnabled != middleEnabled || prevEndEnabled != endEnabled || refreshCoordInfoDisplay)
            {
//                int posX = (int)(Math.ceil(width/2.0) - 92+(4)*(guiScale)  + buttonSize*9)  ;
                int posX = (int)(Math.ceil(width/2.0) - 92+4  + buttonSize*9)  ;
                int posBlockCountX =((int)Math.ceil(width/2.0)) -  92 +6 + buttonSize*9 +(int)(((buttonSize)*3 +12)*(2.5/3.0)) ;
                int posY = height - (buttonSize)*2 - 2;
                int posBlockCountY = height- 10;

                if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                {
                    posX = (int) Math.ceil(width / 2.0) - 117 + 58*4 + 2;
                    coordWindow.moveAllTo(posX -2,posY);
                }
                else
                    coordWindow.moveAllTo(posX,posY);

//                posX =  ((int)Math.ceil(width/2.0)) -  92 +(int)(6*(guiScale) )+ buttonSize*9 +(int)(((buttonSize)*3 +(12*(guiScale))  )*(2.5/3.0)) ;;
                if(smallScreenText)
                {
                    posX += 2;
                    posY = height - (buttonSize)*2 -10 ;
                    if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                    {
                        posX = (int) Math.ceil(width / 2.0) - 115 + 58*4;
                        posBlockCountX = posX ;
                        posBlockCountY = height - (buttonSize)*2 -10;
                    }
                    positionTextAboveHotbar = true;
                }
                else
                {
                    posX =  ((int)Math.ceil(width/2.0)) -  92 +6 + buttonSize*9 +(int)(((buttonSize)*3 +12)*(2.5/3.0)) ;
                    posY = height- 10 ;
                    if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                    {
                        posX = (int) Math.ceil(width / 2.0) - 115 + 58*4 +(int)(((buttonSize)*3 +12)*(2.5/3.0)) ;
                        posBlockCountX = posX ;
                        posBlockCountY = height -10;

                    }

                    positionTextAboveHotbar = false;
                }

                blockCount.setFontScale(textScale);
                blockCount.setX(posBlockCountX);
                blockCount.setY(posBlockCountY);

                if(middleEnabled)
                {
                    sizeCount.setFontScale(textScale);
                    sizeCount.setX(blockCount.getX());
                    sizeCount.setY(blockCount.getY() - breakDist);
                    offset.setFontScale(textScale);
                    offset.setX(blockCount.getX());
                    offset.setY(sizeCount.getY() - breakDist);

                    int endPosY = offset.getY()-breakDist;
                    if(smallScreenText)
                    {
                        if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                            endPosY = offset.getY()-breakDist;
                        else
                            endPosY = posY;
                    }


                    endPos.setFontScale(textScale);
                    endPos.setX(posX);
                    endPos.setY(endPosY);
                    middlePos.setFontScale(textScale);
                    middlePos.setVisibility(ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI);
                    middlePos.setX(endPos.getX());
                    middlePos.setY(endPos.getY()-breakDist);
                    startPos.setX(endPos.getX());
                    startPos.setY(middlePos.getY()-breakDist);
                }
                else
                {
                    sizeCount.setFontScale(textScale);
                    sizeCount.setX(blockCount.getX());
                    sizeCount.setY(blockCount.getY() - breakDist);
                    middlePos.setVisibility(false);
                    if(endEnabled)
                    {
                        offset.setFontScale(textScale);
                        offset.setX(blockCount.getX());
                        offset.setY(sizeCount.getY() - breakDist);

                        int endPosY = offset.getY()-breakDist;
                        if(smallScreenText)
                        {
                            if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                                endPosY = offset.getY()-breakDist;
                            else
                                endPosY = posY;
                        }

                        endPos.setFontScale(textScale);
                        endPos.setVisibility(ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI);
                        endPos.setX(posX);
                        endPos.setY(endPosY);
                        startPos.setX(endPos.getX());
                        startPos.setY(endPos.getY()-breakDist);
                    }
                    else
                    {
                        offset.setFontScale(textScale);
                        offset.setX(blockCount.getX());
                        offset.setY(sizeCount.getY() - breakDist);


                        int endPosY = offset.getY()-breakDist;

                        if(smallScreenText)
                        {
                            if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                                endPosY = offset.getY()-breakDist;
                            else
                                endPosY = posY;
                        }

                        startPos.setX(posX);
                        startPos.setY(endPosY);
                        endPos.setVisibility(false);
                    }
                }
                prevEndEnabled = endEnabled;
                prevMiddleEnabled = middleEnabled;
                prevChangePos = changedPos;
            }
            refreshCoordInfoDisplay = false;
        }

        //set toggleShowFpsOptimisationbuttonsButton to the right arrow
        if(displayOptimiseButton)
            toggleShowFpsOptimisationButtonsButton.setY_texture(60);
        else
            toggleShowFpsOptimisationButtonsButton.setY_texture(35);

        if(FpsOptimiser.checkIfOptimisationIsAvailable())
        {
            increaseFpsButton.setTooltip(FpsOptimiser.increaseFpsTooltip());
            decreaseFpsButton.setTooltip(FpsOptimiser.decreaseFpsTooltip());
            //set toggleShowFpsOptimisationbuttonsButton to the right arrow
            if(displayOptimiseButton)
            {
                toggleShowFpsOptimisationButtonsButton.setY_texture(60);
                toggleShowFpsOptimisationButtonsButton.setTooltip("HIDE buttons to improve FPS");
            }
            else
            {
                toggleShowFpsOptimisationButtonsButton.setY_texture(35);
                toggleShowFpsOptimisationButtonsButton.setTooltip("SHOW buttons to improve FPS");
            }


        }
        else
        {
            if(displayOptimiseButton)
                displayOptimiseButton = false;
            toggleShowFpsOptimisationButtonsButton.setY_texture(110);
            toggleShowFpsOptimisationButtonsButton.setTooltip("current mode has no methods for FPS optimisation");
        }
    }

    @Override
    protected void preDrawElements(int x_resized, int y_resized, long time)
    {
        if(window.isHoverOn() ||
               createButton.isHoverOn() ||
               placeButton.isHoverOn() ||
                buildButton.isHoverOn() ||
                    editButton.isHoverOn() )
        {
            if(!firstHovered)
            {
                if(!ConfigurationHandler.general.HIDE_HELPSCREEN_MESSAGE)
                    activateInitialHelpMessage(time);
                firstHovered = true;
            }

        }

        // ### ACTIVATE BUTTON OF MODE ###
        if(AdvCreation.mode == EnumMainMode.BUILD)
        {
            buildButton.setSelected(true);
            placeButton.setSelected(false);
            createButton.setSelected(false);
            editButton.setSelected(false);
        }
        else if(AdvCreation.mode == EnumMainMode.PLACE)
        {
            buildButton.setSelected(false);
            placeButton.setSelected(true);
            createButton.setSelected(false);
            editButton.setSelected(false);
        }
        else if(AdvCreation.mode == EnumMainMode.CREATE)
        {
            buildButton.setSelected(false);
            placeButton.setSelected(false);
            createButton.setSelected(true);
            editButton.setSelected(false);
        }
        else if(AdvCreation.mode == EnumMainMode.EDIT)
        {
            buildButton.setSelected(false);
            placeButton.setSelected(false);
            createButton.setSelected(false);
            editButton.setSelected(true);
        }

        //draw backgrounds for the information displays
        int backgroundColor = 0xF0100010;
        int textsize = (int)  (textScale*10.0);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
//        int posX =  ((int)Math.ceil(width/2.0)) - 92 +4 + (int)( 20*9 +(int)(((20)*3 +12)*(2.5/3.0)));
//        if(AdvCreation.getMode() == EnumMainMode.PLACE)
//            posX =  ((int)Math.ceil(width/2.0)) - 115 +4 + (int)( 58*4 +(int)(((20)*3 +12)*(2.5/3.0)));
        int posX =  (coordWindow.getX() + (int)(((20)*3 +12)*(2.5/3.0)));
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        if(ConfigurationHandler.absCoordConfig.SHOW_ABS_POS_ON_GUI)
        {
            if(positionTextAboveHotbar)
            {
                //position information background
                int posTopY = startPos.getY() -2;
                drawCustomBackground(coordWindow.getX(),posTopY,width,height-42);

                //BlockSize information background
//            int blockCountTopY = sizeCount.getY() ;
//            if(offset != null && offset.isVisible())
//                blockCountTopY = offset.getY();
//            drawCustomBackground(posX,blockCountTopY,width,height);
                drawCustomBackground(posX,height-42,width,height);
            }
            else
            {
                int posTopY = startPos.getY() -2;
                if(posTopY > height-42 && middleEnabled)
                    posTopY = height-42;

                drawCustomBackground(posX,posTopY,width,height);
            }
        }
        else
        {
            int posTopY = sizeCount.getY() - 2;
            if(offset != null && offset.isVisible())
                posTopY = offset.getY() -2;

            if(AdvCreation.getMode() == EnumMainMode.PLACE && positionTextAboveHotbar)
                posX = coordWindow.getX();

            drawCustomBackground(posX,posTopY,width,height);
        }


        if(displayOptimiseButton)
        {
            increaseFpsButton.setVisibility(true);
            decreaseFpsButton.setVisibility(true);
            if(showButtonCountdown >= 0)
            {
                if(showButtonCountdown > 1)
                {
                    increaseFpsButton.setWidth(10-5*(showButtonCountdown-2));
                    decreaseFpsButton.setWidth(0);
                }
                else
                    decreaseFpsButton.setWidth(10-5*(showButtonCountdown));
                showButtonCountdown--;
            }
        }
        else
        {
            increaseFpsButton.setVisibility(false);
            decreaseFpsButton.setVisibility(false);
            showButtonCountdown = 5;
        }




        super.preDrawElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawTooltips(int x_resized, int y_resized, long time)
    {
        if(!displayInitialHelpMessage)
        {
            if(AdvCreation.mode == EnumMainMode.BUILD)
            {
                helpButton.setTooltip("Help for BUILD mode");
            }
            else if(AdvCreation.mode == EnumMainMode.PLACE)
            {
                helpButton.setTooltip("Help for PLACE mode");
            }
            else if(AdvCreation.mode == EnumMainMode.CREATE)
            {
                helpButton.setTooltip("Help for CREATE mode");
            }
            else if(AdvCreation.mode == EnumMainMode.EDIT)
            {
                helpButton.setTooltip("Help for EDIT mode");
            }
        }
        else
        {
            //flickering of the helpbutton when the help message is initialy shown
            if(time%500 > 250)
            {
                CustomGuiUtils.drawHighlight( helpButton, new Color(170,68,0));
            }
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();
            helpButton.setTooltip("Click here for instruction on how to do things for each mode");
            helpButton.drawTooltip(helpButton.getX() + 10,helpButton.getY() + 10,width,height);
            if(time-startTimeHelpMessage > timeLimitHelpMessage)
            {
                displayInitialHelpMessage = false;
            }

        }

        Color color = new Color(0,0,0);
        if(!fpsOptimisationFeedback.isEmpty())
        {
            timeLimitFpsOptimisationFeedback = 5000;
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int screenWidth = scaledresolution.getScaledWidth();
            int screenHeight = scaledresolution.getScaledHeight();

            increaseFpsButton.setTooltipOn(false);
            decreaseFpsButton.setTooltipOn(false);

            drawHoveringText(fpsOptimisationFeedback,sideWindow.getX(),sideWindow.getY() + sideWindow.getHeight()+15,screenWidth,screenHeight,screenWidth/2,mc.fontRenderer);
            if(time - startTimeFpsOptimisationFeedback > timeLimitFpsOptimisationFeedback)
            {
                fpsOptimisationFeedback.clear();
                increaseFpsButton.setTooltipOn(true);
                decreaseFpsButton.setTooltipOn(true);
            }
        }
        else if(displayOptimiseButton && displayOptimiseHelpMessage && showButtonCountdown <= 0)
        {
            if(startTimeFpsOptimiseMessage == 0)
                startTimeFpsOptimiseMessage = time;

            color = new Color(170,68,0);
            CustomGuiUtils.drawHighlight(increaseFpsButton, color);

            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int screenWidth = scaledresolution.getScaledWidth();
            int screenHeight = scaledresolution.getScaledHeight();
            increaseFpsButton.setTooltip("The current tool is sinking your FPS. Click here to reduce the preview/blockCount limit to get better FPS.");
            increaseFpsButton.drawTooltip(increaseFpsButton.getX() + 10, increaseFpsButton.getY() + 10,screenWidth,screenHeight);
            if(time-startTimeFpsOptimiseMessage > timeLimitFpsOptimiseMessage)
            {
                displayOptimiseHelpMessage = false;
                startTimeFpsOptimiseMessage = 0;
            }
            fpsOptimiseMessageShownOnceAlready = true;

        }
        CustomGuiUtils.drawHighlight( fpsBackground, color);

        super.preDrawTooltips(x_resized, y_resized, time);
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(element != null)
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            if (element.getName().equals(buildButton.getName()))
                AdvCreation.setMode(EnumMainMode.BUILD);
            else if (element.getName().equals(placeButton.getName()))
                AdvCreation.setMode(EnumMainMode.PLACE);
            else if (element.getName().equals(createButton.getName()))
                AdvCreation.setMode(EnumMainMode.CREATE);
            else if (element.getName().equals(editButton.getName()))
                AdvCreation.setMode(EnumMainMode.EDIT);
            else if (element.getName().equals(helpButton.getName()))
                Minecraft.getMinecraft().displayGuiScreen(new GuiHelpScreenVisual());
            else if (element.getName().equals(setAbsCoordButton.getName()) )
                AbsoluteCoordScreen.openAbsoluteCoordScreen();
            else if (element.getName().equals(clearOffsetButton.getName()))
            {
                if(AdvCreation.getMode().equals(EnumMainMode.BUILD))
                    BuildMode.clearMouseOffset();
                else if(AdvCreation.getMode().equals(EnumMainMode.CREATE))
                    BuildTemplateMode.clearMouseOffset();
                else if(AdvCreation.getMode().equals(EnumMainMode.PLACE))
                    PlaceTemplateMode.clearMouseOffset();

            }
            else if (element.getName().equals(toggleShowFpsOptimisationButtonsButton.getName()))
            {
                if(FpsOptimiser.checkIfOptimisationIsAvailable())
                {
                    displayOptimiseButton = !displayOptimiseButton;
                    if(displayOptimiseHelpMessage)
                        displayOptimiseHelpMessage = false;
                }

            }
            else if (element.getName().equals(increaseFpsButton.getName()))
            {
                fpsOptimisationFeedback = FpsOptimiser.increaseFps();
                startTimeFpsOptimisationFeedback = time;
                //                FpsOptimiser.startOptimisation();
            }
            else if (element.getName().equals(decreaseFpsButton.getName()))
            {
                fpsOptimisationFeedback = FpsOptimiser.decreaseFps();
                startTimeFpsOptimisationFeedback = time;
            }

        }

    }

    public void setHelpSelected(boolean bool)
    {
        helpButton.setSelected(bool);
    }

    public void activateInitialHelpMessage(long time)
    {
        displayInitialHelpMessage = true;
        startTimeHelpMessage = time;
    }

    public void setCount(int X, int Y, int Z, int blocks)
    {
        sizeX = X;
        sizeY = Y;
        sizeZ = Z;
        blockCounter = blocks;
        setCountVisibility(true);
    }

    public void setCountVisibility(boolean visible)
    {
        if(sizeCount != null && blockCount != null)
        {
            sizeCount.setVisibility(visible);
            blockCount.setVisibility(visible);
        }

    }

    public void setPosVisibility(boolean visible)
    {
        if(displayPosCoord || (!displayPosCoord && !visible))
        {
            if(sizeCount != null && blockCount != null)
            {
                startPos.setVisibility(visible);

                if(endEnabled  == visible)
                    endPos.setVisibility(visible);
                else
                    endPos.setVisibility(false);

                if(middleEnabled == visible)
                    middlePos.setVisibility(visible);
                else
                    middlePos.setVisibility(false);
            }
        }


    }

    public int getButtonWidth()
    {
        return buttonWidth;
    }

    public void setButtonWidth(int buttonWidth)
    {
        this.buttonWidth = buttonWidth;
    }

    public BlockPos getStartBlockPos()
    {
        return startBlockPos;
    }

    public void setStartBlockPos(BlockPos startBlockPos)
    {
        this.startBlockPos = startBlockPos;
        this.setPosVisibility(true);
    }

    public BlockPos getMiddleBlockPos()
    {
        return middleBlockPos;
    }

    public void setMiddleBlockPos(BlockPos middleBlockPos)
    {
        if(middleEnabled)
        {
            this.middleBlockPos = middleBlockPos;
            this.setPosVisibility(true);
        }

    }

    public BlockPos getEndBlockPos()
    {
        return endBlockPos;
    }

    public void setEndBlockPos(BlockPos endBlockPos)
    {
        if(endEnabled)
        {
            this.endBlockPos = endBlockPos;
            this.setPosVisibility(true);
        }

    }

    public boolean isMiddleEnabled()
    {
        return middleEnabled;
    }

    public void setMiddleEnabled(boolean middleEnabled)
    {
        this.middleEnabled = middleEnabled;
    }

    public BlockPos getOffsetBlockPos()
    {
        return offsetBlockPos;
    }

    public void setOffsetBlockPos(BlockPos offsetBlockPos)
    {
        this.offsetBlockPos = offsetBlockPos;
    }

    public boolean isEndEnabled()
    {
        return endEnabled;
    }

    public void setEndEnabled(boolean endEnabled)
    {
        this.endEnabled = endEnabled;
    }

    public BlockPos getPlaceCoordBlockPos()
    {
        return placeCoordBlockPos;
    }

    public void setPlaceCoordBlockPos(BlockPos placeCoordBlockPos)
    {
        this.placeCoordBlockPos = placeCoordBlockPos;
    }

    public BlockPos getDeleteCoordBlockPos()
    {
        return deleteCoordBlockPos;
    }

    public void setDeleteCoordBlockPos(BlockPos deleteCoordBlockPos)
    {
        this.deleteCoordBlockPos = deleteCoordBlockPos;
    }

    public BlockPos getCameraFocusPointCoordPos()
    {
        return cameraFocusPointCoordPos;
    }

    public void setCameraFocusPointCoordPos(BlockPos cameraFocusPointCoordPos)
    {
        this.cameraFocusPointCoordPos = cameraFocusPointCoordPos;
    }

    public void setSetAbsCoordButtonEnabled(boolean enable)
    {
        this.setAbsCoordButton.setEnabled(enable);
    }

    public boolean isDisplayPosCoord()
    {
        return displayPosCoord;
    }

    public void setDisplayPosCoord(boolean displayPosCoord)
    {
        this.displayPosCoord = displayPosCoord;
    }

    public void drawCustomBackground(int left,int top, int right,int bottom )
    {
        this.drawGradientRect(left,top, right,bottom, 0x44000000, 0x44000000);

//        GlStateManager.disableLighting();
//        GlStateManager.disableFog();
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferbuilder = tessellator.getBuffer();
//        this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        float f = 32.0F;
//        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
//        bufferbuilder.pos((double)left, (double)top, 0.0D).tex(0.0D, (double)((float)( bottom-top) / 32.0F + (float)0.0D)).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.pos((double)right, (double)top, 0.0D).tex((double)((float)(right-left) / 32.0F), (double)((float)( bottom-top) / 32.0F + (float)0.0D)).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.pos((double)right,(double) bottom, 0.0D).tex((double)((float)(right-left) / 32.0F), (double)0.0D).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.pos((double)left, (double)bottom, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
//        tessellator.draw();
    }


    public void setAllowShowPlacedCoord(boolean allowShowPlacedCoord)
    {
        this.allowShowPlacedCoord = allowShowPlacedCoord;
    }

    public boolean isAllowShowPlacedCoord()
    {
        return allowShowPlacedCoord;
    }

    public void showFpsOptimiseButton()
    {
        displayOptimiseButton = true;

        if(!ConfigurationHandler.general.HIDE_LOW_FPS_MESSAGE &&  !fpsOptimiseMessageShownOnceAlready)
        {
            displayOptimiseHelpMessage = true;
        }
    }

    public void hideFpsOptimiseButton()
    {
        displayOptimiseButton = false;
        if(!displayOptimiseHelpMessage)
            displayOptimiseHelpMessage = false;}
}
