package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.item.EnumDyeColor;
import top.theillusivec4.comforts.common.init.ComfortsTileEntities;

public class TileEntityHammock extends TileEntityComfortsBase {

    public TileEntityHammock() {
        super(ComfortsTileEntities.HAMMOCK_TE);
    }

    public TileEntityHammock(EnumDyeColor colorIn) {
        super(ComfortsTileEntities.HAMMOCK_TE, colorIn);
    }
}
