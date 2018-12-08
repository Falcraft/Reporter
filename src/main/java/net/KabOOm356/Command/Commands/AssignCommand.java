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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.AssignPhrases;
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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AssignCommand
extends ReporterCommand {
    private static final Logger log = LogManager.getLogger(AssignCommand.class);
    private static final String name = "Assign";
    private static final int minimumNumberOfArguments = 2;
    private static final String permissionNode = "reporter.assign";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)AssignPhrases.assignHelp, (Entry<String>)AssignPhrases.assignHelpDetails)}));
    private static final List<String> aliases = Collections.emptyList();

    public AssignCommand(ReporterCommandManager manager) {
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
            Player player = BukkitUtil.getOfflinePlayer(args.get(1)).getPlayer();
            if (this.canAssignReport(sender, index, player)) {
                this.assignReport(sender, index, player);
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, "Failed to assign player!", (Throwable)e);
            sender.sendMessage(this.getErrorMessage());
        }
    }

    private void assignReport(CommandSender sender, int index, Player player) throws ClassNotFoundException, SQLException, InterruptedException {
        String query = "UPDATE Reports SET ClaimStatus=?, ClaimDate=?, ClaimedBy=?, ClaimedByUUID=?, ClaimPriority=? WHERE ID=?";
        ArrayList<String> params = new ArrayList<String>();
        params.add(0, "1");
        params.add(1, Reporter.getDateformat().format(new Date()));
        params.add(2, player.getName());
        params.add(3, player.getUniqueId().toString());
        params.add(4, Integer.toString(this.getServiceModule().getPlayerService().getModLevel((CommandSender)player).getLevel()));
        params.add(5, Integer.toString(index));
        ExtendedDatabaseHandler database = this.getManager().getDatabaseHandler();
        int connectionId = database.openPooledConnection();
        try {
            database.preparedUpdateQuery(connectionId, "UPDATE Reports SET ClaimStatus=?, ClaimDate=?, ClaimedBy=?, ClaimedByUUID=?, ClaimPriority=? WHERE ID=?", params);
        }
        catch (SQLException e) {
            log.error(String.format("Failed to execute assign query on connection [%d]!", connectionId));
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        String playerName = (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(player) + (Object)ChatColor.WHITE;
        String output = this.getManager().getLocale().getString(AssignPhrases.assignSuccessful);
        output = output.replaceAll("%p", playerName);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        sender.sendMessage((Object)ChatColor.WHITE + output);
        playerName = (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(sender) + (Object)ChatColor.WHITE;
        output = this.getManager().getLocale().getString(AssignPhrases.assignedToReport);
        output = output.replaceAll("%p", playerName);
        output = output.replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.WHITE);
        player.sendMessage((Object)ChatColor.BLUE + Reporter.getLogPrefix() + output);
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer senderPlayer = (OfflinePlayer)sender;
            this.getServiceModule().getModStatsService().incrementStat(senderPlayer, ModeratorStatService.ModeratorStat.ASSIGNED);
        }
    }

    public boolean canAssignReport(CommandSender sender, int index, Player player) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean senderIsConsoleOrOp;
        if (player == null) {
            String output = this.getManager().getLocale().getString(AssignPhrases.assignedPlayerMustBeOnline);
            sender.sendMessage((Object)ChatColor.RED + output);
            return false;
        }
        if (!this.getServiceModule().getReportPermissionService().canAlterReport(sender, index, (CommandSender)player)) {
            return false;
        }
        if (BukkitUtil.playersEqual(sender, (CommandSender)player)) {
            String output = this.getManager().getLocale().getString(AssignPhrases.useClaimToAssignSelf);
            sender.sendMessage((Object)ChatColor.RED + output);
            return false;
        }
        ModLevel senderLevel = this.getServiceModule().getPlayerService().getModLevel(sender);
        ModLevel playerLevel = this.getServiceModule().getPlayerService().getModLevel((CommandSender)player);
        boolean senderHasLowerModLevel = senderLevel.getLevel() <= playerLevel.getLevel();
        boolean bl = senderIsConsoleOrOp = sender.isOp() || sender instanceof ConsoleCommandSender;
        if (!senderIsConsoleOrOp && senderHasLowerModLevel) {
            String output = this.getManager().getLocale().getString(AssignPhrases.cannotAssignHigherPriority);
            sender.sendMessage((Object)ChatColor.RED + output);
            output = this.getManager().getLocale().getString(AssignPhrases.playerPriority);
            output = output.replaceAll("%p", (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(player) + (Object)ChatColor.WHITE);
            output = output.replaceAll("%m", (Object)playerLevel.getColor() + playerLevel.getName() + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + output);
            this.getServiceModule().getPlayerService().displayModLevel(sender);
            return false;
        }
        return this.getServiceModule().getReportPermissionService().requirePriority(sender, index, (CommandSender)player);
    }

    @Override
    public List<Usage> getUsages() {
        return usages;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }
}

