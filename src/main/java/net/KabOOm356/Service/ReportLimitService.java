/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package net.KabOOm356.Service;

import java.util.Calendar;
import java.util.Collection;
import java.util.PriorityQueue;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.ReportPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Configuration.Entry.ConfigurationEntries;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Runnable.Timer.ReportTimer;
import net.KabOOm356.Service.ConfigurationService;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Service.Store.type.PlayerReport;
import net.KabOOm356.Service.Store.type.PlayerReportQueue;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ReportLimitService
extends Service {
    private static final Logger log = LogManager.getLogger(ReportLimitService.class);
    private final Plugin plugin = BukkitUtil.getPlugin(Reporter.class.getSimpleName());

    protected ReportLimitService(ServiceModule module) {
        super(module);
    }

    public boolean canReport(CommandSender sender) {
        if (this.getConfigurationService().get(ConfigurationEntries.limitReports).booleanValue()) {
            int numberOfReports = 0;
            PlayerReportQueue playerReportQueue = this.getPlayerReports().get(sender);
            if (playerReportQueue != null) {
                for (PriorityQueue queue : playerReportQueue.values()) {
                    numberOfReports += queue.size();
                }
                int limit = this.getConfigurationService().get(ConfigurationEntries.reportLimit);
                return this.canReport(sender, limit, numberOfReports);
            }
        }
        return true;
    }

    public boolean canReport(CommandSender sender, OfflinePlayer reported) {
        PlayerReportQueue playerReportQueue;
        PriorityQueue<ReportTimer> queue;
        if (this.getConfigurationService().get(ConfigurationEntries.limitReportsAgainstPlayers).booleanValue() && (playerReportQueue = this.getPlayerReports().get(sender)) != null && (queue = playerReportQueue.get(reported)) != null) {
            int numberOfReports = playerReportQueue.get(reported).size();
            int limit = this.getConfigurationService().get(ConfigurationEntries.reportLimitAgainstPlayers);
            return this.canReport(sender, limit, numberOfReports);
        }
        return true;
    }

    public void hasReported(CommandSender sender, OfflinePlayer reportedPlayer) {
        boolean noLimit;
        Player player;
        boolean isPlayer = BukkitUtil.isPlayer(sender);
        boolean canReport = this.canReport(sender);
        boolean canReportPlayer = this.canReport(sender, reportedPlayer);
        if (isPlayer && canReport && canReportPlayer && !(noLimit = this.hasPermission(player = Player.class.cast((Object)sender), "reporter.report.nolimit"))) {
            String output;
            ReportTimer timer = new ReportTimer();
            Calendar executionTime = Calendar.getInstance();
            executionTime.add(13, this.getConfigurationService().get(ConfigurationEntries.limitTime));
            timer.init(this, player, reportedPlayer, executionTime.getTimeInMillis());
            long bukkitTicks = BukkitUtil.convertSecondsToServerTicks(this.getConfigurationService().get(ConfigurationEntries.limitTime));
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, (Runnable)timer, bukkitTicks);
            this.getPlayerReports().put(sender, reportedPlayer, timer);
            if (this.getConfigurationService().get(ConfigurationEntries.alertConsoleWhenLimitReached).booleanValue() && !this.canReport(sender)) {
                output = "%p has reached their reporting limit!".replaceAll("%p", player.getName());
                log.log(Level.INFO, Reporter.getLogPrefix() + output);
            }
            if (this.getConfigurationService().get(ConfigurationEntries.alertConsoleWhenLimitAgainstPlayerReached).booleanValue() && !this.canReport(sender, reportedPlayer)) {
                output = "%p has reached their reporting limit for reporting %r!".replaceAll("%p", player.getName()).replaceAll("%r", BukkitUtil.formatPlayerName(reportedPlayer));
                log.log(Level.INFO, Reporter.getLogPrefix() + output);
            }
        }
    }

    public int getRemainingTime(CommandSender sender, OfflinePlayer reported) {
        PriorityQueue<ReportTimer> timers;
        PlayerReportQueue playerReports = this.getPlayerReports().get(sender);
        if (playerReports != null && (timers = playerReports.get(reported)) != null && timers.peek() != null) {
            return timers.peek().getTimeRemaining();
        }
        return 0;
    }

    public int getRemainingTime(CommandSender sender) {
        PlayerReportQueue entry = this.getPlayerReports().get(sender);
        Integer time = null;
        if (entry != null) {
            for (PriorityQueue timers : entry.values()) {
                ReportTimer timer = (ReportTimer)timers.peek();
                if (timer == null || time != null && timer.getTimeRemaining() >= time) continue;
                time = timer.getTimeRemaining();
            }
        }
        return time != null ? time : 0;
    }

    public void limitExpired(ReportTimer expired) {
        Player player = expired.getPlayer();
        OfflinePlayer reported = expired.getReported();
        if (!this.canReport((CommandSender)player)) {
            if (this.getConfigurationService().get(ConfigurationEntries.alertPlayerWhenAllowedToReportAgain).booleanValue()) {
                player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getLocale().getString(ReportPhrases.allowedToReportAgain));
            }
            if (this.getConfigurationService().get(ConfigurationEntries.alertConsoleWhenAllowedToReportAgain).booleanValue()) {
                log.log(Level.INFO, Reporter.getLogPrefix() + player.getName() + " is now allowed to report again!");
            }
        }
        if (this.getConfigurationService().get(ConfigurationEntries.limitReportsAgainstPlayers).booleanValue() && !this.canReport((CommandSender)player, reported)) {
            if (this.getConfigurationService().get(ConfigurationEntries.alertPlayerWhenAllowedToReportPlayerAgain).booleanValue()) {
                String output = this.getLocale().getString(ReportPhrases.allowedToReportPlayerAgain).replaceAll("%r", (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(reported) + (Object)ChatColor.WHITE);
                player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
            }
            if (this.getConfigurationService().get(ConfigurationEntries.alertConsoleWhenAllowedToReportPlayerAgain).booleanValue()) {
                log.log(Level.INFO, Reporter.getLogPrefix() + String.format("%s is now allowed to report %s again!", BukkitUtil.formatPlayerName(player), BukkitUtil.formatPlayerName(reported)));
            }
        }
        this.getPlayerReports().remove((OfflinePlayer)player, reported, expired);
    }

    private boolean hasReported(CommandSender sender) {
        return !this.getPlayerReports().get(sender).isEmpty();
    }

    private boolean isPlayerAndHasReported(CommandSender sender) {
        return BukkitUtil.isPlayer(sender) && this.hasReported(sender);
    }

    private boolean canReport(CommandSender sender, int limit, int numberOfReports) {
        Player player;
        if (this.isPlayerAndHasReported(sender) && !this.hasLimitOverride(player = Player.class.cast((Object)sender))) {
            return numberOfReports < limit;
        }
        return true;
    }

    private boolean hasLimitOverride(Player player) {
        return this.hasPermission(player, "reporter.report.nolimit");
    }

    private ConfigurationService getConfigurationService() {
        return this.getModule().getConfigurationService();
    }

    private Locale getLocale() {
        return this.getStore().getLocaleStore().get();
    }

    private boolean hasPermission(Player player, String permission) {
        return this.getModule().getPermissionService().hasPermission(player, permission);
    }

    private PlayerReport getPlayerReports() {
        return this.getStore().getPlayerReportStore().get();
    }
}

