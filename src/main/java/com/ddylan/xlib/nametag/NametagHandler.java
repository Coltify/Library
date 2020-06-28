package com.ddylan.xlib.nametag;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.packet.ScoreboardTeamPacketMod;
import com.ddylan.xlib.util.Color;
import com.google.common.primitives.Ints;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NametagHandler {

    @Getter(AccessLevel.PROTECTED) private Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<>();
    private List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    private int teamCreateIndex = 1;
    private List<NametagProvider> providers = new ArrayList<>();
    @Getter @Setter private boolean nametagRestrictionEnabled = false;
    @Getter @Setter private String nametagRestrictBypass = "";
    @Getter @Setter private boolean async;
    @Getter @Setter private int updateInterval;
    @Getter private boolean initiated = false;

    public NametagHandler() {
        if (Library.getInstance().getConfig().getBoolean("disableNametags", false)) {
            Library.getInstance().getLogger().fine("'disableNametags' is set to false in 'config.yml'");
            Library.getInstance().getLogger().fine("This is fine, NametagHandler will simply not initiate.");
            return;
        }
        if (!initiated) {
            initiated = true;
            nametagRestrictionEnabled = Library.getInstance().getConfig().getBoolean("NametagPacketRestriction.Enabled", false);
            nametagRestrictBypass = Color.translate(Library.getInstance().getConfig().getString("NametagPacketRestriction.BypassPrefix"));

            new NametagThread().start();
            Library.getInstance().getServer().getPluginManager().registerEvents(new NametagListener(), Library.getInstance());
            registerProvider(new NametagProvider.DefaultNametagProvider());
        } else {
            Library.getInstance().getLogger().fine("NametagHandler has been called to initiate again...");
            Library.getInstance().getLogger().fine("You aren't trying to initiate this externally, right?");
        }
    }

    public void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);

        providers.sort((a, b) -> Ints.compare(b.getWeight(), a.getWeight()));
    }

    public NametagInfo getOrCreate(String prefix, String suffix) {
        for (NametagInfo info : registeredTeams) {
            if (info.getPrefix().equals(prefix) && info.getSuffix().equals(suffix)) {
                return info;
            }
        }
        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getPacket();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return newTeam;
    }

    public void applyUpdate(NametagUpdate pendingUpdate) {
        Player toRefreshPlayer = Library.getInstance().getServer().getPlayerExact(pendingUpdate.getToRefresh());

        if (toRefreshPlayer == null) {
            return;
        }

        if (pendingUpdate.getRefreshFor() == null) {
            for (Player refreshFor : Library.getInstance().getServer().getOnlinePlayers()) {
                reloadPlayerInternal(toRefreshPlayer, refreshFor);
            }
        } else {
            Player refreshForPlayer = Library.getInstance().getServer().getPlayerExact(pendingUpdate.getRefreshFor());

            if (refreshForPlayer != null) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    protected void reloadPlayerInternal(Player toRefreshPlayer, Player refreshFor) {
        if (!refreshFor.hasMetadata("xLibNametag-LoggedIn")) {
            return;
        }
        NametagInfo provided = null;
        int providerIndex = 0;

        while (provided == null) {
            provided = providers.get(providerIndex++).fetchNametag(toRefreshPlayer, refreshFor);
        }

        if ((((CraftPlayer)refreshFor).getHandle()).playerConnection.networkManager.getVersion() > 5 &&
                nametagRestrictionEnabled) {
            String prefix = provided.getPrefix();
            if (prefix != null && !prefix.equalsIgnoreCase(nametagRestrictBypass)) {
                return;
            }
        }


        Map<String, NametagInfo> teamInfoMap = new HashMap<>();

        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }

        (new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefreshPlayer.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefreshPlayer.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }


    public void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Library.getInstance().getServer().getOnlinePlayers()) {
            if (refreshFor == toRefresh)
                continue;  reloadPlayer(toRefresh, refreshFor);
        }
    }

    public void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }


    protected void initiatePlayer(Player player) {
        for (NametagInfo teamInfo : registeredTeams) {
            teamInfo.getPacket().sendToPlayer(player);
        }
    }
}
