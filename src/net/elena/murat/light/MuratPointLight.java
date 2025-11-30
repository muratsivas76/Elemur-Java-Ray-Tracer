package net.elena.murat.light;

import java.awt.Color;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

public class MuratPointLight implements Light {
  private final Point3 position;
  private final Color color;
  private double intensity;
  private final double constantAttenuation;
  private final double linearAttenuation;
  private final double quadraticAttenuation;
  
  public MuratPointLight(Point3 position, Color color, double intensity) {
    this(position, color, intensity, 1.0, 0.1, 0.01);
  }
  
  public MuratPointLight(Point3 position, Color color, double intensity,
    double constantAttenuation, double linearAttenuation, double quadraticAttenuation) {
    this.position = position;
    this.color = color;
    this.intensity = Math.max(0, intensity);
    this.constantAttenuation = Math.max(0, constantAttenuation);
    this.linearAttenuation = Math.max(0, linearAttenuation);
    this.quadraticAttenuation = Math.max(0, quadraticAttenuation);
  }
  
  @Override
  public Point3 getPosition() {
    return position;
  }
  
  @Override
  public Color getColor() {
    return color;
  }
  
  @Override
  public void setIntensity(double dins) {
    this.intensity = dins;
  }
  
  @Override
  public double getIntensity() {
    return intensity;
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
    Vector3 direction = position.subtract(point);
    return direction.length() < Ray.EPSILON ? new Vector3(0,0,0) : direction.normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Vector3 direction = point.subtract(position);
    return direction.length() < Ray.EPSILON ? new Vector3(0,0,0) : direction.normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = position.distance(point);
    double attenuation = constantAttenuation +
    linearAttenuation * distance +
    quadraticAttenuation * distance * distance;
    return intensity / Math.max(attenuation, Ray.EPSILON);
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
    return position.distance(point);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("MuratPointLight muratpointlight {\n");
    sb.append("    position = " + position + ";\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(color) + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("    constantAttenuation = " + constantAttenuation + ";\n");
    sb.append("    linearAttenuation = " + linearAttenuation + ";\n");
    sb.append("    quadraticAttenuation = " + quadraticAttenuation + ";\n}");
    return sb.toString();
  }
  
}
