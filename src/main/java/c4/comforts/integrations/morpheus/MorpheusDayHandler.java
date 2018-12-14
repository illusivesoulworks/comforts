/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.integrations.morpheus;

import c4.comforts.common.blocks.BlockHammock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.quetzi.morpheus.api.INewDayHandler;

@Optional.Interface(iface = "net.quetzi.morpheus.api.INewDayHandler", modid = "morpheus", striprefs=true)
public class MorpheusDayHandler implements INewDayHandler {

    @Override
    public void startNewDay() {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        boolean skipToNight = false;

        for (EntityPlayer entityplayer : world.playerEntities) {
            BlockPos bedLocation = entityplayer.bedLocation;

            if (entityplayer.isPlayerFullyAsleep() && bedLocation != null && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock) {
                long worldTime = world.getWorldTime() % 24000L;

                if (worldTime > 500L && worldTime < 11500L) {
                    skipToNight = true;
                }
                break;
            }
        }

        long worldTime = world.getWorldTime();
        long i = worldTime + 24000L;

        if (skipToNight) {
            world.setWorldTime((i - i % 24000L) - 12001L);
        } else {
            world.setWorldTime(i - i % 24000L);
        }
    }
}
