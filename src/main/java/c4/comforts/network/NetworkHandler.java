/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.network;

import c4.comforts.Comforts;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {


    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Comforts.MODID);

    private static int id = 0;

    public static void init() {
        NetworkHandler.registerMessage(SPacketSleep.SPacketSleepHandler.class, SPacketSleep.class, Side.CLIENT);
    }

    private static void registerMessage(Class messageHandler, Class requestMessageType, Side side) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, id++, side);
    }
}
