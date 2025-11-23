package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

public class WaterRippleMaterial implements Material {
  private final Color waterColor;
  private final double waveSpeed;
  private final double reflectivity;
  private Matrix4 objectInverseTransform;
  private double time;
  
  public WaterRippleMaterial(Color waterColor, double waveSpeed, Matrix4 invTransform) {
    this(waterColor, waveSpeed, 0.4, invTransform);
  }
  
  public WaterRippleMaterial(Color waterColor, double waveSpeed, double reflectivity, Matrix4 invTransform) {
    this.waterColor = waterColor;
    this.waveSpeed = waveSpeed;
    this.reflectivity = Math.max(0, Math.min(1, reflectivity));
    this.objectInverseTransform = invTransform;
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4();
    this.objectInverseTransform = tm.inverse();
  }
  
  public void update(double deltaTime) {
    time += deltaTime * waveSpeed;
  }
  
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewPos) {
    // 1. Coordinate transformation
    Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
    Vector3 normal = objectInverseTransform.transformNormal(worldNormal).normalize();
    
    // 2. Calculate wave effect using LightProperties
    LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
    double waveEffect = calculateWaveEffect(localPoint);
    
    // 3. Perturb normal based on waves
    Vector3 perturbedNormal = perturbNormal(normal, localPoint, waveEffect);
    
    // 4. Calculate lighting using LightProperties
    Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
    
    // Diffuse component
    double NdotL = Math.max(0, perturbedNormal.dot(lightProps.direction));
    Color diffuse = ColorUtil.multiplyColor(waterColor, NdotL * lightProps.intensity);
    
    // Specular component (water-like specular)
    Vector3 halfVec = lightProps.direction.add(viewDir).normalize();
    double NdotH = Math.max(0, perturbedNormal.dot(halfVec));
    double specularIntensity = Math.pow(NdotH, 128) * reflectivity;
    Color specular = ColorUtil.multiplyColor(lightProps.color, specularIntensity * lightProps.intensity);
    
    // Fresnel effect for water
    double fresnel = calculateFresnel(perturbedNormal, viewDir);
    Color fresnelColor = ColorUtil.multiplyColor(Color.WHITE, fresnel * reflectivity * lightProps.intensity);
    
    // Combine all components - ColorUtil.addColors kullanıyoruz
    Color finalColor = ColorUtil.add(diffuse, specular);
    finalColor = ColorUtil.add(finalColor, fresnelColor);
    
    // Add wave-based color variation
    Color waveColor = ColorUtil.multiplyColor(waterColor, 1.0 + waveEffect * 0.3);
    finalColor = ColorUtil.blendColors(finalColor, waveColor, 0.7f);
    
    // Add ambient contribution from light properties
    if (lightProps.direction.length() < 0.001) { // Ambient light
      Color ambient = ColorUtil.multiplyColor(waterColor, lightProps.intensity * 0.3);
      finalColor = ColorUtil.add(finalColor, ambient);
    }
    
    return finalColor;
  }
  
  private double calculateWaveEffect(Point3 p) {
    return 0.3 * (
      Math.sin(p.x * 3 + time) +
      Math.cos(p.z * 4 + time * 1.3) +
      Math.sin(p.x * 5 + p.z * 7 + time * 0.7)
    ) / 3.0;
  }
  
  private Vector3 perturbNormal(Vector3 original, Point3 p, double wave) {
    double dx = 0.5 * Math.cos(p.x * 5 + time);
    double dz = 0.5 * Math.sin(p.z * 5 + time);
    return new Vector3(
      original.x + dx,
      original.y,
      original.z + dz
    ).normalize();
  }
  
  private double calculateFresnel(Vector3 normal, Vector3 viewDir) {
    return Math.pow(1.0 - Math.max(0, normal.dot(viewDir)), 5);
  }
  
  @Override
  public double getReflectivity() {
    return reflectivity;
  }
  
  @Override
  public double getIndexOfRefraction() {
    return 1.33; // Water's IOR
  }
  
  @Override
  public double getTransparency() {
    return 0.3;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("WaterRippleMaterial waterripplematerial {\n");
    sb.append("        waterColor = " + net.elena.murat.util.ColorUtil.toColorString(waterColor) + ";\n");
    sb.append("        waveSpeed = " + waveSpeed + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
