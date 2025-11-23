import java.awt.Color;

import net.elena.murat.light.Light;
import net.elena.murat.lovert.Scene;
import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;

public class CustomCandleLight implements Light {
    private Point3 position = new Point3(0, 3, 0);
    private Color color = new Color(255, 150, 100);
    private double intensity = 8.0;
    private double[] idIntensity = new double[]{0.0, 0.0};
    private long startTime = System.currentTimeMillis();

    public CustomCandleLight() {
        System.out.println("SUPER CANDLE - FULL POWER");
    }

    private double getFlicker() {
        double time = (System.currentTimeMillis() - startTime) * 0.005;
        return (Math.sin(time) + Math.sin(time * 2.1) + Math.sin(time * 3.7)) * 0.15;
    }

    @Override
    public Point3 getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        double total = Math.max(1.0, intensity + getFlicker() + idIntensity[0] + idIntensity[1]);
        int r = Math.min(255, (int)(color.getRed() * total * 0.8));
        int g = Math.min(255, (int)(color.getGreen() * total * 0.6));
        int b = Math.min(255, (int)(color.getBlue() * total * 0.4));
        return new Color(r, g, b);
    }

    @Override
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public double getIntensity() {
        return intensity + idIntensity[0] + idIntensity[1] + getFlicker();
    }

    @Override
    public void setIncDecIntensity(double[] idIntensity) {
        this.idIntensity = idIntensity;
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
        double attenuation = 1.0 / (1.0 + 0.02 * distance);
        return getIntensity() * attenuation * 5.0;
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
        return "CustomCandleLight customcandlelight {\n" +
               "    className \"extraSML/CustomCandleLight.class\"\n" +
               "    firstAnimationIntensity " + idIntensity[0] + "\n" +
               "    secondAnimationIntensity " + idIntensity[1] + "\n" +
               "}";
    }
    
}
