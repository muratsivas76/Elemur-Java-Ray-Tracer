package net.elena.murat.material;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class SandMaterial implements Material {
    private final Color baseSandColor;
    private final Color darkSandColor;
    private final double grainSize;
    private final double roughness;
    private Matrix4 objectInverseTransform;
    
    // Default constructor
    public SandMaterial() {
        this(new Color(194, 179, 128),   // Light sand color - RGB(0.76, 0.70, 0.50)
             new Color(166, 148, 97),    // Dark sand color - RGB(0.65, 0.58, 0.38)
             0.05,                       // Grain size
             0.8,                        // Roughness
             new Matrix4());             // Identity transform
    }
    
    // Custom constructor
    public SandMaterial(Color baseSandColor, Color darkSandColor, 
                              double grainSize, double roughness, Matrix4 objectInverseTransform) {
        this.baseSandColor = baseSandColor;
        this.darkSandColor = darkSandColor;
        this.grainSize = Math.max(0.01, grainSize);
        this.roughness = Math.max(0.1, Math.min(1.0, roughness));
        this.objectInverseTransform = objectInverseTransform;
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        if (tm == null) tm = new Matrix4();
        this.objectInverseTransform = tm;
    }
    
    @Override
    public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewerPos) {
        // Transform to local coordinates
        Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
        
        // Sand pattern using noise functions
        double noise = improvedNoise(localPoint.x * 10, localPoint.y * 10, localPoint.z * 10);
        double grainPattern = Math.sin(localPoint.x * 100 * grainSize) * Math.cos(localPoint.y * 100 * grainSize);
        
        // Interpolate between base and dark sand colors based on pattern
        double patternFactor = (noise + grainPattern) * 0.5;
        patternFactor = Math.max(0, Math.min(1, (patternFactor + 1) * 0.5));
        
        Color surfaceColor = ColorUtil.blendColors(baseSandColor, darkSandColor, patternFactor);
        
        // Apply lighting using LightProperties
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        
        // Diffuse component
        double NdotL = Math.max(0, worldNormal.dot(lightProps.direction));
        Color diffuse = ColorUtil.multiplyColors(
            surfaceColor, 
            lightProps.color, 
            NdotL * lightProps.intensity
        );
        
        // Specular component (low specular for rough sand)
        Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
        Vector3 halfway = lightProps.direction.add(viewDir).normalize();
        double NdotH = Math.max(0, worldNormal.dot(halfway));
        double specularIntensity = Math.pow(NdotH, 1.0 / roughness) * 0.1; // Low specular
        
        Color specular = ColorUtil.multiplyColors(
            Color.WHITE,
            lightProps.color,
            specularIntensity * lightProps.intensity
        );
        
        // Ambient component (high ambient for soft sand appearance)
        Color ambient = ColorUtil.multiplyColors(
            surfaceColor,
            lightProps.color,
            0.5 * lightProps.intensity // High ambient
        );
        
        // Combine all components
        return ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
    }
    
    private double improvedNoise(double x, double y, double z) {
        // Simple perlin noise-like function
        return (Math.sin(x * 12.9898 + y * 78.233 + z * 45.164) * 43758.5453) % 1.0;
    }
    
    @Override
    public double getReflectivity() {
        return 0.05; // Low reflectivity for sand
    }
    
    @Override
    public double getIndexOfRefraction() {
        return 1.0; // No refraction for sand
    }
    
    @Override
    public double getTransparency() {
        return 0.0; // Opaque sand
    }
    
    // Getters
    public Color getBaseSandColor() { return baseSandColor; }
    public Color getDarkSandColor() { return darkSandColor; }
    public double getGrainSize() { return grainSize; }
    public double getRoughness() { return roughness; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SandMaterial sandmaterial {\n");
    sb.append("        baseSandColor = " + net.elena.murat.util.ColorUtil.toColorString(baseSandColor) + ";\n");
    sb.append("        darkSandColor = " + net.elena.murat.util.ColorUtil.toColorString(darkSandColor) + ";\n");
    sb.append("        grainSize = " + grainSize + ";\n");
    sb.append("        roughness = " + roughness + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
