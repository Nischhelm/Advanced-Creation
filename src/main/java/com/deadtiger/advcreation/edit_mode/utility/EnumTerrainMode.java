package com.deadtiger.advcreation.edit_mode.utility;

public enum EnumTerrainMode
{
    ONLY_TERRAIN("ONLY TERRAIN","Only manipulate blocks that are part of the terrain"),
    ALl("ALL BLOCKS","Manipulate all blocks");

    public String buttonText;
    public String tooltipText;

    EnumTerrainMode(String buttonText, String tooltipText)
    {
        this.buttonText = buttonText;
        this.tooltipText = tooltipText;
    }
    
    public EnumTerrainMode rotateMode()
    {
        switch (this)
        {
            case ONLY_TERRAIN:
                return ALl;
            case ALl:
                return ONLY_TERRAIN;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }
}
