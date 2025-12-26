package com.deadtiger.advcreation.undo_actions;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.warningScreen.GuiWarningScreenFactory;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class UndoFunctionality
{
    public static boolean UndoActivated = false;
    public static boolean RedoActivated = false;
    
    private static ArrayDeque<Action> actionHistory  = new ArrayDeque<Action>();
    private static ArrayDeque<Action> actionUndoneHistory  = new ArrayDeque<Action>();
    
    private static int maxHistoryLength = 100;
    private static int maxUndoneHistoryLength = 100;

    public static boolean alreadyDidUndoRedoAction = false;

    public static void addActionToHistory(Action action)
    {
        actionHistory.addLast(action);
        if(actionHistory.size() > maxHistoryLength)
        {
            actionHistory.removeFirst();
        }
        //when the player does a new action after undoing the undone action cannot be redone
        actionUndoneHistory.clear();
    }
    
    public static Action popActionFromHistory()
    {
        if(actionHistory.isEmpty())
            return null;
        return actionHistory.removeLast();
    }
    
    public static void undoAction(World world)
    {
        if(!actionHistory.isEmpty())
        {
            if(!NetworkPlaceBlockListFormatter.isPlacementOperationInProgress() && !alreadyDidUndoRedoAction && !actionHistory.isEmpty())
            {
                Action action = popActionFromHistory();
                if(action == null)
                    giveNegativeFeedback("No Action To Undo");
                else
                {
                    Action currAction = new Action();
                    for (TemplateBlock block : action.getPreviousBlockStates())
                    {
                        BlockPos blockPos = new BlockPos(block.getX_offset(), block.getY_offset(), block.getZ_offset());

                        //save previous state in the opposite list
                        IBlockState currBlockState = world.getBlockState(blockPos);
                        TileEntity tileEntity = world.getTileEntity(blockPos);
                        currAction.add(EnumFacing.NORTH, blockPos, currBlockState, tileEntity);
                    }

                    NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, action.getPreviousBlockStates(), new ArrayList<TemplateBlock>(), null, null,currAction);
                    if(IsometricCamera.isPlayerInIsometricPerspective())
                    {
                        givePositiveFeedback("Undo Action", true);
                    }
                    else if(Minecraft.getMinecraft().playerController.isInCreativeMode())
                        GuiOverlayManager.UNDO_REDO_NOTIFICATION.setNotification("Undone Last Action");

                    UndoFunctionality.addActionToRedoHistory(currAction);
                    alreadyDidUndoRedoAction = true;
                }
            }
        }
        else
        {
            if(IsometricCamera.isPlayerInIsometricPerspective())
            {
                giveNegativeFeedback("No Action To Undo");
            }
             else if(Minecraft.getMinecraft().playerController.isInCreativeMode())
                GuiOverlayManager.UNDO_REDO_NOTIFICATION.setNotification("No Action To Undo");
        }

        UndoFunctionality.deactivateUndo();
    }

    private static void givePositiveFeedback(String s, boolean b)
    {
        if (AdvCreation.mode == EnumMainMode.PLACE)
            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), s, true, b);
        else
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), s, true, b);
    }

    private static void giveNegativeFeedback(String s)
    {
        if (AdvCreation.mode == EnumMainMode.PLACE)
            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), s, true, false, 0xFFED5A1B);
        else
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), s, true, false, 0xFFED5A1B);
    }

    public static void addActionToRedoHistory(Action action)
    {
        actionUndoneHistory.addLast(action);
        if(actionUndoneHistory.size() > maxUndoneHistoryLength)
        {
            actionUndoneHistory.removeFirst();
        }
    }
    
    public static Action popActionFromRedoHistory()
    {
        if(!actionUndoneHistory.isEmpty())
            return actionUndoneHistory.removeLast();
        return null;
    }
    
    public static void redoUndoneAction(World world)
    {
        if(!NetworkPlaceBlockListFormatter.isPlacementOperationInProgress() && !alreadyDidUndoRedoAction && !actionUndoneHistory.isEmpty())
        {
            Action action = popActionFromRedoHistory();
            if(action == null)
            {
                giveNegativeFeedback("No Action To Redo");

            }
            else
            {
                Action currAction = new Action();
                for (TemplateBlock block : action.getPreviousBlockStates()) {
                    BlockPos blockPos = new BlockPos(block.getX_offset(), block.getY_offset(), block.getZ_offset());

                    //save previous state in the opposite list
                    IBlockState currBlockState = world.getBlockState(blockPos);
                    TileEntity tileEntity = world.getTileEntity(blockPos);
                    currAction.add(EnumFacing.NORTH, blockPos, currBlockState,tileEntity);
                }

                NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN,action.getPreviousBlockStates(),new ArrayList<TemplateBlock>(),null,null,currAction);
                if(IsometricCamera.isPlayerInIsometricPerspective())
                {
                    givePositiveFeedback("Redo Action", false);
                }
                else if(Minecraft.getMinecraft().playerController.isInCreativeMode())
                    GuiOverlayManager.UNDO_REDO_NOTIFICATION.setNotification("Redone Last Undone Action");

                actionHistory.addLast(currAction);
                if(actionHistory.size() > maxHistoryLength)
                {
                    actionHistory.removeFirst();
                }
                alreadyDidUndoRedoAction = true;
            }
        }
        else
        {

            if(IsometricCamera.isPlayerInIsometricPerspective())
            {
                giveNegativeFeedback("No Action To Redo");
            }
            else if(Minecraft.getMinecraft().playerController.isInCreativeMode())
                GuiOverlayManager.UNDO_REDO_NOTIFICATION.setNotification("No Action To Redo");


        }
        UndoFunctionality.deactivateRedo();
    }

    public static void activateUndo()
    {
        activateUndo(false);
    }
    public static void activateUndo(boolean skipCheck)
    {
        if(!skipCheck && isLastActionGigantic())
            Minecraft.getMinecraft().displayGuiScreen(GuiWarningScreenFactory.factory.createUndoLargeActionWarningScreen());
        else
            UndoActivated = true;
    }

    public static void deactivateUndo()
    {
        UndoActivated = false;
    }

    public static void activateRedo()
    {
        activateRedo(false);
    }

    public static void activateRedo(boolean skipCheck)
    {
        if(!skipCheck && isLastUndoneActionGigantic())
            Minecraft.getMinecraft().displayGuiScreen(GuiWarningScreenFactory.factory.createRedoLargeActionWarningScreen());
        else
            RedoActivated = true;
    }

    public static void deactivateRedo()
    {
        RedoActivated = false;
    }

    protected static boolean isLastActionGigantic()
    {
        Action action = actionHistory.peekLast();
        if(action == null)
            return false;
        else
            return action.isGiganticAction();
    }

    protected static boolean isLastUndoneActionGigantic()
    {
        Action action = actionUndoneHistory.peekLast();
        if(action == null)
            return false;
        else
            return action.isGiganticAction();
    }
}
