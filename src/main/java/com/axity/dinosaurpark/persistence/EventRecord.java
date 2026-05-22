package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/** Record inmutable para registrar un evento en el CSV */
public record EventRecord(
    long step,
    String eventName,
    String description,
    String affectedEntities,
    LocalDateTime timestamp
) {
    public String toCsvLine() {
        return step + "," + eventName + "," + description + ","
            + affectedEntities + "," + timestamp;
    }
}
