/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  ru.tehkode.permissions.PermissionManager
 *  ru.tehkode.permissions.bukkit.PermissionsEx
 */
package net.KabOOm356.Permission;

import net.KabOOm356.Permission.PermissionType;
import net.KabOOm356.Reporter.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionHandler {
    private static final Logger log = LogManager.getLogger(PermissionHandler.class);
    private static final String permissionExName = "PermissionsEx";
    private PermissionType type = null;
    private PermissionManager permissionsExHandler;

    public PermissionHandler() {
        this.setupPermissionsEx();
        if (this.type == null) {
            this.type = PermissionType.SuperPerms;
        }
        log.info(Reporter.getDefaultConsolePrefix() + (Object)((Object)this.type) + " support enabled.");
    }

    private void setupPermissionsEx() {
        if (Bukkit.getPluginManager().isPluginEnabled(permissionExName)) {
            this.permissionsExHandler = PermissionsEx.getPermissionManager();
            if (this.permissionsExHandler == null) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to obtain PermissionsEx handler.");
                log.warn(Reporter.getDefaultConsolePrefix() + "PermissionsEx support could not be enabled.");
            } else {
                this.type = PermissionType.PermissionsEx;
            }
        }
    }

    private boolean usingPermissionsEx() {
        return PermissionType.PermissionsEx == this.type;
    }

    public boolean hasPermission(Player player, String permission) {
        if (this.usingPermissionsEx()) {
            return this.permissionsExHandler.has(player, permission);
        }
        return player.hasPermission(permission);
    }
}

