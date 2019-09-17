/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.block;

import com.mojang.datafixers.util.Either;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

public class ComfortsBaseBlock extends BedBlock {

  private final BedType type;

  public ComfortsBaseBlock(BedType type, DyeColor colorIn, Block.Properties properties) {
    super(colorIn, properties);
    this.type = type;
  }

  @Override
  public boolean onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos,
      @Nonnull PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

    if (worldIn.isRemote) {
      return true;
    } else {

      if (state.get(PART) != BedPart.HEAD) {
        BlockPos headPos = pos.offset(state.get(HORIZONTAL_FACING));
        BlockState headState = worldIn.getBlockState(headPos);

        if (headState.getBlock() != this) {
          return true;
        }
      }

      net.minecraftforge.common.extensions.IForgeDimension.SleepResult sleepResult = worldIn.dimension
          .canSleepAt(player, pos);

      if (sleepResult
          != net.minecraftforge.common.extensions.IForgeDimension.SleepResult.BED_EXPLODES) {

        if (sleepResult == net.minecraftforge.common.extensions.IForgeDimension.SleepResult.DENY) {
          return true;
        }

        if (state.get(OCCUPIED)) {
          PlayerEntity otherPlayer = this.getPlayerInBed(worldIn, pos);

          if (otherPlayer != null) {
            player.sendStatusMessage(
                new TranslationTextComponent("block.comforts." + type.name + "occupied"), true);
            return true;
          }

          worldIn.setBlockState(pos, state.with(OCCUPIED, false), 4);
        }

        Either<SleepResult, Unit> player$sleepresult = player.trySleep(pos);
        final BlockState finalState = state;
        player$sleepresult.ifRight(unit -> {
          BlockState newState = finalState.with(OCCUPIED, true);
          worldIn.setBlockState(pos, newState, 4);
          CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {

            if (sleepdata.getSleepingPos() == null) {
              sleepdata
                  .setSleepingPos(player.getBedLocation(player.world.getDimension().getType()));
            }
          });
        });
        player$sleepresult.ifLeft(result -> {
          ITextComponent text;
          switch (result) {
            case NOT_POSSIBLE_NOW:
              text = type == BedType.HAMMOCK ? new TranslationTextComponent(
                  "block.comforts." + type.name + ".no_sleep")
                  : new TranslationTextComponent("block.minecraft.bed.no_sleep");
              break;
            case TOO_FAR_AWAY:
              text = new TranslationTextComponent("block.comforts." + type.name + "too_far_away");
              break;
            default:
              text = result.getMessage();
          }
          player.sendStatusMessage(text, true);
        });
        return true;
      } else {
        worldIn.removeBlock(pos, false);
        BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
        if (worldIn.getBlockState(blockpos).getBlock() == this) {
          worldIn.removeBlock(blockpos, false);
        }
        worldIn.createExplosion(null, DamageSource.netherBedExplosion(), (double) pos.getX() + 0.5D,
            (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Mode.DESTROY);
        return true;
      }
    }
  }


  @Nullable
  private PlayerEntity getPlayerInBed(World worldIn, BlockPos pos) {

    for (PlayerEntity player : worldIn.getPlayers()) {

      if (player.isSleeping() && player.getBedLocation(player.world.getDimension().getType())
          .equals(pos)) {
        return player;
      }
    }
    return null;
  }

  enum BedType {
    HAMMOCK("hammock"), SLEEPING_BAG("sleeping_bag");

    private final String name;

    BedType(String name) {
      this.name = name;
    }
  }
}
