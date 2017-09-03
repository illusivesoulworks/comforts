package c4.comforts.blocks;

import c4.comforts.blocks.BlockSleepingBag;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsBlocks {

    public static BlockSleepingBag[] sleepingBags;

    public static void preInit() {
        sleepingBags = new BlockSleepingBag[16];

        for(int i = 0; i < 16; i++) {
            EnumDyeColor color = EnumDyeColor.byMetadata(i);
            sleepingBags[i] = new BlockSleepingBag(color);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        for (BlockSleepingBag sleepingBag : sleepingBags) {
            sleepingBag.initModel();
        }
    }

}
