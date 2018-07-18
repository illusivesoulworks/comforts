/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.util;

import c4.comforts.Comforts;
import c4.comforts.api.ComfortsRegistry;
import c4.comforts.client.gui.ComfortsTab;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.network.NetworkHandler;
import c4.comforts.network.SPacketSleep;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class ComfortsUtil {

    public static List<PotionEffect> debuffs = new ArrayList<>();

    public static final ComfortsTab comfortsTab = new ComfortsTab();

    public static int getColor(int metadata) {
        return ItemDye.DYE_COLORS[15 - metadata];
    }

    public static void applyDebuffs(EntityPlayer player) {
        for (PotionEffect effect : getDebuffs()) {
            player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
        }
    }

    public static boolean notAllowedToSleep(EntityPlayer player, BlockPos bedLocation) {
        Block bedBlock = player.world.getBlockState(bedLocation).getBlock();
        long worldTime = player.world.getWorldTime() % 24000L;
        return !(bedBlock instanceof BlockHammock && worldTime > 500L && worldTime < 11500L);
    }

    public static EntityPlayer.SleepResult trySleep(EntityPlayer player, BlockPos bedLocation) {
        EntityPlayer.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, bedLocation);

        if (ret != null) {
            return ret;
        }
        EnumFacing enumfacing = player.world.getBlockState(bedLocation).getValue(BlockHorizontal.FACING);

        if (!player.world.isRemote)
        {
            if (player.isPlayerSleeping() || !player.isEntityAlive()) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (!player.world.provider.isSurfaceWorld()) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
            }

            if (notAllowedToSleep(player, bedLocation)) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
            }

            try {

                if (!EntityPlayerAccessor.bedInRange(player, bedLocation, enumfacing)) {
                    return EntityPlayer.SleepResult.TOO_FAR_AWAY;
                }
            } catch (Exception e) {
                Comforts.logger.log(Level.ERROR, "Failed to invoke method bedInRange");
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) bedLocation.getX() - d0, (double) bedLocation.getY() - d1, (double) bedLocation.getZ() - d0, (double) bedLocation.getX() + d0, (double) bedLocation.getY() + d1, (double) bedLocation.getZ() + d0),
                    mob -> mob != null && mob.isPreventingPlayerRest(player) && ComfortsRegistry.mobSleepFilters.stream().allMatch(filter -> filter.apply(mob)));

            if (!list.isEmpty()) {
                return EntityPlayer.SleepResult.NOT_SAFE;
            }
        }

        if (player.isRiding()) {
            player.dismountRidingEntity();
        }

        try {
            EntityPlayerAccessor.spawnShoulderEntities(player);
            EntityPlayerAccessor.setSize(player, 0.2F, 0.2F);
        } catch (Exception e) {
            Comforts.logger.log(Level.ERROR, "Failed to invoke methods spawnShoulderEntities and setSize");
        }

        float f1 = 0.5F + (float)enumfacing.getFrontOffsetX() * 0.4F;
        float f = 0.5F + (float)enumfacing.getFrontOffsetZ() * 0.4F;

        try {
            EntityPlayerAccessor.setRenderOffsetForSleep(player, enumfacing);
        } catch (Exception e) {
            Comforts.logger.log(Level.ERROR, "Failed to invoke method setRenderOffsetForSleep");
        }
        float height = 0.1875F;
        player.setPosition((double)((float)bedLocation.getX() + f1), (double)((float)bedLocation.getY() + height), (double)((float)bedLocation.getZ() + f));

        try {
            EntityPlayerAccessor.setSleeping(player, true);
            EntityPlayerAccessor.setSleepTimer(player, 0);
        } catch (Exception e) {
            Comforts.logger.log(Level.ERROR, "Failed to invoke methods setSleeping and/or setSleepTimer");
        }
        player.bedLocation = bedLocation;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;

        if (!player.world.isRemote) {
            player.world.updateAllPlayersSleepingFlag();
        }
        return EntityPlayer.SleepResult.OK;
    }

    public static EntityPlayer.SleepResult goToSleep(EntityPlayer player, BlockPos bedLocation) {
        EntityPlayer.SleepResult entityplayer$sleepresult = trySleep(player, bedLocation);

        if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            playerMP.addStat(StatList.SLEEP_IN_BED);
            SPacketSleep sleepPacket = new SPacketSleep(playerMP, bedLocation);
            playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, playerMP.rotationYaw, playerMP.rotationPitch);

            for (EntityPlayer trackingPlayer : playerMP.getServerWorld().getEntityTracker().getTrackingPlayers(playerMP)) {
                NetworkHandler.INSTANCE.sendTo(sleepPacket, (EntityPlayerMP) trackingPlayer);
            }
            NetworkHandler.INSTANCE.sendTo(sleepPacket, playerMP);
        }
        return entityplayer$sleepresult;
    }

    public static List<PotionEffect> getDebuffs() {
        return debuffs;
    }

    public static void parseDebuffs() {

        for (String s : ConfigHandler.sleepingBagDebuffs) {

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
}
