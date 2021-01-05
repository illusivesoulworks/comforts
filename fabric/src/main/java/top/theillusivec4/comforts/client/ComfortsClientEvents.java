package top.theillusivec4.comforts.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsComponents;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;

public class ComfortsClientEvents {

  public static float getSleepTranslation(AbstractClientPlayerEntity player) {
    return player.getSleepingPosition().map(pos -> {
      final Block bed = player.world.getBlockState(pos).getBlock();
      if (bed instanceof SleepingBagBlock) {
        return -0.375F;
      } else if (bed instanceof HammockBlock) {
        return -0.5F;
      }
      return 0.0F;
    }).orElse(0.0F);
  }

  public static void playerTick(PlayerEntity player) {
    World world = player.getEntityWorld();

    if (!player.isSleeping()) {
      ComfortsComponents.SLEEP_TRACKER.maybeGet(player).ifPresent(tracker -> {
        tracker.getAutoSleepPos().ifPresent(pos -> {
          final BlockState state = world.getBlockState(pos);

          if (pos.getSquaredDistance(player.getX(), player.getY(), player.getZ(), true) < 20 &&
              state.getBlock() instanceof SleepingBagBlock) {
            BlockHitResult hit = new BlockHitResult(new Vec3d(0, 0, 0),
                player.getHorizontalFacing(), pos, false);
            ClientPlayerInteractionManager interactionManager =
                MinecraftClient.getInstance().interactionManager;

            if (interactionManager != null) {
              interactionManager
                  .interactBlock((ClientPlayerEntity) player, (ClientWorld) player.world,
                      Hand.MAIN_HAND, hit);
            }
          }
          tracker.setAutoSleepPos(null);
        });
      });
    }
  }
}
