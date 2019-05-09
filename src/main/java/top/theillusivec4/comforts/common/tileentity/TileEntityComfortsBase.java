package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.block.BlockBed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityComfortsBase extends TileEntity {

    private EnumDyeColor color;

    public TileEntityComfortsBase(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public TileEntityComfortsBase(TileEntityType<?> tileEntityType, EnumDyeColor colorIn) {
        this(tileEntityType);
        this.setColor(colorIn);
    }

    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 11, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        this.read(pkt.getNbtCompound());
    }

    @OnlyIn(Dist.CLIENT)
    public EnumDyeColor getColor() {

        if (this.color == null) {
            this.color = ((BlockBed)this.getBlockState().getBlock()).getColor();
        }
        return this.color;
    }

    public void setColor(EnumDyeColor color) {
        this.color = color;
    }
}
