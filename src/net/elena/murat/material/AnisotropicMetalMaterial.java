package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class AnisotropicMetalMaterial implements Material {
    private final Color metalColor;
    private final double anisotropy;
    private final double roughnessX;
    private final double roughnessY;
    private Matrix4 objectInverseTransform;
    
    // Phong constants for metallic appearance
    private final double ambientCoefficient = 0.1;
    private final double diffuseCoefficient = 0.3;
    private final double specularCoefficient = 0.9;
    private final double shininess = 80.0;
    private final double reflectivity = 0.8;
    private final double ior = 1.0;
    private final double transparency = 0.0;
    
    // Default constructor
    public AnisotropicMetalMaterial() {
        this(new Color(179, 179, 191),   // Silver-gray metal - RGB(0.7, 0.7, 0.75)
             0.8,                        // Anisotropy strength
             0.1,                        // Roughness in X direction
             0.4,                        // Roughness in Y direction
             new Matrix4());             // Identity transform
    }
    
    // Full constructor
    public AnisotropicMetalMaterial(Color metalColor, double anisotropy, 
                                   double roughnessX, double roughnessY, 
                                   Matrix4 objectInverseTransform) {
        this.metalColor = metalColor;
        this.anisotropy = Math.max(0.0, Math.min(1.0, anisotropy));
        this.roughnessX = Math.max(0.01, Math.min(1.0, roughnessX));
        this.roughnessY = Math.max(0.01, Math.min(1.0, roughnessY));
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
        
        // Anisotropic effect - brush strokes pattern
        double brushPattern = Math.sin(localPoint.x * 50) * anisotropy;
        double anisotropicEffect = (brushPattern + 1) * 0.5;
        
        // Surface color with anisotropic variation
        Color surfaceColor = ColorUtil.multiplyColor(
            metalColor, 
            0.8 + anisotropicEffect * 0.4
        );
        
        // Apply lighting using LightProperties
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        
        // Calculate anisotropic roughness based on direction
        Vector3 lightDir = lightProps.direction;
        double dotX = Math.abs(worldNormal.dot(new Vector3(1, 0, 0)));
        double dotY = Math.abs(worldNormal.dot(new Vector3(0, 1, 0)));
        double effectiveRoughness = roughnessX * dotX + roughnessY * dotY;
        
        // Diffuse component (low for metal)
        double NdotL = Math.max(0, worldNormal.dot(lightDir));
        Color diffuse = ColorUtil.multiplyColors(
            surfaceColor, 
            lightProps.color, 
            diffuseCoefficient * NdotL * lightProps.intensity
        );
        
        // Anisotropic specular component
        Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
        Vector3 halfway = lightDir.add(viewDir).normalize();
        
        // Anisotropic specular calculation
        double NdotH = Math.max(0, worldNormal.dot(halfway));
        double specularIntensity = Math.pow(NdotH, shininess * (1.0 - effectiveRoughness));
        
        Color specular = ColorUtil.multiplyColors(
            new Color(204, 204, 217), // Metallic specular color - RGB(0.8, 0.8, 0.85)
            lightProps.color,
            specularCoefficient * specularIntensity * lightProps.intensity
        );
        
        // Ambient component
        Color ambient = ColorUtil.multiplyColors(
            surfaceColor,
            lightProps.color,
            ambientCoefficient * lightProps.intensity
        );
        
        // Combine all components (emphasize specular for metallic look)
        return ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
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
    public double getTransparency() {
        return transparency;
    }
    
    // Getters
    public Color getMetalColor() { return metalColor; }
    public double getAnisotropy() { return anisotropy; }
    public double getRoughnessX() { return roughnessX; }
    public double getRoughnessY() { return roughnessY; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("AnisotropicMetalMaterial anisotropicmetalmaterial {\n");
    sb.append("        metalColor = " + net.elena.murat.util.ColorUtil.toColorString(metalColor) + ";\n");
    sb.append("        anisotropy = " + anisotropy + ";\n");
    sb.append("        roughnessX = " + roughnessX + ";\n");
    sb.append("        roughnessY = " + roughnessY + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
