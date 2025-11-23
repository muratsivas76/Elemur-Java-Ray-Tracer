package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;

public class PhongElenaMaterial implements Material {
  private final Color diffuseColor;
  private final double reflectivity;
  private final double shininess;
  private final double ambientCoefficient;
  
  public PhongElenaMaterial(Color diffuseColor, double reflectivity, double shininess) {
    this(diffuseColor, reflectivity, shininess, 0.1);
  }
  
  public PhongElenaMaterial(Color diffuseColor, double reflectivity,
    double shininess, double ambientCoefficient) {
    this.diffuseColor = diffuseColor;
    this.reflectivity = Math.max(0, Math.min(1, reflectivity));
    this.shininess = Math.max(1, shininess);
    this.ambientCoefficient = Math.max(0, Math.min(1, ambientCoefficient));
  }
  
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewerPos) {
    // 1. Get light properties
    Color lightColor = light.getColor();
    double intensity = light.getIntensityAt(worldPoint);
    Vector3 lightDir = light.getDirectionAt(worldPoint).normalize();
    
    // 2. Diffuse component (including light color)
    double NdotL = Math.max(0, worldNormal.dot(lightDir));
    int r = (int)(diffuseColor.getRed() * NdotL * (lightColor.getRed()/255.0) * intensity);
    int g = (int)(diffuseColor.getGreen() * NdotL * (lightColor.getGreen()/255.0) * intensity);
    int b = (int)(diffuseColor.getBlue() * NdotL * (lightColor.getBlue()/255.0) * intensity);
    
    // 3. Specular component (including light color)
    if (NdotL > 0) {
      Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
      Vector3 reflectDir = lightDir.negate().reflect(worldNormal);
      double RdotV = Math.max(0, reflectDir.dot(viewDir));
      double specular = Math.pow(RdotV, shininess) * reflectivity * intensity;
      
      r += (int)(lightColor.getRed() * specular);
      g += (int)(lightColor.getGreen() * specular);
      b += (int)(lightColor.getBlue() * specular);
    }
    
    // 4. Ambient component (NOT including light color)
    r += (int)(diffuseColor.getRed() * ambientCoefficient);
    g += (int)(diffuseColor.getGreen() * ambientCoefficient);
    b += (int)(diffuseColor.getBlue() * ambientCoefficient);
    
    // 5. Color clamping
    return new Color(
      Math.min(255, r),
      Math.min(255, g),
      Math.min(255, b)
    );
  }
  
  @Override public double getReflectivity() { return reflectivity; }
  @Override public double getIndexOfRefraction() { return 1.0; }
  @Override public double getTransparency() { return 0.0; }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PhongElenaMaterial phongelenamaterial {\n");
    sb.append("        diffuseColor = " + net.elena.murat.util.ColorUtil.toColorString(diffuseColor) + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("        shininess = " + shininess + ";\n");
    sb.append("        ambientCoefficient = " + ambientCoefficient + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}