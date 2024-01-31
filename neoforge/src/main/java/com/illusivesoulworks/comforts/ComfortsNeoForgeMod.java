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

import com.illusivesoulworks.comforts.common.ComfortsCommonEventsListener;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.SleepDataAttachment;
import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.common.network.ComfortsClientPayloadHandler;
import com.illusivesoulworks.comforts.common.network.SPacketAutoSleep;
import com.illusivesoulworks.comforts.common.network.SPacketPlaceBag;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(ComfortsConstants.MOD_ID)
public class ComfortsNeoForgeMod {

  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
      DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ComfortsConstants.MOD_ID);
  public static final Supplier<AttachmentType<? extends ISleepData>> SLEEP_DATA =
      ATTACHMENT_TYPES.register("sleep_data",
          () -> AttachmentType.serializable(SleepDataAttachment::new).copyOnDeath().build());

  public ComfortsNeoForgeMod(IEventBus eventBus) {
    ComfortsCommonMod.init();
    ComfortsCommonMod.initConfig();

    if (FMLEnvironment.dist == Dist.CLIENT) {
      ComfortsNeoForgeClientMod.init(eventBus);
    }
    ATTACHMENT_TYPES.register(eventBus);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerPayloadHandler);
    eventBus.addListener(this::creativeTab);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    NeoForge.EVENT_BUS.register(new ComfortsCommonEventsListener());
  }

  private void registerPayloadHandler(final RegisterPayloadHandlerEvent evt) {
    evt.registrar(ComfortsConstants.MOD_ID).play(SPacketAutoSleep.ID, SPacketAutoSleep::new,
        handler -> handler.client(ComfortsClientPayloadHandler.getInstance()::handleAutoSleep));
    evt.registrar(ComfortsConstants.MOD_ID).play(SPacketPlaceBag.ID, SPacketPlaceBag::new,
        handler -> handler.client(ComfortsClientPayloadHandler.getInstance()::handlePlaceBag));
  }

  private void creativeTab(final BuildCreativeModeTabContentsEvent evt) {
    ResourceKey<CreativeModeTab> tab = evt.getTabKey();

    if (tab == CreativeModeTabs.COLORED_BLOCKS || tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {

      for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
        evt.accept(value.get());
        ComfortsConstants.LOG.info(value.getResourceKey().toString());
      }

      for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
        evt.accept(value.get());
      }
    }

    if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
      evt.accept(ComfortsRegistry.ROPE_AND_NAIL_ITEM.get());
    }
  }
}