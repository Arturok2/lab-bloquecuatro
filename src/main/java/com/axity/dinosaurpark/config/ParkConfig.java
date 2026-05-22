package com.axity.dinosaurpark.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Patrón Singleton: garantiza que park.properties se lea UNA sola vez.
 *
 * CONCEPTOS CLAVE:
 *  - Constructor private → nadie puede hacer "new ParkConfig()" desde fuera
 *  - getInstance() crea la instancia solo la primera vez (lazy initialization)
 *  - Todas las clases comparten exactamente la misma configuración
 */
public final class ParkConfig {

    // La única instancia de esta clase (null hasta que alguien llame getInstance())
    private static ParkConfig instance;

    // El objeto Properties que guarda todos los pares clave=valor del archivo
    private final Properties props;

    //  Constructor PRIVADO 
    // Nadie fuera de esta clase puede hacer "new ParkConfig()"
    private ParkConfig() {
        props = new Properties();
        // getResourceAsStream busca el archivo en el classpath (src/main/resources/)
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("park.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "No se encontró park.properties en el classpath. " +
                    "Asegúrate de que esté en src/main/resources/");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer park.properties", e);
        }
    }

    //  Punto de acceso global 
    // Si instance es null → crea la instancia. Si ya existe → devuelve la misma.
    public static ParkConfig getInstance() {
        if (instance == null) {
            instance = new ParkConfig();
        }
        return instance;
    }

    //  Métodos de lectura 

    public int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public long getSeed() {
        return Long.parseLong(props.getProperty("simulation.seed", "42").trim());
    }

    public int getTotalSteps() {
        return getInt("simulation.totalSteps", 50);
    }

    //  Solo para tests 
    // Permite resetear la instancia entre pruebas (visibilidad package/default)
    static void resetForTesting() {
        instance = null;
    }
}
