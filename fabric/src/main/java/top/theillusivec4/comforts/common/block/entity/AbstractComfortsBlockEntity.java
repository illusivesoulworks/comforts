package top.theillusivec4.comforts.common.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractComfortsBlockEntity extends BlockEntity implements
    BlockEntityClientSerializable {

  private DyeColor color;

  public AbstractComfortsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public AbstractComfortsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                     DyeColor color) {
    super(type, pos, state);
    this.setColor(color);
  }

  public DyeColor getColor() {

    if (this.color == null) {
      this.color = ((BedBlock) this.getCachedState().getBlock()).getColor();
    }
    return this.color;
  }

  public void setColor(DyeColor color) {
    this.color = color;
  }

  @Override
  public void fromClientTag(NbtCompound compoundTag) {
    this.readNbt(compoundTag);
  }

  @Override
  public NbtCompound toClientTag(NbtCompound compoundTag) {
    return this.writeNbt(compoundTag);
  }
}
