package top.theillusivec4.comforts.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;

public class EventHandlerCommon {

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = evt.getNewSpawn();

        if (pos != null) {
            Block block = world.getBlockState(pos).getBlock();

            if (!world.isRemote && block instanceof BlockSleepingBag) {
                player.bedLocation = ObfuscationReflectionHelper.getPrivateValue(EntityPlayer.class, player, "field_71077_c");
                evt.setCanceled(true);
            }
        }
    }
}
