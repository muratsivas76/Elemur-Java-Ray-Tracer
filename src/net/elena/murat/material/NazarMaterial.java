package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * NazarMaterial - Turkish protection shield! Keeps evil eyes away
 */
public class NazarMaterial implements Material {
    private final Color blueColor;
    private final Color whiteColor;
    private final double glowIntensity;
    private final double transparency;
    private final double reflectivity;
    private Matrix4 objectInverseTransform;
    
    public NazarMaterial(Color blueColor, Color whiteColor, double glowIntensity, 
                        double transparency, double reflectivity, Matrix4 invTransform) {
        this.blueColor = blueColor;
        this.whiteColor = whiteColor;
        this.glowIntensity = Math.max(0.0, Math.min(1.0, glowIntensity));
        this.transparency = Math.max(0.0, Math.min(1.0, transparency));
        this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
        this.objectInverseTransform = invTransform;
    }
    
    public NazarMaterial(Matrix4 invTransform) {
        this(new Color(30, 144, 255),    // Dodger blue
             new Color(255, 255, 255),   // White
             0.3, 0.25, 0.4, invTransform);
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        if (tm == null) tm = new Matrix4();
        this.objectInverseTransform = tm;
    }
    
    @Override
    public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewPos) {
        // 1. Coordinate transformation
        Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
        Vector3 normal = objectInverseTransform.transformNormal(worldNormal).normalize();
        
        // 2. Nazar bead circular pattern
        double distanceFromCenter = Math.sqrt(localPoint.x * localPoint.x + localPoint.y * localPoint.y);
        double ringPattern = Math.sin(distanceFromCenter * 20.0) * 0.5 + 0.5;
        
        // 3. Blue-white rings
        Color baseColor = ringPattern > 0.7 ? blueColor : whiteColor;
        
        // 4. Light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 5. Glass-like reflective properties
        double NdotL = Math.max(0, normal.dot(lightDir));
        Vector3 reflectDir = lightDir.negate().reflect(normal);
        double RdotV = Math.max(0, reflectDir.dot(viewDir));
        
        // 6. Glass specular (high shininess)
        double specular = Math.pow(RdotV, 128.0) * 0.8;
        
        // 7. Self-illumination property (glow)
        Color glowColor = ColorUtil.multiply(blueColor, (float)glowIntensity);
        
        // 8. Color mixing
        Color ambient = ColorUtil.multiplyColors(baseColor, lightProps.color, 0.2 * lightProps.intensity);
        Color diffuse = ColorUtil.multiplyColors(baseColor, lightProps.color, NdotL * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        // 9. Add glow
        Color combined = ColorUtil.combineColors(ambient, diffuse, specularColor);
        return ColorUtil.add(combined, glowColor);
    }
    
    @Override public double getReflectivity() { return reflectivity; }
    @Override public double getIndexOfRefraction() { return 1.5; } // Glass IOR
    @Override public double getTransparency() { return transparency; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("NazarMaterial nazarmaterial {\n");
    sb.append("        blueColor = " + net.elena.murat.util.ColorUtil.toColorString(blueColor) + ";\n");
    sb.append("        whiteColor = " + net.elena.murat.util.ColorUtil.toColorString(whiteColor) + ";\n");
    sb.append("        glowIntensity = " + glowIntensity + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
