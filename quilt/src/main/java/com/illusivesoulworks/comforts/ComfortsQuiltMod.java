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

package com.illusivesoulworks.comforts;

import com.illusivesoulworks.comforts.common.ComfortsEvents;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ComfortsQuiltMod implements ModInitializer {

  @Override
  public void onInitialize(ModContainer modContainer) {
    ComfortsCommonMod.init();
    EntitySleepEvents.ALLOW_SLEEPING.register(
        (player, sleepingPos) -> ComfortsEvents.onSleep(player));
    EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
      ComfortsEvents.Result result = ComfortsEvents.checkTime(player.level(), sleepingPos);

      if (result == ComfortsEvents.Result.DENY) {
        return InteractionResult.FAIL;
      } else if (result == ComfortsEvents.Result.ALLOW) {
        return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
    });
    EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {

      if (entity instanceof Player player) {
        ComfortsEvents.onWakeUp(player);
      }
    });
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {

      for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
        entries.accept(value.get());
      }

      for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
        entries.accept(value.get());
      }
    });
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {

      for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
        entries.accept(value.get());
      }

      for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
        entries.accept(value.get());
      }
      entries.accept(ComfortsRegistry.ROPE_AND_NAIL_ITEM.get());
    });
  }
}
