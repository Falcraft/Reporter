/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Service;

import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PlayerService
extends Service {
    private static final String highModLevelPermission = "reporter.modlevel.high";
    private static final String normalModLevelPermission = "reporter.modlevel.normal";
    private static final String lowModLevelPermission = "reporter.modlevel.low";

    protected PlayerService(ServiceModule module) {
        super(module);
    }

    public ModLevel getModLevel(CommandSender sender) {
        if (sender.isOp()) {
            return ModLevel.HIGH;
        }
        if (sender instanceof ConsoleCommandSender) {
            return ModLevel.HIGH;
        }
        if (BukkitUtil.isPlayer(sender)) {
            Player player = Player.class.cast((Object)sender);
            if (this.hasPermission(player, highModLevelPermission)) {
                return ModLevel.HIGH;
            }
            if (this.hasPermission(player, normalModLevelPermission)) {
                return ModLevel.NORMAL;
            }
            if (this.hasPermission(player, lowModLevelPermission)) {
                return ModLevel.LOW;
            }
        }
        return ModLevel.NONE;
    }

    public boolean requireModLevelInBounds(CommandSender sender, String modLevel) {
        if (ModLevel.modLevelInBounds(modLevel)) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + this.getLocale().getString(GeneralPhrases.priorityLevelNotInBounds));
        return false;
    }

    public void displayModLevel(CommandSender sender) {
        ModLevel level = this.getModLevel(sender);
        String output = this.getLocale().getString(GeneralPhrases.displayModLevel).replaceAll("%m", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.WHITE + output);
    }

    public void displayModLevel(CommandSender sender, CommandSender player) {
        String playerName = BukkitUtil.formatPlayerName(player);
        ModLevel level = this.getModLevel(player);
        String output = this.getLocale().getString(GeneralPhrases.displayOtherModLevel).replaceAll("%p", (Object)ChatColor.BLUE + playerName + (Object)ChatColor.WHITE).replaceAll("%m", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.WHITE + output);
    }

    private boolean hasPermission(Player sender, String permission) {
        return this.getModule().getPermissionService().hasPermission(sender, permission);
    }

    private Locale getLocale() {
        return this.getStore().getLocaleStore().get();
    }
}

