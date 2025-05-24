package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.block.RopeAndNailBlock;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ComfortsModelProvider extends ModelProvider {

  public static final ModelTemplate TEMPLATE = new ModelTemplate(
      Optional.empty(),
      Optional.empty(),
      TextureSlot.PARTICLE
  );

  public ComfortsModelProvider(PackOutput output) {
    super(output, ComfortsConstants.MOD_ID);
  }

  @Override
  protected void registerModels(@Nonnull BlockModelGenerators blockModels,
                                @Nonnull ItemModelGenerators itemModels) {
    EnumMap<DyeColor, Block> dyedWool = new EnumMap<>(DyeColor.class);
    dyedWool.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
    dyedWool.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
    dyedWool.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
    dyedWool.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
    dyedWool.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
    dyedWool.put(DyeColor.LIME, Blocks.LIME_WOOL);
    dyedWool.put(DyeColor.PINK, Blocks.PINK_WOOL);
    dyedWool.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
    dyedWool.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
    dyedWool.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
    dyedWool.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
    dyedWool.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
    dyedWool.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
    dyedWool.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
    dyedWool.put(DyeColor.RED, Blocks.RED_WOOL);
    dyedWool.put(DyeColor.BLACK, Blocks.BLACK_WOOL);

    for (Map.Entry<DyeColor, Block> entry : dyedWool.entrySet()) {
      DyeColor color = entry.getKey();
      Block wool = entry.getValue();
      ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(
          ComfortsConstants.MOD_ID,
          "block/" + color.getName() + "_cloth");
      TEMPLATE.create(resourceLocation, new TextureMapping().put(TextureSlot.PARTICLE,
                                                                 TextureMapping.getBlockTexture(
                                                                     wool)),
                      blockModels.modelOutput);
      Block hammock = ComfortsRegistry.HAMMOCKS.get(color).get();
      Block sleepingBag = ComfortsRegistry.SLEEPING_BAGS.get(color).get();
      blockModels.blockStateOutput.accept(
          BlockModelGenerators.createSimpleBlock(hammock, resourceLocation));
      blockModels.registerSimpleFlatItemModel(hammock.asItem());
      blockModels.blockStateOutput.accept(
          BlockModelGenerators.createSimpleBlock(sleepingBag, resourceLocation));
      blockModels.registerSimpleFlatItemModel(sleepingBag.asItem());
    }
    ResourceLocation rope =
        ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "block/rope_and_nail");
    ResourceLocation supportingRope =
        ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "block/rope_and_nail_s");
    blockModels.blockStateOutput.accept(
        MultiVariantGenerator.multiVariant(ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get())
            .with(
                PropertyDispatch.properties(RopeAndNailBlock.HORIZONTAL_FACING,
                                            RopeAndNailBlock.SUPPORTING,
                                            BaseComfortsBlock.WATERLOGGED)
                    .generate(
                        (horizontalFacing, isSupporting, isWaterlogged) -> {
                          VariantProperties.Rotation rotation = VariantProperties.Rotation.R0;

                          if (horizontalFacing == Direction.EAST) {
                            rotation = VariantProperties.Rotation.R270;
                          } else if (horizontalFacing == Direction.WEST) {
                            rotation = VariantProperties.Rotation.R90;
                          } else if (horizontalFacing == Direction.NORTH) {
                            rotation = VariantProperties.Rotation.R180;
                          }
                          ResourceLocation resourceLocation = isSupporting ? supportingRope : rope;
                          return Variant.variant().with(VariantProperties.Y_ROT, rotation)
                              .with(VariantProperties.MODEL, resourceLocation);
                        }
                    )
            )
    );
    blockModels.registerSimpleFlatItemModel(ComfortsRegistry.ROPE_AND_NAIL_ITEM.get());
  }
}
