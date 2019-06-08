// bridge between the GUI and the model (GoLGrid)
package gameoflife;

import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class Controller {
  // model classes
  private GoLGrid grid;
  private SimulationManager simulationManager;
  private GridDragManager gridDragManager;
  // reference to the View
  private GUI gui;

  // configuration object
  public Controller() {
    simulationManager = new SimulationManager(this);
    gridDragManager = new GridDragManager(this);
  }

  public void setGUI(GUI gui) {
    this.gui = gui;
    refreshGridDimension();
  }

  // methods for GUI
  public boolean isAlive(int col, int row) {
    return grid.isAlive(col, row);
  }

  public void setAlive(int col, int row, boolean alive) {
    grid.setAlive(col, row, alive);
  }

  // listeners
  public ComponentListener gridResizeListener() {
    return new ComponentListener() {

        public void componentResized(ComponentEvent e) {
          refreshGridDimension();
        }

        public void componentHidden(ComponentEvent e) {}

        public void componentMoved(ComponentEvent e) {}

        public void componentShown(ComponentEvent e) {}
    };
  }

  public void refreshGridDimension() {
    Dimension dim = gui.getGolGridDimension();
    resizeCellWithDiemension(dim);
  }

  public MouseAdapter cellClickListener(int col, int row, CellButton cellButton) {
    return new MouseAdapter() {
      // helper for invoking listeners from the parent
      // public void propagateEvent(MouseEvent e) {
      //   // dispatch events to higher level
      //   Component component = (Component)e.getSource();
      //   component.getParent().dispatchEvent(e);
      // }

      @Override
      public void mouseClicked(MouseEvent e) {
        boolean isCurrentlyAlive = grid.isAlive(col, row);
        grid.setAlive(col, row, !isCurrentlyAlive);
        // change the color
        Color color = Configuration.getCellColor(!isCurrentlyAlive);

        cellButton.setColor(color);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          gridDragManager.startTracking(grid.isAlive(col, row));
        }

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        gridDragManager.reportMouseOver(col, row);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          System.out.println("Mouse released");
          gridDragManager.stopTracking();
        }
      }
    };
  }


  // trigger the next from the model, and set everything for the UI
  public void next() {
    grid.next(); // trigger groundtruth on model side

    gui.setGeneration(grid.getGenerations());
    refreshGridDimension();
  }

  public ActionListener nextClickListener() {

    return new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        next();
      }
    };
  }

  public ActionListener startClickListener() {
    return new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        boolean hasSimulationStarted = simulationManager.isSimulationRunning.get();
        if(hasSimulationStarted) {
          simulationManager.stopSimulation();
        } else {
          simulationManager.startSimuation();
        }
        // and notify the gui
        gui.setSimulationRunning(!hasSimulationStarted);
      }
    };
  }

  public ChangeListener speedChangeListener(JSpinner spinner) {
    return new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
          double interval = model.getNumber().doubleValue();
          simulationManager.setSimulationInterval(
            Configuration.convertSpeedValueToLongInterval(interval)
          );
        }
    };
  }

  public ItemListener cellSizeChangeListener(JComboBox dropbox) {
    return new ItemListener(){
      @Override
      public void itemStateChanged(ItemEvent ie) {
        if(ie.getStateChange() == ItemEvent.SELECTED) { // make sure that it is called once only
          Configuration.CellSize selectedSize = (Configuration.CellSize)dropbox.getSelectedItem();
          // to prevent uneven sizing, if that would ever occur...
          synchronized(this) {
            Configuration.CELL_SIZE = selectedSize;
            refreshGridDimension();
          }
        }
      }
    };
  }

  public ItemListener shapeChangeListener(JComboBox dropbox) {
    return new ItemListener(){
      @Override
      public void itemStateChanged(ItemEvent ie) {
        if(ie.getStateChange() == ItemEvent.SELECTED) {
          GoLGrid.Shape selectedShape = (GoLGrid.Shape)dropbox.getSelectedItem();
          // to prevent uneven sizing, if that would ever occur...
          synchronized(this) {
            String errorMessage = grid.shape(selectedShape.name());
            if(errorMessage != null) {
              gui.showErrorMessage(errorMessage);
            } else {
              refreshGridDimension();
            }
          }
        }
      }
    };
  }

  public ActionListener clearListener() {
    return new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        synchronized(this) {
          grid.clear();

          simulationManager.stopSimulation();
          refreshGridDimension();
        }
      }
    };
  }
  /**
    Before giving away the manager, set the GUI so that the
  */
  public GridDragManager getGridDragManager(GUI gui) {
    gridDragManager.setGui(gui);
    return gridDragManager;
  }

  // state-inferencing methods
  private void resizeCellWithDiemension(Dimension dim) {

    Dimension cellDimension = Configuration.cellDimension();
    // now calculate how many cells should there be on each row and col respectively.
    int numCols = dim.width / cellDimension.width;
    int numRows = dim.height / cellDimension.height;

    if (grid == null) {
      grid = new GoLGrid(numCols, numRows);
    } else {
      // now set the model.
      grid.resize(numCols, numRows);

      gui.reloadGridCells(numCols, numRows);
    }

  }

}
