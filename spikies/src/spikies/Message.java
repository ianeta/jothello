/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spikies;

import java.util.*;

/**
 *
 * @author mocksuwannatat
 */
public class Message implements Comparable {
  
  public Message(String line) {
    Scanner sc = new Scanner(line.replace("\"", "").replace(",", "\n"));
    number = sc.nextInt();
    time = sc.nextFloat();
    size = sc.nextInt(); sc.nextLine();
    src = sc.nextLine();
    dst = sc.nextLine();
    protocol = sc.nextLine();
    info = sc.nextLine();
  }
  
  public String toString() {
    return String.format("[#%d, time=%f, size=%d, src=%s, dst=%s, " +
        "protocol=\"%s\", info=\"%s\"]", 
        number, time, size, src, dst, protocol, info);
  }
  
  public int compareTo(Object o) {
    Message m = (Message) o;
    float diff = time - m.time;
    if (diff > 0) return 1;
    if (diff < 0) return -1;
    return 0;
  }
  
  int number;
  float time;
  int size; 
  String src;
  String dst;
  String protocol;
  String info;
  
}
