/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Command.Help;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.KabOOm356.Command.Help.HelpCommandDisplay;
import net.KabOOm356.Command.Help.Usage;
import net.KabOOm356.Command.ReporterCommand;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Locale.Entry.LocalePhrase;
import net.KabOOm356.Locale.Entry.LocalePhrases.HelpPhrases;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class HelpCommand {
    private static final float commandsPerPage = 5.0f;
    private static final String format = (Object)ChatColor.BLUE + Reporter.getLogPrefix() + (Object)ChatColor.RED + "%usage" + (Object)ChatColor.WHITE + " - %description";
    private final Locale locale;
    private final ArrayList<ArrayList<Usage>> pages = new ArrayList();
    private final HelpCommandDisplay display;

    public HelpCommand(Locale locale, Collection<ReporterCommand> commands, HelpCommandDisplay display) {
        Validate.notNull((Object)locale);
        Validate.notNull(commands);
        Validate.notNull((Object)display);
        this.locale = locale;
        this.display = display;
        this.createPages(commands);
    }

    private static ArrayList<Usage> createPage(ArrayList<Usage> help, int pageNumber) {
        ArrayList<Usage> page = new ArrayList<Usage>();
        int startIndex = HelpCommand.getPageStartIndex(pageNumber);
        int endIndex = HelpCommand.getPageEndIndex(pageNumber, help.size());
        for (int index = startIndex; index < endIndex; ++index) {
            page.add(help.get(index));
        }
        return page;
    }

    private static int getPageStartIndex(int page) {
        Float pageStartIndex = Float.valueOf((float)page * 5.0f);
        return pageStartIndex.intValue();
    }

    private static int getPageEndIndex(int page, int total) {
        Float endIndex = Float.valueOf((float)HelpCommand.getPageStartIndex(page) + 5.0f);
        return endIndex.floatValue() > (float)total ? total : endIndex.intValue();
    }

    private static int getPageIndex(int page) {
        return page - 1;
    }

    private static int calculateNumberOfPages(int numberOfHelpMessages) {
        double numberOfPages = (float)numberOfHelpMessages / 5.0f;
        Double roundedNumberOfPages = Math.ceil(numberOfPages);
        return roundedNumberOfPages.intValue();
    }

    public void printHelp(CommandSender sender, int page) {
        if (this.requireValidHelpPage(sender, page)) {
            this.printHeader(sender, page);
            this.printPage(sender, page);
            this.printFooter(sender, page);
        }
    }

    public int getNumberOfHelpPages() {
        return this.pages.size();
    }

    private void createPages(Collection<ReporterCommand> commands) {
        ArrayList<Usage> help = this.getHelp(commands);
        int pageCount = HelpCommand.calculateNumberOfPages(help.size());
        for (int page = 0; page < pageCount; ++page) {
            this.pages.add(HelpCommand.createPage(help, page));
        }
    }

    private ArrayList<Usage> getHelp(Collection<ReporterCommand> commands) {
        ArrayList<Usage> help = new ArrayList<Usage>();
        for (ReporterCommand command : commands) {
            help.addAll(command.getUsages());
        }
        return help;
    }

    private void printFooter(CommandSender sender, int page) {
        if (page != this.pages.size()) {
            String nextPage = this.getLocale().getString(this.display.getNext()).replaceAll("%p", Integer.toString(page + 1));
            sender.sendMessage((Object)ChatColor.GOLD + nextPage);
        } else {
            String aliases = this.getLocale().getString(this.display.getAlias());
            sender.sendMessage((Object)ChatColor.BLUE + aliases);
        }
    }

    private void printPage(CommandSender sender, int page) {
        int pageIndex = HelpCommand.getPageIndex(page);
        ArrayList<Usage> currentPage = this.pages.get(pageIndex);
        for (Usage usage : currentPage) {
            String usageString = this.getLocale().getString((Entry)usage.getKey());
            String description = this.getLocale().getString((Entry)usage.getValue());
            sender.sendMessage(format.replaceAll("%usage", usageString).replaceAll("%description", description));
        }
    }

    private boolean requireValidHelpPage(CommandSender sender, int page) {
        return this.requireValidHelpPageMinimum(sender, page) && this.requireValidHelpPageMaximum(sender, page);
    }

    private boolean requireValidHelpPageMaximum(CommandSender sender, int page) {
        if (page > this.pages.size()) {
            String line = this.getLocale().getString(HelpPhrases.numberOfHelpPages).replaceAll("%p", Integer.toString(this.pages.size()));
            sender.sendMessage((Object)ChatColor.RED + line);
            return false;
        }
        return true;
    }

    private boolean requireValidHelpPageMinimum(CommandSender sender, int page) {
        if (page <= 0) {
            String line = this.getLocale().getString(HelpPhrases.pageNumberOutOfRange);
            sender.sendMessage((Object)ChatColor.RED + line);
            line = this.getLocale().getString(this.display.getHint());
            sender.sendMessage((Object)ChatColor.RED + line);
            return false;
        }
        return true;
    }

    private void printHeader(CommandSender sender, int page) {
        String header = this.getLocale().getString(this.display.getHeader()).replaceAll("%p", Integer.toString(page)).replaceAll("%c", (Object)ChatColor.GOLD + Integer.toString(this.pages.size()));
        sender.sendMessage((Object)ChatColor.GREEN + header);
    }

    private Locale getLocale() {
        return this.locale;
    }
}

