package com.github.willter.gt6mwgeologymarker;

import java.util.ListIterator;

import com.github.willter.gt6mwgeologymarker.lib.Utils;
import com.github.willter.gt6mwgeologymarker.network.PacketOreSurvey;

import gregapi.block.metatype.BlockStones;
import gregapi.item.multiitem.MultiItem;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.notick.TileEntityBase03MultiTileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class JournalBehaviour extends gregapi.item.multiitem.behaviors.IBehavior.AbstractBehaviorDefault {
	public static JournalBehaviour INSTANCE = new JournalBehaviour();

	@Override
	public boolean onItemUse(MultiItem aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY,
			int aZ, byte aSide, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(MultiItem aItem, ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
		return aStack;
	}

	private static boolean lookForSampleServer(World aWorld, int x, int y, int z, EntityPlayer aPlayer) {
		// stuff that needs server-side data
		final net.minecraft.tileentity.TileEntity i = aWorld.getTileEntity(x, y, z);
		if (!(i instanceof TileEntityBase03MultiTileEntities)) {
			return false;
		}

		// FIXME: why the heck this requires AE2?..
		if (!((TileEntityBase03MultiTileEntities) i).getTileEntityName()
				.equalsIgnoreCase("gt.multitileentity.rock")) {
			return false;
		}

		// serverside data only!!!
		final ItemStack sample = ((gregtech.tileentity.placeables.MultiTileEntityRock) i).mRock; // XXX GT
		if (sample == null) {
			// is default rock.
			if (ConfigHandler.trackStoneRocks) {
				TakeSampleServer(aWorld, x, y, z,
						(short) ((TileEntityBase03MultiTileEntities) i).getDrops(0, false).get(0)
								.getItemDamage(),
						Utils.STONE_LAYER, aPlayer);
			}
		} else if (gregapi.util.OM.is(gregapi.data.OD.itemFlint, sample)) {
			// ignore
		} else if (gregapi.util.OM.materialcontains(sample, gregapi.data.TD.Properties.STONE)) {
			if (ConfigHandler.trackStoneRocks) {
				TakeSampleServer(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.STONE_LAYER, aPlayer);
			}
		} else if (gregapi.data.OP.oreRaw.contains(sample)) {
			TakeSampleServer(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.BEDROCK_ORE_VEIN, aPlayer);
		} else {
			TakeSampleServer(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.ORE_VEIN, aPlayer);
		}
		return true;
	}

	private static boolean lookForSampleClient(World aWorld, int x, int y, int z, EntityPlayer aPlayer) {
		// works client-side, since it's based only on block meta-id
		net.minecraft.block.Block b = aWorld.getBlock(x, y, z);

		if (b instanceof gregapi.block.prefixblock.PrefixBlock) {
			final ItemStack sample = ((gregapi.block.prefixblock.PrefixBlock) b).getItemStackFromBlock(aWorld, x, y,
					z, gregapi.data.CS.SIDE_INVALID);
			final String tName = b.getUnlocalizedName();
			if (tName.endsWith(".bedrock")) {
				TakeSample(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.BEDROCK_ORE_VEIN, aPlayer);
			} else if (tName.startsWith("gt.meta.ore.normal.")) {
				TakeSample(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.ORE_VEIN, aPlayer);
			}
		} else if (b instanceof gregapi.block.misc.BlockBaseFlower) {
			TakeSample(aWorld, x, y, z, (short) 0, Utils.FLOWER_ORE_MARKER, aPlayer);
		} else if (ConfigHandler.trackStoneRocks && b instanceof BlockStones
				&& b.getDamageValue(aWorld, x, y, z) == BlockStones.STONE) {
			TakeSample(aWorld, x, y, z, ((BlockStones) b).mMaterial.mID, Utils.STONE_LAYER, aPlayer);
		}
		return true;
	}

	/**
	 * Determines if an ore sample can be generated from this location, then calls
	 * TakeSample to do so.
	 * 
	 * @param aWorld
	 * @param x
	 * @param y
	 * @param z
	 * @param aPlayer
	 * @return
	 */
	public static boolean lookForSample(World aWorld, int x, int y, int z, EntityPlayer aPlayer) {
		if (aWorld.isRemote) {
			return lookForSampleClient(aWorld, x, y, z, aPlayer);
		}

		return lookForSampleServer(aWorld, x, y, z, aPlayer);
	}

	// @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.SERVER)
	static void TakeSampleServer(final World aWorld, int x, int y, int z, short meta, byte sourceType,
			final EntityPlayer aPlayer) {
		if (sourceType == Utils.STONE_LAYER && (meta == 8649 || meta == 8757)) {
			// ignore meteors
		} else {
			Utils.NW_PJ.sendToPlayer(new PacketOreSurvey(x, y, z, meta, sourceType), (EntityPlayerMP) aPlayer);
		}
	}

	/**
	 * Generates an ore sample knowledge for this location.
	 * 
	 * @param aWorld
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 * @param sourceType
	 * @param aPlayer
	 */
	// @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
	public static void TakeSample(final World aWorld, int x, int y, int z, short meta, byte sourceType,
			final EntityPlayer aPlayer) {
		final int dim = aWorld.provider.dimensionId;
		Utils.debugLog("Sampling " + meta + " at " + x + "," + y + "," + z
				+ " on world " + dim);

		final String oreName = meta == 0 ? "Unknown Ore" : OreDictMaterial.MATERIAL_ARRAY[meta].mNameLocal;
		if (sourceType == Utils.STONE_LAYER) {
			boolean found = false;
			for (String stoneName : ConfigHandler.stoneBlacklist) {
				if (stoneName.equals(oreName)) {
					found = true;
					break;
				}
			}

			if (found != ConfigHandler.stoneBlacklistInverted) {
				Utils.debugLog("Blacklisted stone found: " + oreName);
				return; // blacklisted
			}
		}

		if (GT6MWGeologyMarker.mapWriterInstance == null) {
			return;
		}

		final mapwriter.map.MarkerManager markerManager = GT6MWGeologyMarker.mapWriterInstance.markerManager;

		final int chunkX = x / 16;
		final int chunkZ = z / 16;
		ListIterator<mapwriter.map.Marker> iter = markerManager.markerList
				.listIterator();
		while (iter.hasNext()) {
			final mapwriter.map.Marker marker = iter.next();

			final int markerChunkX = marker.x / 16;
			final int markerChunkZ = marker.z / 16;

			Utils.debugLog("Check marker (bedrock) {" + marker.name + ", " + markerChunkX + ", " + markerChunkZ
					+ "}" + " against {"
					+ oreName + ", " + chunkX + ", " + chunkZ + "}");

			if (oreName.equals(marker.name) && dim == marker.dimension
					&& Utils.IsInNChunksFrom(ConfigHandler.veinDistance, markerChunkX, markerChunkZ, chunkX,
							chunkZ)) {
				return;
			}
		}

		// Found traces of a new vein
		switch (sourceType) {
			case Utils.ORE_VEIN:
				Utils.createMapMarker(x, y, z, dim, oreName, Utils.OreVeinGroup, aPlayer);
				break;
			case Utils.STONE_LAYER:
				Utils.createMapMarker(x, y, z, dim, oreName, Utils.StoneLayerGroup, aPlayer);
				break;
			default:
				Utils.createMapMarker(x, y, z, dim, oreName, Utils.BedrockVeinGroup, aPlayer);
				break;
		}
	}
}
