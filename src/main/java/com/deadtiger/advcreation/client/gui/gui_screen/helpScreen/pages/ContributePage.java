package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class ContributePage extends AbstractPage
{

    public ContributePage(String title)
    {
        super(title);

        //        ###############################################################################################
        String[][] prop = {{"You can make a bug report by pressing the hotkey shown above."},
                {"This will make a screenshot of the current screen and will allow you to describe the problem. There are multiple fields in this report that I would like you to fill in."},
                {""},
                {"The 1st field is called 'Type' with this I want you to describes the kind of issue you are reporting. There are a few option that appear, you don't have to " +
                        "choose one of those but I prefer you do it for my own administration, current options are:"},
                {"Bug:", "A graphical,logical or performance bug you have encountered in the mod."},
                {"Suggestion:", "An idea you have that you think would improve the mod."},
                {"Appreciation:", "If you like a certain feature in the mod let me know! Getting positive feedback tells me what I should definitely keep in the future."},
                {"Missing feature:", "Do you feel like you are missing some feature to easily complete the task you are trying to do?"},
                {"Confusing features:", "Are you having a hard time figuring out what the use is of a feature or how to use it?"},
                {"Other:", "Anything else you want to tell that doesn't fit in any of the types here?"},
                {"Awesomeness:", "Did you do something AWESOME with my mod? Let me know!"},
                {""},
                {"The 2nd field is called 'topic' with this I want you to describe what feature of the mod the reports is about. Again there are options that you can use from that I prefer you use:"},
                {"HUD interface:", "The buttons and information on screen during the game"},
                {"preview graphics:", "The non-existing blocks and lines you see when trying to build."},
                {"settings:", "Missing settings,problems with controls or confusing settings."},
                {"tool/interface organisation:", "Comments about how I have organised the features ex: BUILD/EDIT/PLACE/CREATE mainmode structure."},
                {"graphical issue:", "The rendering of the world or player. Sometimes thing don't render correctly"},
                {"tool functionality:", "How the tools function like the 3 clicks necessary for the RECTANGLE Tool, do you like it or not?"},
                {"user interface screen:", "Comments about the custom template inventory screen, the help screen or other screens."},
                {"help text:", "Comments specifically about instructions on the help screen. Is something Confusing?"},
                {"what is going on?:", "Select this if you have no clue what is going on in the screenshot."},
                {"other:", "Your report doesn't fit any of these topics?"},
                {"Look at this!:", "You just want to show me this for whatever reason."},
                {""},
                {"The next 2 fields is where you can freely type your subject and description of the problem/comment."},
                {""},
                {"The last field allows you to give the problem/comment a rating from 0 to 6. the options are as following:"},
                {"0", "game breaking"},
                {"1", "seriously annoying"},
                {"2", "mild annoyance"},
                {"3", "neutral"},
                {"4", "OK"},
                {"5", "great"},
                {"6", "WOW!"},
                {""},
                {"After you have made a few report you can zip the 'reports' folder that you can find in the '.minecraft' folder and upload it on my website at 'www.advancedcreationmod.com/upload_reports'"},
                {""},
                {"Not sure what warrants a bug report? Anything that bothers you, feels akward, is confusing, doesn't work for you personally. " +
                        "Literally anything is good enough! So don't be to critical of your own thoughts just spill them in the report and send it to me. " +
                        "I won't judge you! "},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.OPEN_REPORT_SCREEN.getKeybind()};
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings1, "Make bug report")};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "bugreport", "png", keys4, "Make Bug Reports", prop, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjust = {{"You can enable key and click logging in the mod options. This will create log files in the mod_logs folder inside the .minecraft folder. This will log all the actions that you do in the game. This can give me a lot of data that I can use to see what features players use a lot and how they are used and possibly identify problems or new features becuase of it."},
                {""},
                {"This feature is disabled by default because some players might feel this breaches there privacy." +
                        " I personally do not think this should be a concern since it only provides information regarding" +
                        " how you play minecraft with this mod. I don't believe this could ever give me any information about your personal life outside of minecraft."},
                {""},
                {"Anyway these logs are only stored locally and if you want to help me by providing them to me, you can zip the 'mod_logs' folder and upload it on my website on 'www.advancedcreationmod.com/upload_logs'"}
        };

        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "keylogs", "png", null, "Sending me keylogging files", adjust, mc.getResourceManager());

        //        ###############################################################################################
        String[][] iconmaker = {{"If you want to support me monetarily you can become a patreon or donate directly via PayPal. See my website on page 'www.advancedcreationmod.com/about_donate'"}
        };

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), null, "png", null, "Donate", iconmaker, mc.getResourceManager());

        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);

    }
}
