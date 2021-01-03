package top.theillusivec4.comforts.common.network;

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.comforts.common.ComfortsMod;
import top.theillusivec4.comforts.common.ComfortsComponents;

public class ComfortsNetwork {

  public static Identifier SYNC_AUTOSLEEP = ComfortsMod.id("autosleep");

  public static void sendAutoSleep(PlayerEntity player, BlockPos pos) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeBlockPos(pos);
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SYNC_AUTOSLEEP, buf);
  }

  public static void readAutoSleep(PacketContext ctx, PacketByteBuf buf) {
    BlockPos pos = buf.readBlockPos();
    ctx.getTaskQueue().execute(() -> {
      ComfortsComponents.SLEEP_TRACKER.maybeGet(ctx.getPlayer())
          .ifPresent(tracker -> tracker.setAutoSleepPos(pos));
    });
  }
}
