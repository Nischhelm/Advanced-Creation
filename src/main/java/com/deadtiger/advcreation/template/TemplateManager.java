package com.deadtiger.advcreation.template;

import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplateInventoryScreenSimple;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.utility.TemplateFolderUtility;
import com.deadtiger.advcreation.utility.UnzipUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TemplateManager
{


    //The list of all the templates available to the player
    public static ArrayList<Template> TEMPLATES_LIST = new ArrayList<>();
    public static ArrayList<String> FILENAME_LIST = new ArrayList<>();
    public static HashMap<String, String> MAP_FILENAME_TO_PROPERTIES_PATH = new HashMap<>();
    public static HashMap<String, String> MAP_FILENAME_TO_FOLDERNAME = new HashMap<>();

    public static ArrayList<String> templatesCompatibleMcVersions = new ArrayList<>();
    public static ArrayList<String> templateMcVersionsFound = new ArrayList<>();


    static
    {
        templatesCompatibleMcVersions.add(Reference.MC_VERSION);
        templatesCompatibleMcVersions.add("1.12");

        for (String version: templatesCompatibleMcVersions)
        {
            if(!GuiTemplateInventoryScreenSimple.currMcVersionNames.contains(version))
                GuiTemplateInventoryScreenSimple.currMcVersionNames.add(version);
        }

    }

    /**
     * Extracts all template zipfiles in the "advcreation_template_zip" folder that do not already exist in the
     * "advcreation_template" folder
     */
    public static void extractTemplateZips()
    {
        File mcDataFolder = Minecraft.getMinecraft().gameDir;
        File zippedTemplateFolder = new File(mcDataFolder, "advcreation_templates_zips");
        File templatesFolder = new File(mcDataFolder, "advcreation_templates");

        if (!zippedTemplateFolder.exists())
        {
            //No Zipped templates exist so just make the zipped template folder and return
            zippedTemplateFolder.mkdir();
            return;
        }

        File[] listOfZippedTemplateFiles = zippedTemplateFolder.listFiles();
        File[] listOfUnzippedTemplateFolders;

        //list existing unzipped templates folders
        if (!templatesFolder.exists())
        {
            templatesFolder.mkdir();
        }
        listOfUnzippedTemplateFolders = templatesFolder.listFiles();

        for (File zippedTemplateFile : listOfZippedTemplateFiles)
        {
            if (zippedTemplateFile.isFile())
            {
                //if it is a template zipfile extract the name of the template folder and check if that folder already exists
                processTemplateZipfile(templatesFolder, listOfUnzippedTemplateFolders, zippedTemplateFile);
            }
            else if (zippedTemplateFile.isDirectory())
            {
                System.out.println(zippedTemplateFile.getName() + "is a directory in " + zippedTemplateFolder.getName() + " which should only contain .zip files of templates");
            }
        }
    }

    /**
     *  load all templates in the unzipped templates folder
     */
    public static void loadTemplates()
    {
        TEMPLATES_LIST.clear();
        File mcDataFolder = Minecraft.getMinecraft().gameDir;
        File templatesFolder = new File(mcDataFolder, "advcreation_templates");
        if (!templatesFolder.exists())
        {
            templatesFolder.mkdir();
            return;
        }

        File[] listOfUnzippedTemplateFiles = templatesFolder.listFiles();
        for (File UnzippedTemplateFile : listOfUnzippedTemplateFiles)
        {
            if (UnzippedTemplateFile.isFile())
            {
                //alpha1.1 templates were files now they are folders
                convertOldFileToNewFormat(templatesFolder, UnzippedTemplateFile);
            }
            else if (UnzippedTemplateFile.isDirectory())
            {
                File[] templateFiles = UnzippedTemplateFile.listFiles();
                String templateName = null;
                System.out.println("Directory " + UnzippedTemplateFile.getName());
                //first go through all the files in the folder to find the nbt file
                for (File templateFile : templateFiles)
                {
                    if (templateFile.getName().contains(".nbt"))
                    {
                        templateName = templateFile.getName();
                        FILENAME_LIST.add(templateName);
                    }
                }
                if (templateName == null)
                    continue;

                //if the name of the directory contains _ver it is a file from the alpha1.3 mod version
                if (UnzippedTemplateFile.getName().contains("_ver"))
                {
                    //go through all the files to find the properties file
                    for (File templateFile : templateFiles)
                    {
                        if (templateFile.getName().contains("properties.txt"))
                        {
                            Template template = new Template(1, templateName, templateFile.getAbsolutePath(), UnzippedTemplateFile.getName());
                            TEMPLATES_LIST.add(template);
                            TemplateManager.addNewTemplateMcVersionFound(template.getMcVersion());
                        }
                    }
                }
                else if (UnzippedTemplateFile.getName().contains("_hash"))//needs to be after the "_ver" check!
                {
                    //if the name of the directory contains only _hash it is a file from the alpha1.2 mod version
                    Template template = new Template(1, templateName);
                    TEMPLATES_LIST.add(template);
                    convertOldFolderToNewFormat(templateName, template);
                }
            }
        }
    }


    /**
     * This is an old function when I tried to load only those templates that where on screen at the moment.
     * Might be usefull later to reduce the amount of memory used for unused templates
     * change is either 1 or -1 to indicate in which direction of the list we are going
     * load only the templates displayed in the template menu
     *
     * change is either 1 or -1 to indicate in which direction of the list we are going
     * load only the templates displayed in the template menu
     * @param change
     */
    public static void reloadTemplates(int change)
    {
        if (change == -1)
        {
            //we go down in the list so we add to the front of the list and delete the last template
            TEMPLATES_LIST.remove(TEMPLATES_LIST.size() - 1);
            Template template = new Template();
            if (FILENAME_LIST.size() > GuiTemplaceInventoryScreenFunctionality.displayed_index && 0 <= GuiTemplaceInventoryScreenFunctionality.displayed_index)
            {
                template = new Template(1, FILENAME_LIST.get(GuiTemplaceInventoryScreenFunctionality.displayed_index));
            }
            TEMPLATES_LIST.add(0, template);
        }
        else if (change == 1)
        {
            //we go up in the list so we add to the end of the list and delete the first template
            TEMPLATES_LIST.remove(0);
            int index = GuiTemplaceInventoryScreenFunctionality.displayed_index + 2;
            Template template = new Template();
            if (FILENAME_LIST.size() > index && 0 <= index)
            {
                template = new Template(1, FILENAME_LIST.get(index));
            }
            TEMPLATES_LIST.add(template);
        }
        else
        {
            //reload the templates without any changes to the template list
            TEMPLATES_LIST.clear();
            for (int i = 0; i < 3; i++)
            {
                int index = GuiTemplaceInventoryScreenFunctionality.displayed_index + i;
                Template template = new Template();
                if (FILENAME_LIST.size() > index && 0 <= index)
                {
                    template = new Template(1, FILENAME_LIST.get(index));
                }
                TEMPLATES_LIST.add(template);
            }
        }
    }


    private static void processTemplateZipfile(File templatesFolder, File[] listOfUnzippedTemplateFolders, File listOfZippedTemplateFile)
    {
        String zipName = (listOfZippedTemplateFile.getName().split("\\.zip"))[0];
        boolean doesNotExistYet = true;

        if (listOfUnzippedTemplateFolders.length != 0)
        {
            for (File unzippedFile : listOfUnzippedTemplateFolders)
            {
                //Check if the template zipfile has the same name as a unzipped template folder
                if (unzippedFile.getName().equals(zipName))
                {
                    //also check if all the icons of template are present
                    doesNotExistYet = checkIfIconsDontExist(templatesFolder, zipName);
                }
            }
        }

        //if the unzipped template folder didn't exist yet unzip the template zipfile
        if (doesNotExistYet)
        {
            try
            {
                UnzipUtility.unzip(listOfZippedTemplateFile.getAbsolutePath(), templatesFolder.getAbsolutePath() + "//" + zipName);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("File " + listOfZippedTemplateFile.getName());
    }


    /**
     * Checks if all 8 icons of a template are in the template folder
     * @param templatesFolder
     * @param zipName
     * @return True if icons don't exist, False if the do
     */
    private static boolean checkIfIconsDontExist(File templatesFolder, String zipName)
    {
        boolean doesNotExistYet;
        doesNotExistYet = false;
        boolean selected = false;
        for (EnumFacing orient : EnumFacing.HORIZONTALS)
        {
            String fileName = orient.toString() + "_selected_" + selected + ".png";
            File templateDir = new File(templatesFolder, zipName);
            File file2 = new File(templateDir, fileName);
            if (!file2.exists())
            {
                doesNotExistYet = true;
                break;
            }
        }
        if (!doesNotExistYet)
        {
            selected = true;
            for (EnumFacing orient : EnumFacing.HORIZONTALS)
            {
                String fileName = orient.toString() + "_selected_" + selected + ".png";
                File templateDir = new File(templatesFolder, zipName);
                File file2 = new File(templateDir, fileName);
                if (!file2.exists())
                {
                    doesNotExistYet = true;
                    break;
                }
            }
        }
        return doesNotExistYet;
    }

    /**
     * Convert template file from alpha1.1 to a template folder of alpha1.3
     * @param templatesFolder   The file object of the folder with unzipped templates
     * @param templateFile      The old .nbt file object that is transformed to the new format
     */
    private static void convertOldFileToNewFormat(File templatesFolder, File templateFile)
    {
        System.out.println("Convert old File " + templateFile.getName() + " to new format ");
        String TemplateFilterName = templateFile.getName();
        Template template = new Template(0, TemplateFilterName);
        template.tryCalculateProperties();
        if (template.save())
        {
            File newDir = new File(templatesFolder, TemplateFilterName.split(".nbt")[0]);
            File newTemplate = new File(newDir, TemplateFilterName);

            FILENAME_LIST.add(newTemplate.getName());

            if (templateFile.delete())
            {
                String TemplateFilePath = templateFile.getAbsolutePath();
                System.out.println("New format folder '" + newTemplate.getName() +"' created, Original file deleted " + TemplateFilePath);
            }
        }
    }


    private static void convertOldFolderToNewFormat(String filename, Template template)
    {
        //calculate new properties and write the template again in the new format
        template.tryCalculateProperties();
        template.writeTemplate();

        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File folder = new File(mcDataDir, "advcreation_templates");
        File oldTemplateFolder = new File(folder, filename.split(".nbt")[0]);
        File newTemplateFolder = new File(folder, template.getDirname());

        copyIcons(oldTemplateFolder, newTemplateFolder);
        //zip the template and delete the original folder
        template.zipTemplate();
        TemplateFolderUtility.deleteFolder(oldTemplateFolder);
    }

    private static void copyIcons(File oldTemplateFolder, File newTemplateFolder)
    {
        File[] listOfFiles = oldTemplateFolder.listFiles();
        for (File file : listOfFiles)
        {
            if (file.isFile() && file.getName().contains(".png"))
            {
                File newImage = new File(newTemplateFolder, file.getName());

                //copies the image to the new directory
                try(InputStream in = new BufferedInputStream(new FileInputStream(file)))
                {
                    newImage.createNewFile();

                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(newImage));

                    byte[] buffer = new byte[1024];
                    int lengthRead;
                    while ((lengthRead = in.read(buffer)) > 0)
                    {
                        out.write(buffer, 0, lengthRead);
                        out.flush();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }


    public static boolean isAllowedMcVersion(String mcVersion)
    {
        return templatesCompatibleMcVersions.contains(mcVersion);
    }


    public static void addNewTemplateMcVersionFound(String mcVersion)
    {
        if(!templateMcVersionsFound.contains(mcVersion))
            templateMcVersionsFound.add(mcVersion);
    }
}
