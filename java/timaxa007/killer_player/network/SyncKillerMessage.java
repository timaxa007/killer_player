package timaxa007.killer_player.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import timaxa007.killer_player.KillerPlayer;

public class SyncKillerMessage implements IMessage {

	public NBTTagCompound killer;

	public SyncKillerMessage() {}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, killer);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		killer = ByteBufUtils.readTag(buf);
	}

	public static class Handler implements IMessageHandler<SyncKillerMessage, IMessage> {

		@Override
		public IMessage onMessage(SyncKillerMessage packet, MessageContext message) {
			if (message.side.isClient())
				act(packet);
			else
				act(message.getServerHandler().playerEntity, packet);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void act(SyncKillerMessage packet) {
			Minecraft mc = Minecraft.getMinecraft();
			KillerPlayer killerPlayer = KillerPlayer.get(mc.thePlayer);
			if (killerPlayer == null) return;
			killerPlayer.loadNBTData(packet.killer);
		}

		private void act(EntityPlayerMP player, SyncKillerMessage packet) {
			KillerPlayer killerPlayer = KillerPlayer.get(player);
			if (killerPlayer == null) return;
			killerPlayer.loadNBTData(packet.killer);
		}

	}

}
