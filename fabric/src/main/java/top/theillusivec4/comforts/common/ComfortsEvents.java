package top.theillusivec4.comforts.common;

import java.util.List;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.config.ComfortsConfig;

public class ComfortsEvents {

  public static void setup() {
    EntitySleepEvents.ALLOW_SLEEPING.register(ComfortsEvents::trySleep);
    EntitySleepEvents.ALLOW_SLEEP_TIME.register(ComfortsEvents::canSleepNow);
    EntitySleepEvents.STOP_SLEEPING.register(ComfortsEvents::wakeUp);
  }

  private static void wakeUp(LivingEntity livingEntity, BlockPos blockPos) {
    World world = livingEntity.world;

    if (!world.isClient) {
      livingEntity.getSleepingPosition().ifPresent(
          bedPos -> ComfortsComponents.SLEEP_TRACKER.maybeGet(livingEntity).ifPresent(tracker -> {
            final long wakeTime = world.getTimeOfDay();
            final long timeSlept = wakeTime - tracker.getSleepTime();
            final BlockState state = world.getBlockState(bedPos);

            if (state.getBlock() instanceof SleepingBagBlock) {
              boolean broke = false;

              if (timeSlept > 500L) {
                List<StatusEffectInstance> debuffs = ComfortsConfig.sleepingBagDebuffs;

                for (StatusEffectInstance effect : debuffs) {
                  livingEntity.addStatusEffect(
                      new StatusEffectInstance(effect.getEffectType(), effect.getDuration(),
                          effect.getAmplifier()));
                }

                if (world.random.nextDouble() < ComfortsConfig.sleepingBagBreakage) {
                  broke = true;
                  final BlockPos blockpos = bedPos
                      .offset(state.get(HorizontalFacingBlock.FACING).getOpposite());
                  world.removeBlock(bedPos, false);
                  world.removeBlock(blockpos, false);

                  if (livingEntity instanceof PlayerEntity) {
                    ((PlayerEntity) livingEntity).sendMessage(
                        new TranslatableText("block.comforts.sleeping_bag.broke"), true);
                  }
                  world.playSound(null, bedPos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS,
                      1.0F, 1.0F);
                  livingEntity.clearSleepingPosition();
                }
              }

              if (!broke) {
                tracker.getAutoSleepPos().ifPresent(pos -> {
                  final BlockPos blockpos = bedPos
                      .offset(state.get(HorizontalFacingBlock.FACING).getOpposite());
                  world.removeBlock(blockpos, false);
                  world.removeBlock(bedPos, false);
                  livingEntity.clearSleepingPosition();
                });
              }
            }
            tracker.setWakeTime(wakeTime);
            tracker.setTiredTime(wakeTime + (long) (timeSlept / ComfortsConfig.sleepyFactor));
            tracker.setAutoSleepPos(null);
          }));
    }
  }

  private static PlayerEntity.SleepFailureReason trySleep(PlayerEntity player, BlockPos blockPos) {
    return ComfortsComponents.SLEEP_TRACKER.maybeGet(player).map(tracker -> {
      final long dayTime = player.world.getTimeOfDay();
      tracker.setSleepTime(dayTime);

      if (ComfortsConfig.wellRested) {

        if (tracker.getWakeTime() > dayTime) {
          tracker.setWakeTime(0);
          tracker.setTiredTime(0);
        }

        if (tracker.getTiredTime() > dayTime) {
          player.sendMessage(new TranslatableText("capability.comforts.not_sleepy"), true);
          return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
        }
      }
      return null;
    }).orElse(null);
  }

  private static ActionResult canSleepNow(PlayerEntity player, BlockPos blockPos,
                                          boolean vanillaResult) {
    final World world = player.getEntityWorld();
    final long worldTime = world.getTimeOfDay() % 24000L;

    if (world.getBlockState(blockPos).getBlock() instanceof HammockBlock) {

      if (worldTime > 500L && worldTime < 11500L) {
        return ActionResult.SUCCESS;
      } else {
        return ComfortsConfig.nightHammocks ? ActionResult.PASS : ActionResult.FAIL;
      }
    }
    return ActionResult.PASS;
  }

  public static void setWakeTime(ServerWorld serverWorld, long curTime) {
    final boolean[] activeHammock = {false};
    List<? extends PlayerEntity> players = serverWorld.getPlayers();

    for (PlayerEntity player : players) {
      player.getSleepingPosition().ifPresent(bedPos -> {
        if (player.isSleeping() &&
            serverWorld.getBlockState(bedPos).getBlock() instanceof HammockBlock) {
          activeHammock[0] = true;
        }
      });

      if (activeHammock[0]) {
        break;
      }
    }

    if (activeHammock[0] && serverWorld.isDay()) {
      final long i = curTime + 24000L;
      serverWorld.setTimeOfDay((i - i % 24000L) - 12001L);
    }
  }
}
