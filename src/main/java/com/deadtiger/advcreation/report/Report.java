package com.deadtiger.advcreation.report;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.util.Closer;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

public class Report
{
    public static String TIP_REPORT_TYPE = "What type of issue are you reporting";
    public static String TIP_REPORT_TOPIC = "What is the report about";
    public static String TIP_REPORT_RATING = "How do you feel about the issue";

    //template property options
    public static ArrayList<String> TYPES = new ArrayList<>(Arrays.asList(
            "bug","suggestion","appreciation","missing feature","confusing feature","other","Awesomeness"));
    public static ArrayList<String> TOPICS = new ArrayList<>(Arrays.asList(
            "HUD interface","preview graphics","settings","tool/interface organisation","graphical issue",
            "tool functionality","user interface screen", "help text","what is going on?","other","Look at this!"));
    public static ArrayList<String> RATINGS = new ArrayList<>(Arrays.asList(
            "0 - game breaking","1 - seriously annoying","2 - mild annoyance","3 - neutral","4 - OK","5 - great","6 - WOW!"));

    public String playerName;
    public String type;
    public String topic;
    public int rating;
    public String subject;
    public String description;

    public ResourceLocation screenshot;

    public static LocalDateTime TIME;
    public static File SCREENSHOT_DIR;
    public static File SCREENSHOT_LOCATION;
    public static int SCREENSHOT_WIDTH;
    public static int SCREENSHOT_HEIGHT;

    public static Report INSTANCE;

    public Report()
    {
        if(SCREENSHOT_LOCATION != null)
        {
            this.screenshot = getScreenshot(SCREENSHOT_LOCATION,"report_screenshot");
        }
        this.playerName = Minecraft.getMinecraft().player.getName();
        this.type = "Type";
        this.topic = "Topic";
        this.rating = 3;
        this.subject = "Subject";
        this.description = "Description";
    }

    public Report(String playerName, String type, String topic, int rating, String subject , String description)
    {
        this.playerName = playerName;
        this.type = type;
        this.topic = topic;
        this.rating = rating;
        this.subject = subject;
        this.description = description;

    }

    public static void saveScreenshot()
    {
        TIME = LocalDateTime.now(ZoneId.systemDefault());
        Minecraft mc =  Minecraft.getMinecraft();
        File mod_reports = new File(mc.gameDir,"reports");
        File mod_reports_player = new File(mod_reports,"reports_" + Minecraft.getMinecraft().player.getName());
        String dirName ="report_" + TIME.toString().replace('.','-').replace(':','-') ;
        SCREENSHOT_DIR = new File(mod_reports_player, dirName);

        if (!mod_reports.exists())
        {
            if (!mod_reports.mkdirs())
            {
                return ;
            }
        }
        if (!mod_reports_player.exists())
        {
            if (!mod_reports_player.mkdirs())
            {
                return;
            }
        }
        if(!SCREENSHOT_DIR.mkdirs())
            return;

        SCREENSHOT_WIDTH = mc.displayWidth;
        SCREENSHOT_HEIGHT = mc.displayHeight;

        SCREENSHOT_LOCATION =saveScreenshot(SCREENSHOT_DIR,"report_screenshot" +".png", mc.displayWidth, mc.displayHeight, mc.getFramebuffer());


    }

    public ResourceLocation getScreenshot(File icon,String name)
    {
        ResourceLocation output = null;

        if (icon.exists()) {
            InputStream inputstream  = null;
            try
            {
                Minecraft mc = Minecraft.getMinecraft();
                inputstream = new FileInputStream(icon);
                output = mc.renderEngine.getDynamicTextureLocation(name, new DynamicTexture(ImageIO.read(inputstream)));

            }
            catch (IOException ioexception)
            {
                LogManager.getLogger().error("Unable to load logo: {}", icon, ioexception);
            }
            finally
            {
                try {
                    Closer.close(inputstream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else
            return null;
        return output;
    }

    public static File saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer)
    {
        try
        {
            BufferedImage bufferedimage = ScreenShotHelper.createScreenshot(width, height, buffer);
            File file2;

            file2 = new File(gameDirectory, screenshotName);

            file2 = file2.getCanonicalFile(); // FORGE: Fix errors on Windows with paths that include \.\
            net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(bufferedimage, file2);
            if (event.isCanceled()) return null; else file2 = event.getScreenshotFile();
            ImageIO.write(bufferedimage, "png", file2);
            ITextComponent itextcomponent = new TextComponentString(file2.getName());
            itextcomponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            itextcomponent.getStyle().setUnderlined(Boolean.valueOf(true));
            if (event.getResultMessage() != null) return null;
            return file2;
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    public boolean deleteScreenshot()
    {
        if(SCREENSHOT_DIR.exists())
        {
            File[] files = SCREENSHOT_DIR.listFiles();
            for(File file: files)
            {
                file.delete();
            }
            SCREENSHOT_DIR.delete();
        }

        return SCREENSHOT_DIR.delete();
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean setTextRating(String ratingText)
    {
        if(RATINGS.contains(ratingText))
        {
            this.setRating(RATINGS.indexOf(ratingText));
            return true;
        }
        return false;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean saveReport() {
        File file2 = new File(SCREENSHOT_DIR, "report_"+ playerName + ".txt");

        String mode = AdvCreation.getMode().name();
        String toolName = getToolName(AdvCreation.getMode());

        OutputStream outputstream = null;
        boolean flag2;
        try
        {
            outputstream = new FileOutputStream(file2);
            try (PrintWriter p = new PrintWriter(outputstream ))
            {
                p.println("mc_version; mod_version; time; player; mode; toolname; type; topic; rating; subject; description; screenshotName");
                p.println(Reference.MC_VERSION + "; " + Reference.MOD_VERSION + "; " + TIME + "; " + playerName + "; " +
                        mode + "; " +
                        toolName + "; " +
                        type + "; " +
                        topic + "; " +
                        rating + "; " +
                        subject + "; " +
                        description);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            return false;
        }
        catch (Throwable var13)
        {
            flag2 = false;
        }
        finally
        {
            IOUtils.closeQuietly(outputstream);
        }
        return flag2;
    }

    public static String getToolName(EnumMainMode mode)
    {
        if(mode.equals(EnumMainMode.BUILD))
            return BuildMode.TOOLMODE.toolModeName;
        else if(mode.equals(EnumMainMode.EDIT))
           return BuildMode.TOOLMODE.toolModeName;
        else if(mode.equals(EnumMainMode.PLACE))
        {
            int select_index = GuiTemplaceInventoryScreenFunctionality.selected_index;
            if(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index )
                return TemplateManager.FILENAME_LIST.get(select_index);
            return "None";
        }
        else
           return BuildTemplateMode.MODE.toString();
    }
}
