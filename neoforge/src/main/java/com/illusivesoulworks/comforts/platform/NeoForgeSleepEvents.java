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

import com.illusivesoulworks.comforts.ComfortsNeoForgeMod;
import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.common.network.SPacketAutoSleep;
import com.illusivesoulworks.comforts.common.network.SPacketPlaceBag;
import com.illusivesoulworks.comforts.platform.services.ISleepEvents;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeSleepEvents implements ISleepEvents {

  @Override
  public Player.BedSleepingProblem getSleepResult(ServerPlayer player, BlockPos pos) {
    return null;
  }

  @Override
  public Either<Player.BedSleepingProblem, Unit> getSleepResult(ServerPlayer player, BlockPos pos,
                                                                Either<Player.BedSleepingProblem, Unit> vanillaResult) {
    return EventHooks.canPlayerStartSleeping(player, pos, vanillaResult);
  }

  @Override
  public boolean isAwakeTime(Player player, BlockPos pos) {
    return player.level().isBrightOutside();
  }

  @Override
  public Optional<? extends ISleepData> getSleepData(Player player) {
    return Optional.of(player.getData(ComfortsNeoForgeMod.SLEEP_DATA.get()));
  }

  @Override
  public void sendAutoSleepPacket(ServerPlayer player, BlockPos pos) {
    PacketDistributor.sendToPlayer(player, new SPacketAutoSleep(player.getId(), pos));
  }

  @Override
  public void sendPlaceBagPacket(ServerPlayer player, UseOnContext context) {
    PacketDistributor.sendToPlayer(player,
        new SPacketPlaceBag(player.getId(), context.getHand(), context.getClickedFace(),
            context.getClickedPos(), context.getClickLocation().toVector3f(), context.isInside()));
  }
}
