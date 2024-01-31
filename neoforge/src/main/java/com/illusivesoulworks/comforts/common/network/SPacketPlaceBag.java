package com.illusivesoulworks.comforts.common.network;

import com.illusivesoulworks.comforts.ComfortsConstants;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record SPacketPlaceBag(int entityId, InteractionHand hand, Direction direction,
                              BlockPos blockPos, Vec3 location, boolean inside)
    implements CustomPacketPayload {

  public static final ResourceLocation ID =
      new ResourceLocation(ComfortsConstants.MOD_ID, "place_bag");

  public SPacketPlaceBag(FriendlyByteBuf buf) {
    this(buf.readInt(), buf.readEnum(InteractionHand.class), buf.readEnum(Direction.class),
        buf.readBlockPos(), new Vec3(buf.readVector3f()), buf.readBoolean());
  }

  @Override
  public void write(@Nonnull FriendlyByteBuf buf) {
    buf.writeInt(this.entityId());
    buf.writeEnum(this.hand());
    buf.writeVector3f(this.location().toVector3f());
    buf.writeEnum(this.direction());
    buf.writeBlockPos(this.blockPos());
    buf.writeBoolean(this.inside());
  }

  @Nonnull
  @Override
  public ResourceLocation id() {
    return ID;
  }
}
