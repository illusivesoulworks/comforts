package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record HammockEnabledCondition() implements ICondition {

  public static final HammockEnabledCondition INSTANCE = new HammockEnabledCondition();

  public static MapCodec<HammockEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  @Override
  public boolean test(IContext context) {
    return ComfortsConfig.COMMON.enableHammockRecipes.get();
  }

  @Override
  public MapCodec<? extends ICondition> codec() {
    return CODEC;
  }
}
