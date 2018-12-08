/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Util;

public final class TimeUtil {
    public static final int secondsPerMinute = 60;
    public static final int secondsPerHour = 3600;

    private TimeUtil() {
    }

    public static int getMinutes(int seconds) {
        return TimeUtil.convert(seconds, 60);
    }

    public static int getHours(int seconds) {
        return TimeUtil.convert(seconds, 3600);
    }

    private static int convert(int seconds, int perSeconds) {
        if (seconds == 0) {
            return 0;
        }
        return (int)Math.ceil(seconds / perSeconds);
    }
}

