package com.axity.dinosaurpark.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Escribe los 3 archivos CSV de la simulación.
 *
 * CONCEPTO - Persistencia:
 *   Sin guardar datos en archivos, toda la información desaparece al
 *   terminar el programa. Los CSV permiten analizar la simulación después
 *   (puedes abrirlos en Excel o en cualquier hoja de cálculo).
 *
 * CONCEPTO - FileWriter(path, false):
 *   El segundo parámetro "false" significa SOBREESCRIBIR el archivo.
 *   Si fuera "true" (append), cada corrida acumularía filas sobre las anteriores.
 *   Con false, cada corrida empieza con archivos limpios.
 *
 * CONCEPTO - BufferedWriter:
 *   Envuelve a FileWriter para escribir más eficientemente.
 *   En vez de escribir al disco línea por línea (lento), agrupa las
 *   escrituras en un buffer interno y las manda al disco en lotes.
 */
public class CsvWriter {

    private final Path revenuesPath;
    private final Path expensesPath;
    private final Path eventsPath;

    public CsvWriter(String outputDirectory) {
        try {
            // Crea el directorio si no existe
            Path outputDir = Paths.get(outputDirectory);
            Files.createDirectories(outputDir);

            this.revenuesPath = outputDir.resolve("revenues.csv");
            this.expensesPath = outputDir.resolve("expenses.csv");
            this.eventsPath   = outputDir.resolve("events.csv");

            // Inicializar cada archivo con su header (SOBREESCRIBE si ya existe)
            initFile(revenuesPath, "id,type,amount,touristId,zone,timestamp");
            initFile(expensesPath, "id,type,amount,description,timestamp");
            initFile(eventsPath,   "step,eventName,description,affectedEntities,timestamp");

        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de salida: " + outputDirectory, e);
        }
    }

    /** Crea el archivo con el header. El false en FileWriter = SOBREESCRIBIR */
    private void initFile(Path path, String header) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) {
            writer.write(header);
            writer.newLine();
        }
    }

    /** Agrega una fila de ingreso al archivo revenues.csv */
    public void appendRevenue(RevenueRecord record) {
        appendLine(revenuesPath, record.toCsvLine());
    }

    /** Agrega una fila de gasto al archivo expenses.csv */
    public void appendExpense(ExpenseRecord record) {
        appendLine(expensesPath, record.toCsvLine());
    }

    /** Agrega una fila de evento al archivo events.csv */
    public void appendEvent(EventRecord record) {
        appendLine(eventsPath, record.toCsvLine());
    }

    /** Escribe una línea al final de un archivo (modo append = true) */
    private void appendLine(Path path, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en " + path + ": " + e.getMessage());
        }
    }
}
