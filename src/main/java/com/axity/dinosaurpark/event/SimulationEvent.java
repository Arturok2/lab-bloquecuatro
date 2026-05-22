package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.util.Random;

/**
 * Interfaz Strategy para todos los eventos de la simulación.
 *
 * CONCEPTO - Patrón Strategy:
 *   Todos los eventos (escape de dinosaurio, apagón, tormenta) implementan
 *   esta interfaz. Esto permite al Engine tratarlos de forma idéntica:
 *
 *     event.execute(state, rng);  // no importa si es Escape, Blackout o Storm
 *
 *   Para agregar un nuevo evento en el futuro, solo creas una nueva clase
 *   que implemente SimulationEvent — NO necesitas modificar el Engine.
 *   Esto se llama principio Open/Closed (abierto para extensión, cerrado para modificación).
 *
 * CONCEPTO - ¿Por qué execute() recibe ParkState y Random?
 *   - ParkState: el evento necesita acceder al estado del parque para modificarlo
 *     (hacer escapar un dinosaurio, marcar un turista como atacado, etc.)
 *   - Random: el mismo generador que usa el Engine, con la misma semilla,
 *     garantizando que los resultados sean reproducibles con simulation.seed
 */
public interface SimulationEvent {

    /** Nombre corto del evento (para logs y el CSV) */
    String getName();

    /** Descripción legible del evento */
    String getDescription();

    /**
     * Ejecuta el evento: modifica el estado del parque.
     *
     * @param state  estado actual del parque (turistas, dinosaurios, planta, etc.)
     * @param rng    generador de números aleatorios del Engine
     */
    void execute(ParkState state, Random rng);

    /** Crea el registro para guardar en events.csv */
    EventRecord toRecord(long step);
}
