/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.asm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ASMHooks {

    public static boolean cannotSleep(EntityPlayer player) {
        World world = player.world;
        long worldTime = world.getWorldTime() % 24000L;
        return world.isDaytime() && !(worldTime > 500L && worldTime < 11500L);
    }
}
