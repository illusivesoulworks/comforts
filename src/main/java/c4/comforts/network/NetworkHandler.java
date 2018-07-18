/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
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

    @SuppressWarnings("unchecked")
    private static void registerMessage(Class messageHandler, Class requestMessageType, Side side) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, id++, side);
    }
}
