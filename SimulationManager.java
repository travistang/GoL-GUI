package gameoflife;
import java.util.concurrent.atomic.AtomicBoolean;
// another data class holding everything related to the simulation
public class SimulationManager {
  private Controller controller;

  private long simulationInterval = Configuration.getDefaultSpeedInterval();

  public final AtomicBoolean isSimulationRunning = new AtomicBoolean(false);

  private Thread simulationThread;
  SimulationManager(Controller controller) {
    this.controller = controller;
  }

  public void setSimulationInterval(long simulationInterval) {
    this.simulationInterval = simulationInterval;
  }

  public void stopSimulation() {
    isSimulationRunning.set(false);
    simulationThread = null;
  }

  public void startSimuation() {
    if(simulationThread != null || isSimulationRunning.get()) return;

    isSimulationRunning.set(true);

    simulationThread = new Thread() {
      public void run() {
        while(isSimulationRunning.get()) {
          controller.next();

          try {
            Thread.sleep(simulationInterval);
          } catch ( InterruptedException e) {
            stopSimulation();
          }

        }
      }
    };

    simulationThread.start();
  }

  public long getSimulationInterval() {
    return simulationInterval;
  }


}
