package top.theillusivec4.comforts.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.tileentity.TileEntitySleepingBag;

import javax.annotation.Nonnull;

public class BlockSleepingBag extends BlockBed {

    private static final VoxelShape SLEEPING_BAG_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    private final EnumDyeColor color;

    public BlockSleepingBag(EnumDyeColor color) {
        super(color, Block.Properties.create(Material.CLOTH).sound(SoundType.CLOTH).hardnessAndResistance(0.1F));
        this.color = color;
        this.setRegistryName(Comforts.MODID, "sleeping_bag_" + color.getTranslationKey());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return SLEEPING_BAG_SHAPE;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntitySleepingBag(this.color);
    }
}
