package com.deadtiger.advcreation.client.gui.gui_screen.customIngameMenu;

import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.GuiFreeToggleButton;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.util.List;

public class GuiCustomIngameMenu
{
    public static GuiButton gameModeButton;
    public static GuiButton perspectiveButton;
    public static GuiButton isometricButton;
    public static GuiButton ignorePlantsButton;
    public static GuiButton ignoreLiquidsButton;
    public static int CURR_THIRD_PERSON_VIEW;

    private static long firstHoverOverTime = 0;

    private static String perspectiveDisplayStrings[] = {"First-Person","Third-Person","Third-Person F","Isometric"};
    private static String isometricDisplayStrings[] = {"Normal View","Normal View","Normal View","Isometric View"};

    public static void initGui(List<GuiButton> buttonList, int width, int height)
    {
        int y = height / 4 + 8 + -20;
        for (GuiButton button: buttonList)
        {
            if(button.id == 1)
            {
                y = button.y;
                button.y = y + 24*2;
            }
        }
        Minecraft mc = Minecraft.getMinecraft();
        //gamemode button
        String gamemodeText = "CREATIVE gamemode";
        if(!mc.player.isCreative())
        {
            gamemodeText = "SURVIVAL gamemode";
        }

        if(gameModeButton == null)
        {
            gameModeButton = new GuiButton(9, width / 2 - 100, y, 200, 20,gamemodeText);
        }
        else
        {
            gameModeButton.x =  width / 2 - 100;
//            perspectiveButton.y = height / 4 + 120 + -16;
            gameModeButton.y = y;
            gameModeButton.displayString =  gamemodeText;

        }
        y += 24;

        //ignore plants button
        if(ignorePlantsButton == null)
        {
            ignorePlantsButton = new GuiFreeToggleButton(8,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),width / 2 + 102 +5 + 10, y+10,20,20,0,90,20,90,0,120,20,120);
        }
        else
        {
            ignorePlantsButton.x = width / 2 + 102 +5;
            ignorePlantsButton.y = y;
        }
        ((GuiFreeToggleButton) ignorePlantsButton).setActive(IsometricCamera.isLeafRaytracingDisabled());
        ignorePlantsButton.visible = false;
        if(((GuiFreeToggleButton)ignorePlantsButton).isActive())
            ignorePlantsButton.displayString = ("Current State: Ignore Plants In Selection (" + Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind().getDisplayName() + ")");
        else
            ignorePlantsButton.displayString = ("Current State: Include Plants In Selection (" + Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind().getDisplayName() + ")");


//        gameModeButton = new GuiButton(9, width / 2 - 100, y ,200,20, gamemodeText);
//
//        //IgnorePlants button
//        ignorePlantsButton = new GuiFreeToggleButton(8,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),width / 2 + 102 +5+10, y+10 + 24,20,20,0,90,20,90,0,120,20,120);
//        ((GuiFreeToggleButton)ignorePlantsButton).active = IsometricCamera.IGNORE_PLANTS;
//        ((GuiFreeToggleButton)ignorePlantsButton).visible = false;

        //Ignore liquids button
        if(ignoreLiquidsButton == null)
        {
            ignoreLiquidsButton = new GuiFreeToggleButton(8,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),width / 2 + 102 +5+10 + 22, y+10,20,20,60,90,80,90,60,120,80,120);
        }
        else
        {
            ignoreLiquidsButton.x = width / 2 + 100 +5+10 + 20;
            ignoreLiquidsButton.y = y;
        }

        ((GuiFreeToggleButton)ignoreLiquidsButton).active = IsometricCamera.IGNORE_FLUIDS;
        ignoreLiquidsButton.visible = false;

        if(((GuiFreeToggleButton)ignoreLiquidsButton).isActive())
            ignoreLiquidsButton.displayString = "Current State: Ignore Liquids In Selection (" + Keybindings.TOGGLE_IGNORE_LIQUIDS.getKeybind().getDisplayName() + ")";
        else
            ignoreLiquidsButton.displayString = "Current State: Include Liquids  In Selection (" + Keybindings.TOGGLE_IGNORE_LIQUIDS.getKeybind().getDisplayName() + ")";

        //Perspective button
        String perspective = "First-Person View (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";
        CURR_THIRD_PERSON_VIEW = IsometricCamera.getThirdPersonViewSetting();
        if(Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode())
        {
            perspective = perspectiveDisplayStrings[CURR_THIRD_PERSON_VIEW] + " (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";
             if (CURR_THIRD_PERSON_VIEW == 3)
             {
                ((GuiFreeToggleButton) ignorePlantsButton).visible = true;
                ignoreLiquidsButton.visible = true;
            }
        }
        else
        {
            int index = CURR_THIRD_PERSON_VIEW < 3 ? CURR_THIRD_PERSON_VIEW: IsometricCamera.previousNormalView;
            perspective = perspectiveDisplayStrings[index ] + " (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";
        }

//        perspectiveButton = new GuiButton(8, width / 2 - 100, y + 24,200,20, perspective);

//        if(perspectiveButton == null)
//        {
            //        }
//        else
//        {
//            perspectiveButton.x =  width / 2 - 100;
////            perspectiveButton.y = height / 4 + 120 + -16;
//            perspectiveButton.y = y;
//            perspectiveButton.displayString =  perspective;
//        }
        isometricButton = null;
        if(Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode())
        {
            perspectiveButton = new GuiButton(8,width / 2 - 100, y, 200, 20,perspective);
        }
        else
        {
            String isometric = isometricDisplayStrings[CURR_THIRD_PERSON_VIEW] + " (" + Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getDisplayName() + ")";


            perspectiveButton = new GuiButton(8,width / 2 - 100, y, 98, 20,perspective);
            if( CURR_THIRD_PERSON_VIEW == 3)
                perspectiveButton.enabled = false;
            isometricButton = new GuiButton(8,width / 2 + 2, y, 98, 20,isometric);

            if( CURR_THIRD_PERSON_VIEW == 3)
            {
                ((GuiFreeToggleButton)ignorePlantsButton).visible = true;
                ignoreLiquidsButton.visible = true;
                perspectiveButton.enabled = false;
            }
            if(!Minecraft.getMinecraft().player.isCreative())
                isometricButton.enabled = false;

            buttonList.add(isometricButton);
        }

        buttonList.add(ignorePlantsButton);
        buttonList.add(gameModeButton);
        buttonList.add(ignoreLiquidsButton);
        buttonList.add(perspectiveButton);
    }


    public static void actionPerformed(GuiButton button, GuiScreen guiScreen)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(button == perspectiveButton)
            togglePerspectiveButton(button, mc);
        else if(button == ignorePlantsButton)
            IsometricCamera.IGNORE_PLANTS = ((GuiFreeToggleButton)ignorePlantsButton).active;
        else if(button == gameModeButton)
            toggleGameMode(button,mc,guiScreen);
        else if(button == ignoreLiquidsButton)
        {
            IsometricCamera.IGNORE_FLUIDS =  ((GuiFreeToggleButton)button).isActive();
            if(((GuiFreeToggleButton)button).isActive())
                button.displayString =  "Current State: Ignore Liquids In Selection (" + Keybindings.TOGGLE_IGNORE_LIQUIDS.getKeybind().getDisplayName() + ")";
            else
                button.displayString =  "Current State: Include Liquids In Selection (" + Keybindings.TOGGLE_IGNORE_LIQUIDS.getKeybind().getDisplayName() + ")";

        }
        else if(button == isometricButton)
        {

            ((GuiFreeToggleButton)ignorePlantsButton).visible = false;
            ignoreLiquidsButton.visible = false;
            String perspective = "First-Person View (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";
            if( CURR_THIRD_PERSON_VIEW == 3)
            {
                CURR_THIRD_PERSON_VIEW = IsometricCamera.previousNormalView;
                perspectiveButton.enabled = true;
                perspective = isometricDisplayStrings[0] + " (" + Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getDisplayName() + ")";
            }
            else
            {

                IsometricCamera.previousNormalView = CURR_THIRD_PERSON_VIEW;
                ((GuiFreeToggleButton)ignorePlantsButton).visible = true;
                ignoreLiquidsButton.visible = true;
                CURR_THIRD_PERSON_VIEW =  3;
                perspectiveButton.enabled = false;
                perspective = isometricDisplayStrings[3] + " (" + Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getDisplayName() + ")";

            }


            IsometricCamera.newThirdPersonViewValue =  CURR_THIRD_PERSON_VIEW;
            IsometricCamera.CHANGED_PERSPECTIVE = true;

            button.displayString = perspective;
        }
    }

    private static void toggleGameMode(GuiButton button,Minecraft mc,GuiScreen guiScreen)
    {
        EntityPlayer entityplayer = mc.player;
        boolean isCreative = entityplayer.isCreative();

        String command = "/gamemode c";
        String gamemodeText = "CREATIVE gamemode";
        if(isCreative)
        {
            gamemodeText = "SURVIVAL gamemode";
            command = "/gamemode s";
            //disable the isometric button when in survival when the isometric button is not the same as the perspective
            if(isometricButton != null)
                isometricButton.enabled = false;
            perspectiveButton.enabled = true;

        }
        else
        {
            if(isometricButton != null)
                isometricButton.enabled = true;
            if(Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getKeyCode() != Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode())
                perspectiveButton.enabled = false;

            if(ConfigurationHandler.general.ENTER_CREATIVE_IN_ISOMETRIC)
            {
                IsometricCamera.previousNormalView = CURR_THIRD_PERSON_VIEW;
                ((GuiFreeToggleButton)ignorePlantsButton).visible = true;
                ignoreLiquidsButton.visible = true;
                CURR_THIRD_PERSON_VIEW =  3;
                if(isometricButton != null) isometricButton.displayString = isometricDisplayStrings[CURR_THIRD_PERSON_VIEW] + " (" + Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getDisplayName() + ")";
                IsometricCamera.newThirdPersonViewValue =  CURR_THIRD_PERSON_VIEW;
                IsometricCamera.CHANGED_PERSPECTIVE = true;
            }
        }

        guiScreen.sendChatMessage(command,false);

        gameModeButton.displayString = gamemodeText;



        //send feedback to user
//        ITextComponent itextcomponent = new TextComponentTranslation("gameMode." + gameType.getName(), new Object[0]);
//        entityplayer.sendMessage(new TextComponentTranslation("gameMode.changed", new Object[]{itextcomponent}));

    }

    private static void togglePerspectiveButton(GuiButton button, Minecraft mc)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode())
        {
            CURR_THIRD_PERSON_VIEW =  CURR_THIRD_PERSON_VIEW + 1;
            if(minecraft.playerController != null && minecraft.playerController.getCurrentGameType() != null &&(minecraft.playerController.getCurrentGameType().equals(GameType.CREATIVE)) )
            {
                if ( CURR_THIRD_PERSON_VIEW > 3)
                    CURR_THIRD_PERSON_VIEW = 0;
            }
            else
            {
                if ( CURR_THIRD_PERSON_VIEW > 2)
                    CURR_THIRD_PERSON_VIEW = 0;
            }

            ((GuiFreeToggleButton)ignorePlantsButton).visible = false;
            ignoreLiquidsButton.visible = false;
            String perspective = perspectiveDisplayStrings[CURR_THIRD_PERSON_VIEW] + " (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";
            if( CURR_THIRD_PERSON_VIEW == 3)
            {
                ((GuiFreeToggleButton)ignorePlantsButton).visible = true;
                ignoreLiquidsButton.visible = true;
            }
            IsometricCamera.newThirdPersonViewValue =  CURR_THIRD_PERSON_VIEW;
            IsometricCamera.CHANGED_PERSPECTIVE = true;

            button.displayString = perspective;
        }
        else
        {
            CURR_THIRD_PERSON_VIEW =  CURR_THIRD_PERSON_VIEW + 1;

            if ( CURR_THIRD_PERSON_VIEW > 2)
                CURR_THIRD_PERSON_VIEW = 0;


            ((GuiFreeToggleButton)ignorePlantsButton).visible = false;
            ignoreLiquidsButton.visible = false;
            int index = CURR_THIRD_PERSON_VIEW < 3 ? CURR_THIRD_PERSON_VIEW: IsometricCamera.previousNormalView;
            String perspective = perspectiveDisplayStrings[index ] + " (" + mc.gameSettings.keyBindTogglePerspective.getDisplayName() + ")";

            IsometricCamera.newThirdPersonViewValue =  CURR_THIRD_PERSON_VIEW;
            IsometricCamera.CHANGED_PERSPECTIVE = true;

            button.displayString = perspective;
        }



    }

    public static void drawTooltips(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        int mouseX = event.getMouseX();
        int mouseY =  event.getMouseY();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledResolution.getScaledWidth();
        int heigth = scaledResolution.getScaledHeight();



        long time = System.currentTimeMillis();
        if(ignorePlantsButton.visible && ignorePlantsButton.isMouseOver())
        {
            if(time-firstHoverOverTime > 500)
            {
                if(((GuiFreeToggleButton)ignorePlantsButton).active)
                    CustomGuiUtils.drawHoveringText("Current State: Ignore Plants In Selection (" + Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind().getDisplayName() + ")", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
                else
                    CustomGuiUtils.drawHoveringText("Current State: Include Plants In Selection (" + Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind().getDisplayName() + ")", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
            }
        }
        else  if(ignoreLiquidsButton != null && ignoreLiquidsButton.visible && ignoreLiquidsButton.isMouseOver())
        {
            if(time-firstHoverOverTime > 500)
            {

                CustomGuiUtils.drawHoveringText(ignoreLiquidsButton.displayString, mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
            }
        }
        else if(perspectiveButton.enabled && perspectiveButton.isMouseOver())
        {
            if(time-firstHoverOverTime > 500)
            {
                if(Minecraft.getMinecraft().player.isCreative())
                {
                    CustomGuiUtils.drawHoveringText("Toggle Perspective", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
                }
//                else
//                    CustomGuiUtils.drawHoveringText("Isometric View is only available in the CREATIVE gamemode", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
            }
        }
        else if(isometricButton != null && isometricButton.isMouseOver())
        {
            if(time-firstHoverOverTime > 500)
            {
                if(Minecraft.getMinecraft().player.isCreative())
                {
                    CustomGuiUtils.drawHoveringText("Toggle Isometric View", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
                }
                else
                    CustomGuiUtils.drawHoveringText("Isometric View is only available in the CREATIVE gamemode", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
            }
        }
        else if(gameModeButton != null && gameModeButton.isMouseOver())
        {
            if(time-firstHoverOverTime > 500)
            {
                if(Minecraft.getMinecraft().player.isCreative())
                {
                    CustomGuiUtils.drawHoveringText("Toggle Isometric View", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
                }
                else
                    CustomGuiUtils.drawHoveringText("Isometric View is only available in the CREATIVE gamemode", mouseX+10, mouseY-3,width,heigth,-1,fontRenderer);
            }
        }
        else
            firstHoverOverTime = time;
    }
}
