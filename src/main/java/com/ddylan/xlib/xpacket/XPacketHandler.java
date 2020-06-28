package com.ddylan.xlib.xpacket;

import com.ddylan.xlib.Library;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class XPacketHandler {

    private final String GLOBAL_MESSAGE_CHANNEL = "XPacket:All";
    final String PACKET_MESSAGE_DIVIDER = "||";

    public XPacketHandler() {
        FileConfiguration config = Library.getInstance().getConfig();

        String localHost = config.getString("Redis.Host");
        int localDb = config.getInt("Redis.DbId", 0);
        String remoteHost = config.getString("BackboneRedis.Host");
        int remoteDb = config.getInt("BackboneRedis.DbId", 0);

        boolean sameServer = (localHost.equalsIgnoreCase(remoteHost) && localDb == remoteDb);

        connectToServer(Library.getInstance().getXJedis().getLocalJedisPool());

        if (!sameServer) {
            connectToServer(Library.getInstance().getXJedis().getBackboneJedisPool());
        }
    }

    public void connectToServer(JedisPool connectTo) {
        Thread subscribeThread = new Thread(() -> {
            while (Library.getInstance().isEnabled()) {
                try (Jedis jedis = connectTo.getResource()) {
                    JedisPubSub pubSub = new XPacketPubSub();

                    String channel = "XPacket:All";

                    jedis.subscribe(pubSub, channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"xLib - XPacket Subscribe Thread");

        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    public void sendToAll(XPacket packet) {
        send(packet, Library.getInstance().getXJedis().getBackboneJedisPool());
    }

    public void sendToAllViaLocal(XPacket packet) {
        send(packet, Library.getInstance().getXJedis().getLocalJedisPool());
    }

    public void send(XPacket packet, JedisPool sendOn) {
        if (!Library.getInstance().isEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Library.getInstance(), () -> {
            try (Jedis jedis = sendOn.getResource()) {
                String encodedPacket = packet.getClass().getName() + "||" + Library.getInstance().getXJedis().PLAIN_GSON.toJson(packet);
                jedis.publish("XPacket:All", encodedPacket);
            }
        });
    }

}
