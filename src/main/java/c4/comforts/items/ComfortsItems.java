package c4.comforts.items;

import c4.comforts.items.ItemSleepingBag;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsItems {

    @GameRegistry.ObjectHolder("comforts:sleeping_bag")
    public static ItemSleepingBag sleepingBag;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        sleepingBag.initModel();
    }
}
