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
public class GreedyThinker implements Thinker {
  
  public static final String MY_NAME = "Mr. Greedy";
  private Random random = null;
  
  public String getName() {
    return MY_NAME;
  }
  
  public RowCol nextMove(char who, OBoard board) {
    RowCol ans = new RowCol();
    Vector<RowCol> lot = new Vector<RowCol>();
    int best = -1;
    for (int r = 0; r < board.size(); r++) {
      for (int c = 0; c < board.size(); c++) {
        if (board.canSet(r, c, who)) {
          int score = board.calculateImmediateFlips(r, c, who);
          if (score > best) {
            ans.row = r;
            ans.col = c;
            best = score;
            lot.clear();
            lot.add(ans);
          } else if (score == best) {
            RowCol ans2 = new RowCol();
            ans2.row = r; 
            ans2.col = c;
            lot.add(ans2);
          }
        }
      }
    }
    if (random == null) random = new Random();
    int idx = random.nextInt(lot.size());
    return lot.get(idx);
  }

}
