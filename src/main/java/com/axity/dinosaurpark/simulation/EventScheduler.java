package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.event.BlackoutEvent;
import com.axity.dinosaurpark.event.DinosaurEscapeEvent;
import com.axity.dinosaurpark.event.SimulationEvent;
import com.axity.dinosaurpark.event.StormEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Programa los eventos de la simulación de forma determinística.
 *
 * CONCEPTO - Determinismo:
 *   Con new Random(seed), la secuencia de números es SIEMPRE la misma.
 *   Así, con simulation.seed=42, la simulación siempre produce los mismos
 *   resultados. Esto facilita depurar: corres dos veces y obtienes exactamente
 *   lo mismo, lo que te permite saber si un cambio en el código alteró el comportamiento.
 *
 * CONCEPTO - Map<Integer, SimulationEvent>:
 *   Es un diccionario: la clave es el número de step, el valor es el evento
 *   que ocurrirá en ese step. Si el step 15 tiene un ESCAPE_DINOSAURIO,
 *   al ejecutar checkForEvent(15) obtienes ese evento.
 *
 * NOTA: El lab INTERMEDIO elimina esta clase y la reemplaza por eventos
 *       probabilísticos (cada evento tiene % de ocurrir en cada step).
 */
public class EventScheduler {

    // Diccionario: step → evento programado para ese step
    private final Map<Integer, SimulationEvent> scheduledEvents;

    public EventScheduler(long seed, int totalSteps) {
        this.scheduledEvents = new HashMap<>();

        // Usa el mismo Random con semilla para reproducibilidad
        Random rng = new Random(seed);

        // Lista de eventos disponibles
        SimulationEvent[] events = {
            new DinosaurEscapeEvent(),
            new BlackoutEvent(),
            new StormEvent()
        };

        // Pre-calcular cuándo ocurrirá cada evento (en qué step)
        // Cada evento se asigna a un step aleatorio distinto
        for (SimulationEvent event : events) {
            int step = rng.nextInt(totalSteps);
            // Si ese step ya tiene un evento, busca el siguiente libre
            while (scheduledEvents.containsKey(step)) {
                step = (step + 1) % totalSteps;
            }
            scheduledEvents.put(step, event);
        }
    }

    /**
     * Revisa si hay un evento programado para este step.
     *
     * CONCEPTO - Optional:
     *   Optional<T> es un contenedor que puede o no tener un valor.
     *   Es más seguro que devolver null porque obliga al llamador a verificar
     *   si hay un valor antes de usarlo:
     *
     *     scheduler.checkForEvent(step).ifPresent(e -> e.execute(state, rng));
     *
     *   Si no hay evento, ifPresent() no hace nada — sin NullPointerException.
     */
    public Optional<SimulationEvent> checkForEvent(int step) {
        return Optional.ofNullable(scheduledEvents.get(step));
    }
}
