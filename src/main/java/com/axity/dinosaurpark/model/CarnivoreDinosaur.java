package com.axity.dinosaurpark.model;

/**
 * Subclase concreta de Dinosaur: representa un dinosaurio carnívoro.
 *
 * CONCEPTO - Herencia (extends):
 *   Al extender Dinosaur, CarnivoreDinosaur hereda TODOS sus campos y métodos.
 *   Solo necesita implementar los métodos abstractos que Dinosaur declaró:
 *   getDiet() y getDangerLevel(). El resto (escape(), recapture(), etc.) ya
 *   están implementados en la clase padre y se usan directamente.
 *
 *   "super(id, name, species, 500.0)" llama al constructor del padre y le pasa
 *   el costo de alimentación específico de los carnívoros.
 */
public class CarnivoreDinosaur extends Dinosaur {

    public CarnivoreDinosaur(int id, String name, String species) {
        // Llama al constructor de Dinosaur (la clase padre)
        // Los carnívoros cuestan $500 por día para alimentar
        super(id, name, species, 500.0);
    }

    @Override
    public String getDiet() {
        return "CARNIVORE";
    }

    @Override
    public double getDangerLevel() {
        // Los carnívoros son muy peligrosos: 90% de probabilidad de atacar
        return 0.9;
    }
}
