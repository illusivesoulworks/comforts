package top.theillusivec4.comforts.common;

import java.util.List;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.somnus.api.SleepEvents;

public class ComfortsEvents {

  public static void setup() {
    SleepEvents.GET_WORLD_WAKE_TIME.register(ComfortsEvents::getWakeTime);
    SleepEvents.INTERRUPT_SLEEPING.register(ComfortsEvents::interruptSleeping);
  }

  public static TriState interruptSleeping(PlayerEntity player, BlockPos pos) {
    final World world = player.getEntityWorld();
    final long worldTime = world.getTimeOfDay() % 24000L;

    if (world.getBlockState(pos).getBlock() instanceof HammockBlock) {

      if (worldTime > 500L && worldTime < 11500L) {
        return TriState.FALSE;
      } else {
        return TriState.TRUE;
      }
    }
    return TriState.DEFAULT;
  }

  public static long getWakeTime(ServerWorld serverWorld, long newTime, long minTime){
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
}
