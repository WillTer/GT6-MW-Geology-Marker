package com.github.willter.gt6mwgeologymarker;

// @author Alexander James 2019-03-24

import net.minecraftforge.common.config.Configuration;
import scala.Int;

public class ConfigHandler {
	private static final String GENERAL = "General";

	public static Configuration tMainConfig;

	public static boolean trackRock;
	public static boolean debug;
	public static int veinDistance;

	public static void init(java.io.File configFile) {
		if (tMainConfig == null) {
			tMainConfig = new Configuration(configFile);
		}
		tMainConfig.load();

		ConfigHandler.trackRock = tMainConfig.getBoolean("TrackStoneRocks_false", GENERAL, false,
				"Should indicator rocks for stone layer types be tracked? Normally non-ore rock data is discarded.");

		ConfigHandler.debug = tMainConfig.getBoolean("debug_logs_false", "Dbug", false, "");

		ConfigHandler.veinDistance = tMainConfig.getInt("veinDistance_2", GENERAL, 2, 0, Int.MaxValue(),
				"Distance in chunks between samples of the same ore that will be counts as different veins");

		// Additional configs go inside here

		if (tMainConfig.hasChanged())
			tMainConfig.save();
	}

}
