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

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.block.BlockHammock;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventHandlerCommon {

    private static final Method WAKE_ALL_PLAYERS = ObfuscationReflectionHelper.findMethod(WorldServer.class, "func_73053_d");

    public static List<PotionEffect> debuffs = new ArrayList<>();

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = evt.getNewSpawn();

        if (pos != null) {
            Block block = world.getBlockState(pos).getBlock();

            if (!world.isRemote && (block instanceof BlockSleepingBag || block instanceof BlockHammock)) {
                player.bedLocation = ObfuscationReflectionHelper.getPrivateValue(EntityPlayer.class, player, "field_71077_c");
                evt.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onSleepTimeCheck(SleepingTimeCheckEvent evt) {
        World world = evt.getEntityPlayer().getEntityWorld();
        long worldTime = world.getDayTime() % 24000L;

        if (world.getBlockState(evt.getSleepingLocation()).getBlock() instanceof BlockHammock) {

            if (worldTime > 500L && worldTime < 11500L) {
                evt.setResult(Event.Result.ALLOW);
            } else {

                if (ComfortsConfig.SERVER.nightHammocks.get()) {
                    evt.setResult(Event.Result.DEFAULT);
                } else {
                    evt.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreWorldTick(TickEvent.WorldTickEvent evt) {

        if (evt.phase == TickEvent.Phase.START && evt.world instanceof WorldServer) {
            WorldServer world = (WorldServer) evt.world;

            if (world.areAllPlayersAsleep()) {
                boolean skipToNight = false;

                if (world.getGameRules().getBoolean("doDaylightCycle")) {

                    for (EntityPlayer entityplayer : world.playerEntities) {
                        BlockPos bedLocation = entityplayer.bedLocation;

                        if (entityplayer.isPlayerFullyAsleep() && bedLocation != null
                                && world.getBlockState(bedLocation).getBlock() instanceof BlockHammock) {
                            long i = world.getDayTime() + 24000L;
                            long worldTime = world.getDayTime() % 24000L;

                            if (worldTime > 500L && worldTime < 11500L) {
                                skipToNight = true;
                                world.setDayTime((i - i % 24000L) - 12001L);
                            }
                            break;
                        }
                    }
                }

                if (skipToNight) {

                    try {
                        WAKE_ALL_PLAYERS.invoke(world);
                    } catch (Exception e) {
                        Comforts.LOGGER.error("Error trying to wake all players! " + e.getMessage());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostPlayerTick(TickEvent.PlayerTickEvent evt) {

        if (evt.phase == TickEvent.Phase.END && evt.side == LogicalSide.SERVER) {
            EntityPlayer player = evt.player;
            CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {

                if (!player.isPlayerSleeping() && sleepdata.isSleeping()) {
                    World world = player.world;
                    BlockPos pos = sleepdata.getSleepingPos();
                    IBlockState state = world.getBlockState(pos);

                    if (world.isBlockLoaded(pos) && state.getBlock() instanceof BlockSleepingBag) {
                        EntityPlayer.SleepResult sleepResult = player.trySleep(pos);

                        if (sleepResult != EntityPlayer.SleepResult.OK) {
                            sleepdata.setSleeping(false);
                            sleepdata.setSleepingPos(null);
                        }
                    } else {
                        sleepdata.setSleeping(false);
                        sleepdata.setSleepingPos(null);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();
        World world = player.world;

        if (!world.isRemote) {
            CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
                BlockPos pos = sleepdata.getSleepingPos();
                IBlockState state = world.getBlockState(pos);

                if (state.getBlock() instanceof BlockSleepingBag) {
                    boolean broke = false;
                    List<PotionEffect> debuffs = getDebuffs();

                    if (!debuffs.isEmpty()) {

                        for (PotionEffect effect : debuffs) {
                            player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
                        }
                    }

                    if (world.rand.nextDouble() < ComfortsConfig.SERVER.sleepingBagBreakage.get()) {
                        broke = true;
                        BlockPos blockpos = pos.offset(state.get(BlockHorizontal.HORIZONTAL_FACING).getOpposite());
                        world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 35);
                        player.sendStatusMessage(new TextComponentTranslation("block.comforts.sleeping_bag.broke"), true);
                        world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                    if (sleepdata.isSleeping()) {
                        sleepdata.setSleeping(false);
                        sleepdata.setSleepingPos(null);

                        if (!broke) {
                            ItemStack stack = new ItemStack(state.getBlock().getItemDropped(state, world, pos, 0));
                            BlockPos blockpos = pos.offset(state.get(BlockHorizontal.HORIZONTAL_FACING).getOpposite());
                            world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 35);

                            if (!player.abilities.isCreativeMode) {
                                ItemHandlerHelper.giveItemToPlayer(player, stack, player.inventory.currentItem);
                            }
                        }
                    }
                }
                long wakeTime = world.getDayTime();
                long timeSlept = wakeTime - sleepdata.getSleepTime();

                if (timeSlept > 500L) {
                    sleepdata.setWakeTime(wakeTime);
                    sleepdata.setTiredTime(wakeTime + (long)(timeSlept / ComfortsConfig.SERVER.sleepyFactor.get()));
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();

        if (!player.world.isRemote && ComfortsConfig.SERVER.wellRested.get()) {
            long dayTime = player.getEntityWorld().getDayTime();
            CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {

                if (sleepdata.getWakeTime() > dayTime) {
                    sleepdata.setWakeTime(0);
                    sleepdata.setTiredTime(0);
                }

                if (sleepdata.getTiredTime() > dayTime) {
                    player.sendStatusMessage(new TextComponentTranslation("capability.comforts.not_sleepy"), true);
                    evt.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
                }
            });
        }
    }

    private static List<PotionEffect> getDebuffs() {
        List<String> configDebuffs = ComfortsConfig.SERVER.sleepingBagDebuffs.get();

        if (!configDebuffs.isEmpty()) {

            if (debuffs.isEmpty()) {

                for (String s : configDebuffs) {
                    String[] elements = s.split("\\s+");
                    Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(elements[0]));

                    if (potion == null) {
                        continue;
                    }
                    int duration = 0;
                    int amp = 0;
                    try {
                        duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
                        amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
                    } catch (Exception e) {
                        Comforts.LOGGER.error("Problem parsing sleeping bag debuffs in config!", e);
                    }
                    debuffs.add(new PotionEffect(potion, duration * 20, amp - 1));
                }
            }
            return debuffs;
        }
        return new ArrayList<>();
    }
}
