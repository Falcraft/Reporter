/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Command;

import net.KabOOm356.Command.Command;
import net.KabOOm356.Command.ReporterCommandManager;

public abstract class ReporterCommand
extends Command {
    protected ReporterCommand(ReporterCommandManager manager, String commandName, String commandPermissionNode, int minimumNumberOfArguments) {
        super(manager, commandName, commandPermissionNode, minimumNumberOfArguments);
    }
}

