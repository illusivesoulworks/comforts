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
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.BlockRopeAndNail;

import javax.annotation.Nonnull;

import static top.theillusivec4.comforts.common.block.BlockRopeAndNail.HORIZONTAL_FACING;
import static top.theillusivec4.comforts.common.block.BlockRopeAndNail.SUPPORTING;

public class ItemHammock extends ItemComfortsBase {

    public ItemHammock(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockRopeAndNail) {
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING), 3);
            IBlockState blockstate = world.getBlockState(blockpos);

            if (hasPartneredRopes(state, blockstate)) {
                EnumFacing facing = state.get(HORIZONTAL_FACING);
                EnumActionResult result = this.tryPlace(new BlockItemUseContext(context.getWorld(), context.getPlayer(), context.getItem(), context.getPos().offset(facing), facing, context.getHitX(), context.getHitY(), context.getHitZ()));

                if (result == EnumActionResult.SUCCESS) {
                    world.setBlockState(pos, state.with(SUPPORTING, true));
                    world.setBlockState(blockpos, blockstate.with(SUPPORTING, true));
                }
                return result;
            }
        }
        return EnumActionResult.FAIL;
    }

    private boolean hasPartneredRopes(IBlockState state, IBlockState blockstate) {
        return blockstate.getBlock() instanceof BlockRopeAndNail
                && blockstate.get(HORIZONTAL_FACING) == state.get(HORIZONTAL_FACING).getOpposite()
                && !state.get(SUPPORTING)
                && !blockstate.get(SUPPORTING);
    }
}
