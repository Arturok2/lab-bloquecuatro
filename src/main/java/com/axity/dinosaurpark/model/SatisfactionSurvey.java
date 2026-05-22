package com.axity.dinosaurpark.model;

/**
 * Encuesta de satisfacción completada por un turista al salir de un encierro.
 *
 * Puntuaciones por tipo de encierro:
 *   BASIC   → entre 1 y 3
 *   PREMIUM → entre 2 y 4
 *   VIP     → entre 3 y 5
 */
public record SatisfactionSurvey(
    int touristId,
    String enclosureName,
    int score       // valor entre 1 y 5
) {}