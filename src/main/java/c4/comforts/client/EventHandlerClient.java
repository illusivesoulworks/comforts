/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.client;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre evt) {
        final EntityPlayer player = evt.getEntityPlayer();
        if (player instanceof EntityOtherPlayerMP && player.isPlayerSleeping()) {
            Block bed = player.world.getBlockState(player.bedLocation).getBlock();
            if (bed instanceof BlockSleepingBag) {
                player.renderOffsetY = -0.375F;
            } else if (bed instanceof BlockHammock) {
                player.renderOffsetY = -0.5F;
            }
        }
    }
}
