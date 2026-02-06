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
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

    if (!(state.getBlock() instanceof RopeAndNailBlock)) {
        if (player != null) {
            player.displayClientMessage(Component.translatable("item.comforts.hammock.no_rope"), true);
        }
        return InteractionResult.FAIL;
    }

    final Direction direction = state.getValue(RopeAndNailBlock.HORIZONTAL_FACING);

    // One unified scan
    final int partnerDist = findNearestPartnerRope(level, state, pos, direction, 1, 12);

    if (partnerDist == 3) {
        final BlockPos partnerPos = pos.relative(direction, 3);
        final BlockState partnerState = level.getBlockState(partnerPos);

        InteractionResult result = this.place(
                BlockPlaceContext.at(new BlockPlaceContext(context), pos, direction)
        );

        if (result.consumesAction()) {
            level.setBlockAndUpdate(pos, state.setValue(RopeAndNailBlock.SUPPORTING, true));
            level.setBlockAndUpdate(partnerPos, partnerState.setValue(RopeAndNailBlock.SUPPORTING, true));
        } else if (player != null) {
            player.displayClientMessage(Component.translatable("item.comforts.hammock.no_space"), true);
        }
        return result;
    }

    if (player != null) {
        if (partnerDist == 1 || partnerDist == 2) {
            player.displayClientMessage(Component.translatable("item.comforts.hammock.no_space"), true);
        } else if (partnerDist > 3) {
            player.displayClientMessage(Component.translatable("item.comforts.hammock.ropes_too_far"), true);
        } else {
            player.displayClientMessage(Component.translatable("item.comforts.hammock.missing_rope"), true);
        }
    }

    return InteractionResult.FAIL;
  }

  private int findNearestPartnerRope(Level level, BlockState startingState, BlockPos startingPos,
                                     Direction direction, int startingDistance, int maxDistance) {

    for (int i = startingDistance; i <= maxDistance; i++) {
      BlockPos pos = startingPos.relative(direction, i);
      BlockState state = level.getBlockState(pos);

      if (!state.isAir()) {

        if (hasPartneredRopes(startingState, state)) {
          return i;
        } else if (!state.canBeReplaced()) {
          break;
        }
      }
    }
    return -1;
  }

  private boolean hasPartneredRopes(BlockState state, BlockState otherState) {
    return otherState.getBlock() instanceof RopeAndNailBlock &&
        otherState.getValue(RopeAndNailBlock.HORIZONTAL_FACING) ==
            state.getValue(RopeAndNailBlock.HORIZONTAL_FACING).getOpposite() &&
        !state.getValue(RopeAndNailBlock.SUPPORTING) &&
        !otherState.getValue(RopeAndNailBlock.SUPPORTING);
  }

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context,
                              @Nonnull TooltipDisplay tooltipDisplay,
                              @Nonnull Consumer<Component> consumer, @Nonnull TooltipFlag flag) {
    consumer.accept(Component.translatable("item.comforts.hammock.placement.tooltip",
                                           Component.translatable("item.comforts.rope_and_nail")
                                               .withStyle(ChatFormatting.YELLOW))
                        .withStyle(ChatFormatting.GRAY));
  }
}
