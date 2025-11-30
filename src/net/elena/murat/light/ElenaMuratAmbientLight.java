package net.elena.murat.light;

import java.awt.Color;

import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;
import net.elena.murat.lovert.Scene;

public class ElenaMuratAmbientLight implements Light {
  private final Color color;
  private double intensity;
  private static final Vector3 ZERO_VECTOR = new Vector3(0, 0, 0);
  
  public ElenaMuratAmbientLight(Color color, double intensity) {
    this.color = color != null ? color : new Color(200, 220, 255);
    this.intensity = Math.max(0, Math.min(1, intensity));
  }
  
  @Override
  public Point3 getPosition() {
    return null;
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
    return ZERO_VECTOR;
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    return ZERO_VECTOR;
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    return intensity;
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return intensity;
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    return true; // Ambient light is always visible
  }
  
  // Utility methods
  public ElenaMuratAmbientLight withColor(Color newColor) {
    return new ElenaMuratAmbientLight(newColor, intensity);
  }
  
  public ElenaMuratAmbientLight withIntensity(double newIntensity) {
    return new ElenaMuratAmbientLight(color, newIntensity);
  }
  
  public static ElenaMuratAmbientLight createDefault() {
    return new ElenaMuratAmbientLight(new Color(200, 220, 255), 0.15);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ElenaMuratAmbientLight elenamuratambientlight {\n");
    sb.append("    color = " + net.elena.murat.util.ColorUtil.toColorString(color) + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n}");
    return sb.toString();
  }
  
}
