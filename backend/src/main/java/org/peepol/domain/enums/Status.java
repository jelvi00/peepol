package org.peepol.domain.enums;

public enum Status {

    DISABLED(0), ENABLED(1);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public static Status fromId(int id) {
        for (Status status : Status.values()) {
            if (status.value == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status id: " + id);
    }
}
