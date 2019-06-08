package gameoflife;
import javax.swing.*;
import java.awt.*;

public class CellButton extends JButton {
  private Color color = Configuration.COLOR_CELL_DEAD;

  private int col, row;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g.create();

    int borderWidth = Configuration.CELL_BORDER_WIDTH;
    g2.setPaint(color);
    g2.fillRect(borderWidth, borderWidth, getWidth() - 2 * borderWidth, getHeight() - 2 * borderWidth);

    g2.dispose();

  };

  public void setColor(Color color) {
    if(this.color.getRGB() == color.getRGB()) {
      return; // do not repaint if it is not updated
    }
   this.color = color;
   this.revalidate();
   this.repaint();
  }

  public void setCoordinate(int col, int row) {
    this.col = col;
    this.row = row;
  }
  public Integer[] getCoordinate() {
    return new Integer[] { col, row };
  }
}
