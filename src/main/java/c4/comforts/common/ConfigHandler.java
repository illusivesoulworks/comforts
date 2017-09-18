package c4.comforts.common;

import c4.comforts.Comforts;
import c4.comforts.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

public class ConfigHandler {

    private static final String CATEGORY_GENERAL = "general";

    public static boolean autoPickUp = true;
    public static boolean autoUse = true;
    public static boolean warmBody = false;
    public static Configuration cfg;

    public static void readConfig() {
        try {
            cfg = CommonProxy.config;
            cfg.load();
            initConfig();
        } catch (Exception e1) {
            Comforts.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initConfig() {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        autoPickUp = cfg.getBoolean("Auto Pick-Up Sleeping Bag", CATEGORY_GENERAL, autoPickUp, "Set to true to automatically pick up sleeping bags after sleeping");
//        autoUse = cfg.getBoolean("Auto Use Sleeping Bag", CATEGORY_GENERAL, autoUse, "Set to true to automatically use sleeping bags");
        if (Loader.isModLoaded("toughasnails")) {
            warmBody = cfg.getBoolean("Insulated Sleeping Bags", CATEGORY_GENERAL, warmBody, "Set to true to have sleeping bags slightly warm your body if you're cold");
        }
    }

    @Mod.EventBusSubscriber
    private static class ConfigChangeHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
            if (e.getModID().equals(Comforts.MODID)) {
                initConfig();

                if (cfg.hasChanged()) {
                    cfg.save();
                }
            }
        }
    }
}
