/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts;

import c4.comforts.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Comforts.MODID,
        name = Comforts.MODNAME,
        version = Comforts.MODVER,
        dependencies = "required-after:forge@[14.22.1.2478,)",
        useMetadata = true,
        guiFactory = "c4." + Comforts.MODID + ".client.gui.GuiFactory",
        acceptedMinecraftVersions = "[1.12.1, 1.13)")

public class Comforts {

        public static final String MODID = "comforts";
        public static final String MODNAME = "Comforts";
        public static final String MODVER = "0.0.7";

        @SidedProxy(clientSide = "c4.comforts.proxy.ClientProxy", serverSide = "c4.comforts.proxy.CommonProxy")
        public static CommonProxy proxy;

        @Mod.Instance
        public static Comforts instance;

        public static Logger logger;

        @Mod.EventHandler
        public void preInit(FMLPreInitializationEvent e) {
            logger = e.getModLog();
            proxy.preInit(e);
        }

        @Mod.EventHandler
        public void init(FMLInitializationEvent e) {
            proxy.init(e);
        }

        @Mod.EventHandler
        public void postInit(FMLPostInitializationEvent e) {
            proxy.postInit(e);
        }
}
