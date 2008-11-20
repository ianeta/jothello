/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmtothello;

/**
 *
 * @author mocksuwannatat
 */
public class GreedyThinker implements Thinker {
  
  public static final String MY_NAME = "Mr. Greedy";
  
  public String getName() {
    return MY_NAME;
  }
  
  public RowCol nextMove(char who, OBoard board) {
    RowCol ans = new RowCol();
    int best = -1;
    for (int r = 0; r < board.size(); r++) {
      for (int c = 0; c < board.size(); c++) {
        if (board.canSet(r, c, who)) {
          int score = board.calculateImmediateFlips(r, c, who);
          if (score > best) {
            ans.row = r;
            ans.col = c;
            best = score;
          }
        }
      }
    }
    // pretend like we're thinking by delaying a bit
    try {
      Thread.sleep(3);
    } catch (InterruptedException e) {
      //ignore
    }
    return ans;
  }

}
