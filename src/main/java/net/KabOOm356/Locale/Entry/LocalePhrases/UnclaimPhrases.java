/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Locale.Entry.LocalePhrases;

import net.KabOOm356.Locale.Entry.LocalePhrase;

public abstract class UnclaimPhrases {
    public static final LocalePhrase reportAlreadyClaimed = new LocalePhrase("reportAlreadyClaimed", "The report at index %i is already claimed by %c, whose priority clearance is equal to or above yours!");
    public static final LocalePhrase reportIsNotClaimed = new LocalePhrase("reportIsNotClaimed", "The report at index %i is not claimed yet!");
    public static final LocalePhrase reportUnclaimSuccess = new LocalePhrase("reportUnclaimSuccess", "You have successfully unclaimed the report at index %i!");
    public static final LocalePhrase unclaimHelp = new LocalePhrase("unclaimHelp", "/report unclaim <Index/last>");
    public static final LocalePhrase unclaimHelpDetails = new LocalePhrase("unclaimHelpDetails", "Opposite of claiming a report, states you would like to step down from being in charge of dealing with this report.");
}

