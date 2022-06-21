/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.comforts.common.registry.ComfortsBlocks;

public class ComfortsBlockLootTables extends BlockLoot {

  private final Map<ResourceLocation, Builder> lootBuilders = Maps.newHashMap();

  private static LootTable.Builder getLootBuilder(final Block block) {
    return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
        .add(LootItem.lootTableItem(block).when(
            LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
                StatePropertiesPredicate.Builder.properties()
                    .hasProperty(BedBlock.PART, BedPart.HEAD)))
            .when(ExplosionCondition.survivesExplosion())));
  }

  @Override
  public void accept(BiConsumer<ResourceLocation, Builder> lootBuilder) {
    List<Block> blocks = new ArrayList<>();
    blocks.addAll(ComfortsBlocks.SLEEPING_BAGS.getEntries().stream().map(RegistryObject::get).toList());
    blocks.addAll(ComfortsBlocks.HAMMOCKS.getEntries().stream().map(RegistryObject::get).toList());
    blocks.forEach(block -> this.add(block, ComfortsBlockLootTables::getLootBuilder));

    Set<ResourceLocation> set = Sets.newHashSet();

    for (Block block : blocks) {
      ResourceLocation resourcelocation = block.getLootTable();

      if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
        LootTable.Builder loottable$builder = this.lootBuilders.remove(resourcelocation);

        if (loottable$builder == null) {
          throw new IllegalStateException(String
              .format("Missing loottable '%s' for '%s'", resourcelocation,
                  ForgeRegistries.BLOCKS.getKey(block)));
        }

        lootBuilder.accept(resourcelocation, loottable$builder);
      }
    }

    if (!this.lootBuilders.isEmpty()) {
      throw new IllegalStateException(
          "Created block loot tables for non-blocks: " + this.lootBuilders.keySet());
    }
  }
}
