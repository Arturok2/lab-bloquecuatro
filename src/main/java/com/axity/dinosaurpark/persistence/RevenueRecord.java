package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/**
 * Record inmutable para guardar un ingreso en el CSV.
 *
 * CONCEPTO - Java Record:
 *   Con "record" declaras en UNA línea una clase de datos completa.
 *   Java genera automáticamente: constructor, getters (sin "get" prefix),
 *   equals(), hashCode() y toString(). No hay setters — es inmutable.
 *
 * Para usar: record.id(), record.type(), record.amount(), etc.
 */
public record RevenueRecord(
    long id,
    String type,        // "TICKET", "SOUVENIR", "SPA", "ENCIERRO_VIP", etc.
    double amount,
    int touristId,
    String zone,
    LocalDateTime timestamp
) {
    /** Convierte el registro a una línea de texto CSV */
    public String toCsvLine() {
        return id + "," + type + "," + String.format("%.2f", amount) + ","
            + touristId + "," + zone + "," + timestamp;
    }
}
