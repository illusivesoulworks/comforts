/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.proxy;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.client.render.RenderHandler;
import c4.comforts.common.items.ComfortsItems;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {

        super.init(e);

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

        itemColors.registerItemColorHandler(ComfortsItems.SLEEPING_BAG.getColorFromItemstack(), ComfortsItems.SLEEPING_BAG);
        itemColors.registerItemColorHandler(ComfortsItems.HAMMOCK.getColorFromItemstack(), ComfortsItems.HAMMOCK);

        for (BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS) {
            blockColors.registerBlockColorHandler(sleepingBag.colorMultiplier(), sleepingBag);
        }

        for (BlockHammock hammock : ComfortsBlocks.HAMMOCKS) {
            blockColors.registerBlockColorHandler(hammock.colorMultiplier(), hammock);
        }

        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        ComfortsBlocks.initModels();
        ComfortsItems.initModels();
    }
}