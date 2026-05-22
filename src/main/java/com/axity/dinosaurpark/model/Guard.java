package com.axity.dinosaurpark.model;

import java.util.List;

/**
 * Guardia de seguridad: recaptura dinosaurios escapados.
 *
 * CONCEPTO - Herencia concreta:
 *   Guard extiende Worker e implementa getRole() con "GUARD".
 *   Su responsabilidad única: manejar dinosaurios fugados.
 */
public class Guard extends Worker {

    public Guard(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "GUARD";
    }

    /**
     * Recorre todos los dinosaurios y recaptura los que hayan escapado.
     *
     * CONCEPTO - Iteración y polimorfismo:
     *   La lista puede contener CarnivoreDinosaur y HerbivoreDinosaur mezclados.
     *   El guardia no necesita saber qué tipo es — solo le importa el status.
     */
    public void recaptureEscapedDinosaurs(List<Dinosaur> dinosaurs) {
        for (Dinosaur d : dinosaurs) {
            if (d.getStatus() == DinosaurStatus.ESCAPED) {
                d.returnToEnclosure();
                System.out.println("  [Guardia " + getName() + "] Recapturó a " + d.getName());
            }
        }
    }
}
