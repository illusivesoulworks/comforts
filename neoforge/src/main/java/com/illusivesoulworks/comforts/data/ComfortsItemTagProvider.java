package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.ComfortsTags;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class ComfortsItemTagProvider extends ItemTagsProvider {

  public ComfortsItemTagProvider(PackOutput p_275343_,
                                 CompletableFuture<HolderLookup.Provider> p_275729_,
                                 CompletableFuture<TagLookup<Block>> p_275322_, String modId) {
    super(p_275343_, p_275729_, p_275322_, modId);
  }

  @Override
  protected void addTags(@Nonnull HolderLookup.Provider provider) {

    for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
      this.tag(ComfortsTags.Items.HAMMOCKS).add(value.get().asItem());
    }

    for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
      this.tag(ComfortsTags.Items.SLEEPING_BAGS).add(value.get().asItem());
    }

    this.tag(Tags.Items.ROPES)
        .addOptional(ResourceLocation.fromNamespaceAndPath("quark", "rope"))
        .addOptional(ResourceLocation.fromNamespaceAndPath("supplementaries", "rope"))
        .addOptional(ResourceLocation.fromNamespaceAndPath("farmersdelight", "rope"));
  }
}
