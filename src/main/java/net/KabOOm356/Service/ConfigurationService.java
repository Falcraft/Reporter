/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.configuration.Configuration
 */
package net.KabOOm356.Service;

import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.Configuration;

public class ConfigurationService
extends Service {
    private static final Logger log = LogManager.getLogger(ConfigurationService.class);

    protected ConfigurationService(ServiceModule module) {
        super(module);
    }

    public <T> T get(Entry<T> entry) {
        Configuration configuration = this.getStore().getConfigurationStore().get();
        Object value = configuration.get(entry.getPath(), entry.getDefault());
        if (value == null) {
            return entry.getDefault();
        }
        if (entry.getDefault().getClass().equals(value.getClass())) {
            return (T)entry.getDefault().getClass().cast(value);
        }
        log.warn(String.format("Configuration entry [%s] of class [%s] did not match the returned class of [%s]!", entry.getPath(), entry.getDefault().getClass().getSimpleName(), value.getClass().getSimpleName()));
        log.warn(String.format("To prevent errors for configuration entry [%s] the default value [%s] will be returned!", entry.getPath(), entry.getDefault()));
        return entry.getDefault();
    }
}

