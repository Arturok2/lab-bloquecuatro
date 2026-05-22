package com.axity.dinosaurpark.model;

/**
 * Clase abstracta base para todos los trabajadores del parque.
 *
 * CONCEPTO - Polimorfismo:
 *   El Engine puede tener una lista "List<Worker>" con guardias y técnicos
 *   mezclados, y cobrar el salario de todos con un solo bucle:
 *
 *     for (Worker w : workers) {
 *         totalExpenses += w.getDailySalary();
 *     }
 *
 *   Si no ponemos herencia, necesitaría listas separadas para cada tipo de trabajador.
 */
public abstract class Worker {

    private final int id;
    private final String name;
    private final double dailySalary;

    public Worker(int id, String name, double dailySalary) {
        this.id = id;
        this.name = name;
        this.dailySalary = dailySalary;
    }

    // Cada subclase dice qué rol cumple
    public abstract String getRole();

    public int getId() { return id; }
    public String getName() { return name; }
    public double getDailySalary() { return dailySalary; }

    @Override
    public String toString() {
        return getRole() + "{id=" + id + ", name='" + name + "'}";
    }
}
