package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen;

import com.deadtiger.advcreation.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import scala.Array;

import java.util.ArrayList;

public class GuiScreenTextPrinter
{

    public final static double charSize = (10.1*0.5)*0.905;

    public static int drawAndFormatBody(String[][] body, double x, double y, int lineDistance)
    {
        return drawAndFormatBody(body,x,y,lineDistance,1.0);
    }

    public static int drawAndFormatBody(String[][] body, double x, double y, int lineDistance, double scale)
    {
        double charSize = (10.1*0.5);
        return drawAndFormatBody(body,x,y,lineDistance,scale,(int)((Minecraft.getMinecraft().displayWidth -50)/charSize));
    }

    public static int drawAndFormatBody(String[][] body, double x, double y, int lineDistance, double scale, int maxWidth) {

        // look for the longest first part of a line
        // save the length of the part to use later to align text to the right
        int maxLengthOfFirstPart = getMaxLengthOfFirstPartOfBody(body);
        int totalStringLimit = calcStringLimit(maxWidth, charSize);
        ArrayList<ArrayList<String>> newBody = formatBody(body, maxLengthOfFirstPart, totalStringLimit);

        return drawBody(x, y, newBody, maxLengthOfFirstPart, lineDistance, scale);
    }



    public static int drawBody(double x, double y, ArrayList<ArrayList<String>> newBody, int maxLengthOfFirstPart, int lineDistance, double scale)
    {
        int end = 0;
        for (int i = 0; i < newBody.size(); i++) {
            // assumes the line contains a control assignment when it has 2 or more strings
            // the first part is aligned to the right and the second part is drawn after that
            end = i;
            if (newBody.get(i).size() > 1) {
                drawText(newBody.get(i).get(0), (int) (Math.ceil(x) + maxLengthOfFirstPart * scale - Minecraft.getMinecraft().fontRenderer.getStringWidth(newBody.get(i).get(0)) * scale), (int) Math.ceil(y) + lineDistance * i, 0xFFFFFF, scale);
                drawText(newBody.get(i).get(1), (int) (Math.ceil(x) + (maxLengthOfFirstPart + 5)* scale), (int) Math.ceil(y) + lineDistance * i, 0xFFFFFF, scale);
            } else {
                drawText(newBody.get(i).get(0), (int) Math.ceil(x), (int) Math.ceil(y) + lineDistance * i, 0xFFFFFF, scale);
            }
        }
        return end;
    }


    public static int drawBodyNoSchadow(double x, double y, ArrayList<ArrayList<String>> newBody, int maxLengthOfFirstPart, int lineDistance, double scale,int color)
    {
        int end = 0;
        for (int i = 0; i < newBody.size(); i++) {
            // assumes the line contains a control assignment when it has 2 or more strings
            // the first part is aligned to the right and the second part is drawn after that
            end = i;
            if (newBody.get(i).size() > 1) {
                drawText(newBody.get(i).get(0), (int) (Math.ceil(x) + maxLengthOfFirstPart * scale - Minecraft.getMinecraft().fontRenderer.getStringWidth(newBody.get(i).get(0)) * scale), (int) Math.ceil(y) + lineDistance * i, color, scale,false,false);
                drawText(newBody.get(i).get(1), (int) (Math.ceil(x) + (maxLengthOfFirstPart + 5)* scale), (int) Math.ceil(y) + lineDistance * i, color, scale,false,false);
            } else {
                drawText(newBody.get(i).get(0), (int) Math.ceil(x), (int) Math.ceil(y) + lineDistance * i, color, scale,false,false);
            }
        }
        return end;
    }

    public static ArrayList<ArrayList<String>> formatBody(String[][] body, int maxLengthOfFirstPart, int totalStringLimit)
    {
        ArrayList<ArrayList<String>> newBody = new ArrayList<>();
        for (int i = 0; i < body.length; i++)
        {
            // assumes the line contains a control assignment when it has 2 or more strings
            // the first part is aligned to the right and the second part is drawn after that
            TextFormatting currformat = null;
            if (IsSummation(body[i]))
            {
                int newStringLimit = (int) (totalStringLimit - maxLengthOfFirstPart /4);
                if(newStringLimit < 20)
                    newStringLimit = 20;

                currformat = extractTextFormating(body[i][1]);
                if((Minecraft.getMinecraft().fontRenderer.getStringWidth(body[i][1])) > newStringLimit*charSize)
                {
                    String secondSplitString = formatNewLineFromBodyIntoNewBody(body[i][1], body[i][0], newStringLimit, newBody, currformat);
                    while((Minecraft.getMinecraft().fontRenderer.getStringWidth(secondSplitString)) > newStringLimit*charSize)
                    {
                        secondSplitString = formatNewLineFromBodyIntoNewBody(secondSplitString, " ", newStringLimit, newBody, currformat);
                    }
                    if(secondSplitString.length() > 0)
                        newBody.add(createNewBodyLine(currformat, secondSplitString, " "));
                }
                else
                {
                    newBody.add(createNewBodyLine(currformat, body[i][1], body[i][0]));
                }
            }
            else
            {
               ArrayList<String> newString = new ArrayList<String>();
                //if the string has a text formating at the beginning copy it
                currformat = extractTextFormating(body[i][0]);
                if( (Minecraft.getMinecraft().fontRenderer.getStringWidth(body[i][0])) > totalStringLimit *charSize)
                {
                    //format the first line
                    String secondSplitString = formatNewLineFromBodyIntoNewBody(body[i][0], null, totalStringLimit, newBody, currformat);
                    while((Minecraft.getMinecraft().fontRenderer.getStringWidth(secondSplitString)) > totalStringLimit *charSize)
                    {
                        secondSplitString = formatNewLineFromBodyIntoNewBody(secondSplitString, null, totalStringLimit, newBody, currformat);
                    }
                    if(secondSplitString.length() > 0)
                        newBody.add(createNewBodyLine(currformat, secondSplitString, null));
                }
                else
                {
                    newString.add(body[i][0]);
                    newBody.add(newString);
                }
            }
        }
        return newBody;
    }

    public static String formatNewLineFromBodyIntoNewBody(String secondSplitString, String firstLine, int newStringLimit, ArrayList<ArrayList<String>> newBody, TextFormatting currformat)
    {
        String[] splitBody;
        splitBody = splitAtFirstSpaceBeforeStringLimit(secondSplitString, newStringLimit);
        newBody.add(createNewBodyLine(currformat, splitBody[0], firstLine));

        secondSplitString = "";
        if (splitBody.length > 1)
            secondSplitString = splitBody[1];
        return secondSplitString;
    }

    public static ArrayList<String> createNewBodyLine(TextFormatting currformat, String secondLine, String firstLine)
    {
        ArrayList<String> newString;
        newString = new ArrayList<String>();
        if(firstLine != null)
            newString.add(firstLine);
        newString.add(addTextFormatting(currformat, secondLine.trim()));
        return newString;
    }

    private static String addTextFormatting(TextFormatting currformat, String trimmedBody)
    {
        String formatString;
        if(currformat != null)
            formatString = currformat + trimmedBody;
        else
            formatString = trimmedBody;
        return formatString;
    }

    private static TextFormatting extractTextFormating(String line)
    {
        TextFormatting currformat = null;
        for( TextFormatting format : TextFormatting.values())
        {
            if(line.contains(format.toString()))
            {
                currformat = format;
                break;
            }
        }
        return currformat;
    }

    public static String[] splitAtFirstSpaceBeforeStringLimit(String line, int newStringLimit)
    {
        String[] splitBody = line.split("(?<=\\G.{" + newStringLimit + "})", 2);
        int lastSpaceIndec = splitBody[0].lastIndexOf(" ");
        if (lastSpaceIndec > 0)
            splitBody = line.split("(?<=\\G.{" + lastSpaceIndec + "})", 2);
        return splitBody;
    }

    public static boolean IsSummation(String[] strings)
    {
        return strings.length > 1  && !strings[1].isEmpty();
    }

    public static int calcStringLimit(int maxWidth, double charSize)
    {
        int stringLimit = 0;
        if(maxWidth == 0)
            stringLimit =(int)((Minecraft.getMinecraft().displayWidth -50)/ charSize);
        else
            stringLimit =(int)(maxWidth / charSize);

        if(stringLimit < 20)
            stringLimit = 20;
        return stringLimit;
    }

    public static int getMaxLengthOfFirstPartOfBody(String[][] body)
    {
        int maxLength = 0;
        ArrayList<Integer> charLengthList = new ArrayList<Integer>();
        for (int i = 0; i < body.length; i++) {
            if (IsSummation(body[i])) {
                // count the lenght of all characters in the string
                int charlenght = 0;
                for (char c : body[i][0].toCharArray()) {
                    charlenght += (int) Minecraft.getMinecraft().fontRenderer.getCharWidth(c)*(ConfigurationHandler.general.HELP_TEXT_SIZE/100.0);
                }
                charLengthList.add(charlenght);

                //if the current charlength is bigger then the previous save it
                if (charlenght > maxLength) {
                    maxLength = charlenght;
                }
            } else {
                charLengthList.add(0);
            }
        }
        return maxLength;
    }

    public static void drawTitle(String title, double x, double y, double scale) {
         scale = 1.5 *scale;
        GlStateManager.pushMatrix();
        {
            GlStateManager.scale(scale, scale, scale);
            Minecraft.getMinecraft().fontRenderer.drawString(title, (int) Math.ceil(x * (1 / scale)), (int) Math.ceil(y * (1 / scale)), 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

    public static void drawText(String text, double x, double y, int color, double scale) {
        drawText(text,x,y,color,scale,false);
    }

    public static void drawText(String text, double x, double y, int color, double scale, boolean centered) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.scale(scale, scale, scale);
            if(centered)
                Minecraft.getMinecraft().fontRenderer.drawString(text, (int) Math.ceil((x * (1 / scale))-((Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2))), (int) Math.ceil(y * (1 / scale)), color);
            else
                Minecraft.getMinecraft().fontRenderer.drawString(text, (int) Math.ceil(x * (1 / scale)), (int) Math.ceil(y * (1 / scale)), color);
        }
        GlStateManager.popMatrix();
    }

    public static void drawText( String text, double x, double y, int color, double scale, boolean centered,boolean schadow) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.scale(scale, scale, scale);
            if(centered)
            {
                if(schadow)
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (int) Math.ceil((x * (1 / scale))-((Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2))), (int) Math.ceil(y * (1 / scale)), color);
                else
                    Minecraft.getMinecraft().fontRenderer.drawString(text, (int) Math.ceil((x * (1 / scale))-((Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2))), (int) Math.ceil(y * (1 / scale)), color);
            }
            else
            {
                if(schadow)
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow ( text, (int) Math.ceil(x * (1 / scale)), (int) Math.ceil(y * (1 / scale)), color);
                else
                    Minecraft.getMinecraft().fontRenderer.drawString ( text, (int) Math.ceil(x * (1 / scale)), (int) Math.ceil(y * (1 / scale)), color);
            }

        }
        GlStateManager.popMatrix();
    }

    public static void setColor(String[][] text, TextFormatting format)
    {
        setColor(text,format, Array.emptyIntArray());
    }

    public static void setColor(String[][] text, TextFormatting format, int[] exclude_list)
    {
        for(int i=0; i < text.length;i++) {
            boolean skip = false;
            for (int num :exclude_list)
            {
                if(i == num)
                {
                    skip = true;
                    break;
                }
            }
            if(skip)
                continue;

            for(int j = 0;j < text[i].length;j++)
            {
                text[i][j] = format + text[i][j];
            }
        }
    }
}
