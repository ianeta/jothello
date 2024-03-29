/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmtothello;

import java.util.*;

/**
 *
 * @author mocksuwannatat
 */
public class AnotherThinker implements Thinker {
  
  public static final String MY_NAME = "Mr. Another";
  private Random random = null;
  
  public AnotherThinker() {
    random = new Random();
  }
  
  public String getName() {
    return MY_NAME;
  }
  
  public RowCol nextMove(char who, OBoard board) {
    Vector<RowCol> moves = new Vector<RowCol>();
    for (int r = 0; r < board.size(); r++) {
      for (int c = 0; c < board.size(); c++) {
        if (board.canSet(r, c, who)) {
          RowCol thisMove = new RowCol();
          thisMove.col = c;
          thisMove.row = r;
          moves.add(thisMove);
        }
      }
    }
    int idx = random.nextInt(moves.size());
    return moves.get(idx);
  }
  
  public void setDepth(int newDepth) {
//    maxDepth = newDepth;
  }

}
