package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * MirrorMaterial - Perfect reflective surface like a clean mirror
 */
public class MirrorMaterial implements Material {
    private final Color tintColor;
    private final double reflectivity;
    private final double sharpness; // Reflection clarity
    private Matrix4 objectInverseTransform;
    
    public MirrorMaterial(Color tintColor, double reflectivity, double sharpness, Matrix4 invTransform) {
        this.tintColor = tintColor;
        this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
        this.sharpness = Math.max(0.1, Math.min(1.0, sharpness));
        this.objectInverseTransform = invTransform;
    }
    
    // Perfect silver mirror
    public MirrorMaterial(Matrix4 invTransform) {
        this(new Color(255, 255, 255),  // No tint
             0.95, 0.98, invTransform);
    }
    
    // Tinted mirror (bronze, blue, etc.)
    public static MirrorMaterial createTintedMirror(Color tint, Matrix4 invTransform) {
        return new MirrorMaterial(tint, 0.85, 0.9, invTransform);
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
        
        // 2. Mirror surface - primarily reflective
        // For mirrors, we rely on the ray tracer's reflection rays
        // This method handles direct lighting only
        
        // 3. Light calculations for direct illumination
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 4. Perfect mirror reflection vector
        Vector3 reflectDir = viewDir.negate().reflect(normal);
        
        // 5. Mirror surface has very high specular reflection
        double NdotL = Math.max(0, normal.dot(lightDir));
        double RdotV = Math.max(0, reflectDir.dot(viewDir));
        
        // 6. Mirror-like specular (very sharp and bright)
        double specular = Math.pow(RdotV, 256.0) * reflectivity * sharpness;
        
        // 7. Minimal diffuse component for mirror surfaces
        double diffuse = NdotL * 0.1; // Very little diffuse
        
        // 8. Color mixing with tint
        Color baseColor = tintColor;
        Color ambient = ColorUtil.multiplyColors(baseColor, lightProps.color, 0.05 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(baseColor, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        Color combined = ColorUtil.combineColors(ambient, diffuseColor, specularColor);
        
        // 9. Apply mirror tint
        if (!tintColor.equals(Color.WHITE)) {
            combined = ColorUtil.multiplyColors(combined, tintColor, 0.3);
        }
        
        return combined;
    }
    
    @Override public double getReflectivity() { return reflectivity; }
    @Override public double getIndexOfRefraction() { return 1.0; } // Mirror doesn't refract
    @Override public double getTransparency() { return 0.0; }
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("MirrorMaterial mirrormaterial {\n");
    sb.append("        tintColor = " + net.elena.murat.util.ColorUtil.toColorString(tintColor) + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("        sharpness = " + sharpness + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
