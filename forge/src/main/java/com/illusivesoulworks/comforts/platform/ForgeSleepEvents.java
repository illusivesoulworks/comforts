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

package com.illusivesoulworks.comforts.platform;

import com.illusivesoulworks.comforts.common.CapabilitySleepData;
import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.common.network.ComfortsForgeNetwork;
import com.illusivesoulworks.comforts.common.network.SPacketAutoSleep;
import com.illusivesoulworks.comforts.platform.services.ISleepEvents;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;

public class ForgeSleepEvents implements ISleepEvents {

  @Override
  public Player.BedSleepingProblem getSleepResult(Player player, BlockPos pos) {
    return ForgeEventFactory.onPlayerSleepInBed(player, Optional.of(pos));
  }

  @Override
  public boolean isAwakeTime(Player player, BlockPos pos) {
    return !ForgeEventFactory.fireSleepingTimeCheck(player, Optional.of(pos));
  }

  @Override
  public Optional<? extends ISleepData> getSleepData(Player player) {
    return CapabilitySleepData.getCapability(player).resolve();
  }

  @Override
  public void sendAutoSleepPacket(ServerPlayer player, BlockPos pos) {
    ComfortsForgeNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketAutoSleep(player.getId(), pos));
  }
}
