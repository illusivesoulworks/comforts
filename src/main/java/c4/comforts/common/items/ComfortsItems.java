/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.items;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsItems {

    @GameRegistry.ObjectHolder("comforts:sleeping_bag")
    public static ItemSleepingBag SLEEPING_BAG;

    @GameRegistry.ObjectHolder("comforts:hammock")
    public static ItemHammock HAMMOCK;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        SLEEPING_BAG.initModel();
        HAMMOCK.initModel();
    }
}
