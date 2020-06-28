package com.ddylan.xlib.boss;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.util.EntityUtils;
import lombok.AllArgsConstructor;
import net.minecraft.server.v1_7_R4.v1_7_R4.*;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.Field;
import java.util.*;

public class BossBarHandler {

    private Map<UUID, BarData> displaying = new HashMap<>();
    private Map<UUID, Integer> lastUpdatedPosition = new HashMap<>();

    private Field spawnPacketAField = null;
    private Field spawnPacketBField = null;
    private Field spawnPacketCField = null;
    private Field spawnPacketDField = null;
    private Field spawnPacketEField = null;
    private Field spawnPacketLField = null;

    private Field metadataPacketAField = null;
    private Field metadataPacketBField = null;

    private TObjectIntHashMap classToIdMap = null;

    private void reflect() {
        try {
            spawnPacketAField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
            spawnPacketAField.setAccessible(true);

            spawnPacketBField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("b");
            spawnPacketBField.setAccessible(true);

            spawnPacketCField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c");
            spawnPacketCField.setAccessible(true);

            spawnPacketDField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("d");
            spawnPacketDField.setAccessible(true);

            spawnPacketEField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("e");
            spawnPacketEField.setAccessible(true);

            spawnPacketLField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            spawnPacketLField.setAccessible(true);

            metadataPacketAField = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            metadataPacketAField.setAccessible(true);

            metadataPacketBField = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            metadataPacketBField.setAccessible(true);

            Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");
            dataWatcherClassToIdField.setAccessible(true);

            classToIdMap = (TObjectIntHashMap)dataWatcherClassToIdField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BossBarHandler() {
        reflect();
        Bukkit.getScheduler().runTaskTimer(Library.getInstance(), () -> {
            for (UUID uuid : displaying.keySet()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null) return;
                int updateTicks = ((((CraftPlayer) player).getHandle()).playerConnection.networkManager.getVersion() != 47) ? 60 : 3;
                if (lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    updatePosition(player);
                    lastUpdatedPosition.put(player.getUniqueId(), MinecraftServer.currentTick);
                }
            }
        }, 1L, 1L);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                removeBossBar(event.getPlayer());
            }

            @EventHandler
            public void onPlayerTelepot(PlayerTeleportEvent event) {
                Player player = event.getPlayer();

                if (!displaying.containsKey(player.getUniqueId())) {
                    return;
                }

                BossBarHandler.BarData data = displaying.get(player.getUniqueId());

                String message = data.message;
                float health = data.health;

                removeBossBar(event.getPlayer());
                setBossBar(event.getPlayer(), message, health);
            }
        }, Library.getInstance());
    }

    public void setBossBar(Player player, String message, float health) {
        try {
            if (message  == null) {
                removeBossBar(player);
                return;
            }

            if (!(health >= 0.0F && health <= 1.0F)) {
                throw new IllegalArgumentException("Health must be between 0 and 1");
            }

            if (message.length() > 64) message = message.substring(0, 64);
            message = ChatColor.translateAlternateColorCodes('&', message);

            if (!displaying.containsKey(player.getUniqueId())) {
                sendSpawnPacket(player, message, health);
            } else {
                sendUpdatePacket(player, message, health);
            }

            displaying.get(player.getUniqueId()).message = message;
            displaying.get(player.getUniqueId()).health = health;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeBossBar(Player player) {
        if (!displaying.containsKey(player.getUniqueId())) {
            return;
        }

        int entityId = displaying.get(player.getUniqueId()).entityId;
        (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));

        displaying.remove(player.getUniqueId());
        lastUpdatedPosition.remove(player.getUniqueId());
    }

    private void sendUpdatePacket(Player player, String message, float health) throws IllegalAccessException {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        int version = entityPlayer.playerConnection.networkManager.getVersion();
        BarData stored = displaying.get(player.getUniqueId());
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();

        metadataPacketAField.set(packet, stored.entityId);

        List<WatchableObject> objects = new ArrayList<>();

        if (health != stored.health) {
            if (version != 47) {
                objects.add(createWatchableObject(6, health * 200.0F));
            } else {
                objects.add(createWatchableObject(6, health * 300.0F));
            }
        }

        if (!message.equals(stored.message)) {
            objects.add(createWatchableObject(version != 47 ? 10 : 2, message));
        }

        metadataPacketBField.set(packet, objects);
        entityPlayer.playerConnection.sendPacket(packet);
    }

    private WatchableObject createWatchableObject(int id, Object object) {
        return new WatchableObject(classToIdMap.get(object.getClass()), id, object);
    }

    private void sendSpawnPacket(Player player, String message, float health) throws IllegalAccessException {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        int version = entityPlayer.playerConnection.networkManager.getVersion();
        displaying.put(player.getUniqueId(), new BarData(EntityUtils.getFakeEntityId(), message, health));
        BarData stored = displaying.get(player.getUniqueId());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        spawnPacketAField.set(packet, stored.entityId);
        DataWatcher watcher = new DataWatcher((Entity) null);

        if (version != 47) {
            spawnPacketBField.set(packet, (byte) EntityType.ENDER_DRAGON.getTypeId());
            watcher.a(6, (float) health * 200.0F);
            spawnPacketCField.set(packet, (int) entityPlayer.locX * 32);
            spawnPacketDField.set(packet, (int)  -6400);
            spawnPacketEField.set(packet, (int)  entityPlayer.locZ * 32);
        } else {
            spawnPacketBField.set(packet, (byte) EntityType.WITHER.getTypeId());

            watcher.a(6, health * 300.0F);
            watcher.a(20, 880);

            double pitch = Math.toRadians(entityPlayer.pitch);
            double yaw = Math.toRadians(entityPlayer.yaw);

            spawnPacketCField.set(packet, (int) (entityPlayer.locX - Math.sin(yaw) * Math.cos(pitch) * 32) * 32);
            spawnPacketDField.set(packet, (int) (entityPlayer.locY - Math.sin(pitch) * 32) * 32);
            spawnPacketEField.set(packet, (int) (entityPlayer.locZ + Math.sin(yaw) * Math.cos(pitch) * 32) * 32);
        }

        watcher.a(version != 47 ? 10 : 2, message);
        spawnPacketLField.set(packet, watcher);
        entityPlayer.playerConnection.sendPacket(packet);
    }

    private void updatePosition(Player player) {
        int x, y, z;
        if (!displaying.containsKey(player.getUniqueId())) {
            return;
        }

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        int version = entityPlayer.playerConnection.networkManager.getVersion();

        if (version != 47) {
            x = (int)(entityPlayer.locX * 32.0D);
            y = -6400;
            z = (int)(entityPlayer.locZ * 32.0D);
        } else {
            double pitch = Math.toRadians(entityPlayer.pitch);
            double yaw = Math.toRadians(entityPlayer.yaw);

            x = (int)((entityPlayer.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
            y = (int)((entityPlayer.locY - Math.sin(pitch) * 32.0D) * 32.0D);
            z = (int)((entityPlayer.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
        }

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityTeleport((displaying.get(player.getUniqueId())).entityId, x, y, z, (byte)0, (byte)0));
    }

    @AllArgsConstructor
    private class BarData {

        private int entityId;
        private String message;
        private float health;

    }

}
