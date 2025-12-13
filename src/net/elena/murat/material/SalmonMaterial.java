package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * SalmonMaterial - Norwegian salmon, fresh and healthy
 */
public class SalmonMaterial implements Material {
    private final Color fleshColor;
    private final Color skinColor;
    private final Color fatColor;
    private final double freshness;
    private final double oiliness;  // Oil content (shininess)
    private Matrix4 objectInverseTransform;
    
    public SalmonMaterial(Color fleshColor, Color skinColor, Color fatColor, 
                         double freshness, double oiliness, Matrix4 invTransform) {
        this.fleshColor = fleshColor;
        this.skinColor = skinColor;
        this.fatColor = fatColor;
        this.freshness = Math.max(0.0, Math.min(1.0, freshness));
        this.oiliness = Math.max(0.0, Math.min(1.0, oiliness));
        this.objectInverseTransform = invTransform;
    }
    
    // Fresh Norwegian salmon
    public SalmonMaterial(Matrix4 invTransform) {
        this(new Color(255, 145, 164),  // Salmon pink
             new Color(200, 100, 80),   // Skin color
             new Color(255, 220, 180),  // Fat color (light yellow)
             0.9, 0.7, invTransform);
    }
    
    // Smoked salmon
    public static SalmonMaterial createSmokedSalmon(Matrix4 invTransform) {
        return new SalmonMaterial(
            new Color(255, 160, 122),  // Smoked salmon color
            new Color(150, 75, 60),    // Dark skin
            new Color(255, 200, 150),  // Light fat
            0.6, 0.8, invTransform
        );
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
        
        // 2. Salmon fillet pattern - fibrous structure
        double fiberPattern = Math.sin(localPoint.x * 20.0 + localPoint.y * 15.0) * 0.4;
        double marbling = Math.cos(localPoint.y * 25.0) * Math.sin(localPoint.z * 18.0) * 0.3;
        
        // 3. Skin/flesh distribution
        double skinThreshold = 0.6;
        double fatThreshold = 0.8;
        double patternValue = Math.abs(fiberPattern + marbling);
        
        Color baseColor;
        if (patternValue > fatThreshold) {
            baseColor = fatColor; // Fat layer
        } else if (patternValue > skinThreshold) {
            baseColor = skinColor; // Skin layer
        } else {
            baseColor = fleshColor; // Flesh layer
        }
        
        // 4. Freshness effect - fresh salmon has more vibrant color
        Color freshColor = ColorUtil.adjustSaturation(baseColor, 0.5f + (float)freshness * 0.5f);
        freshColor = ColorUtil.adjustExposure(freshColor, 0.8f + (float)freshness * 0.4f);
        
        // 5. Light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 6. Oily surface - high specular
        double NdotL = Math.max(0, normal.dot(lightDir));
        Vector3 reflectDir = lightDir.negate().reflect(normal);
        double RdotV = Math.max(0, reflectDir.dot(viewDir));
        
        // Shininess based on oiliness
        double shininess = 64.0 * oiliness;
        double specular = Math.pow(RdotV, shininess) * oiliness;
        
        // 7. Wet/sticky surface effect
        double diffuse = NdotL * (0.6 + 0.4 * freshness);
        
        // 8. Color mixing
        Color ambient = ColorUtil.multiplyColors(freshColor, lightProps.color, 0.2 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(freshColor, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        Color combined = ColorUtil.combineColors(ambient, diffuseColor, specularColor);
        
        // 9. Fresh fish wetness effect
        if (freshness > 0.7) {
            Color wetEffect = ColorUtil.multiply(ColorUtil.blendColors(combined, Color.BLUE, 0.1f), 1.1f);
            combined = ColorUtil.blendColors(combined, wetEffect, (float)oiliness * 0.3f);
        }
        
        return combined;
    }
    
    @Override public double getReflectivity() { return oiliness * 0.4; } // Oily surface reflects
    @Override public double getIndexOfRefraction() { return 1.33; } // Close to water IOR
    @Override public double getTransparency() { return 0.05 * (1.0 - freshness); } // Stale fish is more opaque
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SalmonMaterial salmonmaterial {\n");
    sb.append("        fleshColor = " + net.elena.murat.util.ColorUtil.toColorString(fleshColor) + ";\n");
    sb.append("        skinColor = " + net.elena.murat.util.ColorUtil.toColorString(skinColor) + ";\n");
    sb.append("        fatColor = " + net.elena.murat.util.ColorUtil.toColorString(fatColor) + ";\n");
    sb.append("        freshness = " + freshness + ";\n");
    sb.append("        oiliness = " + oiliness + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
