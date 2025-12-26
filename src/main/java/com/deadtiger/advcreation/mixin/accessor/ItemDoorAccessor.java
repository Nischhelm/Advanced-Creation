package com.deadtiger.advcreation.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemDoor.class)
public interface ItemDoorAccessor {
    @Accessor("block")
    Block getBlock();
}
