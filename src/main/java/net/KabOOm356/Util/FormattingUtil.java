/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.ChatColor
 */
package net.KabOOm356.Util;

import net.KabOOm356.Util.TimeUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

public final class FormattingUtil {
    public static final char tabCharacter = '\t';
    public static final char newLineCharacter = '\n';
    public static final String tab = Character.toString('\t');
    public static final String newLine = Character.toString('\n');

    private FormattingUtil() {
    }

    public static String appendCharacterAfter(String str, String character, String additionCharacter, int additions) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter 'str' cannot be null!");
        }
        if (character == null) {
            throw new IllegalArgumentException("Parameter 'character' cannot be null!");
        }
        if (additionCharacter == null) {
            throw new IllegalArgumentException("Parameter 'additionCharacter' cannot be null!");
        }
        if (additions < 0) {
            throw new IllegalArgumentException("Parameter 'additions' must be greater than zero(0)!\nReceived value: " + additions);
        }
        if (additions == 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        for (int LCV = 0; LCV < additions; ++LCV) {
            builder.append(additionCharacter);
        }
        String repeatedAdditionCharacter = builder.toString();
        str = str.replaceAll(character, character + repeatedAdditionCharacter);
        return str;
    }

    public static String appendCharacterAfter(String str, char character, char additionCharacter, int additions) {
        String c = Character.toString(character);
        String ac = Character.toString(additionCharacter);
        return FormattingUtil.appendCharacterAfter(str, c, ac, additions);
    }

    public static String addCharacterToNewLines(String str, char additionCharacter, int additions) {
        return FormattingUtil.appendCharacterAfter(str, '\n', additionCharacter, additions);
    }

    public static String addCharacterToNewLines(String str, String additionCharacter, int additions) {
        return FormattingUtil.appendCharacterAfter(str, newLine, additionCharacter, additions);
    }

    public static String addTabsToNewLines(String str, int tabs) {
        return FormattingUtil.addCharacterToNewLines(str, tab, tabs);
    }

    public static String capitalizeFirstCharacter(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter 'str' cannot be null!");
        }
        if (str.length() <= 0) {
            return "";
        }
        str = str.toLowerCase();
        return String.valueOf(Character.toUpperCase(str.charAt(0))) + str.substring(1);
    }

    public static String formatTimeRemaining(String formatLine, int seconds) {
        Validate.notNull((Object)formatLine, (String)"The format line cannot be null!");
        int hours = TimeUtil.getHours(seconds);
        int remainingSeconds = seconds % 3600;
        int minutes = TimeUtil.getMinutes(remainingSeconds);
        String line = formatLine.replaceAll("%h", (Object)ChatColor.GOLD + Integer.toString(hours) + (Object)ChatColor.WHITE);
        line = line.replaceAll("%m", (Object)ChatColor.GOLD + Integer.toString(minutes) + (Object)ChatColor.WHITE);
        line = line.replaceAll("%s", (Object)ChatColor.GOLD + Integer.toString(remainingSeconds %= 60) + (Object)ChatColor.WHITE);
        return line;
    }
}

