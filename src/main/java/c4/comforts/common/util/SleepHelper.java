/*
 * Copyright (c) 2017. C4, MIT License
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

    public static boolean notTimeToSleep(EntityPlayer player) {

        World world = player.world;
        long worldTime = world.getWorldTime() % 24000L;

        return world.isDaytime() && !(worldTime > 500L && worldTime < 11500L);
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

        IBlockState state = null;
        if (player.world.isBlockLoaded(bedLocation)) state = player.world.getBlockState(bedLocation);
        if (state != null && state.getBlock().isBed(state, player.world, bedLocation, player)) {
            float f1 = 0.5F + (float)enumfacing.getFrontOffsetX() * 0.4F;
            float f = 0.5F + (float)enumfacing.getFrontOffsetZ() * 0.4F;
            try {
                EntityPlayerAccessor.setRenderOffsetForSleep(player, enumfacing);
            } catch (Exception e) {
                Comforts.logger.log(Level.ERROR, "Failed to invoke method setRenderOffsetForSleep");
            }
            player.setPosition((double)((float)bedLocation.getX() + f1), (double)((float)bedLocation.getY() + 0.6875F), (double)((float)bedLocation.getZ() + f));
        }
        else
        {
            player.setPosition((double)((float)bedLocation.getX() + 0.5F), (double)((float)bedLocation.getY() + 0.6875F), (double)((float)bedLocation.getZ() + 0.5F));
        }

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
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            playerMP.addStat(StatList.SLEEP_IN_BED);
            SPacketSleep sleepPacket = new SPacketSleep(bedLocation, autoSleep);
            NetworkHandler.INSTANCE.sendTo(sleepPacket, playerMP);
        }

        return EntityPlayer.SleepResult.OK;
    }
}
