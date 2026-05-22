package com.axity.dinosaurpark.zone;

/**
 * Tipos de experiencia que puede ofrecer un ObservationEnclosure.
 *
 * Cada tipo tiene un rango de puntuación de satisfacción diferente:
 *   BASIC   → puntuación entre 1 y 3
 *   PREMIUM → puntuación entre 2 y 4
 *   VIP     → puntuación entre 3 y 5
 */
public enum ExperienceType {
    BASIC(1, 3),
    PREMIUM(2, 4),
    VIP(3, 5);

    private final int minScore;
    private final int maxScore;

    ExperienceType(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
}
