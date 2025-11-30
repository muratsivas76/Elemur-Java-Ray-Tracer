import java.awt.Color;

// Custom imports
import net.elena.murat.light.Light;
import net.elena.murat.math.*;
import net.elena.murat.material.Material;

/**
 * A simple test material implementation for demonstrating custom material loading.
 * Creates a gradient color pattern based on object coordinates with basic lighting.
 */
public class TestMaterial implements Material {
    
    private Matrix4 objectTransform = Matrix4.identity();
    
    /**
     * No-args constructor required for dynamic loading
     */
    public TestMaterial() {
        System.out.println("TestMaterial created successfully");
    }
    
    @Override
    public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
        // Transform point to object's local space - DÜZELTİLDİ: multiply yerine transformPoint
        Point3 localPoint = objectTransform.transformPoint(point);
        
        // Create gradient pattern based on local coordinates
        double red = (Math.sin(localPoint.x * 2.0) + 1.0) / 2.0;
        double green = (Math.sin(localPoint.y * 2.0) + 1.0) / 2.0;
        double blue = (Math.sin(localPoint.z * 2.0) + 1.0) / 2.0;
        
        // Calculate basic diffuse lighting
        Vector3 lightDir = light.getDirectionTo(point);
        double diffuse = Math.max(0.0, normal.dot(lightDir));
        
        // Apply lighting to colors
        int r = (int)(red * diffuse * 255);
        int g = (int)(green * diffuse * 255);
        int b = (int)(blue * diffuse * 255);
        
        // Clamp values to valid color range
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return new Color(r, g, b);
    }
    
    @Override
    public double getReflectivity() {
        return 0.2;
    }
    
    @Override
    public double getIndexOfRefraction() {
        return 1.0;
    }
    
    @Override
    public double getTransparency() {
        return 0.0;
    }
    
    @Override
    public void setObjectTransform(Matrix4 tm) {
        this.objectTransform = tm;
    }

    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("CustomMaterial custommaterial {\n");
      sb.append("    className = extraSML/TestMaterial.class;\n}");
      return sb.toString();
    }
    
}
//cd extraSML
//javac -cp ../bin/guielena.jar TestMaterial.java
//OR: javac -cp ../bin/elenagui.jar TestMaterial.java
//OR: javac -cp ../obj TestMaterial.java
