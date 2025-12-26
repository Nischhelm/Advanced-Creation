package com.deadtiger.advcreation.build_mode.utility;

import com.deadtiger.advcreation.template.TemplateBlock;

import java.util.ArrayList;

public class ExtremaXYZ
{
    public int maxX, maxY, maxZ;
    public int minX, minY, minZ;

    public ExtremaXYZ()
    {
        this.maxX = 0;
        this.maxY = 0;
        this.maxZ = 0;
        this.minX = 0;
        this.minY = 0;
        this.minZ = 0;
    }

    public ExtremaXYZ(ArrayList<TemplateBlock> blockList)
    {
        this.maxX = -10000;
        this.maxY = -10000;
        this.maxZ = -10000;
        this.minX = 10000;
        this.minY = 10000;
        this.minZ = 10000;
        calculateExtremas(blockList);
    }

    private void calculateExtremas(ArrayList<TemplateBlock> blockList)
    {
        for (TemplateBlock block : blockList)
        {
            this.maxX = Math.max(this.maxX, block.getX_offset());
            this.maxY = Math.max(this.maxY, block.getY_offset());
            this.maxZ = Math.max(this.maxZ, block.getZ_offset());

            this.minX = Math.min(this.minX, block.getX_offset());
            this.minY = Math.min(this.minY, block.getY_offset());
            this.minZ = Math.min(this.minZ, block.getZ_offset());

        }
    }


}
