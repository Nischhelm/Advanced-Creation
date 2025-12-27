package com.deadtiger.advcreation.client.gui.gui_screen.warningScreen;

import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;

import java.util.function.Function;

public class GuiWarningScreenFactory
{

    public static GuiWarningScreenFactory factory = new GuiWarningScreenFactory();

    public GuiGeneralWarningScreen createCancelPlacementWarningScreen()
    {
        String[][] message = {{"You are about to cancel a placement operation. This might cause the operation to stay partially completed. However, you can usually still undo the cancelled operation."},
                {""},
                {"Are you sure you want to cancel this placement operation?"}};
        Function confirmAction = b -> {
            NetworkPlaceBlockListFormatter.cancelPlacement();
            return null;
        };

        Function cancelAction = b -> {
            return null;
        };

        return new GuiGeneralWarningScreen(message,confirmAction,cancelAction);
    }

    public GuiGeneralWarningScreen createUndoLargeActionWarningScreen()
    {
        String[][] message = {{"You are about to undo a large operation. This might take a long time to undo."},
                {""},
                {"Are you sure you want to undo this large operation?"}};
        Function confirmAction = b -> {
            UndoFunctionality.activateUndo(true);
            return null;
        };

        Function cancelAction = b -> {
            return null;
        };

        return new GuiGeneralWarningScreen(message,confirmAction,cancelAction);
    }

    public GuiGeneralWarningScreen createRedoLargeActionWarningScreen()
    {
        String[][] message = {{"You are about to redo a large operation. This might take a long time to redo."},
                {""},
                {"Are you sure you want to redo this large operation?"}};
        Function confirmAction = b -> {
            UndoFunctionality.activateRedo(true);
            return null;
        };

        Function cancelAction = b -> {
            return null;
        };

        return new GuiGeneralWarningScreen(message,confirmAction,cancelAction);
    }

}
