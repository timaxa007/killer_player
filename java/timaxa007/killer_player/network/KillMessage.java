package timaxa007.killer_player.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import timaxa007.killer_player.KillerPlayer;

public class KillMessage implements IMessage {

	public byte action;
	public int kills;

	public KillMessage() {}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(action);
		buf.writeInt(kills);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		action = buf.readByte();
		kills = buf.readInt();
	}

	public static class Handler implements IMessageHandler<KillMessage, IMessage> {

		@Override
		public IMessage onMessage(KillMessage packet, MessageContext message) {
			if (message.side.isClient())
				act(packet);
			else
				act(message.getServerHandler().playerEntity, packet);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void act(KillMessage packet) {
			Minecraft mc = Minecraft.getMinecraft();
			KillerPlayer killerPlayer = KillerPlayer.get(mc.thePlayer);
			if (killerPlayer == null) return;
			switch(packet.action) {
			case 0:killerPlayer.setKillPlayers(packet.kills);break;
			case 1:killerPlayer.setKillZombies(packet.kills);break;
			default:break;
			}
		}

		private void act(EntityPlayerMP player, KillMessage packet) {
			KillerPlayer killerPlayer = KillerPlayer.get(player);
			if (killerPlayer == null) return;
			switch(packet.action) {
			case 0:killerPlayer.setKillPlayers(packet.kills);break;
			case 1:killerPlayer.setKillZombies(packet.kills);break;
			default:break;
			}
		}

	}

}
