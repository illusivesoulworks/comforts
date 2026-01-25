package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.Collections;
import javax.annotation.Nonnull;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ComfortsLootTableProvider extends BlockLootSubProvider {

  public ComfortsLootTableProvider(HolderLookup.Provider provider) {
    super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
  }

  @Override
  protected void generate() {
    this.dropSelf(ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get());

    for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
      this.add(value.get(), block -> this.createTable(value.get()));
    }
    for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
      this.add(value.get(), block -> this.createTable(value.get()));
    }
  }

  private LootTable.Builder createTable(Block block) {
    return LootTable.lootTable()
        .withPool(
            this.applyExplosionCondition(
                block,
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        LootItem.lootTableItem(block)
                            .apply(
                                CopyComponentsFunction
                                    .copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                    .include(DataComponents.CUSTOM_NAME))
                            .when(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                        StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(BedBlock.PART, BedPart.HEAD))
                            )
                    )
            )
        );
  }

  @Nonnull
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ComfortsRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
  }
}
