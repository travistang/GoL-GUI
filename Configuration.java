package gameoflife;
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

// data class for holding all possible configurations
public class Configuration {
  enum CellSize {
    SMALL, MEDIUM, LARGE
  }


  public static final HashMap<Configuration.CellSize, Dimension> cellSizeDict = new HashMap<Configuration.CellSize, Dimension>() {{
    put(Configuration.CellSize.SMALL, new Dimension(15, 15));
    put(Configuration.CellSize.MEDIUM, new Dimension(25, 25));
    put(Configuration.CellSize.LARGE, new Dimension(35, 35));
  }};

  public static int CELL_BORDER_WIDTH = 1;
  public static Color COLOR_CELL_ALIVE = new Color(255, 255, 0);
  public static Color COLOR_CELL_DEAD = new Color(128, 128, 128);

  // values that are supposed to be changed
  public static CellSize CELL_SIZE = Configuration.CellSize.SMALL;

  // inferencing methods
  public static Dimension cellDimension() {
    return cellSizeDict.get(CELL_SIZE);
  }

  public static Color getCellColor(boolean isAlive) {
    return isAlive?COLOR_CELL_ALIVE:COLOR_CELL_DEAD;
  }
  /**
    Get the spinner model for the speed spinner.
    unit is TIMES PER MINUTE
  */
  public static SpinnerNumberModel speedSpinnerModel() {
    return new SpinnerNumberModel(
      60, 1, 60 * 4, 1
    );
  }

  public static long convertSpeedValueToLongInterval(double value) {
    // number of ticks per second?
    double numTicksPerSecond = value / 60;
    return Math.round(1000 / numTicksPerSecond);
  }

  public static long getDefaultSpeedInterval() {
    return convertSpeedValueToLongInterval(
      speedSpinnerModel().getNumber().doubleValue()
    );
  }
}
