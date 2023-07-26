package com.github.canisartorus.prospectorjournal.lib;

public class GeoTag extends MineralMine {
	public boolean sample = true;
	public final short ore;

	public GeoTag(int ore, int dim, int x, int z, boolean flower) {
		super((short) dim, x, z);
		this.ore = (short) ore;
		this.x = x;
		this.z = z;
		this.sample = flower;
	}
}
