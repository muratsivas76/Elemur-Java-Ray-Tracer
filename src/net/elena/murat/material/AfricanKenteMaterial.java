package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class AfricanKenteMaterial implements Material {
    private final Color[] stripeColors;
    private final double scale;
    private final int phase;
    private Matrix4 objectInverseTransform;
    
    // Phong parameters for matte fabric
    private final double ambientCoefficient = 0.5;
    private final double diffuseCoefficient = 0.8;
    private final double specularCoefficient = 0.05;
    private final double shininess = 5.0;
    private final double reflectivity = 0.1;
    private final double ior = 1.0;
    private final double transparency = 0.0;
    
    /**
     * Default constructor with classic Kente palette:
     * red, yellow, green, black
     */
    public AfricanKenteMaterial() {
        this(
            new Color[]{
                new Color(204, 26, 26),    // red - RGB(0.8, 0.1, 0.1)
                new Color(242, 230, 51),   // yellow - RGB(0.95, 0.9, 0.2)
                new Color(26, 153, 51),    // green - RGB(0.1, 0.6, 0.2)
                new Color(0, 0, 0)         // black - RGB(0.0, 0.0, 0.0)
            },
            8.0,
            0,
            new Matrix4()
        );
    }
    
    public AfricanKenteMaterial(Color[] stripeColors, double scale, int phase, Matrix4 objectInverseTransform) {
        this.stripeColors = stripeColors.clone();
        this.scale = Math.max(0.1, scale);
        this.phase = phase;
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
        
        // Map stripes along X (vertical bands when viewed front-on)
        double x = localPoint.x * scale;
        int stripeIndex = ((int) Math.floor(x) + phase) % stripeColors.length;
        if (stripeIndex < 0) stripeIndex += stripeColors.length;

        Color patternColor = stripeColors[stripeIndex];
        
        // Apply lighting using LightProperties
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        
        // Diffuse component
        double NdotL = Math.max(0, worldNormal.dot(lightProps.direction));
        Color diffuse = ColorUtil.multiplyColors(
            patternColor, 
            lightProps.color, 
            diffuseCoefficient * NdotL * lightProps.intensity
        );
        
        // Specular component (low for matte fabric)
        Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
        Vector3 halfway = lightProps.direction.add(viewDir).normalize();
        double NdotH = Math.max(0, worldNormal.dot(halfway));
        double specularIntensity = Math.pow(NdotH, shininess);
        
        Color specular = ColorUtil.multiplyColors(
            Color.WHITE,
            lightProps.color,
            specularCoefficient * specularIntensity * lightProps.intensity
        );
        
        // Ambient component
        Color ambient = ColorUtil.multiplyColors(
            patternColor,
            lightProps.color,
            ambientCoefficient * lightProps.intensity
        );
        
        // Combine all components
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
    public Color[] getStripeColors() { return stripeColors.clone(); }
    public double getScale() { return scale; }
    public int getPhase() { return phase; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("AfricanKenteMaterial africankentematerial {\n");
    sb.append("    }");
    return sb.toString();
  }

}
