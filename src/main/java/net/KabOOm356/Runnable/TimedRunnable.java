/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Runnable;

import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TimedRunnable
implements Runnable {
    private static final Logger log = LogManager.getLogger(TimedRunnable.class);
    private Long startTime = null;
    private Long endTime = null;

    protected void start() {
        this.startTime = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.log(Level.INFO, "Starting execution of " + this.getClass().getName());
        }
    }

    protected void end() {
        this.endTime = System.currentTimeMillis();
        if (this.startTime != null && log.isDebugEnabled()) {
            log.log(Level.INFO, "Execution of " + this.getClass().getName() + " took " + this.getExecutionTime() + "ms!");
        }
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public Long getExecutionTime() {
        Validate.notNull((Object)this.startTime, (String)"Thread was never started!");
        Validate.notNull((Object)this.endTime, (String)"Thread has not ended!");
        return this.endTime - this.startTime;
    }
}

