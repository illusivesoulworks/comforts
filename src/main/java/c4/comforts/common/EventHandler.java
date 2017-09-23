/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.common;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

public class EventHandler {

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent e) {

        World world = e.getEntityPlayer().getEntityWorld();
        Block block = world.getBlockState(e.getNewSpawn()).getBlock();

        if (!world.isRemote && (block instanceof BlockSleepingBag || block instanceof BlockHammock)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void sleepingBagWakeUp(PlayerWakeUpEvent e) {

        EntityPlayer player = e.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.bedLocation;
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockSleepingBag) {

//            if (Loader.isModLoaded("toughasnails") && ConfigHandler.warmBody) {
//                warmBody(player);
//            }

            if (!ConfigHandler.autoPickUp) { return; }

            ItemStack stack = state.getBlock().getItem(world, pos, state);

            if (!world.isRemote) {
                BlockPos pos1 = pos.offset(state.getValue(BlockSleepingBag.FACING).getOpposite());
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
            }

            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

//    @Optional.Method(modid = "toughasnails")
//    public void warmBody(EntityPlayer player) {
//        ITemperature playerTemp = TemperatureHelper.getTemperatureData(player);
//        int temp = playerTemp.getTemperature().getRawValue();
//        if (temp < 10) {
//            temp += Math.min(5, 10 - temp);
//        }
//        playerTemp.setTemperature(new Temperature(temp));
//    }
}
