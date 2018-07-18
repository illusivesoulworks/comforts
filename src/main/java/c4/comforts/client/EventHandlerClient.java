/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.client;

import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre evt) {
        final EntityPlayer player = evt.getEntityPlayer();
        if (player instanceof EntityOtherPlayerMP && player.isPlayerSleeping() && player.world.getBlockState(player.bedLocation).getBlock() instanceof BlockSleepingBag) {
            player.renderOffsetY = -0.5F;
        }
    }
}
