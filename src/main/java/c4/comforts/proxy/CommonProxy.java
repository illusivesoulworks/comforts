/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.proxy;

import c4.comforts.Comforts;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.EventHandlerCommon;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockRope;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.capability.CapabilitySleepTime;
import c4.comforts.common.capability.CapabilitySleeping;
import c4.comforts.common.capability.CapabilityWellRested;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.items.ItemHammock;
import c4.comforts.common.items.ItemSleepingBag;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsUtil;
import c4.comforts.integrations.morpheus.MorpheusDayHandler;
import c4.comforts.integrations.toughasnails.EventHandlerTAN;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.quetzi.morpheus.Morpheus;

@Mod.EventBusSubscriber
public class CommonProxy {

    public static Configuration config;

    public void preInit(FMLPreInitializationEvent evt) {
        ComfortsBlocks.preInit();
        ComfortsUtil.parseDebuffs();
    }

    public void init(FMLInitializationEvent evt) {

        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        CapabilitySleepTime.register();
        CapabilityWellRested.register();
        CapabilitySleeping.register();
        if (Loader.isModLoaded("morpheus")) {
            morpheusInit();
        }
        if (Loader.isModLoaded("toughasnails") && ConfigHandler.toughasnails.warmBody) {
            MinecraftForge.EVENT_BUS.register(new EventHandlerTAN());
        }
    }

    public void postInit(FMLPostInitializationEvent evt) {}

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().register(new BlockRope());

        for (BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS) {
            evt.getRegistry().register(sleepingBag);
        }

        for (BlockHammock hammock : ComfortsBlocks.HAMMOCKS) {
            evt.getRegistry().register(hammock);
        }

        GameRegistry.registerTileEntity(TileEntityHammock.class, new ResourceLocation(Comforts.MODID, "tile_entity_hammock"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().register(new ItemSleepingBag());
        evt.getRegistry().register(new ItemHammock());
        evt.getRegistry().register(new ItemBlock(ComfortsBlocks.ROPE).setRegistryName("rope"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> evt) {
        int id = 1;
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityRest.class)
                .id(new ResourceLocation(Comforts.MODID, "_entity_rest"), id++)
                .name("entity_rest")
                .tracker(256, 20, false)
                .build();
        evt.getRegistry().register(entry);
    }

    @Optional.Method(modid = "morpheus")
    private static void morpheusInit() {

        Morpheus.register.registerHandler(new MorpheusDayHandler(), 0);
    }
}
