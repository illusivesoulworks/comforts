/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.RopeAndNailBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.common.block.entity.HammockBlockEntity;
import com.illusivesoulworks.comforts.common.block.entity.SleepingBagBlockEntity;
import com.illusivesoulworks.comforts.common.item.BaseComfortsItem;
import com.illusivesoulworks.comforts.common.item.HammockItem;
import com.illusivesoulworks.comforts.common.item.SleepingBagItem;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import com.illusivesoulworks.comforts.common.registry.RegistryProvider;
import com.illusivesoulworks.comforts.platform.Services;
import java.util.Arrays;
import java.util.EnumMap;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ComfortsRegistry {

  public static final RegistryProvider<Block> BLOCKS =
      RegistryProvider.get(Registry.BLOCK_REGISTRY, ComfortsConstants.MOD_ID);
  public static final RegistryProvider<Item> ITEMS =
      RegistryProvider.get(Registry.ITEM_REGISTRY, ComfortsConstants.MOD_ID);
  public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
      RegistryProvider.get(Registry.BLOCK_ENTITY_TYPE_REGISTRY, ComfortsConstants.MOD_ID);

  public static final EnumMap<DyeColor, RegistryObject<Block>> SLEEPING_BAGS =
      new EnumMap<>(DyeColor.class);
  public static final EnumMap<DyeColor, RegistryObject<Block>> HAMMOCKS =
      new EnumMap<>(DyeColor.class);

  public static final RegistryObject<Block> ROPE_AND_NAIL_BLOCK =
      BLOCKS.register("rope_and_nail", RopeAndNailBlock::new);
  public static final RegistryObject<Item> ROPE_AND_NAIL_ITEM =
      ITEMS.register("rope_and_nail", () -> new BaseComfortsItem(ROPE_AND_NAIL_BLOCK.get()));

  public static final RegistryObject<BlockEntityType<SleepingBagBlockEntity>>
      SLEEPING_BAG_BLOCK_ENTITY;
  public static final RegistryObject<BlockEntityType<HammockBlockEntity>>
      HAMMOCK_BLOCK_ENTITY;

  static {
    Arrays.stream(DyeColor.values()).forEach(color -> {
      SLEEPING_BAGS.put(color,
          BLOCKS.register("sleeping_bag_" + color.getName(), () -> new SleepingBagBlock(color)));
      HAMMOCKS.put(color,
          BLOCKS.register("hammock_" + color.getName(), () -> new HammockBlock(color)));
    });
    SLEEPING_BAGS.values().forEach(
        reg -> ITEMS.register(reg.getId().getPath(), () -> new SleepingBagItem(reg.get())));
    HAMMOCKS.values()
        .forEach(reg -> ITEMS.register(reg.getId().getPath(), () -> new HammockItem(reg.get())));
    HAMMOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("hammock",
        () -> Services.REGISTRY_UTIL.createBlockEntityType(HammockBlockEntity::new,
            HAMMOCKS.values().stream().map(RegistryObject::get).toArray(Block[]::new)));
    SLEEPING_BAG_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sleeping_bag",
        () -> Services.REGISTRY_UTIL.createBlockEntityType(SleepingBagBlockEntity::new,
            SLEEPING_BAGS.values().stream().map(RegistryObject::get).toArray(Block[]::new)));
  }

  public static void init() {
  }
}
