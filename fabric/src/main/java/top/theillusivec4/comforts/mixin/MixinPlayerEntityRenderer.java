package top.theillusivec4.comforts.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.comforts.client.ComfortsClient;

@SuppressWarnings("unused")
@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

  @Inject(at = @At("HEAD"), method = "render")
  public void comforts$sleepingTranslate(AbstractClientPlayerEntity playerEntity, float f, float g,
                                       MatrixStack matrixStack,
                                       VertexConsumerProvider vertexConsumerProvider, int i,
                                       CallbackInfo ci) {
    ComfortsClient.sleepingTranslate(playerEntity, matrixStack);
  }

  @Inject(at = @At("TAIL"), method = "render")
  public void comforts$resetSleepingTranslate(AbstractClientPlayerEntity playerEntity, float f,
                                            float g, MatrixStack matrixStack,
                                            VertexConsumerProvider vertexConsumerProvider, int i,
                                            CallbackInfo ci) {
    ComfortsClient.resetSleepingTranslate(playerEntity, matrixStack);
  }
}