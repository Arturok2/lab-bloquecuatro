package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;

/**
 * Interfaz que define el CONTRATO de todas las zonas del parque.
 *
 * CONCEPTO - Interfaz:
 *   Una interfaz es un contrato: cualquier clase que la implemente GARANTIZA
 *   tener todos estos métodos. El Engine puede manejar una lista de ParkZone
 *   sin saber si es ArrivalZone, BathroomZone o cualquier otra:
 *
 *     List<ParkZone> zones = List.of(arrivalZone, hub, bathroom, enclosure);
 *     for (ParkZone z : zones) {
 *         System.out.println(z.getName() + " tiene " + z.getCurrentOccupancy() + " turistas");
 *     }
 *
 *   Esto es polimorfismo a través de interfaces.
 */
public interface ParkZone {

    /** Nombre de la zona (para logs y registros) */
    String getName();

    /** ¿Hay espacio para al menos un turista más? */
    boolean hasCapacity();

    /** Cuántos turistas hay actualmente en esta zona */
    int getCurrentOccupancy();

    /** Capacidad máxima de la zona */
    int getMaxCapacity();

    /** El turista entra a la zona */
    void enter(Tourist tourist);

    /** El turista sale de la zona */
    void exit(Tourist tourist);
}