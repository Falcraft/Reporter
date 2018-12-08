/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.ReportInformationService;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;

public class ReportCountService
extends Service {
    protected ReportCountService(ServiceModule module) {
        super(module);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCount() throws InterruptedException, SQLException, ClassNotFoundException {
        ExtendedDatabaseHandler database = this.getDatabase();
        int connectionId = database.openPooledConnection();
        try {
            String query = "SELECT COUNT(*) AS Count FROM Reports";
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT COUNT(*) AS Count FROM Reports");
            int n = result.getInt("Count");
            return n;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    public int getIncompleteReports() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.getIncompleteReportIndexes().size();
    }

    public int getCompletedReports() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.getCompletedReportIndexes().size();
    }

    public int getNumberOfPriority(ModLevel level) throws SQLException, ClassNotFoundException, InterruptedException {
        return this.getIndexesOfPriority(level).size();
    }

    private ExtendedDatabaseHandler getDatabase() {
        return this.getStore().getDatabaseStore().get();
    }

    private List<Integer> getIncompleteReportIndexes() throws InterruptedException, SQLException, ClassNotFoundException {
        return this.getModule().getReportInformationService().getIncompleteReportIndexes();
    }

    private List<Integer> getCompletedReportIndexes() throws InterruptedException, SQLException, ClassNotFoundException {
        return this.getModule().getReportInformationService().getCompletedReportIndexes();
    }

    private List<Integer> getIndexesOfPriority(ModLevel level) throws InterruptedException, SQLException, ClassNotFoundException {
        return this.getModule().getReportInformationService().getIndexesOfPriority(level);
    }
}

