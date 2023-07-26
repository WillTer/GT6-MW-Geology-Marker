package com.github.canisartorus.prospectorjournal.lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.github.canisartorus.prospectorjournal.ProspectorJournal;
import com.github.canisartorus.prospectorjournal.network.ChatPacket;

import mapwriter.Mw;
import mapwriter.map.MarkerManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;

public class Utils {

	public static gregapi.network.INetworkHandler NW_PJ;

	public final static String GT_FILE = "GT6OreVeins.json",
			GT_BED_FILE = "GT6BedrockSpots.json";
	public final static byte STONE_LAYER = 0, FLOWER_ORE_MARKER = 1, ORE_VEIN = 2, BEDROCK_ORE_VEIN = 3;

	public final static java.util.regex.Pattern patternInvalidChars = java.util.regex.Pattern.compile("[^a-zA-Z0-9_ ]");

	public static String invalidChars(String s) {
		return patternInvalidChars.matcher(s).replaceAll("_");
	}

	public static void writeJson(String name) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = null;
		switch (name) {
			case GT_FILE:
				json = gson.toJson(ProspectorJournal.rockSurvey);
				break;
			case GT_BED_FILE:
				json = gson.toJson(ProspectorJournal.bedrockFault);
				break;
		}

		if (json == null)
			throw new java.lang.IllegalArgumentException(
					ProspectorJournal.MOD_ID + ": " + name + " is not a recognized data file.");
		try {
			if (com.github.canisartorus.prospectorjournal.ConfigHandler.debug) {
				System.out.println("Attempting to write to " + ProspectorJournal.hostName + "/" + name);
			}
			FileWriter fw = new FileWriter(ProspectorJournal.hostName + "/" + name);
			fw.write(json);
			fw.close();
		} catch (IOException e) {
			System.out.println(ProspectorJournal.MOD_ID + ": Could not write to " + name + "!");
		}
	}

	public static void createMapMarker(int x, int y, int z, int dimension, String oreName, String markerGroup,
			final EntityPlayer aPlayer) {
		if (Mw.instance == null) {
			System.out.println(ProspectorJournal.MOD_ID + ": Could not get instance of MapWriter!");
			return;
		}

		MarkerManager markerManager = Mw.instance.markerManager;
		markerManager.addMarker(oreName, markerGroup, x, y, z, dimension, 0xffff0000);
		markerManager.setVisibleGroupName(markerGroup);
		markerManager.update();

		Utils.printMessageToChat(aPlayer, ChatString.MARKED, oreName);
	}

	public static void readJson(String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(ProspectorJournal.hostName + "/" + name));
			Gson gson = new Gson();
			switch (name) {
				case GT_FILE:
					ProspectorJournal.rockSurvey = gson.fromJson(br, new TypeToken<java.util.List<GeoTag>>() {
					}.getType());
					break;
				case GT_BED_FILE:
					ProspectorJournal.bedrockFault = gson.fromJson(br, new TypeToken<java.util.List<GeoTag>>() {
					}.getType());
					break;
			}
			br.close();
		} catch (IOException e) {
			System.out.println(ProspectorJournal.MOD_ID + ": No " + name + " file found.");
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
