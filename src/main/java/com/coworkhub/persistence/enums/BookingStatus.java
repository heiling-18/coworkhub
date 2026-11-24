package com.coworkhub.persistence.enums;

/** RN-09: PENDING -> CONFIRMED -> COMPLETED; PENDING o CONFIRMED -> CANCELLED. */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}
