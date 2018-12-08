/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.Messager.Messages;

public abstract class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean messagesEqual(Message message) {
        return this.getMessage().equalsIgnoreCase(message.getMessage());
    }

    public abstract boolean isEmpty();

    public String toString() {
        return "Message: " + this.message;
    }
}

