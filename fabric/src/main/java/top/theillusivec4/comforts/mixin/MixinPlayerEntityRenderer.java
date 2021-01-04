package top.theillusivec4.comforts.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

  @Inject(at = @At("HEAD"), method = "render")
  public void _comforts_sleepingTranslate(AbstractClientPlayerEntity playerEntity, float f,
                                          float g, MatrixStack matrixStack,
                                          VertexConsumerProvider vertexConsumerProvider, int i,
                                          CallbackInfo ci) {

    if (playerEntity instanceof OtherClientPlayerEntity &&
        playerEntity.getPose() == EntityPose.SLEEPING) {
      playerEntity.getSleepingPosition().ifPresent(pos -> {
        final Block bed = playerEntity.world.getBlockState(pos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          matrixStack.translate(0.0f, -0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          matrixStack.translate(0.0f, -0.5F, 0.0f);
        }
      });
    }
  }

  @Inject(at = @At("TAIL"), method = "render")
  public void _comforts_resetSleepingTranslate(AbstractClientPlayerEntity playerEntity, float f,
                                               float g, MatrixStack matrixStack,
                                               VertexConsumerProvider vertexConsumerProvider, int i,
                                               CallbackInfo ci) {

    if (playerEntity instanceof OtherClientPlayerEntity &&
        playerEntity.getPose() == EntityPose.SLEEPING) {
      playerEntity.getSleepingPosition().ifPresent(pos -> {
        final Block bed = playerEntity.world.getBlockState(pos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          matrixStack.translate(0.0f, 0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          matrixStack.translate(0.0f, 0.5F, 0.0f);
        }
      });
    }
  }
}
