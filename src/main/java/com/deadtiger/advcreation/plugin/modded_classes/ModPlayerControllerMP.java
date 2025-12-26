package com.deadtiger.advcreation.plugin.modded_classes;

//class to change the BlockReachDistance in Class PlayerControllerMP
public class ModPlayerControllerMP
{
    public static float customReachDistance = 1000.0F;
    public static float normalReachDistance = 5f;
    public static boolean giveCustomReachDistance = false;
    
    public static float getCustomReachDistance()
    {
        return getCustomReachDistance(giveCustomReachDistance);
    }

    public static float getCustomReachDistance(boolean custom)
    {
        if(custom)
            return customReachDistance;
        else
            return normalReachDistance;
    }
}
