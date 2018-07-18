/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
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
