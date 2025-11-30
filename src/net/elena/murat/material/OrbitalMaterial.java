package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.util.ColorUtil;

public class OrbitalMaterial implements Material {
    private Color centerColor;
    private Color orbitColor;
    private double ringWidth;
    private int ringCount;
    private double transparency;
    private Matrix4 objectInverseTransform;
    
    // Material properties
    private final double ambientCoefficient = 0.3;
    private final double diffuseCoefficient = 0.7;
    private final double specularCoefficient = 0.4;
    private final double shininess = 50.0;
    private final double reflectivity = 0.2;
    private final double ior = 1.4;
    
    public OrbitalMaterial(Color centerColor, Color orbitColor,
                          double ringWidth, int ringCount, Matrix4 objectInverseTransform) {
        this.centerColor = centerColor;
        this.orbitColor = orbitColor;
        this.ringWidth = Math.max(0.01, ringWidth);
        this.ringCount = Math.max(1, ringCount);
        this.objectInverseTransform = objectInverseTransform != null ? objectInverseTransform : new Matrix4();
        this.transparency = calculateTransparency(centerColor);
    }
    
    public OrbitalMaterial(Color centerColor, Color orbitColor, Matrix4 objectInverseTransform) {
        this(centerColor, orbitColor, 0.1, 5, objectInverseTransform);
    }
    
    private double calculateTransparency(Color color) {
        int alpha = color.getAlpha();
        return 1.0 - ((double)alpha / 255.0);
    }
    
    @Override
    public double getTransparency() {
        return transparency;
    }
    
    @Override
    public double getReflectivity() {
        return reflectivity;
    }
    
    @Override
    public double getIndexOfRefraction() {
        return ior;
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        this.objectInverseTransform = tm != null ? tm : new Matrix4();
    }
    
    @Override
    public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewerPos) {
        // Transform to local coordinates
        Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
        
        // Calculate orbital pattern
        double pattern = calculateOrbitalPattern(localPoint);
        Color surfaceColor = ColorUtil.blendColors(centerColor, orbitColor, pattern);
        
        // Apply lighting
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        
        // Diffuse component
        double NdotL = Math.max(0, worldNormal.dot(lightDir));
        Color diffuse = ColorUtil.multiplyColors(
            surfaceColor, lightProps.color, 
            diffuseCoefficient * NdotL * lightProps.intensity
        );
        
        // Specular component
        Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
        Vector3 halfway = lightDir.add(viewDir).normalize();
        double NdotH = Math.max(0, worldNormal.dot(halfway));
        double specularIntensity = Math.pow(NdotH, shininess);
        
        Color specular = ColorUtil.multiplyColors(
            Color.WHITE, lightProps.color,
            specularCoefficient * specularIntensity * lightProps.intensity
        );
        
        // Ambient component
        Color ambient = ColorUtil.multiplyColors(
            surfaceColor, lightProps.color,
            ambientCoefficient * lightProps.intensity
        );
        
        // Combine all components
        return ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
    }
    
    private double calculateOrbitalPattern(Point3 point) {
        double distanceFromCenter = Math.sqrt(
            point.x * point.x + point.y * point.y + point.z * point.z
        );
        
        // Calculate ring pattern based on distance
        double ringPattern = Math.sin(distanceFromCenter * ringCount * Math.PI * 2);
        
        // Apply smooth step function for sharp rings
        double ringValue = smoothStep(0.5 - ringWidth, 0.5 + ringWidth, 
                                    (ringPattern + 1) * 0.5); // Normalize to 0-1
        
        return ringValue;
    }
    
    private double smoothStep(double edge0, double edge1, double x) {
        x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
        return x * x * (3.0 - 2.0 * x);
    }
    
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    // Getters
    public Color getCenterColor() { return centerColor; }
    public Color getOrbitColor() { return orbitColor; }
    public double getRingWidth() { return ringWidth; }
    public int getRingCount() { return ringCount; }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OrbitalMaterial orbitalmaterial {\n");
        sb.append("        centerColor = " + net.elena.murat.util.ColorUtil.toColorString(centerColor) + ";\n");
        sb.append("        orbitColor = " + net.elena.murat.util.ColorUtil.toColorString(orbitColor) + ";\n");
        sb.append("        ringWidth = " + ringWidth + ";\n");
        sb.append("        ringCount = " + ringCount + ";\n");
        sb.append("    }");
        return sb.toString();
    }
    
}
