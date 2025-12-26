package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.UpdateChangesText;

public class ModChangesPage extends AbstractPage
{


    public ModChangesPage(String title)
    {
        super(title);

        String[][] general = {{ "Here you can see all the changes that are made in all new versions."}};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),null, "mp4", null,"Intro Update Changelogs",general, mc.getResourceManager());
        paragraphs.add(para1);

        for (String[][][] version : UpdateChangesText.updates)
        {
            String subTitle = version[0][0][0];
            String[][] body = version[1];
            Paragraph para2 = new Paragraph(startX,50,paraWidth + (200-startX),null, "mp4", null, subTitle,body, mc.getResourceManager());
            paragraphs.add(para2);
        }
    }

}
