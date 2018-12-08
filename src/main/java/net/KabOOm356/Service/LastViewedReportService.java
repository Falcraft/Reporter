/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Service.Store.type.LastViewed;
import net.KabOOm356.Throwable.NoLastViewedReportException;
import net.KabOOm356.Util.BukkitUtil;
import net.KabOOm356.Util.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

public class LastViewedReportService
extends Service {
    public static final int noLastViewedIndex = -1;
    public static final String lastViewedIndex = "last";

    protected LastViewedReportService(ServiceModule module) {
        super(module);
    }

    private static int getIndex(String index) {
        Integer res = Util.parseInt(index);
        return res == null ? 0 : res;
    }

    public int getLastViewed(CommandSender sender) throws NoLastViewedReportException {
        Validate.notNull((Object)sender);
        if (!this.hasLastViewed(sender)) {
            throw new NoLastViewedReportException(String.format("Sender [%s] does not have a last viewed report!", BukkitUtil.formatPlayerName(sender)));
        }
        return this.getLastViewed().get((Object)sender);
    }

    public boolean hasLastViewed(CommandSender sender) {
        return this.getLastViewed().containsKey((Object)sender) && this.getLastViewed().get((Object)sender) != -1;
    }

    public int getIndexOrLastViewedReport(CommandSender sender, String index) throws NoLastViewedReportException {
        Validate.notNull((Object)sender);
        Validate.notNull((Object)index);
        Validate.notEmpty((String)index);
        if (lastViewedIndex.equalsIgnoreCase(index)) {
            return this.getLastViewed(sender);
        }
        return LastViewedReportService.getIndex(index);
    }

    public void playerViewed(CommandSender sender, int index) {
        this.getLastViewed().put(sender, index);
    }

    public void removeLastViewedReport(CommandSender sender) {
        this.getLastViewed().remove((Object)sender);
    }

    public void deleteIndex(int index) {
        for (Map.Entry<CommandSender, Integer> e : this.getLastViewed().entrySet()) {
            if (e.getValue() == index) {
                e.setValue(-1);
                continue;
            }
            if (e.getValue() <= index) continue;
            e.setValue(e.getValue() - 1);
        }
    }

    public void deleteBatch(List<Integer> remainingIndexes) {
        for (Map.Entry<CommandSender, Integer> e : this.getLastViewed().entrySet()) {
            if (remainingIndexes.contains(e.getValue())) {
                e.setValue(remainingIndexes.indexOf(e.getValue()) + 1);
                continue;
            }
            e.setValue(-1);
        }
    }

    private Map<CommandSender, Integer> getLastViewed() {
        return this.getStore().getLastViewedStore().get();
    }
}

