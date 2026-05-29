package org.peepol.domain.enums;

public enum WishStatus {

    FORGOTTEN(0), WANTED(1), GRANTED(2);

    private final int value;

    WishStatus(int value) {
        this.value = value;
    }

    public static WishStatus fromId(int id) {
        for (WishStatus status : WishStatus.values()) {
            if (status.value == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WISH status id: " + id);
    }
}
