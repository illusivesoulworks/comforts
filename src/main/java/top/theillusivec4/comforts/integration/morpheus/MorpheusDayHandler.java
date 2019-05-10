/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.integration.morpheus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.quetzi.morpheus.api.INewDayHandler;
import top.theillusivec4.comforts.common.block.BlockHammock;

public class MorpheusDayHandler implements INewDayHandler {

    @Override
    public void startNewDay() {
        World world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
        boolean skipToNight = false;

        for (EntityPlayer entityplayer : world.playerEntities) {
            BlockPos bedLocation = entityplayer.bedLocation;

            if (entityplayer.isPlayerFullyAsleep() && bedLocation != null && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock) {
                long worldTime = world.getDayTime() % 24000L;

                if (worldTime > 500L && worldTime < 11500L) {
                    skipToNight = true;
                }
                break;
            }
        }
        long worldTime = world.getDayTime();
        long i = worldTime + 24000L;

        if (skipToNight) {
            world.setDayTime((i - i % 24000L) - 12001L);
        } else {
            world.setDayTime(i - i % 24000L);
        }
    }
}
