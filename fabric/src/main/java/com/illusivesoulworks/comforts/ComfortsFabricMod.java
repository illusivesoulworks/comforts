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
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.network.SPacketAutoSleep;
import com.illusivesoulworks.comforts.common.network.SPacketPlaceBag;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import com.illusivesoulworks.comforts.data.HammockEnabledCondition;
import com.illusivesoulworks.comforts.data.SleepingBagEnabledCondition;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;

public class ComfortsFabricMod implements ModInitializer {

  @Override
  public void onInitialize() {
    ComfortsCommonMod.init();
    EntitySleepEvents.ALLOW_SLEEPING.register(
        (player, sleepingPos) -> ComfortsEvents.onSleep(player));
    EntitySleepEvents.ALLOW_BED.register((entity, sleepingPos, state, vanillaResult) -> {
      ComfortsConstants.Result result = ComfortsEvents.canSleep(entity.level(), sleepingPos);

      if (result == ComfortsConstants.Result.DENY) {
        return InteractionResult.FAIL;
      } else if (result == ComfortsConstants.Result.ALLOW) {
        return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
    });
    EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {

      if (entity instanceof Player player) {
        ComfortsEvents.onWakeUp(player);
      }
    });
    List<RegistryObject<Block>> comfortsBlocks = new ArrayList<>();
    comfortsBlocks.addAll(ComfortsRegistry.HAMMOCKS.values());
    comfortsBlocks.addAll(ComfortsRegistry.SLEEPING_BAGS.values());

    for (RegistryObject<Block> value : comfortsBlocks) {
      Block block = value.get();

      if (block instanceof BaseComfortsBlock baseComfortsBlock) {
        LandPathNodeTypesRegistry.register(block,
                                           (state, neighbor) -> baseComfortsBlock.getBlockPathType(
                                               state, null, null, null));
      }
    }
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {

      for (RegistryObject<Block> value : comfortsBlocks) {
        entries.accept(value.get());
      }
    });
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {

      for (RegistryObject<Block> value : comfortsBlocks) {
        entries.accept(value.get());
      }
      entries.accept(ComfortsRegistry.ROPE_AND_NAIL_ITEM.get());
    });
    PayloadTypeRegistry.playS2C().register(SPacketPlaceBag.TYPE, SPacketPlaceBag.STREAM_CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketAutoSleep.TYPE, SPacketAutoSleep.STREAM_CODEC);
    ResourceConditions.register(HammockEnabledCondition.TYPE);
    ResourceConditions.register(SleepingBagEnabledCondition.TYPE);
  }
}
