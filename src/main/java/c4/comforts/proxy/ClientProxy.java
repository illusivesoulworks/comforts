/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.proxy;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.items.ComfortsItems;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

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
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        ComfortsBlocks.initModels();
        ComfortsItems.initModels();
    }
}