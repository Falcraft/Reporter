/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 */
package net.KabOOm356.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Database.ResultRow;
import net.KabOOm356.Database.SQLResultSet;
import net.KabOOm356.Permission.ModLevel;
import net.KabOOm356.Service.Service;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Util.BukkitUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ReportInformationService
extends Service {
    protected ReportInformationService(ServiceModule module) {
        super(module);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getViewableReports(CommandSender sender) throws InterruptedException, SQLException, ClassNotFoundException {
        ArrayList<Integer> indexes;
        String query;
        ArrayList<String> params = new ArrayList<String>();
        if (BukkitUtil.isPlayer(sender)) {
            OfflinePlayer player = OfflinePlayer.class.cast((Object)sender);
            query = "SELECT ID FROM Reports WHERE SenderUUID=?";
            params.add(player.getUniqueId().toString());
        } else {
            query = "SELECT ID FROM Reports WHERE Sender=?";
            params.add(sender.getName());
        }
        indexes = new ArrayList<Integer>();
        ExtendedDatabaseHandler database = this.getDatabase();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.preparedSQLQuery(connectionId, query, params);
            for (ResultRow row : result) {
                indexes.add(row.getInt("ID"));
            }
        }
        finally {
            database.closeConnection(connectionId);
        }
        return indexes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getCompletedReportIndexes() throws SQLException, ClassNotFoundException, InterruptedException {
        ArrayList<Integer> indexes;
        indexes = new ArrayList<Integer>();
        ExtendedDatabaseHandler database = this.getDatabase();
        int connectionId = database.openPooledConnection();
        try {
            String query = "SELECT ID FROM Reports WHERE CompletionStatus=1";
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT ID FROM Reports WHERE CompletionStatus=1");
            for (ResultRow row : result) {
                indexes.add(row.getInt("ID"));
            }
        }
        finally {
            database.closeConnection(connectionId);
        }
        return indexes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getIncompleteReportIndexes() throws ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<Integer> indexes;
        indexes = new ArrayList<Integer>();
        ExtendedDatabaseHandler database = this.getDatabase();
        int connectionId = database.openPooledConnection();
        try {
            String query = "SELECT ID FROM Reports WHERE CompletionStatus=0";
            SQLResultSet result = database.sqlQuery(connectionId, "SELECT ID FROM Reports WHERE CompletionStatus=0");
            for (ResultRow row : result) {
                indexes.add(row.getInt("ID"));
            }
        }
        finally {
            database.closeConnection(connectionId);
        }
        return indexes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getIndexesOfPriority(ModLevel level) throws ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<Integer> indexes;
        indexes = new ArrayList<Integer>();
        String query = "SELECT ID FROM Reports WHERE Priority = " + level.getLevel();
        ExtendedDatabaseHandler database = this.getDatabase();
        int connectionId = database.openPooledConnection();
        try {
            SQLResultSet result = database.sqlQuery(connectionId, query);
            for (ResultRow row : result) {
                indexes.add(row.getInt("ID"));
            }
        }
        finally {
            database.closeConnection(connectionId);
        }
        return indexes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ModLevel getReportPriority(int index) throws ClassNotFoundException, InterruptedException, SQLException {
        String query = "SELECT Priority FROM Reports WHERE ID=" + index;
        ExtendedDatabaseHandler database = this.getDatabase();
        Integer connectionId = null;
        try {
            connectionId = database.openPooledConnection();
            SQLResultSet result = database.sqlQuery(connectionId, query);
            int level = result.getInt("Priority");
            ModLevel modLevel = ModLevel.getByLevel(level);
            return modLevel;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }

    private ExtendedDatabaseHandler getDatabase() {
        return this.getStore().getDatabaseStore().get();
    }
}

