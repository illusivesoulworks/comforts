/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.comforts.Comforts;

import java.util.ArrayList;
import java.util.List;

public class ComfortsConfig {

    private static final String CONFIG_PREFIX = "gui." + Comforts.MODID + ".config.";

    public static class Server {

        public final ForgeConfigSpec.BooleanValue autoUse;
        public final ForgeConfigSpec.BooleanValue wellRested;
        public final ForgeConfigSpec.DoubleValue sleepyFactor;
        public final ForgeConfigSpec.BooleanValue nightHammocks;
        public final ForgeConfigSpec.DoubleValue sleepingBagBreakage;
        public final ForgeConfigSpec.ConfigValue<List<String>> sleepingBagDebuffs;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            autoUse = builder
                    .comment("Set to true to automatically use sleeping bags when placed")
                    .translation(CONFIG_PREFIX + "autoUse")
                    .define("autoUse", true);

            wellRested = builder
                    .comment("Set to true to prevent sleeping depending on how long you previously slept")
                    .translation(CONFIG_PREFIX + "wellRested")
                    .define("wellRested", false);

            sleepyFactor = builder
                    .comment("If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)")
                    .translation(CONFIG_PREFIX + "sleepyFactor")
                    .defineInRange("sleepyFactor", 2.0D, 1.0D, 20.0D);

            nightHammocks = builder
                    .comment("Set to true to enable sleeping in hammocks at night")
                    .translation(CONFIG_PREFIX + "nightHammocks")
                    .define("nightHammocks", false);

            sleepingBagBreakage = builder
                    .comment("The chance that a sleeping bag will break upon usage")
                    .translation(CONFIG_PREFIX + "sleepingBagBreakage")
                    .defineInRange("sleepingBagBreakage", 0.0D, 0.0D, 1.0D);

            sleepingBagDebuffs = builder
                    .comment("List of debuffs to apply to players after using the sleeping bag\n" +
                            "Format: [effect] [duration(secs)] [power]")
                    .translation(CONFIG_PREFIX + "sleepingBagDebuffs")
                    .worldRestart()
                    .define("sleepingBagDebuffs", new ArrayList<>());
        }
    }

    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
