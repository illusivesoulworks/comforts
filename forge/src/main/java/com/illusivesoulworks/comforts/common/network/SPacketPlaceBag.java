package com.illusivesoulworks.comforts.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class SPacketPlaceBag {
  private final int entityId;
  private final InteractionHand hand;
  private final Direction direction;
  private final BlockPos blockPos;
  private final Vec3 location;
  private final boolean inside;

  public SPacketPlaceBag(int entityIdIn, InteractionHand hand, Vec3 location, Direction direction,
                         BlockPos pos, boolean inside) {
    this.entityId = entityIdIn;
    this.hand = hand;
    this.direction = direction;
    this.location = location;
    this.blockPos = pos;
    this.inside = inside;
  }

  public static void encode(SPacketPlaceBag msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.entityId);
    buf.writeEnum(msg.hand);
    buf.writeVector3f(msg.location.toVector3f());
    buf.writeEnum(msg.direction);
    buf.writeBlockPos(msg.blockPos);
    buf.writeBoolean(msg.inside);
  }

  public static SPacketPlaceBag decode(FriendlyByteBuf buf) {
    return new SPacketPlaceBag(buf.readInt(), buf.readEnum(InteractionHand.class),
        new Vec3(buf.readVector3f()), buf.readEnum(Direction.class), buf.readBlockPos(),
        buf.readBoolean());
  }

  public static void handle(SPacketPlaceBag msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel level = Minecraft.getInstance().level;

      if (level != null) {
        Entity entity = level.getEntity(msg.entityId);

        if (entity instanceof final Player player) {
          ComfortsPackets.handlePlaceBag(player, msg.hand,
              new BlockHitResult(msg.location, msg.direction, msg.blockPos, msg.inside));
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
