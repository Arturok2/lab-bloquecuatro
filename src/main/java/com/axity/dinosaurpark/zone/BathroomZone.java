package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Zona de baños: capacidad limitada con temporizador por slot.
 *
 * CONCEPTO - Map (Diccionario):
 *   Usamos Map<Tourist, Integer> para recordar CUÁNTOS steps le quedan
 *   a cada turista en el baño. La clave es el turista, el valor es
 *   el contador regresivo (cuántos ticks faltan para liberar el slot).
 *
 * CONCEPTO - tick():
 *   Cada step del Engine llama a tick(). Esto decrementa el contador
 *   de cada turista. Cuando llega a 0, el slot queda libre.
 *   Así simulamos que pasar tiempo en el baño "cuesta" steps.
 */
public class BathroomZone implements ParkZone {

    private static final String NAME = "Zona de Baños";

    private final int capacity;           // Slots disponibles (ej. 5)
    private final int useDurationSteps;   // Cuántos steps ocupa cada slot
    private final double spaProbability;
    private final double spaPrice;

    // Turistas que están actualmente en el baño y cuántos steps les quedan
    private final Map<Tourist, Integer> occupiedSlots;
    private long nextRevenueId = 1;

    public BathroomZone(int capacity, int useDurationSteps,
                        double spaProbability, double spaPrice) {
        this.capacity = capacity;
        this.useDurationSteps = useDurationSteps;
        this.spaProbability = spaProbability;
        this.spaPrice = spaPrice;
        this.occupiedSlots = new HashMap<>();
    }

    /**
     * Intenta que el turista entre al baño.
     * Si hay espacio: ocupa un slot y quizás compra el servicio SPA.
     * Si está lleno: el turista se queda afuera este step.
     */
    public void tryEnter(Tourist tourist, Random rng, CsvWriter csvWriter) {
        if (occupiedSlots.size() < capacity && !occupiedSlots.containsKey(tourist)) {
            // El turista entra al baño
            occupiedSlots.put(tourist, useDurationSteps);
            tourist.recordVisit(NAME);

            // ¿Compra servicio SPA?
            if (rng.nextDouble() < spaProbability) {
                tourist.spend(spaPrice);
                csvWriter.appendRevenue(new RevenueRecord(
                    nextRevenueId++, "SPA", spaPrice,
                    tourist.getId(), NAME, LocalDateTime.now()
                ));
            }
        }
    }

    /**
     * Avanza el tiempo: decrementa el contador de cada turista.
     * Cuando el contador llega a 0, el turista libera su slot.
     *
     * Este método debe llamarse UNA VEZ por step desde el SimulationEngine.
     */
    public void tick() {
        // Decrementamos los contadores y eliminamos los que terminaron
        occupiedSlots.replaceAll((tourist, stepsLeft) -> stepsLeft - 1);
        occupiedSlots.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    //  ParkZone 

    @Override public String getName() { return NAME; }
    @Override public boolean hasCapacity() { return occupiedSlots.size() < capacity; }
    @Override public int getCurrentOccupancy() { return occupiedSlots.size(); }
    @Override public int getMaxCapacity() { return capacity; }
    @Override public void enter(Tourist tourist) { /* controlado por tryEnter */ }
    @Override public void exit(Tourist tourist) { occupiedSlots.remove(tourist); }
}

