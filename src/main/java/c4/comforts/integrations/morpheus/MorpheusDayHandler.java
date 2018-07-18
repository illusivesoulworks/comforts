/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
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
        boolean inHammock = false;
        for (EntityPlayer entityplayer : world.playerEntities)
        {
            BlockPos bedLocation = entityplayer.bedLocation;
            if (entityplayer.isPlayerFullyAsleep() && bedLocation != null && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock)
            {
                inHammock = true;
                break;
            }
        }

        long worldTime = world.getWorldTime();
        long i = worldTime + 24000L;

        if (inHammock) {
            world.setWorldTime((i - i % 24000L) - 12001L);
        } else {
            world.setWorldTime(i - i % 24000L);
        }
    }
}
