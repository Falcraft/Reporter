/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.SQLStatServices;

import net.KabOOm356.Service.SQLStatService;
import net.KabOOm356.Service.ServiceModule;

public class PlayerStatService
extends SQLStatService {
    public static final String tableName = "PlayerStats";
    public static final String indexColumn = "UUID";
    public static final String secondaryIndexColumn = "Name";

    public PlayerStatService(ServiceModule module) {
        super(module, tableName, indexColumn, secondaryIndexColumn);
    }

    public static class PlayerStat
    extends SQLStatService.SQLStat {
        public static final PlayerStat REPORTCOUNT = new PlayerStat("Report", "ReportCount");
        public static final PlayerStat FIRSTREPORTDATE = new PlayerStat("FirstReport", "FirstReportDate");
        public static final PlayerStat LASTREPORTDATE = new PlayerStat("LastReport", "LastReportDate");
        public static final PlayerStat REPORTED = new PlayerStat("Reported", "ReportedCount");
        public static final PlayerStat FIRSTREPORTEDDATE = new PlayerStat("FirstReported", "FirstReportedDate");
        public static final PlayerStat LASTREPORTEDDATE = new PlayerStat("LastReported", "LastReportedDate");

        protected PlayerStat(String name, String columnName) {
            super(name, columnName);
        }
    }

}

