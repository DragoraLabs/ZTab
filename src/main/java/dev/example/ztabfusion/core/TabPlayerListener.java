package dev.example.ztabfusion.core;

import dev.example.ztabfusion.ZTabFusionPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TabPlayerListener implements Listener {
    private final ZTabFusionPlugin plugin;

    public TabPlayerListener(ZTabFusionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer()
                .getScheduler()
                .runTaskLater(plugin, () -> plugin.tabViewService().applyNow(event.getPlayer()), 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.tabViewService().onPlayerQuit(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }
}
