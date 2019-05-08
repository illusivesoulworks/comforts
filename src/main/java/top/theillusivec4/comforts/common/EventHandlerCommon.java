package top.theillusivec4.comforts.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.block.BlockHammock;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;

import java.lang.reflect.Method;

public class EventHandlerCommon {

    private static final Method WAKE_ALL_PLAYERS = ObfuscationReflectionHelper.findMethod(WorldServer.class, "func_73053_d");

    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = evt.getNewSpawn();

        if (pos != null) {
            Block block = world.getBlockState(pos).getBlock();

            if (!world.isRemote && block instanceof BlockSleepingBag) {
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
                evt.setResult(Event.Result.DENY);
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
}
