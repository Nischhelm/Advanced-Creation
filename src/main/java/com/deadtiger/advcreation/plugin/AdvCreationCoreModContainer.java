package com.deadtiger.advcreation.plugin;

import com.deadtiger.advcreation.reference.Reference;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.ArrayList;

public class AdvCreationCoreModContainer extends DummyModContainer
{
    public AdvCreationCoreModContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId ="advcreationcore";
        meta.name = "AdvancedCreationCore";
        meta.description = "Core mods that belong to AdvancedCreation";
        meta.version = Reference.VERSION;
        meta.authorList = new ArrayList<String>();
        meta.authorList.add("ChryonicWolf");
    }
    
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        //bus.register(this);
        return true;
    }
}
