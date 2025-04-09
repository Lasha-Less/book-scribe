package com.lb.book_scribe.model;

import lombok.Getter;

@Getter
public enum Role {

    AUTHOR("Author"),
    EDITOR("Editor"),
    TRANSLATOR("Translator"),
    CONTRIBUTOR("Contributor"),
    ILLUSTRATOR("Illustrator"),
    OTHER("Other");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }

}
