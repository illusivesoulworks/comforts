/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.proxy;

import c4.comforts.client.EventHandlerClient;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.items.ComfortsItems;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.network.NetworkHandler;
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
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        itemColors.registerItemColorHandler(ComfortsItems.SLEEPING_BAG.getColorFromItemstack(), ComfortsItems.SLEEPING_BAG);
        itemColors.registerItemColorHandler(ComfortsItems.HAMMOCK.getColorFromItemstack(), ComfortsItems.HAMMOCK);

        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

        for (BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS) {
            blockColors.registerBlockColorHandler(sleepingBag.colorMultiplier(), sleepingBag);
        }

        for (BlockHammock hammock : ComfortsBlocks.HAMMOCKS) {
            blockColors.registerBlockColorHandler(hammock.colorMultiplier(), hammock);
        }
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        ComfortsBlocks.initModels();
        ComfortsItems.initModels();
    }
}