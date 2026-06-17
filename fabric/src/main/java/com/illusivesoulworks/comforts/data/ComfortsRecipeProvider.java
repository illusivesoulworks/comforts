package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ComfortsRecipeProvider extends FabricRecipeProvider {

  public ComfortsRecipeProvider(FabricPackOutput output,
                                CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider,
                                                RecipeOutput recipeOutput) {
    RecipeOutput hammockEnabled =
        this.withConditions(recipeOutput, HammockEnabledCondition.INSTANCE);
    RecipeOutput sleepingBagEnabled =
        this.withConditions(recipeOutput, SleepingBagEnabledCondition.INSTANCE);
    RecipeOutput ropesTagEnabled = this.withConditions(recipeOutput, ResourceConditions.and(
        HammockEnabledCondition.INSTANCE, RopesTagCondition.INSTANCE));
    return new Recipes(provider, recipeOutput, hammockEnabled, sleepingBagEnabled, ropesTagEnabled);
  }

  public static class Recipes extends RecipeProvider {

    RecipeOutput hammockEnabled;
    RecipeOutput sleepingBagEnabled;
    RecipeOutput ropesTagEnabled;

    protected Recipes(HolderLookup.Provider registries, RecipeOutput output,
                      RecipeOutput hammockEnabled, RecipeOutput sleepingBagEnabled,
                      RecipeOutput ropesTagEnabled) {
      super(registries, output);
      this.hammockEnabled = hammockEnabled;
      this.sleepingBagEnabled = sleepingBagEnabled;
      this.ropesTagEnabled = ropesTagEnabled;
    }

    @Override
    public void buildRecipes() {
      List<TagKey<Item>> dyes = List.of(
          ConventionalItemTags.WHITE_DYES,
          ConventionalItemTags.ORANGE_DYES,
          ConventionalItemTags.MAGENTA_DYES,
          ConventionalItemTags.LIGHT_BLUE_DYES,
          ConventionalItemTags.YELLOW_DYES,
          ConventionalItemTags.LIME_DYES,
          ConventionalItemTags.PINK_DYES,
          ConventionalItemTags.GRAY_DYES,
          ConventionalItemTags.LIGHT_GRAY_DYES,
          ConventionalItemTags.CYAN_DYES,
          ConventionalItemTags.PURPLE_DYES,
          ConventionalItemTags.BLUE_DYES,
          ConventionalItemTags.BROWN_DYES,
          ConventionalItemTags.GREEN_DYES,
          ConventionalItemTags.RED_DYES,
          ConventionalItemTags.BLACK_DYES
      );
      List<Item> hammocks = ComfortsRegistry.HAMMOCKS.values().stream()
          .map(blockRegistryObject -> blockRegistryObject.get().asItem()).toList();
      List<Item> sleepingBags = ComfortsRegistry.SLEEPING_BAGS.values().stream()
          .map(blockRegistryObject -> blockRegistryObject.get().asItem()).toList();

      List<Item> wool = List.of(
          Items.WHITE_WOOL,
          Items.ORANGE_WOOL,
          Items.MAGENTA_WOOL,
          Items.LIGHT_BLUE_WOOL,
          Items.YELLOW_WOOL,
          Items.LIME_WOOL,
          Items.PINK_WOOL,
          Items.GRAY_WOOL,
          Items.LIGHT_GRAY_WOOL,
          Items.CYAN_WOOL,
          Items.PURPLE_WOOL,
          Items.BLUE_WOOL,
          Items.BROWN_WOOL,
          Items.GREEN_WOOL,
          Items.RED_WOOL,
          Items.BLACK_WOOL
      );

      for (int i = 0; i < wool.size(); i++) {
        sleepingBag(sleepingBags.get(i), wool.get(i));
        hammock(hammocks.get(i), wool.get(i));
      }
      colorWithDye(this.hammockEnabled, dyes, hammocks, "comforts:hammock");
      colorWithDye(this.sleepingBagEnabled, dyes, sleepingBags, "comforts:sleeping_bag");
      Item ropeAndNail = ComfortsRegistry.ROPE_AND_NAIL_ITEM.get();

      this.shaped(RecipeCategory.DECORATIONS, ropeAndNail, 2)
          .define('A', ConventionalItemTags.STRINGS)
          .define('X', ConventionalItemTags.IRON_INGOTS)
          .pattern("AA ")
          .pattern("AX ")
          .pattern("  A")
          .group("comforts:rope_and_nail")
          .unlockedBy("has_iron_ingot", has(ConventionalItemTags.IRON_INGOTS))
          .save(this.hammockEnabled);

      this.shapeless(RecipeCategory.DECORATIONS, ropeAndNail, 2)
          .requires(ConventionalItemTags.IRON_INGOTS)
          .requires(ConventionalItemTags.ROPES)
          .group("comforts:rope_and_nail")
          .unlockedBy("has_iron_ingot", has(ConventionalItemTags.IRON_INGOTS))
          .save(this.ropesTagEnabled,
                ComfortsConstants.MOD_ID + ":shapeless_" + getItemName(ropeAndNail));
    }

    protected void colorWithDye(RecipeOutput recipeOutput, List<TagKey<Item>> dyes,
                                List<Item> dyeables, String group) {

      for (int i = 0; i < dyes.size(); i++) {
        TagKey<Item> dye = dyes.get(i);
        Item item = dyeables.get(i);
        this.shapeless(RecipeCategory.BUILDING_BLOCKS, item)
            .requires(dye)
            .requires(Ingredient.of(
                dyeables.stream().filter(dyeable -> !dyeable.equals(item))))
            .group(group)
            .unlockedBy("has_needed_dye", has(dye))
            .save(recipeOutput, ComfortsConstants.MOD_ID + ":dye_" + getItemName(item));
      }
    }

    protected void sleepingBag(ItemLike pBed, ItemLike pWool) {
      this.shaped(RecipeCategory.DECORATIONS, pBed)
          .define('#', pWool)
          .pattern(" # ")
          .pattern(" # ")
          .pattern(" # ")
          .group("comforts:sleeping_bag")
          .unlockedBy(getHasName(pWool), has(pWool))
          .save(this.sleepingBagEnabled);
    }

    protected void hammock(ItemLike pBed, ItemLike pWool) {
      this.shaped(RecipeCategory.DECORATIONS, pBed)
          .define('#', pWool)
          .define('S', ConventionalItemTags.STRINGS)
          .define('X', ConventionalItemTags.WOODEN_RODS)
          .pattern(" X ")
          .pattern("S#S")
          .pattern(" X ")
          .group("comforts:hammock")
          .unlockedBy(getHasName(pWool), has(pWool))
          .save(this.hammockEnabled);
    }
  }

  @Override
  public String getName() {
    return ComfortsConstants.MOD_ID;
  }
}
