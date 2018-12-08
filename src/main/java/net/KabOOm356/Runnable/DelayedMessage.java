/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Runnable;

import org.bukkit.entity.Player;

public class DelayedMessage
implements Runnable {
    private final Player player;
    private final String message;

    public DelayedMessage(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void run() {
        if (this.player.isOnline()) {
            this.player.sendMessage(this.message);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }
}

