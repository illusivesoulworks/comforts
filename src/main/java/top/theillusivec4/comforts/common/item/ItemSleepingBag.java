/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsConfig;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

import javax.annotation.Nonnull;

public class ItemSleepingBag extends ItemComfortsBase {

    public ItemSleepingBag(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        EnumActionResult result = super.onItemUse(context);
        EntityPlayer player = context.getPlayer();

        if (player != null && result == EnumActionResult.SUCCESS && ComfortsConfig.SERVER.autoUse.get() && !player.isSneaking()) {
            BlockPos pos = context.getPos().up();
            World world = context.getWorld();
            IBlockState state = world.getBlockState(pos);

            if (state.get(BlockBed.PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(BlockHorizontal.HORIZONTAL_FACING));
            }
            final BlockPos blockpos = pos;
            CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
                sleepdata.setSleeping(true);
                sleepdata.setSleepingPos(blockpos);
            });
            return EnumActionResult.SUCCESS;
        }
        return result;
    }
}
