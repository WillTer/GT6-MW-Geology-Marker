package com.github.canisartorus.prospectorjournal.lib;

//import java.util.Comparator;

public abstract class MineralMine {
	public final short dim;
	public int cx, cz;
	public boolean dead = false;

	public MineralMine(short dim, int cx, int cz) {
		this.dim = dim;
		this.cx = cx;
		this.cz = cz;
	}

	public boolean IsInNChunksFrom(int n, int chunkX, int chunkZ) {
		return Math.abs(cx - chunkX) <= n && Math.abs(cz - chunkZ) <= n;
	}
}
