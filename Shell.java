package gameoflife;
// reading input.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;

public class Shell {
  enum Shape { // common shape that are recognized
    BLOCK, BOAT, BLINKER, TOAD, GLIDER, SPACESHIP, PULSAR
  };
  /**
    The list of coordinates that need to be marked as "alive"
    The data structure is supposed to be Shape -> list of coordaintes (therefore ArrayList<Integer[]> )
  */
  private static HashMap<Shell.Shape, ArrayList<Integer[]> > liveCoordinates = new HashMap<Shell.Shape, ArrayList<Integer[]> >() {
    {
      put(Shell.Shape.BLOCK, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(0,1));
          add(tuple(1,0));
          add(tuple(1,1));
        }
      });
      put(Shell.Shape.BOAT, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(0,1));
          add(tuple(2,1));
          add(tuple(1,2));
        }
      });
      put(Shell.Shape.BLINKER, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(2,0));
        }
      });
      put(Shell.Shape.TOAD, new ArrayList<Integer[]>() {
        {
          add(tuple(1,0));
          add(tuple(2,0));
          add(tuple(3,0));
          add(tuple(0,1));
          add(tuple(1,1));
          add(tuple(2,1));
        }
      });
      put(Shell.Shape.GLIDER, new ArrayList<Integer[]>() {
        {
          add(tuple(0,0));
          add(tuple(1,0));
          add(tuple(2,0));
          add(tuple(0,1));
          add(tuple(1,2));
        }
      });
      put(Shell.Shape.SPACESHIP, new ArrayList<Integer[]>() {
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
      put(Shell.Shape.PULSAR, new ArrayList<Integer[]>() {
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
  private static HashMap<Shell.Shape, Integer[]> sizeConstraints = new HashMap<Shell.Shape, Integer[]>() {
    {
      put(Shell.Shape.BLOCK, tuple(2,2));
      put(Shell.Shape.BOAT, tuple(3,3));
      put(Shell.Shape.BLINKER, tuple(3,1));
      put(Shell.Shape.TOAD, tuple(4,2));
      put(Shell.Shape.GLIDER, tuple(3,3));
      put(Shell.Shape.SPACESHIP, tuple(5,4));
      put(Shell.Shape.PULSAR, tuple(13,13));
    }
  };

  private static Integer[] getStartCoordinateOfShape(Shell.Shape shape) {
    // prepare the "CORNER" of the shape. which is the upper-left corner of the shape.
    // because later the coordinates of the shape needs to be off-set
    Integer[] requiredDimension = sizeConstraints.get(shape);
    int col = grid.getColumns();
    int row = grid.getRows();
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

  public static Grid grid;

  private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  private static final HashMap<String, Integer> paramCount = new HashMap<String, Integer>() {
    {
      put("NEW", 3);
      put("ALIVE", 3);
      put("DEAD", 3);
      put("GENERATE", 1);
      put("PRINT", 1);
      put("CLEAR", 1);
      put("RESIZE", 3);
      put("SHAPE", 2);
      put("HELP", 1);
      put("QUIT", 1);
    }
  };

  // givem an abbreviation or complete command, return the 'capitalized' corresponding command
  // if no such coorespondance found, null is returned
  private static String getMainCommand(String command) {
    Set<String> recognizableCommands = paramCount.keySet();

    for (String candidateCommand: recognizableCommands) {
      if(candidateCommand.startsWith(command.toUpperCase())) {
        return candidateCommand;
      }
    }

    // if the for loop has passed, that means the given command is not recognizable
    return null;
  }
  // given a string, get the short hand of the shape
  private static Shell.Shape shapeFromString(String input) {
    for (Shell.Shape s : Shell.Shape.values()) {
      if(s.name().equals(input.toUpperCase())) {
        return s;
      }
    }
    return null;
  }

  private static void print() {
    if(grid == null) return;
    System.out.println(grid.toString());
  }
  private static boolean isAddressValid(int col, int row) {
    if(grid == null) return false;
    return col >= 0 && col < grid.getColumns() && row >= 0 && row < grid.getRows();
  }
  private static boolean parseCommand(String command) {
    String[] parts = command.split(" ");
    // prepare commands
    // maximum number of params is 2
    Integer[] params = new Integer[2];

    String fullCommand = getMainCommand(parts[0]);
    if (fullCommand == null) {
       // command not recognized.
       System.out.println("Error! Meldung");
       return true; // continue parsing command
    }
    // check and parse params
    int expectedArgs = paramCount.get(fullCommand);
    if (parts.length != expectedArgs) {
      System.out.println("Invalid number of arguments");
      return true;
    }
    // special treatment on "SHAPE", because it doesn't take integer, but string
    if(fullCommand.equals("SHAPE")) {
      shape(parts[1]);
      return true;
    }
    // for the rest, turn all parts to integer, and do stuff accordingly.
    try {
      for(int i = 1; i < expectedArgs; i++) {
        Integer num = Integer.parseInt(parts[i]);
        params[i - 1] = num;  // params i - 1 because array is 0-based
      }
    } catch(NumberFormatException e) {

      System.out.println("Invalid number given.");
      return true;
    } catch (ArrayIndexOutOfBoundsException ae) {
      // what!?
      System.out.println("Internal Error.");
      return true;
    }
    // check if the grid is initialized before some commands
    if( grid == null && !(Arrays.asList("QUIT,NEW,HELP".split(",")).indexOf(fullCommand) > -1)) {
      System.out.println("Game is not initialized.");
      return true;
    }
    // do according to what the command says.
    switch(fullCommand) {
      case "QUIT":
        System.out.println("bye");
        return false; // should not continue;

      case "NEW":
        if(params[0] <= 0 || params[1] <= 0) {
          System.out.println("Invalid coordinates");
          return true;
        }
        grid = new GoLGrid(params[0], params[1]);
        break;
      case "ALIVE":
        if(!isAddressValid(params[0], params[1])) {
          System.out.println("Invalid address");
          return true;
        }
        grid.setAlive(params[0], params[1], true);
        break;
      case "DEAD":
        if(!isAddressValid(params[0], params[1])) {
          System.out.println("Invalid address");
          return true;
        }
        grid.setAlive(params[0], params[1], false);
        break;
      case "CLEAR":
        grid.clear();
        System.out.println("Game reset");
        print();
        break;
      case "PRINT":
        print();
        break;
      case "RESIZE":
        if(params[0] <= 0 || params[1] <= 0) {
          System.out.println("Invalid coordinates");
          return true;
        }
        // check if the given size is the same
        if(params[0] == grid.getColumns() && params[1] == grid.getRows()){
          System.out.println("Please enter a different size than the current game board.");
          return true;
        }
        grid.resize(params[0], params[1]);
        print();
        break;

      case "HELP":
        System.out.println("Some helpful text.");
        break;

      case "GENERATE":
        grid.next();
        System.out.println("Generation: " + grid.getGenerations());
        break;
    }
    return true; // should continue for all other cases
  }
  /**
    Functional stuff.
  */
  private static String readLine() throws IOException {
    String name = reader.readLine();
    return name;
  }
  private static Integer[] tuple(int x, int y) {
    return new Integer[] {x, y};
  }
  /**
    Requirements:
      - Clear is called before anything.
      - Patern is centered.
      - At least those written on the draft must be implemented.
  */
  private static void shape(String shapeName) {
    grid.clear();

    Shell.Shape shape = shapeFromString(shapeName);
    if(shape == null) {
      System.out.println("Unrecognized shape name");
      return;
    }

    Integer[] startCoordinates = getStartCoordinateOfShape(shape);
    if (startCoordinates == null) {
      return; // the shape does not fit into the size of the board.
    }
    // coordaintes are there, proceed to clearing the grid, and populate on it.
    grid.clear();
    ArrayList<Integer[]> shapeAliveLocation = liveCoordinates.get(shape);

    for(Integer[] coordinates: shapeAliveLocation) {
      grid.setAlive(
        coordinates[0] + startCoordinates[0],
        coordinates[1] + startCoordinates[1],
        true
      );
    }

  }
  public static void main(String args[]) {

    while(true) {
      try {
        System.out.print("gol> ");
        String command = readLine();
        boolean shouldContinue = parseCommand(command);
        if(!shouldContinue)  {
          break;
        }
      } catch (Exception e) { }

    }
  }
}
