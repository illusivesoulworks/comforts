/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.common.capability;

import c4.comforts.Comforts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {

    private static final ResourceLocation WELL_RESTED_CAP = new ResourceLocation(Comforts.MODID,"wellRested");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> e) {

        if (!((e.getObject()) instanceof EntityPlayer)) { return;}

        e.addCapability(WELL_RESTED_CAP, new WellRested.Provider());
    }
}