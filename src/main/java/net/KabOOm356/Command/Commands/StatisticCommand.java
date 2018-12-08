/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Command.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ResultRow;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.StatisticPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.SQLStatService;
import net.KabOOm356.Service.SQLStatServices.ModeratorStatService;
import net.KabOOm356.Service.SQLStatServices.PlayerStatService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class StatisticCommand
extends ReporterCommand {
    private static final String name = "Statistic";
    private static final int minimumNumberOfArguments = 1;
    private static final String permissionNode = "reporter.statistic.*";
    private static final List<Usage> usages = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new Usage[]{new Usage((Entry<String>)StatisticPhrases.statisticHelp, (Entry<String>)StatisticPhrases.statisticHelpDetails), new Usage("/report statistic/stat list", (Entry<String>)StatisticPhrases.statisticListHelpDetails), new Usage("/report statistic/stat <Player Name> all", (Entry<String>)StatisticPhrases.statisticAllHelpDetails), new Usage("/report statistic/stat <Player Name> all mod|player", (Entry<String>)StatisticPhrases.statisticAllModPlayerHelpDetails)}));
    private static final List<String> aliases = Collections.unmodifiableList(ArrayUtil.arrayToArrayList(new String[]{"Stat"}));

    public StatisticCommand(ReporterCommandManager manager) {
        super(manager, name, permissionNode, 1);
    }

    private static String getStatisticNameString(ArrayList<SQLStatService.SQLStat> stats) {
        StringBuilder stb = new StringBuilder();
        for (int LCV = 0; LCV < stats.size(); ++LCV) {
            SQLStatService.SQLStat stat = stats.get(LCV);
            stb.append((Object)ChatColor.GOLD).append(stat.getName());
            if (LCV == stats.size() - 1) continue;
            stb.append((Object)ChatColor.WHITE).append(", ");
        }
        return stb.toString();
    }

    private static String getStatisticPermission(SQLStatService.SQLStat statistic) {
        String permission = "reporter.statistic.read.*";
        if (statistic instanceof ModeratorStatService.ModeratorStat) {
            permission = "reporter.statistic.read.mod";
        } else if (statistic instanceof PlayerStatService.PlayerStat) {
            permission = "reporter.statistic.read.player";
        }
        return permission;
    }

    @Override
    public void execute(CommandSender sender, ArrayList<String> args) {
        if (args.get(0).equalsIgnoreCase("list")) {
            if (this.getServiceModule().getPermissionService().hasPermission(sender, "reporter.statistic.list")) {
                this.listStatistics(sender);
            } else {
                sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(GeneralPhrases.failedPermissions));
            }
        } else if (args.size() >= 2) {
            SQLStatService.SQLStat statistic = SQLStatService.SQLStat.getByName(args.get(1));
            if (statistic == null) {
                String message = this.getManager().getLocale().getString(StatisticPhrases.notValidStatistic);
                sender.sendMessage((Object)ChatColor.RED + message);
                message = this.getManager().getLocale().getString(StatisticPhrases.tryStatisticList);
                sender.sendMessage((Object)ChatColor.GOLD + message);
                return;
            }
            OfflinePlayer player = this.getManager().getPlayer(args.get(0));
            if (player == null) {
                String message = this.getManager().getLocale().getString(GeneralPhrases.playerDoesNotExist);
                sender.sendMessage((Object)ChatColor.RED + message);
                return;
            }
            if (statistic == SQLStatService.SQLStat.ALL) {
                if (args.size() >= 3) {
                    if (args.get(2).equalsIgnoreCase("mod")) {
                        if (this.getServiceModule().getPermissionService().hasPermission(sender, "reporter.statistic.read.mod")) {
                            this.displayAllModStatistics(sender, player);
                        } else {
                            sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(GeneralPhrases.failedPermissions));
                        }
                    } else if (args.get(2).equalsIgnoreCase("player")) {
                        if (this.getServiceModule().getPermissionService().hasPermission(sender, "reporter.statistic.read.player")) {
                            this.displayAllPlayerStatistics(sender, player);
                        } else {
                            sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(GeneralPhrases.failedPermissions));
                        }
                    } else {
                        this.displayAllStatistics(sender, player);
                    }
                } else {
                    this.displayAllStatistics(sender, player);
                }
            } else {
                String permission = StatisticCommand.getStatisticPermission(statistic);
                if (!this.getServiceModule().getPermissionService().hasPermission(sender, permission)) {
                    sender.sendMessage((Object)ChatColor.RED + this.getManager().getLocale().getString(GeneralPhrases.failedPermissions));
                    return;
                }
                this.displayStatistic(sender, player, statistic);
            }
        } else {
            sender.sendMessage((Object)ChatColor.RED + this.getUsage());
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

    private void displayStatistic(CommandSender sender, OfflinePlayer player, SQLStatService.SQLStat statistic) {
        ResultRow result = this.getStatistic(player, statistic);
        if (result.isEmpty() || result.getString(statistic.getColumnName()).isEmpty()) {
            String message = this.getManager().getLocale().getString(StatisticPhrases.noStatisticEntry);
            message = message.replaceAll("%s", (Object)ChatColor.GREEN + statistic.getName() + (Object)ChatColor.WHITE);
            message = message.replaceAll("%p", (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(player) + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + message);
        } else {
            String message = this.getManager().getLocale().getString(StatisticPhrases.displayStatistic);
            message = message.replaceAll("%s", (Object)ChatColor.GREEN + statistic.getName() + (Object)ChatColor.WHITE);
            message = message.replaceAll("%p", (Object)ChatColor.BLUE + BukkitUtil.formatPlayerName(player) + (Object)ChatColor.WHITE);
            message = message.replaceAll("%v", (Object)ChatColor.GOLD + result.getString(statistic.getColumnName()) + (Object)ChatColor.WHITE);
            sender.sendMessage((Object)ChatColor.WHITE + message);
        }
    }

    private void displayAllStatistics(CommandSender sender, OfflinePlayer player) {
        this.displayAllModStatistics(sender, player);
        this.displayAllPlayerStatistics(sender, player);
    }

    private void displayAllModStatistics(CommandSender sender, OfflinePlayer player) {
        if (!this.getServiceModule().getPermissionService().hasPermission(sender, "reporter.statistic.read.mod")) {
            return;
        }
        ArrayList<SQLStatService.SQLStat> stats = SQLStatService.SQLStat.getAll(ModeratorStatService.ModeratorStat.class);
        for (SQLStatService.SQLStat stat : stats) {
            this.displayStatistic(sender, player, stat);
        }
    }

    private void displayAllPlayerStatistics(CommandSender sender, OfflinePlayer player) {
        if (!this.getServiceModule().getPermissionService().hasPermission(sender, "reporter.statistic.read.player")) {
            return;
        }
        ArrayList<SQLStatService.SQLStat> stats = SQLStatService.SQLStat.getAll(PlayerStatService.PlayerStat.class);
        for (SQLStatService.SQLStat stat : stats) {
            this.displayStatistic(sender, player, stat);
        }
    }

    private void listStatistics(CommandSender sender) {
        String playerStatsList = (Object)ChatColor.WHITE + this.getManager().getLocale().getString(StatisticPhrases.availablePlayerStatistics);
        String modStatsList = (Object)ChatColor.WHITE + this.getManager().getLocale().getString(StatisticPhrases.availableModeratorStatistics);
        ArrayList<SQLStatService.SQLStat> stats = SQLStatService.SQLStat.getAll(PlayerStatService.PlayerStat.class);
        playerStatsList = playerStatsList.replaceAll("%s", StatisticCommand.getStatisticNameString(stats));
        stats = SQLStatService.SQLStat.getAll(ModeratorStatService.ModeratorStat.class);
        modStatsList = modStatsList.replaceAll("%s", StatisticCommand.getStatisticNameString(stats));
        sender.sendMessage(playerStatsList);
        sender.sendMessage(modStatsList);
    }

    private ResultRow getStatistic(OfflinePlayer player, SQLStatService.SQLStat statistic) {
        if (statistic instanceof ModeratorStatService.ModeratorStat) {
            return this.getServiceModule().getModStatsService().getStat(player, statistic);
        }
        if (statistic instanceof PlayerStatService.PlayerStat) {
            return this.getServiceModule().getPlayerStatsService().getStat(player, statistic);
        }
        return null;
    }
}

