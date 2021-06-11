package top.theillusivec4.comforts.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.comforts.common.ComfortsComponents;
import top.theillusivec4.comforts.common.ComfortsMod;

public class ComfortsNetwork {

  public static Identifier SYNC_AUTOSLEEP = ComfortsMod.id("autosleep");

  public static void sendAutoSleep(ServerPlayerEntity player, BlockPos pos) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeBlockPos(pos);
    ServerPlayNetworking.send(player, SYNC_AUTOSLEEP, buf);
  }

  public static void readAutoSleep(MinecraftClient minecraftClient,
                                   ClientPlayNetworkHandler clientPlayNetworkHandler,
                                   PacketByteBuf packetByteBuf, PacketSender packetSender) {
    BlockPos pos = packetByteBuf.readBlockPos();
    minecraftClient.execute(() -> ComfortsComponents.SLEEP_TRACKER.maybeGet(minecraftClient.player)
        .ifPresent(tracker -> tracker.setAutoSleepPos(pos)));
  }
}
