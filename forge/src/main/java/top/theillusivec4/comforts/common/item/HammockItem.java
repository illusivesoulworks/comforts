/*
 * Copyright (c) 2017-2020 C4
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

import static top.theillusivec4.comforts.common.block.RopeAndNailBlock.HORIZONTAL_FACING;
import static top.theillusivec4.comforts.common.block.RopeAndNailBlock.SUPPORTING;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;

public class HammockItem extends ComfortsBaseItem {

  public HammockItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getPos();
    final BlockState state = world.getBlockState(pos);

    if (state.getBlock() instanceof RopeAndNailBlock) {
      final Direction direction = state.get(HORIZONTAL_FACING);
      final BlockPos blockpos = pos.offset(direction, 3);
      final BlockState blockstate = world.getBlockState(blockpos);

      if (hasPartneredRopes(state, blockstate)) {
        ActionResultType result = this.tryPlace(BlockItemUseContext
            .func_221536_a(new BlockItemUseContext(context), context.getPos().offset(direction),
                direction));

        if (result.isSuccessOrConsume()) {
          world.setBlockState(pos, state.with(SUPPORTING, true));
          world.setBlockState(blockpos, blockstate.with(SUPPORTING, true));
        }
        return result;
      }
    }
    return ActionResultType.FAIL;
  }

  private boolean hasPartneredRopes(BlockState state, BlockState otherState) {
    return otherState.getBlock() instanceof RopeAndNailBlock
        && otherState.get(HORIZONTAL_FACING) == state.get(HORIZONTAL_FACING).getOpposite() && !state
        .get(SUPPORTING) && !otherState.get(SUPPORTING);
  }
}
