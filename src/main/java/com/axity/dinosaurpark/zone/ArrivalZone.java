package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Zona de llegada: gestiona la cola de turistas y vende boletos.
 *
 * CONCEPTO - Queue (Cola):
 *   Una cola sigue el principio FIFO (First In, First Out): el primero
 *   en llegar es el primero en ser atendido. Igual que una fila real.
 *   LinkedList implementa la interfaz Queue en Java.
 *
 * CONCEPTO - Lotes (batch):
 *   En lugar de procesar turista por turista, procesamos de a batchSize
 *   por step. Esto simula que la taquilla atiende a varios a la vez.
 */
public class ArrivalZone implements ParkZone {

    private static final String NAME = "Zona de Llegada";

    private final Queue<Tourist> waitingQueue;
    private int currentOccupancy;
    private final int maxCapacity;
    private final double ticketPrice;
    private long nextTicketId = 1;

    public ArrivalZone(List<Tourist> allTourists, double ticketPrice, int maxCapacity) {
        this.waitingQueue = new LinkedList<>(allTourists); // Todos entran en la cola
        this.ticketPrice = ticketPrice;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = 0;
    }

    /**
     * Procesa un lote de turistas: los saca de la cola, les vende el boleto
     * y los marca como IN_PARK.
     *
     * @param batchSize  cuántos turistas procesar en este step
     * @param csvWriter  para registrar los ingresos por boletos
     * @return lista de turistas que acaban de entrar al parque
     */
    public List<Tourist> processBatch(int batchSize, CsvWriter csvWriter) {
        List<Tourist> arrived = new ArrayList<>();

        for (int i = 0; i < batchSize && !waitingQueue.isEmpty(); i++) {
            Tourist tourist = waitingQueue.poll(); // Saca el primero de la cola

            // Vender boleto
            tourist.spend(ticketPrice);
            tourist.setStatus(TouristStatus.IN_PARK);
            tourist.recordVisit(NAME);
            currentOccupancy++;

            // Registrar ingreso en CSV
            RevenueRecord record = new RevenueRecord(
                nextTicketId++,
                "TICKET",
                ticketPrice,
                tourist.getId(),
                NAME,
                LocalDateTime.now()
            );
            csvWriter.appendRevenue(record);

            arrived.add(tourist);
        }

        return arrived;
    }

    public boolean hasMoreTourists() {
        return !waitingQueue.isEmpty();
    }

    //  ParkZone 

    @Override public String getName() { return NAME; }
    @Override public boolean hasCapacity() { return currentOccupancy < maxCapacity; }
    @Override public int getCurrentOccupancy() { return currentOccupancy; }
    @Override public int getMaxCapacity() { return maxCapacity; }

    @Override
    public void enter(Tourist tourist) {
        currentOccupancy++;
    }

    @Override
    public void exit(Tourist tourist) {
        if (currentOccupancy > 0) currentOccupancy--;
    }
}
