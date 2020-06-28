package com.ddylan.xlib.scoreboard;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.packet.ScoreboardTeamPacketMod;
import com.ddylan.xlib.util.Color;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.server.v1_7_R4.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.Field;
import java.util.*;

public class Scoreboard {

    private Player player;
    private Objective objective;
    private Map<String, Integer> displayedScores = new HashMap<>();
    private Map<String, String> scorePrefixes = new HashMap<>();
    private Map<String, String> scoreSuffixes = new HashMap<>();
    private Set<String> sentTeamCreates = new HashSet<>();

    private final StringBuilder separateScoreBuilder = new StringBuilder();
    private final List<String> separateScores = new ArrayList<>();
    private final Set<String> recentlyUpdatedScores = new HashSet<>();
    private final Set<String> usedBaseScores = new HashSet<>();
    private final String[] prefixScoreSuffix = new String[3];

    private final ThreadLocal<LinkedList<String>> localList = ThreadLocal.withInitial(LinkedList::new);

    public Scoreboard(Player player) {
        this.player = player;

        org.bukkit.scoreboard.Scoreboard board = Library.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        this.objective = board.registerNewObjective("LOLPVP", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(board);
    }

    public void update() {
        String untranslatedTitle = Library.getInstance().getScoreboardHandler().getConfiguration().getTitleGetter().getTitle(this.player);
        String title = Color.translate(untranslatedTitle);

        List<String> lines = this.localList.get();
        if (!lines.isEmpty()) lines.clear();
        Library.getInstance().getScoreboardHandler().getConfiguration().getScoreGetter().getScores(this.localList.get(), this.player);
        this.recentlyUpdatedScores.clear();
        this.usedBaseScores.clear();
        int nextValue = lines.size();

        if (lines.size() > 16) {
            Library.getInstance().getLogger().warning("Too many lines passed!");
            return;
        }

        if (title.length() > 32) {
            Library.getInstance().getLogger().warning("Title is too long!");
            return;
        }

        if (!this.objective.getDisplayName().equals(title)) {
            this.objective.setDisplayName(title);
        }

        for (String line : lines) {
            if (48 <= line.length()) throw new IllegalArgumentException("Line is too long! Offending line: " + line);
            String[] separated = separate(line, this.usedBaseScores);
            String prefix = separated[0];
            String score = separated[1];
            String suffix = separated[2];

            this.recentlyUpdatedScores.add(score);

            if (!this.sentTeamCreates.contains(score)) {
                createAndAddMember(score);
            }

            if (!this.displayedScores.containsKey(score) || this.displayedScores.get(score) != nextValue) {
                setScore(score, nextValue);
            }

            if (!this.scorePrefixes.containsKey(score) || !((String)this.scorePrefixes.get(score)).equals(prefix) || !((String)this.scoreSuffixes.get(score)).equals(suffix)) {
                updateScore(score, prefix, suffix);
            }

            nextValue--;
        }

        for (UnmodifiableIterator<String> unmodifiableIterator = ImmutableSet.copyOf(this.displayedScores.keySet()).iterator(); unmodifiableIterator.hasNext(); ) { String displayedScore = unmodifiableIterator.next();
            if (this.recentlyUpdatedScores.contains(displayedScore)) {
                continue;
            }

            removeScore(displayedScore); }

    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(String scoreTitle) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", ImmutableList.of(), 0);
        ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, ImmutableList.of(scoreTitle), 3);

        scoreboardTeamAdd.sendToPlayer(this.player);
        scoreboardTeamAddMember.sendToPlayer(this.player);
        this.sentTeamCreates.add(scoreTitle);
    }

    private void setScore(String score, int value) {
        PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

        setField(scoreboardScorePacket, "a", score);
        setField(scoreboardScorePacket, "b", this.objective.getName());
        setField(scoreboardScorePacket, "c", value);
        setField(scoreboardScorePacket, "d", 0);

        this.displayedScores.put(score, value);
        (((CraftPlayer)this.player).getHandle()).playerConnection.sendPacket(scoreboardScorePacket);
    }

    private void removeScore(String score) {
        this.displayedScores.remove(score);
        this.scorePrefixes.remove(score);
        this.scoreSuffixes.remove(score);
        (((CraftPlayer)this.player).getHandle()).playerConnection.sendPacket(new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        this.scorePrefixes.put(score, prefix);
        this.scoreSuffixes.put(score, suffix);
        (new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2)).sendToPlayer(this.player);
    }

    private String[] separate(String line, Collection<String> usedBaseScores) {
        line = Color.translate(line);
        String prefix = "";
        String score = "";
        String suffix = "";

        this.separateScores.clear();
        this.separateScoreBuilder.setLength(0);

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '*' || (this.separateScoreBuilder.length() == 16 && this.separateScores.size() < 3)) {
                this.separateScores.add(this.separateScoreBuilder.toString());
                this.separateScoreBuilder.setLength(0);

                if (c == '*') {
                    continue;
                }
            }

            this.separateScoreBuilder.append(c);
            continue;
        }
        this.separateScores.add(this.separateScoreBuilder.toString());

        switch (this.separateScores.size()) {
            case 1:
                score = this.separateScores.get(0);
                break;
            case 2:
                score = this.separateScores.get(0);
                suffix = this.separateScores.get(1);
                break;
            case 3:
                prefix = this.separateScores.get(0);
                score = this.separateScores.get(1);
                suffix = this.separateScores.get(2);
                break;
            default:
                Library.getInstance().getLogger().warning("Failed to separate scoreboard line. Input: " + line);
                break;
        }

        if (usedBaseScores.contains(score)) {
            if (score.length() <= 14) {
                for (ChatColor chatColor : ChatColor.values()) {
                    String possibleScore = chatColor + score;

                    if (!usedBaseScores.contains(possibleScore)) {
                        score = possibleScore;
                        break;
                    }
                }
                if (usedBaseScores.contains(score)) {
                    Library.getInstance().getLogger().warning("Failed to find alternate color code for: " + score);
                }
            } else {
                Library.getInstance().getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }

        if (prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if (score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if (suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        usedBaseScores.add(score);
        this.prefixScoreSuffix[0] = prefix;
        this.prefixScoreSuffix[1] = score;
        this.prefixScoreSuffix[2] = suffix;
        return this.prefixScoreSuffix;
    }

}