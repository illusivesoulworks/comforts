/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.network;

import c4.comforts.common.util.ComfortsUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketSleep implements IMessage {

    private int playerID;
    /** Block location of the head part of the bed */
    private BlockPos bedPos;

    public SPacketSleep(){}

    public SPacketSleep(EntityPlayer player, BlockPos posIn) {
        this.playerID = player.getEntityId();
        this.bedPos = posIn;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeInt(this.bedPos.getX());
        buf.writeInt(this.bedPos.getY());
        buf.writeInt(this.bedPos.getZ());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerID = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.bedPos = new BlockPos(x, y, z);
    }

    public static class SPacketSleepHandler implements IMessageHandler<SPacketSleep, IMessage> {
        // Do note that the default constructor is required, but implicitly defined in this case
        @Override
        public IMessage onMessage(SPacketSleep message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
                EntityPlayer player = (EntityPlayer) FMLClientHandler.instance().getWorldClient().getEntityByID(message.playerID);
                ComfortsUtil.trySleep(player, message.bedPos);
            });
            // No response packet
            return null;
        }
    }
}
