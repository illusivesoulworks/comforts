/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.entities;

import c4.comforts.common.tileentities.TileEntityHammock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityRest extends Entity {

    public EntityRest(World world) {
        super(world);
        this.noClip = true;
        this.width = 0.0001F;
        this.height = 0.0625F;
    }

    public EntityRest(World world, BlockPos pos) {
        this(world);
        this.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.1825D;
    }

    @Override
    public void onUpdate() {

        if (this.world.getTileEntity(this.getPosition()) == null
                || !(this.world.getTileEntity(this.getPosition()) instanceof TileEntityHammock)) {
            this.setDead();
        }
        super.onUpdate();
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
    }
}
