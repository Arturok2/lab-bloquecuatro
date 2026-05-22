package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Planta eléctrica: consume energía por step y puede fallar.
 *
 * CONCEPTO - Estado mutable con comportamiento:
 *   La planta tiene estado (energía actual, si está operacional) y
 *   comportamiento (tick consume energía, triggerFailure la apaga,
 *   repair la reactiva). Este patrón de "objeto con estado interno"
 *   es fundamental en POO.
 *
 * CONCEPTO - tick():
 *   Igual que BathroomZone, la planta avanza en el tiempo con tick().
 *   Cada step consume energía. Si llega a 0 o si la probabilidad de
 *   falla se activa, la planta se apaga y el Técnico debe repararla.
 */
public class PowerPlant implements ParkZone {

    private static final String NAME = "Planta Eléctrica";

    private double energyLevel;           // Energía actual (0–100)
    private final double consumptionPerStep; // Cuánta energía consume por step
    private final double failureProbability;
    private boolean operational;           // ¿Está funcionando?
    private long nextExpenseId = 1;
    private long nextEventId = 1;

    public PowerPlant(double initialEnergy, double consumptionPerStep, double failureProbability) {
        this.energyLevel = initialEnergy;
        this.consumptionPerStep = consumptionPerStep;
        this.failureProbability = failureProbability;
        this.operational = true;
    }

    /**
     * Avanza un step: consume energía y evalúa si ocurre una falla.
     */
    public void tick(Random rng, CsvWriter csvWriter) {
        if (!operational) return; // Si ya está apagada, no consume

        energyLevel = Math.max(0, energyLevel - consumptionPerStep);

        // ¿Falla aleatoria?
        if (energyLevel > 0 && rng.nextDouble() < failureProbability) {
            triggerFailure(csvWriter);
        } else if (energyLevel <= 0) {
            // Sin energía → falla obligatoria
            triggerFailure(csvWriter);
        }
    }

    /** Apaga la planta y registra el evento y el gasto */
    public void triggerFailure(CsvWriter csvWriter) {
        this.operational = false;
        this.energyLevel = 0;

        // Registrar gasto de emergencia
        csvWriter.appendExpense(new ExpenseRecord(
            nextExpenseId++,
            "FALLA_ELECTRICA",
            1500.0,
            "Costo de emergencia por falla en planta",
            LocalDateTime.now()
        ));

        // Registrar evento
        csvWriter.appendEvent(new EventRecord(
            0, "FALLA_PLANTA", "La planta eléctrica falló",
            NAME, LocalDateTime.now()
        ));

        System.out.println("  ⚡ [Planta] ¡FALLA ELÉCTRICA! La planta quedó fuera de servicio.");
    }

    /** El técnico repara la planta: vuelve a estar operacional con energía recargada */
    public void repair() {
        this.operational = true;
        this.energyLevel = 100.0;
    }

    public boolean isOperational() { return operational; }
    public double getEnergyLevel() { return energyLevel; }

    //  ParkZone 
    @Override public String getName() { return NAME; }
    @Override public boolean hasCapacity() { return false; }
    @Override public int getCurrentOccupancy() { return 0; }
    @Override public int getMaxCapacity() { return 0; }
    @Override public void enter(Tourist tourist) {}
    @Override public void exit(Tourist tourist) {}
}
