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

import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.platform.Services;
import com.mojang.datafixers.util.Either;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class SleepingBagItem extends BaseComfortsItem {

  public SleepingBagItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public InteractionResult useOn(@Nonnull UseOnContext context) {
    boolean autoUse = ComfortsConfig.SERVER.autoUse.get();
    final Player player = context.getPlayer();

    if (!autoUse || (player != null && player.isCrouching())) {
      return super.useOn(context);
    } else {

      if (player instanceof ServerPlayer serverPlayer) {
        Either<Player.BedSleepingProblem, Unit> result =
            BaseComfortsBlock.trySleep(serverPlayer, context.getClickedPos().above(), true);
        return result.map(bedSleepingProblem -> {
          final Component text = switch (bedSleepingProblem) {
            case NOT_POSSIBLE_NOW -> ComfortsConfig.SERVER.sleepingBagUse.get().getMessage();
            case TOO_FAR_AWAY -> Component.translatable("block.comforts.sleeping_bag.too_far_away");
            default -> bedSleepingProblem.getMessage();
          };

          if (text != null) {
            player.displayClientMessage(text, true);
          }
          return InteractionResult.FAIL;
        }, unit -> {
          final InteractionResult interactionResult = super.useOn(context);

          if (interactionResult.consumesAction()) {
            Services.SLEEP_EVENTS.sendPlaceBagPacket(serverPlayer, context);
            final BlockPos pos = context.getClickedPos().above();
            Services.SLEEP_EVENTS.getSleepData(player).ifPresent(data -> data.setAutoSleepPos(pos));
            Services.SLEEP_EVENTS.sendAutoSleepPacket(serverPlayer, pos);
          }
          return interactionResult;
        });
      }
      return InteractionResult.CONSUME;
    }
  }

  public void syncedUseOn(@Nonnull UseOnContext useOnContext) {
    super.useOn(useOnContext);
  }

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                              @Nonnull List<Component> components,
                              @Nonnull TooltipFlag flag) {

    if (ComfortsConfig.SERVER.autoUse.get()) {
      components.add(Component.translatable("item.comforts.sleeping_bag.auto_use.tooltip")
          .withStyle(ChatFormatting.GRAY));
    }
  }
}
