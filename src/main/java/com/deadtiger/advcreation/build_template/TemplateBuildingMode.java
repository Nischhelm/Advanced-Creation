package com.deadtiger.advcreation.build_template;

/**
 * 3 modes when making a template
 * IDLE:    No start of a template is selected, the player needs to select a block to start making a template
 * START_POS_REFINEMENT:    a starting position of the template is selected with the mouse, the player can now refine
 * which block is the starting position with the arrow keys, to continue the player must
 * select a position where the template will end.
 * END_POS_REFINEMENT:      a end position is selected and can now be refined with the arrow keys, to create the
 * template the player must press "enter"
 */
public enum TemplateBuildingMode
{
    SELECT_START_POS(0),
    SELECT_END_POS(1),
    MAKE_ADJUSTMENTS(2);

    public int index;

    TemplateBuildingMode(int index)
    {
        this.index = index;
    }
}
