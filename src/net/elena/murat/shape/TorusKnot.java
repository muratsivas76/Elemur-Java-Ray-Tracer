package net.elena.murat.shape;

import java.awt.Color;
import java.util.List;

import net.elena.murat.math.*;
import net.elena.murat.material.Material;

/**
 * (p,q) type torus knot
 * EMShape arayüzünü tam olarak uygular
 */
public class TorusKnot implements EMShape {
  private final double R;  // Torus major radius
  private final double r;  // Knot tube radius
  private final int p;     // Knot p parametresi
  private final int q;     // Knot q parametresi
  private Material material;
  private Matrix4 transform = Matrix4.identity();
  private Matrix4 inverseTransform = Matrix4.identity();
  private Color shadowColor = Color.BLACK;
  private double shadowBias = 0.001;
  private Matrix4[] animTransforms = new Matrix4[] {new Matrix4(), new Matrix4()};
  
  private String otherAnimationInfo = "veofr,0e0fr,veofr";
  private boolean isVisibleSpecial = true; 
  private boolean isVisible = true; 
  private boolean isShadowEnable = true;
  private boolean isShadowOnly = false;
  private boolean isReflective = true;
  private boolean isRefractive = true;
  
  private String name = "torusknot";
  
  public TorusKnot(double R, double r, int p, int q) {
    this.R = Math.max(0.1, R);
    this.r = Math.max(0.05, r);
    this.p = p;
    this.q = q;
  }
  
  @Override
  public double intersect(Ray ray) {
    Ray localRay = new Ray(
      inverseTransform.transformPoint(ray.getOrigin()),
      inverseTransform.transformVector(ray.getDirection()).normalize()
    );
    
    // Sphere marching ile kesişim
    double t = 0.0;
    double stepSize = 0.05;
    int maxSteps = 200;
    double threshold = 0.005;
    
    for (int i = 0; i < maxSteps; i++) {
      Point3 point = localRay.pointAtParameter(t);
      double dist = signedDistanceFunction(point);
      
      if (dist < threshold) {
        if (t > Ray.EPSILON) {
          return t;
        }
        break;
      }
      
      if (t > 100.0) break;
      t += Math.max(dist * 0.5, stepSize);
    }
    
    return -1.0;
  }
  
  /**
   * Calculates all intersection intervals between a ray and this torus knot using ray marching.
   * Detects both entry (tIn) and exit (tOut) points by monitoring the SDF sign change.
   * @param ray The ray to test, in world coordinates.
   * @return A list of IntersectionInterval objects. Empty if no valid interval.
   */
  @Override
  public List<IntersectionInterval> intersectAll(Ray ray) {
    // 1. Transform the ray into local space
    Ray localRay = new Ray(
      inverseTransform.transformPoint(ray.getOrigin()),
      inverseTransform.transformVector(ray.getDirection()).normalize()
    );
    
    double t = 0.0;
    double stepSize = 0.05;
    int maxSteps = 200;
    double threshold = 0.005;
    double maxDistance = 100.0;
    
    List<IntersectionInterval> intervals = new java.util.ArrayList<>();
    boolean isInside = false;
    double tIn = -1;
    
    for (int i = 0; i < maxSteps; i++) {
      Point3 point = localRay.pointAtParameter(t);
      double dist = signedDistanceFunction(point);
      
      boolean wasInside = isInside;
      isInside = dist < threshold;
      
      // Entry: from outside to inside
      if (!wasInside && isInside && tIn < 0 && t > Ray.EPSILON) {
        tIn = t;
      }
      
      // Exit: from inside to outside
      if (wasInside && !isInside && tIn >= 0) {
        double tOut = t;
        
        // Create Intersection objects
        Point3 worldIn = ray.pointAtParameter(tIn);
        Point3 worldOut = ray.pointAtParameter(tOut);
        Vector3 normalIn = getNormalAt(worldIn);
        Vector3 normalOut = getNormalAt(worldOut);
        Intersection in = new Intersection(worldIn, normalIn, tIn, this);
        Intersection out = new Intersection(worldOut, normalOut, tOut, this);
        
        intervals.add(new IntersectionInterval(tIn, tOut, in, out));
        tIn = -1; // Reset for next interval
      }
      
      // Move forward
      t += Math.max(dist * 0.5, stepSize);
      
      if (t > maxDistance) {
        // Handle case where ray ends inside (optional)
        break;
      }
    }
    
    return intervals;
  }
  
  private double signedDistanceFunction(Point3 p) {
    double theta = Math.atan2(p.y, p.x);
    double phi = (this.q * theta) / this.p; // Düzeltme: this.p kullanıldı
    
    Point3 knotPos = new Point3(
      (R + r * Math.cos(phi)) * Math.cos(theta),
      (R + r * Math.cos(phi)) * Math.sin(theta),
      r * Math.sin(phi)
    );
    
    return p.subtract(knotPos).length() - (r * 0.3);
  }
  
  @Override
  public Vector3 getNormalAt(Point3 worldPoint) {
    Point3 localPoint = inverseTransform.transformPoint(worldPoint);
    
    double eps = 0.001;
    double dx = signedDistanceFunction(localPoint.add(new Vector3(eps, 0, 0))) -
    signedDistanceFunction(localPoint.add(new Vector3(-eps, 0, 0)));
    double dy = signedDistanceFunction(localPoint.add(new Vector3(0, eps, 0))) -
    signedDistanceFunction(localPoint.add(new Vector3(0, -eps, 0)));
    double dz = signedDistanceFunction(localPoint.add(new Vector3(0, 0, eps))) -
    signedDistanceFunction(localPoint.add(new Vector3(0, 0, -eps)));
    
    Vector3 localNormal = new Vector3(dx, dy, dz).normalize();
    return inverseTransform.inverseTransposeForNormal().transformVector(localNormal).normalize();
  }
  
  @Override
  public void setTransform(Matrix4 transform) {
    this.transform = new Matrix4(transform);
    this.inverseTransform = transform.inverse();
  }
  
  @Override
  public Matrix4 getTransform() {
    return new Matrix4(transform);
  }
  
  @Override
  public Matrix4 getInverseTransform() {
    return new Matrix4(inverseTransform);
  }
  
  @Override
  public Material getMaterial() {
    return material;
  }
  
  @Override
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  @Override
  public void setAnimationTransforms(Matrix4[] atm) {
    this.animTransforms = atm;
  }
  
  @Override
  public Matrix4[] getAnimationTransforms() {
    return this.animTransforms;
  }
  
  @Override
  public Color getShadowColor() {
	  return this.shadowColor;
  }
  
  @Override
  public void setShadowColor(Color color) {
	  this.shadowColor = color;
  }
  
  @Override
  public double getShadowBias() {
	  return this.shadowBias;
  }
  
  @Override
  public void setShadowBias(double bias) {
	  this.shadowBias = bias;
  }
  
  ////////////////////////////
  @Override
  public String getNameInfo() {
	  return ("TorusKnot: " + getTransform().toString());
  }
  
  @Override
  public String getOtherAnimationInfo() {
      return this.otherAnimationInfo;
  }
  
  @Override
  public void setOtherAnimationInfo(String str) {
      this.otherAnimationInfo = str;
  }
  
  @Override
  public boolean isVisible() {
      return this.isVisible;
  }
  
  @Override  
  public boolean isShadowEnable() {
      return this.isShadowEnable;
  }
  
  @Override
  public boolean isShadowOnly() {
      return this.isShadowOnly;
  }

  @Override
  public boolean isReflective() {
      return this.isReflective;
  }

  @Override
  public boolean isRefractive() {
    return this.isRefractive;
  }

  @Override
  public void setVisible(boolean visible) {
      this.isVisible = visible;
  }

  @Override
  public void setShadowEnable(boolean enable) {
      this.isShadowEnable = enable;
  }

  @Override
  public void setShadowOnly(boolean only) {
      this.isShadowOnly = only;
  }
  
  @Override
  public void setReflective(boolean rfl) {
      this.isReflective = rfl;
  }
   
  @Override
  public void setRefractive(boolean rfr) {
      this.isRefractive = rfr;
  }
  
  @Override
  public boolean isVisibleSpecial() {
	  return this.isVisibleSpecial;
  }
  
  @Override
  public void setVisibleSpecial(boolean visible) {
	  this.isVisibleSpecial = visible;
  }
  
  @Override
  public String getName() {
	  return this.name;
  }
  
  @Override
  public void setName(String name) {
	  this.name = name;
  }
  ///////////////////////
 
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TorusKnot " + name + " {\n");
    sb.append("    name = " + name + ";\n");
    sb.append("    R = " + R + ";\n");
    sb.append("    r = " + r + ";\n");
    sb.append("    p = " + p + ";\n");
    sb.append("    q = " + q + ";\n");
    sb.append("\n");
    sb.append("    " + getTransform().toString() + "\n");
    sb.append("\n");
    sb.append("    firstAnim_" + animTransforms[0].toString() + "\n");
    sb.append("    secondAnim_" + animTransforms[1].toString() + "\n");
    sb.append("    otherAnimationInfo = " + otherAnimationInfo + ";\n");
    sb.append("\n");
    sb.append("    shadowColor = " + net.elena.murat.util.ColorUtil.toColorString(shadowColor) + ";\n");
	sb.append("    shadowBias = " + shadowBias + ";\n");
	sb.append("\n");
	sb.append("    isVisibleSpecial = " + isVisibleSpecial + ";\n");
	sb.append("    isVisible = " + isVisible + ";\n");
	sb.append("    isShadowEnable = " + isShadowEnable + ";\n");
	sb.append("    isShadowOnly = " + isShadowOnly + ";\n");
	sb.append("    isReflective = " + isReflective + ";\n");
	sb.append("    isRefractive = " + isRefractive + ";\n");
	sb.append("\n");
    sb.append("    material = " + getMaterial().toString() + ";\n}");
    return sb.toString();
  }
  
}
