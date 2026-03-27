package dev.example.ztabfusion.core;

import dev.example.ztabfusion.config.GroupRule;
import dev.example.ztabfusion.config.TabFusionSettings;
import org.bukkit.entity.Player;

public final class GroupResolver {
    private final TabFusionSettings settings;

    public GroupResolver(TabFusionSettings settings) {
        this.settings = settings;
    }

    public GroupRule resolve(Player player) {
        GroupRule best = settings.defaultGroup();
        for (GroupRule rule : settings.groups()) {
            if (!rule.permission().isBlank() && !player.hasPermission(rule.permission())) {
                continue;
            }
            if (rule.priority() < best.priority()) {
                best = rule;
            }
        }
        return best;
    }
}
