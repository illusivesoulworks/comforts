package com.illusivesoulworks.comforts.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ComfortsClientPayloadHandler {

  private static final ComfortsClientPayloadHandler INSTANCE =
      new ComfortsClientPayloadHandler();

  public static ComfortsClientPayloadHandler getInstance() {
    return INSTANCE;
  }

  public void handleAutoSleep(SPacketAutoSleep msg, PlayPayloadContext ctx) {
    ctx.workHandler().submitAsync(() -> {
          ClientLevel level = Minecraft.getInstance().level;

          if (level != null) {
            Entity entity = level.getEntity(msg.entityId());

            if (entity instanceof final Player player) {
              ComfortsPackets.handleAutoSleep(player, msg.pos());
            }
          }
        })
        .exceptionally(e -> {
          ctx.packetHandler()
              .disconnect(Component.translatable("comforts.networking.failed", e.getMessage()));
          return null;
        });
  }

  public void handlePlaceBag(SPacketPlaceBag msg, PlayPayloadContext ctx) {
    ctx.workHandler().submitAsync(() -> {
          ClientLevel level = Minecraft.getInstance().level;

          if (level != null) {
            Entity entity = level.getEntity(msg.entityId());

            if (entity instanceof final Player player) {
              ComfortsPackets.handlePlaceBag(player, msg.hand(),
                  new BlockHitResult(msg.location(), msg.direction(), msg.blockPos(), msg.inside()));
            }
          }
        })
        .exceptionally(e -> {
          ctx.packetHandler()
              .disconnect(Component.translatable("comforts.networking.failed", e.getMessage()));
          return null;
        });
  }
}
