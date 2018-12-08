/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.ChatColor
 */
package net.KabOOm356.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.KabOOm356.Database.ResultRow;
import net.KabOOm356.Database.SQLResultSet;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

public final class ArrayUtil {
    public static final String defaultEntrySeparator = ", ";
    public static final String defaultElementSeparator = "=";

    private ArrayUtil() {
    }

    public static <T> ArrayList<T> arrayToArrayList(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Parameter 'array' cannot be null!");
        }
        ArrayList arrayList = new ArrayList();
        Collections.addAll(arrayList, array);
        return arrayList;
    }

    public static <T> Set<T> arrayToSet(T[] array) {
        Validate.notNull(array, (String)"Parameter 'array' cannot be null!");
        HashSet set = new HashSet();
        Collections.addAll(set, array);
        return set;
    }

    public static <T extends Map<K, V>, K, V> String indexesToString(T map) {
        return ArrayUtil.indexesToString(map, defaultElementSeparator, defaultEntrySeparator);
    }

    public static <T extends Map<K, V>, K, V> String indexesToString(T map, String elementSeparator, String entrySeparator) {
        Validate.notNull(map, (String)"Parameter 'map' cannot be null!");
        Validate.notNull((Object)elementSeparator);
        Validate.notNull((Object)entrySeparator);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry e : map.entrySet()) {
            builder.append(e.getKey().toString());
            builder.append(elementSeparator);
            builder.append(e.getValue().toString());
            builder.append(entrySeparator);
        }
        if (builder.length() > entrySeparator.length() && builder.lastIndexOf(entrySeparator) != -1) {
            return builder.substring(0, builder.lastIndexOf(entrySeparator));
        }
        return builder.toString();
    }

    public static <T extends Iterable<V>, V> String indexesToString(T array) {
        return ArrayUtil.indexesToString(array, "", defaultEntrySeparator);
    }

    public static <V, T extends Iterable<V>> String indexesToString(T array, String indexPrefix, String indexPostfix) {
        Validate.notNull(array, (String)"Parameter 'array' cannot be null!");
        Validate.notNull((Object)indexPrefix);
        Validate.notNull((Object)indexPostfix);
        StringBuilder builder = new StringBuilder();
        for (Object index : array) {
            builder.append(indexPrefix).append(index.toString()).append(indexPostfix);
        }
        if (builder.length() > indexPostfix.length() && builder.lastIndexOf(indexPostfix) != -1) {
            return builder.substring(0, builder.lastIndexOf(indexPostfix));
        }
        return builder.toString();
    }

    public static <T extends Iterable<V>, V> String indexesToString(T array, ChatColor indexColor, ChatColor separatorColor) {
        Validate.notNull((Object)indexColor);
        Validate.notNull((Object)separatorColor);
        return ArrayUtil.indexesToString(array, indexColor.toString(), (Object)separatorColor + defaultEntrySeparator);
    }

    public static String indexesToString(SQLResultSet resultSet, String columnName, ChatColor indexColor, ChatColor separatorColor) {
        Validate.notNull((Object)resultSet);
        Validate.notNull((Object)columnName);
        Validate.notNull((Object)indexColor);
        Validate.notNull((Object)separatorColor);
        ArrayList<String> array = new ArrayList<String>();
        for (ResultRow row : resultSet) {
            array.add(row.getString(columnName));
        }
        return ArrayUtil.indexesToString(array, indexColor.toString(), (Object)separatorColor + defaultEntrySeparator);
    }
}

