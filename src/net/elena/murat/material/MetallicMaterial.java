package net.elena.murat.material;

import java.lang.reflect.Method;
import java.awt.Color;

import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.ColorUtil;

/**
 * MetallicMaterial represents a metallic surface with strong, colored specular highlights
 * and typically low diffuse reflection. It is designed to work with ray tracing
 * that handles reflections recursively.
 * This material now fully implements the extended Material interface.
 */
public class MetallicMaterial implements Material {
    private Color metallicColor; // The base color of the metal
    private Color specularColor; // The color of the specular highlight (can be metallicColor for true metals)
    private double reflectivity; // How much light is reflected (0.0 to 1.0)
    private double shininess;    // Shininess exponent for specular highlights
    private double ambientCoefficient;
    private double diffuseCoefficient;
    private double specularCoefficient;
    
    // Default values for Material interface methods, as this material is opaque and not refractive
    private final double ior = 1.0; // Index of Refraction for air/vacuum
    private final double transparency = 0.0; // Not transparent
    private Matrix4 objectInverseTransform;
    
    /**
     * Constructs a MetallicMaterial with specified properties.
     */
    public MetallicMaterial(Color metallicColor, Color specularColor, 
                          double reflectivity, double shininess,
                          double ambientCoefficient, double diffuseCoefficient, 
                          double specularCoefficient, Matrix4 objectInverseTransform) {
        this.metallicColor = metallicColor;
        this.specularColor = specularColor;
        this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
        this.shininess = Math.max(1.0, shininess);
        this.ambientCoefficient = Math.max(0.0, Math.min(1.0, ambientCoefficient));
        this.diffuseCoefficient = Math.max(0.0, Math.min(1.0, diffuseCoefficient));
        this.specularCoefficient = Math.max(0.0, Math.min(1.0, specularCoefficient));
        this.objectInverseTransform = objectInverseTransform != null ? objectInverseTransform : new Matrix4();
    }
    
    // Simplified constructor
    public MetallicMaterial(Color metallicColor, Color specularColor, 
                          double reflectivity, double shininess, Matrix4 objectInverseTransform) {
        this(metallicColor, specularColor, reflectivity, shininess, 0.1, 0.3, 0.9, objectInverseTransform);
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        this.objectInverseTransform = tm != null ? tm : new Matrix4();
    }
    
    /**
     * Calculates the direct lighting color at a given point on the metallic surface.
     */
    @Override
    public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
        // Transform to local coordinates if needed
        Point3 localPoint = objectInverseTransform.transformPoint(point);
        Vector3 localNormal = objectInverseTransform.transformVector(normal).normalize();
        
        Color lightColor = light.getColor();
        double intensity = 0.0;
        
        // Handle ambient light separately
        if (light instanceof ElenaMuratAmbientLight) {
            double ambientIntensity = ((ElenaMuratAmbientLight) light).getIntensity();
            return ColorUtil.multiplyColor(metallicColor, ambientCoefficient * ambientIntensity);
        }
        
        // Get light direction and intensity
        Vector3 lightDir = getLightDirection(light, point);
        if (lightDir == null) {
            return Color.BLACK;
        }
        
        // Get intensity based on light type
        if (light instanceof MuratPointLight) {
            intensity = ((MuratPointLight) light).getAttenuatedIntensity(point);
        } else if (light instanceof ElenaDirectionalLight) {
            intensity = ((ElenaDirectionalLight) light).getIntensity();
        } else if (light instanceof PulsatingPointLight) {
            intensity = ((PulsatingPointLight) light).getAttenuatedIntensity(point);
        } else if (light instanceof SpotLight) {
            intensity = ((SpotLight) light).getAttenuatedIntensity(point);
        } else {
            // Fallback for other light types
            try {
                Method getIntensityMethod = light.getClass().getMethod("getIntensity");
                intensity = (Double) getIntensityMethod.invoke(light);
            } catch (Exception e) {
                intensity = 1.0; // Default intensity
            }
        }
        
        // Diffuse component (low for metals)
        double NdotL = Math.max(0, localNormal.dot(lightDir));
        Color diffuse = ColorUtil.multiplyColors(
            metallicColor, lightColor, 
            diffuseCoefficient * intensity * NdotL
        );
        
        // Specular component (strong for metals)
        Vector3 viewDir = viewerPos.subtract(point).normalize();
        Vector3 reflectDir = lightDir.negate().reflect(localNormal);
        double RdotV = Math.max(0, reflectDir.dot(viewDir));
        double specularIntensity = Math.pow(RdotV, shininess);
        
        Color specular = ColorUtil.multiplyColors(
            specularColor, lightColor,
            specularCoefficient * intensity * specularIntensity
        );
        
        // Combine components
        return ColorUtil.add(diffuse, specular);
    }
    
    /**
     * Helper method to get the normalized light direction vector
     */
    private Vector3 getLightDirection(Light light, Point3 point) {
        if (light instanceof MuratPointLight) {
            return ((MuratPointLight) light).getPosition().subtract(point).normalize();
        } else if (light instanceof ElenaDirectionalLight) {
            return ((ElenaDirectionalLight) light).getDirection().negate().normalize();
        } else if (light instanceof PulsatingPointLight) {
            return ((PulsatingPointLight) light).getPosition().subtract(point).normalize();
        } else if (light instanceof SpotLight) {
            return ((SpotLight) light).getDirectionAt(point).normalize();
        } else {
            // Fallback for other light types
            try {
                Method getDirMethod = light.getClass().getMethod("getDirectionAt", Point3.class);
                Vector3 dir = (Vector3) getDirMethod.invoke(light, point);
                return dir != null ? dir.normalize() : new Vector3(0, 1, 0).normalize();
            } catch (Exception e) {
                return new Vector3(0, 1, 0).normalize(); // Safe default
            }
        }
    }
    
    @Override
    public double getReflectivity() { return reflectivity; }
    
    @Override
    public double getIndexOfRefraction() { return ior; }
    
    @Override
    public double getTransparency() { return transparency; }
    
    // Getters
    public Color getMetallicColor() { return metallicColor; }
    public Color getSpecularColor() { return specularColor; }
    public double getShininess() { return shininess; }
    public double getAmbientCoefficient() { return ambientCoefficient; }
    public double getDiffuseCoefficient() { return diffuseCoefficient; }
    public double getSpecularCoefficient() { return specularCoefficient; }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MetallicMaterial metallicmaterial {\n");
        sb.append("        metallicColor = " + ColorUtil.toColorString(metallicColor) + ";\n");
        sb.append("        specularColor = " + ColorUtil.toColorString(specularColor) + ";\n");
        sb.append("        reflectivity = " + reflectivity + ";\n");
        sb.append("        shininess = " + shininess + ";\n");
        sb.append("        ambient = " + ambientCoefficient + ";\n");
        sb.append("        diffuse = " + diffuseCoefficient + ";\n");
        sb.append("        specular = " + specularCoefficient + ";\n");
        sb.append("    }");
        return sb.toString();
    }
    
}
