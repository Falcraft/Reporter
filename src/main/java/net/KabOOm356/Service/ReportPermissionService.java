/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 */
package net.KabOOm356.Service;

import java.sql.SQLException;
import java.util.UUID;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.ClaimPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.PlayerService;
import net.KabOOm356.Service.ReportInformationService;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ReportPermissionService
extends Service {
    private static final Logger log = LogManager.getLogger(ReportPermissionService.class);

    protected ReportPermissionService(ServiceModule module) {
        super(module);
    }

    public boolean canAlterReport(CommandSender sender, int index, CommandSender player) throws InterruptedException, SQLException, ClassNotFoundException {
        if (player == null) {
            return false;
        }
        try {
            if (!this.requirePriority(sender, index, player)) {
                return false;
            }
            if (!this.requireUnclaimedOrPriority(sender, index, player)) {
                sender.sendMessage((Object)ChatColor.WHITE + this.getLocale().getString(GeneralPhrases.contactToAlter));
                return false;
            }
            return true;
        }
        catch (InterruptedException e) {
            log.error(String.format("Failed to check if player [%s] could alter report [%d]!", BukkitUtil.formatPlayerName(player), index));
            throw e;
        }
        catch (SQLException e) {
            log.error(String.format("Failed to check if player [%s] could alter report [%d]!", BukkitUtil.formatPlayerName(player), index));
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.error(String.format("Failed to check if player [%s] could alter report [%d]!", BukkitUtil.formatPlayerName(player), index));
            throw e;
        }
    }

    public boolean canAlterReport(CommandSender sender, int index) throws InterruptedException, SQLException, ClassNotFoundException {
        return this.canAlterReport(sender, index, sender);
    }

    public boolean requireUnclaimedOrPriority(CommandSender sender, int index, CommandSender player) throws ClassNotFoundException, InterruptedException, SQLException {
        if (this.hasPermissionOverride(sender)) {
            return true;
        }
        String query = "SELECT ClaimStatus, ClaimedByUUID, ClaimedBy, ClaimPriority FROM Reports WHERE ID=" + index;
        ExtendedDatabaseHandler database = this.getDatabase();
        Integer connectionId = null;
        try {
            connectionId = database.openPooledConnection();
            SQLResultSet result = database.sqlQuery(connectionId, query);
            boolean isClaimed = result.getBoolean("ClaimStatus");
            String claimedByName = result.getString("ClaimedBy");
            int claimPriority = result.getInt("ClaimPriority");
            String claimedByUUIDString = result.getString("ClaimedByUUID");
            UUID claimedByUUID = null;
            if (!claimedByUUIDString.isEmpty()) {
                claimedByUUID = UUID.fromString(claimedByUUIDString);
            }
            OfflinePlayer claimedByOfflinePlayer = BukkitUtil.getOfflinePlayer(claimedByUUID, claimedByName);
            CommandSender claimedBy = CommandSender.class.cast((Object)claimedByOfflinePlayer);
            boolean isClaimedBySender = BukkitUtil.playersEqual(sender, claimedBy);
            boolean isClaimedByPlayer = BukkitUtil.playersEqual(player, claimedBy);
            if (isClaimed && !isClaimedBySender && !isClaimedByPlayer && claimPriority >= this.getModLevel(player).getLevel()) {
                String formattedClaimName = BukkitUtil.formatPlayerName(claimedBy);
                String output = this.getLocale().getString(ClaimPhrases.reportAlreadyClaimed).replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED).replaceAll("%c", (Object)ChatColor.BLUE + formattedClaimName + (Object)ChatColor.RED);
                sender.sendMessage((Object)ChatColor.RED + output);
                boolean bl = false;
                return bl;
            }
        }
        catch (ClassNotFoundException e) {
            log.error("Failed to check if report can be altered by player!");
            throw e;
        }
        catch (InterruptedException e) {
            log.error("Failed to check if report can be altered by player!");
            throw e;
        }
        catch (SQLException e) {
            log.error("Failed to check if report can be altered by player!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
        return true;
    }

    public boolean checkPriority(CommandSender sender, int index) throws ClassNotFoundException, InterruptedException, SQLException {
        if (this.hasPermissionOverride(sender)) {
            return true;
        }
        ModLevel modLevel = this.getModLevel(sender);
        try {
            ModLevel reportPriority = this.getReportPriority(index);
            return reportPriority.getLevel() <= modLevel.getLevel();
        }
        catch (ClassNotFoundException e) {
            log.error(String.format("Failed to do a priority check for player [%s] on report [%d]", BukkitUtil.formatPlayerName(sender), index));
            throw e;
        }
        catch (InterruptedException e) {
            log.error(String.format("Failed to do a priority check for player [%s] on report [%d]", BukkitUtil.formatPlayerName(sender), index));
            throw e;
        }
        catch (SQLException e) {
            log.error(String.format("Failed to do a priority check for player [%s] on report [%d]", BukkitUtil.formatPlayerName(sender), index));
            throw e;
        }
    }

    public boolean requirePriority(CommandSender sender, int index, CommandSender player) throws InterruptedException, SQLException, ClassNotFoundException {
        try {
            if (!this.checkPriority(player, index)) {
                ModLevel reportPriority = this.getReportPriority(index);
                String output = this.getLocale().getString(GeneralPhrases.reportRequiresClearance).replaceAll("%i", (Object)ChatColor.GOLD + Integer.toString(index) + (Object)ChatColor.RED).replaceAll("%m", (Object)reportPriority.getColor() + reportPriority.getName() + (Object)ChatColor.RED);
                sender.sendMessage((Object)ChatColor.RED + output);
                if (BukkitUtil.playersEqual(sender, player)) {
                    this.displayModLevel(sender);
                } else {
                    this.displayModLevel(sender, player);
                }
                return false;
            }
        }
        catch (InterruptedException e) {
            log.error(String.format("Failed to check required priority for report [%d]!", index));
            throw e;
        }
        catch (SQLException e) {
            log.error(String.format("Failed to check required priority for report [%d]!", index));
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.error(String.format("Failed to check required priority for report [%d]!", index));
            throw e;
        }
        return true;
    }

    private boolean hasPermissionOverride(CommandSender sender) {
        return sender.isOp() || sender instanceof ConsoleCommandSender;
    }

    private ExtendedDatabaseHandler getDatabase() {
        return this.getStore().getDatabaseStore().get();
    }

    private ModLevel getModLevel(CommandSender player) {
        return this.getModule().getPlayerService().getModLevel(player);
    }

    private void displayModLevel(CommandSender sender) {
        this.getModule().getPlayerService().displayModLevel(sender);
    }

    private void displayModLevel(CommandSender sender, CommandSender player) {
        this.getModule().getPlayerService().displayModLevel(sender, player);
    }

    private Locale getLocale() {
        return this.getStore().getLocaleStore().get();
    }

    private ModLevel getReportPriority(int index) throws InterruptedException, SQLException, ClassNotFoundException {
        return this.getModule().getReportInformationService().getReportPriority(index);
    }
}

