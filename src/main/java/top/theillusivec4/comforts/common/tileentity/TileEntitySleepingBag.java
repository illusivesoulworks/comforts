package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;
import top.theillusivec4.comforts.common.init.ComfortsTileEntities;

public class TileEntitySleepingBag extends TileEntityComfortsBase {

    public TileEntitySleepingBag() {
        super(ComfortsTileEntities.SLEEPING_BAG_TE);
    }

    public TileEntitySleepingBag(EnumDyeColor colorIn) {
        super(ComfortsTileEntities.SLEEPING_BAG_TE, colorIn);
    }
}
