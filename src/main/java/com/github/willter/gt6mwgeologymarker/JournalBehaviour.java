package com.github.willter.gt6mwgeologymarker;

import com.github.willter.gt6mwgeologymarker.lib.GeoTag;
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

	static final short[] multiFlowers = { 9130, 9211, 9133, 9194, 9217, 9193, 9128, 9195, 9196, 9197 };

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
			if (ConfigHandler.trackRock) {
				TakeSampleServer(aWorld, x, y, z,
						(short) ((TileEntityBase03MultiTileEntities) i).getDrops(0, false).get(0)
								.getItemDamage(),
						Utils.STONE_LAYER, aPlayer);
			}
		} else if (gregapi.util.OM.is(gregapi.data.OD.itemFlint, sample)) {
			// ignore
		} else if (gregapi.util.OM.materialcontains(sample, gregapi.data.TD.Properties.STONE)) {
			if (ConfigHandler.trackRock) {
				TakeSampleServer(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.STONE_LAYER, aPlayer);
			}
		} else if (gregapi.data.OP.oreRaw.contains(sample)) {
			TakeSampleServer(aWorld, x, y, z, (short) sample.getItemDamage(), Utils.FLOWER_ORE_MARKER, aPlayer);
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
			short type = 0;
			final int metadata = aWorld.getBlockMetadata(x, y, z);
			if (b.getUnlocalizedName().equalsIgnoreCase("gt.block.flower.a")) {
				// TODO: map or ...?
				switch (metadata) {
					case 0: // Gold
						type = 790;
						break;
					case 1: // Galena
						type = 9117;
						break;
					case 2: // Chalcopyrite
						type = 9111;
						break;
					case 3: // Sphalerite & Smithsonite
						type = 0; // either 9130 or 9211
						break;
					case 4: // Pentlandite
						type = 9145;
						break;
					case 5: // Uraninite
						type = 9134;
						break;
					case 6: // Cooperite
						type = 9116;
						break;
					case 8: // any Hexorium
					case 7: // generic Orechid
						break;
					default:
						Utils.debugLog("Found unregistered ore with block metadata " + metadata);
						break;
				}
			} else if (b.getUnlocalizedName().equalsIgnoreCase("gt.block.flower.b")) {
				// TODO: map or ...?
				switch (metadata) {
					case 0: // Arsenopyrite
						type = 9216;
						break;
					case 1: // Stibnite
						type = 9131;
						break;
					case 2: // Gold
						type = 790;
						break;
					case 3: // Copper
						type = 290;
						break;
					case 4: // Redstone
						type = 8333;
						break;
					case 5: // Pitchblende
						type = 9155;
						break;
					case 6: // Diamonds
						type = 8300;
						break;
					case 7: // any W
						type = 0; // any of 9133, 9194, 9217, 9193, 9128, 9195, 9196, 9197
						break;
					default:
						Utils.debugLog("Found unregistered ore with block metadata " + metadata);
						break;
				}
			}
			TakeSample(aWorld, x, y, z, type, Utils.FLOWER_ORE_MARKER, aPlayer);
		} else if (ConfigHandler.trackRock && b instanceof BlockStones
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

		final String oreName = OreDictMaterial.MATERIAL_ARRAY[meta].mNameLocal;
		if (sourceType == Utils.STONE_LAYER) {
			for (String stoneName : ConfigHandler.stoneBlacklist) {
				if (stoneName == oreName) {
					Utils.debugLog("Blacklisted stone found: " + oreName);
					return; // blacklisted
				}
			}
		}

		final int chunkX = x / 16;
		final int chunkZ = z / 16;
		if (sourceType == Utils.FLOWER_ORE_MARKER || sourceType == Utils.BEDROCK_ORE_VEIN) {
			boolean match = false;
			if (GT6MWGeologyMarker.bedrockFault.size() != 0) {
				for (GeoTag tag : GT6MWGeologyMarker.bedrockFault) {
					if (dim == tag.dim && meta == tag.ore) {
						// include adjacent chunks as same unit.
						// generates a 32 pattern of indicators, and a 6 spread of ores.
						if (tag.IsInNChunksFrom(ConfigHandler.veinDistance, chunkX, chunkZ)) {
							match = true;
							break;
						}
					} else if (tag.dim == dim && tag.ore == 0) {
						// find a vein under non-specific flowers
						boolean tSpecify = (sourceType == Utils.BEDROCK_ORE_VEIN);
						// allow the confusing Sphalerite / Smithsonite flower to be specified by the
						// raw ore chunk
						// and the various tungsten ores too
						for (int i = 0, j = multiFlowers.length; i < j && !tSpecify; i++) {
							if (tag.ore == multiFlowers[i]) {
								tSpecify = true;
							}
						}
						if (tSpecify && tag.IsInNChunksFrom(ConfigHandler.veinDistance, chunkX, chunkZ)) {
							GT6MWGeologyMarker.bedrockFault.remove(tag);
							match = false;
							continue;
						}
					} else if (tag.dim == dim && meta == 0) {
						if (tag.IsInNChunksFrom(ConfigHandler.veinDistance, chunkX, chunkZ)) {
							if (!tag.sample) {
								match = true;
								break;
							}
							for (int i = 0, j = multiFlowers.length; i < j; i++) {
								if (tag.ore == multiFlowers[i]) {
									match = true;
									break;
								}
							}
						}
					}
					if (match)
						break;
				}
			}
			if (!match) {
				// make a new entry
				GT6MWGeologyMarker.bedrockFault
						.add(new GeoTag(meta, dim, chunkX, chunkZ,
								sourceType == Utils.BEDROCK_ORE_VEIN ? false : true));
				Utils.writeJson(Utils.GT_BED_FILE);
			}
		}

		if (meta == 0) {
			return;
		}

		// ignore non-specific rocks and empty ores
		if (GT6MWGeologyMarker.rockSurvey.size() != 0) {
			for (GeoTag rock : GT6MWGeologyMarker.rockSurvey) {
				if (meta == rock.ore && dim == rock.dim
						&& rock.IsInNChunksFrom(ConfigHandler.veinDistance, chunkX, chunkZ)) {
					switch (sourceType) {
						case Utils.ORE_VEIN:
							if (!rock.sample) {
								rock.dead = false;
								Utils.writeJson(Utils.GT_FILE);
								return;
							}
							break;
						case Utils.STONE_LAYER: // result of server-side message only
							if (!rock.sample) {
								return;
							}
							break;
						default:
							return;
					}
				}
			}
		}

		// Found traces of a new vein
		switch (sourceType) {
			case Utils.ORE_VEIN:
				GT6MWGeologyMarker.rockSurvey.add(new GeoTag(meta, dim, chunkX, chunkZ, false));
				Utils.createMapMarker(x, y, z, dim, oreName, "Ore Veins", aPlayer);
				break;
			case Utils.STONE_LAYER:
				GT6MWGeologyMarker.rockSurvey.add(new GeoTag(meta, dim, chunkX, chunkZ, false));
				Utils.createMapMarker(x, y, z, dim, oreName, "Stone Layers", aPlayer);
				break;
			default:
				GT6MWGeologyMarker.rockSurvey.add(new GeoTag(meta, dim, chunkX, chunkZ, true));
				Utils.createMapMarker(x, y, z, dim, oreName, "Bedrock Ore Veins", aPlayer);
				break;
		}
		Utils.writeJson(Utils.GT_FILE);
	}
}
