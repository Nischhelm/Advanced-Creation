package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

public class FpsOptimisationPage extends AbstractPage
{


    public FpsOptimisationPage(String title) {
        super(title);

        String[][] general = {{"As of Beta1.0, there is the ability to change certain settings to improve the frame rate of the game."},
                { "This function has been added to improve usability when working on large areas or when using big templates on weaker computers. "},
                {""},
                {"It works by giving the player a direct view of their current FPS above the help button. The caret next to it opens a small screen with arrows that allow the player to increase the FPS (up arrow) or decrease the FPS (down arrow)."},
                {"Of course, this doesn't magically make minecraft more efficient, it comes with a trade-off."},
                {"It works by changing the relevant mod options that control the amount of blocks previewed or the maximum size of the operation that can be performed by the player at one time."},
                {"Like all other HUD overlay buttons hovering over buttons shows a tooltip of what the button does."},
                {""},
                {"Only certain tools can/need to use this feature. These are the following:"},
                { "1.", "BUILD MODE: CIRCLE tool, PULL tool, FILLGAP tool, COPY/PASTE tool, MOVE/DEL tool"},
                { "2.", "All EDIT MODE tools"},
                { "3.", "PLACE MODE: only when a large enough template is in hand"},
                {"In all other situations the caret next the FPS counter is grayed out and the arrow buttons will be unavailable"},
                {""},
                {"If you are using this mod on a potato, I suggest you read on to see how you can improve your experience."}};

//        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),"trailer_ingame", "mp4", keys,"Overview",general, mc.getResourceManager());
//        para1.setFreezeEndGif(true);

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),"fps_optimisation_intro", "png",null,"Intro FPS Optimisation",general, mc.getResourceManager());

        //        ###############################################################################################
        String[][] absoluteCoordScreen = {{ "The above GIF animation shows an example of how to use FPS optimisation in PLACE mode for a large template."},
                {""},
                {"While you can open the FPS optimisation overlay yourself with the caret (as seen in previous paragraph). " +
                        "When the FPS drops below 5 frames per second for a short while the FPS optimisation overlay opens automatically and a prompt is given telling you that you have the option to improve your fps."},
                {"In the case of large templates, low FPS can occur when the program has to draw to many preview blocks. " +
                        "Therefore the amount of blocks that " +
                        "the mod shows you as preview of the template is decreased to improve the FPS when you press the up arrow. " +
                        "Do note that only the preview is reduced, the whole template is still placed when you right-click."},
                {""},
                {"If you are in the opposite situation: you have high enough FPS and you want to see more of the preview you can press the down arrow to increase the amount of " +
                        "preview blocks but decrease the FPS."},
                {""},
                {"Whenever you press any of the arrow buttons a tooltip pops up below the arrows that shows which mod option is changed and what the current value is."},
                {""},
                {"Which mod options are changed is also different from tool to tool. The following mod options are used for FPS optimisation:"},
                { "1.", "'Big template preview block limit':" },
                {"","Reducing this will show you less of a big template when trying to place it but your FPS should be better."},
                {"","Used for tools: PLACE MODE, COPY/PASTE tool, MOVE/DEL tool"},
                { "2.", "'Show top and bottom of big templates':"},
                {"","Controls whether to show the top and bottom layers of a big template"},
                {"","Used for tools: PLACE MODE, COPY/PASTE tool, MOVE/DEL tool"},
                { "3.", "'Max Circle radius':"},

                {"","Controls the maximum circle radius you can achieve when using a circular tool."},
                {"","Used for tools: All EDIT MODE tools (except PAINTBUCKET tool), CIRCLE tool"},
                { "4.","'Max placed/deleted blocks':" },
                {"", "Controls the maximum amount of blocks that can be placed at once for the BUILD mode PULL and FILLGAP tools and EDIT mode PAINTBUCKET tool"},
                {"", "Used for tools: PULL tool, FILLGAP tool,PAINTBUCKET tool"},
                {""},
                {"QUICK DISCLAIMER: Due to the complexity of making an automated FPS optimiser coupled with the facts that " +
                        "the FPS is dependent on your computer and I don't know what kind of trade-off the player wants between added limitations and their FPS. " +
                        "I have opted to make this feature player adjustable so you can choose what trade-off you want. I'd love it if you play around with it and give me some feedback on your thoughts."}
        };



        Paragraph para2= new Paragraph(startX,50,paraWidth + (200-startX),"69_fps_optimisation", "mp4", null,"Using FPS Optimisation",absoluteCoordScreen, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);

    }

}
