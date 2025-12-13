import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// Custom imports
import net.elena.murat.light.Light;
import net.elena.murat.lovert.Scene;
import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;

/**
 * CUSTOM VORTEX LIGHT - EXPERIMENTAL FEATURE
 * Warning: This custom light may not work with all materials.
 * Lighting behavior depends on material implementation.
 * Use at your own risk. Community contributions welcome!
 * 
 * Creates a swirling vortex light effect with spiral pattern.
 */
public class CustomVortexLight implements Light {
    
    private Point3 center = new Point3(0, 2, 0);
    private Color color = new Color(100, 200, 255); // Blue vortex
    private double intensity = 1.0;
    private double[] idIntensity = new double[]{0.0, 0.0};
    
    // Vortex parameters
    private double spiralRadius = 3.0;
    private double spiralTightness = 0.5;
    private int spiralPoints = 8;
    
    /**
     * No-args constructor required for dynamic loading
     */
    public CustomVortexLight() {
        System.out.println("CUSTOM VORTEX LIGHT - EXPERIMENTAL: May not work with all materials");
    }
    
    private List<Point3> getSpiralPoints() {
        List<Point3> points = new ArrayList<>();
        double animatedIntensity = getAnimatedIntensity();
        
        for (int i = 0; i < spiralPoints; i++) {
            double angle = i * (2 * Math.PI / spiralPoints);
            double radius = spiralRadius * animatedIntensity;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + i * spiralTightness;
            points.add(new Point3(x, y, z));
        }
        return points;
    }
    
    private double getAnimatedIntensity() {
        return Math.max(0.1, intensity + idIntensity[0] + idIntensity[1]);
    }
    
    @Override
    public Point3 getPosition() {
        return center;
    }
    
    @Override
    public Color getColor() {
        double anim = getAnimatedIntensity();
        int r = (int)(color.getRed() * anim);
        int g = (int)(color.getGreen() * anim);
        int b = (int)(color.getBlue() * anim);
        return new Color(
            Math.min(255, r),
            Math.min(255, g), 
            Math.min(255, b)
        );
    }
    
    @Override
    public void setIntensity(double intensity) {
        this.intensity = Math.max(0, intensity);
    }
    
    @Override
    public double getIntensity() {
        return getAnimatedIntensity();
    }
    
    @Override
    public void setIncDecIntensity(double[] incDecIntensity) {
        this.idIntensity = incDecIntensity;
    }
    
    @Override
    public double[] getIncDecIntensity() {
        return idIntensity;
    }
    
    @Override
    public Vector3 getDirectionAt(Point3 point) {
        List<Point3> spiral = getSpiralPoints();
        Point3 closest = spiral.get(0);
        double minDist = Double.MAX_VALUE;
        
        for (Point3 p : spiral) {
            double dist = p.distance(point);
            if (dist < minDist) {
                minDist = dist;
                closest = p;
            }
        }
        return closest.subtract(point).normalize();
    }
    
    @Override
    public double getAttenuatedIntensity(Point3 point) {
        double dist = center.distance(point);
        double attenuation = 1.0 / (1.0 + 0.2 * dist);
        return getAnimatedIntensity() * attenuation;
    }
    
    @Override
    public double getIntensityAt(Point3 point) {
        return getAttenuatedIntensity(point);
    }
    
    @Override
    public Vector3 getDirectionTo(Point3 point) {
        return getDirectionAt(point);
    }
    
    @Override
    public boolean isVisibleFrom(Point3 point, Scene scene) {
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CustomVortexLight customvortexlight {\n");
        sb.append("    // EXPERIMENTAL: May not work with all materials\n");
        sb.append("    className = extraSML/CustomVortexLight.class;\n");
        sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
        sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n}");
        return sb.toString();
    }
}
