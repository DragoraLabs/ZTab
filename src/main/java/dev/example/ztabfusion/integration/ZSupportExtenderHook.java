package dev.example.ztabfusion.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZSupportExtenderHook {
    private final JavaPlugin owner;
    private final boolean integrationEnabled;
    private boolean linked;
    private String statusToken = "not-linked";
    private String details = "zSupportExtender not detected";

    public ZSupportExtenderHook(JavaPlugin owner, boolean integrationEnabled) {
        this.owner = owner;
        this.integrationEnabled = integrationEnabled;
    }

    public void detect() {
        if (!integrationEnabled) {
            linked = false;
            statusToken = "disabled";
            details = "integration disabled by config";
            return;
        }

        PluginManager pluginManager = owner.getServer().getPluginManager();
        Plugin target = pluginManager.getPlugin("zSupportExtender");
        if (target == null || !target.isEnabled()) {
            linked = false;
            statusToken = "not-found";
            details = "zSupportExtender not installed or not enabled";
            return;
        }

        linked = true;
        statusToken = readStatusToken(target);
        details = "linked to " + target.getName() + " " + target.getDescription().getVersion();
    }

    public boolean linked() {
        return linked;
    }

    public String statusToken() {
        return statusToken;
    }

    public String details() {
        return details;
    }

    private static String readStatusToken(Plugin target) {
        try {
            Method method = target.getClass().getMethod("serverPaperSupportStatus");
            Object value = method.invoke(target);
            if (value instanceof String token && !token.isBlank()) {
                return token.trim();
            }
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | SecurityException ignored) {
            // fallback below
        }
        return "linked";
    }
}
