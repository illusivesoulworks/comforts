/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SPacketAutoSleep {

  private final int entityId;
  private final BlockPos pos;

  public SPacketAutoSleep(int entityIdIn, BlockPos posIn) {
    this.entityId = entityIdIn;
    this.pos = posIn;
  }

  public static void encode(SPacketAutoSleep msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.entityId);
    buf.writeBlockPos(msg.pos);
  }

  public static SPacketAutoSleep decode(FriendlyByteBuf buf) {
    return new SPacketAutoSleep(buf.readInt(), buf.readBlockPos());
  }

  public static void handle(SPacketAutoSleep msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel level = Minecraft.getInstance().level;

      if (level != null) {
        Entity entity = level.getEntity(msg.entityId);

        if (entity instanceof final Player player) {
          ComfortsPackets.handleAutoSleep(player, msg.pos);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
