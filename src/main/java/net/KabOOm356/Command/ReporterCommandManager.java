/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package net.KabOOm356.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import net.KabOOm356.Command.Command;
import net.KabOOm356.Command.Commands.AssignCommand;
import net.KabOOm356.Command.Commands.ClaimCommand;
import net.KabOOm356.Command.Commands.CompleteCommand;
import net.KabOOm356.Command.Commands.DeleteCommand;
import net.KabOOm356.Command.Commands.DowngradeCommand;
import net.KabOOm356.Command.Commands.ListCommand;
import net.KabOOm356.Command.Commands.MoveCommand;
import net.KabOOm356.Command.Commands.ReportCommand;
import net.KabOOm356.Command.Commands.RequestCommand;
import net.KabOOm356.Command.Commands.RespondCommand;
import net.KabOOm356.Command.Commands.StatisticCommand;
import net.KabOOm356.Command.Commands.UnassignCommand;
import net.KabOOm356.Command.Commands.UnclaimCommand;
import net.KabOOm356.Command.Commands.UpgradeCommand;
import net.KabOOm356.Command.Commands.ViewCommand;
import net.KabOOm356.Command.Help.HelpCommand;
import net.KabOOm356.Command.Help.HelpCommandDisplay;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Entry.LocalePhrases.HelpPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.BukkitUtil;
import net.KabOOm356.Util.FormattingUtil;
import net.KabOOm356.Util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ReporterCommandManager
implements CommandExecutor {
    private static final Logger log = LogManager.getLogger(ReporterCommandManager.class);
    private final Reporter plugin;
    private final HelpCommand reportHelp;
    private final HelpCommand respondHelp;
    private LinkedHashMap<String, ReporterCommand> reportCommands;
    private HashMap<String, String> aliasReportCommands;
    private LinkedHashMap<String, ReporterCommand> respondCommands;
    private HashMap<String, String> aliasRespondCommands;

    public ReporterCommandManager(Reporter plugin) {
        this.plugin = plugin;
        this.initCommands();
        HelpCommandDisplay.Builder reportHelpDisplayBuilder = new HelpCommandDisplay.Builder();
        reportHelpDisplayBuilder.setHeader(HelpPhrases.reportHelpHeader).setAlias(HelpPhrases.reportHelpAliases).setNext(HelpPhrases.nextReportHelpPage).setHint(GeneralPhrases.tryReportHelp);
        HelpCommandDisplay reportHelpDisplay = reportHelpDisplayBuilder.build();
        this.reportHelp = new HelpCommand(this.getLocale(), this.getReportCommands().values(), reportHelpDisplay);
        HelpCommandDisplay.Builder respondHelpDisplayBuilder = new HelpCommandDisplay.Builder();
        respondHelpDisplayBuilder.setHeader(HelpPhrases.respondHelpHeader).setAlias(HelpPhrases.respondHelpAliases).setNext(HelpPhrases.nextRespondHelpPage).setHint(GeneralPhrases.tryRespondHelp);
        HelpCommandDisplay respondHelpDisplay = respondHelpDisplayBuilder.build();
        this.respondHelp = new HelpCommand(this.getLocale(), this.getRespondCommands().values(), respondHelpDisplay);
    }

    private void initCommands() {
        this.reportCommands = new LinkedHashMap(16, 0.75f, false);
        this.aliasReportCommands = new HashMap();
        this.respondCommands = new LinkedHashMap(16, 0.75f, false);
        this.aliasRespondCommands = new HashMap();
        this.initReportCommand(new AssignCommand(this));
        this.initReportCommand(new ClaimCommand(this));
        this.initReportCommand(new CompleteCommand(this));
        this.initReportCommand(new DeleteCommand(this));
        this.initReportCommand(new DowngradeCommand(this));
        this.initReportCommand(new ListCommand(this));
        this.initReportCommand(new MoveCommand(this));
        this.initReportCommand(new ReportCommand(this));
        this.initReportCommand(new RequestCommand(this));
        this.initReportCommand(new StatisticCommand(this));
        this.initReportCommand(new UnassignCommand(this));
        this.initReportCommand(new UnclaimCommand(this));
        this.initReportCommand(new UpgradeCommand(this));
        this.initReportCommand(new ViewCommand(this));
        this.initRespondCommand(new RespondCommand(this));
    }

    private void initReportCommand(ReporterCommand command) {
        this.reportCommands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            this.aliasReportCommands.put(alias, command.getName());
        }
    }

    private void initRespondCommand(ReporterCommand command) {
        this.respondCommands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            this.aliasRespondCommands.put(alias, command.getName());
        }
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args == null || args.length == 0) {
            if (label.equalsIgnoreCase("respond") || label.equalsIgnoreCase("resp") || label.equalsIgnoreCase("rrespond")) {
                sender.sendMessage((Object)ChatColor.RED + this.getLocale().getString(GeneralPhrases.tryRespondHelp));
            } else {
                sender.sendMessage((Object)ChatColor.RED + this.getLocale().getString(GeneralPhrases.tryReportHelp));
            }
            return true;
        }
        if (!Reporter.isCommandSenderSupported(sender)) {
            sender.sendMessage((Object)ChatColor.RED + "Command Sender type is not supported!");
            return true;
        }
        ArrayList<String> arguments = ArrayUtil.arrayToArrayList(args);
        if (label.equalsIgnoreCase("respond") || label.equalsIgnoreCase("resp") || label.equalsIgnoreCase("rrespond")) {
            ReporterCommand command = this.getCommand(RespondCommand.getCommandName());
            if (arguments.size() >= 1 && arguments.get(0).equalsIgnoreCase("help")) {
                int page = 1;
                if (arguments.size() >= 2 && Util.isInteger(arguments.get(1))) {
                    page = Util.parseInt(arguments.get(1));
                }
                this.respondHelp.printHelp(sender, page);
                return true;
            }
            if (arguments.size() >= command.getMinimumNumberOfArguments()) {
                try {
                    Command commandToRun = command.getRunnableClone(sender, arguments);
                    Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)commandToRun);
                    return true;
                }
                catch (Exception e) {
                    log.error("Failed to run Respond command!", (Throwable)e);
                }
            } else {
                sender.sendMessage((Object)ChatColor.RED + command.getUsage());
            }
            sender.sendMessage((Object)ChatColor.RED + this.getLocale().getString(GeneralPhrases.tryRespondHelp));
            return true;
        }
        if (label.equalsIgnoreCase("report") || label.equalsIgnoreCase("rreport") || label.equalsIgnoreCase("rep")) {
            String subcommand = arguments.remove(0);
            ReporterCommand command = this.getCommand(FormattingUtil.capitalizeFirstCharacter(subcommand));
            if (subcommand.equalsIgnoreCase("help")) {
                int page = 1;
                if (arguments.size() >= 1 && Util.isInteger(arguments.get(0))) {
                    page = Util.parseInt(arguments.get(0));
                }
                this.reportHelp.printHelp(sender, page);
                return true;
            }
            if (command != null) {
                if (arguments.size() >= command.getMinimumNumberOfArguments()) {
                    try {
                        Command commandToRun = command.getRunnableClone(sender, arguments);
                        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)commandToRun);
                        return true;
                    }
                    catch (Exception e) {
                        log.error("Failed to run Report command!", (Throwable)e);
                    }
                } else {
                    sender.sendMessage((Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(command.getUsage()));
                }
            } else {
                command = this.getCommand(ReportCommand.getCommandName());
                arguments.add(0, subcommand);
                if (arguments.size() >= command.getMinimumNumberOfArguments()) {
                    try {
                        Command commandToRun = command.getRunnableClone(sender, arguments);
                        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)commandToRun);
                        return true;
                    }
                    catch (Exception e) {
                        log.error("Failed to Report!", (Throwable)e);
                    }
                } else {
                    sender.sendMessage((Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(command.getUsage()));
                }
            }
            sender.sendMessage((Object)ChatColor.RED + this.getLocale().getString(GeneralPhrases.tryReportHelp));
        }
        return true;
    }

    public final OfflinePlayer getPlayer(String playerName) {
        return BukkitUtil.getPlayer(playerName, this.getConfig().getBoolean("general.matchPartialOfflineUsernames", true));
    }

    protected Reporter getPlugin() {
        return this.plugin;
    }

    public ExtendedDatabaseHandler getDatabaseHandler() {
        return this.plugin.getDatabaseHandler();
    }

    public Locale getLocale() {
        return this.plugin.getLocale();
    }

    public HashMap<String, ReporterCommand> getReportCommands() {
        return this.reportCommands;
    }

    public HashMap<String, String> getAliasReportCommands() {
        return this.aliasReportCommands;
    }

    public HashMap<String, ReporterCommand> getRespondCommands() {
        return this.respondCommands;
    }

    public HashMap<String, String> getAliasRespondCommands() {
        return this.aliasRespondCommands;
    }

    public ReporterCommand getCommand(String commandName) {
        ReporterCommand command = null;
        if (this.reportCommands.containsKey(commandName)) {
            command = this.reportCommands.get(commandName);
        } else if (this.respondCommands.containsKey(commandName)) {
            command = this.respondCommands.get(commandName);
        } else if (this.aliasReportCommands.containsKey(commandName)) {
            command = this.reportCommands.get(this.aliasReportCommands.get(commandName));
        } else if (this.aliasRespondCommands.containsKey(commandName)) {
            command = this.respondCommands.get(this.aliasRespondCommands.get(commandName));
        }
        return command;
    }

    public FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }

    public ServiceModule getServiceModule() {
        return this.getPlugin().getServiceModule();
    }
}

