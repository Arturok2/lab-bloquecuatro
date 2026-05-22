package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/** Record inmutable para registrar un gasto en el CSV */
public record ExpenseRecord(
    long id,
    String type,           // "SALARIO", "FALLA_ELECTRICA", "TORMENTA", etc.
    double amount,
    String description,
    LocalDateTime timestamp
) {
    public String toCsvLine() {
        return id + "," + type + "," + String.format("%.2f", amount) + ","
            + description + "," + timestamp;
    }
}
