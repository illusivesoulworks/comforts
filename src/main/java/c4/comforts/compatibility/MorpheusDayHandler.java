/*
 * Copyright (c) 2017. C4, MIT License
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
