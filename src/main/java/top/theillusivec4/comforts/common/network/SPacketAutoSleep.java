/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

public class SPacketAutoSleep {

  private int entityId;
  private BlockPos pos;

  public SPacketAutoSleep(int entityIdIn, BlockPos posIn) {
    this.entityId = entityIdIn;
    this.pos = posIn;
  }

  public static void encode(SPacketAutoSleep msg, PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeBlockPos(msg.pos);
  }

  public static SPacketAutoSleep decode(PacketBuffer buf) {
    return new SPacketAutoSleep(buf.readInt(), buf.readBlockPos());
  }

  public static void handle(SPacketAutoSleep msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);

      if (entity instanceof PlayerEntity) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        CapabilitySleepData.getCapability(playerEntity)
            .ifPresent(sleepdata -> sleepdata.setAutoSleepPos(msg.pos));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
