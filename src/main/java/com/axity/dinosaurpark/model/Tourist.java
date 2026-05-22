package com.axity.dinosaurpark.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un turista que visita el parque.
 *
 * CONCEPTO: Esta clase es un modelo — guarda datos (campos) y comportamiento
 * (métodos). Los campos id y name son final porque no cambian una vez creados.
 * El status y moneySpent sí cambian durante la simulación.
 */
public class Tourist {

    private final int id;
    private final String name;
    private TouristStatus status;
    private double moneySpent;
    private final List<String> visitedZones;

    public Tourist(int id, String name) {
        this.id = id;
        this.name = name;
        this.status = TouristStatus.WAITING; // Comienza esperando en la cola
        this.moneySpent = 0.0;
        this.visitedZones = new ArrayList<>();
    }

    //  Comportamiento 

    /** Acumula lo que el turista gasta en el parque */
    public void spend(double amount) {
        this.moneySpent += amount;
    }

    /** Registra que el turista visitó una zona */
    public void recordVisit(String zoneName) {
        this.visitedZones.add(zoneName);
    }

    //  Getters y Setters 

    public int getId() { return id; }
    public String getName() { return name; }
    public TouristStatus getStatus() { return status; }
    public void setStatus(TouristStatus status) { this.status = status; }
    public double getMoneySpent() { return moneySpent; }
    public List<String> getVisitedZones() { return visitedZones; }

    @Override
    public String toString() {
        return "Tourist{id=" + id + ", name='" + name + "', status=" + status + "}";
    }
}
