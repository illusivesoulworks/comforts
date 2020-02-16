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

package top.theillusivec4.comforts;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.client.ClientEventHandler;
import top.theillusivec4.comforts.client.renderer.HammockTileEntityRenderer;
import top.theillusivec4.comforts.client.renderer.SleepingBagTileEntityRenderer;
import top.theillusivec4.comforts.common.ComfortsConfig;
import top.theillusivec4.comforts.common.ComfortsRegistry;
import top.theillusivec4.comforts.common.CommonEventHandler;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;
import top.theillusivec4.comforts.common.item.ComfortsBaseItem;
import top.theillusivec4.comforts.common.item.HammockItem;
import top.theillusivec4.comforts.common.item.SleepingBagItem;
import top.theillusivec4.comforts.common.network.ComfortsNetwork;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;
import top.theillusivec4.comforts.data.ComfortsLootProvider;
import top.theillusivec4.comforts.integration.MorpheusIntegration;

@Mod(Comforts.MODID)
public class Comforts {

  public static final String MODID = "comforts";

  public static final ItemGroup CREATIVE_TAB = new ItemGroup(-1, "comforts") {
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
      return new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED));
    }
  };

  public static final Logger LOGGER = LogManager.getLogger();

  public Comforts() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::gatherData);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ComfortsConfig.serverSpec);
  }

  private void gatherData(GatherDataEvent evt) {
    DataGenerator generator = evt.getGenerator();

    if (evt.includeServer()) {
      generator.addProvider(new ComfortsLootProvider(generator));
    }
  }

  private void setup(FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    CapabilitySleepData.register();
    ComfortsNetwork.register();

    if (ModList.get().isLoaded("morpheus")) {
      MorpheusIntegration.register();
    }
  }

  @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientProxy {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent evt) {
      MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
      ClientRegistry.bindTileEntityRenderer(ComfortsRegistry.SLEEPING_BAG_TE,
          SleepingBagTileEntityRenderer::new);
      ClientRegistry
          .bindTileEntityRenderer(ComfortsRegistry.HAMMOCK_TE, HammockTileEntityRenderer::new);
      RenderTypeLookup.setRenderLayer(ComfortsRegistry.ROPE_AND_NAIL, RenderType.translucent());
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre evt) {

      if (evt.getMap().getBasePath() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
        for (DyeColor color : DyeColor.values()) {
          evt.addSprite(new ResourceLocation(Comforts.MODID, "entity/hammock/" + color.getName()));
          evt.addSprite(
              new ResourceLocation(Comforts.MODID, "entity/sleeping_bag/" + color.getName()));
        }
      }
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> evt) {
      IForgeRegistry<Block> registry = evt.getRegistry();
      Arrays.stream(DyeColor.values()).forEach(color -> {
        SleepingBagBlock sleepingBag = new SleepingBagBlock(color);
        ComfortsRegistry.SLEEPING_BAGS.put(color, sleepingBag);
        HammockBlock hammock = new HammockBlock(color);
        ComfortsRegistry.HAMMOCKS.put(color, hammock);
        registry.registerAll(sleepingBag, hammock);
      });
      registry.register(new RopeAndNailBlock());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> evt) {
      IForgeRegistry<Item> registry = evt.getRegistry();
      ComfortsRegistry.SLEEPING_BAGS.values()
          .forEach(block -> registry.register(new SleepingBagItem(block)));
      ComfortsRegistry.HAMMOCKS.values()
          .forEach(block -> registry.register(new HammockItem(block)));
      registry.register(new ComfortsBaseItem(ComfortsRegistry.ROPE_AND_NAIL));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> evt) {
      TileEntityType<?> sleepingBag = TileEntityType.Builder.create(SleepingBagTileEntity::new,
          ComfortsRegistry.SLEEPING_BAGS.values().toArray(new Block[0])).build(null)
          .setRegistryName(Comforts.MODID, "sleeping_bag");
      TileEntityType<?> hammock = TileEntityType.Builder
          .create(HammockTileEntity::new, ComfortsRegistry.HAMMOCKS.values().toArray(new Block[0]))
          .build(null).setRegistryName(Comforts.MODID, "hammock");
      evt.getRegistry().registerAll(sleepingBag, hammock);
    }
  }
}
