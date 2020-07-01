package com.ddylan.library.xpacket;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

public class XPacketPubSub extends JedisPubSub {

    public void onMessage(String channel, String message) {
        Class<?> packetClass;
        int packetMessageSplit = message.indexOf("||");

        String packetClassStr = message.substring(0, packetMessageSplit);
        String messageJson = message.substring(packetMessageSplit + "||".length());


        try {
            packetClass = Class.forName(packetClassStr);
        } catch (ClassNotFoundException ignored) {
            return;
        }


        XPacket packet = (XPacket) LibraryPlugin.getInstance().getXJedis().PLAIN_GSON.fromJson(messageJson, packetClass);

        if (LibraryPlugin.getInstance().isEnabled())
            Bukkit.getScheduler().runTask(LibraryPlugin.getInstance(), packet::onReceive);
    }

}