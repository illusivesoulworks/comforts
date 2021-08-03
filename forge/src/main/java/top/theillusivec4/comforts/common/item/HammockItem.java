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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;

public class HammockItem extends ComfortsBaseItem {

  public HammockItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext context) {
    final Level world = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    final BlockState state = world.getBlockState(pos);

    if (state.getBlock() instanceof RopeAndNailBlock) {
      final Direction direction = state.getValue(HORIZONTAL_FACING);
      final BlockPos blockpos = pos.relative(direction, 3);
      final BlockState blockstate = world.getBlockState(blockpos);

      if (hasPartneredRopes(state, blockstate)) {
        InteractionResult result = this.place(BlockPlaceContext
            .at(new BlockPlaceContext(context), context.getClickedPos().relative(direction),
                direction));

        if (result.consumesAction()) {
          world.setBlockAndUpdate(pos, state.setValue(SUPPORTING, true));
          world.setBlockAndUpdate(blockpos, blockstate.setValue(SUPPORTING, true));
        }
        return result;
      }
    }
    return InteractionResult.FAIL;
  }

  private boolean hasPartneredRopes(BlockState state, BlockState otherState) {
    return otherState.getBlock() instanceof RopeAndNailBlock
        && otherState.getValue(HORIZONTAL_FACING) == state.getValue(HORIZONTAL_FACING).getOpposite() && !state
        .getValue(SUPPORTING) && !otherState.getValue(SUPPORTING);
  }
}
