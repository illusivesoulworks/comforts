/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
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
