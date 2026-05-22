package com.axity.dinosaurpark.model;

/**
 * Estados posibles de un turista durante la simulación.
 *
 * CONCEPTO: Los enums limitan los valores posibles a un conjunto fijo.
 * Usar TouristStatus.IN_PARK es más seguro que usar el String "in_park"
 * porque el compilador detecta errores tipográficos en tiempo de compilación.
 */
public enum TouristStatus {
    WAITING,   // Esperando en la cola de entrada (aún no entró)
    IN_PARK,   // Dentro del parque, visitando zonas
    ATTACKED,  // Fue atacado por un dinosaurio
    EXITED     // Ya salió del parque (terminó su visita)
}
