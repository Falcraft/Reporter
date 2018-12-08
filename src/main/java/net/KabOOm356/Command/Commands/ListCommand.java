/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
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
import net.KabOOm356.Locale.Entry.LocalePhrases.ListPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.ReportCountService;
import net.KabOOm356.Service.ReportInformationService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ListCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(ListCommand.class);
    private static final String name = "List";
    private static final int minimumNumberOfArguments = 0;
    private static final String permissionNode = "reporter.list";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage("/report list [indexes]", (Entry<String>)ListPhrases.listHelpDetails), new Usage("/report list priority [indexes]", (Entry<String>)ListPhrases.listHelpPriorityDetails), new Usage("/report list claimed [indexes]", (Entry<String>)ListPhrases.listHelpClaimedDetails), new Usage("/report list claimed priority [indexes]", (Entry<String>)ListPhrases.listHelpClaimedPriorityDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public ListCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 0);
    }

    public static String getCommandName() {
        return name;
    }

    public static String getCommandPermissionNode() {
        return permissionNode;
    }

    @Override
    public void execute(CommandSender sender, ArrayList<String> args) {
        block22 : {
            if (this.hasPermission(sender)) {
                try {
                    if (args == null || args.isEmpty()) {
                        this.listCommand(sender);
                        break block22;
                    }
                    if (args.size() >= 1 && args.get(0).equalsIgnoreCase("indexes")) {
                        this.listIndexes(sender);
                        break block22;
                    }
                    if (args.size() >= 1 && args.get(0).equalsIgnoreCase("priority")) {
                        if (args.size() >= 2 && args.get(1).equalsIgnoreCase("indexes")) {
                            this.listPriorityIndexes(sender);
                        } else {
                            this.listPriority(sender);
                        }
                        break block22;
                    }
                    if (args.size() >= 1 && args.get(0).equalsIgnoreCase("claimed")) {
                        if (args.size() >= 3 && args.get(1).equalsIgnoreCase("priority") && args.get(2).equalsIgnoreCase("indexes")) {
                            this.listClaimedPriorityIndexes(sender);
                        } else if (args.size() >= 2 && args.get(1).equalsIgnoreCase("indexes")) {
                            this.listClaimedIndexes(sender);
                        } else if (args.size() >= 2 && args.get(1).equalsIgnoreCase("priority")) {
                            this.listClaimedPriority(sender);
                        } else {
                            this.listClaimed(sender);
                        }
                        break block22;
                    }
                    sender.sendMessage((Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(this.getUsage()));
                }
                catch (Exception e) {
                    log.error("Failed to execute list command!", (Throwable)e);
                    sender.sendMessage(this.getErrorMessage());
                }
            } else if (this.getManager().getConfig().getBoolean("general.canViewSubmittedReports", true)) {
                List<Integer> indexes;
                try {
                    indexes = this.getServiceModule().getReportInformationService().getViewableReports(sender);
                }
                catch (Exception e) {
                    sender.sendMessage(this.getErrorMessage());
                    log.log(Level.ERROR, "Failed to list submitted reports!", (Throwable)e);
                    return;
                }
                String indexesString = ArrayUtil.indexesToString(indexes, ChatColor.GOLD, ChatColor.WHITE);
                if (!indexesString.isEmpty()) {
                    String out = (Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listReportsAvailable));
                    out = out.replaceAll("%i", (Object)ChatColor.GOLD + indexesString + (Object)ChatColor.WHITE);
                    sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
                } else {
                    sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listNoReportsAvailable)));
                }
            } else {
                sender.sendMessage(this.getFailedPermissionsMessage());
            }
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

    private void listClaimed(CommandSender sender) throws ClassNotFoundException, SQLException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) AS Count ");
        query.append("FROM Reports ");
        query.append("WHERE ClaimStatus = 1 AND ");
        if (BukkitUtil.isPlayer(sender)) {
            Player p = (Player)sender;
            query.append("ClaimedByUUID = '").append(p.getUniqueId()).append('\'');
        } else {
            query.append("ClaimedBy = '").append(sender.getName()).append('\'');
        }
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query.toString());
            int count = result.getInt("Count");
            String message = this.getManager().getLocale().getString(ListPhrases.listClaimed);
            message = message.replaceAll("%n", (Object)ChatColor.GOLD + Integer.toString(count) + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + message);
        }
        catch (SQLException e) {
            log.log(Level.ERROR, String.format("Failed to list claimed reports on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void listClaimedPriority(CommandSender sender) throws ClassNotFoundException, SQLException, InterruptedException {
        int lowPriorityCount;
        int highPriorityCount;
        int noPriorityCount;
        int normalPriorityCount;
        StringBuilder queryFormat = new StringBuilder();
        queryFormat.append("SELECT COUNT(*) AS Count ");
        queryFormat.append("FROM Reports ");
        queryFormat.append("WHERE ClaimStatus = 1 ");
        if (BukkitUtil.isPlayer(sender)) {
            Player p = (Player)sender;
            queryFormat.append("AND ClaimedByUUID = '").append(p.getUniqueId()).append("' ");
        } else {
            queryFormat.append("AND ClaimedBy = '").append(sender.getName()).append("' ");
        }
        queryFormat.append("AND Priority = ");
        String query = queryFormat.toString();
        noPriorityCount = 0;
        lowPriorityCount = 0;
        normalPriorityCount = 0;
        highPriorityCount = 0;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query + ModLevel.NONE.getLevel());
            noPriorityCount = result.getInt("Count");
            result = database.sqlQuery(connectionId, query + ModLevel.LOW.getLevel());
            lowPriorityCount = result.getInt("Count");
            result = database.sqlQuery(connectionId, query + ModLevel.NORMAL.getLevel());
            normalPriorityCount = result.getInt("Count");
            result = database.sqlQuery(connectionId, query + ModLevel.HIGH.getLevel());
            highPriorityCount = result.getInt("Count");
        }
        catch (SQLException e) {
            log.log(Level.ERROR, String.format("Failed to list claimed reports by priority on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        this.printClaimedPriorityCount(sender, ModLevel.NONE, noPriorityCount);
        this.printClaimedPriorityCount(sender, ModLevel.LOW, lowPriorityCount);
        this.printClaimedPriorityCount(sender, ModLevel.NORMAL, normalPriorityCount);
        this.printClaimedPriorityCount(sender, ModLevel.HIGH, highPriorityCount);
    }

    private void printClaimedPriorityCount(CommandSender sender, ModLevel level, int count) {
        String format = this.getManager().getLocale().getString(ListPhrases.listClaimedPriorityCount);
        String output = format.replaceAll("%n", (Object)level.getColor() + Integer.toString(count) + (Object)ChatColor.WHITE);
        output = output.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
    }

    private void listClaimedIndexes(CommandSender sender) throws ClassNotFoundException, SQLException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID ");
        query.append("FROM Reports ");
        query.append("WHERE ClaimStatus = 1 AND ");
        if (BukkitUtil.isPlayer(sender)) {
            Player p = (Player)sender;
            query.append("ClaimedByUUID = '").append(p.getUniqueId()).append('\'');
        } else {
            query.append("ClaimedBy = '").append(sender.getName()).append('\'');
        }
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query.toString());
            String indexes = ArrayUtil.indexesToString(result, "ID", ChatColor.GOLD, ChatColor.WHITE);
            String message = this.getManager().getLocale().getString(ListPhrases.listClaimedIndexes);
            message = message.replaceAll("%i", indexes);
            sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + message);
        }
        catch (SQLException e) {
            log.log(Level.ERROR, String.format("Failed to list claimed report indexes on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void listClaimedPriorityIndexes(CommandSender sender) throws ClassNotFoundException, SQLException, InterruptedException {
        String normalPriorityIndexes;
        String highPriorityIndexes;
        String lowPriorityIndexes;
        String noPriorityIndexes;
        StringBuilder queryFormat = new StringBuilder();
        queryFormat.append("SELECT ID ");
        queryFormat.append("FROM Reports ");
        queryFormat.append("WHERE ");
        queryFormat.append("ClaimStatus = 1 ");
        if (BukkitUtil.isPlayer(sender)) {
            Player p = (Player)sender;
            queryFormat.append("AND ClaimedByUUID = '").append(p.getUniqueId()).append("' ");
        } else {
            queryFormat.append("AND ClaimedBy = '").append(sender.getName()).append("' ");
        }
        queryFormat.append("AND Priority = ");
        String query = queryFormat.toString();
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query + ModLevel.NONE.getLevel());
            noPriorityIndexes = ArrayUtil.indexesToString(result, "ID", ModLevel.NONE.getColor(), ChatColor.WHITE);
            result = database.sqlQuery(connectionId, query + ModLevel.LOW.getLevel());
            lowPriorityIndexes = ArrayUtil.indexesToString(result, "ID", ModLevel.LOW.getColor(), ChatColor.WHITE);
            result = database.sqlQuery(connectionId, query + ModLevel.NORMAL.getLevel());
            normalPriorityIndexes = ArrayUtil.indexesToString(result, "ID", ModLevel.NORMAL.getColor(), ChatColor.WHITE);
            result = database.sqlQuery(connectionId, query + ModLevel.HIGH.getLevel());
            highPriorityIndexes = ArrayUtil.indexesToString(result, "ID", ModLevel.HIGH.getColor(), ChatColor.WHITE);
        }
        catch (SQLException e) {
            log.log(Level.ERROR, String.format("Failed to list claimed report indexes by priority on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        this.printClaimedPriorityIndexes(sender, ModLevel.NONE, noPriorityIndexes);
        this.printClaimedPriorityIndexes(sender, ModLevel.LOW, lowPriorityIndexes);
        this.printClaimedPriorityIndexes(sender, ModLevel.NORMAL, normalPriorityIndexes);
        this.printClaimedPriorityIndexes(sender, ModLevel.HIGH, highPriorityIndexes);
    }

    private void printClaimedPriorityIndexes(CommandSender sender, ModLevel level, String indexes) {
        String output;
        if (!indexes.isEmpty()) {
            output = this.getManager().getLocale().getString(ListPhrases.listClaimedPriorityIndexes);
            output = output.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
            output = output.replaceAll("%i", indexes);
        } else {
            output = this.getManager().getLocale().getString(ListPhrases.listNoClaimedPriorityIndexes);
            output = output.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        }
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
    }

    private void listPriority(CommandSender sender) {
        int noPriorityCount = 0;
        int lowPriorityCount = 0;
        int normalPriorityCount = 0;
        int highPriorityCount = 0;
        try {
            noPriorityCount = this.getServiceModule().getReportCountService().getNumberOfPriority(ModLevel.NONE);
            lowPriorityCount = this.getServiceModule().getReportCountService().getNumberOfPriority(ModLevel.LOW);
            normalPriorityCount = this.getServiceModule().getReportCountService().getNumberOfPriority(ModLevel.NORMAL);
            highPriorityCount = this.getServiceModule().getReportCountService().getNumberOfPriority(ModLevel.HIGH);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to list reports by priority!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
            return;
        }
        this.printPriorityCount(sender, ModLevel.NONE, noPriorityCount);
        this.printPriorityCount(sender, ModLevel.LOW, lowPriorityCount);
        this.printPriorityCount(sender, ModLevel.NORMAL, normalPriorityCount);
        this.printPriorityCount(sender, ModLevel.HIGH, highPriorityCount);
    }

    private void printPriorityCount(CommandSender sender, ModLevel level, int count) {
        String format = this.getManager().getLocale().getString(ListPhrases.listPriorityCount);
        String output = format.replaceAll("%n", (Object)level.getColor() + Integer.toString(count) + (Object)ChatColor.WHITE);
        output = output.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
    }

    private void listPriorityIndexes(CommandSender sender) {
        List<Integer> lowPriorityIndexes;
        List<Integer> noPriorityIndexes;
        List<Integer> highPriorityIndexes;
        List<Integer> normalPriorityIndexes;
        try {
            noPriorityIndexes = this.getServiceModule().getReportInformationService().getIndexesOfPriority(ModLevel.NONE);
            lowPriorityIndexes = this.getServiceModule().getReportInformationService().getIndexesOfPriority(ModLevel.LOW);
            normalPriorityIndexes = this.getServiceModule().getReportInformationService().getIndexesOfPriority(ModLevel.NORMAL);
            highPriorityIndexes = this.getServiceModule().getReportInformationService().getIndexesOfPriority(ModLevel.HIGH);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to list report indexes by priority!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
            return;
        }
        this.printPriorityIndexes(sender, ModLevel.NONE, noPriorityIndexes);
        this.printPriorityIndexes(sender, ModLevel.LOW, lowPriorityIndexes);
        this.printPriorityIndexes(sender, ModLevel.NORMAL, normalPriorityIndexes);
        this.printPriorityIndexes(sender, ModLevel.HIGH, highPriorityIndexes);
    }

    private void printPriorityIndexes(CommandSender sender, ModLevel level, List<Integer> indexes) {
        String output;
        if (!indexes.isEmpty()) {
            String format = this.getManager().getLocale().getString(ListPhrases.listPriorityIndexes);
            output = format.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
            output = output.replaceAll("%i", ArrayUtil.indexesToString(indexes, level.getColor(), ChatColor.WHITE));
        } else {
            String format = this.getManager().getLocale().getString(ListPhrases.listNoReportsWithPriority);
            output = format.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        }
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
    }

    private void listCommand(CommandSender sender) {
        int incompleteReports;
        int completeReports;
        String listString = BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.reportList));
        try {
            incompleteReports = this.getServiceModule().getReportCountService().getIncompleteReports();
            completeReports = this.getServiceModule().getReportCountService().getCompletedReports();
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to get number of complete and incomplete reports!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
            return;
        }
        if (completeReports != -1 && incompleteReports != -1) {
            String[] parts;
            listString = listString.replaceAll("%r", (Object)ChatColor.RED + Integer.toString(incompleteReports) + (Object)ChatColor.WHITE);
            listString = listString.replaceAll("%c", (Object)ChatColor.GREEN + Integer.toString(completeReports) + (Object)ChatColor.WHITE);
            String[] arrstring = parts = listString.split("%n");
            int n = arrstring.length;
            for (int i = 0; i < n; ++i) {
                String part;
                listString = part = arrstring[i];
                sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + listString);
            }
        } else {
            sender.sendMessage(this.getErrorMessage());
        }
    }

    private void listIndexes(CommandSender sender) {
        List<Integer> completeIndexes;
        List<Integer> incompleteIndexes;
        try {
            completeIndexes = this.getServiceModule().getReportInformationService().getCompletedReportIndexes();
            incompleteIndexes = this.getServiceModule().getReportInformationService().getIncompleteReportIndexes();
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to get number of complete and incomplete reports!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
            return;
        }
        String complete = ArrayUtil.indexesToString(completeIndexes, ChatColor.GREEN, ChatColor.WHITE);
        String incomplete = ArrayUtil.indexesToString(incompleteIndexes, ChatColor.RED, ChatColor.WHITE);
        String out = !completeIndexes.isEmpty() ? BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listReportCompleteIndexes)) : BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listReportNoCompleteIndexes));
        out = out.replaceAll("%i", complete);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
        out = !incompleteIndexes.isEmpty() ? BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listReportIncompleteIndexes)) : BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ListPhrases.listReportNoIncompleteIndexes));
        out = out.replaceAll("%i", incomplete);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + out);
    }
}

