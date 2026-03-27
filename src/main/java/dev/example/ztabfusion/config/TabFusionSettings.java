package dev.example.ztabfusion.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public record TabFusionSettings(
        int updateIntervalTicks,
        boolean headerFooterEnabled,
        List<String> headerFrames,
        List<String> footerFrames,
        boolean playerListNameEnabled,
        String playerListNameFormat,
        boolean sortingEnabled,
        int defaultPriority,
        String defaultPrefix,
        String defaultSuffix,
        List<GroupRule> groups,
        boolean zSupportExtenderIntegrationEnabled) {

    public static TabFusionSettings fromConfig(FileConfiguration config) {
        int updateIntervalTicks = Math.max(10, config.getInt("update.intervalTicks", 20));

        boolean headerFooterEnabled = config.getBoolean("tab.headerFooter.enabled", true);
        List<String> headerFrames =
                normalizeFrames(
                        config.getStringList("tab.headerFooter.headerFrames"), "&b&lzTabFusion");
        List<String> footerFrames =
                normalizeFrames(
                        config.getStringList("tab.headerFooter.footerFrames"),
                        "&7TPS: &a%tps% &7| Ping: &a%ping%ms");

        boolean playerListNameEnabled = config.getBoolean("tab.playerListName.enabled", true);
        String playerListNameFormat =
                normalizeString(
                        config.getString("tab.playerListName.format"), "%prefix%&f%player%%suffix%");

        boolean sortingEnabled = config.getBoolean("tab.sorting.enabled", true);
        int defaultPriority = config.getInt("tab.sorting.defaultPriority", 900);
        String defaultPrefix = normalizeString(config.getString("tab.sorting.defaultPrefix"), "&7");
        String defaultSuffix = normalizeString(config.getString("tab.sorting.defaultSuffix"), "");
        List<GroupRule> groups = loadGroups(config.getMapList("tab.sorting.groups"));

        boolean zSupportExtenderIntegrationEnabled =
                config.getBoolean("integration.zSupportExtender.enabled", true);

        return new TabFusionSettings(
                updateIntervalTicks,
                headerFooterEnabled,
                List.copyOf(headerFrames),
                List.copyOf(footerFrames),
                playerListNameEnabled,
                playerListNameFormat,
                sortingEnabled,
                defaultPriority,
                defaultPrefix,
                defaultSuffix,
                List.copyOf(groups),
                zSupportExtenderIntegrationEnabled);
    }

    public GroupRule defaultGroup() {
        return new GroupRule("default", "", defaultPriority, defaultPrefix, defaultSuffix);
    }

    private static List<GroupRule> loadGroups(List<Map<?, ?>> rawGroups) {
        List<GroupRule> groups = new ArrayList<>();
        for (int i = 0; i < rawGroups.size(); i++) {
            Map<?, ?> rawGroup = rawGroups.get(i);
            if (rawGroup == null || rawGroup.isEmpty()) {
                continue;
            }
            groups.add(GroupRule.fromMap(rawGroup, i));
        }
        return groups;
    }

    private static List<String> normalizeFrames(List<String> frames, String fallback) {
        List<String> normalized = new ArrayList<>();
        for (String frame : frames) {
            String value = normalizeString(frame, "");
            if (!value.isEmpty()) {
                normalized.add(value);
            }
        }
        if (normalized.isEmpty()) {
            normalized.add(fallback);
        }
        return normalized;
    }

    private static String normalizeString(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }
}
