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
import net.KabOOm356.Locale.Entry.LocalePhrases.MovePhrases;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(MoveCommand.class);
    private static final String name = "Move";
    private static final int minimumNumberOfArguments = 2;
    private static final String permissionNode = "reporter.move";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)MovePhrases.moveHelp, (Entry<String>)MovePhrases.moveHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public MoveCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 2);
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
            if (!this.getServiceModule().getPlayerService().requireModLevelInBounds(sender, args.get(1))) {
                return;
            }
            ModLevel priority = ModLevel.getModLevel(args.get(1));
            this.moveReport(sender, index, priority);
        }
        catch (Exception e) {
            log.error("Failed to execute move command!", (Throwable)e);
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

    protected void moveReport(CommandSender sender, int index, ModLevel level) throws ClassNotFoundException, SQLException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ClaimStatus, ClaimedByUUID, ClaimPriority ");
        query.append("FROM Reports ");
        query.append("WHERE ID=").append(index);
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query.toString());
            boolean isClaimed = result.getBoolean("ClaimStatus");
            int currentPriority = result.getInt("ClaimPriority");
            String claimedByUUIDString = result.getString("ClaimedByUUID");
            UUID claimedByUUID = null;
            if (!claimedByUUIDString.isEmpty()) {
                claimedByUUID = UUID.fromString(claimedByUUIDString);
            }
            if (isClaimed && level.getLevel() > currentPriority) {
                query = new StringBuilder();
                query.append("UPDATE Reports ");
                query.append("SET ").append("ClaimStatus='0', ").append("ClaimedByUUID='', ").append("ClaimedBy='', ").append("ClaimDate='', ").append("ClaimPriority=0, ").append("Priority=").append(level.getLevel()).append(' ');
                query.append("WHERE ID=").append(index);
                Player claimingPlayer = null;
                if (claimedByUUID != null) {
                    claimingPlayer = Bukkit.getPlayer((UUID)claimedByUUID);
                }
                if (claimingPlayer != null) {
                    String playerName = BukkitUtil.formatPlayerName(sender);
                    String output = this.getManager().getLocale().getString(MovePhrases.unassignedFromReport);
                    output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED);
                    output = output.replaceAll("%s", (Object)ChatColor.GOLD + playerName + (Object)ChatColor.RED);
                    claimingPlayer.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + output);
                }
            } else {
                query = new StringBuilder();
                query.append("UPDATE Reports ");
                query.append("SET ");
                query.append("Priority = ").append(level.getLevel()).append(' ');
                query.append("WHERE ID=").append(index);
            }
            database.updateQuery(connectionId, query.toString());
        }
        catch (SQLException e) {
            log.log(Level.ERROR, String.format("Failed to move report priority on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        String output = this.getManager().getLocale().getString(MovePhrases.moveReportSuccess);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        output = output.replaceAll("%p", (Object)level.getColor() + level.getName() + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.WHITE + output);
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer senderPlayer = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(senderPlayer, ModeratorStatService.ModeratorStat.MOVED);
        }
    }
}

