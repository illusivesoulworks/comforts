/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.proxy;

import c4.comforts.Comforts;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockRope;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.EventHandler;
import c4.comforts.common.capability.CapabilityHandler;
import c4.comforts.common.capability.IWellRested;
import c4.comforts.common.capability.WellRested;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.compatibility.MorpheusDayHandler;
import c4.comforts.common.items.ItemHammock;
import c4.comforts.common.items.ItemSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
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

import java.io.File;

@Mod.EventBusSubscriber
public class CommonProxy {

    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "comforts.cfg"));
        ConfigHandler.readConfig();
        ComfortsBlocks.preInit();
    }

    public void init(FMLInitializationEvent e) {

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        CapabilityManager.INSTANCE.register(IWellRested.class, new WellRested.Storage(), WellRested.class);
        if (Loader.isModLoaded("morpheus")) {
            morpheusInit();
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {

        e.getRegistry().register(new BlockRope());

        for (BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS)
        {
            e.getRegistry().register(sleepingBag);
        }

        for (BlockHammock hammock : ComfortsBlocks.HAMMOCKS)
        {
            e.getRegistry().register(hammock);
        }

        GameRegistry.registerTileEntity(TileEntityHammock.class, Comforts.MODID + ".tile_entity_hammock");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemSleepingBag());
        e.getRegistry().register(new ItemHammock());
        e.getRegistry().register(new ItemBlock(ComfortsBlocks.ROPE).setRegistryName("rope"));
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
