package com.axity.dinosaurpark.model;

import java.time.LocalDateTime;

/**
 * Registro inmutable de la venta de un boleto.
 *
 * CONCEPTO - Java Record (Java 16+):
 *   Un record es una forma corta de crear una clase de datos inmutable.
 *   Con una sola línea obtienes automáticamente:
 *     - Constructor que acepta todos los campos
 *     - Getter por cada campo (id(), price(), etc.)
 *     - equals(), hashCode() y toString()
 *
 *   Es inmutable: una vez creado el boleto, nadie puede cambiar su precio.
 *   (No hay setters. Igual que un boleto de papel real.)
 */
public record Ticket(
    long id,
    int touristId,
    double price,
    String category,
    LocalDateTime issuedAt
) {
    // El record genera todo automáticamente — no necesitas agregar nada más
    // Para acceder a los datos usa: ticket.id(), ticket.price(), etc.
}
