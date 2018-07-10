/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common;

import c4.comforts.Comforts;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.capability.IWellRested;
import c4.comforts.common.capability.WellRested;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.items.ItemSleepingBag;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

import java.lang.reflect.Method;

public class EventHandler {

    public static final Method WAKE_ALL_PLAYERS = ReflectionHelper.findMethod(WorldServer.class, "wakeAllPlayers", "func_73056_e");

    @SubscribeEvent
    public void onPreWorldTick(TickEvent.WorldTickEvent evt) {

        if (evt.phase == TickEvent.Phase.START && evt.world instanceof WorldServer) {
            WorldServer world = (WorldServer) evt.world;
            if (world.areAllPlayersAsleep())
            {
                boolean inHammock = false;
                if (world.getGameRules().getBoolean("doDaylightCycle"))
                {
                    for (EntityPlayer entityplayer : world.playerEntities)
                    {
                        BlockPos bedLocation = entityplayer.bedLocation;
                        if (entityplayer.isPlayerFullyAsleep() && bedLocation != null && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock)
                        {
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
    public void onPlayerRespawn(PlayerEvent.Clone e) {

        IWellRested newWellRested = e.getEntityPlayer().getCapability(WellRested.Provider.WELL_RESTED_CAP, null);
        IWellRested oldWellRested = e.getOriginal().getCapability(WellRested.Provider.WELL_RESTED_CAP, null);

        if (newWellRested != null && oldWellRested != null) {
            newWellRested.setSleepTime(oldWellRested.getSleepTime());
            newWellRested.setWakeTime(oldWellRested.getWakeTime());
            newWellRested.setTiredTime(oldWellRested.getTiredTime());
        }
    }

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent e) {

        World world = e.getEntityPlayer().getEntityWorld();

        if (e.getNewSpawn() != null) {
            Block block = world.getBlockState(e.getNewSpawn()).getBlock();

            if (!world.isRemote && (block instanceof BlockSleepingBag || block instanceof BlockHammock)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent e) {

        if (e.getEntityPlayer().getEntityWorld().isRemote) { return; }

        IWellRested wellRested = e.getEntityPlayer().getCapability(WellRested.Provider.WELL_RESTED_CAP, null);
        long worldTime = e.getEntityPlayer().getEntityWorld().getWorldTime();

        if (wellRested != null) {

            if (wellRested.getWakeTime() > worldTime) {
                //Some sorcery happened with the time stream so just reset everything
                Comforts.logger.log(Level.ERROR, "Something happened to the time stream! Resetting Well Rested values");
                wellRested.setWakeTime(0);
                wellRested.setSleepTime(0);
                wellRested.setTiredTime(0);
            }

            if (ConfigHandler.wellRested && wellRested.getTiredTime() > worldTime) {
                e.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("capability.comforts.notSleepy"), true);
                e.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
            } else {
                wellRested.setSleepTime(worldTime);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent e) {
        EntityPlayer player = e.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.bedLocation;
        IBlockState state = world.getBlockState(pos);
        IWellRested wellRested = player.getCapability(WellRested.Provider.WELL_RESTED_CAP, null);

        if (world.isRemote) { return; }

        long timeSlept = 0;

        if (wellRested != null) {

            long wakeTime = world.getWorldTime();
            timeSlept = wakeTime - wellRested.getSleepTime();

            if (timeSlept > 500L) {
                wellRested.setWakeTime(wakeTime);
                wellRested.setTiredTime(wakeTime + (long) (timeSlept / ConfigHandler.sleepyRatio));
            }
        }

        if (state.getBlock() instanceof BlockSleepingBag) {

            if (Loader.isModLoaded("toughasnails") && ConfigHandler.warmBody && timeSlept > 1000L) {
                warmBody(player, timeSlept);
            }

            if (!ConfigHandler.autoPickUp) { return; }

            ItemStack stack = state.getBlock().getItem(world, pos, state);

            BlockPos pos1 = pos.offset(state.getValue(BlockSleepingBag.FACING).getOpposite());
            world.setBlockToAir(pos);
            world.setBlockToAir(pos1);

            if (!player.capabilities.isCreativeMode) {
                ItemHandlerHelper.giveItemToPlayer(player, stack, player.inventory.currentItem);
            }

            if (!ConfigHandler.getDebuffs().isEmpty()) {
                ComfortsHelper.applyDebuffs(player, ConfigHandler.getDebuffs());
            }
        }
    }

    @Optional.Method(modid = "toughasnails")
    private void warmBody(EntityPlayer player, long timeSlept) {
        ITemperature playerTemp = TemperatureHelper.getTemperatureData(player);
        int temp = playerTemp.getTemperature().getRawValue();
        if (temp < 10) {
            int warmTemp = (int) (timeSlept / 1000L);
            temp = Math.min(10, temp + Math.min(5, warmTemp));
        }
        playerTemp.setTemperature(new Temperature(temp));
    }
}
