package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class StarryNightMaterial implements Material {
    private final Color baseColor;
    private Matrix4 objectInverseTransform;
    
    private final double ambientCoefficient = 0.3;
    private final double diffuseCoefficient = 0.7;
    private final double specularCoefficient = 0.2;
    private final double shininess = 30.0;
    private final double reflectivity = 0.1;
    private final double ior = 1.0;
    private final double transparency = 0.0;
    
    public StarryNightMaterial() {
        this.baseColor = new Color(10, 10, 50);
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
        
        double starPattern = Math.sin(localPoint.x * 100) * Math.cos(localPoint.y * 100) * Math.sin(localPoint.z * 100);
        boolean isStar = Math.abs(starPattern) > 0.9;
        
        Color surfaceColor = isStar ? new Color(255, 255, 200) : baseColor;
        
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
	  return "StarryNightMaterial starrynightmaterial = {\n    }";
	}

}
