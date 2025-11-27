package net.elena.murat.light;

import java.awt.Color;
import java.util.random.RandomGenerator;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

/**
 * License GPL-3.0
 * Implements a linear tube light source for fluorescent-like lighting.
 * Emits light along a line segment with soft shadow effects.
 *
 * @author Murat iNAN
 * @version 1.0
 */
public class TubeLight implements Light {
  private final Point3 startPoint;
  private final Point3 endPoint;
  private final double radius;
  private final Color color;
  private double intensity;
  private final int sampleCount;
  private final RandomGenerator random;
  private final Vector3 direction;
  private final double length;
  
  /**
   * Constructs a new tube light along a line segment.
   *
   * @param startPoint Starting point of the tube
   * @param endPoint Ending point of the tube
   * @param radius Radius of the tube
   * @param color Light color
   * @param intensity Base intensity value
   * @param sampleCount Number of samples for soft shadow calculation
   */
  public TubeLight(Point3 startPoint, Point3 endPoint, double radius,
    Color color, double intensity, int sampleCount) {
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.radius = Math.max(0, radius);
    this.color = color;
    this.intensity = Math.max(0, intensity);
    this.sampleCount = Math.max(1, sampleCount);
    this.random = RandomGenerator.getDefault();
    
    this.direction = endPoint.subtract(startPoint);
    this.length = direction.length();
  }
  
  @Override
  public Point3 getPosition() {
    // Return midpoint of the tube
    return startPoint.add(direction.scale(0.5));
  }
  
  @Override
  public Color getColor() {
    return color;
  }
  
  private double[] idIntensity = new double[] {0.0, 0.0};
  @Override
  public void setIncDecIntensity(double[] dn) {
    this.idIntensity = dn;
  }
  
  @Override
  public double[] getIncDecIntensity() {
    return this.idIntensity;
  }
  
  @Override
  public double getIntensity() {
    return intensity;
  }
  
  @Override
  public Vector3 getDirectionAt(Point3 point) {
    Vector3 direction = getPosition().subtract(point);
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Vector3 direction = point.subtract(getPosition());
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = getDistanceTo(point);
    // Linear light attenuation
    double attenuation = 1.0 + 0.05 * distance + 0.005 * distance * distance;
    return intensity / Math.max(attenuation, Ray.EPSILON);
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getAttenuatedIntensity(point);
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    // Multi-sample visibility test along the tube length
    int visibleSamples = 0;
    
    for (int i = 0; i < sampleCount; i++) {
      Point3 samplePoint = generateSamplePoint();
      Vector3 sampleDirection = samplePoint.subtract(point).normalize();
      double sampleDistance = samplePoint.distance(point);
      
      Ray shadowRay = new Ray(point.add(sampleDirection.scale(Ray.EPSILON * 10)), sampleDirection);
      if (!scene.intersects(shadowRay, sampleDistance - Ray.EPSILON)) {
        visibleSamples++;
      }
    }
    
    return visibleSamples > 0;
  }
  
  /**
   * Generates a random point along the tube surface.
   */
  private Point3 generateSamplePoint() {
    // Random position along the tube length
    double t = random.nextDouble();
    Point3 linePoint = startPoint.add(direction.scale(t));
    
    // Random point on circle perpendicular to tube direction
    Vector3 tubeDir = direction.normalize();
    Vector3 randomDir = generatePerpendicularVector(tubeDir).normalize();
    double angle = 2 * Math.PI * random.nextDouble();
    
    Vector3 offset = randomDir.rotateAround(tubeDir, angle).scale(radius);
    return linePoint.add(offset);
  }
  
  /**
   * Generates a vector perpendicular to the given direction.
   */
  private Vector3 generatePerpendicularVector(Vector3 direction) {
    if (Math.abs(direction.x) > 0.9) {
      return direction.cross(new Vector3(0, 1, 0));
      } else {
      return direction.cross(new Vector3(1, 0, 0));
    }
  }
  
  @Override
  public void setIntensity(double dins) {
    this.intensity = dins;
  }
  
  public double getDistanceTo(Point3 point) {
    return getPosition().distance(point);
  }
  
  /**
   * Gets the starting point of the tube.
   */
  public Point3 getStartPoint() {
    return startPoint;
  }
  
  /**
   * Gets the ending point of the tube.
   */
  public Point3 getEndPoint() {
    return endPoint;
  }
  
  /**
   * Gets the radius of the tube.
   */
  public double getRadius() {
    return radius;
  }
  
  /**
   * Gets the length of the tube.
   */
  public double getLength() {
    return length;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TubeLight tubelight {\n");
    sb.append("    position = " + getPosition() + ";\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(color) + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    startPoint = " + startPoint + ";\n");
    sb.append("    endPoint = " + endPoint + ";\n");
    sb.append("    radius = " + radius + ";\n");
    sb.append("    length = " + length + ";\n");
    sb.append("    sampleCount = " + sampleCount + ";\n}");
    return sb.toString();
  }
  
}
