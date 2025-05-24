package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

public class ComfortsRecipeProvider extends RecipeProvider {

  private final HolderLookup.RegistryLookup<Item> items;

  public ComfortsRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
    super(provider, recipeOutput);
    this.items = provider.lookupOrThrow(Registries.ITEM);
  }

  @Override
  protected void buildRecipes() {
    List<TagKey<Item>> dyes = List.of(
        Tags.Items.DYES_WHITE,
        Tags.Items.DYES_ORANGE,
        Tags.Items.DYES_MAGENTA,
        Tags.Items.DYES_LIGHT_BLUE,
        Tags.Items.DYES_YELLOW,
        Tags.Items.DYES_LIME,
        Tags.Items.DYES_PINK,
        Tags.Items.DYES_GRAY,
        Tags.Items.DYES_LIGHT_GRAY,
        Tags.Items.DYES_CYAN,
        Tags.Items.DYES_PURPLE,
        Tags.Items.DYES_BLUE,
        Tags.Items.DYES_BROWN,
        Tags.Items.DYES_GREEN,
        Tags.Items.DYES_RED,
        Tags.Items.DYES_BLACK
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
    colorWithDye(HammockEnabledCondition.INSTANCE, dyes, hammocks, "comforts:hammock");
    colorWithDye(SleepingBagEnabledCondition.INSTANCE, dyes, sleepingBags, "comforts:sleeping_bag");
    Item ropeAndNail = ComfortsRegistry.ROPE_AND_NAIL_ITEM.get();

    ConditionalRecipe.builder()
        .condition(HammockEnabledCondition.INSTANCE)
        .recipe(ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ropeAndNail, 2)
                    .define('A', Tags.Items.STRINGS)
                    .define('X', Tags.Items.INGOTS_IRON)
                    .pattern("AA ")
                    .pattern("AX ")
                    .pattern("  A")
                    .group("comforts:rope_and_nail")
                    .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                    ::save)
        .save(this.output, RecipeBuilder.getDefaultRecipeId(ropeAndNail));


    List<ICondition> conditions = new ArrayList<>();
    conditions.add(HammockEnabledCondition.INSTANCE);
    conditions.add(new NotCondition(new TagEmptyCondition(
        ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ropes")))));

    ConditionalRecipe.builder()
        .condition(new AndCondition(conditions))
        .recipe(
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, ropeAndNail, 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ropes")))
                .group("comforts:rope_and_nail")
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                ::save)
        .save(this.output,
              ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID,
                                                    "shapeless_" + getItemName(ropeAndNail)));
  }

  protected void colorWithDye(ICondition condition, List<TagKey<Item>> pDyes,
                              List<Item> pDyeableItems, String pGroup) {

    for (int i = 0; i < pDyes.size(); i++) {
      TagKey<Item> dye = pDyes.get(i);
      Item item = pDyeableItems.get(i);
      ConditionalRecipe.builder().condition(condition)
          .recipe(ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, item)
                      .requires(dye)
                      .requires(Ingredient.of(
                          pDyeableItems.stream().filter(val -> !val.equals(item))))
                      .group(pGroup)
                      .unlockedBy("has_needed_dye", has(dye))
                      ::save)
          .save(this.output, ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID,
                                                                   "dye_" + getItemName(item)));
    }
  }

  protected void sleepingBag(ItemLike pBed, ItemLike pWool) {
    ConditionalRecipe.builder()
        .condition(SleepingBagEnabledCondition.INSTANCE)
        .recipe(ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, pBed)
                    .define('#', pWool)
                    .pattern(" # ")
                    .pattern(" # ")
                    .pattern(" # ")
                    .group("comforts:sleeping_bag")
                    .unlockedBy(getHasName(pWool), has(pWool))
                    ::save)
        .save(this.output, RecipeBuilder.getDefaultRecipeId(pBed));
  }

  protected void hammock(ItemLike pBed, ItemLike pWool) {
    ConditionalRecipe.builder()
        .condition(HammockEnabledCondition.INSTANCE)
        .recipe(ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, pBed)
                    .define('#', pWool)
                    .define('S', Tags.Items.STRINGS)
                    .define('X', Tags.Items.RODS_WOODEN)
                    .pattern(" X ")
                    .pattern("S#S")
                    .pattern(" X ")
                    .group("comforts:hammock")
                    .unlockedBy(getHasName(pWool), has(pWool))
                    ::save)
        .save(this.output, RecipeBuilder.getDefaultRecipeId(pBed));
  }

  public static class Runner extends RecipeProvider.Runner {

    public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
      super(output, registries);
    }

    @Nonnull
    @Override
    public String getName() {
      return ComfortsConstants.MOD_ID;
    }

    @Nonnull
    @Override
    protected RecipeProvider createRecipeProvider(@Nonnull HolderLookup.Provider registries,
                                                  @Nonnull RecipeOutput output) {
      return new ComfortsRecipeProvider(registries, output);
    }
  }
}
