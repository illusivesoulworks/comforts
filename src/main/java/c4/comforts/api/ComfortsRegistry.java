/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.api;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComfortsRegistry {

    public static final List<Function<EntityMob, Boolean>> mobSleepFilters = new ArrayList<>();

    public static void addMobSleepFilter(Function<EntityMob, Boolean> filter) {
        mobSleepFilters.add(filter);
    }
}
