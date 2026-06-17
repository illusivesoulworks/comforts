package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.capability.SleepDataImpl;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SleepDataAttachment extends SleepDataImpl implements ValueIOSerializable {

  @Override
  public void serialize(ValueOutput valueOutput) {
    this.write(valueOutput);
  }

  @Override
  public void deserialize(ValueInput valueInput) {
    this.read(valueInput);
  }
}
