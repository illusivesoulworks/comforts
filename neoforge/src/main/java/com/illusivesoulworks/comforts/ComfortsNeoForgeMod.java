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
import com.illusivesoulworks.comforts.data.ComfortsBlockTagsProvider;
import com.illusivesoulworks.comforts.data.ComfortsItemTagProvider;
import com.illusivesoulworks.comforts.data.ComfortsLootTableProvider;
import com.illusivesoulworks.comforts.data.ComfortsModelProvider;
import com.illusivesoulworks.comforts.data.ComfortsRecipeProvider;
import com.illusivesoulworks.comforts.data.HammockEnabledCondition;
import com.illusivesoulworks.comforts.data.SleepingBagEnabledCondition;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(ComfortsConstants.MOD_ID)
public class ComfortsNeoForgeMod {

  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
      DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ComfortsConstants.MOD_ID);
  private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
      DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ComfortsConstants.MOD_ID);
  public static final Supplier<AttachmentType<? extends ISleepData>> SLEEP_DATA =
      ATTACHMENT_TYPES.register("sleep_data",
                                () -> AttachmentType.serializable(SleepDataAttachment::new)
                                    .copyOnDeath().build());
  public static final Supplier<MapCodec<? extends ICondition>> SLEEPING_BAG_CONDITION =
      CONDITIONS.register("sleeping_bag_enabled", () -> SleepingBagEnabledCondition.CODEC);
  public static final Supplier<MapCodec<? extends ICondition>> HAMMOCK_CONDITION =
      CONDITIONS.register("hammock_enabled", () -> HammockEnabledCondition.CODEC);

  public ComfortsNeoForgeMod(IEventBus eventBus) {
    ComfortsCommonMod.init();
    ComfortsCommonMod.initConfig();

    if (FMLEnvironment.dist == Dist.CLIENT) {
      ComfortsNeoForgeClientMod.init(eventBus);
    }
    ATTACHMENT_TYPES.register(eventBus);
    CONDITIONS.register(eventBus);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerPayloadHandler);
    eventBus.addListener(this::creativeTab);
    eventBus.addListener(this::gatherData);
  }

  private void gatherData(GatherDataEvent.Client evt) {
    DataGenerator generator = evt.getGenerator();
    CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getLookupProvider();
    DataGenerator gen = evt.getGenerator();
    PackOutput packOutput = gen.getPackOutput();
    ComfortsBlockTagsProvider blockTagsProvider =
        new ComfortsBlockTagsProvider(packOutput, lookupProvider, ComfortsConstants.MOD_ID);
    generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                                                      List.of(
                                                          new LootTableProvider.SubProviderEntry(
                                                              ComfortsLootTableProvider::new,
                                                              LootContextParamSets.BLOCK)),
                                                      lookupProvider));
    generator.addProvider(true, new ComfortsRecipeProvider.Runner(packOutput, lookupProvider));
    generator.addProvider(true, blockTagsProvider);
    generator.addProvider(true, new ComfortsItemTagProvider(packOutput, lookupProvider,
                                                            blockTagsProvider.contentsGetter(),
                                                            ComfortsConstants.MOD_ID));
    generator.addProvider(true, new ComfortsModelProvider(packOutput));
  }

  private void setup(final FMLCommonSetupEvent evt) {
    NeoForge.EVENT_BUS.register(new ComfortsCommonEventsListener());
  }

  private void registerPayloadHandler(final RegisterPayloadHandlersEvent evt) {
    evt.registrar(ComfortsConstants.MOD_ID)
        .playToClient(SPacketAutoSleep.TYPE, SPacketAutoSleep.STREAM_CODEC,
                      ComfortsClientPayloadHandler.getInstance()::handleAutoSleep);
    evt.registrar(ComfortsConstants.MOD_ID)
        .playToClient(SPacketPlaceBag.TYPE, SPacketPlaceBag.STREAM_CODEC,
                      ComfortsClientPayloadHandler.getInstance()::handlePlaceBag);
  }

  private void creativeTab(final BuildCreativeModeTabContentsEvent evt) {
    ResourceKey<CreativeModeTab> tab = evt.getTabKey();

    if (tab == CreativeModeTabs.COLORED_BLOCKS || tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {

      for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
        evt.accept(value.get());
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