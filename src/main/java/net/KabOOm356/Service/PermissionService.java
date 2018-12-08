/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Service;

import net.KabOOm356.Permission.PermissionHandler;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Util.BukkitUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class PermissionService
extends Service {
    protected PermissionService(ServiceModule module) {
        super(module);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        if (BukkitUtil.isPlayer(sender)) {
            Player player = Player.class.cast((Object)sender);
            return this.hasPermission(player, permission);
        }
        return true;
    }

    public boolean hasPermission(Player player, String permission) {
        return this.hasPermissionOverride(player) || this.checkPermission(player, permission);
    }

    private boolean checkPermission(Player player, String permission) {
        return this.getPermissions().hasPermission(player, permission);
    }

    private boolean hasPermissionOverride(Player player) {
        return this.getConfiguration().getBoolean("general.permissions.opsHaveAllPermissions", true) && player.isOp();
    }

    private PermissionHandler getPermissions() {
        return this.getStore().getPermissionStore().get();
    }

    private Configuration getConfiguration() {
        return this.getStore().getConfigurationStore().get();
    }
}

