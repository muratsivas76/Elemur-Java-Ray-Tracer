package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.light.*;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

/**
 * PhongMaterial implements the Phong reflection model, which includes
 * ambient, diffuse, and specular components. It also defines properties
 * for reflectivity, index of refraction, and transparency for advanced
 * ray tracing effects.
 * This material fully implements the extended Material interface.
 */
public class PhongMaterial implements Material {
  private final Color diffuseColor;
  private final Color specularColor;
  private final double shininess;
  private final double ambientCoefficient;
  private final double diffuseCoefficient;
  private final double specularCoefficient;
  private final double reflectivity;
  private final double ior; // Index of Refraction
  private final double transparency;
  
  /**
   * Full constructor for PhongMaterial.
   * @param diffuseColor The base color of the material (diffuse component).
   * @param specularColor The color of the specular highlight.
   * @param shininess The shininess exponent for specular highlights.
   *                    Higher values make highlights smaller and more intense.
   * @param ambientCoefficient The ambient light contribution coefficient (0.0 - 1.0).
   * @param diffuseCoefficient The diffuse light contribution coefficient (0.0 - 1.0).
   * @param specularCoefficient The specular light contribution coefficient (0.0 - 1.0).
   * @param reflectivity The reflectivity coefficient (0.0 - 1.0).
   * @param ior The Index of Refraction for transparent materials (igual or greater than 1.0).
   * @param transparency The transparency coefficient (0.0 - 1.0).
   */
  public PhongMaterial(Color diffuseColor, Color specularColor, double shininess,
    double ambientCoefficient, double diffuseCoefficient, double specularCoefficient,
    double reflectivity, double ior, double transparency) {
    this.diffuseColor = diffuseColor;
    this.specularColor = specularColor;
    this.shininess = shininess;
    this.ambientCoefficient = ambientCoefficient;
    this.diffuseCoefficient = diffuseCoefficient;
    this.specularCoefficient = specularCoefficient;
    this.reflectivity = clamp01(reflectivity);
    this.ior = Math.max(1.0, ior);
    this.transparency = clamp01(transparency);
  }
  
  /**
   * Simplified constructor with default parameters.
   * Uses white specular color, shininess of 32.0, and default coefficients.
   * Default reflectivity = 0.0, IOR = 1.0, transparency = 0.0.
   * @param diffuseColor The base color of the material.
   */
  public PhongMaterial(Color diffuseColor) {
    this(diffuseColor, Color.WHITE, 32.0,
      0.1, 0.7, 0.7,
    0.0, 1.0, 0.0);
  }
  
  // --- GETTERS (for internal use) ---
  public Color getDiffuseColor() { return diffuseColor; }
  public Color getSpecularColor() { return specularColor; }
  public double getShininess() { return shininess; }
  public double getAmbientCoefficient() { return ambientCoefficient; }
  public double getDiffuseCoefficient() { return diffuseCoefficient; }
  public double getSpecularCoefficient() { return specularCoefficient; }
  
  // --- MATERIAL INTERFACE IMPLEMENTATION ---
  
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
    // FIX: Use LightProperties for safe lighting calculations
    LightProperties lightProps = LightProperties.getLightProperties(light, point);
    
    // If light is ambient, return only ambient contribution
    if (light instanceof ElenaMuratAmbientLight) {
      return ColorUtil.multiplyColors(
        diffuseColor,
        lightProps.color,
        ambientCoefficient * lightProps.intensity
      );
    }
    
    // Diffuse component
    double NdotL = Math.max(0, normal.dot(lightProps.direction));
    Color diffuse = ColorUtil.multiplyColors(
      diffuseColor, 
      lightProps.color, 
      diffuseCoefficient * NdotL * lightProps.intensity
    );
    
    // Specular component
    Vector3 viewDir = viewerPos.subtract(point).normalize();
    Vector3 reflectDir = lightProps.direction.negate().reflect(normal);
    double RdotV = Math.max(0, reflectDir.dot(viewDir));
    double specFactor = Math.pow(RdotV, shininess);
    
    Color specular = ColorUtil.multiplyColors(
      specularColor,
      lightProps.color,
      specularCoefficient * specFactor * lightProps.intensity
    );
    
    // Ambient component
    Color ambient = ColorUtil.multiplyColors(
      diffuseColor,
      lightProps.color,
      ambientCoefficient * lightProps.intensity
    );
    
    // Combine all components
    return ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
  }
  
  @Override
  public double getReflectivity() {
    return reflectivity;
  }
  
  @Override
  public double getIndexOfRefraction() {
    return ior;
  }
  
  @Override
  public double getTransparency() {
    return transparency;
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
  }
  
  // --- HELPER METHODS ---
  
  /**
   * Clamps a double value between 0.0 and 1.0.
   * @param val The value to clamp.
   * @return Clamped value in [0.0, 1.0].
   */
  private double clamp01(double val) {
    return Math.min(1.0, Math.max(0.0, val));
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PhongMaterial phongmaterial {\n");
    sb.append("        diffuseColor = " + net.elena.murat.util.ColorUtil.toColorString(diffuseColor) + ";\n");
    sb.append("        specularColor = " + net.elena.murat.util.ColorUtil.toColorString(specularColor) + ";\n");
    sb.append("        shininess = " + shininess + ";\n");
    sb.append("        ambientCoefficient = " + ambientCoefficient + ";\n");
    sb.append("        diffuseCoefficient = " + diffuseCoefficient + ";\n");
    sb.append("        specularCoefficient = " + specularCoefficient + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("        ior = " + ior + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}