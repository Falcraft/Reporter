/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package net.KabOOm356.Listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.KabOOm356.Command.Commands.ListCommand;
import net.KabOOm356.Command.Commands.ViewCommand;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.ResultRow;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.AlertPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Runnable.DelayedMessage;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.PlayerMessageService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ReporterPlayerListener
implements Listener {
    private static final Logger log = LogManager.getLogger(ReporterPlayerListener.class);
    private final Reporter plugin;

    public ReporterPlayerListener(Reporter instance) {
        this.plugin = instance;
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean alertReportedPlayerLogin;
        Player player = event.getPlayer();
        PlayerMessageService playerMessageService = this.plugin.getCommandManager().getServiceModule().getPlayerMessageService();
        if (playerMessageService.hasMessages(player.getUniqueId().toString()) || playerMessageService.hasMessages(player.getName())) {
            this.sendMessages(player);
        }
        if (this.plugin.getConfig().getBoolean("general.messaging.listOnLogin.listOnLogin", true)) {
            this.listOnLogin(player);
        }
        if (alertReportedPlayerLogin = this.plugin.getConfig().getBoolean("general.messaging.alerts.reportedPlayerLogin.enabled", true)) {
            boolean alertConsoleReportedPlayerLogin = this.plugin.getConfig().getBoolean("general.messaging.alerts.reportedPlayerLogin.toConsole", true);
            boolean alertPlayersReportedPlayerLogin = this.plugin.getConfig().getBoolean("general.messaging.alerts.reportedPlayerLogin.toPlayer", true);
            if ((alertConsoleReportedPlayerLogin || alertPlayersReportedPlayerLogin) && this.isPlayerReported(player)) {
                this.alertThatReportedPlayerLogin(player);
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getCommandManager().getServiceModule().getLastViewedReportService().removeLastViewedReport((CommandSender)event.getPlayer());
    }

    private void listOnLogin(Player player) {
        ReporterCommand listCommand = this.plugin.getCommandManager().getCommand(ListCommand.getCommandName());
        if (listCommand.hasPermission(player)) {
            listCommand.setSender((CommandSender)player);
            listCommand.setArguments(new ArrayList<String>());
            if (this.plugin.getConfig().getBoolean("general.messaging.listOnLogin.useDelay", true)) {
                int delay = this.plugin.getConfig().getInt("general.messaging.listOnLogin.delay", 5);
                Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, (Runnable)listCommand, BukkitUtil.convertSecondsToServerTicks(delay).longValue());
            } else {
                Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)listCommand);
            }
        }
    }

    private void sendMessages(Player player) {
        boolean canView = this.plugin.getCommandManager().getServiceModule().getPermissionService().hasPermission(player, ViewCommand.getCommandPermissionNode());
        canView = canView || this.plugin.getConfig().getBoolean("general.canViewSubmittedReports", true);
        PlayerMessageService playerMessageService = this.plugin.getCommandManager().getServiceModule().getPlayerMessageService();
        if (canView) {
            ArrayList<String> messages = playerMessageService.getMessages(player.getUniqueId().toString());
            ArrayList<String> playerNameMessages = playerMessageService.getMessages(player.getName());
            messages.addAll(playerNameMessages);
            if (this.plugin.getConfig().getBoolean("general.messaging.completedMessageOnLogin.useDelay", true)) {
                int messageGroup = 1;
                int message = 0;
                long delayTime = 0L;
                int delayTimeInSeconds = this.plugin.getConfig().getInt("general.messaging.completedMessageOnLogin.delay", 5);
                while (!messages.isEmpty()) {
                    delayTime = BukkitUtil.convertSecondsToServerTicks(delayTimeInSeconds) * (long)messageGroup;
                    String output = messages.remove(0);
                    Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, (Runnable)new DelayedMessage(player, output), delayTime);
                    if (++message % 5 != 0) continue;
                    ++messageGroup;
                }
            } else {
                for (String message : messages) {
                    player.sendMessage(message);
                }
            }
        }
        playerMessageService.removePlayerMessages(player.getUniqueId().toString());
        playerMessageService.removePlayerMessages(player.getName());
    }

    private boolean isPlayerReported(Player player) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID ").append("FROM Reports ").append("WHERE ReportedUUID = '").append(player.getUniqueId()).append("' AND CompletionStatus = 0");
        try {
            SQLResultSet result = this.plugin.getDatabaseHandler().sqlQuery(query.toString());
            return !result.isEmpty();
        }
        catch (Exception e) {
            log.error("Failed to execute sql query!", (Throwable)e);
            return false;
        }
    }

    private void alertThatReportedPlayerLogin(Player reportedPlayer) {
        SQLResultSet result;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID, ClaimStatus, ClaimedByUUID ").append("FROM Reports ").append("WHERE ReportedUUID = '").append(reportedPlayer.getUniqueId()).append("' AND CompletionStatus = 0");
        try {
            result = this.plugin.getDatabaseHandler().sqlQuery(query.toString());
        }
        catch (Exception e) {
            log.error("Failed to execute sql query!", (Throwable)e);
            return;
        }
        boolean displayAlertToPlayers = this.plugin.getConfig().getBoolean("general.messaging.alerts.reportedPlayerLogin.toPlayer", true);
        ArrayList<Integer> consoleIndexes = new ArrayList<Integer>();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (ResultRow row : result) {
            consoleIndexes.add(row.getInt("ID"));
            if (row.getBoolean("ClaimStatus").booleanValue() && displayAlertToPlayers) {
                String uuidString = row.getString("ClaimedByUUID");
                UUID uuid = UUID.fromString(uuidString);
                OfflinePlayer player = Bukkit.getOfflinePlayer((UUID)uuid);
                this.alertClaimingPlayerReportedPlayerLogin(player, (OfflinePlayer)reportedPlayer, row.getString("ID"));
                continue;
            }
            indexes.add(row.getInt("ID"));
        }
        String reportedPlayerName = (Object)ChatColor.RED + BukkitUtil.formatPlayerName(reportedPlayer) + (Object)ChatColor.WHITE;
        if (this.plugin.getConfig().getBoolean("general.messaging.alerts.reportedPlayerLogin.toConsole", true)) {
            String message = this.plugin.getLocale().getString(AlertPhrases.alertConsoleReportedPlayerLogin).replaceAll("%r", reportedPlayerName).replaceAll("%i", ArrayUtil.indexesToString(indexes, ChatColor.GOLD, ChatColor.WHITE));
            log.info(message);
        }
        this.alertOnlinePlayersReportedPlayerLogin(reportedPlayerName, indexes);
    }

    private void alertClaimingPlayerReportedPlayerLogin(OfflinePlayer player, OfflinePlayer reportedPlayer, String id) {
        if (player.isOnline()) {
            String message = (Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.plugin.getLocale().getString(AlertPhrases.alertClaimedPlayerLogin);
            message = message.replaceAll("%r", (Object)ChatColor.RED + BukkitUtil.formatPlayerName(reportedPlayer) + (Object)ChatColor.WHITE).replaceAll("%i", (Object)ChatColor.GOLD + id + (Object)ChatColor.WHITE);
            player.getPlayer().sendMessage(message);
        }
    }

    private void alertOnlinePlayersReportedPlayerLogin(String reportedPlayerName, ArrayList<Integer> indexes) {
        String message = (Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.plugin.getLocale().getString(AlertPhrases.alertUnclaimedPlayerLogin);
        message = message.replaceAll("%r", reportedPlayerName).replaceAll("%i", ArrayUtil.indexesToString(indexes, ChatColor.GOLD, ChatColor.WHITE));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.plugin.getCommandManager().getServiceModule().getPermissionService().hasPermission(player, "reporter.alerts.onlogin.reportedPlayerLogin")) continue;
            player.sendMessage(message);
        }
    }
}

