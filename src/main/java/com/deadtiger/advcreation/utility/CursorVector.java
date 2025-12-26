package com.deadtiger.advcreation.utility;

import net.minecraft.util.math.Vec3d;

public class CursorVector
{
    
    public final Vec3d start;
    public final Vec3d end;

    public CursorVector()
    {
        this(Vec3d.ZERO,Vec3d.ZERO);
    }

    public CursorVector(Vec3d start, Vec3d end) {
        this.start = start;
        this.end = end;
    }

    public CursorVector(CursorVector cursorVector) {
        this.start = cursorVector.start;
        this.end = cursorVector.end;
    }
}
