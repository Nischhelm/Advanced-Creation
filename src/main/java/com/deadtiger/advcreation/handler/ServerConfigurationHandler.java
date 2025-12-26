package com.deadtiger.advcreation.handler;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraftforge.common.config.Config;

@Config(modid=Reference.MODID,name=Reference.MODID+"_server",category = "general")
public class ServerConfigurationHandler
{

    @Config.Comment({"Does the server print all the logs it receives from the players on the console"})
    @Config.Name("Print actions")
    public static boolean PRINT_ACTIONS = true;
}
