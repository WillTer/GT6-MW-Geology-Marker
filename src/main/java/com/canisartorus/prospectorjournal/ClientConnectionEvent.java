package com.canisartorus.prospectorjournal;

// @Author Dyonovan

import com.canisartorus.prospectorjournal.ProspectorJournal;
import com.canisartorus.prospectorjournal.lib.Utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import java.io.File;
import java.net.InetSocketAddress;

public class ClientConnectionEvent {

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        String hostname;

        if (!event.isLocal) {

            InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
            hostname = address.getHostName() + "_" + address.getPort();

        } else {

            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            hostname = (server != null) ? server.getFolderName() : "sp_world";
        }

        hostname = Utils.invalidChars(hostname);
        hostname = "ProspectorJournal/" + hostname;

        File fileJson = new File(hostname);
        if (!fileJson.exists()) {
            fileJson.mkdirs();
        }

        ProspectorJournal.hostName = hostname;
        ProspectorJournal.rockSurvey.clear();

        Utils.readJson();

    }

}