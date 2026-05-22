package com.axity.dinosaurpark.monitoring;

import com.axity.dinosaurpark.simulation.ParkState;

/**
 * Panel de control de la simulación: imprime el estado actual en consola.
 *
 * CONCEPTO - Clase con solo métodos estáticos:
 *   ParkMonitor no necesita estado propio — solo muestra el estado del parque.
 *   Por eso todos sus métodos son "static": se llaman como ParkMonitor.displaySnapshot()
 *   sin necesitar crear un objeto (no se hace "new ParkMonitor()").
 *
 * CONCEPTO - ¿Por qué es útil el monitor?
 *   Sin el monitor la simulación correría en silencio y no sabríamos qué pasa.
 *   El monitor permite depurar: si los ingresos son 0, puedes ver si los turistas
 *   están llegando, si la planta falla todo el tiempo, etc.
 */
public class ParkMonitor {

    // Evitar que alguien haga "new ParkMonitor()" — no tiene sentido
    private ParkMonitor() {}

    /**
     * Imprime un snapshot del estado actual del parque.
     *
     * Se llama al final de cada step desde el SimulationEngine.
     * En el lab intermedio, solo se llamará cada N steps.
     */
    public static void displaySnapshot(ParkState state) {
        System.out.println("┌─── Step " + String.format("%3d", state.getCurrentStep()) + " ──────────────────────────────────┐");
        System.out.printf("│  👥 Turistas en el parque:  %-5d                    │%n",
            state.countActiveTourists());
        System.out.printf("│  🦕 Dinosaurios en recinto: %-5d                    │%n",
            state.countDinosaursInEnclosure());
        System.out.printf("│  ⚡ Energía disponible:    %5.1f%%                   │%n",
            state.getPowerPlant().getEnergyLevel());
        System.out.printf("│  💰 Ingresos acumulados: $%,10.2f              │%n",
            state.getTotalRevenue());
        System.out.printf("│  📉 Gastos acumulados:   $%,10.2f              │%n",
            state.getTotalExpenses());
        System.out.println("└──────────────────────────────────────────────────────┘");
    }
}
