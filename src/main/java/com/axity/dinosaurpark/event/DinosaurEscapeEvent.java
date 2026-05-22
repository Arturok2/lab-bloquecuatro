package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Dinosaur;
import com.axity.dinosaurpark.model.DinosaurStatus;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Evento: un dinosaurio escapa de su encierro.
 *
 * CONCEPTO - Streams y filter:
 *   dinosaurs.stream()
 *            .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
 *            .collect(Collectors.toList())
 *
 *   Esto crea una lista nueva con SOLO los dinosaurios que están en su encierro.
 *   Es la forma moderna en Java 8+ de filtrar colecciones sin escribir bucles.
 *
 * LÓGICA DEL EVENTO:
 *   1. Filtra dinosaurios disponibles (solo los que están IN_ENCLOSURE)
 *   2. Elige uno al azar con rng.nextInt(lista.size())
 *   3. Lo marca como ESCAPED
 *   4. Si rng.nextDouble() < dangerLevel → el dinosaurio ataca a un turista
 *   5. Guarda el registro en el CSV
 */
public class DinosaurEscapeEvent implements SimulationEvent {

    @Override
    public String getName() { return "ESCAPE_DINOSAURIO"; }

    @Override
    public String getDescription() { return "Un dinosaurio escapó de su recinto"; }

    @Override
    public void execute(ParkState state, Random rng) {
        // Obtener dinosaurios que pueden escapar (solo los que están encerrados)
        List<Dinosaur> candidates = state.getDinosaurs().stream()
            .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
            .collect(Collectors.toList());

        if (candidates.isEmpty()) return; // No hay dinosaurios para escapar

        // Elegir uno al azar
        Dinosaur escaped = candidates.get(rng.nextInt(candidates.size()));
        escaped.escape();

        System.out.println("  🦕 [ESCAPE] " + escaped.getName() + " escapó del encierro!");

        // ¿Ataca a un turista? (depende del nivel de peligro del dinosaurio)
        List<Tourist> activeTourists = state.getActiveTourists();
        if (!activeTourists.isEmpty() && rng.nextDouble() < escaped.getDangerLevel()) {
            Tourist victim = activeTourists.get(rng.nextInt(activeTourists.size()));
            victim.setStatus(TouristStatus.ATTACKED);
            System.out.println("  ⚠️  [ESCAPE] " + escaped.getName() + " atacó a " + victim.getName() + "!");
        }

        // Registrar en CSV
        state.getCsvWriter().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(),
            "dinosaurio+turista", LocalDateTime.now());
    }
}
