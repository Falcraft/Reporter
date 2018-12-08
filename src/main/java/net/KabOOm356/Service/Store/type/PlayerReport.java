/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Service.Store.type;

import java.util.HashMap;
import java.util.UUID;
import net.KabOOm356.Runnable.Timer.ReportTimer;
import net.KabOOm356.Service.Store.type.PlayerReportQueue;
import net.KabOOm356.Util.BukkitUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerReport
extends HashMap<UUID, PlayerReportQueue> {
    private static final long serialVersionUID = 8004603524749240197L;

    public PlayerReportQueue get(OfflinePlayer sender) {
        Validate.notNull((Object)sender);
        return (PlayerReportQueue)super.get(sender.getUniqueId());
    }

    public PlayerReportQueue get(CommandSender sender) {
        if (BukkitUtil.isOfflinePlayer(sender)) {
            OfflinePlayer player = OfflinePlayer.class.cast((Object)sender);
            return this.get(player);
        }
        return null;
    }

    public void put(CommandSender sender, OfflinePlayer reported, ReportTimer timer) {
        if (BukkitUtil.isPlayer(sender)) {
            Player player = Player.class.cast((Object)sender);
            UUID key = player.getUniqueId();
            this.add(key, reported, timer);
        }
    }

    public void put(OfflinePlayer sender, OfflinePlayer reported, ReportTimer timer) {
        UUID key = sender.getUniqueId();
        this.add(key, reported, timer);
    }

    public void remove(OfflinePlayer sender, OfflinePlayer reported, ReportTimer timer) {
        PlayerReportQueue playerReport = this.get(sender);
        if (playerReport != null) {
            playerReport.remove(reported, timer);
        }
    }

    private void add(UUID key, OfflinePlayer player, ReportTimer timer) {
        PlayerReportQueue queue;
        if (this.containsKey(key)) {
            queue = (PlayerReportQueue)this.get(key);
        } else {
            queue = new PlayerReportQueue();
            this.put(key, queue);
        }
        queue.put(player, timer);
    }
}

