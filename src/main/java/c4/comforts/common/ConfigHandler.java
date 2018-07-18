/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common;

import c4.comforts.Comforts;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Comforts.MODID)
public class ConfigHandler {

    @Name("Auto Use Sleeping Bags")
    @Comment("Set to true to automatically use sleeping bags when placed")
    @RequiresMcRestart
    public static boolean autoUse = true;

    @Name("Well-Rested")
    @Comment("Set to true to prevent sleeping depending on how long you previously slept")
    @RequiresMcRestart
    public static boolean wellRested = false;

    @Name("Sleepy Factor")
    @Comment("If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)")
    @RangeDouble(min = 1.0D, max = 20.0D)
    public static float sleepyRatio = 2.0F;

    @Name("Leisure Hammocks")
    @Comment("Set to true to enable relaxing in hammocks without sleeping")
    public static boolean restHammocks = false;

    @Name("Sleeping Bag Debuffs")
    @Comment({"List of debuffs to apply to players after using the sleeping bag",
            "Format: [effect] [duration(secs)] [power]"})
    public static String[] sleepingBagDebuffs = new String[]{};

    public static final ToughAsNails toughasnails = new ToughAsNails();

    public static class ToughAsNails {

        @Name("Insulated Sleeping Bags")
        @Comment("Set to true to have sleeping bags slightly warm your body if you're cold")
        @RequiresMcRestart
        public boolean warmBody = false;
    }

    @Mod.EventBusSubscriber(modid = Comforts.MODID)
    private static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
            if (evt.getModID().equals(Comforts.MODID)) {
                ConfigManager.sync(Comforts.MODID, Config.Type.INSTANCE);
                ComfortsUtil.parseDebuffs();
            }
        }
    }
}
