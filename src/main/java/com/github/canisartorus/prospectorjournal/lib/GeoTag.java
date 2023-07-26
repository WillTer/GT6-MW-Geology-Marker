package com.github.canisartorus.prospectorjournal.lib;

public class GeoTag extends MineralMine {
	public boolean sample = true;
	public final short ore;

	public GeoTag(int ore, int dim, int cx, int cz, boolean flower) {
		super((short) dim, cx, cz);
		this.ore = (short) ore;
		this.cx = cx;
		this.cz = cz;
		this.sample = flower;
	}
}
