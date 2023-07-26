package com.github.willter.gt6mwgeologymarker;

/**
 * @author Alexander James
 * @author Gregorious Techneticies
 * @author Dyonovan
 * @author WillTer
 *
 *
 * Core file for the GT6MWGeologyMarker mod.
 * This keeps track of ore bearing rocks, and indicator flowers,
 * in order to make waypointing everywhere unnecessary
 * to find the large ore Veins as are present in GT6.
 *
 *  Built primarily off of Prospector's Journal at https://github.com/CanisArtorus/ProspectorJournal
 *  and thus remains under Creative Commons CC-BY-NC-SA4.0 (attribution, non-commercial, share-alike)
 */

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import gregapi.network.NetworkHandler;

import java.util.ArrayList;
import java.util.List;

import com.github.willter.gt6mwgeologymarker.lib.*;
import com.github.willter.gt6mwgeologymarker.network.*;

@cpw.mods.fml.common.Mod(modid = GT6MWGeologyMarker.MOD_ID, name = GT6MWGeologyMarker.MOD_NAME, version = GT6MWGeologyMarker.VERSION, dependencies = "required-after:gregapi_post; required-after:gregtech; required-after:mapwriter")
public final class GT6MWGeologyMarker extends gregapi.api.Abstract_Mod {
	/**
	 * Your Mod-ID has to be LOWERCASE and without Spaces. Uppercase Chars and
	 * Spaces can create problems with Resource Packs. This is a vanilla forge
	 * "Issue".
	 */
	public static final String MOD_ID = "gt6mwgeologymarker";
	/** This is your Mods Name */
	public static final String MOD_NAME = "GT6MWGeologyMarker";
	/** This is your Mods Version */
	public static final String VERSION = "0.1.0";
	/** Contains a ModData Object for ID and Name. Doesn't have to be changed. */
	public static gregapi.code.ModData MOD_DATA = new gregapi.code.ModData(MOD_ID, MOD_NAME);

	@cpw.mods.fml.common.SidedProxy(modId = MOD_ID, clientSide = "com.github.willter.gt6mwgeologymarker.ProxyClient", serverSide = "com.github.willter.gt6mwgeologymarker.ProxyServer")
	public static ProxyServer PROXY;

	/*
	 * @cpw.mods.fml.common.Mod.Instance(MOD_ID)
	 * public static GT6MWGeologyMarker instance;
	 */

	public static String hostName = "GT6MWGeologyMarker";
	public static boolean doGui = false;
	public static int xMarker, yMarker, zMarker;
	public static List<GeoTag> rockSurvey = new ArrayList<>();
	public static List<GeoTag> bedrockFault = new ArrayList<>();

	@Override
	public String getModID() {
		return MOD_ID;
	}

	@Override
	public String getModName() {
		return MOD_NAME;
	}

	@Override
	public String getModNameForLog() {
		return "GT6-MW Geology Marker";
	}

	@Override
	public ProxyServer getProxy() {
		return PROXY;
	}

	// Do not change these 7 Functions. Just keep them this way.
	@EventHandler
	public final void onPreLoad(FMLPreInitializationEvent aEvent) {
		onModPreInit(aEvent);
	}

	@EventHandler
	public final void onLoad(FMLInitializationEvent aEvent) {
		onModInit(aEvent);
	}

	@EventHandler
	public final void onPostLoad(FMLPostInitializationEvent aEvent) {
		onModPostInit(aEvent);
	}

	@EventHandler
	public final void onServerStarting(FMLServerStartingEvent aEvent) {
		onModServerStarting(aEvent);
	}

	@EventHandler
	public final void onServerStarted(FMLServerStartedEvent aEvent) {
		onModServerStarted(aEvent);
	}

	@EventHandler
	public final void onServerStopping(FMLServerStoppingEvent aEvent) {
		onModServerStopping(aEvent);
	}

	@EventHandler
	public final void onServerStopped(FMLServerStoppedEvent aEvent) {
		onModServerStopped(aEvent);
	}

	@Override
	public void onModPreInit2(FMLPreInitializationEvent aEvent) {
		// Make new items, add them to OreDicts, and do recipes using only internal
		// items.
		ConfigHandler.init(aEvent.getSuggestedConfigurationFile());

		Utils.NW_PJ = new NetworkHandler(MOD_ID, "CAPJ",
				new ChatPacket(0), new ChatPacket(1), new ChatPacket(2),
				new PacketOreSurvey(0), new PacketOreSurvey(1), new PacketOreSurvey(2), new PacketOreSurvey(3),
				new PacketOreSurvey(4), new PacketOreSurvey(5), new PacketOreSurvey(6), new PacketOreSurvey(7));

		net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new RightClickEvent());
		cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new ClientConnectionEvent());
		getProxy().registerKeybindings();
	}

	@Override
	public void onModInit2(FMLInitializationEvent aEvent) {
		getProxy().initKeybinds();
	}

	@Override
	public void onModPostInit2(FMLPostInitializationEvent aEvent) {
		// Insert your PostInit Code here and not above
		getProxy().registerPointer();
	}

	@Override
	public void onModServerStarting2(FMLServerStartingEvent aEvent) {
		// Insert your ServerStarting Code here and not above
	}

	@Override
	public void onModServerStarted2(FMLServerStartedEvent aEvent) {
		// Insert your ServerStarted Code here and not above
	}

	@Override
	public void onModServerStopping2(FMLServerStoppingEvent aEvent) {
		// Insert your ServerStopping Code here and not above
	}

	@Override
	public void onModServerStopped2(FMLServerStoppedEvent aEvent) {
		// Insert your ServerStopped Code here and not above
	}
}
