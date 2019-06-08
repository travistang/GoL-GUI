package gameoflife;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;


// class for the pane-dragging functionality
public class GridDragManager extends MouseInputAdapter {

  volatile Boolean shouldCellUnderDragBeAlive = null;
  Controller controller;
  GUI gui;

  GridDragManager(Controller controller) {
    this.controller = controller;
  }
  public void setGui(GUI gui) {
    this.gui = gui;
  }

  // common method for extracting the cell button under the cursor, or null if it does not exist
  private CellButton getCellButtonUnderCursor(MouseEvent e) {
    if(gui == null) return null;
    Container gridContainer = gui.getGridContainer();
    Point mouseLocation = e.getLocationOnScreen();
    Component component = gridContainer.getComponentAt(mouseLocation);
    if(component == null || !(component instanceof CellButton)) return null; // nothing under the cursor or it is not a cellButton
    CellButton cellButton = (CellButton)component;
    return cellButton;
  }

  // deal with the initial click, the task is to determine and set the boolean flag
  @Override
  public void mousePressed(MouseEvent e) {
    System.out.println("mouse pressed");
    CellButton cellButton = getCellButtonUnderCursor(e);
    if(cellButton == null) return;
    Integer[] coord = cellButton.getCoordinate();
    int col = coord[0];
    int row = coord[1];
    // set the flag based on the stored information of that cell button
    shouldCellUnderDragBeAlive = controller.isAlive(col, row);

  }
  // reset the flag when mouse is released
  @Override
  public void mouseReleased(MouseEvent e) {
    System.out.println("mouse released");
    shouldCellUnderDragBeAlive = null;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    System.out.println("mouse dragged");
    // find out what is under the mouse
    if(shouldCellUnderDragBeAlive == null) return; // what?

    CellButton cellButton = getCellButtonUnderCursor(e);
    if(cellButton == null) return;

    // take out the coordinates
    Integer[] coord = cellButton.getCoordinate();
    int col = coord[0];
    int row = coord[1];

    // modify the model
    controller.setAlive(col, row, shouldCellUnderDragBeAlive);
    // notify GUI to update
    controller.refreshGridDimension();
  }

  public void startTracking(boolean alive) {
    shouldCellUnderDragBeAlive = alive;
  }

  public void stopTracking() {
    shouldCellUnderDragBeAlive = null;
  }

  // method for the cellButton to report themselves being hovered by the mouse.
  public void reportMouseOver(int col, int row) {
    if(shouldCellUnderDragBeAlive != null) {
      controller.setAlive(col, row, shouldCellUnderDragBeAlive);
      controller.refreshGridDimension();
    }
  }
}
