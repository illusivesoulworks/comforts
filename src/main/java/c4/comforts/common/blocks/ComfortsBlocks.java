package c4.comforts.common.blocks;

import c4.comforts.common.items.ComfortsItems;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsBlocks {

    public static BlockSleepingBag[] SLEEPING_BAGS;
    public static BlockHammock[] HAMMOCKS;

    @GameRegistry.ObjectHolder("comforts:rope")
    public static BlockRope ROPE;

    public static void preInit() {
        SLEEPING_BAGS = new BlockSleepingBag[16];
        HAMMOCKS = new BlockHammock[16];

        for(int i = 0; i < 16; i++) {
            EnumDyeColor color = EnumDyeColor.byMetadata(i);
            SLEEPING_BAGS[i] = new BlockSleepingBag(color);
            HAMMOCKS[i] = new BlockHammock(color);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {

        ROPE.initModel();

        for (BlockSleepingBag sleepingBag : SLEEPING_BAGS) {
            sleepingBag.initModel(ComfortsItems.SLEEPING_BAG);
        }

        for (BlockHammock hammock : HAMMOCKS) {
            hammock.initModel(ComfortsItems.HAMMOCK);
        }
    }
}
