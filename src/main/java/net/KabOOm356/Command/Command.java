/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.GeneralPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Runnable.RunnableWithState;
import net.KabOOm356.Runnable.TimedRunnable;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Throwable.IndexNotANumberException;
import net.KabOOm356.Throwable.IndexOutOfRangeException;
import net.KabOOm356.Throwable.NoLastViewedReportException;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command
extends TimedRunnable
implements RunnableWithState {
    private static final Logger log = LogManager.getLogger(Command.class);
    private final ReporterCommandManager manager;
    private final String name;
    private final String permissionNode;
    private final int minimumNumberOfArguments;
    private boolean isRunning = false;
    private boolean isPendingToRun = false;
    private boolean hasRun = false;
    private CommandSender sender = null;
    private ArrayList<String> arguments = null;

    protected Command(ReporterCommandManager manager, String commandName, String commandPermissionNode, int minimumNumberOfArguments) {
        this.manager = manager;
        this.name = commandName;
        this.permissionNode = commandPermissionNode;
        this.minimumNumberOfArguments = minimumNumberOfArguments;
    }

    public abstract void execute(CommandSender var1, ArrayList<String> var2) throws NoLastViewedReportException, IndexOutOfRangeException, IndexNotANumberException;

    public boolean hasPermission(Player player) {
        return this.hasPermission(player, this.permissionNode);
    }

    public boolean hasPermission(Player player, String perm) {
        return this.manager.getServiceModule().getPermissionService().hasPermission(player, perm);
    }

    public boolean hasPermission(CommandSender sender) {
        Player player;
        if (BukkitUtil.isPlayer(sender) && !this.hasPermission(player = (Player)sender)) {
            return false;
        }
        return true;
    }

    public boolean hasRequiredPermission(CommandSender sender) {
        if (!this.hasPermission(sender)) {
            sender.sendMessage(this.getFailedPermissionsMessage());
            return false;
        }
        return true;
    }

    protected ReporterCommandManager getManager() {
        return this.manager;
    }

    protected ServiceModule getServiceModule() {
        return this.getManager().getServiceModule();
    }

    public String getName() {
        return this.name;
    }

    public String getPermissionNode() {
        return this.permissionNode;
    }

    public abstract List<Usage> getUsages();

    public abstract List<String> getAliases();

    public String getUsage() {
        return this.getManager().getLocale().getString((Entry)this.getUsages().get(0).getKey());
    }

    public String getDescription() {
        return this.getManager().getLocale().getString((Entry)this.getUsages().get(0).getValue());
    }

    public String getErrorMessage() {
        return (Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + this.manager.getLocale().getString(GeneralPhrases.error);
    }

    public String getFailedPermissionsMessage() {
        return (Object)ChatColor.RED + BukkitUtil.colorCodeReplaceAll(this.manager.getLocale().getString(GeneralPhrases.failedPermissions));
    }

    public int getMinimumNumberOfArguments() {
        return this.minimumNumberOfArguments;
    }

    public void setSender(CommandSender sender) {
        if (this.isRunning || this.isPendingToRun) {
            throw new IllegalArgumentException("The current command is in-flight and should not be modified!");
        }
        this.sender = sender;
    }

    public void setArguments(ArrayList<String> arguments) {
        if (this.isRunning || this.isPendingToRun) {
            throw new IllegalArgumentException("The current command is in-flight and should not be modified!");
        }
        this.arguments = arguments;
    }

    public Command getRunnableClone(CommandSender sender, ArrayList<String> arguments) throws Exception {
        try {
            Class<?> clazz = this.getClass();
            Constructor<?> constructor = clazz.getDeclaredConstructor(ReporterCommandManager.class);
            Command command = (Command)constructor.newInstance(this.manager);
            command.setSender(sender);
            command.setArguments(arguments);
            command.isPendingToRun = true;
            return command;
        }
        catch (Exception e) {
            log.warn(String.format("Failed to clone command [%s]!", this.getClass().getName()));
            throw e;
        }
    }

    @Override
    public void run() {
        Validate.notNull((Object)this.sender);
        Validate.notNull(this.arguments);
        try {
            this.start();
            this.isRunning = true;
            this.execute(this.sender, this.arguments);
        }
        catch (NoLastViewedReportException e) {
            String message = this.getManager().getLocale().getString(GeneralPhrases.noLastReport);
            this.sender.sendMessage(message);
        }
        catch (IndexNotANumberException e) {
            String message = this.getManager().getLocale().getString(GeneralPhrases.indexInt);
            this.sender.sendMessage(message);
        }
        catch (IndexOutOfRangeException e) {
            String message = this.getManager().getLocale().getString(GeneralPhrases.indexRange);
            this.sender.sendMessage(message);
        }
        finally {
            this.isRunning = false;
            this.isPendingToRun = false;
            this.hasRun = true;
            this.end();
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public boolean isPendingToRun() {
        return this.isPendingToRun;
    }

    @Override
    public boolean isStopped() {
        return !this.isRunning;
    }

    @Override
    public boolean hasRun() {
        return this.hasRun;
    }

    public String toString() {
        return "Command Name: " + this.name + '\n' + "Permission Node: " + this.permissionNode + '\n' + "Minimum Number of Arguments: " + this.minimumNumberOfArguments;
    }
}

