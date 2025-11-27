package net.elena.murat.light;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

public class BioluminescentLight implements Light {
  private final List<Point3> organismPositions;
  private final Color baseColor;
  private final double pulseSpeed;
  private double baseIntensity;
  private final double attenuationFactor;
  private double currentTime;
  private static final double MIN_PULSE_INTENSITY = 0.7;
  private static final double PULSE_AMPLITUDE = 0.3;
  
  public BioluminescentLight(List<Point3> positions, Color color, double pulseSpeed) {
    this(positions, color, pulseSpeed, 100.0, 0.0001);
  }
  
  public BioluminescentLight(List<Point3> positions, Color color,
    double pulseSpeed, double baseIntensity, double attenuationFactor) {
    if (positions == null || positions.isEmpty()) {
      throw new IllegalArgumentException("Organism positions cannot be null or empty");
    }
    this.organismPositions = Collections.unmodifiableList(positions);
    this.baseColor = color != null ? color : new Color(255, 255, 255);
    this.pulseSpeed = Math.max(0.1, pulseSpeed);
    this.baseIntensity = Math.max(10.0, baseIntensity);
    this.attenuationFactor = Math.max(0.00001, attenuationFactor);
    this.currentTime = 0.0;
  }
  
  public void update(double deltaTime) {
    this.currentTime += deltaTime * this.pulseSpeed;
  }
  
  @Override
  public Point3 getPosition() {
    return organismPositions.get(0);
  }
  
  @Override
  public Color getColor() {
    return baseColor;
  }
  
  @Override
  public void setIntensity(double intensity) {
    this.baseIntensity = Math.max(0.1, intensity);
  }
  
  @Override
  public double getIntensity() {
    // TEST: Pulse'u kapat, direkt max intensity dön
    return baseIntensity;
  }
  
  private double[] idIntensity = new double[] {0.0, 0.0};
  
  @Override
  public void setIncDecIntensity(double[] dn) {
    if (dn != null && dn.length >= 2) {
      this.idIntensity[0] = dn[0];
      this.idIntensity[1] = dn[1];
    }
  }
  
  @Override
  public double[] getIncDecIntensity() {
    return new double[] {idIntensity[0], idIntensity[1]};
  }
  
  @Override
  public Vector3 getDirectionAt(Point3 point) {
    Point3 closest = findClosestPosition(point);
    Vector3 direction = closest.subtract(point);
    return direction.length() > Ray.EPSILON ? direction.normalize() : new Vector3(0, 1, 0);
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Point3 closest = findClosestPosition(point);
    Vector3 direction = point.subtract(closest);
    return direction.length() > Ray.EPSILON ? direction.normalize() : new Vector3(0, 1, 0);
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = getClosestDistance(point);
    double intensity = getIntensity();
    
    // Daha yumuşak attenuation
    double attenuation = 1.0 / (1.0 + attenuationFactor * distance * distance);
    return intensity * attenuation;
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getAttenuatedIntensity(point);
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    Point3 closest = findClosestPosition(point);
    Vector3 toLight = closest.subtract(point);
    double distance = toLight.length();
    
    if (distance < Ray.EPSILON) {
      return true;
    }
    
    Vector3 direction = toLight.normalize();
    Ray shadowRay = new Ray(
      point.add(direction.scale(Ray.EPSILON * 2)),
      direction
    );
    
    return !scene.intersects(shadowRay, distance - Ray.EPSILON * 4);
  }
  
  public double getClosestDistance(Point3 point) {
    return findClosestPosition(point).distance(point);
  }
  
  private Point3 findClosestPosition(Point3 point) {
    Point3 closest = organismPositions.get(0);
    double minDistance = closest.distance(point);
    
    for (int i = 1; i < organismPositions.size(); i++) {
      double distance = organismPositions.get(i).distance(point);
      if (distance < minDistance) {
        minDistance = distance;
        closest = organismPositions.get(i);
      }
    }
    return closest;
  }
  
  private double calculatePulseFactor() {
    return MIN_PULSE_INTENSITY + PULSE_AMPLITUDE * (0.5 + 0.5 * Math.sin(currentTime));
  }
  
  // Utility methods
  public BioluminescentLight withPositions(List<Point3> newPositions) {
    return new BioluminescentLight(newPositions, baseColor, pulseSpeed, baseIntensity, attenuationFactor);
  }
  
  public BioluminescentLight withColor(Color newColor) {
    return new BioluminescentLight(organismPositions, newColor, pulseSpeed, baseIntensity, attenuationFactor);
  }
  
  public BioluminescentLight withPulseSpeed(double newSpeed) {
    return new BioluminescentLight(organismPositions, baseColor, newSpeed, baseIntensity, attenuationFactor);
  }
  
  public static BioluminescentLight createDefault() {
    return new BioluminescentLight(
      Collections.singletonList(new Point3(0, 2, 0)),
      new Color(255, 255, 255),  // BEYAZ IŞIK
      2.0,
      500.0,      // ÇOK YÜKSEK
      0.00001     // NEREDEYSE SIFIR ATTENUATION
    );
  }
  
  public String organize(List<Point3> points) {
    if (points == null || points.isEmpty()) {
      return "[]";
    }
    
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < points.size(); i++) {
      result.append(points.get(i).toString());
      if (i < points.size() - 1) {
        result.append("-");
      }
    }
    result.append("]");
    
    return result.toString();
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("BioluminescentLight bioluminescentlight {\n");
    sb.append("    positions = " + organize(organismPositions) + ";\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(baseColor) + ";\n");
    sb.append("    pulseSpeed = " + pulseSpeed + ";\n");
    sb.append("    baseIntensity = " + baseIntensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    attenuationFactor = " + attenuationFactor + ";\n}");
    return sb.toString();
  }
  
}
