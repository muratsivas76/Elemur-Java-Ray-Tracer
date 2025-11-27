package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class HokusaiMaterial implements Material {
    public static final int PLANE_MODE = 0;
    public static final int SPHERE_MODE = 1;

    private final Color waterColor;
    private final Color foamColor;
    private final double scale;
    private final int mode; // 0: plane, 1: sphere
    private final double radius;
    
    // Phong constants for water
    private final double ambientCoefficient = 0.2;
    private final double diffuseCoefficient = 0.8;
    private final double specularCoefficient = 0.9;
    private final double shininess = 300.0;
    private final double reflectivity = 0.6;
    private final double ior = 1.33; // Water index of refraction
    private final double transparency = 0.3;
    
    private Matrix4 objectInverseTransform;
    
    // Default constructor
    public HokusaiMaterial() {
        this(new Color(26, 51, 128),     // Dark blue water - RGB(0.1, 0.2, 0.5)
             new Color(255, 255, 255),   // White foam - RGB(1.0, 1.0, 1.0)
             4.0,                        // scale
             PLANE_MODE,
             1.0,                        // radius
             new Matrix4());             // Identity transform
    }
    
    // Full constructor
    public HokusaiMaterial(Color waterColor, Color foamColor, double scale, 
                          int mode, double radius, Matrix4 objectInverseTransform) {
        this.waterColor = waterColor;
        this.foamColor = foamColor;
        this.scale = Math.max(0.1, scale);
        this.mode = (mode == SPHERE_MODE) ? SPHERE_MODE : PLANE_MODE;
        this.radius = Math.max(0.1, radius);
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
        
        double u, v;

        if (mode == SPHERE_MODE) {
            // Spherical mapping (latitude-longitude)
            double len = localPoint.length();
            if (len < 1e-6) {
                u = 0; v = 0;
            } else {
                // Normalize
                double x = localPoint.x / len;
                double y = localPoint.y / len;
                double z = localPoint.z / len;

                // Latitude: -π/2 → π/2
                double lat = Math.asin(Math.max(-1.0, Math.min(1.0, y)));
                // Longitude: -π → π
                double lon = Math.atan2(z, x);

                u = (lon / (2 * Math.PI)) + 0.5; // [0,1)
                v = (lat / Math.PI) + 0.5;       // [0,1)
            }
        } else {
            // Plane mode: XY plane
            u = localPoint.x * scale;
            v = localPoint.y * scale;
        }

        // Tile coordinates
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        // Hokusai wave pattern color
        Color waveColor = createHokusaiWave(u, v);
        
        // Apply lighting using LightProperties
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        
        // Diffuse component
        double NdotL = Math.max(0, worldNormal.dot(lightProps.direction));
        Color diffuse = ColorUtil.multiplyColors(
            waveColor, 
            lightProps.color, 
            diffuseCoefficient * NdotL * lightProps.intensity
        );
        
        // Specular component (high specular for water)
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
            waveColor,
            lightProps.color,
            ambientCoefficient * lightProps.intensity
        );
        
        // Combine all components
        return ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
    }
    
    private Color createHokusaiWave(double u, double v) {
        // Wave direction: left to right
        double waveX = u * 8.0;      // 8 repetitions
        double waveY = v * 2.0;      // slow vertical change

        // Basic wave form (Hokusai-style curve)
        double baseWave = Math.sin(waveX - waveY * 0.5) * 0.5 + 0.5;

        // Foam effect: on wave peaks
        double foam = 0.0;
        if (baseWave > 0.85) {
            // Dynamic foam: more foam with higher waves
            foam = (baseWave - 0.85) * 6.0;
            foam = Math.min(1.0, foam);
        }

        // Depth fading (darker at bottom)
        double depthFade = Math.exp(-v * 3.0); // fade towards bottom

        // Color blending
        Color water = ColorUtil.multiplyColor(waterColor, depthFade);
        return ColorUtil.blendColors(water, foamColor, (float)foam);
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
    public Color getWaterColor() { return waterColor; }
    public Color getFoamColor() { return foamColor; }
    public double getScale() { return scale; }
    public int getMode() { return mode; }
    public double getRadius() { return radius; }
    
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("HokusaiMaterial hokusaimaterial {\n");
    sb.append("        waterColor = " + net.elena.murat.util.ColorUtil.toColorString(waterColor) + ";\n");
    sb.append("        foamColor = " + net.elena.murat.util.ColorUtil.toColorString(foamColor) + ";\n");
    sb.append("        scale = " + scale + ";\n");
    sb.append("        mode = " + mode + ";\n");
    sb.append("        radius = " + radius + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}
