package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.zone.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Estado global del parque durante la simulación.
 *
 * CONCEPTO - Separación de responsabilidades (SRP):
 *   ParkState SABE cosas: cuántos turistas hay, cuánta energía queda,
 *   cuánto dinero se ganó. SimulationEngine HACE cosas: ejecuta el loop,
 *   llama a los eventos, mueve a los turistas.
 *
 *   Al separarlos, los eventos pueden leer y modificar el estado del parque
 *   sin necesitar una referencia al Engine completo. Esto evita dependencias
 *   circulares y hace el código más fácil de testear.
 *
 * Este objeto se pasa como referencia a eventos y al monitor.
 * Cualquier cambio que haga un evento queda reflejado aquí.
 */
public class ParkState {

    // Estado del tiempo
    private int currentStep;

    // Entidades del parque
    private final List<Tourist> allTourists;
    private final List<Dinosaur> dinosaurs;
    private final List<Worker> workers;

    // Zonas
    private final PowerPlant powerPlant;

    // Persistencia
    private final CsvWriter csvWriter;

    // Generador de números aleatorios compartido
    private final Random rng;

    // Acumuladores financieros
    private double totalRevenue;
    private double totalExpenses;

    public ParkState(List<Tourist> allTourists, List<Dinosaur> dinosaurs,
                     List<Worker> workers, PowerPlant powerPlant,
                     CsvWriter csvWriter, Random rng) {
        this.allTourists = allTourists;
        this.dinosaurs = dinosaurs;
        this.workers = workers;
        this.powerPlant = powerPlant;
        this.csvWriter = csvWriter;
        this.rng = rng;
        this.currentStep = 0;
        this.totalRevenue = 0.0;
        this.totalExpenses = 0.0;
    }

    // ── Métodos de utilidad ──────────────────────────────────────────────────

    /** Turistas que actualmente están dentro del parque */
    public List<Tourist> getActiveTourists() {
        return allTourists.stream()
            .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
            .collect(Collectors.toList());
    }

    /** Dinosaurios que están en su encierro (no escapados) */
    public long countDinosaursInEnclosure() {
        return dinosaurs.stream()
            .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
            .count();
    }

    /** Turistas que están activamente visitando el parque */
    public long countActiveTourists() {
        return getActiveTourists().size();
    }

    // ── Acumuladores financieros ─────────────────────────────────────────────

    public void addRevenue(double amount) { this.totalRevenue += amount; }
    public void addExpense(double amount) { this.totalExpenses += amount; }

    // ── Paso del tiempo ──────────────────────────────────────────────────────

    public void incrementStep() { this.currentStep++; }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getCurrentStep() { return currentStep; }
    public List<Tourist> getAllTourists() { return allTourists; }
    public List<Dinosaur> getDinosaurs() { return dinosaurs; }
    public List<Worker> getWorkers() { return workers; }
    public PowerPlant getPowerPlant() { return powerPlant; }
    public CsvWriter getCsvWriter() { return csvWriter; }
    public Random getRng() { return rng; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalExpenses() { return totalExpenses; }
}
