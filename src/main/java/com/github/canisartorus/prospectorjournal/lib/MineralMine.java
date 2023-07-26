package com.github.canisartorus.prospectorjournal.lib;

//import java.util.Comparator;

public abstract class MineralMine {
	public final short dim;
	public int x, z;
	public boolean dead = false;

	public int cx() {
		return x / 16;
	}

	public int cz() {
		return z / 16;
	}

	public MineralMine(short dim, int x, int z) {
		this.dim = dim;
		this.x = x;
		this.z = z;
	}
}
