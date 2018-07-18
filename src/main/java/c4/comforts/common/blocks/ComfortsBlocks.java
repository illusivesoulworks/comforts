/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

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
    }
}
