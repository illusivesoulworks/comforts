package c4.comforts.client.gui;

import c4.comforts.Comforts;
import c4.comforts.proxy.CommonProxy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Comforts.MODID, false, false, Comforts.MODNAME);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        for (Property prop : CommonProxy.config.getCategory("general").getOrderedValues()) {
            list.add(new ConfigElement(prop));
        }

        return list;
    }
}
