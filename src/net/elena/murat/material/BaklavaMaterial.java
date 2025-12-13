package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * BaklavaMaterial - Golden layered pastry with syrup shine
 */
public class BaklavaMaterial implements Material {
    private final Color pastryColor;
    private final Color syrupColor;
    private final Color nutColor;
    private final double layers; // Number of pastry layers
    private final double syrupiness; // Syrup absorption level
    private Matrix4 objectInverseTransform;
    
    public BaklavaMaterial(Color pastryColor, Color syrupColor, Color nutColor,
                          double layers, double syrupiness, Matrix4 invTransform) {
        this.pastryColor = pastryColor;
        this.syrupColor = syrupColor;
        this.nutColor = nutColor;
        this.layers = Math.max(1.0, Math.min(20.0, layers));
        this.syrupiness = Math.max(0.0, Math.min(1.0, syrupiness));
        this.objectInverseTransform = invTransform;
    }
    
    // Classic baklava
    public BaklavaMaterial(Matrix4 invTransform) {
        this(new Color(255, 223, 0),    // Golden pastry
             new Color(210, 180, 140),  // Light brown syrup
             new Color(139, 69, 19),    // Saddle brown nuts
             12.0, 0.8, invTransform);
    }
    
    // Extra syrupy baklava
    public static BaklavaMaterial createSyrupyBaklava(Matrix4 invTransform) {
        return new BaklavaMaterial(
            new Color(255, 215, 0),     // Brighter gold
            new Color(205, 133, 63),    // Syrup color
            new Color(160, 82, 45),     // Nut color
            15.0, 0.95, invTransform
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
        
        // 2. Layered pastry pattern
        double layerPattern = Math.sin(localPoint.y * layers * 8.0) * 0.6;
        double nutPattern = Math.cos(localPoint.x * 15.0) * Math.sin(localPoint.z * 12.0);
        
        // 3. Determine material component
        Color baseColor;
        if (nutPattern > 0.5) {
            baseColor = nutColor; // Nut filling
        } else if (layerPattern > 0.3) {
            baseColor = pastryColor; // Pastry layer
        } else {
            baseColor = syrupColor; // Syrup soaked area
        }
        
        // 4. Syrup effect - glossy and translucent
        Color syrupyColor = ColorUtil.blendColors(baseColor, syrupColor, (float)syrupiness * 0.4f);
        
        // 5. Light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 6. Syrupy surface - medium specular
        double NdotL = Math.max(0, normal.dot(lightDir));
        Vector3 halfVec = lightDir.add(viewDir).normalize();
        double NdotH = Math.max(0, normal.dot(halfVec));
        
        // Syrupiness affects shininess
        double shininess = 32.0 * syrupiness;
        double specular = Math.pow(NdotH, shininess) * syrupiness;
        
        // 7. Golden color enhancement
        double diffuse = NdotL * (0.7 + 0.3 * syrupiness);
        
        // 8. Color mixing
        Color ambient = ColorUtil.multiplyColors(syrupyColor, lightProps.color, 0.25 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(syrupyColor, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        Color combined = ColorUtil.combineColors(ambient, diffuseColor, specularColor);
        
        // 9. Golden glow effect
        if (syrupiness > 0.6) {
            Color goldenGlow = ColorUtil.multiply(pastryColor, 0.2f);
            combined = ColorUtil.add(combined, goldenGlow);
        }
        
        return combined;
    }
    
    @Override public double getReflectivity() { return syrupiness * 0.3; }
    @Override public double getIndexOfRefraction() { return 1.4; } // Syrupy IOR
    @Override public double getTransparency() { return syrupiness * 0.1; } // Slightly translucent
  
    @Override
    public String toString() {
     StringBuffer sb = new StringBuffer();
     sb.append("BaklavaMaterial baklavamaterial {\n");
     sb.append("        pastryColor = " + net.elena.murat.util.ColorUtil.toColorString(pastryColor) + ";\n");
     sb.append("        syrupColor = " + net.elena.murat.util.ColorUtil.toColorString(syrupColor) + ";\n");
     sb.append("        nutColor = " + net.elena.murat.util.ColorUtil.toColorString(nutColor) + ";\n");
     sb.append("        layers = " + layers + ";\n");
     sb.append("        syrupiness = " + syrupiness + ";\n");
     sb.append("    }");
     return sb.toString();
   }

}
