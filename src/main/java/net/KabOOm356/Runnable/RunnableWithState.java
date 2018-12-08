/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Runnable;

public interface RunnableWithState
extends Runnable {
    public boolean isRunning();

    public boolean isPendingToRun();

    public boolean isStopped();

    public boolean hasRun();
}

