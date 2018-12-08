/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service;

import java.sql.SQLException;
import net.KabOOm356.Service.ReportCountService;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Throwable.IndexOutOfRangeException;

public class ReportValidatorService
extends Service {
    protected ReportValidatorService(ServiceModule module) {
        super(module);
    }

    public void requireReportIndexValid(int index) throws ClassNotFoundException, SQLException, InterruptedException, IndexOutOfRangeException {
        int count = this.getCount();
        if (index < 1 || index > count) {
            String message = String.format("The requested index [%d] is out side of range [1-%d]", index, count);
            throw new IndexOutOfRangeException(message);
        }
    }

    public boolean isReportIndexValid(int index) throws InterruptedException, SQLException, ClassNotFoundException {
        try {
            this.requireReportIndexValid(index);
            return true;
        }
        catch (IndexOutOfRangeException e) {
            return false;
        }
    }

    private int getCount() throws InterruptedException, SQLException, ClassNotFoundException {
        return this.getModule().getReportCountService().getCount();
    }
}

