package top.theillusivec4.comforts.mixin;

import java.util.function.BooleanSupplier;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.comforts.common.ComfortsEvents;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(ServerWorld.class)
public class MixinServerWorld {

  private long newTime;
  private long curTime;

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.setTimeOfDay(J)V"), method = "tick")
  public void comforts$setTimeOfDayPre(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    curTime = ((ServerWorld) (Object) this).getTimeOfDay();
    long l = curTime + 24000L;
    newTime = l - l % 24000L;
  }

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.setTimeOfDay(J)V", shift = At.Shift.AFTER), method = "tick")
  public void comforts$setTimeOfDayPost(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    ServerWorld world = (ServerWorld) (Object) this;
    world.setTimeOfDay(ComfortsEvents.getWakeTime(world, newTime, curTime));
  }
}
