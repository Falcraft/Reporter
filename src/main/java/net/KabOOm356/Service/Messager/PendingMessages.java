/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.Messager;

import java.util.ArrayList;
import java.util.Collection;
import net.KabOOm356.Service.Messager.Messages.Message;
import net.KabOOm356.Service.Messager.Messages.ReporterMessage;

public class PendingMessages
extends ArrayList<Message> {
    private static final long serialVersionUID = 8224514226473615637L;

    public PendingMessages() {
    }

    public PendingMessages(Message message) {
        this.add(message);
    }

    public PendingMessages(ArrayList<Message> messages) {
        this.addAll(messages);
    }

    @Override
    public boolean add(Message message) {
        if (message instanceof ReporterMessage) {
            ReporterMessage reporterMessage = (ReporterMessage)message;
            for (Message pendingMessage : this) {
                ReporterMessage reporterPendingMessage;
                if (!(pendingMessage instanceof ReporterMessage) || !reporterMessage.messagesEqual(reporterPendingMessage = (ReporterMessage)pendingMessage)) continue;
                reporterPendingMessage.addIndexes(reporterMessage.getIndexes());
                return true;
            }
            return super.add(reporterMessage);
        }
        return super.add(message);
    }

    public void reindexMessages(ArrayList<Integer> remainingIndexes) {
        for (Message message : this) {
            if (!(message instanceof ReporterMessage)) continue;
            ReporterMessage reporterMessage = (ReporterMessage)message;
            reporterMessage.reindex(remainingIndexes);
        }
        this.removeEmpty();
    }

    public void removeIndex(int index) {
        for (Message message : this) {
            if (!(message instanceof ReporterMessage)) continue;
            ((ReporterMessage)message).removeIndex(index);
        }
        this.removeEmpty();
    }

    @Override
    public boolean remove(Object obj) {
        boolean removed = false;
        do {
            boolean bl = removed = removed || super.remove(obj);
        } while (this.contains(obj));
        this.removeEmpty();
        return removed;
    }

    private void removeEmpty() {
        ArrayList<Message> deletion = new ArrayList<Message>();
        for (Message message : this) {
            if (!message.isEmpty()) continue;
            deletion.add(message);
        }
        for (Message message : deletion) {
            this.remove(message);
        }
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            for (Message message : this) {
                if (message.isEmpty()) continue;
                return false;
            }
        }
        return true;
    }
}

