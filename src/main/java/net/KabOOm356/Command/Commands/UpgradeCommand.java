/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Command.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.KabOOm356.Command.Commands.MoveCommand;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.UpgradePhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.ReportPermissionService;
import net.KabOOm356.Service.ReportValidatorService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Throwable.IndexNotANumberException;
import net.KabOOm356.Throwable.IndexOutOfRangeException;
import net.KabOOm356.Throwable.NoLastViewedReportException;
import net.KabOOm356.Util.ArrayUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class UpgradeCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(UpgradeCommand.class);
    private static final String name = "Upgrade";
    private static final String permissionNode = "reporter.move";
    private static final int minimumNumberOfArguments = 1;
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)UpgradePhrases.upgradeHelp, (Entry<String>)UpgradePhrases.upgradeHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public UpgradeCommand(ReporterCommandManager manager) {
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
            int index = this.getServiceModule().getLastViewedReportService().getIndexOrLastViewedReport(sender, args.get(0));
            if (!this.getServiceModule().getReportValidatorService().isReportIndexValid(index)) {
                return;
            }
            if (!this.getServiceModule().getReportPermissionService().canAlterReport(sender, index)) {
                return;
            }
            ModLevel newPriority = this.getNextPriorityLevel(index);
            if (newPriority == ModLevel.UNKNOWN) {
                String output = this.getManager().getLocale().getString(UpgradePhrases.reportIsAtHighestPriority);
                output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED);
                sender.sendMessage((Object)ChatColor.RED + output);
                return;
            }
            MoveCommand move = (MoveCommand)this.getManager().getCommand("Move");
            move.moveReport(sender, index, newPriority);
        }
        catch (Exception e) {
            log.error("Failed to upgrade report priority!", (Throwable)e);
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

    private ModLevel getNextPriorityLevel(int index) throws ClassNotFoundException, SQLException, InterruptedException {
        int connectionId;
        String query = "SELECT Priority FROM Reports WHERE ID=" + index;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        try {
            connectionId = database.openPooledConnection();
        }
        catch (ClassNotFoundException e) {
            log.error("Failed to open pooled connection to get next highest priority!");
            throw e;
        }
        catch (SQLException e) {
            log.error("Failed to open pooled connection to get next highest priority!");
            throw e;
        }
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query);
            int currentPriorityLevel = result.getInt("Priority");
            ModLevel modLevel = ModLevel.getByLevel(currentPriorityLevel + 1);
            return modLevel;
        }
        catch (SQLException e) {
            log.error(String.format("Failed to execute query to get next highest priority on connection [%s]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }
}

