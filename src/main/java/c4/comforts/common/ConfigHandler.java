/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common;

import c4.comforts.Comforts;
import c4.comforts.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static final String CATEGORY_GENERAL = "general";

    public static boolean autoPickUp = true;
    public static boolean autoUse = true;
    public static boolean warmBody = true;
    public static boolean wellRested = false;
    public static float sleepyRatio = 2.0F;
    public static boolean restHammocks = false;
    public static String[] sleepingBagDebuffs = new String[]{};
    public static Configuration cfg;

    private static List<PotionEffect> debuffs = new ArrayList<>();

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
        autoUse = cfg.getBoolean("Auto Use Sleeping Bag", CATEGORY_GENERAL, autoUse, "Set to true to automatically use sleeping bags");
        wellRested = cfg.getBoolean("Well Rested", CATEGORY_GENERAL, wellRested, "Set to true to activate an effect that prevents sleeping depending on how long you previously slept");
        sleepyRatio = cfg.getFloat("Sleepy Factor", CATEGORY_GENERAL, sleepyRatio, 1.0F, 20.0F, "If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)");
        restHammocks = cfg.getBoolean("Leisure Hammocks", CATEGORY_GENERAL, restHammocks, "Set to true to enable relaxing in hammocks without sleeping");
        if (Loader.isModLoaded("toughasnails")) {
            warmBody = cfg.getBoolean("Insulated Sleeping Bags", CATEGORY_GENERAL, warmBody, "Set to true to have sleeping bags slightly warm your body if you're cold");
        }
        sleepingBagDebuffs = cfg.getStringList("Sleeping Bag Debuffs", CATEGORY_GENERAL, sleepingBagDebuffs, "List of debuffs to apply to players after using the sleeping bag\n" +"Format: [effect] [duration(secs)] [power]");
        parseDebuffs();
    }

    public static List<PotionEffect> getDebuffs() {
        return debuffs;
    }

    private static void parseDebuffs() {

        for (String s : sleepingBagDebuffs) {

            String[] elements = s.split("\\s+");
            Potion potion = Potion.getPotionFromResourceLocation(elements[0]);
            if (potion == null) continue;
            int duration = 0;
            int amp = 0;
            try {
                duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
                amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
            } catch (Exception e1) {
                Comforts.logger.log(Level.ERROR, "Problem parsing sleeping bag debuffs in config!", e1);
            }
            debuffs.add(new PotionEffect(potion, duration * 20, amp - 1));
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
