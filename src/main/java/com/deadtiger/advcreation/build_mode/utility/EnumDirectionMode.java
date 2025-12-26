package com.deadtiger.advcreation.build_mode.utility;

public enum EnumDirectionMode
{
    XY("XY","Selection keeps in XY plane"),
    ZY("ZY","Selection keeps in ZY plane"),
    XZ("XZ","Selection keeps in XZ plane"),
    FREE("FREE","Free Selection"),
    GROUND("GRND","Selection Follows Ground"),
    AUTO("AUTO","Automatically predicts XY,ZY,XZ direction");
    
    public String buttonText;
    public String tooltipText;
    
    EnumDirectionMode(String buttonText, String tooltipText)
    {
        this.buttonText = buttonText;
        this.tooltipText = tooltipText;
    }
    
    public EnumDirectionMode rotateMode()
    {
        switch (this)
        {
            case XY:
                return ZY;
            case ZY:
                return XZ;
            case XZ:
                return AUTO;
            case AUTO:
                return FREE;
            case FREE:
                return GROUND;
            case GROUND:
                return XY;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }
    
    public EnumDirectionMode rotateOpositeMode()
    {
        switch (this)
        {
            case XY:
                return GROUND;
            case GROUND:
                return FREE;
            case FREE:
                return AUTO;
            case AUTO:
                return XZ;
            case XZ:
                return ZY;
            case ZY:
                return XY;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }
}
