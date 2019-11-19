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

package top.theillusivec4.comforts.integration;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.quetzi.morpheus.Morpheus;
import top.theillusivec4.comforts.common.block.HammockBlock;

public class MorpheusIntegration {

  public static void register() {
    Morpheus.register.registerHandler(() -> {
      ServerWorld world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);

      if (!HammockBlock.skipToNight(world)) {
        long i = world.getDayTime() + 24000L;
        world.setDayTime(i - i % 24000L);
      }
    }, 0);
  }
}
