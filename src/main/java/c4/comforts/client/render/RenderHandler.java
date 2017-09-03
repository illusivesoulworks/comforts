package c4.comforts.client.render;

import c4.comforts.blocks.BlockSleepingBag;
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
            if (player.isPlayerSleeping() && player.world.getBlockState(player.bedLocation).getBlock() instanceof BlockSleepingBag) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0F, -0.375F, 0F);
                this.pop = true;
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
