//package top.theillusivec4.comforts.common.integration;
//
//import com.stereowalker.survive.entity.SurviveEntityStats;
//import com.stereowalker.survive.util.TemperatureStats;
//import java.util.Optional;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import top.theillusivec4.comforts.common.ComfortsConfig;
//import top.theillusivec4.comforts.common.block.SleepingBagBlock;
//import top.theillusivec4.comforts.common.capability.CapabilitySleepData;
//
//public class SurviveIntegration {
//
//  @SubscribeEvent
//  public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
//
//    if (ComfortsConfig.insulatedSleepingBag) {
//      PlayerEntity player = evt.getPlayer();
//      World world = player.world;
//      Optional<BlockPos> maybePos = player.getBedPosition();
//      maybePos.ifPresent(pos -> {
//        if (!world.isRemote && world.getBlockState(pos).getBlock() instanceof SleepingBagBlock) {
//          CapabilitySleepData.getCapability(player).ifPresent(sleepData -> {
//            long timeSlept = world.getDayTime() - sleepData.getSleepTime();
//
//            if (timeSlept > 1000L) {
//              warmBody((ServerPlayerEntity) player, timeSlept);
//            }
//          });
//        }
//      });
//    }
//  }
//
//  private void warmBody(ServerPlayerEntity player, long timeSlept) {
//    TemperatureStats stats = SurviveEntityStats.getTemperatureStats(player);
//    double currentTemp = stats.getTemperatureLevel();
//    double newTemp = currentTemp;
//
//    if (currentTemp < 37.0D) {
//      double warmFactor = (double) (timeSlept / 1000L);
//      newTemp = (37.0D - currentTemp) * warmFactor / 10.0D + currentTemp;
//    }
//    stats.setTemperatureLevel((int) newTemp);
//    SurviveEntityStats.setTemperatureStats(player, stats);
//  }
//}
