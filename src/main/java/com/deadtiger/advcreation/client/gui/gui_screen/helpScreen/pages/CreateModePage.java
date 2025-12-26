package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class CreateModePage extends AbstractPage
{

    public CreateModePage(String title)
    {
        super(title);
        //        ###############################################################################################
        String[][] intro = {{"CREATE mode allows you to select an area of the world and save it as a template."}};

        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings2 = {Keybindings.CONFIRM_TEMPLATE_CREATION.getKeybind()};
        KeyBinding[] keybindings3 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings4 = {Keybindings.CANCEL_TEMPLATE_CREATION.getKeybind()};
        Paragraph.KeyInformation[] keys1 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK start of SELECT area"),
                new Paragraph.KeyInformation(keybindings1, "2nd CLICK end of SELECT area"),
                new Paragraph.KeyInformation(keybindings2, "Finish Template Creation"),
                new Paragraph.KeyInformation(keybindings3, "Cancel Selection"),
                new Paragraph.KeyInformation(keybindings4, "Cancel Selection")};

        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), "42_create_mode", "mp4", keys1, "Intro CREATE Mode", intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] prop = {{"You can adjust the original SELECTION area by grabbing a side of the SELECTION box and dragging it to a new position."}
        };
        KeyBinding[] keybindings5 = {Keybindings.ALTER_TOOL_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK GRAB Highlighted Side"),
                new Paragraph.KeyInformation(keybindings1, "2nd CLICK RELEASE Hightlighted Side"),
                new Paragraph.KeyInformation(keybindings5, "SELECT Back/Front Side")};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "43_adjust_selection", "mp4", keys4, "Adjust Selection Area", prop, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjust = {{"When confirming a template you come to a save screen where you give the name and properties of the template."},
                {TextFormatting.DARK_GREEN + "Only the Template Name is really required the rest is optional."},
                {"Here there are 4 properties that you can specify:"},
                {"Template Name", "The name given by the player to the template"},
                {"Function", "The original function of the template ex.: castle, house, blacksmith, etc."},
                {"Category", "Which part of a structure this template is, 'structure' is a standalone building but other ex. are: wall, tower, room, etc."},
                {"Style", "The architecture style of the building ex.: medieval, futuristic, gothic, etc."},
                {""},
                {"The Function, Category and Style textfields show suggestions but you can write what you want there."},
                {"None of these properties change the usage of the template, they are purely descriptive. They are handy for looking up similar templates in the template inventory or on the website"}
        };

        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "templatesave", "png", null, "Template Save Screen", adjust, mc.getResourceManager());

        //        ###############################################################################################
        String[][] iconmaker = {{"When you have made a new template and see it for the first time in your template inventory, the window will become smaller and will " +
                "draw all the sides of the template. This can be a bit jarring and you might think something is wrong but this is intentional. " +
                "This is a hacky way of creating the icons that are used in the template inventory and on the website. Just wait until it is done."}

        };

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), "44_iconmaking", "mp4", null, "Making Template Icons", iconmaker, mc.getResourceManager());

        //        ###############################################################################################
        String[][] upload = {{"You can share your templates with other players on my website 'www.advancedcreationmod.com'. You do this by following these steps:"},
                {"1. ", "Go to 'www.advancedcreationmod.com/upload'"},
                {"2. ", "Press the 'choose files' button and browse to the 'advcreation_templates_zips' folder in the '.minecraf' folder."},
                {"3. ", "Select the templates you want to upload (you can select multiple) and press the 'open' button and then press the 'upload' button."},
                {"4. ", "Next you can choose which of the icons from different perspectives makes your template look the best and you can check the properties. The selected icon will be the first icon seen by other players."},
                {"5. ", "Press the 'accept' button to complete the upload process."}
        };


        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "54_upload_template", "mp4", null, "Upload your Templates", upload, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);

    }
}
