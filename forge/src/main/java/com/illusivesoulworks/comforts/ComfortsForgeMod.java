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

import com.illusivesoulworks.comforts.common.CapabilitySleepData;
import com.illusivesoulworks.comforts.common.ComfortsCommonEventsListener;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.common.network.ComfortsForgeNetwork;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import com.illusivesoulworks.comforts.data.ComfortsRecipeProvider;
import com.illusivesoulworks.comforts.data.HammockEnabledCondition;
import com.illusivesoulworks.comforts.data.SleepingBagEnabledCondition;
import com.mojang.serialization.MapCodec;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ComfortsConstants.MOD_ID)
public class ComfortsForgeMod {

  private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
      DeferredRegister.create(ForgeRegistries.Keys.CONDITION_SERIALIZERS, ComfortsConstants.MOD_ID);
  public static final Supplier<MapCodec<? extends ICondition>> SLEEPING_BAG_CONDITION =
      CONDITIONS.register("sleeping_bag_enabled", () -> SleepingBagEnabledCondition.CODEC);
  public static final Supplier<MapCodec<? extends ICondition>> HAMMOCK_CONDITION =
      CONDITIONS.register("hammock_enabled", () -> HammockEnabledCondition.CODEC);

  public ComfortsForgeMod(FMLJavaModLoadingContext context) {
    ComfortsCommonMod.init();
    ComfortsCommonMod.initConfig();

    IEventBus eventBus = context.getModEventBus();
    if (FMLEnvironment.dist == Dist.CLIENT) {
      ComfortsForgeClientMod.init(eventBus);
    }
    CONDITIONS.register(eventBus);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerCapabilities);
    eventBus.addListener(this::creativeTab);
    eventBus.addListener(this::gatherData);
  }

  private void gatherData(GatherDataEvent evt) {
    DataGenerator generator = evt.getGenerator();

    if (evt.includeServer()) {
      CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getLookupProvider();
      DataGenerator gen = evt.getGenerator();
      PackOutput packOutput = gen.getPackOutput();
      generator.addProvider(true, new ComfortsRecipeProvider(packOutput, lookupProvider));
    }
  }

  private void setup(final FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new ComfortsCommonEventsListener());
    MinecraftForge.EVENT_BUS.register(new CapabilitySleepData.CapabilityEvents());
    ComfortsForgeNetwork.setup();
  }

  private void registerCapabilities(final RegisterCapabilitiesEvent evt) {
    evt.register(ISleepData.class);
  }

  private void creativeTab(final BuildCreativeModeTabContentsEvent evt) {
    ResourceKey<CreativeModeTab> tab = evt.getTabKey();

    if (tab == CreativeModeTabs.COLORED_BLOCKS || tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {

      for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
        evt.accept(value);
      }

      for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
        evt.accept(value);
      }
    }

    if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
      evt.accept(ComfortsRegistry.ROPE_AND_NAIL_ITEM);
    }
  }
}