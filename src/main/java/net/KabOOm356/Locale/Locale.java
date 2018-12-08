/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationOptions
 *  org.bukkit.configuration.MemoryConfigurationOptions
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.configuration.file.YamlConfigurationOptions
 */
package net.KabOOm356.Locale;

import net.KabOOm356.Configuration.ConstantEntry;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Util.Initializable;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.MemoryConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

public class Locale
extends YamlConfiguration
implements Initializable {
    private boolean isInitialized = false;

    public String getString(Entry<String> entry) {
        if (entry instanceof ConstantEntry) {
            ConstantEntry constantEntry = ConstantEntry.class.cast(entry);
            return this.getString(constantEntry);
        }
        return this.getString(entry.getPath(), entry.getDefault());
    }

    public String getString(ConstantEntry<String> entry) {
        return entry.getDefault();
    }

    public String getPhrase(String phrase) {
        return super.getString("locale.phrases." + phrase);
    }

    public String getInfo(String info) {
        return this.getString("locale.info." + info);
    }

    @Override
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public void initialized() {
        this.isInitialized = true;
    }
}

