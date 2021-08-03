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

package top.theillusivec4.comforts.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

public class CommonEventHandler {

  @SubscribeEvent
  public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
    final Player player = evt.getPlayer();
    final Level world = player.getCommandSenderWorld();
    final BlockPos pos = evt.getNewSpawn();

    if (pos != null && !world.isClientSide) {
      Block block = world.getBlockState(pos).getBlock();

      if (block instanceof SleepingBagBlock || block instanceof HammockBlock) {
        evt.setCanceled(true);
      }
    }
  }

  @SubscribeEvent
  public void onSleepTimeCheck(SleepingTimeCheckEvent evt) {
    final Level world = evt.getPlayer().getCommandSenderWorld();
    final long worldTime = world.getDayTime() % 24000L;

    evt.getSleepingLocation().ifPresent(sleepingLocation -> {
      if (world.getBlockState(sleepingLocation).getBlock() instanceof HammockBlock) {

        if (worldTime > 500L && worldTime < 11500L) {
          evt.setResult(Event.Result.ALLOW);
        } else {

          if (ComfortsConfig.SERVER.nightHammocks.get()) {
            evt.setResult(Event.Result.DEFAULT);
          } else {
            evt.setResult(Event.Result.DENY);
          }
        }
      }
    });
  }

  @SubscribeEvent
  public void onSleepFinished(SleepFinishedTimeEvent evt) {
    LevelAccessor world = evt.getWorld();

    if (world instanceof ServerLevel) {
      ServerLevel serverWorld = (ServerLevel) world;
      final boolean[] activeHammock = {false};
      List<? extends Player> players = world.players();

      for (Player player : players) {
        player.getSleepingPos().ifPresent(bedPos -> {
          if (player.isSleepingLongEnough() && world.getBlockState(bedPos)
              .getBlock() instanceof HammockBlock) {
            activeHammock[0] = true;
          }
        });

        if (activeHammock[0]) {
          break;
        }
      }

      if (activeHammock[0] && ((ServerLevel) world).getLevel().isDay()) {
        final long i = serverWorld.getDayTime() + 24000L;
        evt.setTimeAddition((i - i % 24000L) - 12001L);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
    final Player player = evt.getPlayer();
    Level world = player.level;

    if (!world.isClientSide) {
      CapabilitySleepData.getCapability(player)
          .ifPresent(sleepdata -> player.getSleepingPos().ifPresent(bedPos -> {
            final long wakeTime = world.getDayTime();
            final long timeSlept = wakeTime - sleepdata.getSleepTime();
            final BlockState state = world.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {
                List<MobEffectInstance> debuffs = ComfortsConfig.sleepingBagDebuffs;

                if (!debuffs.isEmpty()) {

                  for (MobEffectInstance effect : debuffs) {
                    player.addEffect(
                        new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                            effect.getAmplifier()));
                  }
                }

                if (world.random.nextDouble() < ComfortsConfig.SERVER.sleepingBagBreakage.get()) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                  world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                  world.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 35);
                  player.displayClientMessage(
                      new TranslatableComponent("block.comforts.sleeping_bag.broke"), true);
                  world.playSound(null, bedPos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS,
                      1.0F, 1.0F);
                  player.clearSleepingPos();
                }
              }

              if (!broke && sleepdata.getAutoSleepPos() != null) {
                final BlockPos blockpos = bedPos
                    .relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                world.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 35);
                player.clearSleepingPos();
              }
            }
            sleepdata.setWakeTime(wakeTime);
            sleepdata.setTiredTime(
                wakeTime + (long) (timeSlept / ComfortsConfig.SERVER.sleepyFactor.get()));
            sleepdata.setAutoSleepPos(null);
          }));
    }
  }

  @SubscribeEvent
  public void onPlayerSleep(PlayerSleepInBedEvent evt) {
    final Player player = evt.getPlayer();
    CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {

      if (!player.level.isClientSide) {
        final long dayTime = player.getCommandSenderWorld().getDayTime();
        sleepdata.setSleepTime(dayTime);

        if (ComfortsConfig.SERVER.wellRested.get()) {
          if (sleepdata.getWakeTime() > dayTime) {
            sleepdata.setWakeTime(0);
            sleepdata.setTiredTime(0);
          }

          if (sleepdata.getTiredTime() > dayTime) {
            player.displayClientMessage(new TranslatableComponent("capability.comforts.not_sleepy"),
                true);
            evt.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
          }
        }
      }
    });
  }
}
