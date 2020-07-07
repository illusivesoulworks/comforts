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
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class ComfortsBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, Builder> lootBuilders = Maps.newHashMap();

  private static LootTable.Builder getLootBuilder(Block block) {
    return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(
        ItemLootEntry.builder(block).acceptCondition(BlockStateProperty.builder(block)
            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                .withProp(BedBlock.PART, BedPart.HEAD)))
            .acceptCondition(SurvivesExplosion.builder())));
  }

  @Override
  public void accept(BiConsumer<ResourceLocation, Builder> lootBuilder) {
    List<Block> blocks = new ArrayList<>();
    blocks.addAll(ComfortsRegistry.SLEEPING_BAGS.values());
    blocks.addAll(ComfortsRegistry.HAMMOCKS.values());
    blocks.forEach(block -> this.registerLootTable(block, ComfortsBlockLootTables::getLootBuilder));

    Set<ResourceLocation> set = Sets.newHashSet();

    for (Block block : blocks) {
      ResourceLocation resourcelocation = block.getLootTable();

      if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
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
