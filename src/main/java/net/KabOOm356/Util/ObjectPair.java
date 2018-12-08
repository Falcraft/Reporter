/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Util;

import java.util.Map;

public class ObjectPair<K, V>
implements Map.Entry<K, V> {
    private K key;
    private V value;

    public ObjectPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public K setKey(K key) {
        K oldKey = this.key;
        this.key = key;
        return oldKey;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public String toString() {
        return "[" + this.key + '=' + this.value + ']';
    }
}

