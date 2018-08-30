/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common;

import c4.comforts.Comforts;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

public class EventHandlerCommon {

    public static final Method WAKE_ALL_PLAYERS = ReflectionHelper.findMethod(WorldServer.class, "wakeAllPlayers",
            "func_73053_d");

    @SubscribeEvent
    public void onPreWorldTick(TickEvent.WorldTickEvent evt) {

        if (evt.phase == TickEvent.Phase.START && evt.world instanceof WorldServer) {
            WorldServer world = (WorldServer) evt.world;

            if (world.areAllPlayersAsleep()) {
                boolean inHammock = false;

                if (world.getGameRules().getBoolean("doDaylightCycle")) {

                    for (EntityPlayer entityplayer : world.playerEntities) {
                        BlockPos bedLocation = entityplayer.bedLocation;

                        if (entityplayer.isPlayerFullyAsleep() && bedLocation != null
                                && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock) {
                            inHammock = true;
                            long i = world.getWorldTime() + 24000L;
                            world.setWorldTime((i - i % 24000L) - 12001L);
                            break;
                        }
                    }
                }

                if (inHammock) {

                    try {
                        WAKE_ALL_PLAYERS.invoke(world);
                    } catch (Exception e) {
                        Comforts.logger.log(Level.ERROR, "Error trying to wake all players!" + e.getMessage());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void allowDaytimeNapping(SleepingTimeCheckEvent evt) {
        World world = evt.getEntityPlayer().getEntityWorld();
        long worldTime = world.getWorldTime() % 24000L;
        if (world.getBlockState(evt.getSleepingLocation()).getBlock() instanceof BlockHammock
                && (worldTime > 500L && worldTime < 11500L)) {
            evt.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void stopResting(EntityMountEvent evt) {

        if (evt.isDismounting()) {
            Entity entity = evt.getEntityBeingMounted();

            if (entity instanceof EntityRest) {
                TileEntity tileentity = evt.getWorldObj().getTileEntity(entity.getPosition());

                if (tileentity instanceof TileEntityHammock) {
                    TileEntityHammock tileentityhammock = (TileEntityHammock) tileentity;
                    tileentityhammock.setOccupied(false);
                }
                entity.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
        World world = evt.getEntityPlayer().getEntityWorld();

        if (evt.getNewSpawn() != null) {
            Block block = world.getBlockState(evt.getNewSpawn()).getBlock();

            if (!world.isRemote && (block instanceof BlockSleepingBag || block instanceof BlockHammock)) {
                evt.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();

        if (!player.world.isRemote
                && player.world.getBlockState(player.bedLocation).getBlock() instanceof BlockSleepingBag) {

            if (!ComfortsUtil.getDebuffs().isEmpty()) {
                ComfortsUtil.applyDebuffs(player);
            }
        }
    }
}
