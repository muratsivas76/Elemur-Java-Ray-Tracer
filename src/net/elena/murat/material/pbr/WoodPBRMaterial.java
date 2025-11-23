package net.elena.murat.material.pbr;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

public class WoodPBRMaterial implements PBRCapableMaterial {
  private final Color woodColor1, woodColor2;
  private final double tileSize;
  private final double roughness;
  private final double specularScale;
  private boolean isAlternateTile = false;
  
  public WoodPBRMaterial() {
    this(new Color(160, 110, 60), new Color(130, 90, 50), 0.5, 0.3, 1.5);
  }
  
  public WoodPBRMaterial(Color color1, Color color2, double tileSize,
    double roughness, double specularScale) {
    this.woodColor1 = color1;
    this.woodColor2 = color2;
    this.tileSize = tileSize;
    this.roughness = roughness;
    this.specularScale = specularScale;
  }
  
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
    // 1. Checkerboard pattern
    int tileX = (int)(point.x / tileSize) % 2;
    int tileZ = (int)(point.z / tileSize) % 2;
    isAlternateTile = (tileX + tileZ) % 2 == 0;
    
    // 2. Base color selection
    Color baseColor = isAlternateTile ? woodColor1 : woodColor2;
    
    // 3. Light calculations (Checkerboard's ambient/diffuse logic)
    Vector3 lightDir = light.getDirectionTo(point).normalize();
    double diffuse = Math.max(0.5, normal.dot(lightDir)); // Min 0.5 brightness guarantee
    
    // 4. Wood texture (grain effect)
    double grain = 1.0 + Math.sin(point.x * 30) * 0.2;
    Color woodColor = ColorUtil.multiply(baseColor, (float)grain);
    
    // 5. Specular (PBR)
    Vector3 viewDir = new Vector3(point, viewerPos).normalize();
    Vector3 halfway = viewDir.add(lightDir).normalize();
    double specular = Math.pow(Math.max(0, normal.dot(halfway)), 50) * specularScale;
    
    // 6. RESULT: Checkerboard's brightness guarantee + Wood texture
    return ColorUtil.add(
      ColorUtil.multiply(woodColor, (float)diffuse),
      ColorUtil.multiply(Color.WHITE, (float)(specular * 0.8))
    );
  }
  
  // PBR Properties
  @Override public Color getAlbedo() { return isAlternateTile ? woodColor1 : woodColor2; }
  @Override public double getRoughness() { return roughness; }
  @Override public double getMetalness() { return 0.0; }
  @Override public MaterialType getMaterialType() { return MaterialType.DIELECTRIC; }
  @Override public double getReflectivity() { return 0.3; }
  @Override public double getIndexOfRefraction() { return 1.53; }
  @Override public double getTransparency() { return 0.0; }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("WoodPBRMaterial woodpbrmaterial {\n");
    sb.append("        color1 = " + net.elena.murat.util.ColorUtil.toColorString(woodColor1) + ";\n");
    sb.append("        color2 = " + net.elena.murat.util.ColorUtil.toColorString(woodColor2) + ";\n");
    sb.append("        tileSize = " + tileSize + ";\n");
    sb.append("        roughness = " + roughness + ";\n");
    sb.append("        specularScale = " + specularScale + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
