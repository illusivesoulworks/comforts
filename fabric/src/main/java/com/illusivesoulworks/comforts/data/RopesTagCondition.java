package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public record RopesTagCondition() implements ResourceCondition {

  public static final RopesTagCondition INSTANCE = new RopesTagCondition();

  public static MapCodec<RopesTagCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  public static final ResourceConditionType<?> TYPE = ResourceConditionType.create(
      Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "ropes_tag_has_items"),
      CODEC);

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
