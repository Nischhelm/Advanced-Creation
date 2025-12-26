package com.deadtiger.advcreation.build_mode.utility;


import net.minecraft.util.math.Vec3d;

public class FillVector
{
    public Vec3d startVec = null;
    public Vec3d endVec = null;
    public double x;
    public double y;
    public double z;
    
    public float red;
    public float green;
    public float blue;
    
    public FillVector() {
    }
    
    public FillVector(Vec3d startVec)
    {
        this.startVec = startVec;
        this.x = startVec.x;
        this.y = startVec.y;
        this.z = startVec.z;
        this.red= 0.0f;
        this.green= 1.0f;
        this.blue= 0.0f;
    }
    
    public void setColor(float red,float green, float blue)
    {
        this.red = red;
        this.green= green;
        this.blue = blue;
    }
    
    public boolean addVec(Vec3d vec, EnumDirectionMode dir, Vec3d centerVec)
    {
//        double diff = Math.abs(centerVec.z)-Math.abs(vec.z);
//        if(dir == EnumDirectionMode.XY)
//            diff = Math.abs(centerVec.y)-Math.abs(vec.y);
        //correction so that it also works when drawing arround the centerlines of the world (z=0,X=0)
        double diff = centerVec.z-vec.z;
        if(dir == EnumDirectionMode.XY)
            diff = centerVec.y-vec.y;
        
        if(this.startVec == null)
        {
            this.startVec = vec;
            return false;
        }
        else
        {
            //if startVec already exists but you found a block that is closer to the middle ZY plane
            // use the new coordinate as start vec
//            double prevDiff = Math.abs(centerVec.z)-Math.abs(this.startVec.z);
//            if(dir == EnumDirectionMode.XY )
//                prevDiff = Math.abs(centerVec.y)-Math.abs(this.startVec.y);
            //correction so that it also works when drawing arround the centerlines of the world (z=0,X=0)
            double prevDiff = centerVec.z-this.startVec.z;
            if(dir == EnumDirectionMode.XY )
                prevDiff = centerVec.y-this.startVec.y;
            
            if(prevDiff*diff >= 0)
            {
                if(Math.abs(prevDiff)< Math.abs(diff))
                {
                    this.startVec = vec;
                    return false;
                }
                return false;
            }
        }
        if(endVec == null)
        {
            endVec = vec;
            return true;
        }
        else
        {
        
//            double prevDiff = Math.abs(centerVec.z)-Math.abs(endVec.z);
//            if(dir == EnumDirectionMode.XY )
//                prevDiff = Math.abs(centerVec.y)-Math.abs(endVec.y);
            //correction so that it also works when drawing arround the centerlines of the world (z=0,X=0)
            double prevDiff = centerVec.z-endVec.z;
            if(dir == EnumDirectionMode.XY )
                prevDiff = centerVec.y-endVec.y;
            
            if(prevDiff*diff > 0)
            {
                if(Math.abs(prevDiff)< Math.abs(diff))
                {
                    endVec = vec;
                    return true;
                }
            }
        }
        return false;
    }
    
}