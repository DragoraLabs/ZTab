package dev.example.ztabfusion.core;

import dev.example.ztabfusion.config.GroupRule;
import java.util.Locale;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class PlaceholderEngine {
    private final Plugin plugin;
    private final Supplier<String> zSupportStatusSupplier;

    public PlaceholderEngine(Plugin plugin, Supplier<String> zSupportStatusSupplier) {
        this.plugin = plugin;
        this.zSupportStatusSupplier = zSupportStatusSupplier;
    }

    public String resolve(String template, Player player, GroupRule group) {
        String output = template == null ? "" : template;
        output = replace(output, "%player%", player.getName());
        output = replace(output, "%world%", player.getWorld().getName());
        output = replace(output, "%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        output = replace(output, "%max%", String.valueOf(Bukkit.getMaxPlayers()));
        output = replace(output, "%ping%", String.valueOf(readPing(player)));
        output = replace(output, "%tps%", readTps());
        output = replace(output, "%x%", String.valueOf(player.getLocation().getBlockX()));
        output = replace(output, "%y%", String.valueOf(player.getLocation().getBlockY()));
        output = replace(output, "%z%", String.valueOf(player.getLocation().getBlockZ()));
        output = replace(output, "%prefix%", group.prefix());
        output = replace(output, "%suffix%", group.suffix());
        output = replace(output, "%zse_status%", safeValue(zSupportStatusSupplier.get()));
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    private String readTps() {
        try {
            Object value = plugin.getServer().getClass().getMethod("getTPS").invoke(plugin.getServer());
            if (value instanceof double[] tps && tps.length > 0) {
                double current = Math.max(0.0D, Math.min(20.0D, tps[0]));
                return String.format(Locale.ROOT, "%.1f", current);
            }
        } catch (ReflectiveOperationException ignored) {
            // fallback below
        }
        return "20.0";
    }

    private static int readPing(Player player) {
        try {
            Object value = player.getClass().getMethod("getPing").invoke(player);
            if (value instanceof Number number) {
                return Math.max(0, number.intValue());
            }
        } catch (ReflectiveOperationException ignored) {
            // fallback below
        }
        return -1;
    }

    private static String replace(String input, String token, String value) {
        return input.replace(token, safeValue(value));
    }

    private static String safeValue(String value) {
        return value == null ? "" : value;
    }
}
