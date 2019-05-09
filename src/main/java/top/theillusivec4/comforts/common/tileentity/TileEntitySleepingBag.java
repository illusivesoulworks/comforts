package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.item.EnumDyeColor;
import top.theillusivec4.comforts.common.init.ComfortsTileEntities;

public class TileEntitySleepingBag extends TileEntityComfortsBase {

    public TileEntitySleepingBag() {
        super(ComfortsTileEntities.SLEEPING_BAG_TE);
    }

    public TileEntitySleepingBag(EnumDyeColor colorIn) {
        super(ComfortsTileEntities.SLEEPING_BAG_TE, colorIn);
    }
}
