package com.deadtiger.advcreation.edit_mode.utility;

import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.BaseAdjustMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.DigRaiseAdjustMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.LevelAdjustMode;

public enum EnumTerrainShapeMode
{
    PLANE(1,"PLANE","Use the strongest smoothening but weakest sharpening effect","Level with a flat slope"),
    RISE(2,"RISE","Use a strong smoothening but weak sharpening effect","Level with a gentle slope"),
    SLOPE(3,"SLOPE","Use a decent smoothening but mild sharpening effect","Level with a long medium slope"),
    HILL(4,"HILL","Use a medium smoothening & medium sharpening effect","Level with a medium slope"),
    VALLEY(5,"VALLEY","Use a decent sharpening & mild smoothening effect","Level with a short medium slope"),
    MOUNTAIN(6, "MOUNTAIN","Use a strong sharpening but weak smoothening effect","Level with a steep slope"),
    CLIFF(7, "CLIFF","Use the strongest sharpening but weakest smoothening effect","Level with a intense slope"),
    STRAIGHT(8, "STRAIGHT","Use straight-edged sharpening and NO smoothening effect","Level with a straight edge");


    public int intensityNumber;
    public String buttonText;
    public String tooltipText;
    public String tooltipTextAlt;

    EnumTerrainShapeMode(int intensityNumber, String buttonText, String tooltipText)
    {
        this(intensityNumber,buttonText,tooltipText,tooltipText);
    }

    EnumTerrainShapeMode(int intensityNumber, String buttonText, String tooltipText, String tooltipTextAlt)
    {
        this.intensityNumber = intensityNumber;
        this.buttonText = buttonText;
        this.tooltipText = tooltipText;
        this.tooltipTextAlt = tooltipTextAlt;
    }

    public EnumTerrainShapeMode rotateMode()
    {
        switch (this)
        {
            case PLANE:
                return RISE;
            case RISE:
                return SLOPE;
            case SLOPE:
                return HILL;
            case HILL:
                return VALLEY;
            case VALLEY:
                return MOUNTAIN;
            case MOUNTAIN:
                return CLIFF;
            case CLIFF:
                return STRAIGHT;
            case STRAIGHT:
                return PLANE;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }
    
    public EnumTerrainShapeMode rotateOpositeMode()
    {
        switch (this)
        {
            case PLANE:
                return STRAIGHT;
            case STRAIGHT:
                return CLIFF;
            case CLIFF:
                return MOUNTAIN;
            case MOUNTAIN:
                return VALLEY;
            case VALLEY:
                return HILL;
            case HILL:
                return SLOPE;
            case SLOPE:
                return RISE;
            case RISE:
                return PLANE;
            default:
                throw new IllegalStateException("Invalid Direction Mode!!");
        }
    }

    public String getButtonText()
    {
        return this.intensityNumber + "." + this.buttonText;
    }

    public int getDataIndex()
    {
        if(this.intensityNumber >= TerrainEditData.smoothingData.size())
        {
            return TerrainEditData.smoothingData.size()-1;
        }
        return this.intensityNumber-1;
    }

    public String getDigRaiseTooltipText()
    {
        if(this.intensityNumber == 1)
            return "Dig/Raise a hole/hill of 1 block";
        else
            return "Dig/Raise a hole/hill of "+ EditMode.TERRAIN_SHAPE_MODE.intensityNumber +" blocks";
    }

    public String getLevelTooltipText()
    {
        return tooltipTextAlt;
    }

    public String getAdjustModeAppropriateTooltipText(BaseAdjustMode tool)
    {
        if(tool instanceof LevelAdjustMode)
            return this.tooltipTextAlt;
        else if(tool instanceof DigRaiseAdjustMode)
            return this.getDigRaiseTooltipText();
        return this.tooltipText;
    }
}
