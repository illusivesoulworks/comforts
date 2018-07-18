/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.api;

import net.minecraft.entity.monster.EntityMob;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComfortsRegistry {

    public static final List<Function<EntityMob, Boolean>> mobSleepFilters = new ArrayList<>();

    public static void addMobSleepFilter(Function<EntityMob, Boolean> filter) {
        mobSleepFilters.add(filter);
    }
}
