package top.theillusivec4.comforts.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.Comforts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySleepData {

    @CapabilityInject(ISleepData.class)
    public static final Capability<ISleepData> SLEEP_DATA_CAP = null;

    public static final ResourceLocation ID = new ResourceLocation(Comforts.MODID, "sleep_data");

    private static final String WAKE_TAG = "wakeTime";
    private static final String TIRED_TAG = "tiredTime";
    private static final String SLEEP_TAG = "sleepTime";
    private static final String SLEEPING_TAG = "sleeping";
    private static final String LOC_TAG = "sleepingLocation";
    private static final String BED_TAG = "bedLocation";

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
        CapabilityManager.INSTANCE.register(ISleepData.class, new Capability.IStorage<ISleepData>() {

            @Override
            public INBTBase writeNBT(Capability<ISleepData> capability, ISleepData instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.putLong(WAKE_TAG, instance.getWakeTime());
                compound.putLong(TIRED_TAG, instance.getTiredTime());
                compound.putLong(SLEEP_TAG, instance.getSleepTime());
                compound.putBoolean(SLEEPING_TAG, instance.isSleeping());
                BlockPos pos = instance.getSleepingPos();

                if (pos != null) {
                    compound.put(LOC_TAG, NBTUtil.writeBlockPos(pos));
                }
                BlockPos pos2 = instance.getBedPos();

                if (pos2 != null) {
                    compound.put(BED_TAG, NBTUtil.writeBlockPos(pos2));
                }
                return compound;
            }

            @Override
            public void readNBT(Capability<ISleepData> capability, ISleepData instance, EnumFacing side, INBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setWakeTime(compound.getLong(WAKE_TAG));
                instance.setTiredTime(compound.getLong(TIRED_TAG));
                instance.setSleepTime(compound.getLong(SLEEP_TAG));
                instance.setSleeping(compound.getBoolean(SLEEPING_TAG));

                if (compound.hasUniqueId(LOC_TAG)) {
                    instance.setSleepingPos(NBTUtil.readBlockPos(compound.getCompound(LOC_TAG)));
                }

                if (compound.hasUniqueId(BED_TAG)) {
                    instance.setBedPos(NBTUtil.readBlockPos(compound.getCompound(BED_TAG)));
                }
            }
        }, SleepDataWrapper::new);
    }

    public static LazyOptional<ISleepData> getCapability(final EntityPlayer player) {
        return player.getCapability(SLEEP_DATA_CAP);
    }

    public interface ISleepData {

        long getSleepTime();

        void setSleepTime(long time);

        long getWakeTime();

        void setWakeTime(long wakeTime);

        long getTiredTime();

        void setTiredTime(long tiredTime);

        boolean isSleeping();

        void setSleeping(boolean value);

        BlockPos getSleepingPos();

        void setSleepingPos(BlockPos pos);

        BlockPos getBedPos();

        void setBedPos(BlockPos pos);
    }

    public static class SleepDataWrapper implements ISleepData {

        long sleepTime = 0;
        long wakeTime = 0;
        long tiredTime = 0;
        boolean isSleeping = false;
        BlockPos sleepLocation = null;
        BlockPos bedLocation = null;

        @Override
        public long getSleepTime() {
            return sleepTime;
        }

        @Override
        public void setSleepTime(long time) {
            sleepTime = time;
        }

        @Override
        public long getWakeTime() {
            return wakeTime;
        }

        @Override
        public void setWakeTime(long time) {
            wakeTime = time;
        }

        @Override
        public long getTiredTime() {
            return tiredTime;
        }

        @Override
        public void setTiredTime(long time) {
            tiredTime = time;
        }

        @Override
        public boolean isSleeping() {
            return isSleeping;
        }

        @Override
        public void setSleeping(boolean value) {
            isSleeping = value;
        }

        @Override
        public BlockPos getSleepingPos() {
            return sleepLocation;
        }

        @Override
        public void setSleepingPos(BlockPos pos) {
            sleepLocation = pos;
        }

        @Override
        public BlockPos getBedPos() {
            return bedLocation;
        }

        @Override
        public void setBedPos(BlockPos pos) {
            bedLocation = pos;
        }
    }

    public static class Provider implements ICapabilitySerializable<INBTBase> {

        final LazyOptional<ISleepData> optional;
        final ISleepData data;

        Provider() {
            this.data = new SleepDataWrapper();
            this.optional = LazyOptional.of(() -> data);
        }

        @SuppressWarnings("ConstantConditions")
        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return SLEEP_DATA_CAP.orEmpty(capability, optional);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public INBTBase serializeNBT() {
            return SLEEP_DATA_CAP.writeNBT(data, null);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void deserializeNBT(INBTBase nbt) {
            SLEEP_DATA_CAP.readNBT(data, null, nbt);
        }
    }

    public static class CapabilityEvents {

        @SubscribeEvent
        public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            Entity entity = evt.getObject();

            if (entity instanceof EntityPlayer) {
                evt.addCapability(CapabilitySleepData.ID, new Provider());
            }
        }

        @SubscribeEvent
        public void onPlayerDeath(final PlayerEvent.Clone evt) {

            if (evt.isWasDeath()) {
                EntityPlayer player = evt.getEntityPlayer();
                EntityPlayer original = evt.getOriginal();
                CapabilitySleepData.getCapability(player).ifPresent(sleepdata ->
                        CapabilitySleepData.getCapability(original).ifPresent(originaldata -> {
                            sleepdata.setBedPos(originaldata.getBedPos());
                            sleepdata.setSleepingPos(originaldata.getSleepingPos());
                            sleepdata.setSleeping(originaldata.isSleeping());
                            sleepdata.setSleepTime(originaldata.getSleepTime());
                            sleepdata.setTiredTime(originaldata.getTiredTime());
                            sleepdata.setWakeTime(originaldata.getWakeTime());
                    }));
            }
        }
    }
}
