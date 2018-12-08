/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service;

import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.StoreModule;

public abstract class Service {
    private final ServiceModule module;

    protected Service(ServiceModule module) {
        this.module = module;
    }

    protected ServiceModule getModule() {
        return this.module;
    }

    protected StoreModule getStore() {
        return this.getModule().getStore();
    }
}

