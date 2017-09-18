/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.proxy;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockRope;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.EventHandler;
import c4.comforts.compatibility.MorpheusDayHandler;
import c4.comforts.common.items.ItemHammock;
import c4.comforts.common.items.ItemSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    }

    @Optional.Method(modid = "morpheus")
    public void morpheusInit(FMLInitializationEvent e) {

        Morpheus.register.registerHandler(new MorpheusDayHandler(), 0);
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
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemSleepingBag());
        e.getRegistry().register(new ItemHammock());
        e.getRegistry().register(new ItemBlock(ComfortsBlocks.ROPE).setRegistryName("rope"));
    }
}
