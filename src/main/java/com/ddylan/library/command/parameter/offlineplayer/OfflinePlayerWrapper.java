package com.ddylan.library.command.parameter.offlineplayer;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.util.callback.Callback;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OfflinePlayerWrapper
{
    private String source;

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }
    private UUID uniqueId; private String name;
    public void loadAsync(final Callback<Player> callback) {
        (new BukkitRunnable() {
            public void run() {
                final Player player = OfflinePlayerWrapper.this.loadSync();
                (new BukkitRunnable() {
                    public void run() {
                        callback.callback(player);
                    }
                }).runTask(LibraryPlugin.getInstance());
            }
        }).runTaskAsynchronously(LibraryPlugin.getInstance());
    }

    public Player loadSync() {
        if ((this.source.charAt(0) == '"' || this.source.charAt(0) == '\'') && (this.source.charAt(this.source.length() - 1) == '"' || this.source.charAt(this.source.length() - 1) == '\'')) {
            this.source = this.source.replace("'", "").replace("\"", "");
            this.uniqueId = LibraryPlugin.getInstance().getUuidCache().uuid(this.source);
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }
            this.name = LibraryPlugin.getInstance().getUuidCache().name(this.uniqueId);
            if (Bukkit.getPlayer(this.uniqueId) != null) {
                return Bukkit.getPlayer(this.uniqueId);
            }
            if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }
            MinecraftServer minecraftServer = ((CraftServer)Bukkit.getServer()).getServer();
            EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, minecraftServer.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager((World)minecraftServer.getWorldServer(0)));
            CraftPlayer craftPlayer1 = entityPlayer.getBukkitEntity();
            if (craftPlayer1 != null) {
                craftPlayer1.loadData();
            }
            return (Player)craftPlayer1;
        }

        if (Bukkit.getPlayer(this.source) != null) {
            return Bukkit.getPlayer(this.source);
        }
        this.uniqueId = LibraryPlugin.getInstance().getUuidCache().uuid(this.source);
        if (this.uniqueId == null) {
            this.name = this.source;
            return null;
        }
        this.name = LibraryPlugin.getInstance().getUuidCache().name(this.uniqueId);
        if (Bukkit.getPlayer(this.uniqueId) != null) {
            return Bukkit.getPlayer(this.uniqueId);
        }
        if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
            return null;
        }
        MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager((World)server.getWorldServer(0)));
        CraftPlayer craftPlayer = entity.getBukkitEntity();
        if (craftPlayer != null) {
            craftPlayer.loadData();
        }
        return (Player)craftPlayer;
    }


    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }
}
