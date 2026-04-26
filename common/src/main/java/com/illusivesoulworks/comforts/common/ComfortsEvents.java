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
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.mixin.AccessorPlayer;
import com.illusivesoulworks.comforts.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

public class ComfortsEvents {

  public static ComfortsConstants.Result canSleep(Level level, BlockPos pos) {
    return BaseComfortsBlock.canSleep(level, pos);
  }

  public static void resetSleepCounter(Player player) {
    player.getSleepingPos().ifPresent(bedPos -> {
      Block block = player.level().getBlockState(bedPos).getBlock();

      if (block instanceof BaseComfortsBlock comfortsBlock) {
        ComfortsConstants.TimeUse timeUse = comfortsBlock.getComfortsTimeUse();

        if (timeUse == ComfortsConstants.TimeUse.USE_WITHOUT_SLEEPING) {
          ((AccessorPlayer) player).setSleepCounter(0);
        }
      }
    });
  }

  public static long getWakeTime(ServerLevel level, long currentTime, long newTime) {
    final boolean[] daySleeping = {false};
    List<? extends Player> players = level.players();

    for (Player player : players) {
      player.getSleepingPos().ifPresent(bedPos -> {
        if (player.isSleepingLongEnough()) {
          ComfortsConstants.TimeUse timeUse = ComfortsConstants.TimeUse.NIGHT;
          Block block = level.getBlockState(bedPos).getBlock();

          if (block instanceof HammockBlock) {
            timeUse = ComfortsConfig.SERVER.hammockUse.get();
          } else if (block instanceof SleepingBagBlock) {
            timeUse = ComfortsConfig.SERVER.sleepingBagUse.get();
          }

          if (timeUse == ComfortsConstants.TimeUse.DAY ||
              timeUse == ComfortsConstants.TimeUse.DAY_OR_NIGHT) {
            daySleeping[0] = true;
          }
        }
      });

      if (daySleeping[0]) {
        break;
      }
    }

    if (daySleeping[0] && level.getLevel().isBrightOutside()) {
      final long i = currentTime + 24000L;
      long result = (i - i % 24000L) - 12001L;
      return Math.max(ComfortsConfig.SERVER.nightWakeTimeOffset.get() + result, currentTime);
    }
    return Math.max(newTime + ComfortsConfig.SERVER.dayWakeTimeOffset.get(), currentTime);
  }

  private static final List<MobEffectInstance> SLEEPING_BAG_EFFECTS = new ArrayList<>();
  static boolean effectsInitialized = false;

  public static void onWakeUp(Player player) {
    Level level = player.level();

    if (!level.isClientSide()) {
      Services.SLEEP_EVENTS.getSleepData(player)
          .ifPresent(data -> player.getSleepingPos().ifPresent(bedPos -> {
            final long wakeTime = level.getDefaultClockTime();
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

                if (level.getRandom().nextDouble() < breakChance) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                  level.removeBlock(bedPos, false);
                  level.removeBlock(blockpos, false);
                  player.sendOverlayMessage(
                      Component.translatable("item.comforts.sleeping_bag.broke"));
                  level.playSound(null, bedPos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS,
                                  1.0F, 1.0F);
                  player.clearSleepingPos();
                }
              }

              if (!broke && data.getAutoSleepPos() != null) {
                final BlockPos blockpos = bedPos
                    .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());

                if (!player.isCreative()) {
                  level.removeBlock(blockpos, false);
                  level.removeBlock(bedPos, false);
                } else {
                  level.removeBlock(bedPos, false);
                  level.removeBlock(blockpos, false);
                }
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
      Holder<MobEffect> mobEffect =
          Services.REGISTRY_UTIL.getMobEffect(Identifier.tryParse(elements[0]));

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
        final long dayTime = player.level().getDefaultClockTime();
        data.setSleepTime(dayTime);

        if (ComfortsConfig.SERVER.restrictSleeping.get()) {
          if (data.getWakeTime() > dayTime) {
            data.setWakeTime(0);
            data.setTiredTime(0);
          }

          if (data.getTiredTime() > dayTime) {
            player.sendOverlayMessage(Component.translatable("capability.comforts.not_sleepy"));
            return Player.BedSleepingProblem.OTHER_PROBLEM;
          }
        }
        return null;
      }).orElse(null);
    }
    return null;
  }

  public static int sleepersNeeded(int activePlayers) {
    int percentage = ComfortsConfig.SERVER.daySleepingPercentage.get();

    if (percentage < 0) {
      return 0;
    }
    return Math.max(1, Mth.ceil((float) (activePlayers * percentage) / 100.0F));
  }

  public static boolean announceSleepStatus(SleepStatus sleepStatus, ServerLevel serverLevel) {
    MinecraftServer server = serverLevel.getServer();

    if (!serverLevel.isBrightOutside()) {
      return false;
    }

    if (!server.isSingleplayer() || server.isPublished()) {
      int percentage = ComfortsConfig.SERVER.daySleepingPercentage.get();

      if (percentage < 0) {
        percentage = serverLevel.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
      }

      if (percentage > 100) {
        return false;
      }
      Component component;

      if (sleepStatus.areEnoughSleeping(percentage)) {
        component = Component.translatable("comforts.skipping_day");
      } else {
        component = Component.translatable("sleep.players_sleeping", sleepStatus.amountSleeping(),
                                           sleepStatus.sleepersNeeded(percentage));
      }

      for (ServerPlayer player : serverLevel.players()) {
        player.sendOverlayMessage(component);
      }
      return true;
    }
    return false;
  }
}
