package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * TurkishDelightMaterial - Sweet and soft, melting texture
 */
public class TurkishDelightMaterial implements Material {
    private final Color primaryColor;
    private final Color powderColor;
    private final double softness;
    private final double sweetness; // Reflection control
    private final double transparency;
    private Matrix4 objectInverseTransform;
    
    public TurkishDelightMaterial(Color primaryColor, Color powderColor, double softness, 
                                 double sweetness, double transparency, Matrix4 invTransform) {
        this.primaryColor = primaryColor;
        this.powderColor = powderColor;
        this.softness = Math.max(0.1, Math.min(1.0, softness));
        this.sweetness = Math.max(0.0, Math.min(1.0, sweetness));
        this.transparency = Math.max(0.0, Math.min(0.5, transparency)); // Turkish delight is semi-transparent
        this.objectInverseTransform = invTransform;
    }
    
    // Pink Turkish delight (classic)
    public TurkishDelightMaterial(Matrix4 invTransform) {
        this(new Color(255, 182, 193),  // Light pink
             new Color(255, 250, 250),  // Powder sugar white
             0.8, 0.3, 0.2, invTransform);
    }
    
    // Green Turkish delight (pistachio)
    public static TurkishDelightMaterial createPistachioDelight(Matrix4 invTransform) {
        return new TurkishDelightMaterial(
            new Color(152, 251, 152),  // Pistachio green
            new Color(255, 250, 250),  // Powder sugar
            0.7, 0.4, 0.15, invTransform
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
        
        // 2. Turkish delight pattern - soft waves
        double wave1 = Math.sin(localPoint.x * 6.0 + localPoint.y * 4.0) * 0.3;
        double wave2 = Math.cos(localPoint.y * 8.0 + localPoint.z * 5.0) * 0.2;
        double softPattern = wave1 + wave2;
        
        // 3. Powder sugar effect
        double powderMask = Math.sin(localPoint.x * 15.0) * Math.cos(localPoint.y * 12.0);
        boolean hasPowder = powderMask > 0.6;
        
        // 4. Base color selection
        Color baseColor = hasPowder ? 
            ColorUtil.blendColors(primaryColor, powderColor, 0.7) : 
            primaryColor;
        
        // 5. Soft light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 6. Soft surface - low specular
        double NdotL = Math.max(0, normal.dot(lightDir));
        Vector3 halfVec = lightDir.add(viewDir).normalize();
        double NdotH = Math.max(0, normal.dot(halfVec));
        
        // Shininess adjustment based on softness
        double shininess = 16.0 * (1.0 - softness);
        double specular = Math.pow(NdotH, shininess) * sweetness;
        
        // 7. Soft shadows
        double diffuse = NdotL * (0.7 + 0.3 * softness);
        
        // 8. Semi-transparency of Turkish delight
        Color translucentEffect = ColorUtil.setAlpha(baseColor, (int)(255 * (1.0 - transparency)));
        
        // 9. Color mixing
        Color ambient = ColorUtil.multiplyColors(translucentEffect, lightProps.color, 0.25 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(translucentEffect, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        Color combined = ColorUtil.combineColors(ambient, diffuseColor, specularColor);
        
        // 10. Powder sugar brightness
        if (hasPowder) {
            Color powderHighlight = ColorUtil.multiply(powderColor, 0.3f);
            combined = ColorUtil.add(combined, powderHighlight);
        }
        
        return combined;
    }
    
    @Override public double getReflectivity() { return sweetness * 0.3; } // Sweetness affects reflection
    @Override public double getIndexOfRefraction() { return 1.3; } // Sugary IOR
    @Override public double getTransparency() { return transparency; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TurkishDelightMaterial turkishdelightmaterial {\n");
    sb.append("        primaryColor = " + net.elena.murat.util.ColorUtil.toColorString(primaryColor) + ";\n");
    sb.append("        powderColor = " + net.elena.murat.util.ColorUtil.toColorString(powderColor) + ";\n");
    sb.append("        softness = " + softness + ";\n");
    sb.append("        sweetness = " + sweetness + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
