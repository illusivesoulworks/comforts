package top.theillusivec4.comforts.common.init;

import net.minecraft.tileentity.TileEntityType;
import top.theillusivec4.comforts.common.tileentity.TileEntityHammock;
import top.theillusivec4.comforts.common.tileentity.TileEntitySleepingBag;

public class ComfortsTileEntities {

    public static final TileEntityType<TileEntitySleepingBag> SLEEPING_BAG_TE;

    public static final TileEntityType<TileEntityHammock> HAMMOCK_TE;

    static {
        SLEEPING_BAG_TE = TileEntityType.Builder.create(TileEntitySleepingBag::new).build(null);
        SLEEPING_BAG_TE.setRegistryName("sleeping_bag");

        HAMMOCK_TE = TileEntityType.Builder.create(TileEntityHammock::new).build(null);
        HAMMOCK_TE.setRegistryName("hammock");
    }
}
