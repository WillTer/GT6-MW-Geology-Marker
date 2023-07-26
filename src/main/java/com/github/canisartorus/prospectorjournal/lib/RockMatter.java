package com.github.canisartorus.prospectorjournal.lib;

// @Author Alexander James

public class RockMatter extends GeoTag {
	public short y;

	public RockMatter(int ore, int dim, int x, int y, int z, boolean rock) {
		super(ore, dim, x, z, rock);
		this.y = (short) y;
	}
}
