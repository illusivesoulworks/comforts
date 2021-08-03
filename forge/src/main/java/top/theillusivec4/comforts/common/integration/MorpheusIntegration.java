//package top.theillusivec4.comforts.common.integration;
//
//import net.minecraft.world.server.ServerWorld;
//import net.minecraftforge.fml.server.ServerLifecycleHooks;
//import net.quetzi.morpheus.Morpheus;
//
//public class MorpheusIntegration {
//
//  public static void setup() {
//    Morpheus.register.registerHandler(() -> {
//      ServerWorld serverWorld =
//          ServerLifecycleHooks.getCurrentServer().getWorld(ServerWorld.OVERWORLD);
//
//      if (serverWorld != null) {
//        long l = serverWorld.getDayTime() + 24000L;
//        serverWorld.setDayTime(net.minecraftforge.event.ForgeEventFactory
//            .onSleepFinished(serverWorld, l - l % 24000L, serverWorld.getDayTime()));
//      }
//    }, ServerWorld.OVERWORLD);
//  }
//}
