package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.ComfortsTags;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class ComfortsItemTagProvider extends KeyTagProvider<Item> {

  public ComfortsItemTagProvider(PackOutput output,
                                 CompletableFuture<HolderLookup.Provider> registries,
                                 String modId) {
    super(output, Registries.ITEM, registries, modId);
  }

  @Override
  protected void addTags(@Nonnull HolderLookup.Provider provider) {

    for (RegistryObject<Block> value : ComfortsRegistry.HAMMOCKS.values()) {
      this.tag(ComfortsTags.Items.HAMMOCKS).add(value.get().asItem().builtInRegistryHolder().key());
    }

    for (RegistryObject<Block> value : ComfortsRegistry.SLEEPING_BAGS.values()) {
      this.tag(ComfortsTags.Items.SLEEPING_BAGS)
          .add(value.get().asItem().builtInRegistryHolder().key());
    }

    this.tag(Tags.Items.ROPES)
        .addOptional(ResourceKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("quark", "rope")))
        .addOptional(ResourceKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("supplementaries",
                                                                              "rope")))
        .addOptional(ResourceKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("farmersdelight",
                                                                              "rope")));
  }
}
