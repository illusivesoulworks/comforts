package top.theillusivec4.comforts.common.component;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerSleepTracker implements SleepTrackerComponent {

  private static final String WAKE_TAG = "wakeTime";
  private static final String TIRED_TAG = "tiredTime";
  private static final String SLEEP_TAG = "sleepTime";

  private final PlayerEntity player;
  private long sleepTime = 0;
  private long wakeTime = 0;
  private long tiredTime = 0;
  private BlockPos autoSleepPos = null;

  public PlayerSleepTracker(PlayerEntity player) {
    this.player = player;
  }

  @Override
  public long getSleepTime() {
    return this.sleepTime;
  }

  @Override
  public void setSleepTime(long time) {
    this.sleepTime = time;
  }

  @Override
  public long getWakeTime() {
    return this.wakeTime;
  }

  @Override
  public void setWakeTime(long wakeTime) {
    this.wakeTime = wakeTime;
  }

  @Override
  public long getTiredTime() {
    return this.tiredTime;
  }

  @Override
  public void setTiredTime(long tiredTime) {
    this.tiredTime = tiredTime;
  }

  @Override
  public Optional<BlockPos> getAutoSleepPos() {
    return Optional.ofNullable(this.autoSleepPos);
  }

  @Override
  public void setAutoSleepPos(BlockPos pos) {
    this.autoSleepPos = pos;
  }

  @Override
  public void readFromNbt(CompoundTag compoundTag) {
    this.wakeTime = compoundTag.getLong(WAKE_TAG);
    this.tiredTime = compoundTag.getLong(TIRED_TAG);
    this.sleepTime = compoundTag.getLong(SLEEP_TAG);
  }

  @Override
  public void writeToNbt(CompoundTag compoundTag) {
    compoundTag.putLong(WAKE_TAG, this.wakeTime);
    compoundTag.putLong(TIRED_TAG, this.tiredTime);
    compoundTag.putLong(SLEEP_TAG, this.sleepTime);
  }
}
