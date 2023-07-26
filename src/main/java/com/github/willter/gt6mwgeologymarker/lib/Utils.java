package com.github.willter.gt6mwgeologymarker.lib;

import com.github.willter.gt6mwgeologymarker.ConfigHandler;
import com.github.willter.gt6mwgeologymarker.GT6MWGeologyMarker;
import com.github.willter.gt6mwgeologymarker.network.ChatPacket;

import mapwriter.Mw;
import mapwriter.map.MarkerManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;

public class Utils {
	public static gregapi.network.INetworkHandler NW_PJ;

	public final static byte STONE_LAYER = 0;
	public final static byte FLOWER_ORE_MARKER = 1;
	public final static byte ORE_VEIN = 2;
	public final static byte BEDROCK_ORE_VEIN = 3;

	// TODO: Config?..
	public final static String OreVeinGroup = "Ore Veins";
	public final static String BedrockVeinGroup = "Bedrock Ore Veins";
	public final static String StoneLayerGroup = "Stone Layers";

	public static void createMapMarker(int x, int y, int z, int dimension, String oreName, String markerGroup,
			final EntityPlayer aPlayer) {
		if (Mw.instance == null) {
			System.out.println(GT6MWGeologyMarker.MOD_ID + ": Could not get instance of MapWriter!");
			return;
		}

		MarkerManager markerManager = Mw.instance.markerManager;
		if (markerManager != null) {
			markerManager.addMarker(oreName, markerGroup, x, y, z, dimension, 0xffff0000);
			markerManager.setVisibleGroupName(markerGroup);
			markerManager.update();

			Utils.printMessageToChat(aPlayer, ChatString.MARKED, oreName);
		}
	}

	public static void printMessageToChat(EntityPlayer aPlayer, Utils.ChatString chatString, String oreName) {
		if (aPlayer.isClientWorld()) {
			net.minecraft.util.ChatComponentText chaty = new net.minecraft.util.ChatComponentText(chatString.toString()
					+ oreName);
			aPlayer.addChatMessage(chaty);
		} else {
			Utils.NW_PJ.sendToPlayer(new ChatPacket(chatString), (EntityPlayerMP) aPlayer);
		}
	}

	public static void debugLog(String message) {
		if (ConfigHandler.useDebugLogs) {
			System.out.println(GT6MWGeologyMarker.MOD_NAME + ": " + message);
		}
	}

	public static boolean IsInNChunksFrom(int n, int chunkX, int chunkZ, int fromChunkX, int fromChunkZ) {
		return Math.abs(chunkX - fromChunkX) <= n && Math.abs(chunkZ - fromChunkZ) <= n;
	}

	public static enum ChatString {
		MARKED("msg.marked.name");

		ChatString(String key) {
			mKey = key;
		}

		String mKey;

		@Override
		public String toString() {
			return StatCollector.translateToLocal(mKey);
		}
	}
}
