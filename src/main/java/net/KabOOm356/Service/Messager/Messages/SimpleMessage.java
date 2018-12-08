/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.Messager.Messages;

import net.KabOOm356.Service.Messager.Messages.Message;

public class SimpleMessage
extends Message {
    public SimpleMessage(String message) {
        super(message);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

