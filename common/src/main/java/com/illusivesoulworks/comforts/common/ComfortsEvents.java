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

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ComfortsEvents {

  public static boolean canSetSpawn(Player player, BlockPos pos) {
    final Level level = player.getLevel();

    if (pos != null && !player.getLevel().isClientSide) {
      final Block block = level.getBlockState(pos).getBlock();

      return !(block instanceof SleepingBagBlock) && !(block instanceof HammockBlock);
    }
    return true;
  }

  public static Result checkTime(Level level, BlockPos pos) {
    final long time = level.getDayTime() % 24000L;

    if (level.getBlockState(pos).getBlock() instanceof HammockBlock) {

      if (time > 500L && time < 11500L) {
        return Result.ALLOW;
      } else {

        if (ComfortsConfig.SERVER.nightHammocks.get()) {
          return Result.DEFAULT;
        } else {
          return Result.DENY;
        }
      }
    }
    return Result.DEFAULT;
  }

  public static long getWakeTime(ServerLevel level, long currentTime) {
    final boolean[] activeHammock = {false};
    List<? extends Player> players = level.players();

    for (Player player : players) {
      player.getSleepingPos().ifPresent(bedPos -> {
        if (player.isSleepingLongEnough() && level.getBlockState(bedPos)
            .getBlock() instanceof HammockBlock) {
          activeHammock[0] = true;
        }
      });

      if (activeHammock[0]) {
        break;
      }
    }

    if (activeHammock[0] && level.getLevel().isDay()) {
      final long i = level.getDayTime() + 24000L;
      return (i - i % 24000L) - 12001L;
    }
    return currentTime;
  }

  static final List<MobEffectInstance> SLEEPING_BAG_EFFECTS = new ArrayList<>();

  public static void onWakeUp(Player player) {
    Level level = player.getLevel();

    if (!level.isClientSide) {
      Services.SLEEP_EVENTS.getSleepData(player)
          .ifPresent(data -> player.getSleepingPos().ifPresent(bedPos -> {
            final long wakeTime = level.getDayTime();
            final long timeSlept = wakeTime - data.getSleepTime();
            final BlockState state = level.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {
                List<MobEffectInstance> effectInstances = SLEEPING_BAG_EFFECTS;

                if (!effectInstances.isEmpty()) {

                  for (MobEffectInstance effect : effectInstances) {
                    player.addEffect(
                        new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                            effect.getAmplifier()));
                  }
                }

                if (level.random.nextDouble() < ComfortsConfig.SERVER.sleepingBagBreakage.get()) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                  level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                  level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 35);
                  player.displayClientMessage(
                      Component.translatable("block.comforts.sleeping_bag.broke"), true);
                  level.playSound(null, bedPos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS,
                      1.0F, 1.0F);
                  player.clearSleepingPos();
                }
              }

              if (!broke && data.getAutoSleepPos() != null) {
                final BlockPos blockpos = bedPos
                    .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 35);
                player.clearSleepingPos();
              }
            }
            data.setWakeTime(wakeTime);
            data.setTiredTime(
                wakeTime + (long) (timeSlept / ComfortsConfig.SERVER.sleepyFactor.get()));
            data.setAutoSleepPos(null);
          }));
    }
  }

  public static Player.BedSleepingProblem onSleep(Player player) {

    if (!player.getLevel().isClientSide()) {
      return Services.SLEEP_EVENTS.getSleepData(player).map(data -> {
        final long dayTime = player.getCommandSenderWorld().getDayTime();
        data.setSleepTime(dayTime);

        if (ComfortsConfig.SERVER.wellRested.get()) {
          if (data.getWakeTime() > dayTime) {
            data.setWakeTime(0);
            data.setTiredTime(0);
          }

          if (data.getTiredTime() > dayTime) {
            player.displayClientMessage(Component.translatable("capability.comforts.not_sleepy"),
                true);
            return Player.BedSleepingProblem.OTHER_PROBLEM;
          }
        }
        return null;
      }).orElse(null);
    }
    return null;
  }

  public enum Result {
    ALLOW,
    DEFAULT,
    DENY
  }
}
