/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.KabOOm356.Service.Messager.Group;
import net.KabOOm356.Service.Messager.GroupMessages;
import net.KabOOm356.Service.Messager.Messages.Message;
import net.KabOOm356.Service.Messager.Messages.ReporterMessage;
import net.KabOOm356.Service.Messager.Messages.SimpleMessage;
import net.KabOOm356.Service.Messager.PendingMessages;
import net.KabOOm356.Service.Messager.PlayerMessages;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Util.FormattingUtil;

public class PlayerMessageService
extends Service {
    protected PlayerMessageService(ServiceModule module) {
        super(module);
    }

    public void addMessage(String player, String message) {
        this.getMessages().put(player, Group.DEFAULT, new SimpleMessage(message));
    }

    public void addMessage(String player, String message, int index) {
        this.addMessage(player, Group.DEFAULT, message, index);
    }

    public void addMessage(String player, Group group, String message, int index) {
        this.getMessages().put(player, group, new ReporterMessage(message, index));
    }

    public void reindexMessages(ArrayList<Integer> remainingIndexes) {
        this.getMessages().reindexMessages(remainingIndexes);
    }

    public boolean hasMessages(String player) {
        return this.getMessages().containsKey(player);
    }

    public boolean hasGroup(String player, Group group) {
        return this.hasMessages(player) && ((GroupMessages)this.getMessages().get(player)).containsKey(group);
    }

    public void removeMessage(int index) {
        this.getMessages().removeIndex(index);
    }

    public void removeAll() {
        this.getMessages().clear();
    }

    public ArrayList<String> getMessages(String player) {
        ArrayList<String> playerMessages = new ArrayList<String>();
        if (this.getMessages().containsKey(player)) {
            for (Map.Entry e : ((GroupMessages)this.getMessages().get(player)).entrySet()) {
                for (Message message : (PendingMessages)e.getValue()) {
                    playerMessages.add(message.getMessage());
                }
            }
        }
        return playerMessages;
    }

    public ArrayList<String> getMessages(String player, Group group) {
        HashMap groupedMessages;
        ArrayList<String> playerMessages = new ArrayList<String>();
        if (this.getMessages().containsKey(player) && (groupedMessages = (HashMap)this.getMessages().get(player)).containsKey(group)) {
            for (Message message : (PendingMessages)groupedMessages.get(group)) {
                playerMessages.add(message.getMessage());
            }
        }
        return playerMessages;
    }

    public int getNumberOfMessages(String player) {
        ArrayList<String> messages = this.getMessages(player);
        return messages.size();
    }

    public int getNumberOfMessages(String player, Group group) {
        ArrayList<String> messages = this.getMessages(player, group);
        return messages.size();
    }

    public void removePlayerMessages(String player) {
        this.getMessages().remove(player);
    }

    public void removeGroup(Group group) {
        this.getMessages().remove(group);
    }

    public void removeGroupFromPlayer(String player, Group group) {
        this.getMessages().remove(player, group);
    }

    private PlayerMessages getMessages() {
        return this.getStore().getPlayerMessagesStore().get();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(FormattingUtil.addTabsToNewLines("Message Service\nMessages", 1));
        for (Map.Entry players : this.getMessages().entrySet()) {
            str.append(FormattingUtil.addTabsToNewLines("\nPlayer: " + (String)players.getKey(), 2));
            for (Map.Entry groupedMessages : ((GroupMessages)players.getValue()).entrySet()) {
                str.append(FormattingUtil.addTabsToNewLines("\n" + groupedMessages.getKey(), 3));
                for (Message message : (PendingMessages)groupedMessages.getValue()) {
                    str.append(FormattingUtil.addTabsToNewLines("\n" + message, 4));
                }
            }
        }
        return str.toString();
    }
}

