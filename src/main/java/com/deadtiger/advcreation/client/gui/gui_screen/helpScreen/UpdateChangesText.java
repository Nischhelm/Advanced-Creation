package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class UpdateChangesText
{
    public static String update_title = "Update Changelog";
    public static String alpha1_1_intro = "Alpha1.1";
    public static String[][] alpha1_1_body = {
            {"      1.", "SERVER fixed Concurrency error in Logging each serverTick in server"},
            {"      2.", "Added a GUI HUD button to open inventory (both for templates and normal)"},
            {"      3.", "Improved the making of template icons on screen with different resolutions fro 1080p"},
            {"      4.", "In Template Inventory the Hotbar is lowered to better indicate its function"},

            {"      5.", "Fixed the preview of the FILLGAP Toolmode in BuildMode, it doesn't remain static anymore"},
            {"      6.", "When in isometric view the focus point of the camera is now indicated by a axis system with colors of the respective planes"},
            {"      7.", "Added a progressbar HUD element on indicating the tool you are using/ how many clicks are still needed to complete and indicate other actions and statuses "},
            {"      8.", "Increased Pull ToolMode block limit"},
            {"      9.", "CLIENT+SERVER Templates can now contain fire and water to be placed in the world"},
            {"      10.", "Improved Refresh terrain button functionality"},
            {"      11.", "Other players can see where the camera focus point is of other players and their name above it"},
            {"      12.", "Added Update Changes Log to HelpScreen at the end"}
    };

    public static String alpha1_2_intro = "Alpha1.2";
    public static String[][] alpha1_2_body = {
            {"      1.", "Templates are now automatically zipped in the advcreation_templates_zips for easy uploading and downloading from/to website"},
            {"      2.", "created a hash that can uniquely identify a template and can be checked by the website to prevent duplicates"},
            {"      3.", "Old templates are automatically converted to the new format."}
    };

    public static String alpha1_3_intro = "Alpha1.3";
    public static String[][] alpha1_3_body = {
            {"      1.", "You can change toolmode even when you have no block in your hand"},
            {"      2.", "When actions don't work because you have no block in hand the game lets you now"},
            {"      3.", "Update some of the keybind description to be more clear"},
            {"      4.", "Added hotkey indication in the tooltip for the mainmode buttons on the guioverlay"},
            {"      5.", "Mirror function added to PLACE and BUILD"},
            {"      6.", "Fixed bug where you couldn't place doors"},
            {"      7.", "Fixed bug where the overlay button for the template inventory opened the wrong inventory"},
            {"      8.", "New helpscreen with explanatory gifs and a topic overview sidebar"},
            {"      9.", "Can now place crops like melon stems, pumpkin stems, wheat and beetroot in the template"},
            {"      10.", "Fixed preview and placement of chests and beds, chest will allways place towards the camera centerpoint"},
            {"      11.", "Fixed crash at client creation of logging file"},
            {"      12.", "Fixed crash at movetool update currtemplate"},
            {"      13.", "Reduced minimum Y coord from 5 to 1 to allow construction on flatworlds"},
            {"      14.", "Full refactory of the code to improve cooperation"},
            {"      15.", "Fixed small bugs"},
            {"      16.", "Client mod and server mod are now merged into one jar for for both forge and core mod. These are still seperate though"},
            {"      17.", "Created seperate configuration files for server and client."},
            {"      18.", "Ported to MC 1.12.2"},
            {"      19.", "Changed legend_logs.txt file location to playerlogs folder"},
            {"      20.", "Fixed nullpointer error in CurveToolMode.java"}
    };

    public static String alpha1_4_intro = "Alpha1.4";
    public static String[][] alpha1_4_body = {
            {"      1.", "remade mp4 helpscreen videos to be smaller"},
            {"      2.", "Coremod and forge mod are now in one jar"},
    };


    public static String alpha1_5_intro = "Alpha1.5";
    public static String[][] alpha1_5_body = {
            {"      1.", "Made camera rotation smoother by changing camera angles from integer to double"},
            {"      2.", "Adjustable speed of zooming and camera rotation in mod config"},
            {"      3.", "Warning screen appears when you cannot open the mod config, saying you have to delete the old config file"},
            {"      4.", "Ability to disable the zooming towards cursor in mod config"},
            {"      5.", "Camera level slider and toGround button now teleport camera focus point to new position"},
            {"      6.", "Increased helpscreen mp4s rendering quality"},
            {"      7.", "Added new settings information for camera controls to GeneralControlsPage on HelpScreen"},
            {"      8.", "Thank you McHorse for you valuable feedback that lead to this update!"},
    };

    public static String alpha2_0_intro = "Alpha2.0";
    public static String[][] alpha2_0_body = {
            {"Highlights:"},
            {"      1.", "Isometric view and tools are now ONLY available in Creative mode."},
            {"      2.", "Support for TileEntities."},
            {"",TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      3.", "Ability to use Absolute Coordinates for placement/deletion. "},
            {"",         TextFormatting.DARK_GREEN + "thx to feedback from  Nepisʹ (normally in cyrillic)"},
            {"      4.", "New Ingame menu buttons for perspective switching and gamemodes. " },
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Invisible Davis Studios"},
            {"      5.", "Support for Alt-mode offsetting of selection in BUILD mode & others modes."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from McHorse"},
            {"      6.", "Mod compatibility with Llibrary."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Almos-Rhine & McHorse"},
            {"      7.", "Support for Non-block items in Isometric view."},
            {"      8.", "Re-Introduction of Vanilla third-person views."},
            {"      9.", "New Help screen entries for new features & ability to change Help screen text size."},
            {"      10.", "Various crash and bug fixes. "},
            {"      11.", "Improved highlighting and other quality of life fixes."},
            {"      12.", "Added Mod options to change tool limits and others."},
            {"      13.", TextFormatting.DARK_GREEN +"Many thx to Joe21, McHorse, Almos-Rhine, Nepisʹ (normally in cyrillic), Invisible Davis Studios for their awesome feedback. If there's anyone else with feedback in their minds please surrender it to me, preferable via the reports feature or on my Discord channel (See directions on my website: www.advancedcreationmod.com)."},
            {""},
            {"Details:"},
            {"      1.", "Added capability to use Items that are not blocks in isometric view. These are redirected to Vanilla Minecraft." +
                    " The toolbar indicator above the inventory bar will say 'Not Handled By Advanced Creation'. " +
                    "Examples: Minecarts, boats, bow & arrow, throwables, etc. (Deleting minecarts or boats has the small " +
                    "vanilla reach range)"},
            {"      2.", "Added ability to place TileEntities in general."},
            {"      3.", "Added support for placement and preview rendering of nearly all Vanilla TileEntities such as:" +
                    " Chests, pistons, redstone wires and components, rails, sign, etc… (also includes preview of these " +
                    "blocks in Templates) "},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      4.", "Open tool selection wheel keybind can now work with a mouse key. "},
            {"",
                    TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      5.", "Fixed placing beds,banners, etc... in multiple colors"},
            {"      6.", "Added ability to rotate blocks like skulls and signs between 16 positions instead of 4."},
            {"      7.", "Added ability to edit text on sign when right-click a sign."},
            {"      8.", "Added algorithm to attempt to make valid rail tracks and redstone wire circuits with any BUILD tool."},
            {"      9.", "You can interact with logic blocks in isometric view."},
            {"      10.", "Allow deleting in any tool when holding no block."},
            {"      11.", "Added TEXT_SIZE mod options to change the text font size in the Help screen. "},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from McHorse"},
            {"      12.", "Restored all Vanilla perspective (First-Person, Third-Person, Third-Person Front)"},
            {"      13.", "Added toggle button to change perspectives in the Ingame menu. "},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Invisible Davis Studios"},
            {"      14.", "Added toggle button to change gamemode (Creative and Survival) in the Ingame menu. "},
            {"      15.", "Restored normal functioning of Survival mode."},
            {"      16.", "Isometric view and all tools are now ONLY available in Creative mode as the 4th perspective mode."},
            {"      17.", "Added toggle button to change Ignore plants selection mode in Isometric view to the ingame menu."},
            {"      18.", "Added GuiScreen for the input of absolute coordinates for all tools in BUILD,PLACE and CREATE modes." +
                    " This screen has a dedicated hotkey and HUD button."},
            {"",TextFormatting.DARK_GREEN + "thx to feedback from Nepisʹ (normally in cyrillic)"},
            {"      19.", "Added HUD information about the start, middle and end positions currently selected for all " +
                    "tools in BUILD, PLACE and CREATE modes. This can be turned OFF in Mod Options."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Nepisʹ (normally in cyrillic)"},
            {"      20.", "Added Mod Option to show the absolute coordinate that the mouse is pointing at. Turned OFF by default."},
            {"      21.", "Added Mod Option to show the absolute coordinate of the camera focus point cross on screen. Turned OFF by default.\n"},
            {"      22.", "Fixed compatibility issues with Llibrary. Crude fix! Might cause issues, see Mod Options! "},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Almos-Rhine & McHorse"},
            {"      23.", "Divided Mod Options into Categories."},
            {"      24.", "Added Alt-mode capability to give an offset to the current selection to all BUILD mode tools. " +
                    "(Previously only available in CREATE and PLACE mode) "},
            {"",TextFormatting.DARK_GREEN + "thx to feedback from McHorse\n"},
            {"      25.", "Added better highlight of placement(white)/deletion(red) selection in game."},
            {"      26.", "Added Highlighting to indicate current OFFSET from mouse (goes from white to gray)."},
            {"      27.", "Added HUD information about the current OFFSET from mouse."},
            {"      28.", "Added HUD button to clear OFFSET from mouse."},
            {"      29.", "Added Highlighting of current selected positions (start= green, middle = blue, end = blue)."},
            {"      30.", "Fixed zooming bug after you have been scrolling in a menu/help screen."},
            {"      31.", "Fixed wrong preview of circle helplines in the Paint tool of EDIT mode when using a square brush size."},
            {"      32.", "Made Vanilla tool/block name highlight text, that is shown after selection, visible in Advanced Creation HUD interface."},
            {"      33.", "Fixed Crashes when trying to place incompatible blocks. " +
                    "Now warning message is given in chat when advanced creation is unable to place a block."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      34.", "Fixed occasional crashing in when cancelling CREATE mode"},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      35.", "Added ability to use other mousebuttons for zooming."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      36.", "Removed forced unbinding of Sprint buttons."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      37.", "Fixed crash in MOVE/DEL & COPY/PASTE tool when clicking without a block selected "},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from Joe21"},
            {"      38.", "Fixed bug where GUI_OVERLAY_VISIBLE mod option is overwritten."},
            {"", TextFormatting.DARK_GREEN + "thx to feedback from McHorse"},
            {"      39.", "Fixed critical bug where mod gets stuck in infinite loop while looking for the ground when player position is Y =< 0."},
            {"      40.", "Fixed bugged rendering of highlighted selection box side in last step of CREATE mode  when around the origin point of the map (0,0,0)."},
            {"      41.", "Help screen items and paragraphs are rearranged to reflect the changes."},
            {"      42.", "Promove 6 for redstone wire/rails placement on a hill is added to the help screen on promoves page."},
    };

    public static String[][] loadUpdateFile(String updateName)
    {
        String filename = updateName.replace(".","-").toLowerCase();
        String[][] res = new String[][]{{"", "404: FILE NOT FOUND"}};
        ArrayList<ArrayList<String>> updateText = new ArrayList<>();
        try
        {
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            ResourceLocation resourceLocation = new ResourceLocation(Reference.MODID, "updatechangeslogs/" + filename +".txt");
            IResource iresource =  resourceManager.getResource(resourceLocation);
            Scanner reader = new Scanner(iresource.getInputStream(),"utf-8");
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                if (data.contains("Highlights"))
                {
                    ArrayList<String> subtitle = new ArrayList<>();
                    subtitle.add(data.substring(1));
                    subtitle.add("");
                    updateText.add(subtitle);
                }
                else if ( data.contains("Details"))
                {
                    ArrayList<String> subtitle = new ArrayList<>();
                    subtitle.add(data);
                    subtitle.add("");
                    updateText.add(subtitle);
                }
                else if(data.isEmpty())
                {
                    continue;
                }
                else
                {
                    ArrayList<String> entry = new ArrayList<>();
                    int firstSpaceIndex = data.indexOf(" ");
                    String number = data.substring(0,firstSpaceIndex);
                    int numberLength = number.length();
                    for (int i = 0; i < (8-numberLength); i++)
                    {
                        number = " " + number;
                    }

                    entry.add(number);
                    String text = data.substring(firstSpaceIndex+1);
                    if (data.contains("Many thx"))
                    {
                        entry.add(TextFormatting.DARK_GREEN + text);
                        entry.add("");
                        updateText.add(entry);
                    }
                    else if (data.toLowerCase().contains("thx"))
                    {
                        int firstThxIndex = text.toLowerCase().indexOf("thx");
                        String thankyou = text.substring(firstThxIndex);
                        entry.add(text.substring(0,firstThxIndex-1));
                        updateText.add(entry);
                        ArrayList<String> thanksList = new ArrayList<String>(){{
                            add("");
                            add(TextFormatting.DARK_GREEN +  thankyou);
                        }};
                        updateText.add(thanksList);
                    }
                    else
                    {
                        entry.add(text);
                        updateText.add(entry);
                    }


                }

            }

            res = new String[updateText.size()][2];
            for (int i = 0; i < updateText.size(); i++)
            {
                ArrayList<String> entry = updateText.get(i);
                res[i][0] = entry.get(0);
                res[i][1] = entry.get(1);
            }
            System.out.println(res.length);

        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        finally
        {
            return res;
        }
//        return new String[][]{{"", "404: FILE NOT FOUND"}};
    }

    public static String alpha2_0_1_intro = "alpha2.0.2";
    public static String[][] alpha2_0_1_body = loadUpdateFile(alpha2_0_1_intro);

    public static String alpha2_0_2_intro = "alpha2.0.2";
    public static String[][] alpha2_0_2_body = loadUpdateFile(alpha2_0_2_intro);

    public static String beta1_2_intro = "beta1.2";
    public static String[][] beta1_2_body = loadUpdateFile(beta1_2_intro);

    public static String beta1_2_1_intro = "beta1.2.1";
    public static String[][] beta1_2_1_body = loadUpdateFile(beta1_2_1_intro);

    public static String beta1_2_2_intro = "beta1.2.2";
    public static String[][] beta1_2_2_body = loadUpdateFile(beta1_2_2_intro);

    public static String[][][][] updates = {
            {{{beta1_2_2_intro}}, beta1_2_2_body},
            {{{beta1_2_1_intro}}, beta1_2_1_body},
            {{{beta1_2_intro}}, beta1_2_body},
            {{{alpha2_0_2_intro}},alpha2_0_2_body},
            {{{alpha2_0_1_intro}},alpha2_0_1_body},
            {{{alpha2_0_intro}},alpha2_0_body},
            {{{alpha1_5_intro}},alpha1_5_body},
            {{{alpha1_4_intro}},alpha1_4_body},
            {{{alpha1_3_intro}},alpha1_3_body},
            {{{alpha1_2_intro}},alpha1_2_body},
            {{{alpha1_1_intro}},alpha1_1_body},
    };

}