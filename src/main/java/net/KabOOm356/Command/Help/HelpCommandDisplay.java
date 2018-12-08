/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 */
package net.KabOOm356.Command.Help;

import net.KabOOm356.Locale.Entry.LocalePhrase;
import org.apache.commons.lang.Validate;

public final class HelpCommandDisplay {
    private final LocalePhrase header;
    private final LocalePhrase alias;
    private final LocalePhrase next;
    private final LocalePhrase hint;

    private HelpCommandDisplay(Builder builder) {
        Validate.notNull((Object)builder.header);
        Validate.notNull((Object)builder.alias);
        Validate.notNull((Object)builder.next);
        Validate.notNull((Object)builder.hint);
        this.header = builder.header;
        this.alias = builder.alias;
        this.next = builder.next;
        this.hint = builder.hint;
    }

    public LocalePhrase getHeader() {
        return this.header;
    }

    public LocalePhrase getAlias() {
        return this.alias;
    }

    public LocalePhrase getNext() {
        return this.next;
    }

    public LocalePhrase getHint() {
        return this.hint;
    }

    public static class Builder {
        private LocalePhrase header;
        private LocalePhrase alias;
        private LocalePhrase next;
        private LocalePhrase hint;

        public Builder setHeader(LocalePhrase header) {
            this.header = header;
            return this;
        }

        public Builder setAlias(LocalePhrase alias) {
            this.alias = alias;
            return this;
        }

        public Builder setNext(LocalePhrase next) {
            this.next = next;
            return this;
        }

        public Builder setHint(LocalePhrase hint) {
            this.hint = hint;
            return this;
        }

        public HelpCommandDisplay build() {
            return new HelpCommandDisplay(this);
        }
    }

}

