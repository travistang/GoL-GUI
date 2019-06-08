// deals with rendering issue
package gameoflife;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import javax.management.RuntimeErrorException;
import java.lang.Error;
import java.util.ArrayList;

public class GUI {

  private boolean isSimulationRunning = false; // not atomic, just to indicate the UI if the simulation is running

  Controller controller;
  // GUI components
  JFrame frame;
  Container golGridContainer;
  JLabel generationLabel;

  JButton simulationButton;
  JButton nextButton;

  ArrayList<ArrayList<CellButton> > cellButtons = new ArrayList<>();

  JComboBox<GoLGrid.Shape> shapeDropdownBox;
  int col = 0;
  int row = 0;

  public GUI(Controller controller) {
    this.controller = controller;
    initUI(controller);
  }

  private void initUI(Controller controller) {
    frame = new JFrame("Game of Life");
    // frame.addWindowListener(new WindowEventHandler());
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(1024,768);
    frame.setLayout(new BorderLayout());
    // global layout
    Container contentPane = frame.getContentPane();

    // make the grid having the ability to handle the dragging


    // init the grid panel
    golGridContainer = initGoLGrid(controller);
    GridDragManager manager = controller.getGridDragManager(this);
    golGridContainer.addMouseListener(manager);
    golGridContainer.addMouseMotionListener(manager);
    contentPane.add(golGridContainer, BorderLayout.CENTER);


    // init panel
    JPanel controlPanel = initButtonPanel(controller);
    contentPane.add(controlPanel, BorderLayout.SOUTH);


    controller.setGUI(this);

    frame.setVisible(true);
  }

  private Container initGoLGrid(Controller controller) {
    // get the width and height of
    Container container = new JPanel();
    container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    // make the grid having the ability to handle the resizing
    container.addComponentListener(controller.gridResizeListener());

    return container;
  }

  private JPanel initButtonPanel(Controller controller) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    shapeDropdownBox = new JComboBox<>(GoLGrid.Shape.values());
    shapeDropdownBox.addItemListener(controller.shapeChangeListener(shapeDropdownBox));
    panel.add(new JLabel("Shape"));
    panel.add(shapeDropdownBox);

    panel.add(Box.createHorizontalGlue());

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(controller.clearListener());
    panel.add(clearButton);

    panel.add(Box.createHorizontalGlue());

    // the next button configuration
    nextButton = new JButton("Next");
    nextButton.addActionListener(controller.nextClickListener());
    panel.add(nextButton); // Adds Button to content pane of frame
    // spacing in between
    panel.add(Box.createHorizontalGlue());

    simulationButton = new JButton("Start");
    simulationButton.addActionListener(controller.startClickListener());
    panel.add(simulationButton);

    panel.add(Box.createHorizontalGlue());

    JSpinner speedSpinner = new JSpinner(Configuration.speedSpinnerModel());
    speedSpinner.addChangeListener(controller.speedChangeListener(speedSpinner));
    panel.add(new JLabel("Simulation Speed (ticks per minute)"));
    panel.add(speedSpinner);

    panel.add(Box.createHorizontalGlue());

    JComboBox<Configuration.CellSize> cellSizeBox = new JComboBox<Configuration.CellSize>(
      Configuration.CellSize.values()
    );
    cellSizeBox.addItemListener(controller.cellSizeChangeListener(cellSizeBox));
    panel.add(new JLabel("Cell size"));
    panel.add(cellSizeBox);

    panel.add(Box.createHorizontalGlue());

    generationLabel = new JLabel("Generation 0");
    panel.add(generationLabel);

    return panel;
  }

  // methods that gives a single cell as button.
  private CellButton getCellButton(int col, int row) {
    CellButton cellButton = new CellButton();

    cellButton.setPreferredSize(Configuration.cellDimension());

    boolean isAlive = controller.isAlive(col, row);
    Color color = Configuration.getCellColor(isAlive); // get the color of a dead cell

    cellButton.setColor(color);
    MouseAdapter listener = controller.cellClickListener(col, row, cellButton);
    cellButton.addMouseListener(listener);
    cellButton.addMouseMotionListener(listener);
    return cellButton;
  }
  /**********************
    Methods for the controller to call
  */
  public void showErrorMessage(String message) {
    JOptionPane.showConfirmDialog(
      frame,
      message, "Error",
      JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION
    );
  }

  public Dimension getGolGridDimension() {
    return golGridContainer.getSize();
  }
  // when the grid does not move and you want to update the cell...
  public void repaintCells() {
    synchronized(this) {
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < col; j++) {
          CellButton cellButton = cellButtons.get(i).get(j);
          cellButton.setColor(
            Configuration.getCellColor(
              controller.isAlive(j, i)
            )
          );
        }
      }
    }
  }
  // methods that triggers reloading of the grids
  public void reloadGridCells(int col, int row) {
    if(col == this.col && row == this.row) {
      // just repaint, don't reload
      System.out.println("reload cells");
      repaintCells();
      return;
    }

    this.col = col;
    this.row = row;
    // golGridContainer = initGoLGrid();
    // frame.getContentPane().add(golGridContainer, BorderLayout.CENTER);
    synchronized(this) {
      golGridContainer.removeAll();
      cellButtons.clear();

      for (int i = 0; i < row; i++){
        ArrayList<CellButton> buttonRow = new ArrayList<>();
        for (int j = 0; j < col; j++) {
          CellButton cellButton = getCellButton(j, i);
          cellButton.setCoordinate(j, i);
          golGridContainer.add(cellButton);

          buttonRow.add(cellButton);
        }
        cellButtons.add(buttonRow);
      }
    }


    golGridContainer.revalidate();
    golGridContainer.repaint();
  }

  public void setGeneration(int generation) {
    String text = "Generation " + generation;
    generationLabel.setText(text);
  }

  public Container getGridContainer() {
    return golGridContainer;
  }

  public JFrame getFrame() {
    return frame;
  }
  public void setSimulationRunning(boolean isRunning) {
    isSimulationRunning = isRunning;
    //
    if(isRunning) {
      simulationButton.setText("Stop");
    } else {
      simulationButton.setText("Start");
    }

    nextButton.setEnabled(!isRunning);

  }
}
