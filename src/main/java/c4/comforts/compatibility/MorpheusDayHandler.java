/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.compatibility;

import c4.comforts.common.util.SleepHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.quetzi.morpheus.api.INewDayHandler;

@Optional.Interface(iface = "net.quetzi.morpheus.api.INewDayHandler", modid = "morpheus", striprefs=true)
public class MorpheusDayHandler implements INewDayHandler {

    @Override
    public void startNewDay() {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        SleepHelper.advanceTime(world);
    }
}
