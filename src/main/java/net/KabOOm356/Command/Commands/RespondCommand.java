/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Command.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.RespondPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.ViewPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.ReportValidatorService;
import net.KabOOm356.Service.SQLStatService;
import net.KabOOm356.Service.SQLStatServices.ModeratorStatService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Throwable.IndexNotANumberException;
import net.KabOOm356.Throwable.IndexOutOfRangeException;
import net.KabOOm356.Throwable.NoLastViewedReportException;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RespondCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(RespondCommand.class);
    private static final String name = "Respond";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.respond";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)RespondPhrases.respondHelp, (Entry<String>)RespondPhrases.respondHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public RespondCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 1);
    }

    public static String getCommandName() {
        return name;
    }

    public static String getCommandPermissionNode() {
        return permissionNode;
    }

    @Override
    public void execute(CommandSender sender, ArrayList<String> args) throws NoLastViewedReportException, IndexOutOfRangeException, IndexNotANumberException {
        try {
            if (!this.hasRequiredPermission(sender)) {
                return;
            }
            Player player = null;
            if (!BukkitUtil.isPlayer(sender)) {
                sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + "You must be a player to use this command!");
                return;
            }
            player = (Player)sender;
            int index = this.getServiceModule().getLastViewedReportService().getIndexOrLastViewedReport(sender, args.get(0));
            if (!this.getServiceModule().getReportValidatorService().isReportIndexValid(index)) {
                return;
            }
            if (args.size() == 1) {
                this.teleportToReport(player, index, "reported");
            } else if (args.size() >= 2) {
                this.teleportToReport(player, index, args.get(1));
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to respond to report!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
        }
    }

    @Override
    public List<Usage> getUsages() {
        return usages;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    private void teleportToReport(Player player, int index, String playerLoc) throws ClassNotFoundException, SQLException, InterruptedException {
        if (!playerLoc.equalsIgnoreCase("sender") && !playerLoc.equalsIgnoreCase("reported")) {
            player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(this.getUsage()));
        } else {
            double Z;
            boolean requestedToReported;
            String sender;
            int id;
            boolean sendToReported;
            String World2;
            String details;
            double Y;
            String reported;
            double X;
            sendToReported = requestedToReported = playerLoc.equalsIgnoreCase("reported");
            id = -1;
            X = 0.0;
            Y = 0.0;
            Z = 0.0;
            World2 = null;
            reported = null;
            sender = null;
            details = null;
            ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
            int connectionId = database.openPooledConnection();
            try {
                String query = "SELECT ID, ReportedUUID, Reported, SenderUUID, Sender, Details, SenderX, SenderY, SenderZ, SenderWorld, ReportedX, ReportedY, ReportedZ, ReportedWorld FROM Reports WHERE ID=" + index;
                SQLResultSet result = database.sqlQuery(connectionId, query);
                for (int LCV = 0; LCV < 2; ++LCV) {
                    if (sendToReported) {
                        X = result.getDouble("ReportedX");
                        Y = result.getDouble("ReportedY");
                        Z = result.getDouble("ReportedZ");
                        World2 = result.getString("ReportedWorld");
                    } else {
                        X = result.getDouble("SenderX");
                        Y = result.getDouble("SenderY");
                        Z = result.getDouble("SenderZ");
                        World2 = result.getString("SenderWorld");
                    }
                    if ((X != 0.0 || Y != 0.0 || Z != 0.0) && World2 != null && !World2.isEmpty()) break;
                    sendToReported = !sendToReported;
                }
                if (X == 0.0 && Y == 0.0 && Z == 0.0 || World2 == null || World2.isEmpty()) {
                    player.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(RespondPhrases.bothPlayerLocNF));
                    player.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(RespondPhrases.teleAbort));
                    return;
                }
                id = result.getInt("ID");
                if (!result.getString("ReportedUUID").isEmpty()) {
                    UUID uuid = UUID.fromString(result.getString("ReportedUUID"));
                    OfflinePlayer reportedPlayer = Bukkit.getOfflinePlayer((UUID)uuid);
                    reported = BukkitUtil.formatPlayerName(reportedPlayer);
                } else {
                    reported = result.getString("Reported");
                }
                if (!result.getString("SenderUUID").isEmpty()) {
                    UUID uuid = UUID.fromString(result.getString("SenderUUID"));
                    OfflinePlayer senderPlayer = Bukkit.getOfflinePlayer((UUID)uuid);
                    sender = BukkitUtil.formatPlayerName(senderPlayer);
                } else {
                    sender = result.getString("Sender");
                }
                details = result.getString("Details");
            }
            catch (SQLException e) {
                log.log(Level.ERROR, String.format("Failed to respond to report on connection [%s]!", connectionId));
                throw e;
            }
            finally {
                database.closeConnection(connectionId);
            }
            if (requestedToReported) {
                if (sendToReported) {
                    player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getManager().getLocale().getString(RespondPhrases.telReported));
                } else {
                    player.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(RespondPhrases.reportedPlayerLocNF));
                    player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getManager().getLocale().getString(RespondPhrases.telSender));
                }
            } else if (sendToReported) {
                player.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(RespondPhrases.senderLocNF));
                player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getManager().getLocale().getString(RespondPhrases.telReported));
            } else {
                player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getManager().getLocale().getString(RespondPhrases.telSender));
            }
            String out = this.getManager().getLocale().getString(RespondPhrases.respondTeleportLocation);
            out = out.replaceAll("%world", (Object)ChatColor.GOLD + World2 + (Object)ChatColor.WHITE);
            out = out.replaceAll("%x", (Object)ChatColor.GOLD + Double.toString(Math.round(X)) + (Object)ChatColor.WHITE);
            out = out.replaceAll("%y", (Object)ChatColor.GOLD + Double.toString(Math.round(Y)) + (Object)ChatColor.WHITE);
            out = out.replaceAll("%z", (Object)ChatColor.GOLD + Double.toString(Math.round(Z)) + (Object)ChatColor.WHITE);
            player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
            String reportInfo = this.getManager().getLocale().getString(ViewPhrases.viewAllReportHeader);
            String reportInfoDetails = this.getManager().getLocale().getString(ViewPhrases.viewAllReportDetails);
            reportInfo = reportInfo.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(id) + (Object)ChatColor.WHITE);
            reportInfo = reportInfo.replaceAll("%r", (Object)ChatColor.GOLD + reported + (Object)ChatColor.WHITE);
            reportInfo = reportInfo.replaceAll("%s", (Object)ChatColor.GOLD + sender + (Object)ChatColor.WHITE);
            reportInfoDetails = reportInfoDetails.replaceAll("%d", (Object)ChatColor.GOLD + details);
            player.sendMessage((Object)ChatColor.WHITE + reportInfo);
            player.sendMessage((Object)ChatColor.WHITE + reportInfoDetails);
            Location loc = new Location(Bukkit.getWorld((String)World2), X, Y, Z);
            player.teleport(loc);
            this.getServiceModule().getModStatsService().incrementStat((OfflinePlayer)player, ModeratorStatService.ModeratorStat.RESPONDED);
        }
    }
}

