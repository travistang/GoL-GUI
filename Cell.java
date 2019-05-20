package gameoflife;

public class Cell {
  public boolean isAlive = false;
  public Cell () {}
  public String toString() {
    return isAlive?"X":".";
  }
}
