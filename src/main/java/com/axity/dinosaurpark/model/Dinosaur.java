package com.axity.dinosaurpark.model;

/**
 * Clase abstracta que representa a todos los dinosaurios del parque.
 *
 * CONCEPTO - Clase abstracta:
 *   - Agrupa lo que TODOS los dinosaurios tienen en común (id, nombre, estado)
 *   - Declara métodos abstractos que CADA subclase debe implementar a su manera
 *   - No se puede hacer "new Dinosaur()" directamente — solo sus subclases concretas
 *
 * CONCEPTO - Métodos abstractos:
 *   - getDiet() → CarnivoreDinosaur devuelve "CARNIVORE", HerbivoreDinosaur "HERBIVORE"
 *   - getDangerLevel() → cada tipo tiene su propio nivel de peligro
 *   Esto es polimorfismo: el mismo método, comportamiento diferente según el tipo.
 */
public abstract class Dinosaur {

    private final int id;
    private final String name;
    private final String species;
    private DinosaurStatus status;
    private final double feedingCostPerDay;

    public Dinosaur(int id, String name, String species, double feedingCostPerDay) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.feedingCostPerDay = feedingCostPerDay;
        this.status = DinosaurStatus.IN_ENCLOSURE; // Todos empiezan en su recinto
    }

    // Métodos ABSTRACTOS — cada subclase define su propio comportamiento

    /** Devuelve la dieta del dinosaurio ("CARNIVORE" o "HERBIVORE") */
    public abstract String getDiet();

    /** Nivel de peligro de 0.0 (inofensivo) a 1.0 (extremadamente peligroso) */
    public abstract double getDangerLevel();

    //  Métodos CONCRETOS — iguales para todos los dinosaurios 

    public void escape()            { this.status = DinosaurStatus.ESCAPED;      }
    public void recapture()         { this.status = DinosaurStatus.RECAPTURED;   }
    public void returnToEnclosure() { this.status = DinosaurStatus.IN_ENCLOSURE; }

    //  Getters 

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public DinosaurStatus getStatus() { return status; }
    public double getFeedingCostPerDay() { return feedingCostPerDay; }

    @Override
    public String toString() {
        return getDiet() + " Dinosaur{id=" + id + ", name='" + name + "', status=" + status + "}";
    }
}

