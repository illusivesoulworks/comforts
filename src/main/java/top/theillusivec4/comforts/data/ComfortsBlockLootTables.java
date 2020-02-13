package top.theillusivec4.comforts.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTable.Builder;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class ComfortsBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, Builder> lootBuilders = Maps.newHashMap();

  private static LootTable.Builder getLootBuilder(Block block) {
    return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(
        ItemLootEntry.builder(block)
            .acceptCondition(BlockStateProperty.builder(block).with(BedBlock.PART, BedPart.HEAD)))
        .acceptCondition(SurvivesExplosion.builder()));
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
