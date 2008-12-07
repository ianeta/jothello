/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spikies;

import java.awt.geom.*;

/**
 *
 * @author mocksuwannatat
 */
public class Point extends Point2D.Float {

  public Point() {
    this(0, 0);
  }
  
  public Point(float x_, float y_) {
    x = x_;
    y = y_;
  }
  
  public void zero() {
    x = y = 0;
  }
  
  private float sqr(float x) { 
    return x*x;
  }
  
  public float distWith(Point p2) {
    return (float) Math.sqrt(sqr(x - p2.x) + sqr(y - p2.y));
  }
  
  public void addBy(Point vec) {
    addBy(vec.x, vec.y);
  }
  
  public void addBy(float dx, float dy) {
    x += dx; 
    y += dy;
  }
  
  public void multBy(float f) {
    multBy(f, f);
  }
  
  public void multBy(float mx, float my) {
    x *= mx;
    y *= my;
  }
  
  public Point getRayToward(Point p2) {
    Point ray = new Point();
    ray.x = p2.x - x;
    ray.y = p2.y - y;
    return ray;
  }
  
  public void makeLength(float newLength) {
    normalize();
    multBy(newLength);
  }
  
  public void normalize() {
    float len = size();
    if (len == 0) return;
    x /= len;
    y /= len;
  }
  
  public float size() {
    return (float) Math.sqrt(x*x + y*y);
  }
}
