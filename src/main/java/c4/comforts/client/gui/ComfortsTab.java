/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.client.gui;

import c4.comforts.Comforts;
import c4.comforts.common.items.ComfortsItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ComfortsTab extends CreativeTabs {

    public ComfortsTab() {
        super(Comforts.MODID);
    }

    @Nonnull
    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ComfortsItems.SLEEPING_BAG, 1, 14);
    }
}
