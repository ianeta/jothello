/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spikies;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author mocksuwannatat
 */
public class Node {

  public Node(Main main, String name) {
    this.main = main;
    this.name = name;
    location = new Point();
    Random r = new Random(name.hashCode());
    location.x = r.nextInt(main.getPanelWidth());
    location.y = r.nextInt(main.getPanelHeight());
    messages = new PriorityQueue<Message>();
    hitAreas = new Vector<Shape>();
    tips = new Vector<Tipster>();
    numOutSet = 0;
    initPhysics();
  }
  
  private void initPhysics() {
    velocity = new Point();
    acceleration = new Point();
    netforce = new Point();
    velocity.x = velocity.y = 
        acceleration.x = acceleration.y = 
        netforce.x = netforce.y = 
        mass = 0;
  }
  
  public synchronized void physics_calculate_netforce() {
    if (!isActive()) return;
    netforce.zero();
//    netforce.x = 0;
//    netforce.y = 1000; 
    for (Message msg: messages) {
      String otherNodeName = msg.src.equals(name) ? msg.dst : msg.src;
      Node otherNode = main.findNode(otherNodeName);
      addSpringForceWithNode(otherNode, msg);
    }
    for (int i = 0; i < main.nodes.size(); i++) {
      Node node = main.nodes.get(i);
      if (node != this && node.isActive()) {
        addRepelForceWithNode(node);
      }
    }
    addRepelForceWithBorders();
  }
  
  public void addRepelForceWithBorders() {
    //TODO
    float x = location.x;
    float y = location.y;
    float w = main.getPanelWidth();
    float h = main.getPanelHeight();
    addRepelForceWithLocation(new Point(x, 0));
    addRepelForceWithLocation(new Point(x, h));
    addRepelForceWithLocation(new Point(0, y));
    addRepelForceWithLocation(new Point(w, y));
  }
  
//  public void addRepelForceWithNode_old(Node otherNode) {
//    //TODO
////    float fx = calculate_repel_force(location.x, otherNode.location.x);
////    float fy = calculate_repel_force(location.y, otherNode.location.y);
////    netforce.addBy(fx, fy);
//  }
//  
//  private float calculate_repel_force(float myX, float hisX) {
//    float fx;
//    float k = 1;//.0001f;
//    float dx = myX - hisX;
//    if (dx == 0) fx = 100; 
//    else fx = k/(dx*dx);
//    if (dx < 0) fx *= -1;
//    return fx;
//  }
  
  private float min(float a, float b) {
    return a < b ? a : b;
  }
  
  private float max(float a, float b) {
    return a > b ? a : b;
  }
  
  public void addRepelForceWithLocation(Point loc2) {
    Point loc1 = location; 
    float repelConst = main.NODES_REPEL_FACTOR; 
    float dist = loc1.distWith(loc2);
    dist = max(dist, 0.1f);
    float forceRepel = -repelConst / (dist*dist);
    Point thisForce = loc1.getRayToward(loc2);
    thisForce.makeLength(forceRepel);
    netforce.addBy(thisForce);
  }
  
  public void addRepelForceWithNode(Node otherNode) {
    addRepelForceWithLocation(otherNode.location);
  }
//  public void addRepelForceWithNode(Node otherNode) {
//    Point loc1, loc2;
//    loc1 = location; loc2 = otherNode.location;
//    float repelConst = main.NODES_REPEL_FACTOR; 
//    float dist = loc1.distWith(loc2);
//    dist = max(dist, 0.1f);
//    float forceRepel = -repelConst / (dist*dist);
//    Point thisForce = loc1.getRayToward(loc2);
//    thisForce.makeLength(forceRepel);
//    netforce.addBy(thisForce);
//  }
  
  
  public void addSpringForceWithNode(Node otherNode, Message msg) {
    Point loc1, loc2;
    loc1 = location; loc2 = otherNode.location;
    float dist = loc1.distWith(loc2);
    float x = dist - main.SPRING_RESTING_DISTANCE;
    float forceToward = main.SPRING_CONSTANT * x; 
    Point thisForce = loc1.getRayToward(loc2);
    thisForce.makeLength(forceToward);
    netforce.addBy(thisForce);
  }
  
  public void addSpringForceWithNode_old(Node otherNode, Message msg) {
    float fx = calculate_spring_force(location.x, otherNode.location.x, 
        msg.size);
    float fy = calculate_spring_force(location.y, otherNode.location.y, 
        msg.size);
    netforce.x += fx;
    netforce.y += fy;
  }
  
  private float calculate_spring_force(float myX, float hisX, float bytes) {
    float dx, abs_dx, abs_x, abs_fx, fx;
    dx = hisX - myX;
    abs_dx = Math.abs(dx);
    abs_x = abs_dx - main.SPRING_RESTING_DISTANCE;
    //TODO: factor in the size of message as spring constant?
    abs_fx = abs_x * main.SPRING_CONSTANT;
    fx = dx > 0 ? abs_fx : -abs_fx;
    return fx;
  }
  
  public void physics_time_forward(long delta_t) {
    if (!isActive()) return;
    if (mass <= 0) return;
    float dt = delta_t / main.TIME_FACTOR;
    
    acceleration.x = netforce.x / mass;
    acceleration.y = netforce.y / mass;
    velocity.multBy(main.DAMPING_FACTOR, main.DAMPING_FACTOR);
    velocity.x += acceleration.x * dt;
    velocity.y += acceleration.y * dt;
    
    if (velocity.size() > main.SPEED_LIMIT) {
      velocity.makeLength(main.SPEED_LIMIT/2f);
      System.out.printf("Speed limite enforced on %s\n", toString());
    }
    
    location.x += velocity.x * dt;
    location.y += velocity.y * dt;
   
    // border check 
    if (location.x >= main.getPanelWidth()) {
      location.x = main.getPanelWidth()-1;
//      velocity.zero();
    }
    if (location.y >= main.getPanelHeight()) {
      location.y = main.getPanelHeight()-1;
//      velocity.zero();
    }
    if (location.x <= 0) {
      location.x = 1;
//      velocity.zero();
    }
    if (location.y <= 0) {
      location.y = 1;
//      velocity.zero();
    }
    
  }
  
  public void resetData() {
    hitAreas.clear();
    tips.clear();
    numOutSet = 0;
    messages.clear();
    initPhysics();
  }
  
  public boolean isActive() {
    return !messages.isEmpty();
  }
  
  private synchronized void sendsOrReceivesMessage(Message message) {
    main.latestTime = message.time;
    messages.add(message);
    mass += message.size / main.BYTES_PER_KG; 
    removeOldMessages();
  }
  
  private boolean tooOld(Message msg) {
    return (main.latestTime - msg.time) > main.DEFAULT_T;
  }
  
  public void removeOldMessages() {
    if (messages.isEmpty()) return;
    Message msg = messages.peek();
    int count = 0;
    while (msg != null && tooOld(msg)) { 
      count ++;
      messages.remove();
      mass -= msg.size / main.BYTES_PER_KG;
      msg = messages.peek();
    }
//    if (count > 0) {
//      System.out.printf("node %s removed %d messages.\n", name, count);
//    }
//    System.out.printf("Node %s now has %d messages.\n", name, messages.size());
  }
  
  public void sendsMessage(Message message) {
    sendsOrReceivesMessage(message);
  }
  
  public void receivesMessage(Message message) {
    sendsOrReceivesMessage(message);
  }
  
  public void drawNetForce(Graphics2D g2) { 
    if (!isActive()) return;
    g2.setColor(Color.RED);
    g2.draw(
        new Line2D.Float(
        location.x, location.y, 
        location.x + netforce.x, location.y + netforce.y));
  }
  
  public void drawOutgoingEdges(Graphics2D g2) {
    if (!isActive()) return;
    
    float dotSize = 10;
    float x, y, w, h;
    String text = name;
    Font font = g2.getFont().deriveFont(8f);
    g2.setFont(font);
    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();
    w = textWidth + 2;
    h = textHeight + 2;
    x = location.x - w/2;
    y = location.y + dotSize/2f;
//    float tx = x + 1;
//    float ty = y + textHeight - 1;
//    float arc = 5;
//    RoundRectangle2D.Float behindText = 
//        new RoundRectangle2D.Float(x, y, w, h, arc, arc);
    //drawNetForce(g2);

    // draw spikes
    Set<String> outSet = getSetOfReceivers();
    numOutSet = outSet.size();
    y = location.y + dotSize/2f + textHeight + 1;
    drawTheseOutMessage(g2, 0, y, outSet);
  }
  
  public void drawNode(Graphics2D g2) {
    if (!isActive()) return;
    
    float dotSize = 10;
    float x, y, w, h;
    String text = name;
    Font font = g2.getFont().deriveFont(8f);
    g2.setFont(font);
    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();
    w = textWidth + 2;
    h = textHeight + 2;
    x = location.x - w/2;
    y = location.y + dotSize/2f;
    float tx = x + 1;
    float ty = y + textHeight - 1;
    float arc = 5;
    RoundRectangle2D.Float behindText = 
        new RoundRectangle2D.Float(x, y, w, h, arc, arc);

    // draw spikes
//    Set<String> outSet = getSetOfReceivers();
//    y = location.y + dotSize/2f + textHeight;
//    drawTheseOutMessage(g2, 0, y, outSet);
    
    // draw dot
    g2.setColor(getNodeColor());
    w = h = 10;
    x = location.x - w/2;
    y = location.y - h/2;
    Ellipse2D dot = new Ellipse2D.Float(x, y, w, h);
    g2.fill(dot);
    
    // draw name
    g2.setColor(new Color(.5f, .5f, .5f, .2f));
    g2.fill(behindText);
    g2.setColor(Color.BLACK);
    g2.drawString(text, tx, ty);
    
    
    // hit areas
    hitAreas.clear();
    hitAreas.add(dot);
    hitAreas.add(behindText);
  }
  
  private Color getNodeColor() {
    float r, g, b, a;
    r = g = b = .5f;
    r += numOutSet/8f;
    if (r > 1) r = 1;
    
    a = 1f;
    Color color = new Color(r, g, b, a);
    return color;
  }
  
  public boolean hitTest(float x, float y) {
    for (Shape s : hitAreas) {
      if (s.contains(x, y)) {
        return true;
      }
    }
    return false;
  }
  
  public void moveBy(float dx, float dy) {
    location.x += dx;
    location.y += dy;
    velocity.zero();
  }
  
  public boolean drawTip(Graphics2D g2, float x, float y) {
    if (!isActive()) return false;
    
    for (Tipster tip : tips) {
//      System.out.printf("checking tip=%s, shape=%s, @(%f, %f)\n",
//          tip.text, tip.shape, x, y);
      if (tip.shape.contains(x, y)) {
        reallyDrawTip(g2, x, y, tip);
        return true;
      }
    }
    return false;
  } 
  
  private void reallyDrawTip(Graphics2D g2, float x, float y, Tipster tip) {
//    System.out.println("drawing tip = " + tip.text);
    g2.setColor(Color.RED);
    g2.draw(tip.shape);
    g2.setColor(Color.BLUE);
    g2.setFont(g2.getFont().deriveFont(12f));
    g2.drawString(tip.text, x+20, y+20);
  }
  
  private void drawTheseOutMessage(
      Graphics2D g2, float x, float y, Set<String> set) 
  {
    int n = set.size();
    float d = 10;
//    float y = location.y + 20;
    x = location.x - (n-1)*d/2f;
    
    // base line
//    g2.setColor(new Color(.5f, .5f, .5f, .2f));
//    g2.draw(new Line2D.Float(x, y, x+(n-1)*d, y));
    tips.clear();
    
    float lenOfSpike = 10;
    int i = 0;
    for (String nodeName : set) {
      Node thatNode = main.findNode(nodeName);
      if (thatNode != null) {
        //TODO
        float tipX = x;
        float tipY = y+lenOfSpike;
        float w = 10f;
        float w2 = w/2f;
//        g2.draw(new Line2D.Float(x, y, x, tipY));
//        g2.fill(new Rectangle2D.Float(x-w2, y, w, lenOfSpike));
        
        //spike
        GeneralPath spike = new GeneralPath();
        spike.moveTo(tipX, tipY);
        spike.lineTo(x+w2, y);
        spike.lineTo(x-w2, y);
        spike.lineTo(tipX, tipY);
        spike.closePath();
        int vol = getVolumeTo(thatNode.name);
        g2.setColor(getColorForSpike(vol));
        g2.fill(spike);
        Tipster tip = new Tipster();
//        tip.shape = new Rectangle2D.Float(x-w2, y, w, lenOfSpike);
        tip.shape = spike;
        tip.text = "" + vol + " bytes to " + thatNode.name;
        tips.add(tip);
                
        // line to dest
        g2.setColor(new Color(.5f, .5f, .5f, .2f));
        g2.draw(new Line2D.Float(tipX, tipY, 
            thatNode.location.x, thatNode.location.y));
      }
      i++;
      x += d;
    }
  }
  
  private synchronized int getVolumeTo(String dest) {
    int sum = 0;
    for (Message msg : messages) {
      if (msg.dst.equals(dest)) {
        sum += msg.size;
      }
    }
    return sum;
  }
  
  private Color getColorForSpike(int vol) {
    float r, g, b, a;
    r = .5f;
    g = .5f;
    b = .5f;
    a = .2f;
    
    if (vol > main.maxVolSoFar) main.maxVolSoFar = vol;
    r += (vol*.5f / main.maxVolSoFar);
    if (r > 1) r = 1;
    a = r*.5f; 
    
    Color color = new Color(r, g, b, a);
    
    return color;
  }
  
  private synchronized Set<String> getSetOfReceivers() {
    Set<String> set = new HashSet<String>();
    for (Message msg : messages) {
      if (msg.src.equals(name)) {
        set.add(msg.dst);
      }
    }
    return set;
  }
  
  public String toString() {
    return String.format("Node[name=%s, #msgs=%d]", name, messages.size());
  }
  
  public synchronized void printMessages() {
    System.out.printf("%s\n", toString());
    for (Message msg : messages) {
      System.out.printf("  %s\n", msg);
    }
  }
  
  /** a callback */
  Main main;
  Point location, velocity, acceleration, netforce;
  float mass;
  String name;
//  float latestTime;
  PriorityQueue<Message> messages;
  Vector<Shape> hitAreas; 
  Vector<Tipster> tips;
  
  int numOutSet;
}
