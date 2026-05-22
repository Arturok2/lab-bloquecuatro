package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.monitoring.ParkMonitor;
import com.axity.dinosaurpark.persistence.CsvWriter;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.zone.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Motor de la simulación: ejecuta el loop principal.
 *
 * CONCEPTO - Loop principal (game loop / simulation loop):
 *   Cada iteración es un "step" (instante de tiempo). En cada step
 *   siempre se ejecutan las MISMAS fases en el MISMO orden:
 *
 *   A. LLEGADAS  → turistas nuevos entran al parque
 *   B. MOVIMIENTO → turistas activos visitan zonas
 *   C. TICKS     → zonas avanzan en el tiempo (baños liberan slots, planta consume energía)
 *   D. EVENTOS   → el scheduler dispara el evento programado (si hay)
 *   E. WORKERS   → guardias recapturan, técnicos reparan, se pagan salarios
 *   F. MONITOR   → se imprime el estado del parque en consola
 *
 *   El orden importa: si mueves turistas (B) antes de que lleguen (A),
 *   no habrá nadie que mover ese step.
 *
 * CONCEPTO - Creación del estado inicial:
 *   El Engine construye todos los objetos (turistas, dinosaurios, zonas, etc.)
 *   leyendo la configuración de ParkConfig. Esto es el "setup" de la simulación.
 */
public class SimulationEngine {

    private final ParkConfig config;

    // Estado del parque (pasado a eventos y al monitor)
    private ParkState state;

    // Zonas
    private ArrivalZone arrivalZone;
    private CentralHub centralHub;
    private BathroomZone bathroomZone;
    private PowerPlant powerPlant;
    private ObservationEnclosure enclosure;

    // Trabajadores separados por tipo
    private List<Guard> guards;
    private List<Technician> technicians;

    // Scheduler de eventos (determinístico con semilla)
    private EventScheduler scheduler;

    // Escritor de archivos CSV
    private CsvWriter csvWriter;

    // Generador de números aleatorios con semilla fija
    private Random rng;

    public SimulationEngine() {
        this.config = ParkConfig.getInstance();
    }

    /** Inicializa todos los objetos y ejecuta el loop de simulación */
    public void run() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  PARQUE TURÍSTICO DE DINOSAURIOS         ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        // ── SETUP ───────────────────────────────────────────────────────────
        setup();

        int totalSteps = config.getTotalSteps();
        System.out.println("▶ Iniciando simulación: " + totalSteps + " steps\n");

        // ── LOOP PRINCIPAL ───────────────────────────────────────────────────
        for (int step = 0; step < totalSteps; step++) {
            state.incrementStep();

            // A. LLEGADAS
            List<Tourist> arrived = arrivalZone.processBatch(
                config.getInt("tourist.arrivalBatchSize", 3),
                csvWriter
            );
            for (Tourist t : arrived) {
                state.addRevenue(config.getDouble("zone.enclosure.basic.price", 300.0));
            }

            // B. MOVIMIENTO DE TURISTAS (solo los que están IN_PARK)
            List<Tourist> activeTourists = state.getActiveTourists();
            for (Tourist tourist : activeTourists) {
                centralHub.visit(tourist, rng, csvWriter);
                bathroomZone.tryEnter(tourist, rng, csvWriter);
                enclosure.visit(tourist, rng, csvWriter);
            }

            // C. TICKS DE ZONAS (avanzan el tiempo)
            bathroomZone.tick();
            powerPlant.tick(rng, csvWriter);

            // D. EVENTOS (determinísticos — el scheduler decide cuál y cuándo)
            scheduler.checkForEvent(step).ifPresent(event -> event.execute(state, rng));

            // E. WORKERS
            for (Guard guard : guards) {
                guard.recaptureEscapedDinosaurs(state.getDinosaurs());
            }
            for (Technician tech : technicians) {
                tech.repairIfNeeded(powerPlant);
            }

            // Cobrar salarios de todos los trabajadores
            for (Worker worker : state.getWorkers()) {
                double salary = worker.getDailySalary();
                state.addExpense(salary);
                csvWriter.appendExpense(new ExpenseRecord(
                    step * 100L + worker.getId(),
                    "SALARIO_" + worker.getRole(),
                    salary,
                    "Salario de " + worker.getName(),
                    LocalDateTime.now()
                ));
            }

            // F. MONITOREO (básico: cada step)
            ParkMonitor.displaySnapshot(state);
        }

        // ── RESUMEN FINAL ────────────────────────────────────────────────────
        printFinalSummary();
    }

    // ── SETUP: Creación de todos los objetos ─────────────────────────────────

    private void setup() {
        long seed = config.getSeed();
        int totalSteps = config.getTotalSteps();
        String outputDir = config.getString("output.directory", "output");

        // Generador de números aleatorios con semilla fija (DETERMINISMO)
        this.rng = new Random(seed);

        // CSV
        this.csvWriter = new CsvWriter(outputDir);

        // Turistas
        List<Tourist> tourists = createTourists();

        // Dinosaurios
        List<Dinosaur> dinosaurs = createDinosaurs();

        // Zonas
        this.arrivalZone = new ArrivalZone(tourists,
            config.getDouble("zone.enclosure.basic.price", 300.0),
            config.getInt("tourist.initialCount", 30) * 2);

        this.centralHub = new CentralHub(
            config.getDouble("zone.centralHub.souvenirProbability", 0.4),
            config.getDouble("zone.centralHub.souvenirPrice", 150.0),
            100);

        this.bathroomZone = new BathroomZone(
            config.getInt("zone.bathroom.capacity", 5),
            config.getInt("zone.bathroom.useDurationSteps", 3),
            config.getDouble("zone.bathroom.spaProbability", 0.25),
            config.getDouble("zone.bathroom.spaPrice", 200.0));

        this.powerPlant = new PowerPlant(
            config.getDouble("powerPlant.initialEnergy", 100.0),
            config.getDouble("powerPlant.energyConsumptionPerStep", 3.0),
            config.getDouble("powerPlant.failureProbability", 0.05));

        List<Dinosaur> enclosureDinos = dinosaurs.subList(0,
            Math.min(3, dinosaurs.size()));
        this.enclosure = new ObservationEnclosure(
            "Encierro Principal", ExperienceType.BASIC,
            config.getDouble("zone.enclosure.basic.price", 300.0),
            enclosureDinos, 20);

        // Trabajadores
        this.guards = createGuards();
        this.technicians = createTechnicians();

        List<Worker> allWorkers = new ArrayList<>();
        allWorkers.addAll(guards);
        allWorkers.addAll(technicians);

        // Estado global
        this.state = new ParkState(tourists, dinosaurs, allWorkers, powerPlant, csvWriter, rng);

        // Scheduler de eventos (determinístico)
        this.scheduler = new EventScheduler(seed, totalSteps);

        System.out.println("✓ Setup completado:");
        System.out.println("  - " + tourists.size() + " turistas");
        System.out.println("  - " + dinosaurs.size() + " dinosaurios");
        System.out.println("  - " + guards.size() + " guardias, " + technicians.size() + " técnicos");
        System.out.println("  - Semilla: " + seed);
        System.out.println();
    }

    private List<Tourist> createTourists() {
        int count = config.getInt("tourist.initialCount", 30);
        List<Tourist> tourists = new ArrayList<>();
        String[] names = {"Ana", "Luis", "María", "Carlos", "Elena", "Jorge",
            "Sofía", "Pedro", "Laura", "Diego", "Valeria", "Miguel",
            "Isabella", "Andrés", "Camila", "Fernando", "Daniela", "Rodrigo",
            "Alejandra", "Sebastián", "Natalia", "Gabriel", "Paula", "Javier",
            "Lucia", "Tomás", "Mariana", "Felipe", "Valentina", "Emilio"};
        for (int i = 0; i < count; i++) {
            String name = i < names.length ? names[i] : "Turista" + i;
            tourists.add(new Tourist(i + 1, name));
        }
        return tourists;
    }

    private List<Dinosaur> createDinosaurs() {
        int carnivores = config.getInt("dinosaur.carnivoreCount", 4);
        int herbivores = config.getInt("dinosaur.herbivoreCount", 6);
        List<Dinosaur> list = new ArrayList<>();
        String[][] carnNames = {{"Rex", "T-Rex"}, {"Velo", "Velociraptor"},
            {"Spino", "Spinosaurus"}, {"Carno", "Carnotaurus"}};
        String[][] herbNames = {{"Brachi", "Brachiosaurus"}, {"Tri", "Triceratops"},
            {"Stego", "Stegosaurus"}, {"Anky", "Ankylosaurus"},
            {"Para", "Parasaurolophus"}, {"Diplo", "Diplodocus"}};
        for (int i = 0; i < carnivores && i < carnNames.length; i++) {
            list.add(new CarnivoreDinosaur(i + 1, carnNames[i][0], carnNames[i][1]));
        }
        for (int i = 0; i < herbivores && i < herbNames.length; i++) {
            list.add(new HerbivoreDinosaur(carnivores + i + 1, herbNames[i][0], herbNames[i][1]));
        }
        return list;
    }

    private List<Guard> createGuards() {
        int count = config.getInt("worker.guardCount", 2);
        double salary = config.getDouble("worker.guardDailySalary", 800.0);
        List<Guard> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Guard(i + 1, "Guardia " + (i + 1), salary));
        }
        return list;
    }

    private List<Technician> createTechnicians() {
        int count = config.getInt("worker.technicianCount", 1);
        double salary = config.getDouble("worker.technicianDailySalary", 1200.0);
        List<Technician> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Technician(i + 1, "Técnico " + (i + 1), salary));
        }
        return list;
    }

    private void printFinalSummary() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║           RESUMEN FINAL                  ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf("║  Ingresos totales:   $%,14.2f      ║%n", state.getTotalRevenue());
        System.out.printf("║  Gastos totales:     $%,14.2f      ║%n", state.getTotalExpenses());
        System.out.printf("║  Balance:            $%,14.2f      ║%n",
            state.getTotalRevenue() - state.getTotalExpenses());
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Archivos generados en /output:          ║");
        System.out.println("║    revenues.csv  expenses.csv  events.csv║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
