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

import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.common.capability.SleepDataImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilitySleepData {

  public static final Capability<ISleepData> SLEEP_DATA_CAP =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  private static final Map<UUID, LazyOptional<ISleepData>> SERVER_CACHE = new HashMap<>();
  private static final Map<UUID, LazyOptional<ISleepData>> CLIENT_CACHE = new HashMap<>();

  public static LazyOptional<ISleepData> getCapability(final Player player) {
    UUID key = player.getUUID();
    Map<UUID, LazyOptional<ISleepData>> cache =
        player.level().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
    return cache.computeIfAbsent(key, (k) -> {
      LazyOptional<ISleepData> opt = player.getCapability(SLEEP_DATA_CAP);
      opt.addListener((v) -> cache.remove(key));
      return opt;
    });
  }

  public static class Provider implements ICapabilitySerializable<Tag> {

    final LazyOptional<ISleepData> optional;
    final ISleepData data;

    Provider() {
      this.data = new SleepDataImpl();
      this.optional = LazyOptional.of(() -> data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {
      return SLEEP_DATA_CAP.orEmpty(capability, optional);
    }

    @Override
    public Tag serializeNBT() {
      return data.write();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
      data.read((CompoundTag) nbt);
    }
  }

  public static class CapabilityEvents {

    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
      Entity entity = evt.getObject();

      if (entity instanceof Player) {
        evt.addCapability(ISleepData.ID, new Provider());
      }
    }

    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {

      if (evt.isWasDeath()) {
        final Player player = evt.getEntity();
        final Player original = evt.getOriginal();
        original.reviveCaps();
        CapabilitySleepData.getCapability(player).ifPresent(
            data -> CapabilitySleepData.getCapability(original).ifPresent(data::copyFrom));
        original.invalidateCaps();
      }
    }
  }
}
