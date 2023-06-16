/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.common.item;

import com.illusivesoulworks.comforts.common.block.RopeAndNailBlock;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HammockItem extends BaseComfortsItem {

  public HammockItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext context) {
    final Level level = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    final BlockState state = level.getBlockState(pos);
    final Player player = context.getPlayer();

    if (state.getBlock() instanceof RopeAndNailBlock) {
      final Direction direction = state.getValue(RopeAndNailBlock.HORIZONTAL_FACING);
      final BlockPos blockpos = pos.relative(direction, 3);
      final BlockState blockstate = level.getBlockState(blockpos);

      if (hasPartneredRopes(state, blockstate)) {
        InteractionResult result = this.place(BlockPlaceContext
            .at(new BlockPlaceContext(context), context.getClickedPos().relative(direction),
                direction));

        if (result.consumesAction()) {
          level.setBlockAndUpdate(pos, state.setValue(RopeAndNailBlock.SUPPORTING, true));
          level.setBlockAndUpdate(blockpos, blockstate.setValue(RopeAndNailBlock.SUPPORTING, true));
        } else {

          if (player != null) {
            player.displayClientMessage(
                Component.translatable("block.comforts.hammock.no_space"), true);
          }
        }
        return result;
      } else if (player != null) {
        boolean flag = hasPartneredRopes(state, level.getBlockState(pos.relative(direction, 1)));
        flag = flag || hasPartneredRopes(state, level.getBlockState(pos.relative(direction, 2)));

        if (flag) {
          player.displayClientMessage(
              Component.translatable("block.comforts.hammock.no_space"), true);
        } else {
          player.displayClientMessage(
              Component.translatable("block.comforts.hammock.missing_rope"), true);
        }
      }
    } else if (player != null) {
      player.displayClientMessage(Component.translatable("block.comforts.hammock.no_rope"),
          true);
    }
    return InteractionResult.FAIL;
  }

  private boolean hasPartneredRopes(BlockState state, BlockState otherState) {
    return otherState.getBlock() instanceof RopeAndNailBlock &&
        otherState.getValue(RopeAndNailBlock.HORIZONTAL_FACING) ==
            state.getValue(RopeAndNailBlock.HORIZONTAL_FACING).getOpposite() &&
        !state.getValue(RopeAndNailBlock.SUPPORTING) &&
        !otherState.getValue(RopeAndNailBlock.SUPPORTING);
  }

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                              @Nonnull List<Component> components,
                              @Nonnull TooltipFlag flag) {
    components.add(Component.translatable("item.comforts.hammock.placement.tooltip",
            Component.translatable("block.comforts.rope_and_nail").withStyle(ChatFormatting.YELLOW))
        .withStyle(ChatFormatting.GRAY));
  }
}
