package timaxa007.killer_player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import timaxa007.killer_player.network.SyncKillerMessage;

public class EventsForge {

	@SubscribeEvent
	public void addEntityConstructing(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			if (KillerPlayer.get(player) == null) KillerPlayer.reg(player);
		}
	}

	@SubscribeEvent
	public void cloneKillerPlayer(PlayerEvent.Clone event) {
		KillerPlayer originalKillerPlayer = KillerPlayer.get((EntityPlayer)event.original);
		if (originalKillerPlayer == null) return;
		KillerPlayer newKillerPlayer = KillerPlayer.get((EntityPlayer)event.entityPlayer);
		if (newKillerPlayer == null) return;
		NBTTagCompound nbt = new NBTTagCompound();
		newKillerPlayer.loadNBTData(nbt);
		originalKillerPlayer.saveNBTData(nbt);
	}

	@SubscribeEvent
	public void syncKillerPlayer(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entity;
			KillerPlayer killerPlayer = KillerPlayer.get(player);
			if (killerPlayer == null) return;
			SyncKillerMessage message = new SyncKillerMessage();
			NBTTagCompound nbt = new NBTTagCompound();
			killerPlayer.saveNBTData(nbt);
			message.killer = nbt;
			KillerMod.network.sendTo(message, player);
		}
	}

	@SubscribeEvent
	public void sendInDeathPlayer(LivingDeathEvent event) {
		Entity from = event.source.getSourceOfDamage();//Кто убил.
		EntityLivingBase to = event.entityLiving;//Кого убили.
		if (from instanceof EntityPlayerMP)
			fromKill(from, to);
		else if (from instanceof EntityThrowable)
			fromKill(((EntityThrowable)from).getThrower(), to);
		else if (from instanceof EntityArrow)
			fromKill(((EntityArrow)from).shootingEntity, to);
		else if (from instanceof EntityFireball)
			fromKill(((EntityFireball)from).shootingEntity, to);
	}

	private static void fromKill(Entity from, EntityLivingBase to) {
		if (from instanceof EntityPlayerMP) {
			KillerPlayer killerPlayer = KillerPlayer.get((EntityPlayerMP)from);
			if (killerPlayer == null) return;
			if (to instanceof EntityPlayer) killerPlayer.killPlayer();
			else if (to instanceof EntityZombie) killerPlayer.killZombie();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void drawText(RenderGameOverlayEvent.Post event) {
		switch(event.type) {
		case TEXT:
			KillerPlayer killerPlayer = KillerPlayer.get(Minecraft.getMinecraft().thePlayer);
			if (killerPlayer == null) return;
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(
					StatCollector.translateToLocal("killer.kill.players.name") + ": " + killerPlayer.getKillPlayers() + ".",
					3, 50 + 2, 0xFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(
					StatCollector.translateToLocal("killer.kill.zombies.name") + ": " + killerPlayer.getKillZombies() + ".",
					3, 50 + 12, 0xFFFFFF);
			break;
		default:return;
		}
	}

}
