package gameoflife;
import java.util.ArrayList;
import java.util.Collection;

public class GoLGrid implements Grid {
  private ArrayList< ArrayList<Cell> > grid;
  private int col, row;
  private int generations = 0;

  // Grid construction methods.
  private void addRow() {
    ArrayList<Cell> column = new ArrayList<Cell>();
    for (int i = 0; i < col; i++) {
      column.add(new Cell());
    }
    grid.add(column);
  }

  private boolean isAddressValid(int col, int row) {
    return !(col < 0 || col >= this.col || row < 0 || row >= this.row);
  }

  private Cell cellAt(int col, int row) {
    if(!isAddressValid(col, row)) return null;
    return grid.get(row).get(col);
  }

  private int numAliveNeighbours(int col, int row) {
    int count = 0;
    for (int di = -1; di <= 1; di++) {
      for(int dj = -1; dj <= 1; dj++) {
        if(di == 0 && dj == 0) continue; // the cell itself doens't count.
        Cell cell = cellAt(col + di, row + dj);
        // increment the count if a neighbour is found and that it is alive
        if(cell != null && cell.isAlive) count++;
      }
    }
    return count;
  }

  // compute the life or death of a particular cell on the location.
  // true means this cell is life this round; false otherwise.
  private boolean cellStatusNextRound(int col, int row) {
    int numAlive = numAliveNeighbours(col, row);
    Cell cell = cellAt(col, row);
    // the life -> life situation
    if(cell.isAlive && (numAlive == 2 || numAlive == 3)) {
      return true;
    }
    // the death -> life situation
    if(!cell.isAlive && (numAlive == 3)) {
      return true;
    }
    // the cell dies in the next round otherwise.
    return false;
  }

  public int getColumns() {
    return col;
  }

  public int getRows() {
    return row;
  }
  public int getGenerations() {
    return generations;
  }
  /**
    Strategy: generate the new grid, copy status from the old one, and replace the reference.
  */
  public void resize(int cols, int rows) {
    ArrayList< ArrayList<Cell> > newGrid = new ArrayList< ArrayList<Cell> >();
    for (int i = 0; i < rows; i++) {
      ArrayList<Cell> column = new ArrayList<Cell>();
      for (int j = 0; j < cols; j++) {
        Cell cell = new Cell();
        cell.isAlive = isAlive(j,i); // copy status of the old count.
        column.add(cell);
      }
      newGrid.add(column);
    }
    // replace reference
    grid = newGrid;
    // .. and the marked dimensions
    col = cols;
    row = rows;
  }

  public Collection<Cell> getPopulation() {
    ArrayList<Cell> collection = new ArrayList<Cell>();
    for (ArrayList<Cell> row : grid) {
      for(Cell cell: row) {
        collection.add(cell);
      }
    }
    return collection;
  }

  public void setAlive(int col, int row, boolean alive) {
    Cell cell = cellAt(col, row);
    if(cell == null) return;
    cell.isAlive = alive;
  }

  public boolean isAlive(int col, int row) {
    Cell cell = cellAt(col, row);
    if(cell == null) return false; // out of bound
    return cell.isAlive;
  }

  public void clear() {
    for (Cell cell: getPopulation()) {
      cell.isAlive = false;
    }
    generations = 0;
  }

  public void next() {
    // the default primitive boolean is false.
    boolean shouldCellSurviveNextRound[][] = new boolean[row][col];
    // evaluate whether the cell on each location should be alive next round
    for (int i = 0; i < row; i++) {
      for(int j = 0; j < col; j++) {
        shouldCellSurviveNextRound[i][j] = cellStatusNextRound(j, i);
      }
    }
    // then apply it to the grid.
    for(int i = 0; i < row; i++) {
      for(int j = 0; j < col; j++) {
        setAlive(j, i, shouldCellSurviveNextRound[i][j]);
      }
    }
    // increment the generation count
    generations++;
  }

  public String toString() {
    String rep = "";
    for (ArrayList<Cell> row: grid) {
      for(Cell cell: row) {
        rep += cell.toString();
      }
      rep += '\n'; // new line for a new row.
    }
    return rep;
  }
  public GoLGrid(int col, int row) {
    this.col = col;
    this.row = row;
    grid = new ArrayList<ArrayList<Cell> >();
    for(int i = 0 ; i < row; i++) {
      addRow();
    }
  }
}
