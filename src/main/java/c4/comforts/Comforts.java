/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts;

import c4.comforts.api.ComfortsRegistry;
import c4.comforts.proxy.CommonProxy;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Comforts.MODID,
        name = Comforts.MODNAME,
        version = Comforts.MODVER,
        dependencies = "required-after:forge@[14.21.1.2387,);after:morpheus;after:toughasnails",
        guiFactory = "c4." + Comforts.MODID + ".client.gui.GuiFactory",
        acceptedMinecraftVersions = "[1.12, 1.13)",
        certificateFingerprint = "5d5b8aee896a4f5ea3f3114784742662a67ad32f")

public class Comforts {

        public static final String MODID = "comforts";
        public static final String MODNAME = "Comforts";
        public static final String MODVER = "1.1.3";

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

        @Mod.EventHandler
        public void onMessageReceived(FMLInterModComms.IMCEvent evt) {
            for (FMLInterModComms.IMCMessage message : evt.getMessages()) {
                String key = message.key;
                if (key.equalsIgnoreCase("mobSleepFilter")) {
                    message.getFunctionValue(EntityMob.class, Boolean.class).ifPresent(ComfortsRegistry::addMobSleepFilter);
                }
            }
        }

        @Mod.EventHandler
        public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
            FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
        }
}
