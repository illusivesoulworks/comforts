package com.illusivesoulworks.comforts.data;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;

public record RopesTagCondition() implements ICondition {

  public static final RopesTagCondition INSTANCE = new RopesTagCondition();

  public static MapCodec<RopesTagCondition> CODEC = MapCodec.unit(INSTANCE).stable();

  @Override
  public boolean test(@Nonnull IContext context) {
    boolean tagLoaded = context.isTagLoaded(Tags.Items.ROPES);

    if (tagLoaded) {
      return context
          .registryAccess()
          .lookup(BuiltInRegistries.ITEM.key())
          .map(registry ->
                   registry
                       .get(Tags.Items.ROPES)
                       .map(ropes -> ropes.isBound() && ropes.size() > 0)
                       .orElse(false))
          .orElse(false);
    }
    return false;
  }

  @Nonnull
  @Override
  public MapCodec<? extends ICondition> codec() {
    return CODEC;
  }
}
