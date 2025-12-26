package com.deadtiger.advcreation;

/**
 * The enum for the main modes seen in the mod on the left top corner GUI overlay
 */
public enum EnumMainMode
{
    BUILD(1),
    EDIT(2),
    PLACE(3),
    CREATE(4);

    public int index;

    EnumMainMode(int index)
    {
        this.index = index;
    }

    public EnumMainMode rotateMode()
    {
        switch (this)
        {
            case BUILD:
                return EDIT;
            case EDIT:
                return PLACE;
            case PLACE:
                return CREATE;
            case CREATE:
                return BUILD;
            default:
                throw new IllegalStateException("Unable to get ZY-rotated facing of " + this);
        }
    }
    
    public EnumMainMode rotateModeReversed()
    {
        switch (this)
        {
            case BUILD:
                return CREATE;
            case EDIT:
                return BUILD;
            case PLACE:
                return EDIT;
            case CREATE:
                return PLACE ;
            default:
                throw new IllegalStateException("Unable to get ZY-rotated facing of " + this);
        }
    }
}
