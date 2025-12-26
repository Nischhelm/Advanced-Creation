package com.deadtiger.advcreation.client.input;

import org.lwjgl.input.Mouse;

public class Mousebindings
{
    public static boolean initialiseCameraRotation = true;
    
    Mousebindings()
    {
    
    }

    public static int getWheel()
    {
        return Mouse.getDWheel();
    }
    
    public static int getDX(){return Mouse.getDX();}

    public static int getDY(){return Mouse.getDY();}
    
    public static boolean isRightButtonDown(){return Mouse.isButtonDown(1);}

    public static boolean isLeftButtonDown(){return Mouse.isButtonDown(0);}

    public static boolean isMiddleButtonDown(){return Mouse.isButtonDown(2);}

    
    
}
