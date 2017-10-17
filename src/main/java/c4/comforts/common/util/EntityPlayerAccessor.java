/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.util;

import c4.comforts.Comforts;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EntityPlayerAccessor {

    private static final Field SLEEPING = ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS");
    private static final Field SLEEP_TIMER = ReflectionHelper.findField(EntityPlayer.class, "sleepTimer", "field_71076_b");
    private static final Method SET_RENDER_SLEEP_OFFSET = ReflectionHelper.findMethod(EntityPlayer.class,"setRenderOffsetForSleep", "func_175139_a", EnumFacing.class);
    private static final Method SPAWN_SHOULDER_ENTITIES = ReflectionHelper.findMethod(EntityPlayer.class,"spawnShoulderEntities", "func_192030_dh");
    private static final Method SET_SIZE = ReflectionHelper.findMethod(Entity.class, "setSize", "func_70105_a", Float.TYPE, Float.TYPE);
    private static final Method BED_IN_RANGE = ReflectionHelper.findMethod(EntityPlayer.class, "bedInRange", "func_190774_a", BlockPos.class, EnumFacing.class);
    private static final Constructor<?> SLEEP_ENEMY_PREDICATE;

    static {

        Constructor<?> temp = null;

        try {
            Class<?> sleepPredicate = Class.forName("net.minecraft.entity.player.EntityPlayer$SleepEnemyPredicate");
            temp = sleepPredicate.getDeclaredConstructor(EntityPlayer.class);
            temp.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Comforts.logger.log(Level.ERROR, "Cannot find SleepEnemyPredicate");
        }

        SLEEP_ENEMY_PREDICATE = temp;
    }

    @SuppressWarnings("unchecked")
    public static Predicate<EntityMob> newSleepEnemyPredicate(EntityPlayer player) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return (Predicate<EntityMob>) SLEEP_ENEMY_PREDICATE.newInstance(player);
    }

    public static void setSize(EntityPlayer player, float width, float height) throws IllegalAccessException, InvocationTargetException {
        SET_SIZE.invoke(player, width, height);
    }

    public static void spawnShoulderEntities(EntityPlayer player) throws IllegalAccessException, InvocationTargetException {
        SPAWN_SHOULDER_ENTITIES.invoke(player);
    }

    public static boolean bedInRange(EntityPlayer player, BlockPos pos, EnumFacing enumFacing) throws IllegalAccessException, InvocationTargetException {
        return (Boolean) BED_IN_RANGE.invoke(player, pos, enumFacing);
    }

    public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing enumFacing) throws IllegalAccessException, InvocationTargetException {
        SET_RENDER_SLEEP_OFFSET.invoke(player, enumFacing);
    }

    public static void setSleeping(EntityPlayer player, boolean sleeping) throws IllegalAccessException {
        SLEEPING.setBoolean(player, sleeping);
    }

    public static void setSleepTimer(EntityPlayer player, int timer) throws IllegalAccessException {
        SLEEP_TIMER.setInt(player, timer);
    }
}
