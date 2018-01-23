/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.util;

import c4.comforts.Comforts;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.network.NetworkHandler;
import c4.comforts.network.SPacketSleep;
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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Level;

import java.util.List;

public class SleepHelper {

    public static void advanceTime(World worldIn) {

        long worldTime = worldIn.getWorldTime();
        long i = worldIn.getWorldTime() + 24000L;

        if (worldTime % 24000L >= 12000L) {
            worldIn.setWorldTime(i - i % 24000L);
        } else {
            worldIn.setWorldTime((i - i % 24000L) - 12001L);
        }
    }

    public static boolean notAllowedToSleep(EntityPlayer player, BlockPos bedLocation) {

        Block bedBlock = player.world.getBlockState(bedLocation).getBlock();
        long worldTime = player.world.getWorldTime() % 24000L;

        if (bedBlock instanceof BlockSleepingBag || bedBlock == Blocks.BED) {
            return player.world.isDaytime();
        }

        return !(bedBlock instanceof BlockHammock && worldTime > 500L && worldTime < 11500L);
    }

    public static EntityPlayer.SleepResult trySleep(EntityPlayer player, BlockPos bedLocation, boolean autoSleep)
    {
        EntityPlayer.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, bedLocation);
        if (ret != null) return ret;
        EnumFacing enumfacing;
        enumfacing = autoSleep ? player.getHorizontalFacing() : player.world.getBlockState(bedLocation).getValue(BlockHorizontal.FACING);

        if (!player.world.isRemote)
        {
            if (player.isPlayerSleeping() || !player.isEntityAlive())
            {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (!player.world.provider.isSurfaceWorld())
            {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
            }

            if (!autoSleep) {

                if (notAllowedToSleep(player, bedLocation))
                {
                    return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
                }

                try {
                    if (!EntityPlayerAccessor.bedInRange(player, bedLocation, enumfacing)) {
                        return EntityPlayer.SleepResult.TOO_FAR_AWAY;
                    }
                } catch (Exception e) {
                    Comforts.logger.log(Level.ERROR, "Failed to invoke method bedInRange");
                }

            } else if (player.world.isDaytime()) {

                return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            try {
                List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double)bedLocation.getX() - d0, (double)bedLocation.getY() - d1, (double)bedLocation.getZ() - d0, (double)bedLocation.getX() + d0, (double)bedLocation.getY() + d1, (double)bedLocation.getZ() + d0), EntityPlayerAccessor.newSleepEnemyPredicate(player));
                if (!list.isEmpty())
                {
                    return EntityPlayer.SleepResult.NOT_SAFE;
                }
            } catch (Exception e) {
                Comforts.logger.log(Level.ERROR, "Failed to invoke new SleepEnemyPredicate");
            }
        }

        if (player.isRiding())
        {
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
        float height = player.world.getBlockState(bedLocation).getBlock() instanceof BlockHammock ? 0.1875F : 0.3125F;
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

        if (!player.world.isRemote)
        {
            player.world.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.SleepResult.OK;
    }

    public static EntityPlayer.SleepResult goToSleep(EntityPlayer player, BlockPos bedLocation, boolean autoSleep) {

        EntityPlayer.SleepResult entityplayer$sleepresult = trySleep(player, bedLocation, autoSleep);

        if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            playerMP.addStat(StatList.SLEEP_IN_BED);
            SPacketSleep sleepPacket = new SPacketSleep(playerMP, bedLocation, autoSleep);
            playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, playerMP.rotationYaw, playerMP.rotationPitch);
            for (EntityPlayer trackingPlayer : playerMP.getServerWorld().getEntityTracker().getTrackingPlayers(playerMP)) {
                NetworkHandler.INSTANCE.sendTo(sleepPacket, (EntityPlayerMP) trackingPlayer);
            }
            NetworkHandler.INSTANCE.sendTo(sleepPacket, playerMP);
        }

        return entityplayer$sleepresult;
    }
}
