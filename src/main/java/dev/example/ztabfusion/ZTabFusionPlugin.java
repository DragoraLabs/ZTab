package dev.example.ztabfusion;

import dev.example.ztabfusion.command.ZTabFusionCommand;
import dev.example.ztabfusion.config.TabFusionSettings;
import dev.example.ztabfusion.core.PlaceholderEngine;
import dev.example.ztabfusion.core.TabPlayerListener;
import dev.example.ztabfusion.core.TabViewService;
import dev.example.ztabfusion.integration.ZSupportExtenderHook;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZTabFusionPlugin extends JavaPlugin {
    private TabFusionSettings settings;
    private ZSupportExtenderHook zSupportHook;
    private TabViewService tabViewService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginSettings();

        PluginCommand command = getCommand("ztab");
        if (command != null) {
            ZTabFusionCommand executor = new ZTabFusionCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().warning("Command 'ztab' is missing from plugin.yml.");
        }

        getServer().getPluginManager().registerEvents(new TabPlayerListener(this), this);
        getLogger().info(
                "ztab enabled. zSupportExtender link: "
                        + (zSupportHook.linked() ? "yes" : "no")
                        + " ("
                        + zSupportHook.statusToken()
                        + ")");
    }

    @Override
    public void onDisable() {
        if (tabViewService != null) {
            tabViewService.stop();
        }
    }

    public void reloadPluginSettings() {
        reloadConfig();
        settings = TabFusionSettings.fromConfig(getConfig());
        zSupportHook = new ZSupportExtenderHook(this, settings.zSupportExtenderIntegrationEnabled());
        zSupportHook.detect();

        PlaceholderEngine placeholderEngine = new PlaceholderEngine(this, zSupportHook::statusToken);
        if (tabViewService != null) {
            tabViewService.stop();
        }
        tabViewService = new TabViewService(this, settings, placeholderEngine);
        tabViewService.start();
    }

    public TabFusionSettings settings() {
        return settings;
    }

    public ZSupportExtenderHook zSupportHook() {
        return zSupportHook;
    }

    public TabViewService tabViewService() {
        return tabViewService;
    }

    public String getPluginMetaOrDescriptionVersion() {
        try {
            Method getPluginMeta = getClass().getMethod("getPluginMeta");
            Object meta = getPluginMeta.invoke(this);
            if (meta != null) {
                Method getVersion = meta.getClass().getMethod("getVersion");
                Object version = getVersion.invoke(meta);
                if (version instanceof String value && !value.isBlank()) {
                    return value;
                }
            }
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | SecurityException ignored) {
            // fallback below
        }
        return getDescription().getVersion();
    }
}
