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

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ComfortsEvents {

  public static boolean canSetSpawn(Player player, BlockPos pos) {
    final Level level = player.level();

    if (pos != null && !player.level().isClientSide()) {
      final Block block = level.getBlockState(pos).getBlock();

      return !(block instanceof SleepingBagBlock) && !(block instanceof HammockBlock);
    }
    return true;
  }

  public static Result checkTime(Level level, BlockPos pos) {
    final long time = level.getDayTime() % 24000L;
    ComfortsConfig.ComfortsTimeUse timeUse = ComfortsConfig.ComfortsTimeUse.NIGHT;
    Block block = level.getBlockState(pos).getBlock();

    if (block instanceof HammockBlock) {
      timeUse = ComfortsConfig.SERVER.hammockUse.get();
    } else if (block instanceof SleepingBagBlock) {
      timeUse = ComfortsConfig.SERVER.sleepingBagUse.get();
    }

    if (time > 500L && time < 11500L && (timeUse == ComfortsConfig.ComfortsTimeUse.DAY ||
        timeUse == ComfortsConfig.ComfortsTimeUse.DAY_OR_NIGHT)) {
      return Result.ALLOW;
    }

    if (timeUse == ComfortsConfig.ComfortsTimeUse.DAY_OR_NIGHT ||
        timeUse == ComfortsConfig.ComfortsTimeUse.NIGHT) {
      return Result.DEFAULT;
    }
    return Result.DENY;
  }

  public static long getWakeTime(ServerLevel level, long currentTime) {
    final boolean[] daySleeping = {false};
    List<? extends Player> players = level.players();

    for (Player player : players) {
      player.getSleepingPos().ifPresent(bedPos -> {
        if (player.isSleepingLongEnough()) {
          ComfortsConfig.ComfortsTimeUse timeUse = ComfortsConfig.ComfortsTimeUse.NIGHT;
          Block block = level.getBlockState(bedPos).getBlock();

          if (block instanceof HammockBlock) {
            timeUse = ComfortsConfig.SERVER.hammockUse.get();
          } else if (block instanceof SleepingBagBlock) {
            timeUse = ComfortsConfig.SERVER.sleepingBagUse.get();
          }

          if (timeUse == ComfortsConfig.ComfortsTimeUse.DAY ||
              timeUse == ComfortsConfig.ComfortsTimeUse.DAY_OR_NIGHT) {
            daySleeping[0] = true;
          }
        }
      });

      if (daySleeping[0]) {
        break;
      }
    }

    if (daySleeping[0] && level.getLevel().isDay()) {
      final long i = level.getDayTime() + 24000L;
      return (i - i % 24000L) - 12001L;
    }
    return currentTime;
  }

  private static final List<MobEffectInstance> SLEEPING_BAG_EFFECTS = new ArrayList<>();
  static boolean effectsInitialized = false;

  public static void onWakeUp(Player player) {
    Level level = player.level();

    if (!level.isClientSide) {
      Services.SLEEP_EVENTS.getSleepData(player)
          .ifPresent(data -> player.getSleepingPos().ifPresent(bedPos -> {
            final long wakeTime = level.getDayTime();
            final long timeSlept = wakeTime - data.getSleepTime();
            final BlockState state = level.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {

                if (!effectsInitialized) {
                  initializeEffects();
                  effectsInitialized = true;
                }
                List<MobEffectInstance> effectInstances = SLEEPING_BAG_EFFECTS;

                if (!effectInstances.isEmpty()) {

                  for (MobEffectInstance effect : effectInstances) {
                    player.addEffect(
                        new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                            effect.getAmplifier()));
                  }
                }
                double breakChance =
                    ((double) ComfortsConfig.SERVER.sleepingBagBreakChance.get()) / 100D;
                double luckMultiplier =
                    ComfortsConfig.SERVER.sleepingBagBreakChanceLuckMultiplier.get();

                if (luckMultiplier > 0.0d) {
                  AttributeInstance attributeInstance = player.getAttribute(Attributes.LUCK);

                  if (attributeInstance != null) {
                    breakChance -= luckMultiplier * attributeInstance.getValue();
                  }
                }

                if (level.random.nextDouble() < breakChance) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                  level.removeBlock(bedPos, false);
                  level.removeBlock(blockpos, false);
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
                level.removeBlock(blockpos, false);
                level.removeBlock(bedPos, false);
                player.clearSleepingPos();
              }
            }
            data.setWakeTime(wakeTime);
            data.setTiredTime(
                wakeTime + (long) (timeSlept / ComfortsConfig.SERVER.restMultiplier.get()));
            data.setAutoSleepPos(null);
          }));
    }
  }

  private static void initializeEffects() {
    SLEEPING_BAG_EFFECTS.clear();
    ComfortsConfig.SERVER.sleepingBagEffects.get().forEach(effect -> {
      String[] elements = effect.split(";");
      MobEffect mobEffect = Services.REGISTRY_UTIL.getMobEffect(new ResourceLocation(elements[0]));

      if (mobEffect == null) {
        return;
      }
      int duration = 0;
      int amp = 0;
      try {
        duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
        amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
      } catch (Exception e) {
        ComfortsConstants.LOG.error("Problem parsing sleeping bag effects in config!", e);
      }
      SLEEPING_BAG_EFFECTS.add(new MobEffectInstance(mobEffect, duration * 20, amp - 1));
    });
  }

  public static Player.BedSleepingProblem onSleep(Player player) {

    if (!player.level().isClientSide()) {
      return Services.SLEEP_EVENTS.getSleepData(player).map(data -> {
        final long dayTime = player.getCommandSenderWorld().getDayTime();
        data.setSleepTime(dayTime);

        if (ComfortsConfig.SERVER.restrictSleeping.get()) {
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
