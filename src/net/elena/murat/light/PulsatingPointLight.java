package net.elena.murat.light;

import java.awt.Color;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

public class PulsatingPointLight implements Light {
  private final Point3 initialPosition;
  private final Color baseColor;
  private double baseIntensity;
  private final double pulsationSpeed;
  private final double movementSpeed;
  private final double movementAmplitude;
  private final double constantAttenuation;
  private final double linearAttenuation;
  private final double quadraticAttenuation;
  
  private double currentTime;
  
  public PulsatingPointLight(Point3 initialPosition, Color baseColor, double baseIntensity,
    double pulsationSpeed, double movementSpeed, double movementAmplitude) {
    this(initialPosition, baseColor, baseIntensity, pulsationSpeed, movementSpeed, movementAmplitude,
    1.0, 0.1, 0.01);
  }
  
  public PulsatingPointLight(Point3 initialPosition, Color baseColor, double baseIntensity,
    double pulsationSpeed, double movementSpeed, double movementAmplitude,
    double constantAttenuation, double linearAttenuation, double quadraticAttenuation) {
    this.initialPosition = initialPosition;
    this.baseColor = baseColor;
    this.baseIntensity = Math.max(0, baseIntensity);
    this.pulsationSpeed = Math.max(0, pulsationSpeed);
    this.movementSpeed = Math.max(0, movementSpeed);
    this.movementAmplitude = Math.max(0, movementAmplitude);
    this.constantAttenuation = Math.max(0, constantAttenuation);
    this.linearAttenuation = Math.max(0, linearAttenuation);
    this.quadraticAttenuation = Math.max(0, quadraticAttenuation);
    this.currentTime = 0;
  }
  
  public void update(double deltaTime) {
    this.currentTime += deltaTime;
  }
  
  @Override
  public Point3 getPosition() {
    double offsetX = Math.sin(currentTime * movementSpeed) * movementAmplitude;
    double offsetY = Math.cos(currentTime * movementSpeed * 0.7) * movementAmplitude * 0.5;
    double offsetZ = Math.sin(currentTime * movementSpeed * 0.3) * movementAmplitude * 0.3;
    return new Point3(
      initialPosition.x + offsetX,
      initialPosition.y + offsetY,
      initialPosition.z + offsetZ
    );
  }
  
  @Override
  public Color getColor() {
    double pulsationFactor = 0.7 + 0.3 * Math.sin(currentTime * pulsationSpeed);
    return new Color(
      clampColor(baseColor.getRed() * pulsationFactor),
      clampColor(baseColor.getGreen() * pulsationFactor),
      clampColor(baseColor.getBlue() * pulsationFactor)
    );
  }
  
  @Override
  public void setIntensity(double dins) {
    this.baseIntensity = dins;
  }
  
  @Override
  public double getIntensity() {
    baseIntensity = baseIntensity * (0.8 + 0.2 * Math.sin(currentTime * pulsationSpeed * 1.3));
    return baseIntensity;
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
  public Vector3 getDirectionAt(Point3 point) {
    return getPosition().subtract(point).normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    return point.subtract(getPosition()).normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = getDistanceTo(point);
    double attenuation = calculateAttenuation(distance);
    double pulsationFactor = 0.5 + 0.5 * Math.sin(currentTime * pulsationSpeed * 1.7);
    return getIntensity() * pulsationFactor / Math.max(attenuation, Ray.EPSILON);
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getAttenuatedIntensity(point);
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    Vector3 lightDir = getDirectionTo(point);
    double distance = getDistanceTo(point);
    Ray shadowRay = new Ray(
      point.add(lightDir.scale(Ray.EPSILON * 10)),
      lightDir
    );
    return !scene.intersects(shadowRay, distance - Ray.EPSILON);
  }
  
  public double getDistanceTo(Point3 point) {
    return getPosition().distance(point);
  }
  
  private double calculateAttenuation(double distance) {
    return constantAttenuation +
    linearAttenuation * distance +
    quadraticAttenuation * distance * distance;
  }
  
  private int clampColor(double value) {
    return (int) Math.max(0, Math.min(255, value));
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PulsatingPointLight pulsatingpointlight {\n");
    sb.append("    initialPosition = " + initialPosition + ";\n");
    sb.append("    baseColor = " + net.elena.murat.util.ColorUtil.toColorString(baseColor) + ";\n");
    sb.append("    baseIntensity = " + baseIntensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    pulsationSpeed = " + pulsationSpeed + ";\n");
    sb.append("    movementSpeed = " + movementSpeed + ";\n");
    sb.append("    movementAmplitude = " + movementAmplitude + ";\n");
    sb.append("    constantAttenuation = " + constantAttenuation + ";\n");
    sb.append("    linearAttenuation = " + linearAttenuation + ";\n");
    sb.append("    quadraticAttenuation = " + quadraticAttenuation + ";\n}");
    return sb.toString();
  }
  
}
