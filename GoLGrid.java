package gameoflife;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;

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


  /**********************************************
   Shape-related functionalities
  */
  enum Shape { // common shape that are recognized
    BLOCK, BOAT, BLINKER, TOAD, GLIDER, SPACESHIP, PULSAR
  };

  private static Integer[] tuple(int x, int y) {
    return new Integer[] {x, y};
  }

  private static HashMap<GoLGrid.Shape, ArrayList<Integer[]> > liveCoordinates = new HashMap<GoLGrid.Shape, ArrayList<Integer[]> >() {
    {
      put(GoLGrid.Shape.BLOCK, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(0,1));
          add(tuple(1,0));
          add(tuple(1,1));
        }
      });
      put(GoLGrid.Shape.BOAT, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(0,1));
          add(tuple(2,1));
          add(tuple(1,2));
        }
      });
      put(GoLGrid.Shape.BLINKER, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(2,0));
        }
      });
      put(GoLGrid.Shape.TOAD, new ArrayList<Integer[]>() {
        {
          add(tuple(1,0));
          add(tuple(2,0));
          add(tuple(3,0));
          add(tuple(0,1));
          add(tuple(1,1));
          add(tuple(2,1));
        }
      });
      put(GoLGrid.Shape.GLIDER, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(2,0));
          add(tuple(0,1));
          add(tuple(1,2));
        }
      });
      put(GoLGrid.Shape.SPACESHIP, new ArrayList<Integer[]>() {
        {
          add(tuple(1,0));
          add(tuple(4,0));
          add(tuple(0,1));
          add(tuple(0,2));
          add(tuple(4,2));
          add(tuple(0,3));
          add(tuple(1,3));
          add(tuple(2,3));
          add(tuple(3,3));
        }
      });
      put(GoLGrid.Shape.PULSAR, new ArrayList<Integer[]>() {
        {
          //upper left block, hardcode this, then mirror the rest of the 3 blocks
          ArrayList<Integer[]> cornerCoordinates = new ArrayList<Integer[]>() {
            {
              add(tuple(2,0));
              add(tuple(3,0));
              add(tuple(3,1));
              add(tuple(4,1));
              add(tuple(0,2));
              add(tuple(3,2));
              add(tuple(5,2));
              add(tuple(0,3));
              add(tuple(1,3));
              add(tuple(2,3));
              add(tuple(4,3));
              add(tuple(5,3));
              add(tuple(1,4));
              add(tuple(3,4));
              add(tuple(5,4));
              add(tuple(2,5));
              add(tuple(3,5));
              add(tuple(4,5));
            }
          };
          for(Integer[] coord : cornerCoordinates) {
            add(coord);
            // upper-right
            add(tuple(12 - coord[0], coord[1]));
            // lower-left
            add(tuple(coord[0], 12 - coord[1]));
            // lower right
            add(tuple(12 - coord[0], 12 - coord[1]));
          }
        }
      });
    }
  };
  // Requirements of the size of each shape, recorded by (col, row)
  private static HashMap<GoLGrid.Shape, Integer[]> sizeConstraints = new HashMap<GoLGrid.Shape, Integer[]>() {
    {
      put(GoLGrid.Shape.BLOCK, tuple(2,2));
      put(GoLGrid.Shape.BOAT, tuple(3,3));
      put(GoLGrid.Shape.BLINKER, tuple(3,1));
      put(GoLGrid.Shape.TOAD, tuple(4,2));
      put(GoLGrid.Shape.GLIDER, tuple(3,3));
      put(GoLGrid.Shape.SPACESHIP, tuple(5,4));
      put(GoLGrid.Shape.PULSAR, tuple(13,13));
    }
  };

  // given a string, get the short hand of the shape
  private static GoLGrid.Shape shapeFromString(String input) {
    for (GoLGrid.Shape s : GoLGrid.Shape.values()) {
      if(s.name().equals(input.toUpperCase())) {
        return s;
      }
    }
    return null;
  }

  private Integer[] getStartCoordinateOfShape(GoLGrid.Shape shape) {
    // prepare the "CORNER" of the shape. which is the upper-left corner of the shape.
    // because later the coordinates of the shape needs to be off-set
    Integer[] requiredDimension = sizeConstraints.get(shape);
    int col = getColumns();
    int row = getRows();

    if(requiredDimension[0] > col || requiredDimension[1] > row) {
      System.out.println("Given shape cannot be fit into current board.");
      return null;
    }

    // this is the center of the board
    int midCol = col / 2 + 1;
    int midRow = row / 2 + 1;
    // this is half of the size of the shape
    /**
      Reason to start the shape at w / 2 - s / 2, where w is the size of the board, s is the size of the shape, is...
        ***|***x***|*** <-- the board, | is the edge of the shape, x is the center of the board and (hopefully) the shape
        |------w-------|
        |--w/2--|
           |s/2|
        |---|
          ^
          |
        w/2 - s/2 <<<<-------
    */
    int startCol = midCol - (requiredDimension[0] / 2 + 1);
    int startRow = midRow - (requiredDimension[1] / 2 + 1);
    return new Integer[] { startCol, startRow };
  }
  // return the "Error message" that may arise when deploying the shape
  public String shape(String shapeName) {
    GoLGrid.Shape shape = shapeFromString(shapeName);
    if(shape == null) {
      return "Unrecognized shape name";
    }

    Integer[] startCoordinates = getStartCoordinateOfShape(shape);
    if (startCoordinates == null) {
      return "Shape does not fit into the grid"; // the shape does not fit into the size of the board.
    }
    // coordaintes are there, proceed to clearing the grid, and populate on it.
    clear();
    ArrayList<Integer[]> shapeAliveLocation = liveCoordinates.get(shape);
    System.out.println("start coord" + startCoordinates[0] + " " + startCoordinates[1]);
    System.out.println("Grid size: " + col + " " + row);
    for(Integer[] coordinates: shapeAliveLocation) {
      setAlive(
        coordinates[0] + startCoordinates[0],
        coordinates[1] + startCoordinates[1],
        true
      );
    }
    return null;
  }
}
