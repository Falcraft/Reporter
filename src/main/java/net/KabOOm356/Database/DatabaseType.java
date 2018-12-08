/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database;

public enum DatabaseType {
    SQLITE("SQLite"),
    MYSQL("MySQL");
    
    private final String databaseTypeName;

    private DatabaseType(String databaseTypeName) {
        this.databaseTypeName = databaseTypeName;
    }

    public String toString() {
        return this.databaseTypeName;
    }
}

