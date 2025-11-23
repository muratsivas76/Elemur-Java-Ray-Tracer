package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

public class DamaskCeramicMaterial implements Material {
  private final Color primaryColor;
  private final Color secondaryColor;
  private final double shininess;
  private final double ambientCoeff;
  private final double specularCoeff;
  private final double reflectivity=0.1;
  
  private Matrix4 objectInverseTransform;
  
  public DamaskCeramicMaterial(Color primary, Color secondary,
    double shininess, Matrix4 invTransform) {
    this(primary, secondary, shininess, 0.1, 0.8, invTransform);
  }
  
  public DamaskCeramicMaterial(Color primary, Color secondary,
    double shininess, double ambient,
    double specular, Matrix4 invTransform) {
    this.primaryColor = primary;
    this.secondaryColor = secondary;
    this.shininess = Math.max(1.0, shininess);
    this.ambientCoeff = ambient;
    this.specularCoeff = specular;
    this.objectInverseTransform = invTransform;
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4 ();
    this.objectInverseTransform = tm;
  }
  
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewPos) {
    // 1. Coordinate transformation
    Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
    Vector3 normal = objectInverseTransform.transformNormal(worldNormal).normalize();
    
    // 2. Procedural damask pattern
    double pattern = Math.sin(localPoint.x * 5) *
    Math.cos(localPoint.y * 7) *
    Math.sin(localPoint.z * 3);
    Color baseColor = pattern > 0 ? primaryColor : secondaryColor;
    
    // 3. Light calculations - NULL CHECK EKLENDİ
    Vector3 lightDir;
    Point3 lightPos = light.getPosition();
    
    if (lightPos != null) {
        // Point light - position'dan direction hesapla
        lightDir = lightPos.subtract(worldPoint).normalize();
    } else {
        // Directional light - direction'ı direkt al
        lightDir = light.getDirectionAt(localPoint).normalize();
    }
    
    Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
    Vector3 reflectDir = lightDir.negate().reflect(normal);
    
    // 4. Components
    double NdotL = Math.max(0, normal.dot(lightDir));
    double RdotV = Math.max(0, reflectDir.dot(viewDir));
    
    // 5. Light effect (supports colored light)
    Color lightColor = light.getColor();
    double intensity = light.getIntensityAt(worldPoint);
    
    // 6. Color mixing
    Color ambient = ColorUtil.multiplyColors(baseColor, lightColor, ambientCoeff);
    Color diffuse = ColorUtil.multiplyColors(baseColor, lightColor, NdotL * intensity);
    Color specular = ColorUtil.multiplyColors(lightColor,
      new Color(255, 255, 255),
    Math.pow(RdotV, shininess) * specularCoeff * intensity);
    
    return ColorUtil.combineColors(ambient, diffuse, specular);
  }
  
  @Override public double getReflectivity() { return reflectivity; }
  @Override public double getIndexOfRefraction() { return 1.4; }
  @Override public double getTransparency() { return 0.0; }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("DamaskCeramicMaterial damaskceramicmaterial {\n");
    sb.append("        primary = " + net.elena.murat.util.ColorUtil.toColorString(primaryColor) + ";\n");
    sb.append("        secondary = " + net.elena.murat.util.ColorUtil.toColorString(secondaryColor) + ";\n");
    sb.append("        shininess = " + shininess + ";\n");
    sb.append("        ambient = " + ambientCoeff + ";\n");
    sb.append("        specular = " + specularCoeff + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
