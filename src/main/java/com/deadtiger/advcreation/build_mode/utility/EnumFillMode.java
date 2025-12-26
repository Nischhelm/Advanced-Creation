package com.deadtiger.advcreation.build_mode.utility;

public enum EnumFillMode
{
    NO_FILL("NO FILL","Leave Enclosed Gaps Open"),
    FILL("FILL","Fill Any Enclosed Gaps");
    
    public String buttonText;
    public String tooltipText;
    
    EnumFillMode(String buttonText, String tooltipText)
    {
        this.buttonText = buttonText;
        this.tooltipText = tooltipText;
    }
    
    public EnumFillMode rotateMode()
    {
        switch (this)
        {
            case NO_FILL:
                return FILL;
            case FILL:
                return NO_FILL;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }
}
