package com.deadtiger.advcreation.client.event;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.CopyPasteToolMode;
import com.deadtiger.advcreation.build_mode.tool_mode.MoveToolMode;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.build_template.TemplateBuildingMode;
import com.deadtiger.advcreation.client.FpsOptimiser;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.configWarningScreen.ConfigWarningScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.customIngameMenu.GuiCustomIngameMenu;
import com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel.GuiAdjustModeSelectionScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel.GuiToolModeSelectionScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplateInventoryScreenSimple;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.input.MouseInputHandler;
import com.deadtiger.advcreation.client.input.Mousebindings;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.player.IsometricMovement;
import com.deadtiger.advcreation.client.player.SPPlayerProperties;
import com.deadtiger.advcreation.client.render.RenderCutThrough;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.debug.DebugInfo;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.PaintAdjustMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.PaintBucketAdjustMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.logging.Logging;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.network.message.MessageClientRequestToEditSign;
import com.deadtiger.advcreation.network.message.MessagePreviewListsTemplateBlock;
import com.deadtiger.advcreation.network.message.MessageTriggerClickEvent;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.ridingentity.FakeEntityBoat;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = Reference.MODID,value=Side.CLIENT)
@SideOnly(Side.CLIENT)
public class ClientEventHandler
{

    public static boolean previouslyInGame = false;

    public static float renderTickTime = 0F;
    public static boolean blockHighlightEventTriggeredThisTick = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptCameraSetup(EntityViewRenderEvent.CameraSetup event)
    {
        if (IsometricCamera.isPlayerInIsometricPerspective()){
            // roll arround the XZ axis = 0
            event.setRoll(0.0F);
            // pitch arround the XY axis = 0
            event.setPitch((float) ConfigurationHandler.cameraConfig.X_angle);
            // Yaw arround the ZY axis = 0z
            event.setYaw( (float) ConfigurationHandler.cameraConfig.Y_angle);
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientOpenModConfig(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        if(event.getGui() != null)
        {

            GuiScreen screen  = event.getGui();

            //your in the mod listing screen and you press the config button of advanced creation
            if(screen instanceof GuiModList && event.getButton().id == 20)
            {
                GuiModList modList = (GuiModList) screen;

                ModContainer modContainer =  ConfigWarningScreen.getModContainer(modList);


                if(modContainer.getModId().equals(Reference.MODID))
                {
                    Minecraft.getMinecraft().displayGuiScreen(new ConfigWarningScreen(screen.mc.currentScreen, modContainer));
                }

            }
        }

    }

    /**
     * This event is triggered when the player right clicks with nothing in hand AND not hitting any block!
     *
     * @param event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
            if (IsometricCamera.isPlayerInIsometricPerspective()  && ConfigurationHandler.general.TOOLS_ENABLED)
            {
                int X_mouse = Mouse.getX();
                int Y_mouse = Mouse.getY();

                boolean guiOverlayClicked = GuiOverlayManager.isGuiOverlayRightClicked(event.getWorld().isRemote, X_mouse, Y_mouse);

                if (!guiOverlayClicked)
                {
                    if (AdvCreation.mode == EnumMainMode.CREATE)
                    {
                        BuildTemplateMode.cancelTemplate();
                    }
                }
                AdvCreation.rightClickDownClient = true;
            }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getEntityPlayer().world.isRemote)
        {
            if (IsometricCamera.isPlayerInIsometricPerspective())
            {
                if (event.getHand() == EnumHand.MAIN_HAND)
                {
                    //check if the clicked screen position is on a button in the GUI overlay
                    int X_mouse = Mouse.getX();
                    int Y_mouse = Mouse.getY();

                    boolean guiOverlayClicked = GuiOverlayManager.isGuiOverlayRightClicked(event.getWorld().isRemote, X_mouse, Y_mouse);
                    event.setCanceled(guiOverlayClicked);


                    if (!guiOverlayClicked )
                    {
                        if(!ConfigurationHandler.general.TOOLS_ENABLED)
                        {
                            event.setCanceled(false);
                            return;
                        }

                        Item item = event.getEntityPlayer().getHeldItemMainhand().getItem();

                        if ((item instanceof ItemBlock  && PlacementHelper.isNotIllegalBlockForRightClick(((ItemBlock) item).getBlock())) ||
                                PlacementHelper.isAllowedNonItemBlocks(item) || AdvCreation.getMode() == EnumMainMode.PLACE )
                        {
                            if (!NetworkManager.CLIENT_IS_SENDING_BLOCKS && !NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
                            {
                                //cancel if buildmode.adjustMode is anything but SINGLE_TOOL
                                boolean cancel = false;
                                if (AdvCreation.mode.equals(EnumMainMode.BUILD))
                                {
                                    cancel = BuildMode.cancelRightClick();
                                    event.setCanceled(cancel);
                                }
                                else if (AdvCreation.mode.equals(EnumMainMode.EDIT))
                                {
                                    cancel = EditMode.cancelRightClick();
                                    event.setCanceled(cancel);
                                }

                                //execute the action corresponding to the right mode
                                if (AdvCreation.mode.equals(EnumMainMode.BUILD))
                                {
                                    Minecraft mc = Minecraft.getMinecraft();
                                    if (PlacementHelper.isInteractableBlock(mc.world.getBlockState(event.getPos()).getBlock(), event.getPos())
                                            && BuildMode.RIGHT_CLICK_NUMBER == 0)
                                    {
                                        if(mc.world.getBlockState(event.getPos()).getBlock() instanceof BlockSign)
                                        {
//                                            HelpFunctions.editPlacedSign(mc.player.getHeldItemMainhand(),mc.world, event.getPos(),mc.player);
                                            NetworkHandler.sendSignEditRequestToServer(new MessageClientRequestToEditSign(event.getPos()));
                                        }
                                        else
                                        {
                                            //so if your have rightclicked a door and you are not in the middle of an action
                                            // then the door is clicked normally meaning the event is not canceled
                                            event.setCanceled(false);
                                        }
                                    }
                                    else
                                    {


                                        if ((item instanceof ItemBlock && !(((ItemBlock) item).getBlock() instanceof BlockTripWire)) || BuildMode.allowRightClickException(item) || PlacementHelper.isAllowedNonItemBlocks(item))
                                        {
                                            //Save the blockstates of the blocks that are being replace for the undo feature
                                            Action currAction = new Action();
                                            Logging.logClick("rightclick on ", BuildMode.TOOLMODE.toolModeName, BuildMode.RIGHT_CLICK_NUMBER, BuildMode.DELETE_MODE, BuildMode.TOOLMODE.identificationIndex, 1);

                                            if (BuildMode.rightClick(currAction, event.getEntityPlayer()))
                                            {

                                                //means the right click did something and that action is being saved
                                                if (!currAction.getPreviousBlockStates().isEmpty())
                                                    UndoFunctionality.addActionToHistory(currAction);
                                            }
                                        }
                                        else
                                            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Select Block to execute this action", true, true);
                                    }

//
                                }
                                else if (AdvCreation.mode.equals(EnumMainMode.EDIT))
                                {
                                    Minecraft mc = Minecraft.getMinecraft();
                                    if ((PlacementHelper.isInteractableBlock(mc.world.getBlockState(event.getPos()).getBlock(), event.getPos()))
                                            && EditMode.RIGHT_CLICK_NUMBER == 0)
                                        event.setCanceled(false);
                                    else
                                    {
                                        if (event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof ItemBlock || EditMode.allowRightClickException(item))
                                        {
                                            //Save the blockstates of the blocks that are being replace for the undo feature
                                            Action currAction = new Action();

                                            Logging.logClick("rightclick on ", EditMode.ADJUST_MODE.toolModeName, EditMode.RIGHT_CLICK_NUMBER, EditMode.DELETE_MODE, EditMode.ADJUST_MODE.identificationIndex, 1);

                                            if (EditMode.rightClick(currAction, event.getEntityPlayer()))
                                            {
                                                //means the right click did something and that action is being saved
                                                UndoFunctionality.addActionToHistory(currAction);
                                            }
                                        }
                                        else
                                            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Select Block to execute this action", true, true);
                                    }

                                }
                                else if (AdvCreation.mode.equals(EnumMainMode.PLACE))
                                {
                                    Minecraft mc = Minecraft.getMinecraft();
                                    if ((PlacementHelper.isInteractableBlock(mc.world.getBlockState(event.getPos()).getBlock(), event.getPos()))
                                            && GuiTemplaceInventoryScreenFunctionality.selected_index == -1)
                                        event.setCanceled(false);
                                    else
                                    {
                                        event.setCanceled(true);

                                        Logging.logClickPlaceMode("rightclick on ", 1);
                                        if (TemplateManager.TEMPLATES_LIST.size() > GuiTemplaceInventoryScreenFunctionality.selected_index && 0 <= GuiTemplaceInventoryScreenFunctionality.selected_index)
                                        {
                                            BlockPos position = RenderTemplate.getNewPosition(event.getPos(),
                                                    event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock(),
                                                    event.getFace(),event.getHitVec() );

                                            PlaceTemplateMode.placeTemplate(position, event.getEntityPlayer());

                                            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.mode, "Placed Template", true, false);

                                        }
                                        RenderPreview.previewList.clear();
                                    }
                                }
                                else if (AdvCreation.mode == EnumMainMode.CREATE)
                                {
                                    Minecraft mc = Minecraft.getMinecraft();
                                    if ((PlacementHelper.isInteractableBlock(mc.world.getBlockState(event.getPos()).getBlock(), event.getPos()))
                                            && BuildTemplateMode.MODE == TemplateBuildingMode.SELECT_START_POS)
                                        event.setCanceled(false);
                                    else
                                    {
                                        Logging.logClickCreateMode("rightclick on ", 1);
                                        event.setCanceled(true);
                                        BuildTemplateMode.cancelTemplate();
                                    }
                                }
                                else
                                    event.setCanceled(false);

                            }
                            else
                                event.setCanceled(true);
                        }
                        else
                        {
                            if(AdvCreation.getMode() == EnumMainMode.PLACE)
                                GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Not handled by Advanced Creation",true,false,0xFFED5A1B,false);
                            else
                                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Not handled by Advanced Creation",true,false,0xFFED5A1B,false);
                        }
                    }
                    else
                        event.setCanceled(true);
                }
                else
                    event.setCanceled(true);
            }
            else
                event.setCanceled(false);
        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptCLientLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event)
    {
        //client event
        if (IsometricCamera.isPlayerInIsometricPerspective())
        {
            int X_mouse = Mouse.getX();
            int Y_mouse = Mouse.getY();
            GuiOverlayManager.isGuiOverlayLeftClicked(event.getEntityPlayer().world.isRemote,X_mouse,Y_mouse);

            AdvCreation.leftClickDownClient = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {

        if (event.getEntityPlayer().world.isRemote)
        {

            if (IsometricCamera.isPlayerInIsometricPerspective())
            {
                //check if the clicked screen position is on a button in the GUI overlay
                int X_mouse = Mouse.getX();
                int Y_mouse = Mouse.getY();
                boolean guiOverlayClicked = GuiOverlayManager.isGuiOverlayLeftClicked(event.getEntityPlayer().world.isRemote,X_mouse,Y_mouse);
                event.setCanceled(ConfigurationHandler.general.TOOLS_ENABLED || guiOverlayClicked);
            }

        }
        AdvCreation.leftClickDownClient = true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean handleClientLeftClick(EntityPlayer player, BlockPos clickPos)
    {

        boolean cancelEvent = false;
        if (!GuiOverlayManager.GUI_OVERLAY_CLICKED)
        {
            if (!NetworkManager.CLIENT_IS_SENDING_BLOCKS && !NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
            {
                // use left click on a block to in the template creation and cancel the normal event
                // (don't cancel in normal mode)
                //if the player is in survival leftclick can be held and you don't want that for the create template functionality
                if (AdvCreation.mode == EnumMainMode.CREATE && (Minecraft.getMinecraft().player.isCreative() || !AdvCreation.leftClickDownClient))
                {
                    Logging.logClickCreateMode("leftclick on ", 2);
                    BuildTemplateMode.leftClick(clickPos);
                    cancelEvent = true;

                }
                else if (AdvCreation.mode == EnumMainMode.PLACE)
                {
                    //check if a template is selected for logging and display on
                    Logging.logClickPlaceMode("leftclick on ",2);

                    cancelEvent = true;
                    PlaceTemplateMode.toggleHoldPreview();

                    System.out.println("holdPreview " + PlaceTemplateMode.HOLD_PREVIEW);
                }
                else if (AdvCreation.mode == EnumMainMode.BUILD)
                {
                    cancelEvent = BuildMode.cancelLeftClick();

                    //if the player is in survival leftclick can be held and you don't want that for the build functionality
                    if (Minecraft.getMinecraft().player.isCreative())
                    {
                        //Save the blockstates of the blocks that are being replace for the undo feature
                        Action currAction = new Action();

                        Logging.logClick("leftclick on ", BuildMode.TOOLMODE.toolModeName, BuildMode.RIGHT_CLICK_NUMBER, BuildMode.DELETE_MODE, BuildMode.TOOLMODE.identificationIndex, 2);
//                        if (player.getHeldItemMainhand().getItem() instanceof ItemBlock)
//                        {
                            if (BuildMode.leftClick(currAction, player))
                            {
                                //means the right click did something and that action is being saved
                                UndoFunctionality.addActionToHistory(currAction);
                            }
//                        }
//                        else
//                            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Select Block to execute this action", true, true);
                    }
                }
                else if (AdvCreation.getMode().equals(EnumMainMode.EDIT))
                {
                    cancelEvent = EditMode.cancelLeftClick();

                    //Save the blockstates of the blocks that are being replace for the undo feature
                    Action currAction = new Action();

                    Logging.logClick("leftclick on ", EditMode.ADJUST_MODE.toolModeName, EditMode.RIGHT_CLICK_NUMBER, EditMode.DELETE_MODE, EditMode.ADJUST_MODE.identificationIndex, 2);
//                    if (player.getHeldItemMainhand().getItem() instanceof ItemBlock)
//                    {
                        if (EditMode.leftClick(currAction, player))
                        {
                            //means the right click did something and that action is being saved
                            UndoFunctionality.addActionToHistory(currAction);
                        }
//                    }
//                    else
//                    {
//                        GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Select Block to execute this action", true, true);
//                    }

                }
                else
                    cancelEvent = true;

            }
            else
                cancelEvent = true;

        }
        else
            cancelEvent = true;


        return cancelEvent;
    }

    @SubscribeEvent
    public static void interceptLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!event.getEntityPlayer().world.isRemote)
        {
            if (IsometricCamera.isPlayerInIsometricPerspective())
            {
                if(!ConfigurationHandler.general.TOOLS_ENABLED && !GuiOverlayManager.GUI_OVERLAY_CLICKED)
                {
                    event.setCanceled(false);
                    return;
                }

                //send the leftclick to the client to be handled, he will send the changes back to the server
                NetworkHandler.sendToClient(new MessageTriggerClickEvent(MessageTriggerClickEvent.mouseButton.leftMouse, event.getPos(), event.getFace()), (EntityPlayerMP) event.getEntityPlayer());
                event.setCanceled(true);
            }
        }
    }

    /***
     * intercept update living to change the way the avatar moves over the terrain
     * @param event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) event.getEntityLiving();
//            if (!isPlayerInFirstPerson(player.getName()))
            if (IsometricCamera.isPlayerInIsometricPerspective())
            {
                IsometricMovement.updateMovement(player);
            }
            else
            {
                //when in first person view
                IsometricMovement.FOLLOW_TARGET_X_MODE = false;
                IsometricMovement.FOLLOW_TARGET_Z_MODE = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptGuiOpen(GuiOpenEvent event)
    {
        if (IsometricCamera.isPlayerInIsometricPerspective())
        {

            //cancell any zoom happening when you open any menu
            if(event.getGui() != null)
                IsometricCamera.cancelZoom();

            //Don't open the in game menu when esc is pressed to cancel the current BuildMode or CreateMode operation
            if (event.getGui() instanceof GuiIngameMenu)
            {
//                System.out.println("preview GlStateManager settings:");
//                System.out.println("alphaOn: " + GuiAdjustModeSelectionScreen.alphaOn);
//                System.out.println("blendOn: " + GuiAdjustModeSelectionScreen.blendOn);
//                System.out.println("colorOn: " + GuiAdjustModeSelectionScreen.colorOn);
//                System.out.println("colorMatOn: " + GuiAdjustModeSelectionScreen.colorMatOn);
//                System.out.println("depthOn: " + GuiAdjustModeSelectionScreen.depthOn);
//                System.out.println("otherSetting1On: " + GuiAdjustModeSelectionScreen.otherSetting1On);
//                System.out.println("otherSetting2On: " + GuiAdjustModeSelectionScreen.otherSetting2On);
//                System.out.println("otherSetting3On: " + GuiAdjustModeSelectionScreen.otherSetting3On);
//                System.out.println("otherSetting4On: " + GuiAdjustModeSelectionScreen.otherSetting4On);



                boolean building = (AdvCreation.mode.equals(EnumMainMode.BUILD) && (BuildMode.RIGHT_CLICK_NUMBER > 0));
                boolean creating_template = (AdvCreation.mode.equals(EnumMainMode.CREATE) && BuildTemplateMode.MODE != TemplateBuildingMode.SELECT_START_POS);
                if (building || creating_template|| NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
                    event.setCanceled(true);
                else
                    event.setCanceled(false);
            }
            if (event.getGui() instanceof GuiInventory)
            {
                if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                {
                    System.out.println("Open Custom inventory");
                    event.setGui(new GuiTemplateInventoryScreenSimple());
                }

            }
            else if(event.getGui() instanceof GuiToolModeSelectionScreen)
            {
                GuiToolModeSelectionScreen.mouseXBeforeOpening = Mouse.getX();
                GuiToolModeSelectionScreen.mouseYBeforeOpening = Mouse.getY();
            }
            else if(event.getGui() instanceof GuiAdjustModeSelectionScreen)
            {
                GuiAdjustModeSelectionScreen.mouseXBeforeOpening = Mouse.getX();
                GuiAdjustModeSelectionScreen.mouseYBeforeOpening = Mouse.getY();
            }
            else if(event.getGui() == null)
            {
                //TODO: check if this functions currectly
                previouslyInGame = true;
                //if you are returning to the game from one of the selection wheels set the mouse position to the position before opening the selectionWheel
                if(Minecraft.getMinecraft().currentScreen instanceof GuiToolModeSelectionScreen)
                {
                    System.out.println("closing current GuiToolModeSelectionScreen event" + event);
                    ModMouseHelper.setMouseToPosNextTick(GuiToolModeSelectionScreen.mouseXBeforeOpening,GuiToolModeSelectionScreen.mouseYBeforeOpening);
                }
                else if(Minecraft.getMinecraft().currentScreen instanceof GuiAdjustModeSelectionScreen)
                {
                    ModMouseHelper.setMouseToPosNextTick(GuiAdjustModeSelectionScreen.mouseXBeforeOpening,GuiAdjustModeSelectionScreen.mouseYBeforeOpening);
                }
            }

            MouseInputHandler.wasInScreen = true;
        }


        if (event.getGui() instanceof GuiWorldSelection || event.getGui() instanceof GuiMultiplayer)
        {
            GuiOverlayManager.PLAYER_GUI_INITIALIZED = false;
            NetworkManager.PLAYER_INITIALISED = false;
            IsometricCamera.ENTERED_CREATIVE = true;
            if(previouslyInGame)
            {
                GuiOverlayManager.setGuiOverlayVisible(false);
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
                //if the player was previously ingame then refresh the blacklist in
                BlockBlackListManager.refreshBlackList(new File(Minecraft.getMinecraft().gameDir, BlockBlackListManager.BLACKLIST_FILENAME));
                previouslyInGame = false;
            }
        }

    }

    // specifically for editing the ingame menu
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptPostGuiInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if(event.getGui() instanceof GuiIngameMenu)
        {
            GuiCustomIngameMenu.initGui(event.getButtonList(),event.getGui().width,event.getGui().height);
        }
    }

    // specifically for editing the ingame menu behavior
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptPostGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if(event.getGui() instanceof GuiIngameMenu)
        {
            GuiCustomIngameMenu.actionPerformed(event.getButton(),event.getGui());
        }
    }

    // specifically for editing the ingame menu behavior
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptPostGuiDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if(event.getGui() instanceof GuiIngameMenu)
        {
            GuiCustomIngameMenu.drawTooltips(event);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {

            Logging.tickLogging();
            RenderCutThrough.handleCutthroughRendering();
            NetworkManager.trySendPlaceMessageToServer();

            NetworkPlaceBlockListFormatter.checkPlacement();

            if(!(Keybindings.ROT_CAMERA.isDown()))
                Mousebindings.initialiseCameraRotation = false;




            KeyInputHandler.periodicCheckPressedKeys();
            KeyInputHandler.ticksSinceLastPress++;
        }

        if(event.phase == TickEvent.Phase.END)
        {
            if(Keybindings.ROT_CAMERA.isDown() )
                Mousebindings.initialiseCameraRotation = true;
            if (IsometricCamera.isPlayerInIsometricPerspective())
            {

                ModMouseHelper.checkIfMouseNeedsToBeSetThisTick();

            }

        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            renderTickTime = event.renderTickTime;
            EntityPlayer player = Minecraft.getMinecraft().player;

            if(player != null && player.getPositionEyes((float) event.renderTickTime) != null)
            {

                //TODO: remove this debug code
//                IsometricCamera.repositionMouseToZoomLocation(Minecraft.getMinecraft(), DebugInfo.CURR_HITPOS);
                //reposition the cursor to the previous pointed at block
                IsometricCamera.CAMERA_FOCUS_POINT = player.getPositionEyes((float) event.renderTickTime);
                if (ConfigurationHandler.cameraConfig.ZOOM_TOWARDS_CURSOR &&
                        IsometricCamera.HAS_ZOOMED)
                {
                    if (IsometricCamera.CURR_ZOOM_IN_VEC != null  && IsometricCamera.REPOSITION_ZOOM_ICON_ACTIVE)
                        IsometricCamera.repositionMouseToZoomLocation(Minecraft.getMinecraft(), IsometricCamera.CURR_ZOOM_IN_VEC);

//                    if (player.motionY == 0.0 && player.motionX == 0.0 && player.motionZ == 0.0 &&
//                            ModEntityRenderer.newCustomCameraDistance == ModEntityRenderer.customCameraDistance)
//                        IsometricCamera.HAS_ZOOMED = false;
                }
            }

        }

    }

    // A method that will register the current FOV after modifications are made and will make that available for
    // the raytracing for mouse to ground in isometric perspective
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptFOVChangeEvent(EntityViewRenderEvent.FOVModifier event)
    {
        IsometricCamera.updateModifiedFov(event.getFOV());
    }

    public static boolean started = false;
    public static long beginTime = 0;
    public static long endTime = 0;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptPlayerUpdate(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (event.player.world.isRemote)
            {

                if(!NetworkManager.PLAYER_INITIALISED)
                {
                    NetworkManager.PLAYER_INITIALISED = true;
                    //make sure the player is not clipping in the ground when entering a world in first)-person where
                    // he previously was in isometric
                    if(!IsometricCamera.isPlayerInIsometricPerspective())
                    {
                        IsometricCamera.setThirdPersonViewSetting(Minecraft.getMinecraft().gameSettings.thirdPersonView,false);
                        EntityPlayer player = event.player;
                        BlockPos ground =  IsometricMovement.findClosestGround(player.getPosition());
                        if(player.getPosition().up().getY() < ground.getY())
                        {
                            System.out.println("teleport to ground is true becuase " + player.getPosition().up().getY() +" < " +  ground.getY());
                            IsometricMovement.teleportPlayerTo(ground);
                            player.capabilities.isFlying = false;
                        }


                    }
                }

                //client player update
                SPPlayerProperties.updatePlayerProperties(event);
                //### handle if an action is undone by pressing a key
                if (UndoFunctionality.UndoActivated)
                        UndoFunctionality.undoAction(event.player.world);
                else if (UndoFunctionality.RedoActivated)
                        UndoFunctionality.redoUndoneAction(event.player.world);

                if (!Mousebindings.isRightButtonDown())
                        AdvCreation.rightClickDownClient = false;
                if (!Mousebindings.isLeftButtonDown())
                        AdvCreation.leftClickDownClient = false;


                if(IsometricCamera.ENTERED_CREATIVE && Minecraft.getMinecraft().playerController.isInCreativeMode())
                {
                    if(ConfigurationHandler.general.ENTER_CREATIVE_IN_ISOMETRIC)
                    {
                        IsometricCamera.setThirdPersonViewSetting(3);
                    }

                    if(!ConfigurationHandler.general.HIDE_GUI_OVERLAY_VISIBLE_OFF_MESSAGE &&  !ConfigurationHandler.general.GUI_OVERLAY_VISIBLE)
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + "Mod option 'GUI overlay visible' is set to false. Change it to true to use the Advanced Creation tools"));

                    IsometricCamera.ENTERED_CREATIVE = false;
                }
                if(IsometricCamera.ENTERED_SURVIVAL && Minecraft.getMinecraft().playerController.isNotCreative())
                {
                    if(IsometricCamera.PREV_CUSTOM_THIRD_PERSON_VIEW_SETTING > 2)
                        IsometricCamera.setThirdPersonViewSetting(0);
                    IsometricCamera.ENTERED_SURVIVAL = false;
                }
                if(IsometricCamera.CHANGED_PERSPECTIVE)
                {
                    IsometricCamera.setThirdPersonViewSetting(IsometricCamera.newThirdPersonViewValue);
                    IsometricCamera.CHANGED_PERSPECTIVE = false;
                }


                if(IsometricCamera.isPlayerInIsometricPerspective())
                {
                    BlockPos pos = new BlockPos( event.player.getPositionEyes(0f));
                    if(Minecraft.getMinecraft().world.getBlockState(pos).getBlock() instanceof BlockLiquid && net.minecraftforge.common.ForgeHooks.isInsideOfMaterial(Material.WATER, event.player,pos ))
                        event.player.startRiding(FakeEntityBoat.INSTANCE,true);
                    else
                        event.player.dismountRidingEntity();
                }


//                long time = System.currentTimeMillis();
//                Vec3d vector = event.player.getPositionVector();
//                if(vector.x >= 0 && vector.x < 30)
//                {
//
//                    if(!started && vector.x>= 0)
//                    {
//                        started = true;
//                        beginTime = time;
//                    }
//                    if(started && vector.x >= 5)
//                        endTime = time;
//                    if(vector.x > 5)
//                    {
//                        String mess = "In the zone posX " +vector.x  ;
//                        if(beginTime != 0)
//                        {
//                            if(endTime != 0)
//                                mess = "ended begin " + beginTime + " end " + endTime +" speed " + ((endTime-beginTime)/5.0) + "miliseconds per block : posX "  +vector.x  ;
//                            else
//                                mess = "started begin " + beginTime + " posX "  +vector.x ;
//                        }
//                        System.out.println(mess);
//                    }
//
//                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptRenderHandEvent(RenderHandEvent event)
    {
        if (IsometricCamera.isPlayerInIsometricPerspective())
            //this will prevent the hands from being rendered
            event.setCanceled(true);

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptClientChatEvent(ClientChatEvent event)
    {
        String message = event.getMessage().toLowerCase(Locale.ROOT);
        System.out.println("client sent message " + message);
        if(message.contains("gamemode")&& message.contains("c") && !message.contains("sp"))
            IsometricCamera.ENTERED_CREATIVE = true;
        if(message.contains("gamemode")&& message.contains("s") && !message.contains("sp"))
            IsometricCamera.ENTERED_SURVIVAL = true;
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event)
    {
        //when rendering the player cancel the rendering => also cancels the rendering of the item in the hand
        if (event.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            boolean isometricPerspective;
            if(player == Minecraft.getMinecraft().player)
                isometricPerspective = IsometricCamera.isPlayerInIsometricPerspective();
            else
                isometricPerspective = !NetworkManager.isPlayerInFirstPerson(player.getName());

            if (isometricPerspective)
            {
                GuiOverlayManager.tryInitialisingPlayerGuiOverlay();
                if (!NetworkManager.RENDER_PLAYERS.contains(player))
                    NetworkManager.RENDER_PLAYERS.add(player);
                event.setCanceled(true);
            }
            else
            {
                if (NetworkManager.RENDER_PLAYERS.contains(player))
                    NetworkManager.RENDER_PLAYERS.remove(player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptDrawBlockHighlight(DrawBlockHighlightEvent event)
    {
        blockHighlightEventTriggeredThisTick = true;
        if (IsometricCamera.isPlayerInIsometricPerspective() && Minecraft.getMinecraft().currentScreen == null)
        {
            RenderPreview.previewList.clear();
            int fpsCounter = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class,null,"field_71470_ab"); //debugFPS
//            String fps[] = Minecraft.getMinecraft(). .fpsString.split("fps");
//            FpsOptimiser.updateFpsMeasure(Integer.parseInt(fps[0].replace(" ","")));

            FpsOptimiser.updateFpsMeasure(fpsCounter);


            event.setCanceled(true);
            RayTraceResult objectMouseOver = event.getTarget();
            EntityPlayer entityplayer = event.getPlayer();

            if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                DebugInfo.CURR_HITPOS = objectMouseOver.hitVec;
            }


            //execute zoom in to where the mouse is pointing
            /***
             * Still very inaccurate, messy and approximate calculation of move vectors
             * mouse is still not moved to current zoomed to block when zooming
             *
             * But it will do for now, might even change it back
             */
            if (IsometricCamera.CAMERA_LOOK_VECTOR == null)
                IsometricCamera.CAMERA_LOOK_VECTOR = entityplayer.getLookVec();

            if (ModEntityRenderer.cameraDistanceChange)
            {
                IsometricCamera.executeCameraZoom(objectMouseOver, entityplayer, event.getPartialTicks(), ConfigurationHandler.cameraConfig.ZOOM_TOWARDS_CURSOR);
            }

            Float partialTicks = event.getPartialTicks();
            int hashcode  = 0;

            if(NetworkPlaceBlockListFormatter.isLargePlacementOperationInProgress())
            {
                Color messageColor = new Color(0xFFED5A1B);
                if(AdvCreation.mode.equals(EnumMainMode.PLACE))
                {
                    GuiOverlayManager.TEMPLATE_SELECTION.setRelativeBarLength(NetworkPlaceBlockListFormatter.confirmedPlaceMessageCompletion,NetworkPlaceBlockListFormatter.currMessageNeededToCompleteAction);
                    GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Waiting for placement (" + ((int) ((NetworkPlaceBlockListFormatter.confirmedPlaceMessageCompletion / ((double) NetworkPlaceBlockListFormatter.currMessageNeededToCompleteAction))*100)) + "%)",false,false,messageColor.getRGB(),true);

                }
                else
                {
                    GuiOverlayManager.INVENTORY_SELECTION.setRelativeBarLength(NetworkPlaceBlockListFormatter.confirmedPlaceMessageCompletion,NetworkPlaceBlockListFormatter.currMessageNeededToCompleteAction);
                    GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Waiting for placement (" + ((int) ((NetworkPlaceBlockListFormatter.confirmedPlaceMessageCompletion / ((double) NetworkPlaceBlockListFormatter.currMessageNeededToCompleteAction))*100)) + "%)"  ,false,false,messageColor.getRGB(),false);
                }

            }
            else if(objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
            {

//                String fps[] = Minecraft.getMinecraft().fpsString.split("fps");
//                FpsOptimiser.updateFpsMeasure(Integer.parseInt(fps[0].replace(" ","")));

                ModEntity.setCurrPointedBlockVec(objectMouseOver.hitVec);
                Entity entity = event.getTarget().entityHit;
                if(entity != null)
                {
                    RenderTemplate.drawSelectionBox(entityplayer,objectMouseOver,entity.getRenderBoundingBox(),0,partialTicks,1f,1f,1f,1f);
                }
            }
            else if (AdvCreation.mode.equals(EnumMainMode.BUILD))
            {
                //change the toolmode if it has been changed by the player
                if (BuildMode.TOOLMODE_HAS_CHANGED)
                    BuildMode.applyToolModeChange();

                if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK ) {
                    Item handStack = entityplayer.getHeldItemMainhand().getItem();
                    ModEntity.setCurrPointedBlockVec(objectMouseOver.hitVec);

                    Vec3d hitVec = objectMouseOver.hitVec;
                    BlockPos position = objectMouseOver.getBlockPos();

                    //if both the item in hand and the selected item are slabs of the same type don't get the new position
                    IBlockState hitBlockstate = entityplayer.world.getBlockState(objectMouseOver.getBlockPos());

                    boolean bothHitAndHandAreSlab = false;
                    if ((HelpFunctions.isTheSameSlab(objectMouseOver.sideHit, handStack, handStack.getMetadata(entityplayer.getHeldItemMainhand().getMetadata()), hitBlockstate) &&
                            ((hitBlockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM && objectMouseOver.sideHit == EnumFacing.UP) ||
                                    (hitBlockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP && objectMouseOver.sideHit == EnumFacing.DOWN))))
                        bothHitAndHandAreSlab = true;

                    if (!bothHitAndHandAreSlab) {
                        //get the position of the new block to be placed based on the block currently moused over
                        hitVec = PlacementHelper.getAdjustedHitVec(objectMouseOver, 0.55);
                        position = RenderTemplate.getNewPosition(objectMouseOver.getBlockPos(),
                                entityplayer.world.getBlockState(objectMouseOver.getBlockPos()).getBlock(),
                                objectMouseOver.sideHit, objectMouseOver.hitVec);
                    }
                    if(ConfigurationHandler.general.TOOLS_ENABLED)
                    {

                        if (!KeyInputHandler.alterToolMode) {
                            BuildMode.updateAlterModeInactive(position, hitVec);
                        } else {
                            position = BuildMode.getAlterModePosition(position, hitVec);
                            hitVec = BuildMode.getPrevHitvec();
                        }

                        HelpFunctions.formatNewBlockAndAddToBuildMode(objectMouseOver, entityplayer, handStack, hitVec, position, true, bothHitAndHandAreSlab);
                        if (!IsometricCamera.HAS_ZOOMED) {
                            if (entityplayer.world.getBlockState(objectMouseOver.getBlockPos()).getBlock() instanceof BlockSign && BuildMode.RIGHT_CLICK_NUMBER == 0)
                                BuildMode.SHOW_PREVIEW_BLOCKS = false;
                            else
                                BuildMode.SHOW_PREVIEW_BLOCKS = true;

                            //draw preview and send to other players
                            hashcode = BuildMode.drawPreview(entityplayer, hitVec, objectMouseOver.sideHit, partialTicks);
                            //if hashcode == 0 sending preview to server is done later
                            if (hashcode != 0) {
                                if (hashcode != NetworkManager.PREVIOUS_HASHCODE) {
                                    //send the current previewblocks to the server
                                    if (BuildMode.RIGHT_CLICK_NUMBER >= 2 && (BuildMode.TOOLMODE instanceof CopyPasteToolMode ||
                                            BuildMode.TOOLMODE instanceof MoveToolMode))
                                        MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, RenderPreview.previewList, new ArrayList<>(), entityplayer.getName(), AdvCreation.previewStartPos, AdvCreation.previewEndPos);
                                    else
                                        NetworkManager.sendPreviewBlocksToServer(entityplayer, BuildMode.CURR_PREVIEW_BLOCKS, false);
                                }
                                NetworkManager.PREVIOUS_HASHCODE = hashcode;
                            }
                        }
                    }
                    else
                    {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.enableColorLogic();
                        GlStateManager.disableLighting();

                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position, partialTicks, 0.7F,0.7F,0.7F);
                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position.subtract(objectMouseOver.sideHit.getDirectionVec()), partialTicks, 0.7F,0.0F,0.0F);

                        GlStateManager.enableLighting();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableColorLogic();
                    }
                }
            }
            else if (AdvCreation.mode.equals(EnumMainMode.EDIT))
            {

                if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    Item handStack = entityplayer.getHeldItemMainhand().getItem();

                    //get the position of the new block to be placed based on the block currently moused over
                    Vec3d hitVec = PlacementHelper.getAdjustedHitVec(objectMouseOver, 0.05);
                    BlockPos position = RenderTemplate.getNewPosition(objectMouseOver.getBlockPos(),
                            entityplayer.world.getBlockState(objectMouseOver.getBlockPos()).getBlock(),
                            objectMouseOver.sideHit,objectMouseOver.hitVec);

                    if(ConfigurationHandler.general.TOOLS_ENABLED)
                    {

                        TemplateBlock block1;
                        boolean blackListed = BlockBlackListManager.IsOnBlackList(handStack);
                        if (blackListed)
                            GuiOverlayManager.announceBlacklistMatch();

                        if (handStack instanceof ItemBlock && !blackListed) {
                            block1 = HelpFunctions.getTemplateBlockFromItemBlock(objectMouseOver, entityplayer, (ItemBlock) handStack, hitVec, position);
                        } else
                            block1 = new TemplateBlock(objectMouseOver.sideHit, position, null);
                        IBlockState currState = entityplayer.world.getBlockState(objectMouseOver.getBlockPos());
                        AxisAlignedBB boundingBox = currState.getBlock().getBoundingBox(currState, entityplayer.getEntityWorld(), objectMouseOver.getBlockPos());
                        EnumFacing hitFace = objectMouseOver.sideHit;
                        double currHitblockWidth = 1.0;
                        if (hitFace.getAxis() == EnumFacing.Axis.X)
                            currHitblockWidth = MathHelper.absMax(boundingBox.maxX, boundingBox.minX);
                        else if (hitFace.getAxis() == EnumFacing.Axis.Y)
                            currHitblockWidth = MathHelper.absMax(boundingBox.maxY, boundingBox.minY);
                        else if (hitFace.getAxis() == EnumFacing.Axis.Z)
                            currHitblockWidth = MathHelper.absMax(boundingBox.maxZ, boundingBox.minZ);

                        if (currHitblockWidth > 1000.0 || currHitblockWidth < 0)
                            currHitblockWidth = 0.5;

                        EditMode.addNewBlock(block1, hitVec, objectMouseOver.getBlockPos(), objectMouseOver.sideHit, entityplayer, currHitblockWidth);
                        if (!IsometricCamera.HAS_ZOOMED) {
                            //draw preview and send to other players
                            hashcode = EditMode.drawPreview(entityplayer, hitVec, objectMouseOver.sideHit, partialTicks);
                            //if hashcode == 0 sending preview to server is done later
                            if (hashcode != 0) {
                                if (hashcode != NetworkManager.PREVIOUS_HASHCODE) {
                                    if (EditMode.ADJUST_MODE instanceof PaintAdjustMode ||
                                            EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                                        //send the current previewblocks to the server
                                        NetworkManager.sendPreviewBlocksToServer(entityplayer, new ArrayList<>(), EditMode.CURR_PREVIEW_BLOCKS, EditMode.DRAW_PREVIEW_OUTLINE_ONLY);
                                    else
                                        //send the current previewblocks to the server
                                        NetworkManager.sendPreviewBlocksToServer(entityplayer, EditMode.CURR_PREVIEW_BLOCKS, EditMode.CURR_PREVIEW_OUTLINE_BLOCKS, false);

//                            //send the current previewblocks to the server
//                            NetworkManager.sendPreviewBlocksToServer(playerEntity, EditMode.CURR_PREVIEW_BLOCKS, EditMode.DRAW_PREVIEW_OUTLINE_ONLY);
                                }
                                NetworkManager.PREVIOUS_HASHCODE = hashcode;
                            }
                        }
                    }
                    else
                    {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.enableColorLogic();
                        GlStateManager.disableLighting();

                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position, partialTicks, 0.7F,0.7F,0.7F);
                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position.subtract(objectMouseOver.sideHit.getDirectionVec()), partialTicks, 0.7F,0.0F,0.0F);

                        GlStateManager.enableLighting();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableColorLogic();
                    }
                }
            }
            else if (AdvCreation.mode.equals(EnumMainMode.PLACE))
            {
                if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {

                    BlockPos position = RenderTemplate.getNewPosition(objectMouseOver.getBlockPos(),
                            entityplayer.world.getBlockState(objectMouseOver.getBlockPos()).getBlock(),
                            objectMouseOver.sideHit,objectMouseOver.hitVec );

                    if(ConfigurationHandler.general.TOOLS_ENABLED) {

                        if (!KeyInputHandler.alterToolMode) {
                            PlaceTemplateMode.updateAlterModeInactive(position, objectMouseOver.hitVec);
                        } else {
                            position = PlaceTemplateMode.getAlterModePosition(position, objectMouseOver.hitVec);
                        }

                        if (!IsometricCamera.HAS_ZOOMED) {
                            //draw preview and send to other players
                            hashcode = PlaceTemplateMode.drawPreview(position, entityplayer, objectMouseOver.hitVec, objectMouseOver.sideHit, partialTicks);

                            //if hashcode == 0 sending preview to server is done later
                            if (hashcode != 0) {
                                if (hashcode != NetworkManager.PREVIOUS_HASHCODE) {
                                    if (hashcode == 0)
                                        MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, RenderPreview.previewList, new ArrayList<>(), entityplayer.getName(), RenderPreview.selectionStartPos, RenderPreview.selectionEndPos, false);
                                    else
                                        MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, RenderPreview.previewList, new ArrayList<>(), entityplayer.getName(), RenderPreview.selectionStartPos, RenderPreview.selectionEndPos);

                                }
                                NetworkManager.PREVIOUS_HASHCODE = hashcode;
                            }
                        }
                    }
                    else
                    {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.enableColorLogic();
                        GlStateManager.disableLighting();

                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position, partialTicks, 0.7F,0.7F,0.7F);
                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position.subtract(objectMouseOver.sideHit.getDirectionVec()), partialTicks, 0.7F,0.0F,0.0F);

                        GlStateManager.enableLighting();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableColorLogic();
                    }
                }
            }
            else if (AdvCreation.mode.equals(EnumMainMode.CREATE))
            {
                if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK   )
                {
                    if(ConfigurationHandler.general.TOOLS_ENABLED)
                    {
                        BlockPos position = objectMouseOver.getBlockPos();
                        if (!KeyInputHandler.alterToolMode || BuildTemplateMode.MODE.equals(TemplateBuildingMode.MAKE_ADJUSTMENTS)) {
                            BuildTemplateMode.updateAlterModeInactive(position, objectMouseOver.hitVec);
                        } else {
                            position = BuildTemplateMode.getAlterModePosition(position, objectMouseOver.hitVec);
                        }

                        BuildTemplateMode.updateSelection(Minecraft.getMinecraft(), objectMouseOver.hitVec);

                        //draw preview and send to other players
//                    GuiOverlayManager.setPlacePointedCoordinate(position);
                        GuiOverlayManager.setPlacePointedCoordinate(position.add(BuildTemplateMode.MOUSE_X_OFFSET, BuildTemplateMode.MOUSE_Y_OFFSET, BuildTemplateMode.MOUSE_Z_OFFSET));
                        GuiOverlayManager.setDeletePointedCoordinate(null);
                        hashcode = BuildTemplateMode.drawSelectionBox(Minecraft.getMinecraft(), entityplayer, position, objectMouseOver.hitVec, true, partialTicks);
                        //if hashcode == 0 sending preview to server is done later
                        if (hashcode != 0) {
                            if (hashcode != NetworkManager.PREVIOUS_HASHCODE) {
                                if (hashcode == 0)
                                    MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, new ArrayList<>(), new ArrayList<>(), entityplayer.getName(), AdvCreation.previewStartPos, AdvCreation.previewEndPos, false);
                                else
                                    MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, new ArrayList<>(), new ArrayList<>(), entityplayer.getName(), AdvCreation.previewStartPos, AdvCreation.previewEndPos);
                            }
                            NetworkManager.PREVIOUS_HASHCODE = hashcode;
                        }
                        BuildTemplateMode.drawHighlighting(Minecraft.getMinecraft(), position, partialTicks);
                    }
                    else
                    {
                        //get the position of the new block to be placed based on the block currently moused over
                        BlockPos position = RenderTemplate.getNewPosition(objectMouseOver.getBlockPos(),
                                entityplayer.world.getBlockState(objectMouseOver.getBlockPos()).getBlock(),
                                objectMouseOver.sideHit,objectMouseOver.hitVec);

                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.enableColorLogic();
                        GlStateManager.disableLighting();

                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position, partialTicks, 0.7F,0.7F,0.7F);
                        RenderSelectionHighlight.drawSelectionBlockOutline(entityplayer, position.subtract(objectMouseOver.sideHit.getDirectionVec()), partialTicks, 0.7F,0.0F,0.0F);

                        GlStateManager.enableLighting();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableColorLogic();
                    }
                }

            }

            if(hashcode == 0)
            {
                BlockPos cursorPointer = GuiOverlayManager.getStartPosCoord();
                if(cursorPointer == null)
                    cursorPointer = BlockPos.ORIGIN;
                hashcode = cursorPointer.hashCode();
//                System.out.println("hashcode != previous hashcode: " + hashcode + " != " + NetworkManager.PREVIOUS_HASHCODE + " = " + (hashcode != NetworkManager.PREVIOUS_HASHCODE));
                if (hashcode != NetworkManager.PREVIOUS_HASHCODE)
                {
                    MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, new ArrayList<>(), new ArrayList<>(), entityplayer.getName(), cursorPointer, cursorPointer.add(1,1,1));
                }
                NetworkManager.PREVIOUS_HASHCODE = hashcode;
            }

            if(ConfigurationHandler.absCoordConfig.SHOW_ABS_CAMERA_FOCUS_COORD)
            {
                BlockPos eyesPos = new BlockPos(entityplayer.getPositionEyes(partialTicks));
                GuiOverlayManager.setCameraFocusPointPos(eyesPos);
                GlStateManager.enableColorMaterial();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.enableColorLogic();
                GlStateManager.disableLighting();

                RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,eyesPos,partialTicks);

                GlStateManager.enableLighting();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.disableColorMaterial();
                GlStateManager.disableColorLogic();
            }
//                RenderCameraFocusPoint.renderPlayerAbsoluteCoordinate(entityplayer,partialTicks);


            if (ConfigurationHandler.general.SHOW_OTHER_PLAYERS_PREVIEW)
            {
                //render preview blocks of other people
                NetworkManager.processPreviewMessages(objectMouseOver, entityplayer, partialTicks);
            }



            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();
            GlStateManager.disableLighting();

            IsometricCamera.renderZoomLocationHighlighting(entityplayer,partialTicks);

            GlStateManager.enableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();


//            DebugInfo.renderDebugInformation(event.getPartialTicks());
        }
        //render The indicator to let the player know where the camera is focussing
        NetworkManager.processOtherPlayerRenderingMessages(event.getPlayer(), event.getPartialTicks());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void interceptLastWorldRenderEvent(RenderWorldLastEvent event)
    {
        //this is the last event of the render tick
        //this means it comes after the blockhighlightEvent
        if (IsometricCamera.isPlayerInIsometricPerspective())
        {
            //this event will handle the zooming only if, in this tick, the blockhighlightevent did not happen.
            // should the zoom be initiated between the blockhighlight event and this event then we will wait for next tick to see
            // if the block highlightevent will handle the zoom.
            // if the block highlightEvent didn't happen this tick I'm assuming that it won't happen next tick so I'll handle the zoom here.
            // this way should prevent large zoom jumps by the player as seen before.
            if(!blockHighlightEventTriggeredThisTick)
            {
                if (ModEntityRenderer.cameraDistanceChange)
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    IsometricCamera.executeCameraZoom(new RayTraceResult(player), player, event.getPartialTicks(), false);
                }
            }
        }
        blockHighlightEventTriggeredThisTick = false;
    }

}
