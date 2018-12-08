/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.SQLStatServices;

import net.KabOOm356.Service.SQLStatService;
import net.KabOOm356.Service.ServiceModule;

public class ModeratorStatService
extends SQLStatService {
    public static final String tableName = "ModStats";
    public static final String indexColumn = "ModUUID";
    public static final String secondaryIndexColumn = "ModName";

    public ModeratorStatService(ServiceModule module) {
        super(module, tableName, indexColumn, secondaryIndexColumn);
    }

    public static class ModeratorStat
    extends SQLStatService.SQLStat {
        public static final ModeratorStat ASSIGNED = new ModeratorStat("Assigned", "AssignCount");
        public static final ModeratorStat CLAIMED = new ModeratorStat("Claimed", "ClaimedCount");
        public static final ModeratorStat COMPLETED = new ModeratorStat("Completed", "CompletionCount");
        public static final ModeratorStat DELETED = new ModeratorStat("Deleted", "DeletionCount");
        public static final ModeratorStat MOVED = new ModeratorStat("Moved", "MoveCount");
        public static final ModeratorStat RESPONDED = new ModeratorStat("Responded", "RespondCount");
        public static final ModeratorStat UNASSIGNED = new ModeratorStat("Unassigned", "UnassignCount");
        public static final ModeratorStat UNCLAIMED = new ModeratorStat("Unclaimed", "UnclaimCount");

        protected ModeratorStat(String name, String columnName) {
            super(name, columnName);
        }
    }

}

