import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.lovert.Scene;
import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;

/**
 * HOLOGRAM GRID LIGHT - Sci-fi holographic grid effect
 * Creates a blue grid pattern that flickers like a hologram
 */
public class CustomHologramGridLight implements Light {
    
    private Point3 position = new Point3(0, 2, 0);
    private Color baseColor = new Color(0, 200, 255); // Cyber blue
    private double intensity = 4.0;
    private double[] idIntensity = new double[]{0.0, 0.0};
    private long startTime = System.currentTimeMillis();
    
    // Hologram parameters - FIXED, user cannot change
    private double gridSize = 2.0;
    private double flickerSpeed = 0.03;
    private double pulseSpeed = 0.002;
    
    public CustomHologramGridLight() {
        System.out.println("HOLOGRAM GRID LIGHT - Cyber grid activated");
    }
    
    private double getHologramFlicker() {
        double time = (System.currentTimeMillis() - startTime) * flickerSpeed;
        // Complex flicker pattern for hologram effect
        double flicker = Math.sin(time) * 0.3 + 
                        Math.sin(time * 2.7) * 0.2 + 
                        Math.sin(time * 5.3) * 0.1;
        return flicker;
    }
    
    private double getPulseEffect() {
        double time = (System.currentTimeMillis() - startTime) * pulseSpeed;
        return Math.sin(time) * 0.4 + 0.6; // 0.2 to 1.0 pulse
    }
    
    private double getGridPattern(Point3 point) {
        // Create grid pattern based on position
        double x = Math.abs(point.x - position.x) % gridSize;
        double z = Math.abs(point.z - position.z) % gridSize;
        
        // Grid lines - stronger intensity near grid lines
        double gridEffect = 1.0 - Math.min(x, z) / (gridSize * 0.5);
        return Math.max(0.0, gridEffect * 0.3);
    }

    @Override
    public Point3 getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        double pulse = getPulseEffect();
        double flicker = getHologramFlicker();
        double totalIntensity = Math.max(0.5, intensity + flicker + idIntensity[0] + idIntensity[1]);
        
        // Hologram blue with pulse effect
        int r = (int)(0 * pulse);
        int g = (int)(baseColor.getGreen() * totalIntensity * pulse);
        int b = (int)(baseColor.getBlue() * totalIntensity);
        
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
        return intensity + idIntensity[0] + idIntensity[1] + getHologramFlicker();
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
        return position.subtract(point).normalize();
    }

    @Override
    public double getAttenuatedIntensity(Point3 point) {
        double distance = position.distance(point);
        double attenuation = 1.0 / (1.0 + 0.05 * distance);
        double gridEffect = getGridPattern(point);
        double flicker = getHologramFlicker();
        
        return (intensity + flicker + gridEffect) * attenuation * 3.0;
    }

    @Override
    public double getIntensityAt(Point3 point) {
        return getAttenuatedIntensity(point);
    }

    @Override
    public Vector3 getDirectionTo(Point3 point) {
        return position.subtract(point).normalize();
    }

    @Override
    public boolean isVisibleFrom(Point3 point, Scene scene) {
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CustomHologramGridLight customhologramgridlight {\n");
        sb.append("    className = extraSML/CustomHologramGridLight.class;\n");
        sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
        sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n}");
        return sb.toString();
    }
    
}
