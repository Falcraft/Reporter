/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package net.KabOOm356.Runnable.Timer;

import java.util.Calendar;
import java.util.Comparator;
import net.KabOOm356.Service.ReportLimitService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ReportTimer
extends Thread
implements Comparable<ReportTimer> {
    public static final CompareByTimeRemaining compareByTimeRemaining = new CompareByTimeRemaining();
    public boolean isFinished = true;
    private ReportLimitService manager;
    private Player player;
    private OfflinePlayer reported;
    private long executionTime;

    public ReportTimer() {
        this.setDaemon(false);
    }

    public void init(ReportLimitService manager, Player player, OfflinePlayer reported, long executionTime) {
        this.manager = manager;
        this.player = player;
        this.reported = reported;
        this.executionTime = executionTime;
        this.isFinished = false;
    }

    @Override
    public void run() {
        this.isFinished = true;
        this.manager.limitExpired(this);
    }

    public Player getPlayer() {
        return this.player;
    }

    public OfflinePlayer getReported() {
        return this.reported;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public int getTimeRemaining() {
        long executionTime = this.getExecutionTime();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (int)((executionTime - currentTime) / 1000L);
    }

    @Override
    public int compareTo(ReportTimer arg0) {
        return compareByTimeRemaining.compare(this, arg0);
    }

    public static class CompareByTimeRemaining
    implements Comparator<ReportTimer> {
        @Override
        public int compare(ReportTimer arg0, ReportTimer arg1) {
            return arg0.getTimeRemaining() - arg1.getTimeRemaining();
        }
    }

}

