import java.awt.Color;
import net.elena.murat.light.Light;
import net.elena.murat.lovert.Scene;
import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;
import net.elena.murat.math.Ray;

/**
 * Test ışığı - Dinamik yükleme için sadece boş constructor
 */
public class TestLight implements Light {
  private final Point3 position = new Point3(0, 5, 0);
  private final Color color = Color.WHITE;
  private double intensity = 1.0;
  private final double constantAttenuation = 1.0;
  private final double linearAttenuation = 0.0;
  private final double quadraticAttenuation = 0.0;
  private double[] idIntensity = new double[] {0.0, 0.0};
  
  private static int callCounter = 0;
  
  /**
   * BOŞ CONSTRUCTOR - Dinamik yükleme için
   */
  public TestLight() {
    System.out.println("═══════════════════════════════════════");
    System.out.println("TestLight!");
    System.out.println("Position: " + position);
    System.out.println("Intensity: " + intensity);
    System.out.println("Color: " + color);
    System.out.println("Attenuation: C=" + constantAttenuation + 
                       ", L=" + linearAttenuation + 
                       ", Q=" + quadraticAttenuation);
    System.out.println("═══════════════════════════════════════");
  }
  
  @Override
  public Point3 getPosition() {
    return position;
  }
  
  @Override
  public Color getColor() {
    return color;
  }
  
  @Override
  public void setIntensity(double dins) {
    System.out.println("setIntensity: " + dins);
    this.intensity = dins;
  }
  
  @Override
  public double getIntensity() {
    return intensity;
  }
  
  @Override
  public void setIncDecIntensity(double[] dn) {
    this.idIntensity = dn;
  }
  
  @Override
  public double[] getIncDecIntensity() {
    return this.idIntensity;
  }
  
  @Override
  public Vector3 getDirectionAt(Point3 point) {
    Vector3 direction = position.subtract(point);
    double len = direction.length();
    
    if (callCounter < 3) {
      System.out.println("getDirectionAt: point=" + point + ", len=" + String.format("%.2f", len));
      callCounter++;
    }
    
    return len < Ray.EPSILON ? new Vector3(0,0,0) : direction.normalize();
  }
  
  @Override
  public Vector3 getDirectionTo(Point3 point) {
    Vector3 direction = point.subtract(position);
    return direction.length() < Ray.EPSILON ? new Vector3(0,0,0) : direction.normalize();
  }
  
  @Override
  public double getAttenuatedIntensity(Point3 point) {
    double distance = position.distance(point);
    double attenuation = constantAttenuation +
      linearAttenuation * distance +
      quadraticAttenuation * distance * distance;
    double result = intensity / Math.max(attenuation, Ray.EPSILON);
    
    if (callCounter < 3) {
      System.out.println("getAttenuatedIntensity:");
      System.out.println("   Distance: " + String.format("%.2f", distance));
      System.out.println("   Attenuation: " + String.format("%.4f", attenuation));
      System.out.println("   Result: " + String.format("%.2f", result));
    }
    
    return result;
  }
  
  @Override
  public double getIntensityAt(Point3 point) {
    return getIntensity();
  }
  
  @Override
  public boolean isVisibleFrom(Point3 point, Scene scene) {
    Vector3 lightDir = getDirectionTo(point);
    double distance = getDistanceTo(point);
    Ray shadowRay = new Ray(
      point.add(lightDir.scale(Ray.EPSILON * 10)),
      lightDir
    );
    boolean visible = !scene.intersects(shadowRay, distance - Ray.EPSILON);
    
    if (callCounter < 3) {
      System.out.println("isVisibleFrom: visible=" + visible + ", dist=" + String.format("%.2f", distance));
    }
    
    return visible;
  }
  
  public double getDistanceTo(Point3 point) {
    return position.distance(point);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CustomLight testlight {\n");
    sb.append("    className = extraSML/TestLight.class;\n");
    sb.append("    position = " + position + ";\n");
    sb.append("    intensity = " + intensity + ";\n");
    sb.append("    firstAnimationIntensity = " + idIntensity[0] + ";\n");
    sb.append("    secondAnimationIntensity = " + idIntensity[1] + ";\n");
    sb.append("}");
    return sb.toString();
  }
  
}

//cd extraSML
//javac -cp ../bin/guielena.jar TestLight.java
//OR: javac -cp ../bin/elenagui.jar TestLight.java
//OR: javac -cp ../obj TestLight.java
