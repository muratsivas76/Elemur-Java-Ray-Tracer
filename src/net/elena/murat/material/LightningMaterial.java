package net.elena.murat.material;

import java.awt.Color;
import java.util.Random;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

public class LightningMaterial implements Material {
  private final Color baseColor;
  private final double intensity;
  private final Random random;
  private long lastStrikeTime;
  private double[][] lightningPath;
  
  public LightningMaterial() {
    this(new Color(135, 206, 250), 1.5); // Electric blue
  }
  
  public LightningMaterial(Color baseColor, double intensity) {
    this.baseColor = baseColor;
    this.intensity = Math.max(0.5, Math.min(5.0, intensity));
    this.random = new Random();
    this.lastStrikeTime = System.currentTimeMillis();
    generateLightningPath();
  }
  
  private void generateLightningPath() {
    // Lichtenberg figure algorithm (fractal lightning)
    int segments = 50;
    lightningPath = new double[segments][3]; // x,y,z
    
    double x = 0, y = 1, z = 0; // Start from ceiling
    lightningPath[0] = new double[]{x, y, z};
    
    for (int i = 1; i < segments; i++) {
      // Random direction change (fractal branching)
      x += (random.nextDouble() - 0.5) * 0.3;
      y -= random.nextDouble() * 0.2; // Downward
      z += (random.nextDouble() - 0.5) * 0.1;
      
      lightningPath[i] = new double[]{x, y, z};
    }
  }
  
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
    // 1. Lightning refresh check (at random intervals)
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastStrikeTime > 2000 + random.nextInt(3000)) {
        generateLightningPath();
        lastStrikeTime = currentTime;
    }
    
    // 2. Find closest lightning segment
    double minDist = Double.MAX_VALUE;
    for (int i = 0; i < lightningPath.length - 1; i++) {
        double dist = distanceToLineSegment(
            point,
            new Point3(lightningPath[i][0], lightningPath[i][1], lightningPath[i][2]),
            new Point3(lightningPath[i+1][0], lightningPath[i+1][1], lightningPath[i+1][2])
        );
        minDist = Math.min(minDist, dist);
    }
    
    // 3. Calculate brightness (inverse square law)
    double brightness = intensity / (1.0 + 100 * minDist * minDist);
    
    // 4. Flicker effect
    double flicker = 0.8 + 0.2 * Math.sin(currentTime * 0.05);
    
    // 5. Calculate emissive color with clamping
    int red = (int)(baseColor.getRed() * brightness * flicker);
    int green = (int)(baseColor.getGreen() * brightness * flicker);
    int blue = (int)(baseColor.getBlue() * brightness * flicker);
    int alpha = (int)(255 * Math.min(1, brightness * 2));
    
    // Clamp color components using ColorUtil
    red = ColorUtil.clamp(red);
    green = ColorUtil.clamp(green);
    blue = ColorUtil.clamp(blue);
    alpha = ColorUtil.clamp(alpha);
    
    Color emissiveColor = new Color(red, green, blue, alpha);
    
    // 6. Apply lighting using LightProperties
    if (light != null) {
        LightProperties lightProps = LightProperties.getLightProperties(light, point);
        double NdotL = Math.max(0, normal.dot(lightProps.direction));
        double lightIntensity = NdotL * lightProps.intensity;
        
        // Apply lighting to emissive color
        int litRed = ColorUtil.clamp((int)(red * lightIntensity));
        int litGreen = ColorUtil.clamp((int)(green * lightIntensity));
        int litBlue = ColorUtil.clamp((int)(blue * lightIntensity));
        
        return new Color(litRed, litGreen, litBlue, alpha);
    }
    
    return emissiveColor;
  }
  
  private double distanceToLineSegment(Point3 p, Point3 a, Point3 b) {
    Vector3 ap = p.subtract(a);
    Vector3 ab = b.subtract(a);
    
    double projection = ap.dot(ab) / ab.dot(ab);
    projection = Math.max(0, Math.min(1, projection));
    
    Point3 closest = a.add(ab.scale(projection));
    return p.distance(closest);
  }
  
  @Override public double getReflectivity() { return 0.3; }
  @Override public double getIndexOfRefraction() { return 1.0; }
  @Override public double getTransparency() { return 0.8; }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("LightningMaterial lightningmaterial {\n");
    sb.append("        baseColor = " + net.elena.murat.util.ColorUtil.toColorString(baseColor) + ";\n");
    sb.append("        intensity = " + intensity + ";\n");
    sb.append("    }");
    return sb.toString();
  }

}