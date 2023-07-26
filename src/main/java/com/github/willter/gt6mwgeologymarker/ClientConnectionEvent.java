package com.github.willter.gt6mwgeologymarker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import java.io.File;
import java.net.InetSocketAddress;

import com.github.willter.gt6mwgeologymarker.lib.Utils;

public class ClientConnectionEvent {
    public static final String PJ_FOLDER = "GT6MWGeologyMarker/";
    private boolean CLIENT_JUST_CONNECTED = true;

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        String hostname;
        CLIENT_JUST_CONNECTED = false;

        if (!event.isLocal) {
            InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
            hostname = address.getHostString() + "_" + address.getPort();

        } else {
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            hostname = (server != null) ? server.getFolderName() : "sp_world";
        }

        hostname = Utils.invalidChars(hostname);
        hostname = PJ_FOLDER + hostname;

        File fileJson = new File(hostname);
        if (!fileJson.exists()) {
            System.out.println("Creating new directory " + hostname);
            fileJson.mkdirs();
        }

        GT6MWGeologyMarker.hostName = hostname;

        Utils.readJson(Utils.GT_FILE);
        Utils.readJson(Utils.GT_BED_FILE);
    }

    @SubscribeEvent
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void onPlayerTickEventClient(cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent aEvent) {
        if (CLIENT_JUST_CONNECTED && aEvent.phase == Phase.END && aEvent.side.isClient()) {
            CLIENT_JUST_CONNECTED = false;
            System.out.println(GT6MWGeologyMarker.MOD_NAME
                    + "!!Warning!! Initializing on Client Player Tick. On server join has failed!");

            String hostname;
            if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
                hostname = (server != null) ? server.getFolderName() : "sp_world";
            } else {
                hostname = (Minecraft.getMinecraft().func_147104_D().serverName + "_"
                        + Minecraft.getMinecraft().func_147104_D().serverIP);
            }

            hostname = Utils.invalidChars(hostname);
            hostname = PJ_FOLDER + hostname;

            File fileJson = new File(hostname);
            if (!fileJson.exists()) {
                System.out.println("Creating new directory " + hostname);
                fileJson.mkdirs();
            }

            GT6MWGeologyMarker.hostName = hostname;

            Utils.readJson(Utils.GT_FILE);
            Utils.readJson(Utils.GT_BED_FILE);
        }
    }

}
