package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Evento: apagón masivo — la planta eléctrica falla.
 *
 * Este evento fuerza una falla en la planta aunque la probabilidad
 * normal no se haya activado. El técnico deberá repararla.
 */
public class BlackoutEvent implements SimulationEvent {

    @Override
    public String getName() { return "APAGON_MASIVO"; }

    @Override
    public String getDescription() { return "Apagón masivo en el parque"; }

    @Override
    public void execute(ParkState state, Random rng) {
        System.out.println("  💥 [APAGÓN] ¡Apagón masivo! La planta eléctrica falló.");

        // Forzar falla en la planta
        state.getPowerPlant().triggerFailure(state.getCsvWriter());

        // Gasto de emergencia adicional por el apagón
        state.addExpense(2000.0);
        state.getCsvWriter().appendExpense(new ExpenseRecord(
            System.currentTimeMillis(),
            "APAGON_EMERGENCIA",
            2000.0,
            "Gastos de emergencia por apagón masivo",
            LocalDateTime.now()
        ));

        state.getCsvWriter().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(),
            "planta_electrica", LocalDateTime.now());
    }
}
