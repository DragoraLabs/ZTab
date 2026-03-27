package dev.example.ztabfusion.command;

import dev.example.ztabfusion.ZTabFusionPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class ZTabFusionCommand implements CommandExecutor, TabCompleter {
    private final ZTabFusionPlugin plugin;

    public ZTabFusionCommand(ZTabFusionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            showStatus(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ztab.admin")) {
                sender.sendMessage("You need ztab.admin permission.");
                return true;
            }
            plugin.reloadPluginSettings();
            sender.sendMessage("ztab reloaded.");
            return true;
        }

        sender.sendMessage("Usage: /ztab [status|reload]");
        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args) {
        if (args.length == 1) {
            List<String> values = new ArrayList<>();
            values.add("status");
            if (sender.hasPermission("ztab.admin")) {
                values.add("reload");
            }
            return values;
        }
        return List.of();
    }

    private void showStatus(CommandSender sender) {
        sender.sendMessage(
                "ztab v"
                        + plugin.getPluginMetaOrDescriptionVersion()
                        + " | update interval: "
                        + plugin.settings().updateIntervalTicks()
                        + " ticks");
        sender.sendMessage(
                "zSupportExtender link: "
                        + (plugin.zSupportHook().linked() ? "yes" : "no")
                        + " | status: "
                        + plugin.zSupportHook().statusToken());
    }
}
