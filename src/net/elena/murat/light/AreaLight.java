package net.elena.murat.light;

import java.awt.Color;
import java.util.random.RandomGenerator;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

/**
 * License GPL-3.0
 * Implements a rectangular area light source for soft shadow effects.
 * Samples multiple points across the light surface for realistic illumination.
 *
 * @author Murat iNAN
 * @version 1.0
 */
public class AreaLight implements Light {
  private final Point3 position;  // Center point of the area light
  private final Vector3 normal;   // Orientation direction
  private final Vector3 right;    // Right vector for surface sampling
  private final Vector3 up;       // Up vector for surface sampling
  private final double width;     // Width of the area light
  private final double height;    // Height of the area light
  private final Color color;      // Light color
  private double intensity; // Base intensity
  private final int samplesU;     // Number of horizontal samples
  private final int samplesV;     // Number of vertical samples
  private final RandomGenerator random;
  
  /**
   * Constructs a new rectangular area light.
   *
   * @param position Center position of the light
   * @param normal Orientation direction vector
   * @param width Width of the light surface
   * @param height Height of the light surface
   * @param color Light color
   * @param intensity Base intensity value
   * @param samplesU Number of horizontal samples for soft shadows
   * @param samplesV Number of vertical samples for soft shadows
   */
  public AreaLight(Point3 position, Vector3 normal, double width, double height,
    Color color, double intensity, int samplesU, int samplesV) {
    this.position = position;
    this.normal = normal.normalize();
    this.right = calculateRightVector(normal);
    this.up = this.normal.cross(this.right).normalize();
    this.width = Math.max(0, width);
    this.height = Math.max(0, height);
    this.color = color;
    this.intensity = Math.max(0, intensity);
    this.samplesU = Math.max(1, samplesU);
    this.samplesV = Math.max(1, samplesV);
    this.random = RandomGenerator.getDefault();
  }
  
  /**
   * Calculates a right vector perpendicular to the normal.
   */
  private Vector3 calculateRightVector(Vector3 normal) {
    // Find a vector perpendicular to the normal
    if (Math.abs(normal.y) > 0.9) {
      return new Vector3(1, 0, 0);
      } else {
      return normal.cross(new Vector3(0, 1, 0)).normalize();
    }
  }
  
  @Override
  public Point3 getPosition() {
    return position;
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
    // Average direction from point to light center
    Vector3 direction = position.subtract(point);
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Vector3 direction = point.subtract(position);
    return direction.length() < Ray.EPSILON ? new Vector3(0, 0, 0) : direction.normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = getDistanceTo(point);
    // Simple attenuation - in practice should be calculated per sample
    double attenuation = 1.0 + 0.1 * distance + 0.01 * distance * distance;
    return intensity / Math.max(attenuation, Ray.EPSILON);
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getAttenuatedIntensity(point);
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    // Multi-sample shadow testing for soft shadows
    int visibleSamples = 0;
    int totalSamples = samplesU * samplesV;
    
    for (int i = 0; i < samplesU; i++) {
      for (int j = 0; j < samplesV; j++) {
        Point3 samplePoint = generateSamplePoint(i, j);
        Vector3 direction = samplePoint.subtract(point).normalize();
        double distance = samplePoint.distance(point);
        
        // Offset ray origin to prevent self-intersection
        Ray shadowRay = new Ray(point.add(direction.scale(Ray.EPSILON * 10)), direction);
        if (!scene.intersects(shadowRay, distance - Ray.EPSILON)) {
          visibleSamples++;
        }
      }
    }
    
    // Consider visible if at least one sample point is visible
    return visibleSamples > 0;
  }
  
  /**
   * Generates a sample point on the light surface using stratified sampling.
   */
  private Point3 generateSamplePoint(int i, int j) {
    // Stratified sampling with jitter
    double u = ((i + random.nextDouble()) / samplesU) - 0.5;
    double v = ((j + random.nextDouble()) / samplesV) - 0.5;
    
    Vector3 offset = right.scale(u * width).add(up.scale(v * height));
    return position.add(offset);
  }
  
  @Override
  public void setIntensity(double dins) {
    this.intensity = dins;
  }
  
  public double getDistanceTo(Point3 point) {
    return position.distance(point);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("AreaLight arealight {\n");
    sb.append("    position = " + position + ";\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(color) + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    normal = " + normal + ";\n");
    sb.append("    width = " + width + ";\n");
    sb.append("    height = " + height + ";\n");
    sb.append("    samplesU = " + samplesU + ";\n");
    sb.append("    samplesV = " + samplesV + ";\n}");
    return sb.toString();
  }
  
}
