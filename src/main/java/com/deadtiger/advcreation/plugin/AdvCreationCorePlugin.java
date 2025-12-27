package com.deadtiger.advcreation.plugin;

import com.deadtiger.advcreation.utility.LogHelper;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class AdvCreationCorePlugin implements IFMLLoadingPlugin
{
    public AdvCreationCorePlugin() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.advcreation.json");
    }

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
        return null;
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
