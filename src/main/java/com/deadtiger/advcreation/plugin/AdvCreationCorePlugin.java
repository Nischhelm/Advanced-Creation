package com.deadtiger.advcreation.plugin;

import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.utility.LogHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(Reference.MC_VERSION)
@IFMLLoadingPlugin.TransformerExclusions({"com.deadtiger.advcreation.plugin"})
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE+2)
public class AdvCreationCorePlugin implements IFMLLoadingPlugin
{
    public static final boolean isClient = FMLLaunchHandler.side().isClient();

    @Override
    public String[] getASMTransformerClass() {

        LogHelper.info("getting ASM TransformerClass CLIENT");
        return new String[]{
//                "com.deadtiger.advcreation.plugin.transformer.ShowMouseClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.IsometricViewClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.IsometricMovementClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.ExtendPlayerReachClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.MouseOverSelectionClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.PlayerRotationClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.CutThroughClassTransformer",
//                "com.deadtiger.advcreation.plugin.transformer.DeactivateScrollInventoryClassTransformer",
                "com.deadtiger.advcreation.plugin.transformer.GeneralTransformer"
        };

    }
    
    @Override
    public String getModContainerClass() {
        return "com.deadtiger.advcreation.plugin.AdvCreationCoreModContainer";
        //return null;
    }
    
    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
    
}
