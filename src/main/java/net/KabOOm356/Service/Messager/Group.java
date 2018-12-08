/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Service.Messager;

public class Group {
    public static final Group DEFAULT = new Group("Default");
    private final String groupName;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return this.groupName;
    }

    public boolean equals(Group group) {
        return this.getName().equalsIgnoreCase(group.getName());
    }

    public String toString() {
        return "Group: " + this.groupName;
    }
}

