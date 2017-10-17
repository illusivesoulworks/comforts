/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.network;

import c4.comforts.common.util.SleepHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketSleep implements IMessage {

    /** Block location of the head part of the bed */
    private BlockPos bedPos;
    private boolean autoSleep;

    public SPacketSleep(){}

    public SPacketSleep(BlockPos posIn, boolean autoSleep)
    {
        this.bedPos = posIn;
        this.autoSleep = autoSleep;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.bedPos.getX());
        buf.writeInt(this.bedPos.getY());
        buf.writeInt(this.bedPos.getZ());
        buf.writeBoolean(this.autoSleep);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.bedPos = new BlockPos(x, y, z);
        this.autoSleep = buf.readBoolean();
    }

    public static class SPacketSleepHandler implements IMessageHandler<SPacketSleep, IMessage> {
        // Do note that the default constructor is required, but implicitly defined in this case

        @Override
        public IMessage onMessage(SPacketSleep message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                SleepHelper.trySleep(player, message.bedPos, message.autoSleep);
            });
            // No response packet
            return null;
        }
    }
}
