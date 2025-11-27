package net.elena.murat.light;

import java.awt.Color;
import java.util.random.RandomGenerator;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

/**
 * License GPL-3.0
 * Implements a spherical light source that emits light uniformly from its surface.
 * Produces soft shadows and realistic illumination for spherical light sources.
 *
 * @author Murat iNAN
 * @version 1.0
 */
public class SphereLight implements Light {
  private final Point3 center;
  private final double radius;
  private final Color color;
  private double intensity;
  private final int sampleCount;
  private final RandomGenerator random;
  
  /**
   * Constructs a new spherical light source.
   *
   * @param center Center point of the sphere
   * @param radius Radius of the sphere
   * @param color Light color
   * @param intensity Base intensity value
   * @param sampleCount Number of samples for soft shadow calculation
   */
  public SphereLight(Point3 center, double radius, Color color,
    double intensity, int sampleCount) {
    this.center = center;
    this.radius = Math.max(0, radius);
    this.color = color;
    this.intensity = Math.max(0, intensity);
    this.sampleCount = Math.max(1, sampleCount);
    this.random = RandomGenerator.getDefault();
  }
  
  @Override
  public Point3 getPosition() {
    return center;
  }
  
  @Override
  public Color getColor() {
    return color;
  }
  
  @Override
  public double getIntensity() {
    return intensity;
  }
  
  @Override
  public Vector3 getDirectionAt(Point3 point) {
    Vector3 direction = center.subtract(point);
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Vector3 direction = point.subtract(center);
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = getDistanceTo(point);
    // Physical light attenuation
    double attenuation = 1.0 + 0.1 * distance + 0.01 * distance * distance;
    return intensity / Math.max(attenuation, Ray.EPSILON);
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getAttenuatedIntensity(point);
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    // Multi-sample visibility test for soft shadows
    int visibleSamples = 0;
    
    for (int i = 0; i < sampleCount; i++) {
      Point3 samplePoint = generateSamplePoint();
      Vector3 direction = samplePoint.subtract(point).normalize();
      double distance = samplePoint.distance(point);
      
      Ray shadowRay = new Ray(point.add(direction.scale(Ray.EPSILON * 10)), direction);
      if (!scene.intersects(shadowRay, distance - Ray.EPSILON)) {
        visibleSamples++;
      }
    }
    
    // Consider visible if majority of samples are visible
    return visibleSamples > (sampleCount / 2);
  }
  
  /**
   * Generates a random point on the sphere surface using uniform sampling.
   */
  private Point3 generateSamplePoint() {
    // Uniform sampling on sphere surface
    double theta = 2 * Math.PI * random.nextDouble();
    double phi = Math.acos(1 - 2 * random.nextDouble());
    
    double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
    double y = center.y + radius * Math.sin(phi) * Math.sin(theta);
    double z = center.z + radius * Math.cos(phi);
    
    return new Point3(x, y, z);
  }
  
  public double getDistanceTo(Point3 point) {
    return center.distance(point);
  }
  
  /**
   * Gets the radius of the spherical light.
   */
  public double getRadius() {
    return radius;
  }
  
  @Override
  public void setIntensity(double dins) {
    this.intensity = dins;
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
  
  /**
   * Gets the number of samples used for shadow calculation.
   */
  public int getSampleCount() {
    return sampleCount;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SphereLight spherelight {\n");
    sb.append("    position = " + center + ";\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(color) + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    radius = " + radius + ";\n");
    sb.append("    sampleCount = " + sampleCount + ";\n}");
    return sb.toString();
  }
  
}
