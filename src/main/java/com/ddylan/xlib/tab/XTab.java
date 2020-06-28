package com.ddylan.xlib.tab;

import com.ddylan.xlib.packet.ScoreboardTeamPacketMod;
import com.ddylan.xlib.Library;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class XTab {

    private Player player;
    private Map<String, String> previousNames = new HashMap<>();
    private Map<String, Integer> previousPings = new HashMap<>();
    private String lastHeader = "{\"translate\":\"\"}";
    private String lastFooter = "{\"translate\":\"\"}";
    private Set<String> createdTeams = new HashSet<>();
    private TabLayout initialLayout;
    @Getter
    private boolean initiated = false;

    private StringBuilder removeColorCodesBuilder = new StringBuilder();

    public XTab(Player player) {
        this.player = player;
    }

    private void createAndAddMember(String name, String member) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod("$" + name, "", "", Collections.singletonList(member), 0);
        scoreboardTeamAdd.sendToPlayer(this.player);
    }


    private void init() {
        if (!this.initiated) {
            this.initiated = true;
            TabLayout initialLayout = TabLayout.createEmpty(this.player);
            if (!initialLayout.is18()) {
                for (Player n : Bukkit.getOnlinePlayers()) {
                    updateTabList(n.getName(), 0, ((CraftPlayer) n).getProfile(), 4);
                }
            }

            for (String s : initialLayout.getTabNames()) {
                updateTabList(s, 0, 0);
                String teamName = s.replaceAll("§", "");
                if (!this.createdTeams.contains(teamName)) {
                    createAndAddMember(teamName, s);
                    this.createdTeams.add(teamName);
                }
            }
            this.initialLayout = initialLayout;
        }
    }

    private void updateScore(String score, String prefix, String suffix) {
        ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2);
        scoreboardTeamModify.sendToPlayer(this.player);
    }

    private void updateTabList(String name, int ping, int action) {
        updateTabList(name, ping, TabUtils.getOrCreateProfile(name), action);
    }

    private void updateTabList(String name, int ping, GameProfile profile, int action) {
        PlayerInfoPacketMod playerInfoPacketMod = new PlayerInfoPacketMod("$" + name, ping, profile, action);
        playerInfoPacketMod.sendToPlayer(this.player);
    }


    private String[] splitString(String line) {
        if (line.length() <= 16) {
            return new String[]{line, ""};
        }
        return new String[]{line.substring(0, 16), line.substring(16)};
    }


    protected void update() {
        if (Library.getInstance().getTabHandler().getLayoutProvider() != null) {
            TabLayout tabLayout = Library.getInstance().getTabHandler().getLayoutProvider().provide(this.player);
            if (tabLayout == null) {
                if (this.initiated) {
                    reset();
                }
                return;
            }
            init();

            for (int y = 0; y < TabLayout.HEIGHT; y++) {
                for (int x = 0; x < TabLayout.WIDTH; x++) {
                    String entry = tabLayout.getStringAt(x, y);
                    int ping = tabLayout.getPingAt(x, y);
                    String entryName = this.initialLayout.getStringAt(x, y);

                    this.removeColorCodesBuilder.setLength(0);
                    this.removeColorCodesBuilder.append('$');
                    this.removeColorCodesBuilder.append(entryName);
                    int j = 0;

                    for (int i = 0; i < this.removeColorCodesBuilder.length(); i++) {
                        if ('§' != this.removeColorCodesBuilder.charAt(i)) {
                            this.removeColorCodesBuilder.setCharAt(j++, this.removeColorCodesBuilder.charAt(i));
                        }
                    }

                    this.removeColorCodesBuilder.delete(j, this.removeColorCodesBuilder.length());
                    String teamName = this.removeColorCodesBuilder.toString();


                    if (this.previousNames.containsKey(entryName)) {
                        if (!this.previousNames.get(entryName).equals(entry)) {
                            update(entryName, teamName, entry, ping);
                        } else if (this.previousPings.containsKey(entryName) &&
                                pingToBars(this.previousPings.get(entryName)) != pingToBars(ping)) {
                            updateTabList(entryName, ping, 2);
                            this.previousPings.put(entryName, Integer.valueOf(ping));
                        }
                    } else {
                        update(entryName, teamName, entry, ping);
                    }
                }
            }
            boolean sendHeader = false;
            boolean sendFooter = false;
            String header = tabLayout.getHeader(), footer = tabLayout.getFooter();
            if (!header.equals(this.lastHeader)) {
                sendHeader = true;
            }

            if (!footer.equals(this.lastFooter)) {
                sendFooter = true;
            }

            if (tabLayout.is18() && (sendHeader || sendFooter)) {
//FIXME                ProtocolInjector.PacketTabHeader packet = new ProtocolInjector.PacketTabHeader(header, footer);
                this.lastHeader = header;
                this.lastFooter = footer;
            }
        }
    }

    private int pingToBars(int ping) {
        if (ping < 0) return 5;
        if (ping < 150) return 0;
        if (ping < 300) return 1;
        if (ping < 600) return 2;
        if (ping < 1000) return 3;
        if (ping < 32767) return 4;
        return 5;
    }


    private void reset() {
        this.initiated = false;

        for (String s : this.initialLayout.getTabNames()) {
            updateTabList(s, 0, 4);
        }


        EntityPlayer ePlayer = ((CraftPlayer) this.player).getHandle();


        updateTabList(this.player.getName(), ePlayer.ping, ePlayer.getProfile(), 0);

        int count = 1;
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (this.player == player) {
                continue;
            }

            if (count > (this.initialLayout.getTabNames()).length - 1) {
                break;
            }


            ePlayer = ((CraftPlayer) player).getHandle();

            updateTabList(player.getName(), ePlayer.ping, ePlayer.getProfile(), 0);
            count++;
        }
    }


    private void update(String entryName, String teamName, String entry, int ping) {
        String[] entryStrings = splitString(entry);
        String prefix = entryStrings[0];
        String suffix = entryStrings[1];

        if (!suffix.isEmpty()) {

            if (prefix.charAt(prefix.length() - 1) == '§') {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = '§' + suffix;
            }
            String suffixPrefix = ChatColor.RESET.toString();

            if (!ChatColor.getLastColors(prefix).isEmpty()) {
                suffixPrefix = ChatColor.getLastColors(prefix);
            }

            if (suffix.length() <= 14) {
                suffix = suffixPrefix + suffix;
            } else {
                suffix = suffixPrefix + suffix.substring(0, 14);
            }

            updateScore(teamName, prefix, suffix);
            updateTabList(entryName, ping, 2);
            this.previousNames.put(entryName, entry);
            this.previousPings.put(entryName, ping);
        }
    }

}
