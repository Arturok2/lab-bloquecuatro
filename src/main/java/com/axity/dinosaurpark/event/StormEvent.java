package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Evento: tormenta torrencial — todos los turistas activos son evacuados.
 *
 * La tormenta obliga a evacuar el parque: todos los turistas en IN_PARK
 * van a la zona de "Evacuación". También genera un gasto de $500.
 */
public class StormEvent implements SimulationEvent {

    @Override
    public String getName() { return "TORMENTA_TORRENCIAL"; }

    @Override
    public String getDescription() { return "Tormenta torrencial — evacuación del parque"; }

    @Override
    public void execute(ParkState state, Random rng) {
        System.out.println("  🌧️ [TORMENTA] ¡Tormenta torrencial! Evacuando el parque...");

        // Registrar visita a zona de evacuación para todos los turistas activos
        for (Tourist tourist : state.getActiveTourists()) {
            tourist.recordVisit("Evacuación");
        }

        // Gasto por la tormenta (infraestructura, seguridad, etc.)
        state.addExpense(500.0);
        state.getCsvWriter().appendExpense(new ExpenseRecord(
            System.currentTimeMillis(),
            "TORMENTA_DAÑOS",
            500.0,
            "Daños y costos de evacuación por tormenta",
            LocalDateTime.now()
        ));

        state.getCsvWriter().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(),
            "todos_los_turistas", LocalDateTime.now());
    }
}
