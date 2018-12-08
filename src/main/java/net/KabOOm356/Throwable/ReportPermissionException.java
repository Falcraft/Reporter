/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Throwable;

import net.KabOOm356.Throwable.ReporterException;
import org.bukkit.command.CommandSender;

public abstract class ReportPermissionException
extends ReporterException {
    private final CommandSender sender;
    private final int index;

    public ReportPermissionException(CommandSender sender, int index, String message) {
        super(message);
        this.sender = sender;
        this.index = index;
    }
}

