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
import net.KabOOm356.Locale.Entry.LocalePhrases.UnclaimPhrases;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnclaimCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(UnclaimCommand.class);
    private static final String name = "Unclaim";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.claim";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)UnclaimPhrases.unclaimHelp, (Entry<String>)UnclaimPhrases.unclaimHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public UnclaimCommand(ReporterCommandManager manager) {
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
            if (this.canUnclaimReport(sender, index)) {
                this.unclaimReport(sender, index);
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to unclaim report!", (Throwable)e);
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

    private boolean canUnclaimReport(CommandSender sender, int index) throws ClassNotFoundException, SQLException, InterruptedException {
        block13 : {
            String query = "SELECT ClaimStatus, ClaimedByUUID, ClaimedBy FROM Reports WHERE ID=" + index;
            ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
            int connectionId = database.openPooledConnection();
            try {
                SQLResultSet result = database.sqlQuery(connectionId, query);
                if (result.getBoolean("ClaimStatus").booleanValue()) {
                    boolean senderIsClaimingPlayer = false;
                    Player claimingPlayer = null;
                    if (!result.getString("ClaimedByUUID").isEmpty()) {
                        Player senderPlayer;
                        UUID uuid = UUID.fromString(result.getString("ClaimedByUUID"));
                        claimingPlayer = Bukkit.getPlayer((UUID)uuid);
                        if (BukkitUtil.isPlayer(sender) && (senderPlayer = (Player)sender).getUniqueId().equals(claimingPlayer.getUniqueId())) {
                            senderIsClaimingPlayer = true;
                        }
                    } else if (sender.getName().equals(result.getString("ClaimedBy"))) {
                        senderIsClaimingPlayer = true;
                    }
                    if (!senderIsClaimingPlayer) {
                        String output = this.getManager().getLocale().getString(UnclaimPhrases.reportAlreadyClaimed);
                        String claimedBy = result.getString("ClaimedBy");
                        if (claimingPlayer != null) {
                            claimedBy = BukkitUtil.formatPlayerName((OfflinePlayer)claimingPlayer);
                        }
                        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED);
                        output = output.replaceAll("%c", (Object)ChatColor.BLUE + claimedBy + (Object)ChatColor.RED);
                        sender.sendMessage((Object)ChatColor.RED + output);
                        boolean bl = false;
                        return bl;
                    }
                    break block13;
                }
                String output = this.getManager().getLocale().getString(UnclaimPhrases.reportIsNotClaimed);
                output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED);
                sender.sendMessage((Object)ChatColor.RED + output);
                boolean claimingPlayer = false;
                return claimingPlayer;
            }
            catch (SQLException e) {
                log.error(String.format("Failed to determine if player can unclaim report on connection [%s]!", connectionId));
                throw e;
            }
            finally {
                database.closeConnection(connectionId);
            }
        }
        return true;
    }

    private void unclaimReport(CommandSender sender, int index) throws ClassNotFoundException, SQLException, InterruptedException {
        String query = "UPDATE Reports SET ClaimStatus=0, ClaimedByUUID='', ClaimedBy='', ClaimPriority=0, ClaimDate='' WHERE ID=" + index;
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            database.updateQuery(connectionId, query);
        }
        catch (SQLException e) {
            log.error(String.format("Failed to execute unclaim query on connection [%s]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        String output = this.getManager().getLocale().getString(UnclaimPhrases.reportUnclaimSuccess);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer senderPlayer = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(senderPlayer, ModeratorStatService.ModeratorStat.UNCLAIMED);
        }
    }
}

