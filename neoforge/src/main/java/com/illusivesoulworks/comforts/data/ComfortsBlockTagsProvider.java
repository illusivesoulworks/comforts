package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.ComfortsTags;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

public class ComfortsBlockTagsProvider extends BlockTagsProvider {

  public ComfortsBlockTagsProvider(PackOutput output,
                                   CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   String modId) {
    super(output, lookupProvider, modId);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider pProvider) {

    for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
      this.tag(ComfortsTags.Blocks.HAMMOCKS).add(value.get());
    }

    for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
      this.tag(ComfortsTags.Blocks.SLEEPING_BAGS).add(value.get());
    }
  }
}
