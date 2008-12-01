/*
 * C.java
 *
 * Created on November 13, 2008, 12:18 PM
 *  
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */ 
 
package mmtothello;
 
/**
 * Constants go here.
 *
 * @author mocksuwannatat
 */
public class C {
  
  public static final int DEFAULT_DIMENSION = 8;
  public static final char EMPTY = '-';
  public static final char BLACK = 'B';
  public static final char WHITE = 'W';
  public static final int TOTAL_MOVES = DEFAULT_DIMENSION * DEFAULT_DIMENSION - 3;
  public static final int MIN_GAMES_THRESHOLD = 5;

  
  public static final int[][] DIRECTIONS = { 
    {-1, 0}, 
    {-1, 1}, 
    { 0, 1},
    { 1, 1},
    { 1, 0},
    { 1,-1},
    { 0,-1},
    {-1,-1}
  };
//  public static final Class[] THINKER_CLASSES = {
//    RandomThinker.class,
//    GreedyThinker.class,
//    AnotherThinker.class
//  };
      
      
  /** no instantiation */
  private C() {
  }
  
}
