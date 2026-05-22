package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Dinosaur;
import com.axity.dinosaurpark.model.SatisfactionSurvey;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Encierro de observación: los turistas ven dinosaurios y completan encuestas.
 *
 * CONCEPTO - Composición:
 *   ObservationEnclosure "tiene" dinosaurios (List<Dinosaur>) y "tiene" encuestas
 *   (List<SatisfactionSurvey>). Esto es composición: un objeto contiene otros
 *   objetos. Es una alternativa a la herencia cuando la relación es "tiene un"
 *   en lugar de "es un".
 *
 * CONCEPTO - ExperienceType y polimorfismo de datos:
 *   El tipo de experiencia (BASIC/PREMIUM/VIP) determina el precio de la entrada
 *   y el rango de puntuación de la encuesta. Usando el enum con sus campos
 *   minScore/maxScore, evitamos if/else o switch — solo consultamos los valores
 *   del enum correspondiente.
 */
public class ObservationEnclosure implements ParkZone {

    private final String name;
    private final ExperienceType type;
    private final double entryPrice;
    private final List<Dinosaur> dinosaurs;
    private final List<SatisfactionSurvey> surveys;
    private final int maxCapacity;
    private final List<Tourist> currentTourists;
    private long nextRevenueId = 1;

    public ObservationEnclosure(String name, ExperienceType type,
                                double entryPrice, List<Dinosaur> dinosaurs, int maxCapacity) {
        this.name = name;
        this.type = type;
        this.entryPrice = entryPrice;
        this.dinosaurs = dinosaurs;
        this.surveys = new ArrayList<>();
        this.maxCapacity = maxCapacity;
        this.currentTourists = new ArrayList<>();
    }

    /**
     * El turista visita el encierro: paga la entrada y completa una encuesta.
     */
    public void visit(Tourist tourist, Random rng, CsvWriter csvWriter) {
        tourist.spend(entryPrice);
        tourist.recordVisit(name);

        // Registrar ingreso
        csvWriter.appendRevenue(new RevenueRecord(
            nextRevenueId++, "ENCIERRO_" + type.name(),
            entryPrice, tourist.getId(), name, LocalDateTime.now()
        ));

        // Realizar encuesta de satisfacción
        SatisfactionSurvey survey = conductSurvey(tourist, rng);
        surveys.add(survey);
    }

    /**
     * Genera una encuesta con puntuación aleatoria dentro del rango del tipo.
     *
     * CONCEPTO - Random.nextInt(n):
     *   nextInt(max - min + 1) genera un número entre 0 y (max-min).
     *   Al sumarle min, obtenemos un número entre min y max (ambos inclusive).
     */
    public SatisfactionSurvey conductSurvey(Tourist tourist, Random rng) {
        int min = type.getMinScore();
        int max = type.getMaxScore();
        int score = min + rng.nextInt(max - min + 1);
        return new SatisfactionSurvey(tourist.getId(), name, score);
    }

    public List<SatisfactionSurvey> getSurveys() { return surveys; }
    public List<Dinosaur> getDinosaurs() { return dinosaurs; }
    public ExperienceType getType() { return type; }

    //  ParkZone 

    @Override public String getName() { return name; }
    @Override public boolean hasCapacity() { return currentTourists.size() < maxCapacity; }
    @Override public int getCurrentOccupancy() { return currentTourists.size(); }
    @Override public int getMaxCapacity() { return maxCapacity; }
    @Override public void enter(Tourist tourist) { currentTourists.add(tourist); }
    @Override public void exit(Tourist tourist) { currentTourists.remove(tourist); }
}
