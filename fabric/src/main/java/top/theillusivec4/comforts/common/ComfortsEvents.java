package top.theillusivec4.comforts.common;

import java.util.List;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.config.ComfortsConfig;
import top.theillusivec4.somnus.api.PlayerSleepEvents;
import top.theillusivec4.somnus.api.WorldSleepEvents;

public class ComfortsEvents {

  public static void setup() {
    WorldSleepEvents.GET_WORLD_WAKE_TIME.register(ComfortsEvents::getWakeTime);
    PlayerSleepEvents.TRY_SLEEP.register(ComfortsEvents::trySleep);
    PlayerSleepEvents.CAN_SLEEP_NOW.register(ComfortsEvents::canSleepNow);
  }

  private static TriState canSleepNow(PlayerEntity player, BlockPos pos) {
    final World world = player.getEntityWorld();
    final long worldTime = world.getTimeOfDay() % 24000L;

    if (world.getBlockState(pos).getBlock() instanceof HammockBlock) {

      if (worldTime > 500L && worldTime < 11500L) {
        return TriState.TRUE;
      } else {
        return ComfortsConfig.nightHammocks ? TriState.DEFAULT : TriState.FALSE;
      }
    }
    return TriState.DEFAULT;
  }

  private static PlayerEntity.SleepFailureReason trySleep(ServerPlayerEntity serverPlayerEntity,
                                                          BlockPos blockPos) {
    return ComfortsComponents.SLEEP_TRACKER.maybeGet(serverPlayerEntity).map(tracker -> {
      final long dayTime = serverPlayerEntity.world.getTimeOfDay();
      tracker.setSleepTime(dayTime);

      if (ComfortsConfig.wellRested) {

        if (tracker.getWakeTime() > dayTime) {
          tracker.setWakeTime(0);
          tracker.setTiredTime(0);
        }

        if (tracker.getTiredTime() > dayTime) {
          serverPlayerEntity
              .sendMessage(new TranslatableText("capability.comforts.not_sleepy"), true);
          return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
        }
      }
      return null;
    }).orElse(null);
  }

  public static long getWakeTime(ServerWorld serverWorld, long newTime, long minTime) {
    final boolean[] activeHammock = {false};
    List<? extends PlayerEntity> players = serverWorld.getPlayers();

    for (PlayerEntity player : players) {
      player.getSleepingPosition().ifPresent(bedPos -> {
        if (player.isSleepingLongEnough() &&
            serverWorld.getBlockState(bedPos).getBlock() instanceof HammockBlock) {
          activeHammock[0] = true;
        }
      });

      if (activeHammock[0]) {
        break;
      }
    }

    if (activeHammock[0] && serverWorld.isDay()) {
      final long i = serverWorld.getTimeOfDay() + 24000L;
      return (i - i % 24000L) - 12001L;
    }
    return newTime;
  }

  public static void wakeUp(PlayerEntity player) {
    World world = player.world;

    if (!world.isClient) {
      player.getSleepingPosition().ifPresent(
          bedPos -> ComfortsComponents.SLEEP_TRACKER.maybeGet(player).ifPresent(tracker -> {
            final long wakeTime = world.getTimeOfDay();
            final long timeSlept = wakeTime - tracker.getSleepTime();
            final BlockState state = world.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {
                List<StatusEffectInstance> debuffs = ComfortsConfig.sleepingBagDebuffs;

                for (StatusEffectInstance effect : debuffs) {
                  player.addStatusEffect(
                      new StatusEffectInstance(effect.getEffectType(), effect.getDuration(),
                          effect.getAmplifier()));
                }

                if (world.random.nextDouble() < ComfortsConfig.sleepingBagBreakage) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .offset(state.get(HorizontalFacingBlock.FACING).getOpposite());
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                  world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 35);
                  player
                      .sendMessage(new TranslatableText("block.comforts.sleeping_bag.broke"), true);
                  world.playSound(null, bedPos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS,
                      1.0F, 1.0F);
                  player.clearSleepingPosition();
                }
              }

              if (!broke) {
                tracker.getAutoSleepPos().ifPresent(pos -> {
                  final BlockPos blockpos = bedPos
                      .offset(state.get(HorizontalFacingBlock.FACING).getOpposite());
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                  world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 35);
                  player.clearSleepingPosition();
                });
              }
            }
            tracker.setWakeTime(wakeTime);
            tracker.setTiredTime(wakeTime + (long) (timeSlept / ComfortsConfig.sleepyFactor));
            tracker.setAutoSleepPos(null);
          }));
    }
  }
}
