package c4.comforts.proxy;

import c4.comforts.common.ConfigHandler;
import c4.comforts.blocks.ComfortsBlocks;
import c4.comforts.blocks.BlockSleepingBag;
import c4.comforts.common.ComfortsHelper;
import c4.comforts.items.ItemSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        MinecraftForge.EVENT_BUS.register(new ComfortsHelper());
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {
        for (BlockSleepingBag sleepingBag : ComfortsBlocks.sleepingBags)
        {
            e.getRegistry().register(sleepingBag);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemSleepingBag());
    }
}
