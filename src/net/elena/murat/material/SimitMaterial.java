package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * SimitMaterial - Turkish sesame bread ring with crusty texture
 */
public class SimitMaterial implements Material {
    private final Color crustColor;
    private final Color sesameColor;
    private final Color softColor;
    private final double crispiness; // Crust hardness
    private final double sesameDensity; // Sesame coverage
    private Matrix4 objectInverseTransform;
    
    public SimitMaterial(Color crustColor, Color sesameColor, Color softColor,
                        double crispiness, double sesameDensity, Matrix4 invTransform) {
        this.crustColor = crustColor;
        this.sesameColor = sesameColor;
        this.softColor = softColor;
        this.crispiness = Math.max(0.0, Math.min(1.0, crispiness));
        this.sesameDensity = Math.max(0.0, Math.min(1.0, sesameDensity));
        this.objectInverseTransform = invTransform;
    }
    
    // Classic Istanbul simit
    public SimitMaterial(Matrix4 invTransform) {
        this(new Color(210, 180, 140),   // Golden brown crust
             new Color(245, 222, 179),   // Sesame seed color
             new Color(255, 248, 220),   // Soft interior color
             0.8, 0.7, invTransform);
    }
    
    // Extra crispy simit
    public static SimitMaterial createCrispySimit(Matrix4 invTransform) {
        return new SimitMaterial(
            new Color(185, 150, 110),    // Darker crust
            new Color(245, 222, 179),    // Sesame seeds
            new Color(255, 245, 230),    // Soft interior
            0.95, 0.8, invTransform
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
        
        // 2. Simit texture patterns
        double crustPattern = Math.sin(localPoint.x * 12.0) * Math.cos(localPoint.y * 10.0);
        double sesamePattern = Math.sin(localPoint.x * 25.0) * Math.cos(localPoint.z * 20.0);
        double softPattern = Math.cos(localPoint.y * 8.0) * Math.sin(localPoint.z * 6.0);
        
        // 3. Determine material component
        Color baseColor;
        if (sesamePattern > 0.8 - (sesameDensity * 0.3)) {
            baseColor = sesameColor; // Sesame seed
        } else if (crustPattern > 0.4 - (crispiness * 0.2)) {
            baseColor = crustColor; // Crust area
        } else {
            baseColor = softColor; // Soft interior
        }
        
        // 4. Crispiness effect - darker and rougher
        Color crispyColor = ColorUtil.darkenColor(baseColor, crispiness * 0.1);
        
        // 5. Light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 6. Surface properties based on crispiness
        double NdotL = Math.max(0, normal.dot(lightDir));
        Vector3 halfVec = lightDir.add(viewDir).normalize();
        double NdotH = Math.max(0, normal.dot(halfVec));
        
        // Crispy crust has different specular
        double shininess = baseColor.equals(sesameColor) ? 40.0 : (30.0 * (1.0 - crispiness));
        double specular = Math.pow(NdotH, shininess) * (1.0 - crispiness) * 0.3;
        
        // 7. Diffuse component
        double diffuse = NdotL * (0.6 + 0.4 * (1.0 - crispiness));
        
        // 8. Color mixing
        Color ambient = ColorUtil.multiplyColors(crispyColor, lightProps.color, 0.3 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(crispyColor, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        Color combined = ColorUtil.combineColors(ambient, diffuseColor, specularColor);
        
        // 9. Freshly baked glow
        if (crispiness > 0.7) {
            Color freshGlow = ColorUtil.multiply(crustColor, 0.15f);
            combined = ColorUtil.add(combined, freshGlow);
        }
        
        return combined;
    }
    
    @Override public double getReflectivity() { return 0.05; } // Bread doesn't reflect much
    @Override public double getIndexOfRefraction() { return 1.2; } // Bread-like IOR
    @Override public double getTransparency() { return 0.0; } // Opaque
  
    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("SimitMaterial simitmaterial {\n");
      sb.append("        crustColor = " + net.elena.murat.util.ColorUtil.toColorString(crustColor) + ";\n");
      sb.append("        sesameColor = " + net.elena.murat.util.ColorUtil.toColorString(sesameColor) + ";\n");
      sb.append("        softColor = " + net.elena.murat.util.ColorUtil.toColorString(softColor) + ";\n");
      sb.append("        crispiness = " + crispiness + ";\n");
      sb.append("        sesameDensity = " + sesameDensity + ";\n");
      sb.append("    }");
      return sb.toString();
    }

}
