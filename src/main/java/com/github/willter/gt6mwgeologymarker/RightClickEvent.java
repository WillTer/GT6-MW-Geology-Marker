package com.github.willter.gt6mwgeologymarker;

/**	@author Alexander James
	@author Dyonovan
**/

import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

//@cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
public class RightClickEvent {

	@cpw.mods.fml.common.eventhandler.SubscribeEvent
	public void playerRightClick(PlayerInteractEvent event) {
		final World world = event.entityPlayer.worldObj;
		if (event.isCanceled() || // !world.isRemote ||
				event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		JournalBehaviour.lookForSample(world, event.x, event.y, event.z, event.entityPlayer);
	}
}
