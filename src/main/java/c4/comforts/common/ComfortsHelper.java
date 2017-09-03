package c4.comforts.common;

import c4.comforts.blocks.BlockSleepingBag;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class ComfortsHelper {

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent e) {

        EntityPlayer player = e.getEntityPlayer();

        if (!player.world.isRemote && player.world.getBlockState(e.getNewSpawn()).getBlock() instanceof BlockSleepingBag) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent e) {

        if (!ConfigHandler.autoPickUp) { return; }

        EntityPlayer player = e.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.bedLocation;
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockSleepingBag) {
            ItemStack stack = state.getBlock().getItem(world, pos, state);

            if (!world.isRemote) {
                BlockPos pos1 = pos.offset(state.getValue(BlockSleepingBag.FACING).getOpposite());
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
            }

            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    public static int getColor(int metadata) {
        return ItemDye.DYE_COLORS[15 - metadata];
    }
}
