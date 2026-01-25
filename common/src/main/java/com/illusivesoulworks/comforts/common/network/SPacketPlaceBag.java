package com.illusivesoulworks.comforts.common.network;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.item.SleepingBagItem;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3fc;

public record SPacketPlaceBag(int entityId, InteractionHand hand, Direction direction,
                              BlockPos blockPos, Vector3fc location, boolean inside)
    implements CustomPacketPayload {

  public static final Type<SPacketPlaceBag> TYPE =
      new Type<>(Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "place_bag"));
  public static final StreamCodec<FriendlyByteBuf, SPacketPlaceBag> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          SPacketPlaceBag::entityId,
          ByteBufCodecs.idMapper((val) -> InteractionHand.values()[val], Enum::ordinal),
          SPacketPlaceBag::hand,
          ByteBufCodecs.idMapper((val) -> Direction.values()[val], Enum::ordinal),
          SPacketPlaceBag::direction,
          BlockPos.STREAM_CODEC,
          SPacketPlaceBag::blockPos,
          ByteBufCodecs.VECTOR3F,
          SPacketPlaceBag::location,
          ByteBufCodecs.BOOL,
          SPacketPlaceBag::inside,
          SPacketPlaceBag::new);

  public static void handle(Player player, InteractionHand hand,
                            BlockHitResult blockHitResult) {

    if (player.getItemInHand(hand).getItem() instanceof SleepingBagItem sleepingBagItem) {
      sleepingBagItem.syncedUseOn(new UseOnContext(player, hand, blockHitResult));
    }
  }

  @Nonnull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
