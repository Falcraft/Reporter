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
 */
package net.KabOOm356.Command.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.ClaimPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.PlayerService;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ClaimCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(ClaimCommand.class);
    private static final String name = "Claim";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.claim";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)ClaimPhrases.claimHelp, (Entry<String>)ClaimPhrases.claimHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public ClaimCommand(ReporterCommandManager manager) {
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
            this.claimReport(sender, index);
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to claim report!", (Throwable)e);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void claimReport(CommandSender sender, int index) throws ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<String> params = new ArrayList<String>();
        params.add("1");
        params.add(BukkitUtil.getUUIDString(sender));
        params.add(sender.getName());
        params.add(Integer.toString(this.getServiceModule().getPlayerService().getModLevel(sender).getLevel()));
        params.add(Reporter.getDateformat().format(new Date()));
        params.add(Integer.toString(index));
        String query = "UPDATE Reports SET ClaimStatus=?, ClaimedByUUID=?, ClaimedBy=?, ClaimPriority=?, ClaimDate=? WHERE ID=?";
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            database.preparedUpdateQuery(connectionId, "UPDATE Reports SET ClaimStatus=?, ClaimedByUUID=?, ClaimedBy=?, ClaimPriority=?, ClaimDate=? WHERE ID=?", params);
        }
        catch (SQLException e) {
            log.error(String.format("Failed to execute claim query on connection [%d]!", connectionId));
        }
        finally {
            database.closeConnection(connectionId);
        }
        String output = this.getManager().getLocale().getString(ClaimPhrases.reportClaimSuccess);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer senderPlayer = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(senderPlayer, ModeratorStatService.ModeratorStat.CLAIMED);
        }
    }
}

