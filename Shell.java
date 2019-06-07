package gameoflife;

// the entry point of the program, that sets everything up.
public class Shell {

   public static void main(String args[]) {

     Controller controller = new Controller();
     GUI gui = new GUI(controller);
   }
}
