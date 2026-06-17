package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public record RopesTagCondition() implements ResourceCondition {

  public static final RopesTagCondition INSTANCE = new RopesTagCondition();

  public static MapCodec<RopesTagCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  public static final ResourceConditionType<?> TYPE = ResourceConditionType.create(
      Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "ropes_tag_has_items"),
      CODEC);

  @NonNull
  @Override
  public ResourceConditionType<?> getType() {
    return TYPE;
  }

  @Override
  public boolean test(RegistryOps.@Nullable RegistryInfoLookup registryInfoLookup) {

    if (registryInfoLookup == null) {
      return false;
    }
    return registryInfoLookup
        .lookup(BuiltInRegistries.ITEM.key())
        .map(registry ->
                 registry.getter()
                     .get(ConventionalItemTags.ROPES)
                     .map(ropes -> ropes.isBound() && ropes.size() > 0)
                     .orElse(false))
        .orElse(false);
  }
}
