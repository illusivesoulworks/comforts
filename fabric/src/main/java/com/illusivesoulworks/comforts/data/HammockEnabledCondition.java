package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record HammockEnabledCondition() implements ResourceCondition {

  public static final HammockEnabledCondition INSTANCE = new HammockEnabledCondition();

  public static MapCodec<HammockEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  public static final ResourceConditionType<?> TYPE = ResourceConditionType.create(
      Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "hammock_enabled"),
      CODEC);

  @Override
  public ResourceConditionType<?> getType() {
    return TYPE;
  }

  @Override
  public boolean test(RegistryOps.@Nullable RegistryInfoLookup registryInfoLookup) {
    return ComfortsConfig.COMMON.enableHammockRecipes.get();
  }
}
