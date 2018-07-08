package timaxa007.killer_player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import timaxa007.killer_player.network.KillMessage;

public class KillerPlayer implements IExtendedEntityProperties {

	private static final String ID = "KillerPlayer";
	private EntityPlayer player;
	private int
	killPlayers,
	killZombies;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagCompound tag = new NBTTagCompound();
		if (killPlayers > 0) tag.setInteger("KillPlayers", killPlayers);
		if (killZombies > 0) tag.setInteger("KillZombies", killZombies);
		nbt.setTag(ID, tag);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		if (nbt.hasKey(ID, NBT.TAG_COMPOUND)) {
			NBTTagCompound tag = nbt.getCompoundTag(ID);
			if (tag.hasKey("KillPlayers", NBT.TAG_INT)) killPlayers = tag.getInteger("KillPlayers");
			if (tag.hasKey("KillZombies", NBT.TAG_INT)) killZombies = tag.getInteger("KillZombies");
		}
	}

	@Override
	public void init(Entity entity, World world) {
		if (entity instanceof EntityPlayer) player = (EntityPlayer)entity;
	}

	public static String reg(EntityPlayer player) {
		return player.registerExtendedProperties(KillerPlayer.ID, new KillerPlayer());
	}

	public static KillerPlayer get(EntityPlayer player) {
		return (KillerPlayer)player.getExtendedProperties(KillerPlayer.ID);
	}

	public void killPlayer() {
		setKillPlayers(getKillPlayers() + 1);
	}

	public void setKillPlayers(int killPlayers) {
		this.killPlayers = killPlayers;
		if (player instanceof EntityPlayerMP) {
			KillMessage message = new KillMessage();
			message.action = 0;
			message.kills = killPlayers;
			KillerMod.network.sendTo(message, (EntityPlayerMP)player);
		}
	}

	public int getKillPlayers() {
		return killPlayers;
	}

	public void killZombie() {
		setKillZombies(getKillZombies() + 1);
	}

	public void setKillZombies(int killZombies) {
		this.killZombies = killZombies;
		if (player instanceof EntityPlayerMP) {
			KillMessage message = new KillMessage();
			message.action = 1;
			message.kills = killZombies;
			KillerMod.network.sendTo(message, (EntityPlayerMP)player);
		}
	}

	public int getKillZombies() {
		return killZombies;
	}

}
