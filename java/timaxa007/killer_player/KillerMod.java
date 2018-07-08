package timaxa007.killer_player;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import timaxa007.killer_player.network.KillMessage;
import timaxa007.killer_player.network.SyncKillerMessage;

@Mod(modid = KillerMod.MODID, name = KillerMod.NAME, version = KillerMod.VERSION)
public class KillerMod {

	public static final String
	MODID = "killer",
	NAME = "Killer Mod",
	VERSION = "1";

	@Mod.Instance(MODID)
	public static KillerMod instance;

	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		network.registerMessage(SyncKillerMessage.Handler.class, SyncKillerMessage.class, 0, Side.CLIENT);
		network.registerMessage(SyncKillerMessage.Handler.class, SyncKillerMessage.class, 0, Side.SERVER);
		network.registerMessage(KillMessage.Handler.class, KillMessage.class, 1, Side.CLIENT);
		network.registerMessage(KillMessage.Handler.class, KillMessage.class, 1, Side.SERVER);
		MinecraftForge.EVENT_BUS.register(new EventsForge());
	}

}
