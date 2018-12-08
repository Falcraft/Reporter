/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 */
package net.KabOOm356.Service.Store;

import org.apache.commons.lang.Validate;

public class Store<T> {
    private final T store;

    protected Store(T store) {
        Validate.notNull(store);
        this.store = store;
    }

    public T get() {
        return this.store;
    }
}

