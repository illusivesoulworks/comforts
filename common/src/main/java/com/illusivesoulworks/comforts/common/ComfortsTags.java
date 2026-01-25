package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.ComfortsConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ComfortsTags {

  public static class Items {

    public static final TagKey<Item> SLEEPING_BAGS = TagKey.create(Registries.ITEM,
                                                                   Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleeping_bags"));
    public static final TagKey<Item> HAMMOCKS = TagKey.create(Registries.ITEM,
                                                              Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "hammocks"));
  }

  public static class Blocks {

    public static final TagKey<Block> SLEEPING_BAGS = TagKey.create(Registries.BLOCK,
                                                                    Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleeping_bags"));
    public static final TagKey<Block> HAMMOCKS = TagKey.create(Registries.BLOCK,
                                                               Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "hammocks"));
  }
}
