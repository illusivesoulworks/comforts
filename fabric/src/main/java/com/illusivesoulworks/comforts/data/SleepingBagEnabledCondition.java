package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public record SleepingBagEnabledCondition() implements ResourceCondition {

  public static final SleepingBagEnabledCondition INSTANCE = new SleepingBagEnabledCondition();

  public static MapCodec<SleepingBagEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  public static final ResourceConditionType<?> TYPE = ResourceConditionType.create(
      Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleeping_bag_enabled"),
      CODEC);

  @NonNull
  @Override
  public ResourceConditionType<?> getType() {
    return TYPE;
  }

  @Override
  public boolean test(RegistryOps.@Nullable RegistryInfoLookup registryInfoLookup) {
    return ComfortsConfig.COMMON.enableSleepingBagRecipes.get();
  }
}
