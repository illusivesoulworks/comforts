/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
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
