package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * GraniteMaterial - Strong and honest like Norwegian mountains
 */
public class GraniteMaterial implements Material {
    private final Color baseColor;
    private final double roughness;
    private final double specular;
    private final double reflectivity;
    private Matrix4 objectInverseTransform;
    
    public GraniteMaterial(Color baseColor, double roughness, double specular, 
                          double reflectivity, Matrix4 invTransform) {
        this.baseColor = baseColor;
        this.roughness = Math.max(0.0, Math.min(1.0, roughness));
        this.specular = Math.max(0.0, Math.min(1.0, specular));
        this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
        this.objectInverseTransform = invTransform;
    }
    
    public GraniteMaterial(Color baseColor, Matrix4 invTransform) {
        this(baseColor, 0.8, 0.1, 0.05, invTransform);
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
        
        // 2. Procedural granite pattern
        double noise1 = Math.sin(localPoint.x * 8.0) * Math.cos(localPoint.y * 12.0) * Math.sin(localPoint.z * 6.0);
        double noise2 = Math.cos(localPoint.x * 15.0) * Math.sin(localPoint.y * 9.0) * Math.cos(localPoint.z * 11.0);
        double pattern = (noise1 + noise2) * 0.5;
        
        // 3. Granite color variation
        Color graniteColor = pattern > 0.2 ? 
            ColorUtil.darkenColor(baseColor, 0.1) : 
            ColorUtil.lightenColor(baseColor, 0.15);
        
        // 4. Light calculations
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        Vector3 viewDir = viewPos.subtract(worldPoint).normalize();
        
        // 5. Lighting components
        double NdotL = Math.max(0, normal.dot(lightDir));
        double diffuse = NdotL * (1.0 - roughness);
        
        // Rough surface - less specular
        Vector3 halfVec = lightDir.add(viewDir).normalize();
        double NdotH = Math.max(0, normal.dot(halfVec));
        double specular = Math.pow(NdotH, 32.0) * this.specular * (1.0 - roughness);
        
        // 6. Color mixing
        Color ambient = ColorUtil.multiplyColors(graniteColor, lightProps.color, 0.15 * lightProps.intensity);
        Color diffuseColor = ColorUtil.multiplyColors(graniteColor, lightProps.color, diffuse * lightProps.intensity);
        Color specularColor = ColorUtil.multiplyColors(lightProps.color, Color.WHITE, specular * lightProps.intensity);
        
        return ColorUtil.combineColors(ambient, diffuseColor, specularColor);
    }
    
    @Override public double getReflectivity() { return reflectivity; }
    @Override public double getIndexOfRefraction() { return 1.6; } // Granite IOR
    @Override public double getTransparency() { return 0.0; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("GraniteMaterial granitematerial {\n");
    sb.append("        baseColor = " + net.elena.murat.util.ColorUtil.toColorString(baseColor) + ";\n");
    sb.append("        roughness = " + roughness + ";\n");
    sb.append("        specular = " + specular + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
