package net.elena.murat.material;

import java.awt.Color;
import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class MoonSurfaceMaterial implements Material {
    private final Color moonColor;
    private Matrix4 objectInverseTransform;
    
    private final double ambientCoefficient = 0.3;
    private final double diffuseCoefficient = 0.9;
    private final double specularCoefficient = 0.05;
    private final double shininess = 10.0;
    private final double reflectivity = 0.02;
    private final double ior = 1.0;
    private final double transparency = 0.0;
    
    public MoonSurfaceMaterial() {
        this.moonColor = new Color(169, 169, 169);
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
        
        double craterPattern = Math.sin(localPoint.x * 50) * Math.cos(localPoint.y * 50) * Math.sin(localPoint.z * 50);
        double surfaceVariation = 0.7 + Math.abs(craterPattern) * 0.3;
        
        Color surfaceColor = ColorUtil.multiplyColor(moonColor, surfaceVariation);
        
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
		return "MoonSurfaceMaterial moonsurfacematerial = {\n    }";
	}

}
