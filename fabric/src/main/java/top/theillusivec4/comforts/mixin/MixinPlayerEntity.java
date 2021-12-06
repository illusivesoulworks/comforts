package top.theillusivec4.comforts.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.comforts.client.ComfortsClient;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

  @Inject(at = @At("TAIL"), method = "tick")
  public void comforts$playerTick(CallbackInfo ci) {
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (player.world.isClient) {
      ComfortsClient.playerTick(player);
    }
  }
}
