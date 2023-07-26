package com.github.canisartorus.prospectorjournal;

// @author Alexander James 2019-03-24

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	private static final String GENERAL = "General";

	public static Configuration tMainConfig;

	public static boolean trackRock,
			debug, verbose;

	public static void init(java.io.File configFile) {
		if (tMainConfig == null) {
			tMainConfig = new Configuration(configFile);
		}
		tMainConfig.load();

		ConfigHandler.trackRock = tMainConfig.getBoolean("TrackStoneRocks_false", GENERAL, false,
				"Should indicator rocks for stone layer types be tracked? Normally non-ore rock data is discarded.");
		ConfigHandler.verbose = tMainConfig.getBoolean("MentionSamplePickUp_false", GENERAL, false,
				"Sends chat message when a sample is recorded.");

		ConfigHandler.debug = tMainConfig.getBoolean("debug_logs_false", "Dbug", false, "");

		// Additional configs go inside here

		if (tMainConfig.hasChanged())
			tMainConfig.save();
	}

}
