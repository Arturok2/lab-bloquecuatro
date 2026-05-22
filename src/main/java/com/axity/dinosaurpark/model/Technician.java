package com.axity.dinosaurpark.model;

import com.axity.dinosaurpark.zone.PowerPlant;

/**
 * Técnico de mantenimiento: repara la planta eléctrica si falla.
 *
 * CONCEPTO - Responsabilidad única:
 *   El técnico SOLO sabe reparar la planta. No maneja turistas ni dinosaurios.
 *   Cada clase tiene una sola razón para cambiar.
 */
public class Technician extends Worker {

    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }

    /**
     * Repara la planta si no está operacional.
     *
     * VERSIÓN BÁSICA: el técnico puede reparar siempre que la planta esté caída.
     * (En el lab intermedio necesitará un vehículo disponible para poder reparar)
     */
    public void repairIfNeeded(PowerPlant plant) {
        if (!plant.isOperational()) {
            plant.repair();
            System.out.println("  [Técnico " + getName() + "] Reparó la planta eléctrica");
        }
    }
}

