package com.illusivesoulworks.comforts.data;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.block.RopeAndNailBlock;
import com.mojang.math.Quadrant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
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
    // MC 26.2: individual Blocks.*_WOOL constants were replaced by a single
    // Blocks.WOOL ColorCollection, indexed via pick(DyeColor).
    EnumMap<DyeColor, Block> dyedWool = new EnumMap<>(DyeColor.class);
    for (DyeColor color : DyeColor.values()) {
      dyedWool.put(color, Blocks.WOOL.pick(color));
    }

    for (Map.Entry<DyeColor, Block> entry : dyedWool.entrySet()) {
      DyeColor color = entry.getKey();
      Block wool = entry.getValue();
      Identifier resourceLocation = Identifier.fromNamespaceAndPath(
          ComfortsConstants.MOD_ID,
          "block/" + color.getName() + "_cloth");
      TEMPLATE.create(resourceLocation, new TextureMapping().put(TextureSlot.PARTICLE,
                                                                 TextureMapping.getBlockTexture(
                                                                     wool)),
                      blockModels.modelOutput);
      Block hammock = ComfortsRegistry.HAMMOCKS.get(color).get();
      Block sleepingBag = ComfortsRegistry.SLEEPING_BAGS.get(color).get();
      blockModels.blockStateOutput.accept(
          BlockModelGenerators.createSimpleBlock(hammock, BlockModelGenerators.plainVariant(
              resourceLocation)));
      blockModels.registerSimpleFlatItemModel(hammock.asItem());
      blockModels.blockStateOutput.accept(
          BlockModelGenerators.createSimpleBlock(sleepingBag, BlockModelGenerators.plainVariant(
              resourceLocation)));
      blockModels.registerSimpleFlatItemModel(sleepingBag.asItem());
    }
    Identifier rope =
        Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "block/rope_and_nail");
    Identifier supportingRope =
        Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "block/rope_and_nail_s");
    blockModels.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get())
            .with(
                PropertyDispatch.initial(RopeAndNailBlock.HORIZONTAL_FACING,
                                         RopeAndNailBlock.SUPPORTING,
                                         BaseComfortsBlock.WATERLOGGED)
                    .generate(
                        (horizontalFacing, isSupporting, isWaterlogged) -> {
                          Quadrant rotation = Quadrant.R0;

                          if (horizontalFacing == Direction.EAST) {
                            rotation = Quadrant.R270;
                          } else if (horizontalFacing == Direction.WEST) {
                            rotation = Quadrant.R90;
                          } else if (horizontalFacing == Direction.NORTH) {
                            rotation = Quadrant.R180;
                          }
                          Identifier resourceLocation = isSupporting ? supportingRope : rope;
                          return BlockModelGenerators.plainVariant(resourceLocation)
                              .with(VariantMutator.Y_ROT.withValue(rotation));
                        }
                    )
            )
    );
    blockModels.registerSimpleFlatItemModel(ComfortsRegistry.ROPE_AND_NAIL_ITEM.get());
  }
}
