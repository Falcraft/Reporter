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
import net.KabOOm356.Database.ResultRow;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.RequestPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class RequestCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(RequestCommand.class);
    private static final String name = "Request";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.request";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)RequestPhrases.requestHelp, (Entry<String>)RequestPhrases.requestHelpDetails), new Usage("/report request most", (Entry<String>)RequestPhrases.requestMostHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public RequestCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 1);
    }

    public static String getCommandName() {
        return name;
    }

    public static String getCommandPermissionNode() {
        return permissionNode;
    }

    @Override
    public void execute(CommandSender sender, ArrayList<String> args) {
        try {
            if (this.hasRequiredPermission(sender)) {
                if (args.get(0).equalsIgnoreCase("most")) {
                    this.requestMostReported(sender);
                } else {
                    this.requestPlayer(sender, args.get(0));
                }
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to request!", (Throwable)e);
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

    private void requestMostReported(CommandSender sender) throws ClassNotFoundException, SQLException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) AS Count, ReportedUUID, Reported ");
        query.append("FROM Reports ");
        query.append("GROUP BY ReportedUUID HAVING COUNT(*) = ").append('(').append("SELECT COUNT(*) ").append("FROM Reports ").append("GROUP BY ReportedUUID ORDER BY COUNT(*) DESC ").append("LIMIT 1").append(')');
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            ArrayList<String> players = new ArrayList<String>();
            int numberOfReports = -1;
            SQLResultSet result = database.sqlQuery(connectionId, query.toString());
            for (ResultRow row : result) {
                numberOfReports = result.getInt("Count");
                String uuidString = row.getString("ReportedUUID");
                if (!uuidString.isEmpty()) {
                    UUID uuid = UUID.fromString(uuidString);
                    OfflinePlayer player = Bukkit.getOfflinePlayer((UUID)uuid);
                    players.add(BukkitUtil.formatPlayerName(player));
                    continue;
                }
                players.add(result.getString("Reported"));
            }
            if (!players.isEmpty()) {
                String out = this.getManager().getLocale().getString(RequestPhrases.numberOfReportsAgainst);
                out = out.replaceAll("%n", (Object)ChatColor.GOLD + Integer.toString(numberOfReports) + (Object)ChatColor.WHITE);
                out = out.replaceAll("%p", ArrayUtil.indexesToString(players, ChatColor.GOLD, ChatColor.WHITE) + (Object)ChatColor.WHITE);
                sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
            } else {
                sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + this.getManager().getLocale().getString(GeneralPhrases.noReports));
            }
        }
        catch (SQLException e) {
            log.log(Level.ERROR, "Failed to request most reported player!", (Throwable)e);
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void requestPlayer(CommandSender sender, String playerName) throws ClassNotFoundException, SQLException, InterruptedException {
        OfflinePlayer player;
        String indexes;
        player = this.getManager().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage((Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(GeneralPhrases.playerDoesNotExist)));
            return;
        }
        indexes = "";
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            ArrayList<String> params = new ArrayList<String>();
            String query = "SELECT ID FROM Reports WHERE ReportedUUID=?";
            if (!player.getName().equalsIgnoreCase("* (Anonymous)")) {
                params.add(player.getUniqueId().toString());
            } else {
                query = "SELECT ID FROM Reports WHERE Reported=?";
                params.add(player.getName());
            }
            if (this.getManager().getDatabaseHandler().usingSQLite()) {
                query = query + " COLLATE NOCASE";
            }
            SQLResultSet result = this.getManager().getDatabaseHandler().preparedSQLQuery(connectionId, query, params);
            indexes = ArrayUtil.indexesToString(result, "ID", ChatColor.GOLD, ChatColor.WHITE);
        }
        catch (SQLException e) {
            log.log(Level.ERROR, "Failed to request player!", (Throwable)e);
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        String out = null;
        if (indexes.isEmpty()) {
            out = BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(RequestPhrases.reqNF));
            out = out.replaceAll("%p", (Object)ChatColor.GOLD + player.getName() + (Object)ChatColor.RED);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + out);
        } else {
            out = BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(RequestPhrases.reqFI));
            out = out.replaceAll("%p", (Object)ChatColor.GOLD + player.getName() + (Object)ChatColor.WHITE);
            out = out.replaceAll("%i", indexes);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
        }
    }
}

