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
import net.KabOOm356.Locale.Entry.LocalePhrases.UnassignPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.LastViewedReportService;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnassignCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(UnassignCommand.class);
    private static final String name = "Unassign";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.unassign";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)UnassignPhrases.unassignHelp, (Entry<String>)UnassignPhrases.unassignHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public UnassignCommand(ReporterCommandManager manager) {
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
            this.unassignReport(sender, index);
        }
        catch (Exception e) {
            log.error("Failed to unassign report!", (Throwable)e);
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

    private void unassignReport(CommandSender sender, int index) throws ClassNotFoundException, SQLException, InterruptedException {
        String claimedByUUID;
        String claimedBy;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ClaimedByUUID, ClaimedBy FROM Reports WHERE ID=").append(index);
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query.toString());
            claimedByUUID = result.getString("ClaimedByUUID");
            claimedBy = result.getString("ClaimedBy");
            query = new StringBuilder();
            query.append("UPDATE Reports ");
            query.append("SET ");
            query.append("ClaimStatus=0, ClaimedByUUID='', ClaimedBy='', ClaimPriority=0, ClaimDate='' ");
            query.append("WHERE ID=").append(index);
            database.updateQuery(connectionId, query.toString());
        }
        catch (SQLException e) {
            log.error(String.format("Failed to execute unassign query on connection [%s]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        String playerName = claimedBy;
        OfflinePlayer claimingPlayer = null;
        if (!claimedByUUID.isEmpty()) {
            UUID uuid = UUID.fromString(claimedByUUID);
            claimingPlayer = Bukkit.getOfflinePlayer((UUID)uuid);
            playerName = BukkitUtil.formatPlayerName(claimingPlayer);
        }
        String output = this.getManager().getLocale().getString(UnassignPhrases.reportUnassignSuccess);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        output = output.replaceAll("%p", (Object)ChatColor.GOLD + playerName + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer senderPlayer = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(senderPlayer, ModeratorStatService.ModeratorStat.UNASSIGNED);
        }
        if (claimingPlayer != null && claimingPlayer.isOnline()) {
            playerName = BukkitUtil.formatPlayerName(sender);
            output = this.getManager().getLocale().getString(UnassignPhrases.unassignedFromReport);
            output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED);
            output = output.replaceAll("%s", (Object)ChatColor.GOLD + playerName + (Object)ChatColor.RED);
            claimingPlayer.getPlayer().sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + output);
        }
    }
}

