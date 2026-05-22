package com.axity.dinosaurpark.model;

/**
 * Subclase concreta de Dinosaur: representa un dinosaurio herbívoro.
 *
 * Herbívoros son mucho menos peligrosos que los carnívoros
 * y también son más baratos de mantener.
 */
public class HerbivoreDinosaur extends Dinosaur {

    public HerbivoreDinosaur(int id, String name, String species) {
        // Herbívoros cuestan $200 por día (menos que los carnívoros)
        super(id, name, species, 200.0);
    }

    @Override
    public String getDiet() {
        return "HERBIVORE";
    }

    @Override
    public double getDangerLevel() {
        // Los herbívoros son poco peligrosos: solo 20% de probabilidad de atacar
        return 0.2;
    }
}

