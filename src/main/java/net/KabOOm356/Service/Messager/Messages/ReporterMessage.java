/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.KabOOm356.Service.Messager.Messages;

import java.util.ArrayList;
import java.util.Iterator;
import net.KabOOm356.Service.Messager.Messages.Message;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.FormattingUtil;
import org.bukkit.ChatColor;

public class ReporterMessage
extends Message {
    private final ArrayList<Integer> indexes = new ArrayList();

    public ReporterMessage(String message) {
        super(message);
    }

    public ReporterMessage(Message message) {
        super(message.getMessage());
    }

    public ReporterMessage(String message, int index) {
        super(message);
        this.indexes.add(index);
    }

    public String getRawMessage() {
        return super.getMessage();
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        String indexString = ArrayUtil.indexesToString(this.indexes, ChatColor.GOLD, ChatColor.WHITE);
        message = message.replaceAll("%i", indexString);
        return message;
    }

    public ArrayList<Integer> getIndexes() {
        return this.indexes;
    }

    public void addIndexes(ReporterMessage message) {
        if (this.messagesEqual(message)) {
            this.addIndexes(message.getIndexes());
        }
    }

    public void addIndex(int index) {
        if (!this.indexes.contains(index)) {
            this.indexes.add(index);
        }
    }

    public void addIndexes(ArrayList<Integer> indexes) {
        Iterator<Integer> iterator = indexes.iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            this.addIndex(index);
        }
    }

    public void removeIndex(int index) {
        int LCV = 0;
        while (LCV < this.indexes.size()) {
            if (this.indexes.get(LCV) == index) {
                this.indexes.remove(LCV);
                continue;
            }
            if (this.indexes.get(LCV) > index) {
                this.indexes.set(LCV, this.indexes.get(LCV) - 1);
                ++LCV;
                continue;
            }
            ++LCV;
        }
    }

    public void reindex(ArrayList<Integer> remainingIndexes) {
        int LCV = 0;
        while (LCV < this.indexes.size()) {
            if (remainingIndexes.contains(this.indexes.get(LCV))) {
                this.indexes.set(LCV, remainingIndexes.indexOf(this.indexes.get(LCV)) + 1);
                ++LCV;
                continue;
            }
            this.indexes.remove(LCV);
        }
    }

    public boolean messagesEqual(ReporterMessage message) {
        return this.getRawMessage().equalsIgnoreCase(message.getRawMessage());
    }

    @Override
    public boolean isEmpty() {
        return this.indexes.isEmpty();
    }

    @Override
    public String toString() {
        String sb = super.toString() + "\nIndexes: " + ArrayUtil.indexesToString(this.indexes) + '\n' + "Full Message: " + this.getMessage();
        return FormattingUtil.addTabsToNewLines(sb, 1);
    }
}

