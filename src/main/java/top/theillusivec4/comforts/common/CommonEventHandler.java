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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

  public static List<EffectInstance> debuffs = new ArrayList<>();

  @SubscribeEvent
  public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
    final PlayerEntity player = evt.getPlayer();
    final World world = player.getEntityWorld();
    final BlockPos pos = evt.getNewSpawn();

    if (pos != null && !world.isRemote) {
      Block block = world.getBlockState(pos).getBlock();

      if (block instanceof SleepingBagBlock || block instanceof HammockBlock) {
        evt.setCanceled(true);
      }
    }
  }

  @SubscribeEvent
  public void onSleepTimeCheck(SleepingTimeCheckEvent evt) {
    final World world = evt.getPlayer().getEntityWorld();
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
    IWorld world = evt.getWorld();

    if (world instanceof ServerWorld) {
      ServerWorld serverWorld = (ServerWorld) world;
      final boolean[] activeHammock = {false};
      List<? extends PlayerEntity> players = world.getPlayers();

      for (PlayerEntity player : players) {
        player.getBedPosition().ifPresent(bedPos -> {
          if (player.isPlayerFullyAsleep() && world.getBlockState(bedPos)
              .getBlock() instanceof HammockBlock) {
            activeHammock[0] = true;
          }
        });

        if (activeHammock[0]) {
          break;
        }
      }

      if (activeHammock[0] && ((ServerWorld) world).getWorld().isDaytime()) {
        final long i = serverWorld.getDayTime() + 24000L;
        evt.setTimeAddition((i - i % 24000L) - 12001L);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
    final PlayerEntity player = evt.getPlayer();
    World world = player.world;

    if (!world.isRemote) {
      CapabilitySleepData.getCapability(player)
          .ifPresent(sleepdata -> player.getBedPosition().ifPresent(bedPos -> {
            final long wakeTime = world.getDayTime();
            final long timeSlept = wakeTime - sleepdata.getSleepTime();
            final BlockState state = world.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {
                List<EffectInstance> debuffs = ComfortsConfig.sleepingBagDebuffs;

                if (!debuffs.isEmpty()) {

                  for (EffectInstance effect : debuffs) {
                    player.addPotionEffect(
                        new EffectInstance(effect.getPotion(), effect.getDuration(),
                            effect.getAmplifier()));
                  }
                }

                if (world.rand.nextDouble() < ComfortsConfig.SERVER.sleepingBagBreakage.get()) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .offset(state.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite());
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                  world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 35);
                  player.sendStatusMessage(
                      new TranslationTextComponent("block.comforts.sleeping_bag.broke"), true);
                  world.playSound(null, bedPos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS,
                      1.0F, 1.0F);
                  player.clearBedPosition();
                }
              }

              if (!broke && sleepdata.getAutoSleepPos() != null) {
                final BlockPos blockpos = bedPos
                    .offset(state.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite());
                world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 35);
                player.clearBedPosition();
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
    final PlayerEntity player = evt.getPlayer();
    CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {

      if (!player.world.isRemote) {
        final long dayTime = player.getEntityWorld().getDayTime();
        sleepdata.setSleepTime(dayTime);

        if (ComfortsConfig.SERVER.wellRested.get()) {
          if (sleepdata.getWakeTime() > dayTime) {
            sleepdata.setWakeTime(0);
            sleepdata.setTiredTime(0);
          }

          if (sleepdata.getTiredTime() > dayTime) {
            player.sendStatusMessage(new TranslationTextComponent("capability.comforts.not_sleepy"),
                true);
            evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
          }
        }
      }
    });
  }
}
