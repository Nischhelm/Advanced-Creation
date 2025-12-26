package com.deadtiger.advcreation.client.gui.gui_screen.foundation_block_inventory_screen;

import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class FoundationBlockInventoryScreen extends GuiContainerCreative
{
    public FoundationBlockInventoryScreen(EntityPlayer player)
    {
        super(player);
    }

    @Override
    protected void handleMouseClick(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        if (slotIn != null)
        {
            if (slotIn.getStack().getItem() instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock) slotIn.getStack().getItem();
                int meta = itemBlock.getMetadata(slotIn.getStack().getMetadata());
                PlaceTemplateMode.FOUNDATION_BLOCKSTATE = itemBlock.getBlock().getStateForPlacement(mc.world,
                        BlockPos.ORIGIN, EnumFacing.NORTH, 0, 0, 0, meta, mc.player, EnumHand.MAIN_HAND);
                Minecraft.getMinecraft().displayGuiScreen(null);
                return;
            }

        }

        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
