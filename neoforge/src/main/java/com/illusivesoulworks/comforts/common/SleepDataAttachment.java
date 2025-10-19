package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.capability.SleepDataImpl;
import javax.annotation.Nonnull;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class SleepDataAttachment extends SleepDataImpl implements ValueIOSerializable {

  @Override
  public void serialize(@Nonnull ValueOutput valueOutput) {
    this.write(valueOutput);
  }

  @Override
  public void deserialize(@Nonnull ValueInput valueInput) {
    this.read(valueInput);
  }
}
