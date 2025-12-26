package com.deadtiger.advcreation.utility;

import java.io.File;

public class TemplateFolderUtility
{
    public static void deleteFolder(File saves) {
        File[] listOfFiles = saves.listFiles();
        for (File listOfFile : listOfFiles)
        {
            if (listOfFile.isFile())
            {
                listOfFile.delete();
            }
        }
        saves.delete();
    }
}
