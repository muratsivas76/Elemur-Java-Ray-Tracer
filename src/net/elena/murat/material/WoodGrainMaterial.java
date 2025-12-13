package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class WoodGrainMaterial implements Material {
    private final Color woodColor;
    private Matrix4 objectInverseTransform;
    
    private final double ambientCoefficient = 0.4;
    private final double diffuseCoefficient = 0.8;
    private final double specularCoefficient = 0.1;
    private final double shininess = 20.0;
    private final double reflectivity = 0.05;
    private final double ior = 1.0;
    private final double transparency = 0.0;
    
    public WoodGrainMaterial() {
        this.woodColor = new Color(139, 69, 19);
        this.objectInverseTransform = new Matrix4();
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        if (tm == null) tm = new Matrix4();
        this.objectInverseTransform = tm;
    }
    
    @Override
    public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewerPos) {
        Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
        
        double woodGrain = Math.sin(localPoint.x * 20 + Math.sin(localPoint.y * 5) * 2);
        double ringPattern = Math.sin(localPoint.z * 30 + woodGrain * 5);
        
        double woodDarkness = 0.6 + ringPattern * 0.4;
        Color surfaceColor = ColorUtil.multiplyColor(woodColor, woodDarkness);
        
        LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
        Vector3 lightDir = lightProps.direction;
        
        double NdotL = Math.max(0, worldNormal.dot(lightDir));
        Color diffuse = ColorUtil.multiplyColors(surfaceColor, lightProps.color, 
                       diffuseCoefficient * NdotL * lightProps.intensity);
        
        Color ambient = ColorUtil.multiplyColors(surfaceColor, lightProps.color,
                       ambientCoefficient * lightProps.intensity);
        
        return ColorUtil.add(ambient, diffuse);
    }
    
    @Override
    public double getReflectivity() { return reflectivity; }
    @Override
    public double getIndexOfRefraction() { return ior; }
    @Override
    public double getTransparency() { return transparency; }
    
    @Override
	public String toString() {
	  return "WoodGrainMaterial woodgrainmaterial = {\n    }";
	}
	
}
