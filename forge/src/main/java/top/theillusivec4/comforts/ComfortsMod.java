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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.client.ClientEventHandler;
import top.theillusivec4.comforts.client.renderer.ComfortsBaseTileEntityRenderer;
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

@Mod(ComfortsMod.MOD_ID)
public final class ComfortsMod {

  public static final String MOD_ID = "comforts";

  public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(-1, "comforts") {
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
      return new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED));
    }
  };

  public static final Logger LOGGER = LogManager.getLogger();

  public ComfortsMod() {
    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::gatherData);
    eventBus.addListener(this::config);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ComfortsConfig.SERVER_SPEC);
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MOD_ID)) {
      ComfortsConfig.bake();
    }
  }

  private void gatherData(final GatherDataEvent evt) {
    DataGenerator generator = evt.getGenerator();

    if (evt.includeServer()) {
      generator.addProvider(new ComfortsLootProvider(generator));
    }
  }

  private void setup(final FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    CapabilitySleepData.register();
    ComfortsNetwork.register();

//    if (ModList.get().isLoaded("morpheus")) {
//      MorpheusIntegration.setup();
//    }
//
//    if (ModList.get().isLoaded("survive")) {
//      MinecraftForge.EVENT_BUS.register(new SurviveIntegration());
//    }
  }

  @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientProxy {

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent evt) {
      MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
      ItemBlockRenderTypes.setRenderLayer(ComfortsRegistry.ROPE_AND_NAIL, RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
      evt.registerBlockEntityRenderer(ComfortsRegistry.SLEEPING_BAG_TE,
          SleepingBagTileEntityRenderer::new);
      evt.registerBlockEntityRenderer(ComfortsRegistry.HAMMOCK_TE, HammockTileEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(
        final EntityRenderersEvent.RegisterLayerDefinitions evt) {
      evt.registerLayerDefinition(ComfortsBaseTileEntityRenderer.SLEEPING_BAG_HEAD,
          SleepingBagTileEntityRenderer::createHeadLayer);
      evt.registerLayerDefinition(ComfortsBaseTileEntityRenderer.SLEEPING_BAG_FOOT,
          SleepingBagTileEntityRenderer::createFootLayer);
      evt.registerLayerDefinition(ComfortsBaseTileEntityRenderer.HAMMOCK_HEAD,
          HammockTileEntityRenderer::createHeadLayer);
      evt.registerLayerDefinition(ComfortsBaseTileEntityRenderer.HAMMOCK_FOOT,
          HammockTileEntityRenderer::createFootLayer);
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre evt) {

      if (evt.getMap().location() == InventoryMenu.BLOCK_ATLAS) {
        for (final DyeColor color : DyeColor.values()) {
          evt.addSprite(
              new ResourceLocation(ComfortsMod.MOD_ID, "entity/hammock/" + color.getName()));
          evt.addSprite(new ResourceLocation(ComfortsMod.MOD_ID,
              "entity/sleeping_bag/" + color.getName()));
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
    public static void onTileEntityRegistry(final RegistryEvent.Register<BlockEntityType<?>> evt) {
      BlockEntityType<?> sleepingBag = BlockEntityType.Builder.of(SleepingBagTileEntity::new,
          ComfortsRegistry.SLEEPING_BAGS.values().toArray(new Block[0])).build(null)
          .setRegistryName(ComfortsMod.MOD_ID, "sleeping_bag");
      BlockEntityType<?> hammock = BlockEntityType.Builder
          .of(HammockTileEntity::new, ComfortsRegistry.HAMMOCKS.values().toArray(new Block[0]))
          .build(null).setRegistryName(ComfortsMod.MOD_ID, "hammock");
      evt.getRegistry().registerAll(sleepingBag, hammock);
    }
  }
}
