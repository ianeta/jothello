/*
 * Thinker.java
 *
 * Created on November 13, 2008, 2:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mmtothello;
 
/**
 *
 * @author mocksuwannatat
 */
public interface Thinker {
  
  public RowCol nextMove(char who, OBoard board);
  public String getName();
  public void setDepth(int newDepth); 
  
}
