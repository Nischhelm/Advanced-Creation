package com.deadtiger.advcreation.utility;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static com.deadtiger.advcreation.utility.PlacementHelper.parseVec;

public class VecTransformer
{

    public static Vec3d transformHitVecToZYPlane(Vec3d hitVec, Vec3d planeVec, CursorVector traceVec)
    {
        AxisAlignedBB selectionBox = new AxisAlignedBB(planeVec.x,planeVec.y- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.z- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.x,planeVec.y+ BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.z+ BuildMode.DIR_SELECTION_PLANE_SIZE);

        RayTraceResult raytraceresult = null;
        if(selectionBox == null)
            System.out.println("selectionBox == null This should never happen");
        else if(traceVec == null)
            System.out.println("traceVec  == null This should never happen");
        else if(traceVec.start == null || traceVec.end == null)
            System.out.println("traceVec.start: "+ traceVec.start + " == null || traceVec.end:" + traceVec.end + " == null This should never happen");
        else
            raytraceresult = selectionBox.calculateIntercept(traceVec.start, traceVec.end);

        if(raytraceresult != null && raytraceresult.hitVec != null)
            return raytraceresult.hitVec;
        else
            return new Vec3d(planeVec.x,hitVec.y,hitVec.z);

    }

    public static Vec3d transformHitVecToXZPlane(Vec3d hitVec, Vec3d planeVec, CursorVector traceVec)
    {
        AxisAlignedBB selectionBox = new AxisAlignedBB(planeVec.x- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.y,planeVec.z- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.x+ BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.y,planeVec.z+ BuildMode.DIR_SELECTION_PLANE_SIZE);
        RayTraceResult raytraceresult = null;
        if(selectionBox == null)
            System.out.println("selectionBox == null This should never happen");
        else if(traceVec == null)
            System.out.println("traceVec  == null This should never happen");
        else if(traceVec.start == null || traceVec.end == null)
            System.out.println("traceVec.start: "+ traceVec.start + " == null || traceVec.end:" + traceVec.end + " == null This should never happen");
        else
            raytraceresult =selectionBox.calculateIntercept(traceVec.start, traceVec.end);

        if(raytraceresult != null && raytraceresult.hitVec != null)
            return raytraceresult.hitVec;
        else
            return new Vec3d(hitVec.x,planeVec.y,hitVec.z);
    }

    public static Vec3d transformHitVecToXYPlane(Vec3d hitVec, Vec3d planeVec, CursorVector traceVec) {
        AxisAlignedBB selectionBox = new AxisAlignedBB(planeVec.x- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.y- BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.z,planeVec.x+ BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.y+ BuildMode.DIR_SELECTION_PLANE_SIZE,planeVec.z);
        RayTraceResult raytraceresult = null;
        if(selectionBox == null)
            System.out.println("selectionBox == null This should never happen");
        else if(traceVec == null)
            System.out.println("traceVec  == null This should never happen");
        else if(traceVec.start == null || traceVec.end == null)
            System.out.println("traceVec.start: "+ traceVec.start + " == null || traceVec.end:" + traceVec.end + " == null This should never happen");
        else
            raytraceresult = selectionBox.calculateIntercept(traceVec.start, traceVec.end);

        if(raytraceresult != null && raytraceresult.hitVec != null )
            return raytraceresult.hitVec;
        else
            return new Vec3d(hitVec.x,hitVec.y,planeVec.z);
    }

    public static Vec3d transformXZorZYBasedOnCamera(Vec3d hitVec, Vec3d cameraUnitVec, CursorVector cursorVector)
    {
        Vec3d newVec;
        if (Math.abs(cameraUnitVec.y) > Math.abs(cameraUnitVec.x))
            newVec = transformHitVecToXZPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        else
            newVec = transformHitVecToZYPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        return newVec;
    }

    public static Vec3d transformXZorXYBasedOnCamera(Vec3d hitVec, Vec3d cameraUnitVec, CursorVector cursorVector)
    {
        Vec3d newVec;
        if (Math.abs(cameraUnitVec.y) > Math.abs(cameraUnitVec.z))
            newVec = transformHitVecToXZPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        else
            newVec = transformHitVecToXYPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        return newVec;
    }

    public static Vec3d transformXYOrZYBasedOnCamera(Vec3d hitVec, Vec3d cameraUnitVec, CursorVector cursorVector)
    {
        Vec3d newVec;
        if (Math.abs(cameraUnitVec.x) > Math.abs(cameraUnitVec.z))
            newVec = transformHitVecToZYPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        else
            newVec = transformHitVecToXYPlane(hitVec, BuildTemplateMode.SELECTED_VEC, cursorVector);
        return newVec;
    }

    public static Vec3d transformStartVecToDirectionMode(EnumDirectionMode dir, Vec3d hitVec, Vec3d cameraUnitVec, CursorVector cursorVector) {
        Vec3d endVec = hitVec;
        if(cameraUnitVec != null && hitVec != null)
        {
            //allow different coordinates to be non-zero dependend on the workDirectionMode
//            if(WORK_DIRECTION_MODE == EnumDirectionMode.FREE || WORK_DIRECTION_MODE == EnumDirectionMode.GROUND )
//                END_VEC = hitVec;
            if(dir == EnumDirectionMode.XY)
            {
                if(BuildMode.isUsingAbsCoord())
                    endVec = new Vec3d(hitVec.x, hitVec.y, cameraUnitVec.z);
                else
                    endVec = VecTransformer.transformHitVecToXYPlane(hitVec, cameraUnitVec,cursorVector);
            }
            else if(dir == EnumDirectionMode.ZY)
            {
                if (BuildMode.isUsingAbsCoord())
                    endVec = new Vec3d(cameraUnitVec.x, hitVec.y, hitVec.z);
                else
                    endVec = VecTransformer.transformHitVecToZYPlane(hitVec, cameraUnitVec, cursorVector);
            }
            else if(dir == EnumDirectionMode.XZ)
            {
                if (BuildMode.isUsingAbsCoord())
                    endVec = new Vec3d(hitVec.x, cameraUnitVec.y, hitVec.z);
                else
                    endVec = VecTransformer.transformHitVecToXZPlane(hitVec, cameraUnitVec, cursorVector);
            }
            else
                endVec = hitVec;

//            if(ConfigurationHandler.ALWAYS_SNAP_TO_BLOCK_CENTER.get())
//                endVec = parseVec(endVec);


        }
        return endVec;
    }
}
