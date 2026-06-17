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
import com.mojang.datafixers.util.Either;
import java.util.function.Consumer;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

public class HammockItem extends BaseComfortsItem {

  public HammockItem(Block block) {
    super(block);
  }

  @Override
  public @NonNull InteractionResult useOn(UseOnContext context) {
    final Player player = context.getPlayer();
    Either<InteractionResult, HammockErrorState> placementResult = hangHammock(context);
    return placementResult.map(interactionResult -> interactionResult, error -> {

      if (player != null) {
        player.sendSystemMessage(
            Component.translatable("item.comforts.hammock." + error.key));
      }
      return InteractionResult.FAIL;
    });
  }

  private Either<InteractionResult, HammockErrorState> hangHammock(UseOnContext context) {
    final Level level = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    final BlockState state = level.getBlockState(pos);
    HammockErrorState error = HammockErrorState.NO_ROPE;

    if (state.getBlock() instanceof RopeAndNailBlock) {
      final Direction direction = state.getValue(RopeAndNailBlock.HORIZONTAL_FACING);
      int nearestRope = findNearestPartnerRope(level, state, pos, direction, 3, 3);
      boolean hasPartner = nearestRope == 3;

      // Checking for correct rope location and orientation
      if (hasPartner) {
        InteractionResult result = this.place(
            BlockPlaceContext.at(new BlockPlaceContext(context),
                context.getClickedPos().relative(direction),
                direction));

        // Checking that the hammock is placeable between the ropes
        if (result.consumesAction()) {
          final BlockPos blockpos = pos.relative(direction, 3);
          final BlockState blockstate = level.getBlockState(blockpos);
          level.setBlockAndUpdate(pos, state.setValue(RopeAndNailBlock.SUPPORTING, true));
          level.setBlockAndUpdate(blockpos, blockstate.setValue(RopeAndNailBlock.SUPPORTING, true));
          return Either.left(result);
        } else {
          // If the hammock cannot be placed, check for obstacles
          nearestRope = findNearestPartnerRope(level, state, pos, direction, 1, 2);

          // Found a partner candidate that is too close
          if (nearestRope > 0) {
            error = HammockErrorState.NO_SPACE;
          } else { // Found an obstacle
            error = HammockErrorState.NO_PARTNERED_ROPE;
          }
        }
      } else {
        // If a partner candidate isn't found, scan inward distance first
        nearestRope = findNearestPartnerRope(level, state, pos, direction, 1, 2);

        if (nearestRope == -1) {
          // If a partner candidate still isn't found, scan outward distance
          nearestRope = findNearestPartnerRope(level, state, pos, direction, 3, 12);
        }

        if (nearestRope == 0) {
          // Encountered solid block/obstacle between potential candidates
          error = HammockErrorState.NO_PARTNERED_ROPE;
        } else if (nearestRope > 3) {
          error = HammockErrorState.TOO_FAR;
        } else if (nearestRope > 0) {
          error = HammockErrorState.NO_SPACE;
        } else {
          // No candidates found within a reasonable distance
          error = HammockErrorState.NO_PARTNERED_ROPE;
        }
      }
    }
    return Either.right(error);
  }

  private int findNearestPartnerRope(Level level, BlockState startingState,
                                     BlockPos startingPos, Direction direction,
                                     int startingDistance, int maxDistance) {

    for (int i = Math.max(1, startingDistance); i <= maxDistance; i++) {
      BlockPos pos = startingPos.relative(direction, i);
      BlockState state = level.getBlockState(pos);

      if (!state.isAir()) {

        if (hasPartneredRopes(startingState, state)) {
          return i;
        } else if (!state.canBeReplaced()) {
          return 0;
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
  public @NullMarked void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                          TooltipDisplay tooltipDisplay,
                                          Consumer<Component> consumer,
                                          TooltipFlag flag) {
    consumer.accept(Component.translatable("item.comforts.hammock.placement.tooltip",
            Component.translatable("item.comforts.rope_and_nail")
                .withStyle(ChatFormatting.YELLOW))
        .withStyle(ChatFormatting.GRAY));
  }

  private enum HammockErrorState {
    TOO_FAR("ropes_too_far"),
    NO_SPACE("no_space"),
    NO_PARTNERED_ROPE("missing_rope"),
    NO_ROPE("no_rope");

    final String key;

    HammockErrorState(String key) {
      this.key = key;
    }
  }
}
