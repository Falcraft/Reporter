/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Util;

import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Util {
    private static final Logger log = LogManager.getLogger(Util.class);

    private Util() {
    }

    public static int countOccurrences(String str, char character) {
        String c = Character.toString(character);
        return Util.countOccurrences(str, c);
    }

    public static int countOccurrences(String str, String needle) {
        Validate.notNull((Object)str, (String)"Parameter 'str' cannot be null!");
        Validate.notNull((Object)needle, (String)"Parameter 'needle' cannot be null!");
        if (!str.contains(needle)) {
            return 0;
        }
        int it = 0;
        int count = 0;
        while (it != str.lastIndexOf(needle)) {
            it = str.indexOf(needle, it + 1);
            ++count;
        }
        return count;
    }

    public static boolean isInteger(String s) {
        return Util.parseInt(s) != null;
    }

    public static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (Exception e) {
            log.debug(String.format("Failed to parse integer from string [%s]!", str), (Throwable)e);
            return null;
        }
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        Validate.notNull((Object)str, (String)"Parameter 'str' cannot be null!");
        Validate.notNull((Object)suffix, (String)"Parameter 'suffix' cannot be null!");
        if (str.length() < suffix.length()) {
            return false;
        }
        String ending = str.substring(str.length() - suffix.length());
        return ending.equalsIgnoreCase(suffix);
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        Validate.notNull((Object)str, (String)"Parameter 'str' cannot be null!");
        Validate.notNull((Object)prefix, (String)"Parameter 'prefix' cannot be null!");
        if (str.length() < prefix.length()) {
            return false;
        }
        int offset = prefix.length() + 1 <= str.length() ? prefix.length() : str.length();
        String beginning = str.substring(0, offset);
        return beginning.equalsIgnoreCase(prefix);
    }
}

