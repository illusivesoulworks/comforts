/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.items;

import c4.comforts.Comforts;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBase extends Item {

    public ItemBase(String name) {
        this.setCreativeTab(ComfortsUtil.comfortsTab);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setRegistryName(name);
        this.setTranslationKey(Comforts.MODID + "." + name);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getTranslationKey();
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {

        if (this.isInCreativeTab(tab)) {

            for (int i = 0; i < 16; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {

        for (int i = 0; i < 16; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }
    }

    @SideOnly(Side.CLIENT)
    public IItemColor getColorFromItemstack() {
        return (stack, tintIndex) -> tintIndex == 1 ? ComfortsUtil.getColor(stack.getMetadata()) : 0xFFFFFF;
    }
}
