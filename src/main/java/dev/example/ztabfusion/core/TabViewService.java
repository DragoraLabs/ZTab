package dev.example.ztabfusion.core;

import dev.example.ztabfusion.config.GroupRule;
import dev.example.ztabfusion.config.TabFusionSettings;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public final class TabViewService {
    private final Plugin plugin;
    private final TabFusionSettings settings;
    private final PlaceholderEngine placeholderEngine;
    private final GroupResolver groupResolver;
    private final Map<UUID, String> assignedTeamByPlayer = new HashMap<>();
    private int frameCursor;
    private BukkitTask updateTask;

    public TabViewService(
            Plugin plugin, TabFusionSettings settings, PlaceholderEngine placeholderEngine) {
        this.plugin = plugin;
        this.settings = settings;
        this.placeholderEngine = placeholderEngine;
        this.groupResolver = new GroupResolver(settings);
    }

    public void start() {
        stop();
        updateTask =
                plugin.getServer()
                        .getScheduler()
                        .runTaskTimer(plugin, this::tick, 20L, settings.updateIntervalTicks());
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            onPlayerQuit(player.getUniqueId(), player.getName());
        }
    }

    public void applyNow(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        GroupRule group = groupResolver.resolve(player);
        applyPlayerListName(player, group);
        applyHeaderFooter(player, group);
        applySorting(player, group);
    }

    public void onPlayerQuit(UUID playerId, String playerName) {
        String previousTeam = assignedTeamByPlayer.remove(playerId);
        if (previousTeam == null || playerName == null || playerName.isBlank()) {
            return;
        }

        Team team = mainScoreboardTeam(previousTeam);
        if (team != null) {
            team.removeEntry(playerName);
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyNow(player);
        }
        frameCursor++;
    }

    private void applyHeaderFooter(Player player, GroupRule group) {
        if (!settings.headerFooterEnabled()) {
            return;
        }
        String header = selectFrame(settings.headerFrames());
        String footer = selectFrame(settings.footerFrames());
        player.setPlayerListHeaderFooter(
                placeholderEngine.resolve(header, player, group),
                placeholderEngine.resolve(footer, player, group));
    }

    private void applyPlayerListName(Player player, GroupRule group) {
        if (!settings.playerListNameEnabled()) {
            return;
        }
        String formatted = placeholderEngine.resolve(settings.playerListNameFormat(), player, group);
        player.setPlayerListName(trim(formatted, 128));
    }

    private void applySorting(Player player, GroupRule group) {
        if (!settings.sortingEnabled()) {
            return;
        }

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        String teamName = teamName(group);
        String previousTeam = assignedTeamByPlayer.put(player.getUniqueId(), teamName);
        if (previousTeam != null && !previousTeam.equals(teamName)) {
            Team oldTeam = scoreboard.getTeam(previousTeam);
            if (oldTeam != null) {
                oldTeam.removeEntry(player.getName());
            }
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        setTeamPrefix(team, group.prefix());
        setTeamSuffix(team, group.suffix());
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    private static void setTeamPrefix(Team team, String prefix) {
        try {
            team.setPrefix(trim(ChatColor.translateAlternateColorCodes('&', safe(prefix)), 32));
        } catch (Throwable ignored) {
            // compatible no-op
        }
    }

    private static void setTeamSuffix(Team team, String suffix) {
        try {
            team.setSuffix(trim(ChatColor.translateAlternateColorCodes('&', safe(suffix)), 32));
        } catch (Throwable ignored) {
            // compatible no-op
        }
    }

    private Team mainScoreboardTeam(String teamName) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return null;
        }
        return scoreboardManager.getMainScoreboard().getTeam(teamName);
    }

    private String selectFrame(java.util.List<String> frames) {
        int index = Math.floorMod(frameCursor, frames.size());
        return frames.get(index);
    }

    private static String teamName(GroupRule group) {
        String hash = Integer.toHexString(Math.abs(group.key().toLowerCase(Locale.ROOT).hashCode()));
        String hashPart = hash.length() > 4 ? hash.substring(0, 4) : hash;
        return "ztf" + String.format(Locale.ROOT, "%04d", Math.max(0, group.priority())) + hashPart;
    }

    private static String trim(String value, int maxLength) {
        String safe = safe(value);
        if (safe.length() <= maxLength) {
            return safe;
        }
        return safe.substring(0, maxLength);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
