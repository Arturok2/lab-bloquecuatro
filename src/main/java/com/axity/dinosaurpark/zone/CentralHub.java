package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hub central del parque: los turistas compran recuerdos aquí.
 *
 * CONCEPTO - Probabilidad con Random:
 *   rng.nextDouble() devuelve un número entre 0.0 y 1.0.
 *   Si ese número es menor que souvenirProbability (ej. 0.40),
 *   hay 40% de probabilidad de que el turista compre un souvenir.
 *   Esta técnica se llama "Monte Carlo" — se usa mucho en simulaciones.
 */
public class CentralHub implements ParkZone {

    private static final String NAME = "Hub Central";

    private final double souvenirProbability;
    private final double souvenirPrice;
    private final List<Tourist> currentTourists;
    private final int maxCapacity;
    private long nextRevenueId = 1;

    public CentralHub(double souvenirProbability, double souvenirPrice, int maxCapacity) {
        this.souvenirProbability = souvenirProbability;
        this.souvenirPrice = souvenirPrice;
        this.maxCapacity = maxCapacity;
        this.currentTourists = new ArrayList<>();
    }

    /**
     * El turista visita el hub: con cierta probabilidad compra un souvenir.
     */
    public void visit(Tourist tourist, Random rng, CsvWriter csvWriter) {
        tourist.recordVisit(NAME);

        // ¿Compra souvenir? (probabilidad configurable)
        if (rng.nextDouble() < souvenirProbability) {
            tourist.spend(souvenirPrice);

            csvWriter.appendRevenue(new RevenueRecord(
                nextRevenueId++,
                "SOUVENIR",
                souvenirPrice,
                tourist.getId(),
                NAME,
                LocalDateTime.now()
            ));
        }
    }

    // ── ParkZone ─────────────────────────────────────────────────────────────

    @Override public String getName() { return NAME; }
    @Override public boolean hasCapacity() { return currentTourists.size() < maxCapacity; }
    @Override public int getCurrentOccupancy() { return currentTourists.size(); }
    @Override public int getMaxCapacity() { return maxCapacity; }
    @Override public void enter(Tourist tourist) { currentTourists.add(tourist); }
    @Override public void exit(Tourist tourist) { currentTourists.remove(tourist); }
}

