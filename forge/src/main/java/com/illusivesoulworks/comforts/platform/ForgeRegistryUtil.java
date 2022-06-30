/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.platform;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.platform.services.IRegistryUtil;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeRegistryUtil implements IRegistryUtil {

  private static final CreativeModeTab CREATIVE_TAB =
      new CreativeModeTab(-1, ComfortsConstants.MOD_ID) {
        @Nonnull
        public ItemStack makeIcon() {
          return new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED).get());
        }
      };

  @SuppressWarnings("all")
  @Override
  public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
      BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
    return BlockEntityType.Builder.of(builder::apply, blocks).build(null);
  }

  @Override
  public MobEffect getMobEffect(ResourceLocation resourceLocation) {
    return ForgeRegistries.MOB_EFFECTS.getValue(resourceLocation);
  }

  @Override
  public CreativeModeTab getCreativeGroup() {
    return CREATIVE_TAB;
  }
}
