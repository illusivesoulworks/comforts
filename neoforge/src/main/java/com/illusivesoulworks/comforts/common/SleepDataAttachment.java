package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.capability.SleepDataImpl;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class SleepDataAttachment extends SleepDataImpl implements INBTSerializable<CompoundTag> {

  @Override
  public CompoundTag serializeNBT() {
    return this.write();
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    this.read(nbt);
  }
}
