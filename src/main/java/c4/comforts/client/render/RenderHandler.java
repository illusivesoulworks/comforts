/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.client.render;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHandler {

    private boolean pop;

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre e) {
        if (e.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e.getEntity();
            if (player.isPlayerSleeping()) {
                Block block = player.world.getBlockState(player.bedLocation).getBlock();
                if (block instanceof BlockSleepingBag) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0F, -0.375F, 0F);
                    this.pop = true;
                } else if (block instanceof BlockHammock) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0F, -0.5F, 0F);
                    this.pop = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post e) {
        if (this.pop) {
            this.pop = false;
            GlStateManager.popMatrix();
        }
    }
}
