/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class ComfortsLoadingPlugin implements IFMLLoadingPlugin {

    public static boolean runtimeDeobf = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{SleepTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}