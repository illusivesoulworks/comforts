package top.theillusivec4.comforts.common.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;

public abstract class AbstractComfortsBlockEntity extends BlockEntity implements
    BlockEntityClientSerializable {

  private DyeColor color;

  public AbstractComfortsBlockEntity(BlockEntityType<?> type) {
    super(type);
  }

  public AbstractComfortsBlockEntity(BlockEntityType<?> type, DyeColor color) {
    super(type);
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
  public void fromClientTag(CompoundTag compoundTag) {
    this.fromTag(this.getCachedState(), compoundTag);
  }

  @Override
  public CompoundTag toClientTag(CompoundTag compoundTag) {
    return this.toTag(compoundTag);
  }
}
