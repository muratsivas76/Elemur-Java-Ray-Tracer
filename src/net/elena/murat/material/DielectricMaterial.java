package net.elena.murat.material;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.elena.murat.math.*;
import net.elena.murat.light.Light;
import net.elena.murat.util.ColorUtil;

public class DielectricMaterial implements Material {
  
  // Material properties
  private Color diffuseColor;
  private double indexOfRefraction;
  private double transparency;
  private double reflectivity;
  
  // Filter colors for interior and exterior
  private Color filterColorInside;
  private Color filterColorOutside;
  
  private double currentReflectivity;
  private double currentTransparency;
  
  // Object transformation matrix
  private Matrix4 objectTransform;
  
  /**
   * Default constructor with glass-like properties
   */
  public DielectricMaterial() {
    this.diffuseColor = new Color(0.9f, 0.9f, 0.9f);
    this.indexOfRefraction = 1.5;
    this.transparency = 0.8;
    this.reflectivity = 0.1;
    this.filterColorInside = new Color(1.0f, 1.0f, 1.0f);
    this.filterColorOutside = new Color(1.0f, 1.0f, 1.0f);
    this.objectTransform = new Matrix4().identity();
    
    this.currentReflectivity = this.reflectivity;
    this.currentTransparency = this.transparency;
  }
  
  /**
   * Constructor with custom parameters
   */
  public DielectricMaterial(Color diffuseColor, double ior,
    double transparency, double reflectivity) {
    this.diffuseColor = diffuseColor;
    this.indexOfRefraction = ior;
    this.transparency = transparency;
    this.reflectivity = reflectivity;
    this.filterColorInside = new Color(1.0f, 1.0f, 1.0f);
    this.filterColorOutside = new Color(1.0f, 1.0f, 1.0f);
    
    this.objectTransform = new Matrix4().identity();
  }
  
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPoint) {
    Vector3 lightDir = light.getDirectionTo(point).normalize();
    double diffuseFactor = Math.max(0.1, normal.dot(lightDir)); // Min 0.1 for visibility
    
    // Calculate Fresnel effect
    Vector3 viewDir = viewerPoint.subtract(point).normalize();
    double fresnel = Vector3.calculateFresnel(viewDir, normal, 1.0, indexOfRefraction);
    
    // Better energy conservation
    this.currentReflectivity = Math.min(0.9, reflectivity + (fresnel * 0.6));
    this.currentTransparency = Math.max(0.1, transparency * (1.0 - fresnel * 0.3));
    
    // Apply filter colors to diffuse
    Color filteredDiffuse = ColorUtil.multiplyColors(diffuseColor, filterColorOutside);
    Color diffuse = ColorUtil.multiplyColor(filteredDiffuse, diffuseFactor * light.getIntensity());
    
    // Improved specular for glass materials
    Vector3 reflectDir = lightDir.reflect(normal);
    double specularFactor = Math.pow(Math.max(0, viewDir.dot(reflectDir)), 64); // Higher exponent
    Color specular = ColorUtil.multiplyColor(light.getColor(), specularFactor * 0.7 * light.getIntensity());
    
    // Simple combination
    Color result = ColorUtil.add(diffuse, specular);
    
    // Very subtle glass tint
    Color glassTint = new Color(0.99f, 0.995f, 1.0f);
    result = ColorUtil.multiplyColors(result, glassTint);
    
    return ColorUtil.clampColor(result);
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4();
    this.objectTransform = tm;
  }
  
  @Override
  public double getIndexOfRefraction() {
    return indexOfRefraction;
  }
  
  @Override
  public double getTransparency() {
    return currentTransparency;
  }
  
  @Override
  public double getReflectivity() {
    return currentReflectivity;
  }
  
  // Getters and setters for dielectric properties
  public Color getFilterColorInside() { return filterColorInside; }
  public Color getFilterColorOutside() { return filterColorOutside; }
  
  public void setFilterColorInside(Color filterColorInside) {
    this.filterColorInside = filterColorInside;
  }
  
  public void setFilterColorOutside(Color filterColorOutside) {
    this.filterColorOutside = filterColorOutside;
  }
  
  public Color getDiffuseColor() {
    return diffuseColor;
  }
  
  public void setDiffuseColor(Color diffuseColor) {
    this.diffuseColor = diffuseColor;
  }
  
  public void setIndexOfRefraction(double indexOfRefraction) {
    this.indexOfRefraction = indexOfRefraction;
  }
  
  public void setTransparency(double transparency) {
    this.transparency = transparency;
  }
  
  public void setReflectivity(double reflectivity) {
    this.reflectivity = reflectivity;
  }
  
  @Override
  public String toString() {
    return String.format("DielectricMaterial[ior=%.2f, transparency=%.2f, reflectivity=%.2f]",
    indexOfRefraction, transparency, reflectivity);
  }
  
  public String toString2() {
    StringBuffer sb = new StringBuffer();
    sb.append("DielectricMaterial dielectricmaterial {\n");
    sb.append("        diffuseColor = " + net.elena.murat.util.ColorUtil.toColorString(diffuseColor) + ";\n");
    sb.append("        ior = " + indexOfRefraction + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
