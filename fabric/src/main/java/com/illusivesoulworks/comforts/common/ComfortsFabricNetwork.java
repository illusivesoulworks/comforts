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

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.network.ComfortsPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class ComfortsFabricNetwork {

  public static void sendAutoSleep(ServerPlayer player, BlockPos pos) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeBlockPos(pos);
    ServerPlayNetworking.send(player, ComfortsPackets.AUTO_SLEEP, buf);
  }

  public static void sendPlaceBag(ServerPlayer player, InteractionHand hand, Vec3 clickLocation,
                                  Direction clickedFace, BlockPos clickedPos, boolean inside) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeEnum(hand);
    buf.writeVector3f(clickLocation.toVector3f());
    buf.writeEnum(clickedFace);
    buf.writeBlockPos(clickedPos);
    buf.writeBoolean(inside);
    ServerPlayNetworking.send(player, ComfortsPackets.PLACE_BAG, buf);
  }
}
