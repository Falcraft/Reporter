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
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Command.Commands;

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
import net.KabOOm356.Locale.Entry.LocalePhrases.ListPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.ViewPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.ReportCountService;
import net.KabOOm356.Service.ReportInformationService;
import net.KabOOm356.Service.ReportValidatorService;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ViewCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(ViewCommand.class);
    private static final String name = "View";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.view";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)ViewPhrases.viewHelp, (Entry<String>)ViewPhrases.viewHelpDetails), new Usage("/report view all [name]", (Entry<String>)ViewPhrases.viewHelpAllDetails), new Usage("/report view completed|finished [name]", (Entry<String>)ViewPhrases.viewHelpCompletedDetails), new Usage("/report view incomplete|unfinished [name]", (Entry<String>)ViewPhrases.viewHelpIncompleteDetails), new Usage("/report view priority [name]", (Entry<String>)ViewPhrases.viewHelpPriorityDetails), new Usage((Entry<String>)ViewPhrases.viewHelpGivenPriority, (Entry<String>)ViewPhrases.viewHelpGivenPriorityDetails), new Usage("/report view claimed [name]", (Entry<String>)ViewPhrases.viewHelpClaimedDetails), new Usage("/report view claimed priority [name]", (Entry<String>)ViewPhrases.viewHelpClaimedPriorityDetails), new Usage((Entry<String>)ViewPhrases.viewHelpClaimedGivenPriority, (Entry<String>)ViewPhrases.viewHelpClaimedPriorityDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public ViewCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 1);
    }

    private static String[] readQuickData(ResultRow row, boolean displayRealName) {
        String[] array = new String[4];
        array[0] = row.getString("ID");
        String senderName = row.getString("Sender");
        if (!row.getString("SenderUUID").isEmpty()) {
            UUID uuid = UUID.fromString(row.getString("SenderUUID"));
            OfflinePlayer sender = Bukkit.getOfflinePlayer((UUID)uuid);
            senderName = BukkitUtil.formatPlayerName(sender, displayRealName);
        }
        array[1] = senderName;
        String reportedName = row.getString("Reported");
        if (!row.getString("ReportedUUID").isEmpty()) {
            UUID uuid = UUID.fromString(row.getString("ReportedUUID"));
            OfflinePlayer reported = Bukkit.getOfflinePlayer((UUID)uuid);
            reportedName = BukkitUtil.formatPlayerName(reported, displayRealName);
        }
        array[2] = reportedName;
        array[3] = row.getString("Details");
        return array;
    }

    public static String getCommandName() {
        return name;
    }

    public static String getCommandPermissionNode() {
        return permissionNode;
    }

    @Override
    public void execute(CommandSender sender, ArrayList<String> args) throws NoLastViewedReportException, IndexOutOfRangeException, IndexNotANumberException {
        block26 : {
            try {
                if (this.hasPermission(sender)) {
                    if (args.get(0).equalsIgnoreCase("all")) {
                        this.viewAll(sender, this.displayRealName(args, 1));
                    } else if (args.get(0).equalsIgnoreCase("completed") || args.get(0).equalsIgnoreCase("finished")) {
                        this.viewCompleted(sender, this.displayRealName(args, 1));
                    } else if (args.get(0).equalsIgnoreCase("incomplete") || args.get(0).equalsIgnoreCase("unfinished")) {
                        this.viewIncomplete(sender, this.displayRealName(args, 1));
                    } else if (args.get(0).equalsIgnoreCase("priority")) {
                        if (args.size() >= 2 && ModLevel.modLevelInBounds(args.get(1))) {
                            ModLevel level = ModLevel.getModLevel(args.get(1));
                            this.viewPriority(sender, level, this.displayRealName(args, 2));
                        } else {
                            this.viewPriority(sender, this.displayRealName(args, 1));
                        }
                    } else if (args.get(0).equalsIgnoreCase("claimed")) {
                        if (args.size() >= 2 && args.get(1).equalsIgnoreCase("priority")) {
                            if (args.size() >= 3 && ModLevel.modLevelInBounds(args.get(2))) {
                                ModLevel level = ModLevel.getModLevel(args.get(2));
                                this.viewClaimedPriority(sender, level, this.displayRealName(args, 3));
                            } else {
                                this.viewClaimedPriority(sender, this.displayRealName(args, 2));
                            }
                        } else {
                            this.viewClaimed(sender, this.displayRealName(args, 1));
                        }
                    } else {
                        int index = this.getServiceModule().getLastViewedReportService().getIndexOrLastViewedReport(sender, args.get(0));
                        if (!this.getServiceModule().getReportValidatorService().isReportIndexValid(index)) {
                            return;
                        }
                        this.viewReport(sender, index, this.displayRealName(args, 1));
                    }
                    break block26;
                }
                if (this.getManager().getConfig().getBoolean("general.canViewSubmittedReports", true)) {
                    List<Integer> indexes = null;
                    try {
                        indexes = this.getServiceModule().getReportInformationService().getViewableReports(sender);
                    }
                    catch (Exception e) {
                        log.log(Level.ERROR, "Failed to view submitted report!");
                        throw e;
                    }
                    int index = this.getServiceModule().getLastViewedReportService().getIndexOrLastViewedReport(sender, args.get(0));
                    if (!this.getServiceModule().getReportValidatorService().isReportIndexValid(index)) {
                        return;
                    }
                    if (indexes.contains(index)) {
                        this.viewReport(sender, index, false);
                    } else {
                        this.displayAvailableReports(sender, indexes);
                    }
                    break block26;
                }
                sender.sendMessage(this.getFailedPermissionsMessage());
            }
            catch (Exception e) {
                log.error("Failed to view report!", (Throwable)e);
                sender.sendMessage(this.getErrorMessage());
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

    private void viewPriority(CommandSender sender, boolean displayRealName) throws Exception {
        this.viewPriority(sender, ModLevel.NONE, displayRealName);
        this.viewPriority(sender, ModLevel.LOW, displayRealName);
        this.viewPriority(sender, ModLevel.NORMAL, displayRealName);
        this.viewPriority(sender, ModLevel.HIGH, displayRealName);
    }

    private void viewPriority(CommandSender sender, ModLevel level, boolean displayRealName) throws Exception {
        String query = "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details FROM Reports WHERE Priority = " + level.getLevel();
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            int numberOfReports = this.getServiceModule().getReportCountService().getNumberOfPriority(level);
            String[][] reports = new String[numberOfReports][4];
            int count = 0;
            SQLResultSet result = database.sqlQuery(connectionId, query);
            for (ResultRow row : result) {
                reports[count] = ViewCommand.readQuickData(row, displayRealName);
                ++count;
            }
            this.printPriority(sender, level, reports);
        }
        catch (Exception e) {
            log.log(Level.ERROR, String.format("Failed to view reports with priority [%s]!", level.getName()));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void viewClaimedPriority(CommandSender sender, boolean displayRealName) throws Exception {
        this.viewClaimedPriority(sender, ModLevel.NONE, displayRealName);
        this.viewClaimedPriority(sender, ModLevel.LOW, displayRealName);
        this.viewClaimedPriority(sender, ModLevel.NORMAL, displayRealName);
        this.viewClaimedPriority(sender, ModLevel.HIGH, displayRealName);
    }

    private void viewClaimedPriority(CommandSender sender, ModLevel level, boolean displayRealName) throws Exception {
        String query = "SELECT COUNT(*) AS Count FROM Reports WHERE ClaimStatus = 1 AND ClaimedBy = '" + sender.getName() + "' AND Priority = " + level.getLevel();
        Player senderPlayer = null;
        if (BukkitUtil.isPlayer(sender)) {
            senderPlayer = (Player)sender;
            UUID uuid = senderPlayer.getUniqueId();
            query = "SELECT COUNT(*) AS Count FROM Reports WHERE ClaimStatus = 1 AND ClaimedByUUID = '" + uuid.toString() + "' AND Priority = " + level.getLevel();
        }
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query);
            int count = result.getInt("Count");
            query = senderPlayer != null ? "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details FROM Reports WHERE ClaimStatus = 1 AND ClaimedByUUID = '" + senderPlayer.getUniqueId() + "' AND Priority = " + level.getLevel() : "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details FROM Reports WHERE ClaimStatus = 1 AND ClaimedBy = '" + sender.getName() + "' AND Priority = " + level.getLevel();
            result = database.sqlQuery(connectionId, query);
            String[][] reports = new String[count][4];
            count = 0;
            for (ResultRow row : result) {
                reports[count] = ViewCommand.readQuickData(row, displayRealName);
                ++count;
            }
            this.printPriority(sender, level, reports);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to view claimed reports by priority!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void viewClaimed(CommandSender sender, boolean displayRealName) throws Exception {
        String query = "SELECT COUNT(*) AS Count FROM Reports WHERE ClaimStatus = 1 AND ClaimedBy = '" + sender.getName() + '\'';
        Player senderPlayer = null;
        if (BukkitUtil.isPlayer(sender)) {
            senderPlayer = (Player)sender;
            UUID uuid = senderPlayer.getUniqueId();
            query = "SELECT COUNT(*) AS Count FROM Reports WHERE ClaimStatus = 1 AND ClaimedByUUID = '" + uuid + '\'';
        }
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query);
            int claimedCount = result.getInt("Count");
            String[][] claimed = new String[claimedCount][4];
            query = senderPlayer != null ? "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details FROM Reports WHERE ClaimStatus = 1 AND ClaimedByUUID = '" + senderPlayer.getUniqueId() + '\'' : "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details FROM Reports WHERE ClaimStatus = 1 AND ClaimedBy = '" + sender.getName() + '\'';
            int count = 0;
            result = database.sqlQuery(connectionId, query);
            for (ResultRow row : result) {
                claimed[count] = ViewCommand.readQuickData(row, displayRealName);
                ++count;
            }
            String header = this.getManager().getLocale().getString(ViewPhrases.viewYourClaimedReportsHeader);
            sender.sendMessage((Object)ChatColor.GREEN + "-----" + header + "-----");
            this.printQuickView(sender, claimed);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to view claimed reports!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private void printPriority(CommandSender sender, ModLevel level, String[][] reports) {
        String header = this.getManager().getLocale().getString(ViewPhrases.viewPriorityHeader);
        header = header.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.GREEN);
        sender.sendMessage((Object)ChatColor.GREEN + "-----" + (Object)ChatColor.GREEN + header + (Object)ChatColor.GREEN + "------");
        this.printQuickView(sender, reports);
    }

    private void printQuickView(CommandSender sender, String[] report) {
        Locale locale = this.getManager().getLocale();
        String reportHeader = BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewAllReportHeader));
        String reportDetails = BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewAllReportDetails));
        String out = reportHeader.replaceAll("%i", (Object)ChatColor.GOLD + report[0] + (Object)ChatColor.WHITE);
        out = out.replaceAll("%s", (Object)ChatColor.BLUE + report[1] + (Object)ChatColor.WHITE);
        out = out.replaceAll("%r", (Object)ChatColor.RED + report[2] + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.WHITE + out);
        report[3] = report[3].replaceAll("\\$", "\\\\\\$");
        sender.sendMessage((Object)ChatColor.WHITE + reportDetails.replaceAll("%d", new StringBuilder().append((Object)ChatColor.GOLD).append(report[3]).append((Object)ChatColor.WHITE).toString()));
    }

    private void printQuickView(CommandSender sender, String[][] reports) {
        for (String[] entry : reports) {
            this.printQuickView(sender, entry);
        }
    }

    private boolean displayRealName(ArrayList<String> args, int index) {
        String argument;
        boolean displayRealName = this.getManager().getConfig().getBoolean("general.viewing.displayRealName", false);
        if (args.size() >= index + 1 && (argument = args.get(index)) != null && argument.equalsIgnoreCase("name")) {
            displayRealName = true;
        }
        return displayRealName;
    }

    private void displayAvailableReports(CommandSender sender, List<Integer> indexes) {
        String indexesString = ArrayUtil.indexesToString(indexes, ChatColor.GOLD, ChatColor.WHITE);
        Locale locale = this.getManager().getLocale();
        if (!indexesString.isEmpty()) {
            String out = locale.getString(ListPhrases.listReportsAvailable);
            out = out.replaceAll("%i", (Object)ChatColor.GOLD + indexesString + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + out);
        } else {
            sender.sendMessage((Object)ChatColor.RED + locale.getString(ListPhrases.listNoReportsAvailable));
        }
    }

    private void viewAll(CommandSender sender, boolean displayRealName) throws Exception {
        int cIndex;
        String[][] completed;
        String[][] notCompleted;
        int ncIndex;
        String query = "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports";
        notCompleted = null;
        completed = null;
        try {
            notCompleted = new String[this.getServiceModule().getReportCountService().getIncompleteReports()][4];
            completed = new String[this.getServiceModule().getReportCountService().getCompletedReports()][4];
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to initialize report arrays!");
            throw e;
        }
        cIndex = 0;
        ncIndex = 0;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports");
            for (ResultRow row : result) {
                if (row.getBoolean("CompletionStatus").booleanValue()) {
                    completed[cIndex] = ViewCommand.readQuickData(row, displayRealName);
                    ++cIndex;
                    continue;
                }
                notCompleted[ncIndex] = ViewCommand.readQuickData(row, displayRealName);
                ++ncIndex;
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to view all reports!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        if (cIndex != 0 || ncIndex != 0) {
            this.quickViewAll(sender, completed, notCompleted);
        } else {
            sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(ViewPhrases.noReportsToView));
        }
    }

    private void viewCompleted(CommandSender sender, boolean displayRealName) throws Exception {
        String[][] reports;
        int index;
        String query = "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports WHERE CompletionStatus = 1";
        reports = null;
        try {
            reports = new String[this.getServiceModule().getReportCountService().getCompletedReports()][4];
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to initialize completed report array!");
            throw e;
        }
        index = 0;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports WHERE CompletionStatus = 1");
            for (ResultRow row : result) {
                reports[index] = ViewCommand.readQuickData(row, displayRealName);
                ++index;
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to view all completed reports!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        if (index != 0) {
            this.quickViewCompleted(sender, reports);
        } else {
            sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(ListPhrases.listReportNoCompleteIndexes));
        }
    }

    private void viewIncomplete(CommandSender sender, boolean displayRealName) throws Exception {
        String[][] reports;
        int index;
        String query = "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports WHERE CompletionStatus = 0";
        reports = null;
        try {
            reports = new String[this.getServiceModule().getReportCountService().getIncompleteReports()][4];
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to initialize unfinished report array!");
            throw e;
        }
        index = 0;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT ID, SenderUUID, Sender, ReportedUUID, Reported, Details, CompletionStatus FROM Reports WHERE CompletionStatus = 0");
            for (ResultRow row : result) {
                reports[index] = ViewCommand.readQuickData(row, displayRealName);
                ++index;
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to view all incomplete reports!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        if (index != 0) {
            this.quickViewIncomplete(sender, reports);
        } else {
            sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(ListPhrases.listReportNoIncompleteIndexes));
        }
    }

    private void viewReport(CommandSender sender, int index, boolean displayRealName) throws Exception {
        String claimedBy;
        String dateReport;
        String reportedPlayer;
        String completedBy;
        int senderY;
        int senderX;
        String senderWorld;
        String priority;
        String reportedWorld;
        int reportedX;
        boolean claimStatus;
        String summaryDetails;
        int reportedZ;
        String completionDate;
        int senderZ;
        boolean completionStatus;
        int reportedY;
        String reporter;
        String claimDate;
        String reportDetails;
        String query = "SELECT * FROM Reports WHERE ID = " + index;
        reporter = null;
        reportedPlayer = null;
        reportDetails = null;
        dateReport = null;
        priority = null;
        senderWorld = null;
        reportedWorld = null;
        senderX = 0;
        senderY = 0;
        senderZ = 0;
        reportedX = 0;
        reportedY = 0;
        reportedZ = 0;
        claimStatus = false;
        claimedBy = null;
        claimDate = null;
        completionStatus = false;
        completedBy = null;
        completionDate = null;
        summaryDetails = null;
        OfflinePlayer player = null;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            UUID uuid;
            UUID uuid2;
            SQLResultSet result = database.sqlQuery(connectionId, query);
            reporter = result.getString("Sender");
            if (!result.getString("SenderUUID").isEmpty()) {
                uuid = UUID.fromString(result.getString("SenderUUID"));
                player = Bukkit.getOfflinePlayer((UUID)uuid);
                reporter = BukkitUtil.formatPlayerName(player, displayRealName);
            }
            senderWorld = result.getString("SenderWorld");
            senderX = (int)Math.round(result.getDouble("SenderX"));
            senderY = (int)Math.round(result.getDouble("SenderY"));
            senderZ = (int)Math.round(result.getDouble("SenderZ"));
            reportedPlayer = result.getString("Reported");
            if (!result.getString("ReportedUUID").isEmpty()) {
                uuid = UUID.fromString(result.getString("ReportedUUID"));
                player = Bukkit.getOfflinePlayer((UUID)uuid);
                reportedPlayer = BukkitUtil.formatPlayerName(player, displayRealName);
            }
            reportedWorld = result.getString("ReportedWorld");
            reportedX = (int)Math.round(result.getDouble("ReportedX"));
            reportedY = (int)Math.round(result.getDouble("ReportedY"));
            reportedZ = (int)Math.round(result.getDouble("ReportedZ"));
            reportDetails = result.getString("Details");
            dateReport = result.getString("Date");
            int priorityLevel = result.getInt("Priority");
            ModLevel priorityModLevel = ModLevel.getByLevel(priorityLevel);
            priority = (Object)priorityModLevel.getColor() + priorityModLevel.getName();
            claimStatus = result.getBoolean("ClaimStatus");
            claimedBy = result.getString("ClaimedBy");
            if (!result.getString("ClaimedByUUID").isEmpty()) {
                uuid2 = UUID.fromString(result.getString("ClaimedByUUID"));
                player = Bukkit.getOfflinePlayer((UUID)uuid2);
                claimedBy = BukkitUtil.formatPlayerName(player, displayRealName);
            }
            claimDate = result.getString("ClaimDate");
            completionStatus = result.getBoolean("CompletionStatus");
            completedBy = result.getString("CompletedBy");
            if (!result.getString("CompletedByUUID").isEmpty()) {
                uuid2 = UUID.fromString(result.getString("CompletedByUUID"));
                player = Bukkit.getOfflinePlayer((UUID)uuid2);
                completedBy = BukkitUtil.formatPlayerName(player, displayRealName);
            }
            completionStatus = result.getBoolean("CompletionStatus");
            completionDate = result.getString("CompletionDate");
            summaryDetails = result.getString("CompletionSummary");
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to display report view!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        this.printReport(sender, index, priority, reporter, senderWorld, senderX, senderY, senderZ, reportedPlayer, reportedWorld, reportedX, reportedY, reportedZ, reportDetails, dateReport, claimStatus, claimedBy, claimDate, completionStatus, completedBy, completionDate, summaryDetails);
        this.getServiceModule().getLastViewedReportService().playerViewed(sender, index);
    }

    private void quickViewCompleted(CommandSender sender, String[][] reports) {
        Locale locale = this.getManager().getLocale();
        String header = locale.getString(ViewPhrases.viewAllCompleteHeader);
        sender.sendMessage((Object)ChatColor.GREEN + "-----" + (Object)ChatColor.GREEN + header + (Object)ChatColor.GREEN + "------");
        String reportHeader = locale.getString(ViewPhrases.viewAllReportHeader);
        String reportDetails = locale.getString(ViewPhrases.viewAllReportDetails);
        for (int LCV = 0; LCV < reports.length; ++LCV) {
            String out = reportHeader.replaceAll("%i", (Object)ChatColor.GOLD + reports[LCV][0] + (Object)ChatColor.WHITE);
            out = out.replaceAll("%s", (Object)ChatColor.BLUE + reports[LCV][1] + (Object)ChatColor.WHITE);
            out = out.replaceAll("%r", (Object)ChatColor.RED + reports[LCV][2] + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + out);
            reports[LCV][3] = reports[LCV][3].replaceAll("\\$", "\\\\\\$");
            sender.sendMessage((Object)ChatColor.WHITE + reportDetails.replaceAll("%d", new StringBuilder().append((Object)ChatColor.GOLD).append(reports[LCV][3]).append((Object)ChatColor.WHITE).toString()));
        }
    }

    private void quickViewIncomplete(CommandSender sender, String[][] reports) {
        Locale locale = this.getManager().getLocale();
        String header = locale.getString(ViewPhrases.viewAllUnfinishedHeader);
        sender.sendMessage((Object)ChatColor.GREEN + "-----" + (Object)ChatColor.GREEN + header + (Object)ChatColor.GREEN + "------");
        String reportHeader = BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewAllReportHeader));
        String reportDetails = BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewAllReportDetails));
        for (int LCV = 0; LCV < reports.length; ++LCV) {
            String out = reportHeader.replaceAll("%i", (Object)ChatColor.GOLD + reports[LCV][0] + (Object)ChatColor.WHITE);
            out = out.replaceAll("%s", (Object)ChatColor.BLUE + reports[LCV][1] + (Object)ChatColor.WHITE);
            out = out.replaceAll("%r", (Object)ChatColor.RED + reports[LCV][2] + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + out);
            reports[LCV][3] = reports[LCV][3].replaceAll("\\$", "\\\\\\$");
            sender.sendMessage((Object)ChatColor.WHITE + reportDetails.replaceAll("%d", new StringBuilder().append((Object)ChatColor.GOLD).append(reports[LCV][3]).append((Object)ChatColor.WHITE).toString()));
        }
    }

    private void quickViewAll(CommandSender sender, String[][] complete, String[][] notComplete) {
        String viewAllBegin = BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ViewPhrases.viewAllBeginHeader));
        sender.sendMessage((Object)ChatColor.GOLD + "-----" + (Object)ChatColor.GOLD + viewAllBegin + (Object)ChatColor.GOLD + "------");
        this.quickViewCompleted(sender, complete);
        this.quickViewIncomplete(sender, notComplete);
    }

    private void printReport(CommandSender sender, int id, String priority, String reporter, String senderWorld, int senderX, int senderY, int senderZ, String reportedPlayer, String reportedWorld, int reportedX, int reportedY, int reportedZ, String reportDetails, String dateReport, boolean claimStatus, String claimedBy, String claimDate, boolean completionStatus, String completedBy, String completionDate, String summaryDetails) {
        StringBuilder output;
        Locale locale = this.getManager().getLocale();
        boolean displayLocation = this.getManager().getConfig().getBoolean("general.viewing.displayLocation", true);
        String begin = BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewBegin));
        begin = begin.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(id));
        sender.sendMessage((Object)ChatColor.WHITE + "-----" + (Object)ChatColor.BLUE + begin + (Object)ChatColor.WHITE + "------");
        if (!displayLocation || senderWorld.isEmpty() && senderX == 0 && senderY == 0 && senderZ == 0) {
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewSender)) + ' ' + (Object)ChatColor.BLUE + reporter);
        } else {
            output = new StringBuilder();
            output.append(locale.getString(ViewPhrases.viewSender)).append(' ');
            output.append((Object)ChatColor.BLUE).append(reporter).append((Object)ChatColor.GOLD).append(' ');
            output.append('(').append(senderWorld).append(": ");
            output.append(senderX).append(", ").append(senderY).append(", ").append(senderZ).append(')');
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(output.toString()));
        }
        if (!displayLocation || reportedWorld.isEmpty() && reportedX == 0 && reportedY == 0 && reportedZ == 0) {
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(this.getManager().getLocale().getString(ViewPhrases.viewReported)) + ' ' + (Object)ChatColor.RED + reportedPlayer);
        } else {
            output = new StringBuilder();
            output.append(locale.getString(ViewPhrases.viewReported)).append(' ');
            output.append((Object)ChatColor.BLUE).append(reportedPlayer).append((Object)ChatColor.GOLD).append(' ');
            output.append('(').append(reportedWorld).append(": ");
            output.append(reportedX).append(", ").append(reportedY).append(", ").append(reportedZ).append(')');
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(output.toString()));
        }
        sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewDetails)) + ' ' + (Object)ChatColor.GOLD + reportDetails);
        sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewPriority)) + ' ' + priority);
        sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewDate)) + ' ' + (Object)ChatColor.GREEN + dateReport);
        sender.sendMessage((Object)ChatColor.WHITE + "------" + (Object)ChatColor.BLUE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewSummaryTitle)) + (Object)ChatColor.WHITE + "------");
        if (!completionStatus) {
            if (claimStatus) {
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(new StringBuilder().append(locale.getString(ViewPhrases.viewClaimHeader)).append(' ').append((Object)ChatColor.GREEN).append(locale.getString(ViewPhrases.viewStatusClaimed)).toString()));
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(new StringBuilder().append(locale.getString(ViewPhrases.viewClaimedBy)).append(' ').append((Object)ChatColor.BLUE).append(claimedBy).toString()));
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(new StringBuilder().append(locale.getString(ViewPhrases.viewClaimedOn)).append(' ').append((Object)ChatColor.GREEN).append(claimDate).toString()));
            } else {
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(new StringBuilder().append(locale.getString(ViewPhrases.viewClaimHeader)).append(' ').append((Object)ChatColor.RED).append(locale.getString(ViewPhrases.viewStatusUnclaimed)).toString()));
            }
        }
        if (!completionStatus) {
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletionStatus)) + ' ' + (Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewUnfinished)));
        } else {
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletionStatus)) + ' ' + (Object)ChatColor.GREEN + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewFinished)));
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletedBy)) + ' ' + (Object)ChatColor.BLUE + completedBy);
            sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletedOn)) + ' ' + (Object)ChatColor.GREEN + completionDate);
            if (!summaryDetails.isEmpty()) {
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletedSummary)) + ' ' + (Object)ChatColor.GOLD + summaryDetails);
            } else {
                sender.sendMessage((Object)ChatColor.WHITE + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewCompletedSummary)) + ' ' + (Object)ChatColor.GOLD + BukkitUtil.colorCodeReplaceAll(locale.getString(ViewPhrases.viewNoSummary)));
            }
        }
    }
}

