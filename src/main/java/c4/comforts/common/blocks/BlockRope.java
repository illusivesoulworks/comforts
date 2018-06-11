/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.blocks;

import c4.comforts.Comforts;
import c4.comforts.common.items.ComfortsItems;
import c4.comforts.common.util.ComfortsHelper;
import c4.comforts.common.util.OreDictHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class BlockRope extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", (apply) ->
            apply != EnumFacing.DOWN && apply != EnumFacing.UP
    );
    public static final PropertyBool SUPPORTING = PropertyBool.create("supporting");

    protected static final AxisAlignedBB ROPE_NORTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.75D, 0.625D, 0.5D, 1.0D);
    protected static final AxisAlignedBB ROPE_SOUTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.5D, 0.25D);
    protected static final AxisAlignedBB ROPE_WEST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.375D, 1.0D, 0.5D, 0.625D);
    protected static final AxisAlignedBB ROPE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.25D, 0.5D, 0.625D);
    protected static final AxisAlignedBB ROPE_NORTH_S_AABB = new AxisAlignedBB(0.375D, 0.1875D, 0.5625D, 0.625D, 0.5D, 1.0D);
    protected static final AxisAlignedBB ROPE_SOUTH_S_AABB = new AxisAlignedBB(0.375D, 0.1875D, 0.0D, 0.625D, 0.5D, 0.4375D);
    protected static final AxisAlignedBB ROPE_WEST_S_AABB = new AxisAlignedBB(0.5625D, 0.1875D, 0.375D, 1.0D, 0.5D, 0.625D);
    protected static final AxisAlignedBB ROPE_EAST_S_AABB = new AxisAlignedBB(0.0D, 0.1875D, 0.375D, 0.4375D, 0.5D, 0.625D);

    public BlockRope() {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SUPPORTING, false));
        this.setSoundType(SoundType.METAL);
        this.setHardness(0.2F);
        this.setRegistryName("rope");
        this.setUnlocalizedName(Comforts.MODID + ".rope");
        this.setCreativeTab(ComfortsHelper.comfortsTab);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        boolean supporting = state.getValue(SUPPORTING);

        switch (state.getValue(FACING))
        {
            case EAST:
                return supporting ? ROPE_EAST_S_AABB : ROPE_EAST_AABB;
            case WEST:
                return supporting ? ROPE_WEST_S_AABB : ROPE_WEST_AABB;
            case SOUTH:
                return supporting ? ROPE_SOUTH_S_AABB : ROPE_SOUTH_AABB;
            default:
                return supporting ? ROPE_NORTH_S_AABB : ROPE_NORTH_AABB;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos)
    {
        for (EnumFacing enumfacing : FACING.getAllowedValues())
        {
            if (this.canPlaceAt(worldIn, pos, enumfacing))
            {
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing)
    {
        BlockPos blockpos = pos.offset(facing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        return facing != EnumFacing.UP && facing != EnumFacing.DOWN && !isExceptBlockForAttachWithPiston(iblockstate.getBlock()) && iblockstate.getBlockFaceShape(worldIn, pos, facing) == BlockFaceShape.SOLID && iblockstate.getMaterial().isOpaque();
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (this.canPlaceAt(worldIn, pos, facing))
        {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        else
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.canPlaceAt(worldIn, pos, enumfacing))
                {
                    return this.getDefaultState().withProperty(FACING, enumfacing);
                }
            }

            return this.getDefaultState();
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        IBlockState blockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

        if (!(blockstate.getBlock() instanceof BlockHammock)) {
            worldIn.setBlockState(pos, state.withProperty(SUPPORTING, false));
        }

        this.onNeighborChangeInternal(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) {
            return true;
        }
        else if (playerIn.getHeldItem(hand).getItem() != ComfortsItems.HAMMOCK || state.getValue(SUPPORTING) || !checkForPartnerRope(worldIn, pos, state)) {
            return true;
        } else {
            hangHammock(worldIn, pos.offset(state.getValue(FACING)), playerIn, hand, state.getValue(FACING));
            return true;
        }
    }

    private void hangHammock(World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing) {

        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        boolean flag = block.isReplaceable(worldIn, pos);

        BlockPos blockpos = pos.offset(facing);
        ItemStack itemstack = player.getHeldItem(hand);

        if (player.canPlayerEdit(pos, facing, itemstack) && player.canPlayerEdit(blockpos, facing, itemstack)) {
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
            boolean flag1 = iblockstate1.getBlock().isReplaceable(worldIn, blockpos);
            boolean flag2 = flag || worldIn.isAirBlock(pos);
            boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

            if (flag2 && flag3) {
                IBlockState iblockstate2 = ComfortsBlocks.HAMMOCKS[itemstack.getMetadata()].getDefaultState().withProperty(BlockHammock.OCCUPIED, false).withProperty(BlockHammock.FACING, facing).withProperty(BlockHammock.PART, BlockHammock.EnumPartType.FOOT);
                worldIn.setBlockState(pos.offset(facing.getOpposite()), worldIn.getBlockState(pos.offset(facing.getOpposite())).withProperty(SUPPORTING, true));
                worldIn.setBlockState(blockpos.offset(facing), worldIn.getBlockState(blockpos.offset(facing)).withProperty(SUPPORTING, true));
                worldIn.setBlockState(pos, iblockstate2, 10);
                worldIn.setBlockState(blockpos, iblockstate2.withProperty(BlockHammock.PART, BlockHammock.EnumPartType.HEAD), 10);
                SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, worldIn, pos, player);
                worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                worldIn.notifyNeighborsRespectDebug(pos, block, false);
                worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);

                if (player instanceof EntityPlayerMP) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                }

                if (!player.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }
        }
    }

    private boolean checkForPartnerRope(World worldIn, BlockPos pos, IBlockState state) {

        BlockPos blockpos = pos.offset(state.getValue(FACING), 3);
        IBlockState blockstate = worldIn.getBlockState(blockpos);

        if (blockstate.getBlock() instanceof BlockRope) {
            if (blockstate.getValue(FACING) == state.getValue(FACING).getOpposite() && !blockstate.getValue(SUPPORTING)) {
                return true;
            }
        }

        return false;
    }

    protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForDrop(worldIn, pos, state))
        {
            return true;
        }
        else
        {
            EnumFacing enumfacing = state.getValue(FACING);
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            BlockPos blockpos = pos.offset(enumfacing1);
            boolean flag = false;

            if (worldIn.getBlockState(blockpos).getBlockFaceShape(worldIn, blockpos, enumfacing) != BlockFaceShape.SOLID)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, state.getValue(FACING)))
        {
            return true;
        }
        else
        {
            if (worldIn.getBlockState(pos).getBlock() == this)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }

            return false;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState();

        if (meta < 4) {
            iblockstate = iblockstate.withProperty(FACING, EnumFacing.getHorizontal(meta));
        } else {
            iblockstate = iblockstate.withProperty(FACING, EnumFacing.getHorizontal(meta - 4)).withProperty(SUPPORTING, true);
        }

        return iblockstate;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        i += (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(SUPPORTING))
        {
            i += 4;
        }

        return i;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, SUPPORTING);
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
