/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmtothello;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author mocksuwannatat
 */
public class Cell extends JButton {
  private static final int OVAL_MARGIN = 5;  
  private static final Color BOARD_COLOR = 
      new Color(0f, .5f, 0f);
  private static final Color VALID_MOVE_COLOR = 
      new Color(.25f, .5f, .5f);
  private static final Color TEXT_COLOR = Color.BLACK;
//      new Color(.25f, .5f, .5f);

  public Cell(String label) {
    super(label);
    int w = 60;
    Dimension dim = new Dimension(w, w);
    setPreferredSize(dim);
    setMaximumSize(dim);
  }
  
  public void youAre(char who, 
      boolean isValidMove, 
      boolean justFlipped,
      String overlay)
  {
    this.who = who;
    this.isValidMove = isValidMove;
    this.justFlipped = justFlipped;
    this.overlay = overlay;
    repaint();
  }
  
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(isValidMove ? VALID_MOVE_COLOR : BOARD_COLOR);
    g2.fillRect(0, 0, getWidth(), getHeight());
    g2.setColor(Color.LIGHT_GRAY);
    g2.drawRect(0, 0, getWidth(), getHeight());
     
    if (who != C.EMPTY) {
      g2.setColor(who == C.BLACK ? Color.BLACK : Color.WHITE);
      g2.fillOval(OVAL_MARGIN, OVAL_MARGIN, 
          getWidth()-OVAL_MARGIN*2, getHeight()-OVAL_MARGIN*2);
    }
    
    if (justFlipped) {
      g2.setColor(Color.MAGENTA);
      int x = getWidth()/2;
      int y = getHeight()/2;
      int l = 2;
      g2.drawLine(x-l, y, x+l, y);
      g2.drawLine(x, y-l, x, y+l);
    }
    
    if (overlay != null) {
      g2.setColor(TEXT_COLOR);
      g2.drawString(overlay, 10, 20);
    }
  }
  
  private char who = C.EMPTY;
  private boolean isValidMove = false;
  private boolean justFlipped = false; 
  private String overlay;
}
