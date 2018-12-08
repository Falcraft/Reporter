/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Command.Commands;

import java.sql.SQLException;
import java.sql.Statement;
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
import net.KabOOm356.Database.SQL.QueryType;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.DeletePhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.PlayerMessageService;
import net.KabOOm356.Service.PlayerService;
import net.KabOOm356.Service.ReportCountService;
import net.KabOOm356.Service.ReportPermissionService;
import net.KabOOm356.Service.ReportValidatorService;
import net.KabOOm356.Service.SQLStatService;
import net.KabOOm356.Service.SQLStatServices.ModeratorStatService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Throwable.IndexNotANumberException;
import net.KabOOm356.Throwable.IndexOutOfRangeException;
import net.KabOOm356.Throwable.NoLastViewedReportException;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import net.KabOOm356.Util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(DeleteCommand.class);
    private static final String name = "Delete";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.delete";
    private static final ModeratorStatService.ModeratorStat statistic = ModeratorStatService.ModeratorStat.DELETED;
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)DeletePhrases.deleteHelp, (Entry<String>)DeletePhrases.deleteHelpDetails), new Usage("/report delete/remove all", (Entry<String>)DeletePhrases.deleteHelpAllDetails), new Usage("/report delete/remove completed|finished", (Entry<String>)DeletePhrases.deleteHelpCompletedDetails), new Usage("/report delete/remove incomplete|unfinished", (Entry<String>)DeletePhrases.deleteHelpIncompleteDetails), new Usage((Entry<String>)DeletePhrases.deleteHelpPlayer, (Entry<String>)DeletePhrases.deleteHelpPlayerDetails)}));
    private static final List<String> aliases = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new String[]{"Remove"}));

    public DeleteCommand(ReporterCommandManager manager) {
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
            int deletionCount = 0;
            if (args.get(0).equalsIgnoreCase("all")) {
                deletionCount = this.deleteReportBatch(sender, BatchDeletionType.ALL);
            } else if (args.get(0).equalsIgnoreCase("completed") || args.get(0).equalsIgnoreCase("finished")) {
                deletionCount = this.deleteReportBatch(sender, BatchDeletionType.COMPLETE);
            } else if (args.get(0).equalsIgnoreCase("incomplete") || args.get(0).equalsIgnoreCase("unfinished")) {
                deletionCount = this.deleteReportBatch(sender, BatchDeletionType.INCOMPLETE);
            } else if (Util.isInteger(args.get(0)) || args.get(0).equalsIgnoreCase("last")) {
                int index = this.getServiceModule().getLastViewedReportService().getIndexOrLastViewedReport(sender, args.get(0));
                if (!this.getServiceModule().getReportValidatorService().isReportIndexValid(index)) {
                    return;
                }
                if (!this.getServiceModule().getReportPermissionService().canAlterReport(sender, index)) {
                    return;
                }
                this.deleteReport(sender, index);
                deletionCount = 1;
            } else {
                OfflinePlayer player = this.getManager().getPlayer(args.get(0));
                if (player != null) {
                    deletionCount = args.size() >= 2 && args.get(1).equalsIgnoreCase("sender") ? this.deletePlayer(sender, PlayerDeletionType.SENDER, player) : this.deletePlayer(sender, PlayerDeletionType.REPORTED, player);
                }
            }
            if (deletionCount > 0) {
                this.incrementStatistic(sender, deletionCount);
            }
        }
        catch (Exception e) {
            log.error("Failed to execute delete command!", (Throwable)e);
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

    private void incrementStatistic(CommandSender sender, int count) {
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer player = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(player, statistic, count);
        }
    }

    private void deleteReport(CommandSender sender, int index) throws Exception {
        try {
            this.deleteReport(index);
            String out = this.getManager().getLocale().getString(DeletePhrases.deleteReport);
            out = out.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
            this.reformatTables(sender, index);
            this.updateLastViewed(index);
            this.getServiceModule().getPlayerMessageService().removeMessage(index);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to delete single report!");
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deleteReport(int index) throws ClassNotFoundException, SQLException, InterruptedException {
        String query = "Delete FROM Reports WHERE ID = " + index;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            database.updateQuery(connectionId, query);
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private int deletePlayer(CommandSender sender, PlayerDeletionType deletion, OfflinePlayer player) throws Exception {
        String query = this.getQuery(sender, player, QueryType.SELECT, deletion);
        int count = this.getServiceModule().getReportCountService().getCount();
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            ArrayList<Integer> remainingIndexes = new ArrayList<Integer>();
            SQLResultSet result = database.sqlQuery(connectionId, query);
            for (ResultRow row : result) {
                remainingIndexes.add(row.getInt("ID"));
            }
            query = this.getQuery(sender, player, QueryType.DELETE, deletion);
            database.updateQuery(connectionId, query);
            String message = deletion == PlayerDeletionType.REPORTED ? this.getManager().getLocale().getString(DeletePhrases.deletePlayerReported) : this.getManager().getLocale().getString(DeletePhrases.deletePlayerSender);
            String displayName = player.getName();
            if (player.isOnline()) {
                displayName = player.getPlayer().getDisplayName();
            }
            String playerName = BukkitUtil.formatPlayerName(displayName, player.getName());
            message = message.replaceAll("%p", (Object)ChatColor.BLUE + playerName + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + message);
            int totalDeleted = count - remainingIndexes.size();
            this.displayTotalReportsDeleted(sender, totalDeleted);
            this.reformatTables(sender, remainingIndexes);
            this.updateLastViewed(remainingIndexes);
            this.getServiceModule().getPlayerMessageService().reindexMessages(remainingIndexes);
            return totalDeleted;
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to delete reports for a player!");
            throw e;
        }
    }

    private String getQuery(CommandSender sender, OfflinePlayer player, QueryType queryType, PlayerDeletionType deletion) {
        if (queryType == QueryType.DELETE) {
            return this.getDeleteQuery(sender, player, deletion);
        }
        return this.getSelectQuery(sender, player, deletion);
    }

    private String getSelectQuery(CommandSender sender, OfflinePlayer player, PlayerDeletionType deletion) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID FROM Reports WHERE ");
        ModLevel level = this.getServiceModule().getPlayerService().getModLevel(sender);
        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
            if (player.getName().equalsIgnoreCase("* (Anonymous)")) {
                if (deletion == PlayerDeletionType.REPORTED) {
                    query.append("Reported != '").append(player.getName()).append('\'');
                } else if (deletion == PlayerDeletionType.SENDER) {
                    query.append("Sender != '").append(player.getName()).append('\'');
                }
            } else if (deletion == PlayerDeletionType.REPORTED) {
                query.append("ReportedUUID != '").append(player.getUniqueId()).append('\'');
            } else if (deletion == PlayerDeletionType.SENDER) {
                query.append("SenderUUID != '").append(player.getUniqueId()).append('\'');
            }
        } else {
            query.append("NOT (Priority <= ").append(level.getLevel()).append(" AND (ClaimStatus = 0 OR ClaimPriority < ").append(level.getLevel()).append(" OR ");
            if (BukkitUtil.isPlayer(sender)) {
                Player senderPlayer = (Player)sender;
                query.append("ClaimedByUUID = '").append(senderPlayer.getUniqueId()).append("') ");
            } else {
                query.append("ClaimedBy = '").append(sender.getName()).append("') ");
            }
            if (player.getName().equalsIgnoreCase("* (Anonymous)")) {
                if (deletion == PlayerDeletionType.REPORTED) {
                    query.append("AND Reported = '").append(player.getName()).append("')");
                } else if (deletion == PlayerDeletionType.SENDER) {
                    query.append("AND Sender = '").append(player.getName()).append("')");
                }
            } else if (deletion == PlayerDeletionType.REPORTED) {
                query.append("AND ReportedUUID = '").append(player.getUniqueId()).append("')");
            } else if (deletion == PlayerDeletionType.SENDER) {
                query.append("AND SenderUUID = '").append(player.getUniqueId()).append("')");
            }
        }
        return query.toString();
    }

    private String getDeleteQuery(CommandSender sender, OfflinePlayer player, PlayerDeletionType deletion) {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM Reports WHERE ");
        ModLevel level = this.getServiceModule().getPlayerService().getModLevel(sender);
        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
            if (player.getName().equals("* (Anonymous)")) {
                if (deletion == PlayerDeletionType.REPORTED) {
                    query.append("Reported = '").append(player.getName()).append('\'');
                } else if (deletion == PlayerDeletionType.SENDER) {
                    query.append("Sender = '").append(player.getName()).append('\'');
                }
            } else if (deletion == PlayerDeletionType.REPORTED) {
                query.append("ReportedUUID = '").append(player.getUniqueId()).append('\'');
            } else if (deletion == PlayerDeletionType.SENDER) {
                query.append("SenderUUID = '").append(player.getUniqueId()).append('\'');
            }
        } else {
            query.append("(Priority <= ").append(level.getLevel()).append(" AND (ClaimStatus = 0 OR ").append("ClaimPriority < ").append(level.getLevel()).append(" OR ");
            if (BukkitUtil.isPlayer(sender)) {
                Player senderPlayer = (Player)sender;
                query.append("ClaimedByUUID = '").append(senderPlayer.getUniqueId()).append("') ");
            } else {
                query.append("ClaimedBy = '").append(sender.getName()).append("') ");
            }
            if (player.getName().equals("* (Anonymous)")) {
                if (deletion == PlayerDeletionType.REPORTED) {
                    query.append("AND Reported = '").append(player.getName()).append("')");
                } else if (deletion == PlayerDeletionType.SENDER) {
                    query.append("AND Sender = '").append(player.getName()).append("')");
                }
            } else if (deletion == PlayerDeletionType.REPORTED) {
                query.append("AND ReportedUUID = '").append(player.getUniqueId()).append("')");
            } else if (deletion == PlayerDeletionType.SENDER) {
                query.append("AND SenderUUID = '").append(player.getUniqueId()).append("')");
            }
        }
        return query.toString();
    }

    private int deleteReportBatch(CommandSender sender, BatchDeletionType deletion) throws Exception {
        try {
            int beforeDeletion = this.getServiceModule().getReportCountService().getCount();
            ArrayList<Integer> remainingIndexes = this.getRemainingIndexes(sender, deletion);
            int afterDeletion = remainingIndexes.size();
            int totalDeleted = beforeDeletion - afterDeletion;
            this.deleteBatch(sender, deletion);
            this.reformatTables(sender, remainingIndexes);
            this.updateLastViewed(remainingIndexes);
            this.getServiceModule().getPlayerMessageService().reindexMessages(remainingIndexes);
            Locale locale = this.getManager().getLocale();
            String message = "";
            if (deletion == BatchDeletionType.ALL) {
                message = locale.getString(DeletePhrases.deleteAll);
            } else if (deletion == BatchDeletionType.COMPLETE) {
                message = locale.getString(DeletePhrases.deleteComplete);
            } else if (deletion == BatchDeletionType.INCOMPLETE) {
                message = locale.getString(DeletePhrases.deleteIncomplete);
            }
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + message);
            this.displayTotalReportsDeleted(sender, totalDeleted);
            return totalDeleted;
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to delete batch of reports!");
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deleteBatch(CommandSender sender, BatchDeletionType deletion) throws ClassNotFoundException, SQLException, InterruptedException {
        String query = this.getQuery(sender, QueryType.DELETE, deletion);
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            database.updateQuery(connectionId, query);
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private String getQuery(CommandSender sender, QueryType type, BatchDeletionType deletion) {
        if (type == QueryType.DELETE) {
            return this.getDeleteQuery(sender, deletion);
        }
        return this.getSelectQuery(sender, deletion);
    }

    private String getSelectQuery(CommandSender sender, BatchDeletionType deletion) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID FROM Reports WHERE ");
        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
            if (deletion == BatchDeletionType.ALL) {
                query.append('0');
            } else if (deletion == BatchDeletionType.COMPLETE) {
                query.append("CompletionStatus = 0");
            } else if (deletion == BatchDeletionType.INCOMPLETE) {
                query.append("CompletionStatus = 1");
            }
        } else {
            ModLevel level = this.getServiceModule().getPlayerService().getModLevel(sender);
            query.append("NOT (Priority <= ").append(level.getLevel()).append(' ').append("AND ").append("(ClaimStatus = 0 ").append("OR ").append("ClaimPriority < ").append(level.getLevel()).append(' ').append("OR ");
            if (BukkitUtil.isPlayer(sender)) {
                Player senderPlayer = (Player)sender;
                query.append("ClaimedByUUID = '").append(senderPlayer.getUniqueId()).append("')");
            } else {
                query.append("ClaimedBy = '").append(sender.getName()).append("')");
            }
            if (deletion == BatchDeletionType.ALL) {
                query.append(')');
            } else if (deletion == BatchDeletionType.COMPLETE) {
                query.append(' ').append("AND ").append("CompletionStatus = 0)");
            } else if (deletion == BatchDeletionType.INCOMPLETE) {
                query.append(' ').append("AND ").append("CompletionStatus = 1)");
            }
        }
        return query.toString();
    }

    private String getDeleteQuery(CommandSender sender, BatchDeletionType deletion) {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM Reports WHERE ");
        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
            if (deletion == BatchDeletionType.ALL) {
                query.append('1');
            } else if (deletion == BatchDeletionType.COMPLETE) {
                query.append("CompletionStatus = 1");
            } else if (deletion == BatchDeletionType.INCOMPLETE) {
                query.append("CompletionStatus = 0");
            }
        } else {
            ModLevel level = this.getServiceModule().getPlayerService().getModLevel(sender);
            query.append("(Priority <= ").append(level.getLevel()).append(' ').append("AND ").append("(ClaimStatus = 0 ").append("OR ").append("ClaimPriority < ").append(level.getLevel()).append(' ').append("OR ");
            if (BukkitUtil.isPlayer(sender)) {
                Player senderPlayer = (Player)sender;
                query.append("ClaimedByUUID = '").append(senderPlayer.getUniqueId()).append("')");
            } else {
                query.append("ClaimedBy = '").append(sender.getName()).append("')");
            }
            if (deletion == BatchDeletionType.ALL) {
                query.append(')');
            } else if (deletion == BatchDeletionType.COMPLETE) {
                query.append(' ').append("AND ").append("CompletionStatus = 1)");
            } else if (deletion == BatchDeletionType.INCOMPLETE) {
                query.append(' ').append("AND ").append("CompletionStatus = 0)");
            }
        }
        return query.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ArrayList<Integer> getRemainingIndexes(CommandSender sender, BatchDeletionType deletion) throws ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<Integer> remainingIDs;
        remainingIDs = new ArrayList<Integer>();
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            String query = this.getQuery(sender, QueryType.SELECT, deletion);
            SQLResultSet result = database.sqlQuery(connectionId, query);
            for (ResultRow row : result) {
                remainingIDs.add(row.getInt("ID"));
            }
        }
        finally {
            database.closeConnection(connectionId);
        }
        return remainingIDs;
    }

    private void updateLastViewed(int removedIndex) {
        this.getServiceModule().getLastViewedReportService().deleteIndex(removedIndex);
    }

    private void updateLastViewed(ArrayList<Integer> remainingIndexes) {
        this.getServiceModule().getLastViewedReportService().deleteBatch(remainingIndexes);
    }

    private void reformatTables(CommandSender sender, ArrayList<Integer> remainingIndexes) throws Exception {
        Statement stmt = null;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            stmt = database.createStatement(connectionId);
            for (int LCV = 0; LCV < remainingIndexes.size(); ++LCV) {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE Reports SET ID=").append(LCV + 1).append(" WHERE ID=").append(remainingIndexes.get(LCV));
                stmt.addBatch(query.toString());
            }
            stmt.executeBatch();
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed reformatting tables after batch delete!");
            throw e;
        }
        finally {
            block12 : {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block12;
                    log.warn("Failed to close statement!", (Throwable)e);
                }
            }
            database.closeConnection(connectionId);
        }
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(DeletePhrases.SQLTablesReformat)));
    }

    private void reformatTables(CommandSender sender, int removedIndex) throws Exception {
        int count = this.getServiceModule().getReportCountService().getCount();
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        Statement statement = null;
        try {
            statement = database.createStatement(connectionId);
            for (int LCV = removedIndex; LCV <= count; ++LCV) {
                StringBuilder formatQuery = new StringBuilder();
                formatQuery.append("UPDATE Reports SET ID=").append(LCV).append(" WHERE ID=").append(LCV + 1);
                statement.addBatch(formatQuery.toString());
            }
            statement.executeBatch();
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(DeletePhrases.SQLTablesReformat)));
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to reformat table after single delete!");
            throw e;
        }
        finally {
            block12 : {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block12;
                    log.warn("Failed to close statement!", (Throwable)e);
                }
            }
            database.closeConnection(connectionId);
        }
    }

    private void displayTotalReportsDeleted(CommandSender sender, int totalDeleted) {
        String message = this.getManager().getLocale().getString(DeletePhrases.deletedReportsTotal);
        message = message.replaceAll("%r", (Object)ChatColor.RED + Integer.toString(totalDeleted) + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + message);
    }

    private static enum PlayerDeletionType {
        SENDER,
        REPORTED;
        

        private PlayerDeletionType() {
        }
    }

    private static enum BatchDeletionType {
        ALL,
        INCOMPLETE,
        COMPLETE;
        

        private BatchDeletionType() {
        }
    }

}

