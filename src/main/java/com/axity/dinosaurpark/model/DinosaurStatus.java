package com.axity.dinosaurpark.model;

/**
 * Estados posibles de un dinosaurio durante la simulación.
 */
public enum DinosaurStatus {
    IN_ENCLOSURE, // Dentro de su recinto (estado normal)
    ESCAPED,      // Se escapó del recinto (¡peligro!)
    RECAPTURED    // Fue capturado pero aún no regresó al recinto
}
