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

import com.illusivesoulworks.comforts.ComfortsConstants;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SPacketAutoSleep(int entityId, BlockPos pos) implements CustomPacketPayload {

  public static final ResourceLocation ID =
      new ResourceLocation(ComfortsConstants.MOD_ID, "auto_sleep");

  public SPacketAutoSleep(FriendlyByteBuf buf) {
    this(buf.readInt(), buf.readBlockPos());
  }

  @Override
  public void write(@Nonnull FriendlyByteBuf buf) {
    buf.writeInt(this.entityId());
    buf.writeBlockPos(this.pos());
  }

  @Nonnull
  @Override
  public ResourceLocation id() {
    return ID;
  }
}
