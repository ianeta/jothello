/*
 * OBoard.java
 *
 * Created on November 13, 2008, 12:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
 
package mmtothello;

/**
 * Represents an Othello board. 
 *
 * @author mocksuwannatat
 */
public class OBoard {
  
  public OBoard(int dimension) {
    dim = dimension;
    b = new char[dim][dim];
    clear();
  }
  
  public OBoard() {
    this(C.DEFAULT_DIMENSION);
  }
  
  public int count(char who) {
    switch (who) {
      case C.WHITE: return numWhites;
      case C.BLACK: return numBlacks;
      case C.EMPTY: return dim*dim - (numWhites + numBlacks);
    }
    return 0;
  }
  
  private void updateCount(char who, int howMuch) {
    if (who == C.BLACK) numBlacks += howMuch;
    else if (who == C.WHITE) numWhites += howMuch;
  }
  
  public char get(int r, int c) {
    return b[r][c];
  }
    
  public void set(int r, int c, char who) {
    updateCount(b[r][c], -1);
    updateCount(who, 1);
    b[r][c] = who;
  }
  
  public boolean canSet(int r, int c, char who) {
    if (get(r,c) != C.EMPTY) return false;
    /*
     there needs to be a direction which 'who' can score.
     i is direction 
     */
    for (int i=0; i<8; i++) {
      String line = getLine(r, c, i);
      int numWins = howManyWillFlip(who, line);
      if (numWins > 0) return true;
    }
    return false;
  }

  public int calculateImmediateFlips(int r, int c, char who) {
    if (get(r,c) != C.EMPTY) return 0;
    int ans = 0;
    for (int dir = 0; dir < 8; dir++) {
      String line = getLine(r, c, dir);
      ans += howManyWillFlip(who, line);
    }
    return ans;
  }
  
  /** Flip all required pieces once a new piece has been put at (row, col) 
   */
  public void flipAll(int row, int col) {
    clearJustFlipped();
    char who = get(row, col);
    for (int dir = 0; dir < 8; dir++) {
      String line = getLine(row, col, dir);
      int numFlips = howManyWillFlip(who, line);
      int ir = row; 
      int ic = col;
      for (int i = 0; i < numFlips; i++) {
        ir += C.DIRECTIONS[dir][0];
        ic += C.DIRECTIONS[dir][1];
        //System.out.printf("flipping %d, %d\n",ir, );
        set(ir, ic, who);
        justFlipped[ir][ic] = true;
      }
    }
  }
  
  public boolean inRange(int i) {
    return (i >= 0) && (i < dim);
  }
  
  public boolean inRange(int r, int c) {
    return inRange(r) && inRange(c);
  }
  
  private int howManyWillFlip(char who, String line) {
    char other = opponentOf(who);
    for (int i = 1; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == C.EMPTY) return 0;
      if (c == who) return i-1;
    }
    return 0;
  }
  
  /**
   * Returns the string containing the table entries starting from the point 
   * (row, col) in the direction of whichLine to the edge of the table
   *
   * @param whichLine 
   *   direction number, between 0 and 7, 0 is the UP direction, 
   *   1 is Upper-Right, and so on
   */
  public String getLine(int row, int col, int whichLine) {
    int r, c; r = row; c = col;
    int dR = C.DIRECTIONS[whichLine][0];
    int dC = C.DIRECTIONS[whichLine][1];
    String s = "";
    while (inRange(r, c)) {
      s += get(r, c);
      r += dR; 
      c += dC;
    }
    return s;
  }
    
  /**
   * Returns 8 strings representing the 8 directions from point (row, col)
   */
  public String[] getLines(int row, int col) {
    String[] result = new String[8];
    for (int dir = 0; dir < 8; dir++) {
      result[dir] = getLine(row, col, dir);
    }
    return result;
  }
  
  public char opponentOf(char who) {
    return who == C.WHITE ? C.BLACK : C.WHITE;
  } 
  
  public void clear() {
    // clear all
    for (int i=0; i<dim; i++) {
      for (int j=0; j<dim; j++) {
        b[i][j] = C.EMPTY; 
      }
    }
    clearJustFlipped();
    
    // initial four pieces
    int mid = dim/2;
    b[mid][mid] = b[mid-1][mid-1] = C.BLACK;
    b[mid][mid-1] = b[mid-1][mid] = C.WHITE;

    // initial count
    numBlacks = numWhites = 2;
  }
  
  public boolean justFlipped(int r, int c) {
    return justFlipped[r][c];
  }
  
  private void clearJustFlipped() {
    if (justFlipped == null) {
      justFlipped = new boolean[dim][dim];
    }
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        justFlipped[i][j] = false;
      }
    }
  }
  
  
  /** the board */
  private char[][] b;
  /** dimension */
  private int dim;
  private int numBlacks, numWhites;
  /** true if we just flipped it */
  private boolean[][] justFlipped = null;
  
}
