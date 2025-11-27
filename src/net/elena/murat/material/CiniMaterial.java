package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * CiniMaterial - Traditional Turkish Iznik tile material
 * with geometric patterns and glossy finish
 */
public class CiniMaterial implements Material {
  private final Color baseColor;
  private final Color patternColor;
  private final double glossiness;
  private Matrix4 objectInverseTransform;
  
  // Traditional Iznik tile colors
  public static final Color IZNIK_BLUE = new Color(0, 102, 204);
  public static final Color IZNIK_RED = new Color(204, 0, 51);
  public static final Color IZNIK_TURQUOISE = new Color(0, 153, 153);
  public static final Color IZNIK_WHITE = new Color(255, 255, 255);
  public static final Color IZNIK_GREEN = new Color(0, 153, 76);
  
  public CiniMaterial(Color baseColor, Color patternColor, double glossiness, Matrix4 invTransform) {
    this.baseColor = baseColor;
    this.patternColor = patternColor;
    this.glossiness = Math.max(0.1, Math.min(1.0, glossiness));
    this.objectInverseTransform = invTransform;
  }
  
  // Classic blue-white Iznik tile
  public CiniMaterial(Matrix4 invTransform) {
    this(IZNIK_WHITE, IZNIK_BLUE, 0.8, invTransform);
  }
  
  // Red Iznik style
  public static CiniMaterial createRedIznik(Matrix4 invTransform) {
    return new CiniMaterial(IZNIK_WHITE, IZNIK_RED, 0.85, invTransform);
  }
  
  // Turquoise Iznik style
  public static CiniMaterial createTurquoiseIznik(Matrix4 invTransform) {
    return new CiniMaterial(IZNIK_WHITE, IZNIK_TURQUOISE, 0.8, invTransform);
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4();
    this.objectInverseTransform = tm;
  }
  
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewPos) {
    // Transform to object space for pattern generation
    Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
    Vector3 normal = objectInverseTransform.transformNormal(worldNormal).normalize();
    
    // Generate traditional geometric pattern
    Color finalColor = generateCiniPattern(localPoint, baseColor, patternColor);
    
    // Lighting calculations
    LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
    Vector3 lightDir = lightProps.direction;
    Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
    
    // Diffuse component
    double NdotL = Math.max(0, normal.dot(lightDir));
    double diffuse = NdotL * 0.7;
    
    // Specular component for glossy ceramic finish
    Vector3 reflectDir = lightDir.reflect(normal);
    double RdotV = Math.max(0, reflectDir.dot(viewDir));
    double specular = Math.pow(RdotV, 128.0) * glossiness;
    
    // Combine lighting
    Color ambient = ColorUtil.multiplyColors(finalColor, lightProps.color, 0.3 * lightProps.intensity);
    Color diffuseColor = ColorUtil.multiplyColors(finalColor, lightProps.color, diffuse * lightProps.intensity);
    Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
    
    return ColorUtil.combineColors(ambient, diffuseColor, specularColor);
  }
  
  private Color generateCiniPattern(Point3 localPoint, Color base, Color pattern) {
    // Scale for pattern size
    double scale = 8.0;
    double x = localPoint.x * scale;
    double y = localPoint.y * scale;
    double z = localPoint.z * scale;
    
    // Traditional geometric patterns - floral and geometric motifs
    // Using multiple frequency patterns for complexity
    
    // Flower pattern
    double flower = Math.sin(x * 2) * Math.cos(y * 2) +
    Math.sin(x * 3 + y * 2) * 0.5;
    
    // Geometric border
    double border = Math.max(Math.abs(Math.sin(x * 4)), Math.abs(Math.sin(y * 4)));
    
    // Central medallion pattern
    double distance = Math.sqrt(x * x + y * y);
    double medallion = Math.sin(distance * 6) * 0.7 +
    Math.cos(x * 5) * Math.sin(y * 5) * 0.3;
    
    // Combine patterns
    double patternMask = Math.max(flower, Math.max(border * 0.3, medallion));
    
    if (patternMask > 0.4) {
      return pattern;
      } else if (patternMask > 0.2) {
      // Blend between base and pattern for subtle details
      return ColorUtil.blendColors(base, pattern, 0.5);
    }
    
    return base;
  }
  
  @Override public double getReflectivity() { return glossiness * 0.3; }
  @Override public double getIndexOfRefraction() { return 1.4; } // Ceramic-like
  @Override public double getTransparency() { return 0.0; }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CiniMaterial cinimaterial {\n");
    sb.append("        baseColor = " + net.elena.murat.util.ColorUtil.toColorString(baseColor) + ";\n");
    sb.append("        patternColor = " + net.elena.murat.util.ColorUtil.toColorString(patternColor) + ";\n");
    sb.append("        glossiness = " + glossiness + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
