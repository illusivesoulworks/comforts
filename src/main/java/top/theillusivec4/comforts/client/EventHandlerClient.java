package top.theillusivec4.comforts.client;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.common.block.BlockHammock;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;

public class EventHandlerClient {

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre evt) {
        final EntityPlayer player = evt.getEntityPlayer();
        if (player instanceof EntityOtherPlayerMP && player.isPlayerSleeping() && player.bedLocation != null) {
            Block bed = player.world.getBlockState(player.bedLocation).getBlock();
            if (bed instanceof BlockSleepingBag) {
                player.renderOffsetY = -0.375F;
            } else if (bed instanceof BlockHammock) {
                player.renderOffsetY = -0.5F;
            }
        }
    }
}
