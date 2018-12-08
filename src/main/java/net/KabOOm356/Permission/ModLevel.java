/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.KabOOm356.Permission;

import net.KabOOm356.Util.Util;
import org.bukkit.ChatColor;

public enum ModLevel {
    UNKNOWN("UNKNOWN", -1, ChatColor.GRAY),
    NONE("None", 0, ChatColor.GRAY),
    LOW("Low", 1, ChatColor.BLUE),
    NORMAL("Normal", 2, ChatColor.GREEN),
    HIGH("High", 3, ChatColor.RED);
    
    private final String name;
    private final int level;
    private final ChatColor color;

    private ModLevel(String name, int level, ChatColor color) {
        this.name = name;
        this.level = level;
        this.color = color;
    }

    public static boolean modLevelInBounds(String modLevel) {
        return ModLevel.modLevelInBounds(ModLevel.getModLevel((String)modLevel).level);
    }

    public static boolean modLevelInBounds(int modLevel) {
        return modLevel >= 0 && modLevel < 4;
    }

    public static ModLevel getModLevel(String modLevel) {
        ModLevel level = ModLevel.getByName(modLevel);
        if (level == UNKNOWN && Util.isInteger(modLevel)) {
            level = ModLevel.getByLevel(Util.parseInt(modLevel));
        }
        return level;
    }

    public static ModLevel getByLevel(int level) {
        switch (level) {
            case 0: {
                return NONE;
            }
            case 1: {
                return LOW;
            }
            case 2: {
                return NORMAL;
            }
            case 3: {
                return HIGH;
            }
        }
        return UNKNOWN;
    }

    public static ModLevel getByName(String level) {
        boolean isInteger = Util.isInteger(level);
        if (level.equalsIgnoreCase(ModLevel.LOW.name) || isInteger && Integer.parseInt(level) == ModLevel.LOW.level) {
            return LOW;
        }
        if (level.equalsIgnoreCase(ModLevel.NORMAL.name) || isInteger && Integer.parseInt(level) == ModLevel.NORMAL.level) {
            return NORMAL;
        }
        if (level.equalsIgnoreCase(ModLevel.HIGH.name) || isInteger && Integer.parseInt(level) == ModLevel.HIGH.level) {
            return HIGH;
        }
        if (level.equalsIgnoreCase(ModLevel.NONE.name) || isInteger && Integer.parseInt(level) == ModLevel.NONE.level) {
            return NONE;
        }
        return UNKNOWN;
    }

    public static int compareToByLevel(ModLevel level1, ModLevel level2) {
        return level1.compareToByLevel(level2);
    }

    public ChatColor getColor() {
        return this.color;
    }

    public int compareToByLevel(ModLevel level) {
        return this.getLevel() - level.getLevel();
    }

    public int getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "Priority: " + this.name + "\nLevel: " + this.level;
    }
}

