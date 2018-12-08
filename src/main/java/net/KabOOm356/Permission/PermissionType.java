/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Permission;

public enum PermissionType {
    PermissionsEx("PermissionsEx"),
    SuperPerms("SuperPerms");
    
    private final String typeName;

    private PermissionType(String typeName) {
        this.typeName = typeName;
    }

    public String toString() {
        return this.typeName;
    }
}

